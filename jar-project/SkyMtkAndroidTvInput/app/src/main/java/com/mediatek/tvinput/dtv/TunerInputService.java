
package com.mediatek.tvinput.dtv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContentUris;
import android.content.ContentValues;

import android.content.SharedPreferences;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputHardwareInfo;
import android.media.PlaybackParams;

import android.os.storage.VolumeInfo;
import android.text.TextUtils;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.net.Uri;
import android.os.SystemProperties;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.android.internal.os.SomeArgs;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.Channel;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.AbstractInputService.AbstractInputSession;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

//TODO Need twoworld API
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.MtkTvSubtitleBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.MtkTvTimeshift;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase.TimeshiftErrorID;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase.TimeshiftRecordStatus;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase.TimeshiftStopFlag;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase.TimeshiftPlaybackSpeed;
import com.mediatek.twoworlds.tv.MtkTvTimeshiftBase.TimeshiftPlaybackStatus;

import android.media.tv.TvContentRating;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputService;
import android.media.tv.TvInputManager;
import android.media.tv.TvContract;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvInputService.Session;

import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAVModeBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

/**
 * This class implement Tuner service in TIS
 */
public class TunerInputService extends AbstractInputService {
  private static final boolean DEBUG = true;
  private final List<DataSyncThread> syncThreads = new ArrayList<DataSyncThread>();
  private ContentResolver contentResolver;
  private DiskReceiver diskReceiver = null;
  private TunerInputSessionImpl tunerInputSessionImpl;
  private Timer timer;// schedule time to create sync thread ;
  private int index = 1;
  private int sourceTotalCount = 0;
  // Tuner hardware device id -> Tuner inputId
  private final Map<Integer, String> mTunerHardwareInputIdMap = new HashMap<Integer, String>();

  // Tuner inputId -> Tuner TvInputInfo
  private final Map<String, TvInputInfo> mTunerInputMap = new HashMap<String, TvInputInfo>();

  /**
   * Available Main device id list for this type filter
   */
  protected List<Integer> availTunerInputDeviceIds = new ArrayList<Integer>();

  // private List<TunerInputSessionImpl> dtvSessions = new ArrayList<TunerInputSessionImpl>();
  private final notifyTunerInputSessionImpl nfyTunerInputSessionImpl =
      new notifyTunerInputSessionImpl();

  class CreateThreadTask extends TimerTask {
    @Override
    public void run() {
      // check TvRemoteService is ready

      boolean tvRemoteServiceOK = false;
      tvRemoteServiceOK = (SystemProperties.get("sys.mtk.tvremoteservice.ready").equals("1"));
      Log.d(TAG, "tvRemoteServiceOK=" + tvRemoteServiceOK);
      if (tvRemoteServiceOK) {
        syncThreads.add(new ChannelSyncThread("ChannelSyncThread", contentResolver,
            TunerInputService.this));
        syncThreads.add(new ProgramSyncThread("ProgramlSyncThread", contentResolver,
            TunerInputService.this));
        syncThreads.add(new ChannelSelStatusThread("ChannelSelStatusThread", contentResolver,
            TunerInputService.this));
        for (DataSyncThread thread : syncThreads) {
          thread.start();
          Log.d(TAG, "Start thread " + thread.getName());
        }
        timer.cancel();
        Log.d(TAG, "timer.cancel()");
      }
    }
  }

  public TunerInputService() {
    super();
    TAG += "(Tuner)";

    platDeviceMainTotalNumber = 2;
    deviceFilter = TvInputConst.TV_INPUT_TYPE_BUILD_IN_TUNER;
    clazz = this.getClass();
  }

  @Override
  public TvInputInfo onHardwareAdded(TvInputHardwareInfo hardwareInfo) {
    if (hardwareInfo.getType() != this.deviceFilter) {
      return null;
    }

    Log.d(TAG, "onHardwareAdded filter info=> type=" + TvInputConst.getInputName(deviceFilter));
    Log.d(TAG,
        "onHardwareAdded: " + hardwareInfo.toString() + "\t resolveInfo" + resolveInfo.toString());

    int deviceId = hardwareInfo.getDeviceId();

    if (mTunerHardwareInputIdMap.containsKey(deviceId)) {
      Log.e(TAG, "Already created TvInputInfo for deviceId=" + deviceId);
      return null;
    }

    availTunerInputDeviceIds.add(deviceId);

    Collections.sort(availTunerInputDeviceIds);// main < sub

    TvInputInfo info = null;
    try {
      Log.d(TAG, "resolveInfo:" + resolveInfo.toString());
      {
        String inputName = "TV";
        //StringBuffer dispName = new StringBuffer(clazz.getSimpleName().replace("InputService", ""));
        if (availTunerInputDeviceIds.size() <= 1)
        {
          inputName = "DVB-C";
        }
        else if (availTunerInputDeviceIds.size() <= 2)
        {
          inputName = "ATV";
        }
        else if(availTunerInputDeviceIds.size() <= 3)
        {
          inputName = "DVB-T";
        }
        else if(availTunerInputDeviceIds.size() <= 4)
        {
          inputName = "DVB-S";
        }
        Log.d(TAG,"DispName: " + inputName);
        info = TvInputInfo.createTvInputInfo(this, resolveInfo, hardwareInfo, inputName,
            null);
      }
      mTunerHardwareInputIdMap.put(deviceId, info.getId());// collect device id and TvInputInfo into
                                                           // map.
      mTunerInputMap.put(info.getId(), info);
      Log.d(TAG, "Created TvInputInfo for deviceId=" + deviceId + "\t info=" + info.getId() + "\t"
          + info.toString());

    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (mTunerInputMap.size() <= 4)
    {
      Log.d(TAG,"###########----mTunerInputMap.size() <= 4");
      return mTunerInputMap.get(mTunerHardwareInputIdMap.get(deviceId));
    }

    if (mTunerInputMap.size() == (platDeviceMainTotalNumber * 2 +4))
    {
      MtkTvInputSource input = MtkTvInputSource.getInstance();
      sourceTotalCount = input.getInputSourceTotalNumber();
      Log.d(TAG, "onHardwareAdded sourceTotalCount:" + sourceTotalCount);
      if (TvInputConst.DEBUG) {
        for (int sortedDeviceId : availTunerInputDeviceIds) {
          Log.d(TAG, "onHardwareAdded sorted device id:" + sortedDeviceId);
        }
      }
      Log.d(TAG,"availTunerInputDeviceIds get 1: " + availTunerInputDeviceIds.get(1));
      Log.d(TAG,"sourceTotalCount: " + sourceTotalCount);
      if (availTunerInputDeviceIds.get(1) == sourceTotalCount)
      {
        Log.d(TAG,"availTunerInputDeviceIds.get(1) == sourceTotalCount");
        // Has only one tv hardware source
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(0), availTunerInputDeviceIds.get(0));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(1), availTunerInputDeviceIds.get(0));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(2), availTunerInputDeviceIds.get(0));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(3), availTunerInputDeviceIds.get(0));
      }
      else
      {
        Log.d(TAG,"availTunerInputDeviceIds.get(1) != sourceTotalCount");
        // Has two tv hardware source(DTV/ATV)
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(0), availTunerInputDeviceIds.get(0));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(1), availTunerInputDeviceIds.get(1));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(2), availTunerInputDeviceIds.get(2));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(3), availTunerInputDeviceIds.get(3));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(4), availTunerInputDeviceIds.get(0));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(5), availTunerInputDeviceIds.get(1));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(6), availTunerInputDeviceIds.get(2));
        deviceIdMapSourceId.put(availTunerInputDeviceIds.get(7), availTunerInputDeviceIds.get(3));
      }
    }
    return null;
  }

  @Override
  public String onHardwareRemoved(TvInputHardwareInfo hardwareInfo) {
    int deviceId = hardwareInfo.getDeviceId();
    String inputId = mTunerHardwareInputIdMap.get(deviceId);
    if (inputId == null) {
      if (DEBUG)
        Log.d(TAG, "TvInputInfo for deviceId=" + deviceId + " does not exist.");
      return null;
    }
    TvInputInfo info = mTunerInputMap.get(inputId);
    if (info == null) {
      return null;
    }
    mTunerHardwareInputIdMap.remove(deviceId);
    mTunerInputMap.remove(inputId);
    Log.d(TAG, "onHardwareRemoved id =" + inputId + "\t" + info.toString());
    return inputId;
  }

  public int getTunerInputSessionSize() {
    // return dtvInputSessionImpl;
    return SessionList.size();
  }

  public notifyTunerInputSessionImpl getTunerInputSession() {
    // return dtvInputSessionImpl;
    return nfyTunerInputSessionImpl;
  }

  @Override
  public Session onCreateSession(String inputId) {
    Log.d(TAG, "onCreateSession inputId=" + inputId + ", index = " + index);
    TvInputInfo info = mTunerInputMap.get(inputId);
    int mHardwareDeviceId = -1;
    Iterator<Map.Entry<Integer, String>> iter = mTunerHardwareInputIdMap.entrySet().iterator();
    while (iter.hasNext())
    {
      Map.Entry<Integer, String> entry = iter.next();
      String inputId_entry = entry.getValue();
      Log.d(TAG,
          "onCreateSession mTunerHardwareInputIdMap(" + entry.getKey() + ") = " + entry.getValue());
      if (inputId.equals(inputId_entry))
      {
        mHardwareDeviceId = entry.getKey();
        Log.d(TAG, "onCreateSession mHardwareDeviceId(" + mHardwareDeviceId + ")");
        break;
      }
    }

    if (mHardwareDeviceId == -1)
    {
      throw new IllegalArgumentException("Unknown mHardwareDeviceId: " + mHardwareDeviceId
          + " ; this should not happen.");
    }

    if (info == null) {
      throw new IllegalArgumentException("Unknown inputId: " + inputId
          + " ; this should not happen.");
    }
    tunerInputSessionImpl = new TunerInputSessionImpl(this, info, mHardwareDeviceId, index);
    // mSessionMap.put(inputId, dtvInputSessionImpl);
    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = tunerInputSessionImpl;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args).sendToTarget();
    Log.d(TAG, "onCreateSession() send DO_ADD_SESSION message, return");
    index = index + 1;

    return tunerInputSessionImpl;
  }

  class notifyTunerInputSessionImpl {
    public notifyTunerInputSessionImpl() {

    }

    public void notifyTracksChanged(final List<TvTrackInfo> tracks) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyTracksChanged(tracks);
        }
      }
    }

    public void notifyTrackSelected(final int type, final String trackId) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyTrackSelected(type, trackId);
        }
      }
    }

    public void notifyContentAllowed() {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyContentAllowed();
        }
      }
    }

    public void notifyContentBlocked(final TvContentRating rating) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyContentBlocked(rating);
        }
      }
    }

    public void notifyVideoAvailable() {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyVideoAvailable();
        }
      }
    }

    public void notifyVideoUnavailable(final int reason) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyVideoUnavailable(reason);
        }
      }
    }

    public void notifyChannelRetuned(final Uri channelUri) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyChannelRetuned(channelUri);
        }
      }
    }

    public void notifySessionEvent(final String eventType, final Bundle eventArgs) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifySessionEvent(eventType, eventArgs);
        }
      }
    }

    public void notifyTimeShiftStatusChanged(final int status) {
      synchronized (mLock) {
        for (AbstractInputSession session : SessionList) {
          session.notifyTimeShiftStatusChanged(status);
        }
      }
    }
  }

  @Override
  public void onCreate() {
    super.onCreate();
    contentResolver = getContentResolver();
    timer = new Timer();
    timer.schedule(new CreateThreadTask(), 0, 100);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    // Remove listener from TV remote service
    // Stop all syncThread
    for (DataSyncThread thread : syncThreads) {
      // TODO Need twoworld API
      // if (thread.tvCallback != null) {
      // thread.tvCallback.removeListener();
      // }
      if (thread != null) {
        thread.setStop(true);
      }
    }
  }

  @Override
  public TvInputManager getTvInputManager() {
    return this.tvInputManager;
  }

  class TimeshiftCallbackHandler extends MtkTvTVCallbackHandler {
    public static final int VIDEO_FMT_UPDATE = 37;
    public static final int AUDIO_FMT_UPDATE = 38;
    private final AbstractInputSession session;
    private Timer timer;

    class GetDurationTask extends TimerTask {
      @Override
      public void run() {
        MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
        if ((timeshift.getRecordStatus() != TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED)
            || (timeshift.getDuration() >= 5000))
        {
          timer.cancel();
          Log.d(TAG, "(TimeshiftCallbackHandler)timer.cancel()");
          if (timeshift.getRecordStatus() != TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED)
          {
            Log.d(
                TAG,
                "(TimeshiftCallbackHandler) GetDurationTask "
                + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
            session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
          }
          else
          {
            Log.d(
                TAG,
                "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus(TIMESHIFT_RECORD_STARTED),"
                +"GetDurationTask notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_AVAILABLE)\n");
            session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
          }
        }
      }
    }

    public TimeshiftCallbackHandler(AbstractInputSession session) {
      this.session = session;
    }

    @Override
    public int notifySvctxNotificationCode(int code) throws RemoteException {
      //Log.d(TAG, "(TimeshiftCallbackHandler) notifySvctxNotificationCode=" + code);
      if ((((TunerInputSessionImpl) session).getTS_SwitchChannelFlag())
          && (code == VIDEO_FMT_UPDATE))
      {
        Log.d(
            TAG,
            "(TimeshiftCallbackHandler) notifySvctxNotificationCode(VIDEO_FMT_UPDATE) "
             + "and b_TS_switchChannel = true");
        Thread t = new Thread(new Runnable() {
          @Override
          public void run()
          {
            MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
            timeshift.setAutoRecord(true);
            Log.d(
                TAG,
                "(TimeshiftCallbackHandler) notifySvctxNotificationCode(VIDEO_FMT_UPDATE) "
                + "and b_TS_switchChannel = true, call setAutoRecord(true)");
          }
        });
        t.start();

        ((TunerInputSessionImpl) session).resetTS_SwitchChannelFlag();
      }
      return 0;
    }

    /**
     * This method is used to notify Timeshift Record Status. Please override this function and
     * perform your behavior.
     * <p>
     *
     * @param status ,the value of Timeshift Record status.
     * @param argv1 , the value of the @param
     * @return 0 callback success. others callback fail.
     *         </p>
     */
    public int notifyTimeshiftRecordStatus(int status, long argv1) throws RemoteException {
      Log.d(TAG, "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus status=" + status
          + "argv1=" + argv1);
      MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
      if (status == TimeshiftRecordStatus.TIMESHIFT_RECORD_STOPPED.Value())
      {
        synchronized (((TunerInputSessionImpl) session).getTSSyncObject()) {
          ((TunerInputSessionImpl) session).getTSSyncObject().notify();
          Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", ts_sync_object notify");
        }
        Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", ts_sync_object release mutex");
        Log.d(
            TAG,
            "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus(TIMESHIFT_RECORD_STOPED), "
            + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
        session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
      }
      else if (status == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED.Value())
      {
        /*
         * timer = new Timer(); timer.schedule(new GetDurationTask(), 0, 100);
         */
        Log.d(
            TAG,
            "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus(TIMESHIFT_RECORD_STARTED), "
                + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_AVAILABLE)\n");
        session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
      }
      return 0;
    }

    /**
     * This method is used to notify Timeshift no disk file. Please override this function and
     * perform your behavior.
     * <p>
     *
     * @param argv1 , the value of the @param
     * @return 0 callback success. others callback fail.
     *         </p>
     */
    public int notifyTimeshiftNoDiskFile(long argv1) throws RemoteException {
      Log.d(TAG, "(TimeshiftCallbackHandler) notifyTimeshiftNoDiskFile argv1=" + argv1);
      Log.d(
          TAG,
          "(TimeshiftCallbackHandler) notifyTimeshiftNoDiskFile, "
           + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_AVAILABLE)\n");
      session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
      return 0;
    }

    /**
     * This method is used to notify Timeshift playback speed. Please override this function and
     * perform your behavior.
     * <p>
     *
     * @param speed , the value of Timeshift playback speed
     * @return 0 callback success. others callback fail.
     *         </p>
     */
    public int notifyTimeshiftSpeedUpdate(int speed) throws RemoteException {
      Log.d(TAG, "(TimeshiftCallbackHandler) notifyTimeshiftSpeedUpdate speed=" + speed);

      String ISessionEvent = "session_event_timeshift_speedupdate";
      Bundle IContextData = new Bundle();
      IContextData.putFloat("SpeedUpdate", speed);
      session.notifySessionEvent(ISessionEvent, IContextData);
      return 0;
    }

    /**
     * This method is used to notify Timeshift playback status. Please override this function and
     * perform your behavior.
     * <p>
     *
     * @param speed , the value of Timeshift playback status
     * @return 0 callback success. others callback fail.
     *         </p>
     */
    public int notifyTimeshiftPlaybackStatusUpdate(int status) throws RemoteException {
      Log.d(TAG, "(TimeshiftCallbackHandler) notifyTimeshiftPlaybackStatusUpdate status=" + status);

      String ISessionEvent = "session_event_timeshift_playbackstatusupdate";
      Bundle IContextData = new Bundle();
      IContextData.putInt("PlaybackStatusUpdate", status);
      session.notifySessionEvent(ISessionEvent, IContextData);
      return 0;
    }

    /**
     * This method is used to notify Timeshift storage removed status. Please override this function
     * and perform your behavior.
     * <p>
     *
     * @return 0 callback success. others callback fail.
     *         </p>
     */
    // public int notifyTimeshiftStorageRemoved() throws RemoteException {
    // Log.d(TAG, "(TimeshiftCallbackHandler) notifyTimeshiftStorageRemoved");
    //
    // Log.d(
    // TAG,
    // "(TimeshiftCallbackHandler) notifyTimeshiftStorageRemoved(), "
    // + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
    // session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
    //
    // return 0;
    // }
  }

  public class DiskReceiver extends BroadcastReceiver {
    private final AbstractInputSession session;
    public DiskReceiver(AbstractInputSession session) {
      this.session = session;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
      if (TextUtils.equals(intent.getAction(), VolumeInfo.ACTION_VOLUME_STATE_CHANGED)) {
        final int state = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE, -1);
        if (state == VolumeInfo.STATE_UNMOUNTED ||
            state == VolumeInfo.STATE_BAD_REMOVAL) {
          Log.d(
              TAG,
              "DiskReceiver (onReceive) ACTION_VOLUME_STATE_CHANGED(), "
                  + "VolumeInfo.STATE_UNMOUNTED or VolumeInfo.STATE_BAD_REMOVAL\n");
          session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
          MtkTvTimeshift timeshift = ((TunerInputSessionImpl)session).getTimeShiftObject();
          if (timeshift != null) {
            if(timeshift.getRecordStatus() == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED)
            {
              timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV);
            }
          }
        }
      }
    }
  }
  /**
   * DTV <BR>
   */
  class TunerInputSessionImpl extends AbstractInputSession {
    public static final int PlaybackSpeed_XF1 = 1;
    public static final int PlaybackSpeed_XF2 = 2;
    public static final int PlaybackSpeed_XF4 = 4;
    public static final int PlaybackSpeed_XF8 = 8;
    public static final int PlaybackSpeed_XF12 = 12;
    public static final int PlaybackSpeed_XF16 = 16;
    public static final int PlaybackSpeed_XF32 = 32;
    public static final int PlaybackSpeed_XF48 = 48;
    public static final int PlaybackSpeed_XF128 = 128;
    public static final int PlaybackSpeed_XR1 = -1;
    public static final int PlaybackSpeed_XR2 = -2;
    public static final int PlaybackSpeed_XR4 = -4;
    public static final int PlaybackSpeed_XR8 = -8;
    public static final int PlaybackSpeed_XR12 = -12;
    public static final int PlaybackSpeed_XR16 = -16;
    public static final int PlaybackSpeed_XR32 = -32;
    public static final int PlaybackSpeed_XR48 = -48;
    public static final int PlaybackSpeed_XR128 = -128;

    private static final String EXTRA_REQUEST_TIMESHIFT =
        "com.mediatek.tvinput.dtv.DTVInputService.REQUEST_TIMESHIFT";

    private MtkTvTimeshift timeshift = null;// TODO Need twoworld API
    private TimeshiftCallbackHandler timeshiftCallback = null;

    private MtkTvBroadcast broadcastControl;// TODO Need twoworld API
    private MtkTvChannelList channelListControl;// TODO Need twoworld API
    private final List<MtkTvChannelInfoBase> channels = null;// TODO Need twoworld API
    private SharedPreferences mSharedPreferences;
    private final MtkTvAVModeBase mAudio = MtkTvAVMode.getInstance();
    private byte isBarkerChannel = MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE;

    private boolean b_TS_switchChannel = false;

    public boolean getTS_SwitchChannelFlag() {
      return b_TS_switchChannel;
    }

    public void resetTS_SwitchChannelFlag() {
      b_TS_switchChannel = false;
    }

    private final Object ts_sync_object = new Object();

    public Object getTSSyncObject() {
      return ts_sync_object;
    }

    public MtkTvTimeshift getTimeShiftObject() {
      return timeshift;
    }

    protected TunerInputSessionImpl(AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(Tuner)";

      boolean turnkey = true;
      if (turnkey) {
        broadcastControl = MtkTvBroadcast.getInstance();// TODO Need twoworld API
        channelListControl = MtkTvChannelList.getInstance();// TODO Need twoworld API
        if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1"))
        {
          diskReceiver = new DiskReceiver(this);
          timeshift = MtkTvTimeshift.getInstance();
          timeshiftCallback = new TimeshiftCallbackHandler(this);

          IntentFilter diskFilter = new IntentFilter(VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
          getApplicationContext().registerReceiver(diskReceiver, diskFilter);
        }
      } else {
        // broadcastControl = new MtkTvBroadcast();
        // channelListControl = new MtkTvChannelList();
      }
    }

    public AbstractInputService getTunerInputService() {
      return inputService;
    }

    @Override
    public void onRelease() {
      Log.d(TAG, "Session release");
      MtkTvTimeshiftBase.TimeshiftRecordStatus timeshif_tstatus =
          TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;
      if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
        timeshif_tstatus = timeshift.getRecordStatus();
        if (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED)
        {
          Log.d(TAG,
              "onRelease, (timeshif_tstatus == MtkTvTimeshiftBase.TIMESHIFT_RECORD_STARTED) "
                  + "stop rec\n");
          timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
        }

        try {
          getApplicationContext().unregisterReceiver(diskReceiver);
        } catch (Exception e) {
          Log.d(TAG, "onRelease unregisterReceiver(diskReceiver) error");
        }

        diskReceiver = null;
        timeshiftCallback.removeListener();
        timeshiftCallback = null;
        timeshift = null;
      }
      super.onRelease();
    }

    @Override
    public boolean onSelectTrack(int type, String trackId) {
      int ret = 0;
      boolean b_ret = false;
      Log.d(TAG, "Enter onSelectTrack, type=" + type + "trackId=" + trackId);
      if (TvTrackInfo.TYPE_SUBTITLE == type) {
        MtkTvSubtitleBase mtkSubtitle = new MtkTvSubtitleBase();
        try {
          ret = mtkSubtitle.playStream(Integer.parseInt(trackId));
        } catch (Exception e) {

        }
        if (0 == ret) {
          Log.d(TAG, "onSelectTrack_subtitle successfully\n");
          return true;
        }
      }
      else if (TvTrackInfo.TYPE_AUDIO == type) {

        b_ret = mAudio.selectAudioById(trackId);
        Log.d(TAG, "Select audio Track: " + b_ret);
        return b_ret;
      }

      Log.d(TAG, "Leave onSelectTrack");
      return false;
    }

    @Override
    public void onUnblockContent(TvContentRating unblockedRating) {
      Log.d(TAG, "onUnblockContent: unblockRating=[" + unblockedRating.flattenToString() + "]\n");

      String[] focusWin = {
          "main", "sub"
      };
      MtkTvAppTVBase tvAppTvBase = new MtkTvAppTVBase();

      int result = MtkTvConfig.getInstance().getConfigValue(
          MtkTvConfigType.CFG_PIP_POP_TV_FOCUS_WIN);
      result = (0 == result) ? MtkTvConfigType.TV_FOCUS_WIN_MAIN : MtkTvConfigType.TV_FOCUS_WIN_SUB;

      tvAppTvBase.unlockService(focusWin[result]);
      this.notifyContentAllowed();
    }

    @Override
    public void onAppPrivateCommand(String action, Bundle data) {
      Log.d(TAG, "action: " + action);

      int ret = 0;
      if ((action.compareTo("session_event_timeshift_stop_mmp_and_select_tv")) == 0)
      {
        ret = timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV);
        Log.d(TAG,
            "onAppPrivateCommand, "
                + "timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV), ret = ("
                + ret + ")\n");
        return;
      }
      else if ((action.compareTo("session_event_timeshift_stop_rec")) == 0)
      {
        ret = timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
        Log.d(TAG,
            "onAppPrivateCommand, "
                + "timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD), ret = ("
                + ret + ")\n");
        return;
      }

      if (MtkTvTISMsgBase.MTK_TIS_MSG_RESET.equals(action)) {
        isBarkerChannel = MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE;
      }
      else if (MtkTvTISMsgBase.MTK_TIS_MSG_CHANNEL.equals(action)) {
        if (null != data) {
          isBarkerChannel = data.getByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL);
          Log.d(TAG, "isBarkerChannel: " + isBarkerChannel);
        }
      }
    }

    public long onTimeShiftGetStartPosition() {
      long startPostion = timeshift.getStartPosition();
      Log.d(TAG, "onTimeShiftGetStartPosition, timeshift.getStartPosition(), startPostion = ("
          + startPostion + ")\n");
      return startPostion;
    }

    public long onTimeShiftGetCurrentPosition() {
      long currentPostion = timeshift.getCurrentPosition();
      Log.d(TAG,
          "onTimeShiftGetCurrentPosition, timeshift.getCurrentPosition(), currentPostion = ("
              + currentPostion + ")\n");
      return currentPostion;
    }

    public void onTimeShiftPause() {
      int ret = 0;
      ret = timeshift.setPlaybackPause();
      Log.d(TAG, "onTimeShiftPause, timeshift.setPlaybackPause(), ret = (" + ret + ")\n");

      if (ret != 0)
      {
        Log.d(TAG, "onTimeShiftPause, timeshift.getErrorID() = " + timeshift.getErrorID());
        if (timeshift.getErrorID() == 7)
        {
          Log.d(
              TAG,
              "onTimeShiftPause, not create Timeshift file, "
              + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
          getTunerInputSession().notifyTimeShiftStatusChanged(
              TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

          Intent intent = (this.mInfo).createSetupIntent();
          if (intent == null) {
            Toast.makeText((Context) (this.inputService),
                "The input doesn\'t support setup activity", Toast.LENGTH_SHORT).show();
            return;
          }
          intent.putExtra(EXTRA_REQUEST_TIMESHIFT, true);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          Log.d(TAG,
              "onTimeShiftPause, not create Timeshift file, "
                + "startActivity(EXTRA_REQUEST_TIMESHIFT)\n");
        }
      }
      return;
    }

    public void onTimeShiftResume() {
      int ret = 0;
      ret = timeshift.setPlaybackResume();
      Log.d(TAG, "onTimeShiftResume, timeshift.setPlaybackResume(), ret = (" + ret + ")\n");

      if (ret != 0)
      {
        Log.d(TAG, "onTimeShiftResume, timeshift.getErrorID() = " + timeshift.getErrorID());
        if (timeshift.getErrorID() == 7)
        {
          Log.d(
              TAG,
              "onTimeShiftResume, not create Timeshift file, "
              + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
          getTunerInputSession().notifyTimeShiftStatusChanged(
              TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

          Intent intent = (this.mInfo).createSetupIntent();
          if (intent == null) {
            Toast.makeText((Context) (this.inputService),
                "The input doesn\'t support setup activity", Toast.LENGTH_SHORT).show();
            return;
          }
          intent.putExtra(EXTRA_REQUEST_TIMESHIFT, true);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          Log.d(TAG,
              "onTimeShiftResume, not create Timeshift file, "
                + "startActivity(EXTRA_REQUEST_TIMESHIFT)\n");
        }
      }
      return;
    }

    public void onTimeShiftSeekTo(long timeMs) {
      int ret = 0;
      ret = timeshift.seekTo(timeMs);
      Log.d(TAG, "onTimeShiftSeekTo, timeshift.seekTo(" + timeMs + "), ret = (" + ret + ")\n");
      return;
    }

    public void onTimeShiftSetPlaybackParams(PlaybackParams params) {
      float mSpeed = 0.0f;
      int ret = 0;
      try {
        mSpeed = params.getSpeed();
      } catch (Exception e) {
        Log.e(TAG, "onTimeShiftSetPlaybackParams params.getSpeed Exception:");
      }
      Log.d(TAG, "onTimeShiftSetPlaybackParams, mSpeed(" + mSpeed + ")\n");
      switch ((int) mSpeed)
      {
        case PlaybackSpeed_XF1:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_1X);
          break;
        case PlaybackSpeed_XF2:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_2X);
          break;
        case PlaybackSpeed_XF4:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_4X);
          break;
        case PlaybackSpeed_XF8:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_8X);
          break;
        case PlaybackSpeed_XF12:
        case PlaybackSpeed_XF16:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_16X);
          break;
        case PlaybackSpeed_XF32:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_32X);
          break;
        case PlaybackSpeed_XF48:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_64X);
          break;
        case PlaybackSpeed_XF128:
          ret = timeshift
              .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_128X);
          break;
        case PlaybackSpeed_XR1:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_1X);
          break;
        case PlaybackSpeed_XR2:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_2X);
          break;
        case PlaybackSpeed_XR4:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_4X);
          break;
        case PlaybackSpeed_XR8:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_8X);
          break;
        case PlaybackSpeed_XR12:
        case PlaybackSpeed_XR16:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_16X);
          break;
        case PlaybackSpeed_XR32:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_32X);
          break;
        case PlaybackSpeed_XR48:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_64X);
          break;
        case PlaybackSpeed_XR128:
          ret = timeshift.setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_128X);
          break;
        default:
          Log.e(TAG, "onTimeShiftSetPlaybackParams, mSpeed:invalid value!\n");
          return;
      }
      if (ret != 0)
      {
        Log.d(TAG,
            "onTimeShiftSetPlaybackParams, timeshift.getErrorID() = " + timeshift.getErrorID());
        if (timeshift.getErrorID() == 7)
        {
          Log.d(
              TAG,
              "onTimeShiftSetPlaybackParams, not create Timeshift file, "
               + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
          getTunerInputSession().notifyTimeShiftStatusChanged(
              TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

          Intent intent = (this.mInfo).createSetupIntent();
          if (intent == null) {
            Toast.makeText((Context) (this.inputService),
                "The input doesn\'t support setup activity", Toast.LENGTH_SHORT).show();
            return;
          }
          intent.putExtra(EXTRA_REQUEST_TIMESHIFT, true);
          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
          Log.d(TAG,
              "onTimeShiftSetPlaybackParams, not create Timeshift file, "
                + "startActivity(EXTRA_REQUEST_TIMESHIFT)\n");
        }
      }
      return;
    }

    @Override
    public boolean onTune(Uri uri) {
      // call super to switch to DTV input
      final boolean b_status = super.onTune(uri);
      
      Uri tmpUri = uri;
      boolean tmpfilterChannel = false;
      
      if (uri == null) {
          return false;
      }
      
      if (uri.toString().startsWith(TvContract.Channels.CONTENT_URI.toString())) {
    	  //do nothings
      }
      else if (uri.toString().startsWith(MtkTvTISMsgBase.FILTER_CHANNEL_URI.toString())) {
        tmpfilterChannel = true;
        tmpUri = ContentUris.withAppendedId(TvContract.Channels.CONTENT_URI,
                    ContentUris.parseId(uri));
      }
      else {
        return false;
      }

      final Uri uri_1 = tmpUri;
      final boolean filterChannel = tmpfilterChannel;
      
      /*
       * workaround for monkey test throw exception when tune exceed 2000ms
       */
      if (b_status == false)
      {
        Log.d(TAG, "b_status = false, indicated Tv is played before this tune!");
      }
      else
      {
        Log.d(TAG, "b_status = true, indicated Tv is played by this tune!");
      }
      // TODO Need twoworld API
      Thread t = new Thread(new Runnable()
      {
        @Override
        public void run()
        {
          MtkTvTimeshiftBase.TimeshiftRecordStatus timeshif_tstatus =
              TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;

          // Process channel url and change channel
          Channel c = getChannel(uri_1);
          if (c == null) {
            getTunerInputSession().notifyVideoUnavailable(
                TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
            Log.d(TAG, "notifyVideoUnavailable, before channelselect, invalid uri!\n");
            return;
          }

          if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1"))
          {
            timeshif_tstatus = timeshift.getRecordStatus();
            if (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED)
            {
              Log.d(TAG,
                  "onTune, (timeshif_tstatus == MtkTvTimeshiftBase.TIMESHIFT_RECORD_STARTED) "
                      + "stop rec\n");
              timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
            }
          }
          boolean b_ts_rec =
              (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) ? true : false;

          if (b_status == true)
          {
            synchronized (sync_object) {
              try {
                while (!sync_condition) {
                  Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", sync_object wait");
                  sync_object.wait();
                }
              } catch (InterruptedException e) {
              }
              Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", sync_object get mutex");
            }
          }
          if (b_ts_rec == true)
          {
            synchronized (ts_sync_object) {
              try {
                Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", ts_sync_object wait");
                ts_sync_object.wait();
              } catch (InterruptedException e) {
              }
              Log.d(TAG, "Thread " + Thread.currentThread().getName()
                  + ", ts_sync_object get mutex");
            }
          }
          {
            Log.d(TAG, c.toString());
            final MtkTvChannelInfoBase mtkTvChannelInfo = getMtkChannelinfo(c);
            if (mtkTvChannelInfo == null) {
              Log.d(TAG, "mtkTvChannelInfo is null,return");
              getTunerInputSession().notifyVideoUnavailable(
                  TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              return;
            }

            {
              Log.d(TAG, "call broadcastControl.channelSelect ");
              int ret = filterChannel ? 0 : broadcastControl.channelSelect(mtkTvChannelInfo,
                  ((isBarkerChannel == MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE) ? false : true));
              Log.d(TAG, "channelSelect return ret:" + ret);
              if (ret == 0)
              {
                getTunerInputSession().notifyVideoAvailable();
                getTunerInputSession().notifyChannelRetuned(uri_1);
                if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
                  Log.d(TAG, "onTune, channelSelect return ret:" + ret
                      + ", b_TS_switchChannel = true");
                  b_TS_switchChannel = true;
                  Log.d(TAG,
                      "onTune, notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
                  getTunerInputSession().notifyTimeShiftStatusChanged(
                      TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
                }
              }
              else
              {
                getTunerInputSession().notifyVideoUnavailable(
                    TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              }
            }

            TunerInputService tunerInputService = (TunerInputService) getTunerInputService();
            List<DataSyncThread> syncThreads = tunerInputService.syncThreads;
            if (syncThreads != null) {
              for (DataSyncThread thread : syncThreads) {
                if (thread.getClass() == ProgramSyncThread.class) {
                  ((ProgramSyncThread) thread).notifyChannel(mtkTvChannelInfo);
                }
              }
            }
          }
        }
      });
      t.start();
      return true;
    }

    // TODO Need twoworld API
    private MtkTvChannelInfoBase getMtkChannelinfo(Channel channel) {
      String data = new String(channel.getData());
      int svlId = 0;
      int svlRecId = 0;
      Log.d(TAG, "internal_provider_data=" + data);

      if (data != null) {
        String[] userData = data.split(",");
        if (userData.length >= 2) {
          svlId = Integer.parseInt(userData[1]);
          svlRecId = Integer.parseInt(userData[2]);
        }
      }

      Log.d(TAG, "getMtkChannelinfo svlid=" + svlId + " svlRecid=" + svlRecId);

      MtkTvChannelInfoBase channelInfo = channelListControl.getChannelInfoBySvlRecId(svlId,
          svlRecId);

      return channelInfo;
    }
  }

}
