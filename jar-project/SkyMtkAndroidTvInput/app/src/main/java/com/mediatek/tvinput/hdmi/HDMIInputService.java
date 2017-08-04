
package com.mediatek.tvinput.hdmi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient;
import android.hardware.hdmi.HdmiTvClient.InputChangeListener;
import android.hardware.hdmi.HdmiTvClient.SelectCallback;
import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Surface;

import com.android.internal.os.SomeArgs;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
import com.mediatek.twoworlds.tv.MtkTvScan;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.hardware.hdmi.HdmiDeviceInfo.ADDR_INTERNAL;
import static android.hardware.hdmi.HdmiDeviceInfo.DEVICE_TV;
import static com.mediatek.tvinput.DebugTag.DEBUG_TAG;

/**
 * Reference implementation of TvInputService which handles both hardware/logical HDMI input
 * sessions.
 */
public class HDMIInputService extends TvInputService {
  private static final boolean DEBUG = true;
  // private static final int[] ICONS = { R.drawable.fake_icon0, R.drawable.fake_icon1,
  // R.drawable.fake_icon2, R.drawable.fake_icon3 };

  private static final String INVALID_INPUT_ID = "";

  // Timer message id used to measure the time lapse from device select to the reception
  // of the command <Active Source> from the device. User notification may be required
  // if the command doesn't arrive in time.
  private static final int TIMER_DEVICE_SELECT = 100;

  // Timer message id that starts when we receive onSetMain(false). If the onSetMain(true)
  // for the new session is called within the specified time (see INTERNAL_SELECT_TIMEOUT_MS)
  // we know that sessions changed hands within HDMI-based inputs.
  private static final int TIMER_INTERNAL_SELECT = 101;

  // We wait 20 seconds for the command <Active Source> to come. Chosen by heuristics.
  private static final int DEVICE_SELECT_TIMEOUT_MS = 20 * 1000;

  // We wait 500ms till the onSetMain(true) will be called for the new HDMI session.
  // If not, we decide the new session is not an HDMI-based one any more; therefore
  // declare TV is the new active source.
  private static final int INTERNAL_SELECT_TIMEOUT_MS = 500;

  // States used to keep track of progress of device select.

  // Initial state. Device select has not been started yet.
  private static final int STATE_INIT = 0;

  // Device select has started, and wait for the response from HdmiControlService.
  // If the call returns with error, state goes to STATE_DONE.
  private static final int STATE_SELECT_STARTED = 1;

  // Device select returned successfully, and now waiting for the command
  // (<Active Source>) from the selected device.
  private static final int STATE_WAIT_INPUT_CHANGE = 2;

  // Device select finished its cycle.
  private static final int STATE_DONE = 3;

  // Context used to keeps track of setSurface, HDMI device select(device or port) operation.
  // HDMI device select should be initiated either in onSetMain or onSetSurface since it is
  // possible that only one of them can be called for certain scenarios. Device select is
  // done by whichever comes first in case both are called. We use the state variables to
  // determine which one should do it, and also figure out when the deferred setSurface
  // should be performed.
  //
  // For logical devices, the deferred operation is done in InputChangeListener.onChanged
  // where we receive the matched <Active Source> command. For hardware devices (ports),
  // deferred operation is not required since the whatever is shown on the port should be
  // visible as soon as user switched to the port. State goes straight from STATE_INIT to
  // STATE_COMPLETE.
  //
  // Note that a state cycle is valid only for a certain input. The states are not valid
  // any more if we get a new input (TvInputInfo) which is different from the one of
  // the current session, the states should be reset to initial state.
  private static class InputContext {
    // State that keeps track of the progress of device select operation.
    int state; // Device select call state
    String inputId; // TV Input that this context deals with
    Surface surface; // Surface to set when device select is successfully done.
                     // Set if deferred operation is necessary

    InputContext() {
      reset();
    }

    void reset() {
      state = STATE_INIT;
      inputId = INVALID_INPUT_ID;
      surface = null;
    }

    // Update the context state to keep integrity with the TV input in interest.
    // The new input is different from the current one the context is associated,
    // reset the context to initial state.
    void update(TvInputInfo info) {
      if (!inputId.equals(info.getId())) {
        reset();
      }
    }
  }

  private static final TvStreamConfig[] EMPTY_STREAM_CONFIGS = {};

  private TvStreamConfig[] mStreamConfigs = EMPTY_STREAM_CONFIGS;
  // hardware device id -> inputId
  private final SparseArray<String> mHardwareInputIdMap = new SparseArray<String>();

  // cec device id -> inputId
  private final SparseArray<String> mCecInputIdMap = new SparseArray<String>();

  // inputId -> TvInputInfo
  private final Map<String, TvInputInfo> mInputMap = new HashMap<String, TvInputInfo>();

  // inputId -> portId
  private final Map<String, Integer> mPortIdMap = new HashMap<String, Integer>();

  // inputId -> TvInputService.Session
  private final Map<String, HdmiInputSessionImpl> mSessionMap =
      new HashMap<String, HdmiInputSessionImpl>();

  // portId -> hardware device Id
  private final SparseIntArray mHardwareIdMap = new SparseIntArray();

  // ID of the selected HDMI TvInputInfo. Set to INVALID_INPUT_ID if no HDMI input is selected.
  private String mInputId;

  private TvInputManager mManager = null;
  private HdmiTvClient mHdmiControl = null;
  private HdmiInputChangeListener mInputChangeListener;
  private final InputContext mInputContext = new InputContext();
  private final MtkTvInputSource input = MtkTvInputSource.getInstance();
  private ResolveInfo mResolveInfo;
  private final Random mRandom = new Random();

  private final ServiceHandler mHandler = new ServiceHandler();

  @Override
  public void onCreate() {
    super.onCreate();
    initHdmiControl();
  }

  public void onDestroy() {
    super.onDestroy();
    Log.d(DEBUG_TAG, "########onDestroy " + this.toString());
  }

  private void initHdmiControl() {
    mResolveInfo = getPackageManager().resolveService(
        new Intent(SERVICE_INTERFACE).setClass(this, getClass()),
        PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
    mManager = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);
    HdmiControlManager hdmiControlManager =
        (HdmiControlManager) getSystemService(Context.HDMI_CONTROL_SERVICE);
    if (hdmiControlManager != null) {
      mHdmiControl = (HdmiTvClient) hdmiControlManager.getClient(DEVICE_TV);
    }
    if (mHdmiControl != null) {
      mInputChangeListener = new HdmiInputChangeListener();
      mHdmiControl.setInputChangeListener(mInputChangeListener);
    } else {
      Log.e(DEBUG_TAG, "Can't access HdmiControlService");
    }
  }

  @Override
  public Session onCreateSession(String inputId) {
    Log.d(DEBUG_TAG, "onCreateSession inputId=" + inputId);
    TvInputInfo info = mInputMap.get(inputId);
    if (info == null) {
      throw new IllegalArgumentException("Unknown inputId: " + inputId
          + " ; this should not happen.");
    }
    HdmiInputSessionImpl session = null;

    if (isLogicalInput(info)) {
      Log.d(DEBUG_TAG, "onCreateSession isLogicalInput");
      session = new HdmiLogicalInputSessionImpl(this, info);
    } else {
      Log.d(DEBUG_TAG, "onCreateSession isHardwareInput");
      session = new HdmiHardwareInputSessionImpl(this, info);
    }

    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = session;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args).sendToTarget();
    Log.d(DEBUG_TAG, "onCreateSession() send DO_ADD_SESSION message, return");

    // mSessionMap.put(inputId, session);
    return session;
  }

  // Returns true if the given TV input is a logical one(either CEC or MHL) but not a hardware.
  private static boolean isLogicalInput(TvInputInfo info) {
    return info.getParentId() != null;
  }

  @Override
  public TvInputInfo onHardwareAdded(TvInputHardwareInfo hardwareInfo) {
    if (hardwareInfo.getType() != TvInputHardwareInfo.TV_INPUT_TYPE_HDMI) {
      return null;
    }
    Log.d(DEBUG_TAG, "onHardwareAdded hardwareInfo=" + hardwareInfo);
    int deviceId = hardwareInfo.getDeviceId();
    if (mHardwareInputIdMap.indexOfKey(deviceId) >= 0) {
      Log.e(DEBUG_TAG, "Already created TvInputInfo for deviceId=" + deviceId);
      return null;
    }
    int portId = hardwareInfo.getHdmiPortId();
    if (portId < 0) {
      Log.e(DEBUG_TAG, "Failed to get HDMI port for deviceId=" + deviceId);
      return null;
    }
    if (mHardwareIdMap.indexOfKey(portId) >= 0) {
      Log.e(DEBUG_TAG, "Already have port " + portId + " for deviceId=" + deviceId);
      return null;
    }
    TvInputInfo info = null;
    try {
      info = TvInputInfo.createTvInputInfo(this, mResolveInfo, hardwareInfo,
          "HDMI " + hardwareInfo.getHdmiPortId(), null);
      Log.e(DEBUG_TAG, "onHardwareAdded:" + "HDMI " + hardwareInfo.getHdmiPortId());
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    mHardwareInputIdMap.put(deviceId, info.getId());
    mPortIdMap.put(info.getId(), portId);
    mInputMap.put(info.getId(), info);
    mHardwareIdMap.put(portId, deviceId);
    if (DEBUG)
      Log.d(DEBUG_TAG, "onHardwareAdded returns " + info);
    return info;
  }

  @Override
  public String onHardwareRemoved(TvInputHardwareInfo hardwareInfo) {
    int deviceId = hardwareInfo.getDeviceId();
    String inputId = mHardwareInputIdMap.get(deviceId);
    if (inputId == null) {
      if (DEBUG)
        Log.d(DEBUG_TAG, "TvInputInfo for deviceId=" + deviceId + " does not exist.");
      return null;
    }
    int portId = getPortIdForDeviceIdLocked(deviceId);
    if (portId == -1) {
      Log.w(DEBUG_TAG, "Port not exists for deviceId=" + deviceId);
    }
    mHardwareIdMap.delete(portId);
    mHardwareInputIdMap.remove(deviceId);
    mPortIdMap.remove(inputId);
    mInputMap.remove(inputId);
    if (DEBUG)
      Log.d(DEBUG_TAG, "onHardwareRemoved returns " + inputId);
    return inputId;
  }

  private int getPortIdForDeviceIdLocked(int deviceId) {
    String inputId = mHardwareInputIdMap.get(deviceId, null);
    return mPortIdMap.containsKey(inputId) ? mPortIdMap.get(inputId) : -1;
  }

  // Called when CEC/MHL device is added. Creates a corresponding TvInputInfo instance,
  // and updates various mappings for future use.
  @Override
  public TvInputInfo onHdmiDeviceAdded(HdmiDeviceInfo device) {
    int id = device.getId();
    if (mCecInputIdMap.indexOfKey(id) >= 0) {
      Log.e(DEBUG_TAG, "Already created TvInputInfo for id:" + id);
      return null;
    }
    int portId = device.getPortId();
    if (portId < 0) {
      Log.e(DEBUG_TAG, "Failed to get HDMI port for id: " + id);
      return null;
    }
    if (mHardwareIdMap.indexOfKey(portId) < 0) {
      Log.e(DEBUG_TAG, "Unknown HDMI port " + portId + " for id=" + id);
      return null;
    }
    int hardwareDeviceId = mHardwareIdMap.get(portId);
    TvInputInfo parentInfo = mInputMap.get(mHardwareInputIdMap.get(hardwareDeviceId));
    TvInputInfo info = null;
    try {
      Log.d(DEBUG_TAG, "onHdmiDeviceAdded DisplayName:" + device.getDisplayName()
          + "\t parent hardwareDeviceId=" + hardwareDeviceId + "\tport="
          + portId);
      info = TvInputInfo.createTvInputInfo(this, mResolveInfo, device, parentInfo.getId(),
          device.getDisplayName(), null);
    } catch (XmlPullParserException e) {
      e.printStackTrace();
      return null;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    mCecInputIdMap.put(id, info.getId());
    mPortIdMap.put(info.getId(), portId);
    mInputMap.put(info.getId(), info);
    if (DEBUG)
      Log.d(DEBUG_TAG, "onHdmiDeviceAdded returns " + info.getId());
    return info;
  }

  @Override
  public String onHdmiDeviceRemoved(HdmiDeviceInfo device) {
    int id = device.getId();
    String inputId = mCecInputIdMap.get(id);
    if (inputId == null) {
      if (DEBUG) {
        Log.d(DEBUG_TAG, "TvInputInfo for id=" + id + " does not exist.");
      }
      return null;
    }
    mCecInputIdMap.remove(id);
    mPortIdMap.remove(inputId);
    mInputMap.remove(inputId);
    if (DEBUG) {
      Log.d(DEBUG_TAG, "onHdmiDeviceRemoved port=" + device.getPortId());
      Log.d(DEBUG_TAG, "onHdmiDeviceRemoved returns " + inputId);
    }
    return inputId;
  }

  private String findInputIdForDeviceInfo(HdmiDeviceInfo device) {
    String inputId = INVALID_INPUT_ID;
    if (device.isCecDevice() || device.isMhlDevice()) {
      inputId = mCecInputIdMap.get(device.getId(), "");
    } else {
      if (mHardwareIdMap.indexOfKey(device.getPortId()) >= 0) {
        int hardwareDeviceId = mHardwareIdMap.get(device.getPortId());
        inputId = mHardwareInputIdMap.get(hardwareDeviceId, "");
      }
    }
    return inputId;
  }

  private HdmiInputSessionImpl getSession(String inputId) {
    return mSessionMap.get(inputId);
  }

  // InputChangeListener implementation. Called when an external command from CEC/MHL bus
  // such as <Active Source> comes in for TV to change input in accordance.
  //
  // The service can be in either one of the two different states when the listener is invoked
  // by the command from, say, device A:
  //
  // 1) TV already selected device A, and was waiting for the command: Routing control
  // was performed and <Set Stream Path> might have broadcast to wake up the device A.
  // Then we set the surface, turn the video available so that users can start watching video.
  //
  // 2) TV was watching input B which is different from device A. This situation happens when
  // the device attempts to initiate the command on its own. In this case we broadcast
  // ACTION_VIEW intent with the corresponding input URI for TV app to select the given input.
  private final class HdmiInputChangeListener implements InputChangeListener {
    @Override
    public void onChanged(final HdmiDeviceInfo device) {
      runOnServiceThread(new Runnable() {
        @Override
        public void run() {
          if (DEBUG) {
            Log.d(DEBUG_TAG, "HdmiInputChangeListener onChanged: device = " + device.toString());
          }
          String inputId = findInputIdForDeviceInfo(device);
          if (inputId == INVALID_INPUT_ID) {
            if (DEBUG) {
              Log.d(DEBUG_TAG, "HdmiInputChangeListener"
                  + " onChanged: (inputId == INVALID_INPUT_ID) return ");
            }
            return;
          }
          mHandler.clearTimer(TIMER_DEVICE_SELECT);
          if (inputId.equals(mInputContext.inputId)) {
            if (DEBUG) {
              Log.d(DEBUG_TAG, "HdmiInputChangeListener"
                  + " onChanged: (inputId == (mInputContext.inputId)) ");
            }
            HdmiInputSessionImpl session = getSession(inputId);
            // Set the surface deferred from the actual onSetSurface now.
            if (session != null) {
              if (hasDeferredSurface(mInputContext)) {
                if (DEBUG) {
                  Log.d(
                      DEBUG_TAG,
                      "HdmiInputChangeListener"
                          + " onChanged: (inputId == (mInputContext.inputId)) "
                          + " hasDeferredSurface call setSurface");
                }
                session.setSurface(mInputContext.surface);
              }
              if (DEBUG) {
                Log.d(DEBUG_TAG,
                    "HdmiInputChangeListener"
                        + " onChanged: (inputId == (mInputContext.inputId)) notifyVideoAvailable");
              }
              session.notifyVideoAvailable();
            }
            // mInputContext.reset();
          } else {
            // A new input change request overwriting the one being anticipated, or
            // we were not watching HDMI at all. Request a new input selection to app.
            if (DEBUG) {
              Log.d(DEBUG_TAG, "HdmiInputChangeListener"
                  + " onChanged: (inputId != (mInputContext.inputId)) ");
            }
            if(MtkTvScan.getInstance().isScanning()){
                if (DEBUG) {
                    Log.d(DEBUG_TAG, "HdmiInputChangeListener"
                        + " onChanged: (inputId != (mInputContext.inputId)) isScanning is true, return");
                }
                return;
            }
            if (Settings.Secure.getInt(HDMIInputService.this.getContentResolver(),
                    Settings.Secure.USER_SETUP_COMPLETE, 0) == 0) {
                Log.d(DEBUG_TAG, "Ignoring storage notification: setup not complete");
                return;
            }
            mInputContext.reset();
            requestInputChange(inputId);
            mInputContext.inputId = inputId;
          }
        }
      });
    }

    private/* static */boolean hasDeferredSurface(InputContext inputContext) {
      return inputContext.surface != null && inputContext.state == STATE_WAIT_INPUT_CHANGE;
    }

    private void requestInputChange(String inputId) {
      Log.d(DEBUG_TAG,"requestInputChange" + inputId);
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(TvContract.buildChannelUriForPassthroughInput(inputId));
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra("livetv", true);
      startActivity(intent);
    }
  }

  private void runOnServiceThread(Runnable r) {
    mHandler.post(r);
  }

  // Abstract class which both hardware/logical HDMI sessions are inherited from. Acquires
  // access to the relevant HDMI hardware resource for the sessions to use when they are active.
  private abstract class HdmiInputSessionImpl extends Session {
    protected final TvInputInfo mInfo;
    protected final int mHardwareDeviceId;
    protected final int mPortId;
    // protected Surface mSurface = null;
    private TvInputManager.Hardware mHardware = null;
    private final MtkTvMultiView multiview = MtkTvMultiView.getInstance();// TODO Need twoworld API
    private float mSourceVolume = -1f;
    private boolean mReleased = false;
    private int totalCount = 0;
    /**
     * Indicator Main Or Sub
     */
    protected int activeOutType = TvInputConst.InputInvalid;// InputInvalid;
    protected boolean needSetSurface = false;
    private TvStreamConfig[] mStreamConfigs = EMPTY_STREAM_CONFIGS;

    HdmiInputSessionImpl(Context context, TvInputInfo info) {
      super(context);
      mInfo = info;
      mPortId = mPortIdMap.get(info.getId());
      mHardwareDeviceId = mHardwareIdMap.get(mPortId);

      totalCount = input.getInputSourceTotalNumber();
      Log.d(DEBUG_TAG, "HdmiInputSessionImpl() totalCount=" + totalCount);

      acquireHardware();
    }

    private void acquireHardware() {
      if (mHardware != null) {
        Log.d(DEBUG_TAG, "acquireHardware(mHardware != null) return");
        return;
      }

      int deviceId = mHardwareDeviceId;
      if (activeOutType == TvInputConst.InputMain) {
        deviceId = mHardwareDeviceId;
      }
      else if (activeOutType == TvInputConst.InputSub) {
        if (("cn").equals(SystemProperties.get("ro.mtk.system.marketregion"))
            || ("eu").equals(SystemProperties.get("ro.mtk.system.marketregion")))
        {
          deviceId = mHardwareDeviceId + totalCount;
        }
        else
        {
          deviceId = mHardwareDeviceId + totalCount + 1;
        }
      }
      else
      {
        Log.d(DEBUG_TAG, "acquireHardware() activeOutType = TvInputConst.InputInvalid directly return");
        return;
      }
      Log.d(DEBUG_TAG, "acquireHardware() deviceId=" + deviceId);
      TvInputManager.HardwareCallback callback = new TvInputManager.HardwareCallback() {
        @Override
        public void onReleased() {
          mHardware = null;
          mStreamConfigs = EMPTY_STREAM_CONFIGS;
          HDMIInputService.this.mStreamConfigs = EMPTY_STREAM_CONFIGS;
        }

        @Override
        public void onStreamConfigChanged(TvStreamConfig[] configs) {
          mStreamConfigs = configs;
          HDMIInputService.this.mStreamConfigs = configs;
          Log.d(DEBUG_TAG, "onStreamConfigChanged() mStreamConfigs.length=" + configs.length);
        }
      };
      mHardware = mManager.acquireTvInputHardware(deviceId, callback, mInfo);
      return;
    }

    private boolean postSetSurface(Surface surface, TvStreamConfig config) {
      if ((activeOutType == TvInputConst.InputInvalid)
          && (surface != null)) {
        Log.d(DEBUG_TAG,
            "postSetSurface()"
                + " (activeOutType == TvInputConst.InputInvalid) && (surface != null) return false");
        return false;
      }
      // needSetSurface = false;

      SomeArgs args = SomeArgs.obtain();
      args.arg1 = mHardware;
      args.arg2 = surface;
      args.arg3 = config;
      args.arg4 = this;
      mHandler.obtainMessage(ServiceHandler.DO_SET_SURFACE, args).sendToTarget();
      Log.d(DEBUG_TAG, "postSetSurface() send DO_SET_SURFACE message, return true");
      // boolean ret = mHardware.setSurface(surface, config);
      // Log.d(DEBUG_TAG, "setSurface() setSurface return = " + ret);
      if (mSourceVolume != -1f)
      {
        Log.d(DEBUG_TAG,
            "postSetSurface() (mSourceVolume != -1f)"
                + " recall mHardware.setStreamVolume(mSourceVolume = "
                + mSourceVolume + ")");
        mHardware.setStreamVolume(mSourceVolume);
        mSourceVolume = -1f;
      }
      return true;
    }

    // Passes surface object to the corresponding hardware for rendering of the content
    // to be visible.
    protected boolean setSurface(Surface surface) {
      /*
       * if (surface == null) { mStreamConfigs = EMPTY_STREAM_CONFIGS; }
       */
      Log.d(DEBUG_TAG, "setSurface " + ", mReleased = " + mReleased);
      if (mReleased) {
        Log.e(DEBUG_TAG, "setSurface, (mReleased) return false");
        return false;
      }
      if (mHardware == null) {
        acquireHardware();
        if (mHardware == null) {
          Log.d(DEBUG_TAG, "setSurface() setSurface (mHardware == null)return false ");
          return false;
        }
      }
      TvStreamConfig config = null;
      if (surface != null) {
        config = getStreamConfig();
        if (config == null) {
          Log.d(DEBUG_TAG, "setSurface() setSurface (config == null)return false ");
          return false;
        }
      }
      // boolean ret = mHardware.setSurface(surface, config);
      // Log.d(DEBUG_TAG, "setSurface() setSurface return = " + true);
      if (config == null)
      {
        Log.d(DEBUG_TAG, "setSurface() postSetSurface surface = " + surface + ", config = null");
      }
      else
      {
        Log.d(DEBUG_TAG,
            "setSurface() postSetSurface surface = " + surface + ", config = " + config.toString());
      }
      boolean ret = postSetSurface(surface, config);
      return ret;
    }

    @Override
    public void onRelease() {
      mReleased = true;
      if (DEBUG)
        Log.d(DEBUG_TAG, "onRelease() mReleased = true");
      SomeArgs args = null;
      if (mInputContext.surface != null){
        if (mHardware != null) {
          args = SomeArgs.obtain();
          args.arg1 = mHardware;
          args.arg2 = null;
          args.arg3 = null;
          args.arg4 = this;
          mHandler.obtainMessage(ServiceHandler.DO_SET_SURFACE, args).sendToTarget();
          if (DEBUG)
            Log.d(DEBUG_TAG, "onRelease() send DO_SET_SURFACE(null) message");
        }
      }
      
      if (DEBUG)
        Log.d(DEBUG_TAG, "onRelease() call mInputContext.reset()");
      mInputContext.reset();
      
      args = SomeArgs.obtain();
      if (activeOutType == TvInputConst.InputMain)
      {
        if (mHardware != null) {
          args.arg1 = mHardware;
          args.argi1 = mHardwareDeviceId;

          mHandler.obtainMessage(ServiceHandler.DO_RELEASE_HARDWARE, args).sendToTarget();
          Log.d(DEBUG_TAG, "onRelease() send DO_RELEASE_HARDWARE message");

          mHardware = null;
        }
      }
      else if (activeOutType == TvInputConst.InputSub)
      {
        if (mHardware != null) {
          args.arg1 = mHardware;

          if (("cn").equals(SystemProperties.get("ro.mtk.system.marketregion"))
              || ("eu").equals(SystemProperties.get("ro.mtk.system.marketregion")))
          {
            args.argi1 = mHardwareDeviceId + totalCount;
          }
          else
          {
            args.argi1 = mHardwareDeviceId + totalCount + 1;
          }

          mHandler.obtainMessage(ServiceHandler.DO_RELEASE_HARDWARE, args).sendToTarget();
          Log.d(DEBUG_TAG, "onRelease() send DO_RELEASE_HARDWARE message");
          
          mHardware = null;
        }
      }

      SomeArgs args_another = SomeArgs.obtain();
      args_another.arg1 = mInfo;
      mHandler.obtainMessage(ServiceHandler.DO_REMOVE_SESSION, args_another).sendToTarget();
      Log.d(DEBUG_TAG, "onRelease() send DO_REMOVE_SESSION message");

      activeOutType = TvInputConst.InputInvalid;
    }

    // HDMI API used to select the device. For hardware session this should invoke portSelect(),
    // and for logical session, deviceSelect().
    abstract void hdmiSelect();

    @Override
    public void onSetMain(boolean isMain) {
      if (DEBUG) {
        Log.d(DEBUG_TAG, "onSetMain: isMain = " + isMain + ", mReleased = " + mReleased);
      }
      if (mReleased) {
        Log.e(DEBUG_TAG, "onSetMain, (mReleased) return");
        return;
      }
      mHandler.clearAllTimers();
      if (DEBUG) {
        Log.d(DEBUG_TAG, "onSetMain: " + isMain + " info:" + mInfo.getId() + " selectedInput:"
            + mInputContext.inputId + " state:"
            + mInputContext.state);
      }
      mInputContext.update(mInfo);
      if (isMain) {
        if (DEBUG) {
          Log.d(DEBUG_TAG, "onSetMain: isMain = true");
        }
        if (mInputContext.state == STATE_INIT) {
          if (DEBUG) {
            Log.d(DEBUG_TAG, "onSetMain:isMain = true(mInputContext.state == STATE_INIT)"
                + " call hdmiSelect");
          }
          hdmiSelect();
        }
      } else {
        if (DEBUG) {
          Log.d(DEBUG_TAG, "onSetMain:isMain = false call startInternalSourceSelectTimer");
        }
        startInternalSourceSelectTimer();
      }
    }

    private TvStreamConfig getStreamConfig() {
      int i = 0;
      while ((HDMIInputService.this.mStreamConfigs.length == 0)
    		  && (this.mStreamConfigs.length == 0))
      {
        i = i + 1;
        if (i >= 50)
        {
          Log.d(DEBUG_TAG, "getStreamConfig  sleep(2) wait 50 times(no any stream config), break");
          break;
        }
        Log.d(
            DEBUG_TAG,
            "getStreamConfig  sleep(2), wait onStreamConfigChanged(TvStreamConfig[]) callback"
                + " to be call in child thread ");
        try
        {
          Thread.sleep(2); /* main thread sleep 2ms to wait child thread to set stream config */
        } catch (InterruptedException e)
        {
          e.printStackTrace();
          return null;
        }
      }
      for (TvStreamConfig config : this.mStreamConfigs) {
        if (config.getType() == TvStreamConfig.STREAM_TYPE_INDEPENDENT_VIDEO_SOURCE) {
          return config;
        }
      }
      for (TvStreamConfig config : HDMIInputService.this.mStreamConfigs) {
        if (config.getType() == TvStreamConfig.STREAM_TYPE_INDEPENDENT_VIDEO_SOURCE) {
          return config;
        }
      }
      return null;
    }

    @Override
    public void onSetStreamVolume(float volume) {
      Log.d(DEBUG_TAG, "onSetStreamVolume(" + volume + ")" + ", mReleased = " + mReleased);
      if (mReleased) {
        Log.e(DEBUG_TAG, "onSetStreamVolume, (mReleased) return");
        return;
      }
      // mtkTvVolCtrl.setVolume((int) volume);//TODO Need twoworld API
      mSourceVolume = volume;
      int deviceId = mHardwareDeviceId;
      if ((SystemProperties.get("sys.mtk.livetv.ready").equals("0"))
          && (mSourceVolume == 1))
      {
        Log.d(DEBUG_TAG,
            "onSetStreamVolume(1) call MtkTvMultiView api(setAudioFocusbySourceid), deviceId = "
                + deviceId + ".");
        multiview.setAudioFocusbySourceid(deviceId);
      }
      try{
        if (mHardware != null)
        {
          Log.d(DEBUG_TAG, "onSetStreamVolume (mHardware != null) call mHardware.setStreamVolume(" + volume
              + ")");
          mHardware.setStreamVolume(volume);
          mSourceVolume = -1f;
        }
      } catch (IllegalStateException e)
      {
        e.printStackTrace();
        return;
      }
    }

    @Override
    public boolean onTune(Uri channelUri) {
      // inputService.showHardwareInfoList();
      synchronized (TvInputService.class) {
        String urlStr = channelUri.toString();
        int newOutType = TvInputConst.InputInvalid;
        int deviceid = -1;

        Log.d(DEBUG_TAG, "onTune(" + urlStr + ")");

        if (urlStr.indexOf("main") != -1) {// turn to Main
          newOutType = TvInputConst.InputMain;
        } else if (urlStr.indexOf("sub") != -1) {// turn to Sub
          newOutType = TvInputConst.InputSub;
        } else {
          if (urlStr.indexOf("com.mediatek.tvinput") != -1) {
            newOutType = TvInputConst.InputMain;// default turn to Main
          } else {
          }
        }

        int activeOutType = this.activeOutType;

        if ((activeOutType == TvInputConst.InputInvalid) || (activeOutType != newOutType)) {

          if (activeOutType == TvInputConst.InputInvalid)
          {
            Log.d(DEBUG_TAG,
                "onTune, (activeOutType == TvInputConst.InputInvalid), activeOutType = newOutType("
                    + newOutType + ")");
          }

          if ((activeOutType != TvInputConst.InputInvalid)
              && (activeOutType != newOutType))
          {
            Log.d(DEBUG_TAG, "onTune, (activeOutType != newOutType), activeOutType = newOutType("
                + newOutType + ")");
          }
          this.activeOutType = newOutType;
        } else {
          Log.d(DEBUG_TAG, "onTune, same to old one, activeOutType = " + this.activeOutType);
        }
        // needSetSurface = true;
      }

      if (mInputContext.surface != null) {
        Log.d(DEBUG_TAG, "onTune call (mInputContext.surface != null) HdmiInputSessionImpl::setSurface");
        setSurface(mInputContext.surface);
      }

      return true;
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {
      // No-op
    }

    @Override
    public void onSurfaceChanged(int format, int width, int height) {
      Log.d(DEBUG_TAG, "onSurfaceChanged, format = " + format + " ,width = " + width + " ,height = "
          + height);

      Canvas canvas = null;

      if (null != mInputContext.surface) {
        try {
          canvas = mInputContext.surface.lockCanvas(null);
        } catch (Exception ex) {
        }

        try {
          mInputContext.surface.unlockCanvasAndPost(canvas);
        } catch (Exception ex) {
        }
      }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
      if (mHdmiControl != null) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
          mHdmiControl.sendKeyEvent(KeyEvent.KEYCODE_HOME, false);
        } else {
          mHdmiControl.sendKeyEvent(keyCode, false);
        }
      }
      return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (mHdmiControl != null) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
          mHdmiControl.sendKeyEvent(KeyEvent.KEYCODE_HOME, true);
        } else {
          mHdmiControl.sendKeyEvent(keyCode, true);
        }
      }
      return true;
    }
  }

  // Service-wide handler that manages timers used in session management.
  // See the definition of TIMER_DEVICE_SELECT, TIMER_INTERNAL_SELECT.
  // Also used to run InputChangeListener#onChange in the service thread.
  private class ServiceHandler extends Handler {
    private static final int DO_SET_SURFACE = 1;
    private static final int DO_RELEASE_HARDWARE = 2;
    private static final int DO_REMOVE_SESSION = 3;
    private static final int DO_ADD_SESSION = 4;

    @Override
    public void handleMessage(Message msg) {
      SomeArgs args;
      TvInputManager.Hardware mHardware;
      HdmiInputSessionImpl session;
      switch (msg.what) {
        case TIMER_DEVICE_SELECT:
          // TODO: Show a banner indicating time out here.
          if (DEBUG)
            Log.d(DEBUG_TAG, "Time out on device select.");
          session = getSession((String) msg.obj);
          if (session != null) {
            if (DEBUG)
              Log.d(DEBUG_TAG, "Time out on device select. notifyVideoUnavailable");
                    session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
          }
          break;
        case TIMER_INTERNAL_SELECT:
          if (DEBUG)
            Log.d(DEBUG_TAG, "Internal input is selected.");
          if (mHdmiControl == null) {
            if (DEBUG)
            {
              Log.d(DEBUG_TAG, "Internal input is selected.(mHdmiControl == null) break;");
            }
            break;
          }
          if (DEBUG)
          {
            Log.d(
                DEBUG_TAG,
                "Internal input is selected."
                 + "call  mHdmiControl.deviceSelect(HdmiDeviceInfo.idForCecDevice(ADDR_INTERNAL))");
          }
          mHdmiControl.deviceSelect(HdmiDeviceInfo.idForCecDevice(ADDR_INTERNAL),
              new SelectCallback() {
                @Override
                public void onComplete(int result) {
                  // Switch to internal input is always successful.
                }
              });
          break;
        case DO_SET_SURFACE:
          if (DEBUG)
            Log.d(DEBUG_TAG, "DO_SET_SURFACE.");
          args = (SomeArgs) msg.obj;
          mHardware = (TvInputManager.Hardware) args.arg1;
          Surface surface = (Surface) args.arg2;
          TvStreamConfig config = (TvStreamConfig) args.arg3;
          session = (HdmiInputSessionImpl) args.arg4;
          if (config == null)
          {
            Log.d(DEBUG_TAG, "DO_SET_SURFACE()  surface = " + surface + ", config = null");
          }
          else
          {
            Log.d(DEBUG_TAG, "DO_SET_SURFACE()  surface = " + surface + ", config = "
                + config.toString());
          }
          boolean ret = false;
          try{
           ret = mHardware.setSurface(surface, config);
           Log.d(DEBUG_TAG, "DO_SET_SURFACE mHardware.setSurface() return = " + ret);
          } catch (IllegalStateException e)
          {
            e.printStackTrace();
            return;
          }

          if (ret) {
            if (surface != null) {
              Log.d(DEBUG_TAG, "DO_SET_SURFACE mHardware.setSurface() notifyVideoAvailable");
              session.notifyVideoAvailable();
            }
          } else {
            if (surface != null) {
              Log.d(
                  DEBUG_TAG,
                  "DO_SET_SURFACE mHardware.setSurface() notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN");
              session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
            }
          }
          break;
        case DO_RELEASE_HARDWARE:
          if (DEBUG)
            Log.d(DEBUG_TAG, "DO_RELEASE_HARDWARE.");
          args = (SomeArgs) msg.obj;
          mHardware = (TvInputManager.Hardware) args.arg1;
          int device_Id = args.argi1;
          Log.d(DEBUG_TAG, "DO_RELEASE_HARDWARE. releaseTvInputHardware device_Id = " + device_Id);
          mManager.releaseTvInputHardware(device_Id, mHardware);
          break;
        case DO_REMOVE_SESSION:
          if (DEBUG)
            Log.d(DEBUG_TAG, "DO_REMOVE_SESSION.");
          args = (SomeArgs) msg.obj;
          TvInputInfo mInfo = (TvInputInfo) args.arg1;
          Log.d(DEBUG_TAG, "DO_REMOVE_SESSION. remove mInfo.getId() = " + mInfo.getId());
          mSessionMap.remove(mInfo.getId());
          break;
        case DO_ADD_SESSION:
          if (DEBUG)
            Log.d(DEBUG_TAG, "DO_ADD_SESSION.");
          args = (SomeArgs) msg.obj;
          String inputId = (String) args.arg1;
          session = (HdmiInputSessionImpl) args.arg2;
          Log.d(DEBUG_TAG, "DO_ADD_SESSION. add inputId = " + inputId + ",session = " + session);
          mSessionMap.put(inputId, session);
          break;
        default:
          Log.w(DEBUG_TAG, "Unsupported message:" + msg.what);
          break;
      }
    }

    public void clearAllTimers() {
      clearTimer(TIMER_DEVICE_SELECT);
      clearTimer(TIMER_INTERNAL_SELECT);
    }

    public void clearTimer(int msg) {
      removeMessages(msg);
    }

    public void setTimer(int msg, String inputId, int delayMillis) {
      sendMessageDelayed(obtainMessage(msg, inputId), delayMillis);
    }
  }

  private void startInternalSourceSelectTimer() {
    mHandler.setTimer(TIMER_INTERNAL_SELECT, null, INTERNAL_SELECT_TIMEOUT_MS);
  }

  // HdmiInputSessionImpl for logical device session (CEC/MHL).
  //
  // Uses HdmiControlService.deviceSelect() to send <Set Stream Path>. We should wait for
  // the device to respond with <Active Source> when its video becomes available for CEC device.
  private class HdmiLogicalInputSessionImpl extends HdmiInputSessionImpl {

    private final int mId;

    HdmiLogicalInputSessionImpl(Context context, TvInputInfo info) {
      super(context, info);
      mId = mCecInputIdMap.keyAt(mCecInputIdMap.indexOfValue(info.getId()));
    }

    // Passes the surface to hardware to use it to render the input stream.
    //
    // We defer the operation until the selected logical device confirms it is ready
    // to stream the contents. This effectively makes sure that port switching won't happen
    // before signaling is completed.
    @Override
    public boolean onSetSurface(Surface surface) {
      if (DEBUG)
        Log.d(DEBUG_TAG, "onSetSurface surface:" + surface);
      // For situations where signaling can (need) not be done, just set the surface.
      if (surface == null || mHdmiControl == null) {
        Log.d(DEBUG_TAG, "onSetSurface surface == null || mHdmiControl == null");
        return setSurface(surface);
      }
      mInputContext.update(mInfo);
      // If signaling was already completed, just go ahead and set the surface.
      if ((mInputContext.state == STATE_DONE)
          || (mInputContext.state == STATE_WAIT_INPUT_CHANGE)) {
        if (mInputContext.state == STATE_DONE)
        {
            if (DEBUG)
               Log.d(DEBUG_TAG,
                   "onSetSurface(mInputContext.state == STATE_DONE)"
                      + " call HdmiInputSessionImpl setSurface");
        }

        if (mInputContext.state == STATE_WAIT_INPUT_CHANGE)
        {
          if (DEBUG)
            Log.d(DEBUG_TAG,
                "onSetSurface(mInputContext.state == STATE_WAIT_INPUT_CHANGE)"
                    + " call HdmiInputSessionImpl setSurface");
          mInputContext.state = STATE_DONE;
        }

        mInputContext.surface = surface;

        return setSurface(mInputContext.surface);
      } else if (mInputContext.state == STATE_INIT) {
        if (DEBUG)
          Log.d(DEBUG_TAG, "onSetSurface(mInputContext.state == STATE_INIT) call deviceSelect");
        deviceSelect();

        mInputContext.surface = surface;
        if (DEBUG)
          Log.d(DEBUG_TAG,
              "onSetSurface(mInputContext.state == STATE_INIT)"
                  + " call HdmiInputSessionImpl setSurface");
        return setSurface(mInputContext.surface);
      }
      mInputContext.surface = surface;
      return true;
    }

    private void deviceSelect() {
      if (mHdmiControl == null) {
        mInputContext.state = STATE_DONE;
        return;
      }
      mHandler.clearAllTimers();
      mHdmiControl.deviceSelect(mId, new SelectCallback() {
        @Override
        public void onComplete(final int result) {
          runOnServiceThread(new Runnable() {
            @Override
            public void run() {
              if (DEBUG) {
                Log.d(DEBUG_TAG, "deviceSelect onComplete: result=" + result);
              }
              if (result != HdmiControlManager.RESULT_SUCCESS) {
                if (DEBUG) {
                  Log.d(DEBUG_TAG, "deviceSelect onComplete: notifyVideoUnavailable");
                }
                notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
                //mInputContext.reset();
                return;
              }
              if (isMhlDevice()) {
                if (DEBUG) {
                  Log.d(DEBUG_TAG, "deviceSelect onComplete: isMhlDevice");
                }
                if (mInputContext.surface != null) {
                  mInputContext.state = STATE_DONE;
                  setSurface(mInputContext.surface);

                  if (DEBUG) {
                    Log.d(DEBUG_TAG, "deviceSelect onComplete: isMhlDevice notifyVideoAvailable");
                  }
                  notifyVideoAvailable();
                }
              } else {
                if (DEBUG) {
                  Log.d(DEBUG_TAG,
                      "deviceSelect onComplete: not isMhlDevice, "
                          + "mInputContext.state = STATE_WAIT_INPUT_CHANGE");
                }
                mInputContext.state = STATE_WAIT_INPUT_CHANGE;
                if (DEBUG) {
                  Log.d(
                      DEBUG_TAG,
                      "deviceSelect onComplete: not isMhlDevice, "
                          + "mInputContext.state = STATE_WAIT_INPUT_CHANGE notifyVideoAvailable");
                }
                notifyVideoAvailable();
              }
            }
          });
        }
      });
      // Start the timer (See the definition of TIMER_DEVICE_SELECT) to wait a
      // certain amount of time before giving up on <Active Source> from the selected
      // device.
      // Timer needs not be started if the session is already the active one (which happens
      // if this call was resulted in by external input change) , or the device is MHL.
      /*
       * if (!mInfo.getId().equals(mInputContext.inputId) && !isMhlDevice()) {
       * mHandler.setTimer(TIMER_DEVICE_SELECT, mInfo.getId(), DEVICE_SELECT_TIMEOUT_MS); }
       */
      mInputContext.state = STATE_SELECT_STARTED;
      mInputContext.inputId = mInfo.getId();
    }

    private boolean isMhlDevice() {
      return HdmiDeviceInfo.idForMhlDevice(mPortId) == mId;
    }

    @Override
    public void hdmiSelect() {
      deviceSelect();
    }
  }

  // HdmiInputSessionImpl for hardware session. Represents a session when a HDMI port is
  // selected for input. Uses HdmiControlService.portSelect() to initiate routing control.
  //
  // Port selection keeps the active input to that of hardware if the routing control
  // ends up with a non-CEC device. On the other hand, if the active routing path is a CEC device,
  // it sends <Active Source> in response and the active input gets switched accordingly.
  //
  // Hardware session, unlike logical one, does not do deferred surface operation since
  // hardware port itself should be always available no matter what input may be coming in -
  // i.e. surface gets always set in onSetSurface().
  private class HdmiHardwareInputSessionImpl extends HdmiInputSessionImpl {

    HdmiHardwareInputSessionImpl(Context context, TvInputInfo info) {
      super(context, info);
    }

    @Override
    public boolean onSetSurface(Surface surface) {
      if (DEBUG)
        Log.d(DEBUG_TAG, "onSetSurface surface:" + surface);
      mInputContext.update(mInfo);
      if (surface != null) {
        if (mInputContext.state == STATE_INIT) {
          if (DEBUG)
            Log.d(DEBUG_TAG, "onSetSurface(mInputContext.state == STATE_INIT) call portSelect");
          portSelect();
        }
      }
      mInputContext.surface = surface;
      if (DEBUG)
        Log.d(DEBUG_TAG, "onSetSurface HdmiInputSessionImpl setSurface");
      return setSurface(surface);
    }

    public void portSelect() {
      if (mHdmiControl == null) {
        return;
      }
      mHandler.clearAllTimers();
      mHdmiControl.portSelect(mPortId, new SelectCallback() {
        @Override
        public void onComplete(final int result) {
          runOnServiceThread(new Runnable() {
            @Override
            public void run() {
              if (DEBUG)
                Log.d(DEBUG_TAG, "portSelect onComplete: result=" + result);
              if (result != HdmiControlManager.RESULT_SUCCESS) {
                if (DEBUG) {
                  Log.d(DEBUG_TAG, "portSelect onComplete: notifyVideoUnavailable");
                }
                notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              } else {
                if (DEBUG) {
                  Log.d(DEBUG_TAG, "portSelect onComplete: notifyVideoAvailable");
                }
                notifyVideoAvailable();
              }
              // For port switching, there's no need to wait for <Active Source> which
              // might not come at all. Make the state complete now.
              mInputContext.state = STATE_DONE;
            }
          });
        }
      });
      mInputContext.state = STATE_SELECT_STARTED;
      mInputContext.inputId = mInfo.getId();
    }

    @Override
    public void hdmiSelect() {
      portSelect();
    }
  }
}
