
package com.mediatek.tvinput.composite;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.media.tv.TvInputInfo;
import android.util.Log;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.TvInputConst;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.internal.os.SomeArgs;

import android.text.TextUtils;

import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import android.os.HandlerThread;
import android.os.Process;
import android.os.SystemProperties;

import android.media.tv.TvInputInfo;
import android.media.tv.TvContentRating;

import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.TVInputReceiver;
import com.mediatek.tvinput.TVInputReceiverCallBack;
import com.mediatek.tvinput.TVInputRatingProcesser;
import com.mediatek.tvinput.AbstractInputService.AbstractRutineTaskThread;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

/**
 * This class implement Composite service in TIS<BR>
 * TODO platform not support
 */
public class Composite2InputService extends AbstractInputService {
  private final List<AbstractRutineTaskThread> syncThreads =
                           new ArrayList<AbstractRutineTaskThread>();
  private ContentResolver contentResolver;
  private Composite2InputSessionImpl composite2InputSessionImpl;
  private Timer timer;
  private int index = 1;

  class CreateThreadTask extends TimerTask {
    @Override
    public void run() {
      boolean tvRemoteServiceOK = false;
      tvRemoteServiceOK = (SystemProperties.get("sys.mtk.tvremoteservice.ready").equals("1"));
      // Log.d(TAG, "tvRemoteServiceOK=" + tvRemoteServiceOK);

      if (tvRemoteServiceOK) {
        syncThreads.add(new Composite2InputRutineTaskThread("Composite2InputRutineTaskThread",
            contentResolver, Composite2InputService.this));

        for (AbstractRutineTaskThread thread : syncThreads) {
          thread.start();
          Log.d(TAG, "Start thread " + thread.getName());
        }

        timer.cancel();
        Log.d(TAG, "timer.cancel()");
      }
    }
  }

  public Composite2InputService() {
    super();
    TAG += "(Composite2)";
    deviceFilter = TvInputConst.TV_INPUT_TYPE_COMPOSITE;
    clazz = this.getClass();
    platDeviceMainTotalNumber = 1;// for device filter,total number of this filter,main only
    platDeviceMainIndex = 2;// 1 base
  }

  @Override
  public Session onCreateSession(String inputId) {
    Log.d(TAG, "onCreateSession inputId=" + inputId + ", index = " + index);
    TvInputInfo info = mInputMap.get(inputId);
    int mHardwareDeviceId = -1;
    if (info == null) {
      throw new IllegalArgumentException("Unknown inputId: " + inputId
          + " ; this should not happen.");
    }

    Iterator<Map.Entry<Integer, TvInputInfo>> iter = deviceIdMap.entrySet().iterator();
    while (iter.hasNext())
    {
      Map.Entry<Integer, TvInputInfo> entry = iter.next();
      TvInputInfo info_entry = entry.getValue();
      Log.d(TAG, "onCreateSession deviceIdMap(" + entry.getKey() + ") = " + entry.getValue());
      if (info.equals(info_entry))
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

    composite2InputSessionImpl = new Composite2InputSessionImpl(this, info, mHardwareDeviceId,
        index);

    tvInputReceiver = new TVInputReceiver();
    IntentFilter inentFilter = new IntentFilter();
    inentFilter.addAction(TVInputReceiver.ACTION_BLOCKED_RATINGS_CHANGED);
    inentFilter.addAction(TVInputReceiver.ACTION_PARENTAL_CONTROLS_ENABLED_CHANGED);
    registerReceiver(tvInputReceiver, inentFilter);

    // mSessionMap.put(inputId, composite2InputSessionImpl);
    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = composite2InputSessionImpl;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args).sendToTarget();
    Log.d(TAG, "onCreateSession() send DO_ADD_SESSION message, return");
    index = index + 1;
    return composite2InputSessionImpl;
  }

  public Composite2InputSessionImpl getComposite2InputSession() {
    return composite2InputSessionImpl;
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

    for (AbstractRutineTaskThread thread : syncThreads) {
      if (thread != null) {
        thread.setStop(true);
      }
    }
  }

  /**
   * VGA <BR>
   */
  class Composite2InputSessionImpl extends AbstractInputSession {
    protected Composite2InputSessionImpl(AbstractInputService mtkTvCommonInputService,
        TvInputInfo info, int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(Composite2)";
    }

    @Override
    public void onRelease() {
      super.onRelease();
      Log.d(TAG, "Session release");

      if (inputService != null && inputService.getTVInputReceiver() != null) {
        Log.d(TAG, "Session release, unreg receiver");
        unregisterReceiver(inputService.getTVInputReceiver());
        inputService.setTVInputReceiver(null);
      }
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
  }

  // TODO Need twoworld API
  class RutineTaskCallbackHandler extends MtkTvTVCallbackHandler {
    private final String TAG = "RutineTaskCallbackHandler";
    private final Composite2InputRutineTaskThread mRutineThread;

    public RutineTaskCallbackHandler(Composite2InputRutineTaskThread rutineThread) {
      this.mRutineThread = rutineThread;
    }

    @Override
    /**
     * [MTK Internal] Following is used to recieve the status of current channel select.
     */
    public int notifySvctxNotificationCode(int code) throws RemoteException {
      Log.d(TAG, "notifySvctxNotificationCode=" + code + "\n");
      mRutineThread.notifyAVStatus(code);

      return 0;
    }

  }

  public class Composite2InputRutineTaskThread extends AbstractRutineTaskThread {
    public String TAG = "Composite2InputRutineTaskThread";

    private Handler mHandler;

    private final Composite2InputService service;

    private TVInputRatingProcesser ratingProcesser = null;

    private RutineTaskCallbackHandler tvCallback = null;

    public TVInputReceiverCallBack tvRevCallback = null;

    /* notify content blocked/unblocked */
    public static final int SERVICE_BLOCKED = 9;
    public static final int SERVICE_UNBLOCKED = 12;

    /* notify onVideoAvailable */
    public static final int EVENT_NORMAL = 0;
    public static final int SCRAMBLED_AUDIO_VIDEO_SVC = 23;
    public static final int SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC = 24;
    public static final int SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC = 26;
    public static final int SCRAMBLED_VIDEO_NO_AUDIO_SVC = 27;

    /* notify onVideoUnavailable */
    public static final int EVENT_SIGNAL_LOSS = 5;
    public static final int NO_AUDIO_VIDEO_SVC = 19;
    public static final int AUDIO_ONLY_SVC = 20;
    public static final int SCRAMBLED_AUDIO_NO_VIDEO_SVC = 25;
    public static final int EVENT_NO_RESOURCES = 60;
    public static final int EVENT_INTERNAL_ERROR = 61;

    /* notify notifyRatingChangedMsg */
    public static final int MSG_RATING_BLOCKED_CHANGED = 2000;
    public static final int MSG_PARENTAL_CONTROLS_ENABLED_CHANGED = 2001;

    private final MtkTvInputSourceBase mInput = MtkTvInputSource.getInstance();

    public Composite2InputRutineTaskThread(String name, ContentResolver contentResolver,
        Composite2InputService service) {
      super(name, contentResolver);
      this.service = service;
      Log.d(TAG, "constructed function");

      ratingProcesser = new TVInputRatingProcesser("Composite2InputService");

      tvCallback = new RutineTaskCallbackHandler(this);
      tvRevCallback = new TVInputReceiverCallBack(this);
    }

    @Override
    protected void processSync() {

      Log.d(TAG, "processSync go go go");

      if (mThread == null) {
        Log.d(TAG, "mThread == null");
        return;
      }

      mHandler = new Handler(mThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
          Log.d(TAG, "enter handle message");

          if (checkSessionexist()) {
            Log.d(TAG, "checkSessionexist return true");
          }

          switch (msg.what)
          {
          /* notify notifyRatingChangedMsg */
            case MSG_PARENTAL_CONTROLS_ENABLED_CHANGED:
            {
              Log.d(TAG, "enter MSG_PARENTAL_CONTROLS_ENABLED_CHANGED\n");

              if (null == ratingProcesser) {
                Log.e(TAG, "Rating processer not inited.\n\n");
                break;
              }

              ratingProcesser.checkContentBlocked();
              break;
            }
            case MSG_RATING_BLOCKED_CHANGED:
            {
              Log.d(TAG, "enter MSG_RATING_BLOCKED_CHANGED\n");

              if (null == ratingProcesser) {
                Log.e(TAG, "Rating processer not inited.\n\n");
                break;
              }

              ratingProcesser.syncParentalControlSettings();
              ratingProcesser.checkContentBlocked();

              break;
            }
            /* notify onVideoAvailable */
            case SERVICE_BLOCKED:
            {
              Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);

              if (null == ratingProcesser) {
                Log.e(TAG, "Rating processer not inited.\n\n");
                break;
              }

              MtkTvRatingConvert2Goo ratingMapped = new MtkTvRatingConvert2Goo();
              MtkTvRatingConvert2Goo.getCurrentRating(ratingMapped);

              Log.d(TAG, "Get current rating:" + ratingMapped.toString());

              if (!TextUtils.isEmpty(ratingMapped.getDomain())
                  && !TextUtils.isEmpty(ratingMapped.getRatingSystem())
                  && !TextUtils.isEmpty(ratingMapped.getRating())) {
                TvContentRating contentRating = TvContentRating.createRating(
                    ratingMapped.getDomain(),
                    ratingMapped.getRatingSystem(),
                    ratingMapped.getRating(),
                    ratingMapped.getSubRating());
                ratingProcesser.tryToNotifyContentBlocked(contentRating);
              }

              ratingProcesser.b_svctx_locked = true;

              Log.d(TAG, "SERVICE_BLOCKED,Done!");

              break;
            }
            case SERVICE_UNBLOCKED:
            {
              Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);

              if (null == ratingProcesser) {
                Log.e(TAG, "Rating processer not inited.\n\n");
                break;
              }

              ratingProcesser.tryToNotifyContentAllowed();
              ratingProcesser.b_svctx_locked = false;
              Log.d(TAG, "SERVICE_UNBLOCKED,Done!");
              break;
            }
            case EVENT_NORMAL:
            case SCRAMBLED_AUDIO_VIDEO_SVC:
            case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
            case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
            case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
            case EVENT_SIGNAL_LOSS:
            case NO_AUDIO_VIDEO_SVC:
            case AUDIO_ONLY_SVC:
            case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
            case EVENT_NO_RESOURCES:
            case EVENT_INTERNAL_ERROR:
            {
              Log.d(TAG, "enter Process SVCTX Message\n");

              if (null == ratingProcesser) {
                Log.e(TAG, "Rating processer not inited.\n\n");
                break;
              }

              ratingProcesser.checkContentBlocked();
              break;
            }

            default:
            {
              Log.d(TAG, " default msg.what" + msg.what);
              break;
            }
          }

        }
      };
    }

    private boolean checkSessionexist() {
      Log.d(TAG, "checkSessionexist Enter");

      if (service == null || service.getComposite2InputSession() == null) {
        Log.d(TAG, "checkSessionexist session is null");
        return false;
      }

      ratingProcesser.setTVInputService(service);
      ratingProcesser.setTVInputSession(service.getComposite2InputSession());

      return true;
    }

    @Override
    public void notifyAVStatus(int code) {
      Log.d(TAG, "notifyAVStatus Enter:" + code);

      // if (!checkSessionexist()) return;

      Message msg = Message.obtain();
      switch (code)
      {
        case EVENT_NORMAL:
        case SCRAMBLED_AUDIO_VIDEO_SVC:
        case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
        case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
        case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
        case EVENT_SIGNAL_LOSS:
        case NO_AUDIO_VIDEO_SVC:
        case AUDIO_ONLY_SVC:
        case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
        case EVENT_NO_RESOURCES:
        case EVENT_INTERNAL_ERROR:
        case SERVICE_BLOCKED:
        case SERVICE_UNBLOCKED: {
          msg.what = msg.arg1 = msg.arg2 = code;
          Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
              + msg.arg1);
          mHandler.sendMessage(msg);
          break;
        }
        default: {
          break;
        }
      }

      return;
    }

    @Override
    public int notifyRatingChangedMsg() {
      Log.d(TAG, "notifyRatingChangedMsg enter.\n");

      if (!checkSessionexist())
        return 0;

      Message msg = Message.obtain();
      msg.what = msg.arg1 = msg.arg2 = MSG_RATING_BLOCKED_CHANGED;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);

      return 0;
    }

    @Override
    public int notifyParentalEnabledChangedMsg() {
      Log.d(TAG, "notifyParentalEnabledChangedMsg enter.\n");

      if (!checkSessionexist())
        return 0;

      Message msg = Message.obtain();
      msg.what = msg.arg1 = msg.arg2 = MSG_PARENTAL_CONTROLS_ENABLED_CHANGED;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);

      return 0;
    }

  }

}
