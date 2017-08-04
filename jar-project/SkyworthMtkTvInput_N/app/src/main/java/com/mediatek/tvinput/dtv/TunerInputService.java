
package com.mediatek.tvinput.dtv;

import java.io.File;
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
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputHardwareInfo;
import android.media.PlaybackParams;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnSeekCompleteListener;

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
import android.view.Surface;
import android.widget.Toast;

import com.android.internal.os.SomeArgs;

import com.mediatek.MtkMediaPlayer.PlayerSpeed;
import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.Channel;
import com.mediatek.tvinput.RecordedProgram;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.AbstractInputService.AbstractInputSession;
import com.mediatek.tvinput.AbstractInputService.AbstractRecordingSession;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

//TODO Need twoworld API
import com.mediatek.twoworlds.tv.MtkTvMultiView;
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
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordNotifyMsgType;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordStopReason;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordSrcType;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordBaseErrorID;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.MtkTvPvrBrowserBase;
import com.mediatek.twoworlds.tv.model.MtkTvPvrBrowserItemBase;

import android.media.tv.TvContentRating;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputService;
import android.media.tv.TvInputManager;
import android.media.tv.TvContract;
import android.media.tv.TvInputService.Session;
import android.media.tv.TvTrackInfo;
import android.media.tv.TvTrackInfo.Builder;

import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAVModeBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.view.KeyEvent;

import static android.security.KeyStore.getApplicationContext;
import static com.mediatek.tvinput.dtv.TunerInputServiceSetup.TAG;

/**
 * This class implement Tuner service in TIS
 */
public class TunerInputService extends AbstractInputService {
  private static final boolean DEBUG = true;
  private final List<DataSyncThread> syncThreads = new ArrayList<DataSyncThread>();
  // private ContentResolver contentResolver;
  private DiskReceiver diskReceiver_timeshift = null;
  private DiskReceiver diskReceiver_dvr = null;
  private TunerInputSessionImpl tunerInputSessionImpl;
  private TunerRecordingSessionImpl tunerRecordSessionImpl;
  private Timer timer;// schedule time to create sync thread ;
  private int index = 1;
  private int index_record = 1;
  private int sourceTotalCount = 0;
  private TvInputInfo mCurrentInfo = null;
  // Tuner hardware device id -> Tuner inputId
  private final Map<Integer, String> mTunerHardwareInputIdMap = new HashMap<Integer, String>();

  // Tuner inputId -> Tuner TvInputInfo
  private final Map<String, TvInputInfo> mTunerInputMap = new HashMap<String, TvInputInfo>();

  /**
   * Available Main device id list for this type filter
   */
  protected List<Integer> availTunerInputDeviceIds = new ArrayList<Integer>();

  // private List<TunerInputSessionImpl> dtvSessions = new
  // ArrayList<TunerInputSessionImpl>();
  private final notifyTunerInputSessionImpl nfyTunerInputSessionImpl = new notifyTunerInputSessionImpl();

  class CreateThreadTask extends TimerTask {
    @Override
    public void run() {
      // check TvRemoteService is ready

      boolean tvRemoteServiceOK = false;
      tvRemoteServiceOK = (SystemProperties
          .get("sys.mtk.tvremoteservice.ready").equals("1"));
      Log.d(TAG, "tvRemoteServiceOK=" + tvRemoteServiceOK);
      if (tvRemoteServiceOK) {
        syncThreads.add(new ChannelSyncThread("ChannelSyncThread",
            contentResolver, TunerInputService.this));
        syncThreads.add(new ProgramSyncThread("ProgramlSyncThread",
            contentResolver, TunerInputService.this));
        syncThreads.add(new ChannelSelStatusThread(
            "ChannelSelStatusThread", contentResolver,
            TunerInputService.this));
        syncThreads.add(new RecordingFileSyncThread(
            "RecordingFileSyncThread", contentResolver,
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

    Log.d(TAG,
        "onHardwareAdded filter info=> type="
            + TvInputConst.getInputName(deviceFilter));
    Log.d(TAG, "onHardwareAdded: " + hardwareInfo.toString()
        + "\t resolveInfo" + resolveInfo.toString());

    int deviceId = hardwareInfo.getDeviceId();

    if (mTunerHardwareInputIdMap.containsKey(deviceId)) {
      Log.e(TAG, "Already created TvInputInfo for deviceId=" + deviceId);
      return null;
    }

    availTunerInputDeviceIds.add(deviceId);

    Collections.sort(availTunerInputDeviceIds);// main < sub

    TvInputInfo info = null;
    Log.d(TAG, "resolveInfo:" + resolveInfo.toString());
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
    else {
      inputName = "unknow";
    }
    Log.d(TAG,"DispName: " + inputName);
    info = new TvInputInfo.Builder(this, resolveInfo)
            .setTvInputHardwareInfo(hardwareInfo)
            .setLabel(inputName)
            .setIcon(null)
            .setCanRecord(true)
            .setTunerCount(2)
            .build();
    mTunerHardwareInputIdMap.put(deviceId, info.getId());// collect
    // device id
    // and
    // TvInputInfo
    // into
    // map.
    mTunerInputMap.put(info.getId(), info);
    Log.d(TAG, "Created TvInputInfo for deviceId=" + deviceId + "\t info=" + info.getId() + "\t"
        + info.toString());

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
        Log.d(TAG, "TvInputInfo for deviceId=" + deviceId
            + " does not exist.");
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
    return SessionList.size();
  }

  public int getTunerRecordingSessionSize() {
    return RecordSessionList.size();
  }

  public List<AbstractRecordingSession> getTunerRecordingSession() {
    return RecordSessionList;
  }

  public void setTunerRecordingSessionVideoStatus(boolean mVideoAvailable) {
    List<AbstractRecordingSession> mSessions = getTunerRecordingSession();
    if (mSessions.size() == 0) {
      return;
    }
    for (AbstractRecordingSession session : mSessions) {
      session.setRecordingSessionTunedResult(mVideoAvailable);
    }
  }

  public notifyTunerInputSessionImpl getTunerInputSession() {
    // return dtvInputSessionImpl;
    return nfyTunerInputSessionImpl;
  }

  public TvInputInfo getCurrentTvInputInfo() {
    return mCurrentInfo;
  }

  @Override
  public RecordingSession onCreateRecordingSession(String inputId) {
    Log.d(TAG, "onCreateRecordingSession inputId=" + inputId
        + ", index_record = " + index_record);
    TvInputInfo info = mTunerInputMap.get(inputId);
    int mHardwareDeviceId = -1;
    Iterator<Map.Entry<Integer, String>> iter = mTunerHardwareInputIdMap
        .entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Integer, String> entry = iter.next();
      String inputId_entry = entry.getValue();
      Log.d(TAG, "onCreateRecordingSession mTunerHardwareInputIdMap("
          + entry.getKey() + ") = " + entry.getValue());
      if (inputId.equals(inputId_entry)) {
        mHardwareDeviceId = entry.getKey();
        Log.d(TAG, "onCreateRecordingSession mHardwareDeviceId("
            + mHardwareDeviceId + ")");
        break;
      }
    }

    if (mHardwareDeviceId == -1) {
      throw new IllegalArgumentException("Unknown mHardwareDeviceId: "
          + mHardwareDeviceId + " ; this should not happen.");
    }

    if (info == null) {
      throw new IllegalArgumentException("Unknown inputId: " + inputId
          + " ; this should not happen.");
    }
    tunerRecordSessionImpl = new TunerRecordingSessionImpl(this, info,
        mHardwareDeviceId, index_record);
    // mSessionMap.put(inputId, dtvInputSessionImpl);
    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = tunerRecordSessionImpl;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_RECORDSESSION, args)
        .sendToTarget();
    Log.d(TAG,
        "onCreateRecordingSession() send DO_ADD_RECORDSESSION message, return");
    index_record = index_record + 1;

    return tunerRecordSessionImpl;
  }

  @Override
  public Session onCreateSession(String inputId) {
    Log.d(TAG, "onCreateSession inputId=" + inputId + ", index = " + index);
    TvInputInfo info = mTunerInputMap.get(inputId);
    int mHardwareDeviceId = -1;
    Iterator<Map.Entry<Integer, String>> iter = mTunerHardwareInputIdMap
        .entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<Integer, String> entry = iter.next();
      String inputId_entry = entry.getValue();
      Log.d(TAG,
          "onCreateSession mTunerHardwareInputIdMap("
              + entry.getKey() + ") = " + entry.getValue());
      if (inputId.equals(inputId_entry)) {
        mHardwareDeviceId = entry.getKey();
        Log.d(TAG, "onCreateSession mHardwareDeviceId("
            + mHardwareDeviceId + ")");
        break;
      }
    }

    if (mHardwareDeviceId == -1) {
      throw new IllegalArgumentException("Unknown mHardwareDeviceId: "
          + mHardwareDeviceId + " ; this should not happen.");
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

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args)
        .sendToTarget();
    Log.d(TAG, "onCreateSession() send DO_ADD_SESSION message, return");
    index = index + 1;

    mCurrentInfo = info;
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

    public void notifySessionEvent(final String eventType,
        final Bundle eventArgs) {
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
    // contentResolver = getContentResolver();
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

  public MtkTvChannelInfoBase getMtkChannelinfo(Channel channel) {
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
    MtkTvChannelList channelListControl = MtkTvChannelList.getInstance();
    MtkTvChannelInfoBase channelInfo = channelListControl
        .getChannelInfoBySvlRecId(svlId, svlRecId);

    return channelInfo;
  }

  public Uri getCurrentChannelUri() {
    Uri channelUri = null;
    int _id = -1;
    int channelId = getCurrentChannelId();
    long newId = (channelId & 0xffffffffL);
    Log.d(TAG, "channelId>>>" + channelId + ">>" + newId);
    if (getCurrentTvInputInfo() != null)
    {
      String[] projection = {
          TvContract.Channels._ID, TvContract.Channels.COLUMN_INPUT_ID,
          TvContract.Channels.COLUMN_TYPE
      };
      String selection = TvContract.Channels.COLUMN_INPUT_ID + " = ?";
      selection += " and substr(cast(" + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
          + " as varchar),19,10) = ?";
      String[] selectionArgs = {
          getCurrentTvInputInfo().getId(), String.format("%010d", newId)
      };
      Cursor cursor = contentResolver.query(TvContract.Channels.CONTENT_URI, projection,
          selection, selectionArgs, null);
      if (cursor != null && cursor.getCount() > 0) {
        Log.d(TAG, "cursor count " + cursor.getCount());
        cursor.moveToFirst();
        do {
          int index = -1;
          index = cursor.getColumnIndex(TvContract.Channels._ID);
          if (index >= 0) {
            _id = cursor.getInt(index);
            Log.d(TAG, " index _id = " + _id);
            break;
          }
        } while (cursor.moveToNext());
      }
      cursor.close();
      if (_id != -1) {
        channelUri = TvContract.buildChannelUri(_id);
      }
    }
    return channelUri;
  }

  class RecordCallbackHandler extends MtkTvTVCallbackHandler {
    public static final int VIDEO_FMT_UPDATE = 37;
    public static final int AUDIO_FMT_UPDATE = 38;
    private final AbstractRecordingSession session;

    public RecordCallbackHandler(AbstractRecordingSession session) {
      this.session = session;
    }

    public int notifyRecordNotification(int updateType, int argv1, int argv2)
        throws RemoteException {
      Log.d(TAG, "(RecordCallbackHandler) updateType = " + updateType
          + ",getBookFlag=" + session.getBookFlag()
          + ",getRecordingFlag = " + session.getRecordingFlag());
      if ((RecordNotifyMsgType.values()[updateType] == RecordNotifyMsgType.RECORD_PVR_NTFY_VIEW_SCHEDULE_START)
          && session.getBookFlag()) {
        Log.d(TAG,
            "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_SCHEDULE_START");
        int index = argv1;
        // isBGM = argv2 == 1;
        try {
          List<MtkTvBookingBase> bookingBases = null;
          bookingBases = MtkTvRecord.getInstance().getBookingList();
          if (bookingBases != null && bookingBases.size() > 0) {
            MtkTvBookingBase bookingBase = bookingBases.get(index);
            if (bookingBase.getDeviceIndex() == 1) {
              MtkTvRecord.getInstance().deleteBooking(index);
              if (bookingBase.getRepeatMode() != 0) {
                long startTime = bookingBase
                    .getRecordStartTime() + 24 * 60 * 60;
                bookingBase.setRecordStartTime(startTime);
                MtkTvRecord.getInstance().addBooking(
                    bookingBase);
              }
              session.resetBookFlag();
              if (session.getDtvChannelFlag()) {
                Log.d(TAG,
                    "RECORD_PVR_NTFY_VIEW_SCHEDULE_START, MtkTvRecord.getInstance().start(1) ");
                MtkTvRecord.getInstance().start(1);
              } else {
                Log.d(TAG,
                    "RECORD_PVR_NTFY_VIEW_SCHEDULE_START, MtkTvRecord.getInstance().start(0) ");
                MtkTvRecord.getInstance().start(0);
              }
              session.setRecordingFlag();
              String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING;
              Bundle IContextData = new Bundle();
              IContextData
                  .putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING_KEY,
                      MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING_VALUE);

              Log.d(TAG,
                  "(RecordCallbackHandler), RECORD_PVR_NTFY_VIEW_SCHEDULE_START"
                      + "setRecordingFlag(), "
                      + "notifySessionEvent(MTK_TIS_SESSION_EVENT_RECORDING)");
              session.notifySessionEvent(ISessionEvent,
                  IContextData);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if ((RecordNotifyMsgType.values()[updateType] == RecordNotifyMsgType.RECORD_PVR_NTFY_VIEW_STATUS_STOPPED)
          && session.getRecordingFlag()) {
        Log.d(TAG,
            "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_STATUS_STOPPED");
        //
        Thread t = new Thread(new Runnable() {
          @Override
          public void run() {
            MtkTvPvrBrowserBase base = new MtkTvPvrBrowserBase();
            MtkTvPvrBrowserItemBase recordItem = null;
            String PvrFileName = session.getRecordingFileName();
            if (PvrFileName != null) {
              recordItem = base.getPvrBrowserItemByPath(PvrFileName);
            } else {
              Log.d(TAG,
                  "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_STATUS_STOPPED, PvrFileName is null!");
              Log.d(TAG,
                  "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_STATUS_STOPPED,"
                      + " notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
              session.resetRecordingFlag();
              session.notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
              return;
            }

            if (session.mChannelUri != null) {
              ContentValues values = new ContentValues();
              String inputId = session.getChannelInputId(session.mChannelUri);
              long channelId = ContentUris.parseId(session.mChannelUri);
              values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, inputId);
              values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID,
                  channelId);

              // Uri.fromFile(new File(recordItem.mPath))
              values.put(
                  TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI,
                  ContentResolver.SCHEME_FILE + "://" + recordItem.mPath);
              values.put(TvContract.RecordedPrograms.COLUMN_TITLE,
                  recordItem.mChannelName);
              values.put(
                  TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION,
                  recordItem.mProgramName);
              values.put(
                  TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS,
                  recordItem.mStartTime);
              values.put(
                  TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS,
                  recordItem.mEndTime);
              values.put(
                  TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS,
                  recordItem.mDuration);
              contentResolver.insert(TvContract.RecordedPrograms.CONTENT_URI,
                  values);

              Uri recordedProgramUri = session.getRecordedProgramUri(inputId,
                  channelId, recordItem.mStartTime, recordItem.mEndTime);
              session.resetRecordingFlag();
              if (recordedProgramUri != null) {
                Log.d(TAG,
                    "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_STATUS_STOPPED,"
                        + " notifyRecordingStopped recordedProgramUri = "
                        + recordedProgramUri);
                session.notifyRecordingStopped(recordedProgramUri);
              } else {
                Log.d(TAG,
                    "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_STATUS_STOPPED,"
                        + " notifyRecordingStopped(null) ");
                session.notifyRecordingStopped(null);
              }
            }
          }
        });
        t.start();
      } else if ((RecordNotifyMsgType.values()[updateType] == RecordNotifyMsgType.RECORD_PVR_NTFY_VIEW_INSERT_PVR_FILE)
          && session.getRecordingFlag()) {
        Log.d(TAG,
            "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_INSERT_PVR_FILE");
        Thread t = new Thread(new Runnable() {
          @Override
          public void run() {
            String PvrFileName = MtkTvRecord.getInstance()
                .getRecordingFileName();
            Log.d(TAG,
                "(RecordCallbackHandler) RECORD_PVR_NTFY_VIEW_INSERT_PVR_FILE, setRecordingFileName("
                    + PvrFileName + ")");
            session.setRecordingFileName(PvrFileName);
          }
        });
        t.start();
      }
      return 0;
    }
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
            || (timeshift.getDuration() >= 5000)) {
          timer.cancel();
          Log.d(TAG, "(TimeshiftCallbackHandler)timer.cancel()");
          if (timeshift.getRecordStatus() != TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) {
            Log.d(TAG,
                "(TimeshiftCallbackHandler) GetDurationTask "
                    + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
            session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
          } else {
            Log.d(TAG,
                "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus(TIMESHIFT_RECORD_STARTED),"
                    + "GetDurationTask notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_AVAILABLE)\n");
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
      // Log.d(TAG,
      // "(TimeshiftCallbackHandler) notifySvctxNotificationCode=" +
      // code);
      if ((((TunerInputSessionImpl) session).getTS_SwitchChannelFlag())
          && (code == VIDEO_FMT_UPDATE)) {
        Log.d(TAG,
            "(TimeshiftCallbackHandler) notifySvctxNotificationCode(VIDEO_FMT_UPDATE) "
                + "and b_TS_switchChannel = true");
        Thread t = new Thread(new Runnable() {
          @Override
          public void run() {
            MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
            timeshift.setAutoRecord(true);
            Log.d(TAG,
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
    public int notifyTimeshiftRecordStatus(int status, long argv1)
        throws RemoteException {
      Log.d(TAG,
          "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus status="
              + status + "argv1=" + argv1);
      MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
      if (status == TimeshiftRecordStatus.TIMESHIFT_RECORD_STOPPED
          .Value()) {
        synchronized (((TunerInputSessionImpl) session)
            .getTSSyncObject()) {
          ((TunerInputSessionImpl) session).setTSSyncCondition();
          ((TunerInputSessionImpl) session).getTSSyncObject()
              .notify();
          Log.d(TAG, "Thread " + Thread.currentThread().getName()
              + ", ts_sync_object notify");
        }
        Log.d(TAG, "Thread " + Thread.currentThread().getName()
            + ", ts_sync_object release mutex");
        Log.d(TAG,
            "(TimeshiftCallbackHandler) notifyTimeshiftRecordStatus(TIMESHIFT_RECORD_STOPED), "
                + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
        session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
      } else if (status == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED
          .Value()) {
        /*
         * timer = new Timer(); timer.schedule(new GetDurationTask(), 0, 100);
         */
        Log.d(TAG,
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
      Log.d(TAG,
          "(TimeshiftCallbackHandler) notifyTimeshiftNoDiskFile argv1="
              + argv1);
      Log.d(TAG,
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
      Log.d(TAG,
          "(TimeshiftCallbackHandler) notifyTimeshiftSpeedUpdate speed="
              + speed);

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
    public int notifyTimeshiftPlaybackStatusUpdate(int status)
        throws RemoteException {
      Log.d(TAG,
          "(TimeshiftCallbackHandler) notifyTimeshiftPlaybackStatusUpdate status="
              + status);

      String ISessionEvent = "session_event_timeshift_playbackstatusupdate";
      Bundle IContextData = new Bundle();
      if (status == TimeshiftPlaybackStatus.TIMESHIFT_PLAYBACK_STOPPING.Value())
      {
        Log.d(TAG,
            "(TimeshiftCallbackHandler) MMP playback stopping TIME_SHIFT_STATUS_UNAVAILABLE");
        session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
      }
      else if (status == TimeshiftPlaybackStatus.TIMESHIFT_PLAYBACK_STOPED.Value())
      {
        MtkTvTimeshift timeshift = MtkTvTimeshift.getInstance();
        MtkTvTimeshiftBase.TimeshiftRecordStatus timeshif_tstatus = TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;
        timeshif_tstatus = timeshift.getRecordStatus();
        if (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) {
          Log.d(
              TAG,
              "(TimeshiftCallbackHandler) MMP playback stopped and TIMESHIFT_RECORD_STARTED TIME_SHIFT_STATUS_AVAILABLE");
          session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_AVAILABLE);
        }
        else {
          Log.d(TAG, "(TimeshiftCallbackHandler) not notify TIME_SHIFT_STATUS_AVAILABLE");
        }
      }
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
    // Log.d(TAG,
    // "(TimeshiftCallbackHandler) notifyTimeshiftStorageRemoved");
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
    private AbstractInputSession session = null;
    private AbstractRecordingSession record_session = null;

    public DiskReceiver(AbstractInputSession session) {
      this.session = session;
    }

    public DiskReceiver(AbstractRecordingSession record_session) {
      this.record_session = record_session;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
      int state = -1;
      if (TextUtils.equals(intent.getAction(),
          VolumeInfo.ACTION_VOLUME_STATE_CHANGED)) {
        if (session != null) {
          state = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE,
              -1);
          if (state == VolumeInfo.STATE_UNMOUNTED
              || state == VolumeInfo.STATE_BAD_REMOVAL) {
            Log.d(TAG,
                "DiskReceiver (onReceive) ACTION_VOLUME_STATE_CHANGED(), "
                    + "VolumeInfo.STATE_UNMOUNTED or VolumeInfo.STATE_BAD_REMOVAL\n");
            session.notifyTimeShiftStatusChanged(TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
            MtkTvTimeshift timeshift = ((TunerInputSessionImpl) session)
                .getTimeShiftObject();
            if (timeshift != null) {
              if (timeshift.getRecordStatus() == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) {
                timeshift
                    .stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV);
              }
            }
          }
        } else if (record_session != null) {
          // recording session job
          state = intent.getIntExtra(VolumeInfo.EXTRA_VOLUME_STATE,
              -1);
          if (state == VolumeInfo.STATE_UNMOUNTED
              || state == VolumeInfo.STATE_BAD_REMOVAL) {
            Log.d(TAG,
                "DiskReceiver (onReceive) ACTION_VOLUME_STATE_CHANGED(), "
                    + "VolumeInfo.STATE_UNMOUNTED or VolumeInfo.STATE_BAD_REMOVAL\n");

            int status = MtkTvRecord.getInstance().getStatus();
            if (status == 1) // RECORD_PVR_RECORDING
            {
              Log.d(TAG, "Recording Session onRelease,  "
                  + "stop rec\n");
              MtkTvRecord.getInstance().stop();
            } else if (status == 4) { // RECORD_PVR_SCHEDULING
              List<MtkTvBookingBase> bookingBases = null;
              bookingBases = MtkTvRecord.getInstance()
                  .getBookingList();
              if (bookingBases != null && bookingBases.size() > 0) {
                for (int index = 0; index < bookingBases.size(); index++) {
                  MtkTvBookingBase bookingBase = bookingBases
                      .get(index);
                  if (bookingBase.getDeviceIndex() == 1) {
                    MtkTvRecord.getInstance()
                        .deleteBooking(index);
                  }
                }
              }
            }
            record_session
                .notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
          }
        }
      }
    }
  }

  class TunerRecordingSessionImpl extends AbstractRecordingSession {
    private RecordCallbackHandler dvrCallback = null;

    protected TunerRecordingSessionImpl(
        AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(Tuner)";
      if (SystemProperties.get("sys.mtk.tif.dvr").equals("1")) {
        diskReceiver_dvr = new DiskReceiver(this);
        IntentFilter diskFilter = new IntentFilter(
            VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
        getApplicationContext().registerReceiver(diskReceiver_dvr,
            diskFilter);

        dvrCallback = new RecordCallbackHandler(this);
      }
    }

    public boolean tuneChannel(Uri channelUri) {
      if (channelUri == null
          || !channelUri.toString().startsWith(
              TvContract.Channels.CONTENT_URI.toString())) {
        Log.e(TAG,
            "tuneChannel, channelUri is null or invalid channelUri! return false\n");
        return false;
      }

      if (channelUri.equals(getCurrentChannelUri())) {
        Log.d(TAG,
            "tuneChannel, channelUri == getCurrentChannelUri\n");
        //return true;
      }

      Channel c = getChannel(channelUri);
      if (c == null) {
        Log.e(TAG, "tuneChannel, getChanne() is null, return false\n");
        return false;
      }

      Log.d(TAG, "tuneChannel," + c.toString());
      MtkTvChannelInfoBase mtkTvChannelInfo = getMtkChannelinfo(c);
      if (mtkTvChannelInfo == null) {
        Log.e(TAG,
            "tuneChannel getMtkChannelinfo() is null,return false\n");
        return false;
      }

      MtkTvBroadcast broadcastControl = MtkTvBroadcast.getInstance();
      Log.d(TAG, "tuneChannel call broadcastControl.channelSelect ");
      int ret = broadcastControl.channelSelect(mtkTvChannelInfo, false);
      Log.d(TAG, "tuneChannel channelSelect, ret:" + ret);
      if (ret != 0) {
        Log.e(TAG, "tuneChannel channelSelect, ret: " + ret
            + ", return false");
        return false;
      }
      return true;
    }

    @Override
    public void onTune(Uri channelUri, Bundle params) {
      // Log.d(TAG, "onTune, channelUri = " + channelUri.toString());
      // Log.d(TAG, "onTune, Current mChannelUri = " + mChannelUri.toString());
      if (channelUri == null)
      {
        Log.e(TAG, "onTune, (channelUri == null), return");
        return;
      }
      if (channelUri.equals(mChannelUri))
      {
        Log.e(TAG, "onTune, (channelUri == mChannelUri), return");
        return;
      }

      boolean tune_status = tuneChannel(channelUri);
      if (tune_status == false) {
        Log.e(TAG,
            "onTune, tuneChannel fail, notifyError TvInputManager.RECORDING_ERROR_UNKNOWN");
        notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
      } else {
        super.onTune(channelUri, params);
      }
    }

    @Override
    public void onTune(Uri channelUri) {
      // Log.d(TAG, "onTune, channelUri = " + channelUri.toString());
      // Log.d(TAG, "onTune, Current mChannelUri = " + mChannelUri.toString());
      if (channelUri == null)
      {
        Log.e(TAG, "onTune, (channelUri == null), return\n");
        return;
      }
      if (channelUri.equals(mChannelUri))
      {
        Log.e(TAG, "onTune, (channelUri == mChannelUri), return\n");
        return;
      }

      boolean tune_status = tuneChannel(channelUri);
      if (tune_status == false) {
        Log.e(TAG,
            "onTune, tuneChannel fail, notifyError TvInputManager.RECORDING_ERROR_UNKNOWN");
        notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
      } else {
        super.onTune(channelUri);
      }
    }

    public void onAppPrivateCommand(String action, Bundle data) {
      if (MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION.equals(action)) {
        Thread t = new Thread(new Runnable() {
          @Override
          public void run()
          {
            int duration = MtkTvRecord.getInstance().getRecordingPosition();
            Log.d(TAG,
                "onAppPrivateCommand, "
                    + "MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION action, duration = ("
                    + duration + ")\n");

            String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION;
            Bundle IContextData = new Bundle();
            IContextData
                .putInt(
                    MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION_VALUE,
                    duration);

            notifySessionEvent(ISessionEvent, IContextData);
            Log.d(TAG,
                "onAppPrivateCommand,"
                    + "notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION,"
                    + " MTK_TIS_SESSION_EVENT_DVR_RECORD_DURATION_VALUE = " + duration + ")");
            return;
          }
        });
        t.start();
      }
    }

    @Override
    public void onRelease() {
      Log.d(TAG, "Recording Session release");
      int status = -1;
      if (SystemProperties.get("sys.mtk.tif.dvr").equals("1")) {
        status = MtkTvRecord.getInstance().getStatus();
        if (status == 1) // RECORD_PVR_RECORDING
        {
          Log.d(TAG, "Recording Session onRelease,  " + "stop rec\n");
          MtkTvRecord.getInstance().stop();
        } else if (status == 4) { // RECORD_PVR_SCHEDULING
          List<MtkTvBookingBase> bookingBases = null;
          bookingBases = MtkTvRecord.getInstance().getBookingList();
          if (bookingBases != null && bookingBases.size() > 0) {
            for (int index = 0; index < bookingBases.size(); index++) {
              MtkTvBookingBase bookingBase = bookingBases
                  .get(index);
              if (bookingBase.getDeviceIndex() == 1) {
                MtkTvRecord.getInstance().deleteBooking(index);
              }
            }
          }
        }

        try {
          getApplicationContext()
              .unregisterReceiver(diskReceiver_dvr);
        } catch (Exception e) {
          Log.d(TAG,
              "Recording Session onRelease unregisterReceiver(diskReceiver) error");
        }

        diskReceiver_dvr = null;
        dvrCallback.removeListener();
        dvrCallback = null;
      }
      super.onRelease();
    }
  }

  /**
   * DTV <BR>
   */
  class TunerInputSessionImpl extends AbstractInputSession {
    public static final float PlaybackSpeed_SF1_2 = (1 / 2);
    public static final float PlaybackSpeed_SF1_4 = (1 / 4);
    public static final float PlaybackSpeed_SF1_8 = (1 / 8);
    public static final float PlaybackSpeed_SF1_16 = (1 / 16);
    public static final float PlaybackSpeed_SF1_32 = (1 / 32);

    public static final float PlaybackSpeed_SR1_2 = (-1) * (1 / 2);
    public static final float PlaybackSpeed_SR1_4 = (-1) * (1 / 4);
    public static final float PlaybackSpeed_SR1_8 = (-1) * (1 / 8);
    public static final float PlaybackSpeed_SR1_16 = (-1) * (1 / 16);
    public static final float PlaybackSpeed_SR1_32 = (-1) * (1 / 32);

    public static final int PlaybackSpeed_XF1 = 1;
    public static final int PlaybackSpeed_XF2 = 2;
    public static final int PlaybackSpeed_XF4 = 4;
    public static final int PlaybackSpeed_XF8 = 8;
    public static final int PlaybackSpeed_XF12 = 12;
    public static final int PlaybackSpeed_XF16 = 16;
    public static final int PlaybackSpeed_XF32 = 32;
    public static final int PlaybackSpeed_XF48 = 48;
    public static final int PlaybackSpeed_XF64 = 64;
    public static final int PlaybackSpeed_XF128 = 128;
    public static final int PlaybackSpeed_XR1 = -1;
    public static final int PlaybackSpeed_XR2 = -2;
    public static final int PlaybackSpeed_XR4 = -4;
    public static final int PlaybackSpeed_XR8 = -8;
    public static final int PlaybackSpeed_XR12 = -12;
    public static final int PlaybackSpeed_XR16 = -16;
    public static final int PlaybackSpeed_XR32 = -32;
    public static final int PlaybackSpeed_XR48 = -48;
    public static final int PlaybackSpeed_XR64 = -64;
    public static final int PlaybackSpeed_XR128 = -128;

    private static final String EXTRA_REQUEST_TIMESHIFT = "com.mediatek.tvinput.dtv.DTVInputService.REQUEST_TIMESHIFT";

    private static final String DVR_PLAYBACK_STATUS = "PlaybackStatusUpdate";

    private static final String DVR_PLAYBACK_PLAY = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PLAY";
    private static final String DVR_PLAYBACK_PAUSE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PAUSE";
    private static final String DVR_PLAYBACK_PLAY_COMPLETE = "com.mediatek.tvinput.dtv.TunerInputService.DVR_PLAYBACK_PLAY_COMPLETE";

    private MtkTvTimeshift timeshift = null;// TODO Need twoworld API
    private TimeshiftCallbackHandler timeshiftCallback = null;

    private final MtkTvBroadcast broadcastControl;// TODO Need twoworld API
    // private MtkTvChannelList channelListControl;// TODO Need twoworld API
    private final List<MtkTvChannelInfoBase> channels = null;// TODO Need
    // twoworld
    // API
    private SharedPreferences mSharedPreferences;
    private final MtkTvAVModeBase mAudio = MtkTvAVMode.getInstance();
    private byte isBarkerChannel = MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE;

    private int flag_Tune_RecordPlay = -1; // identify this session is for
    // tune channel(0) or for
    // record play(1)
    private RecordPlayView mRecordPlayView = null;
    private boolean b_TS_switchChannel = false;

    public boolean getTS_SwitchChannelFlag() {
      return b_TS_switchChannel;
    }

    public void resetTS_SwitchChannelFlag() {
      b_TS_switchChannel = false;
    }

    /* Tune Timeshift sync object */
    private final Object ts_sync_object = new Object();

    public Object getTSSyncObject() {
      return ts_sync_object;
    }

    private boolean ts_sync_condition = false;

    public void setTSSyncCondition() {
      ts_sync_condition = true;
    }

    /* Tune Timeshift sync object */

    /* Tune and DVR playback sync object */
    private final Object tune_sync_object = new Object();

    public Object getTuneSyncObject() {
      return tune_sync_object;
    }

    private boolean tune_sync_condition = false;

    public void setTuneSyncCondition() {
      tune_sync_condition = true;
    }

    /* Tune and DVR playback sync object */

    /* setsurface and surfacechanged sync object */
    private final Object surface_sync_object = new Object();

    public Object getSurfaceSyncObject() {
      return surface_sync_object;
    }

    private boolean surface_sync_condition = false;

    public void setSurfaceSyncCondition() {
      surface_sync_condition = true;
    }

    /* setsurface and surfacechanged sync object */

    public MtkTvTimeshift getTimeShiftObject() {
      return timeshift;
    }

    protected TunerInputSessionImpl(
        AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(Tuner)";

      broadcastControl = MtkTvBroadcast.getInstance();// TODO Need
      // twoworld API

      if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
        diskReceiver_timeshift = new DiskReceiver(this);
        timeshift = MtkTvTimeshift.getInstance();
        timeshiftCallback = new TimeshiftCallbackHandler(this);

        IntentFilter diskFilter = new IntentFilter(
            VolumeInfo.ACTION_VOLUME_STATE_CHANGED);
        getApplicationContext().registerReceiver(
            diskReceiver_timeshift, diskFilter);
      }

      mRecordPlayView = new RecordPlayView((Context) this.inputService);

      mRecordPlayView.setOnPreparedListener(mPreparedListener);
      mRecordPlayView.setOnCompletionListener(mCompletionListener);
      mRecordPlayView.setOnInfoListener(mInfoListener);
      mRecordPlayView.setOnSeekCompleteListener(mSeekCompletionListener);
      mRecordPlayView.setOnErrorListener(mErrorListener);
    }

    public AbstractInputService getTunerInputService() {
      return inputService;
    }

    @Override
    public void onRelease() {
      Log.d(TAG, "Session release");
      tune_sync_condition = false;
      surface_sync_condition = false;
      ts_sync_condition = false;

      if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
        Log.d(TAG,
            "onRelease, Runnable start");
        Thread t = new Thread(new Runnable() {
          @Override
          public void run() {
            MtkTvTimeshiftBase.TimeshiftRecordStatus timeshif_tstatus = TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;
            timeshif_tstatus = timeshift.getRecordStatus();
            if (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) {
              Log.d(
                  TAG,
                  "onRelease, Runnable:(timeshif_tstatus == MtkTvTimeshiftBase.TIMESHIFT_RECORD_STARTED) "
                      + "stop rec\n");
              timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
            }

            try {
              Log.d(TAG,
                  "onRelease, Runnable:unregisterReceiver(diskReceiver_timeshift)");
              getApplicationContext().unregisterReceiver(
                  diskReceiver_timeshift);
            } catch (Exception e) {
              Log.d(TAG,
                  "onRelease, Runnable:unregisterReceiver(diskReceiver_timeshift) error");
            }

            Log.d(TAG,
                "onRelease, Runnable:timeshiftCallback removeListener");
            diskReceiver_timeshift = null;
            timeshiftCallback.removeListener();
            timeshiftCallback = null;
            timeshift = null;
          }
        });
        t.start();
      }

      if (flag_Tune_RecordPlay == 1) {
        setSurface_RecordPlay(null);
      }
      flag_Tune_RecordPlay = -1;
      mRecordPlayView.setOnCompletionListener(null);
      mRecordPlayView.setOnInfoListener(null);
      mRecordPlayView.setOnErrorListener(null);
      mRecordPlayView.setOnSeekCompleteListener(null);
      mRecordPlayView.setOnPreparedListener(null);
      mRecordPlayView = null;

      super.onRelease();
    }

    @Override
    public boolean onSelectTrack(int type, String trackId) {
      if (flag_Tune_RecordPlay == 0) {
        int ret = 0;
        boolean b_ret = false;
        Log.d(TAG,
            "Enter onSelectTrack(flag_Tune_RecordPlay == 0), type="
                + type + "trackId=" + trackId);
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
        } else if (TvTrackInfo.TYPE_AUDIO == type) {
          b_ret = mAudio.selectAudioById(trackId);
          Log.d(TAG, "Select audio Track: " + b_ret);
          return b_ret;
        }

        Log.d(TAG, "Leave onSelectTrack");
        return false;
      } else if (flag_Tune_RecordPlay == 1) {
        Log.d(TAG, "onSelectTrack(flag_Tune_RecordPlay == 1)"
            + ", type = " + type + ", trackId = " + trackId
            + ", call mRecordPlayView.selectTrack");
        return mRecordPlayView.selectTrack(type, trackId);
      }
      Log.e(TAG,
          "flag_Tune_RecordPlay !=0 && != 1, Leave onSelectTrack return false");
      return false;
    }

    @Override
    public void onUnblockContent(TvContentRating unblockedRating) {
      Log.d(TAG,
          "onUnblockContent: unblockRating=["
              + unblockedRating.flattenToString() + "]\n");

      String[] focusWin = {
          "main", "sub"
      };
      MtkTvAppTVBase tvAppTvBase = new MtkTvAppTVBase();

      int result = MtkTvConfig.getInstance().getConfigValue(
          MtkTvConfigType.CFG_PIP_POP_TV_FOCUS_WIN);
      result = (0 == result) ? MtkTvConfigType.TV_FOCUS_WIN_MAIN
          : MtkTvConfigType.TV_FOCUS_WIN_SUB;

      tvAppTvBase.unlockService(focusWin[result]);
      this.notifyContentAllowed();
    }

    public void onAppPrivateCommand(String action, Bundle data) {
      final String mAction = action;
      final Bundle mData = data;
      if (flag_Tune_RecordPlay == 0) {
        Log.d(TAG,
            "onAppPrivateCommand (flag_Tune_RecordPlay == 0) call onAppPrivateCommandImpl()");
        onAppPrivateCommandImpl(action, data);
        return;
      } else if (flag_Tune_RecordPlay == 1) {
        // do nothings
        Log.d(TAG,
            "onAppPrivateCommand (flag_Tune_RecordPlay == 1) call onAppPrivateCommandImpl_DVR()");
        onAppPrivateCommandImpl_DVR(action, data);
        return;
      }

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          synchronized (tune_sync_object) {
            try {
              while (!tune_sync_condition) {
                Log.d(TAG, "onAppPrivateCommand Thread "
                    + Thread.currentThread().getName()
                    + ", tune_sync_object wait");
                tune_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onAppPrivateCommand Thread " + Thread.currentThread().getName()
                + ", tune_sync_object get mutex");
          }
          if (flag_Tune_RecordPlay == 0) {
            Log.d(TAG,
                "onAppPrivateCommand (flag_Tune_RecordPlay == 0) call onAppPrivateCommandImpl()");
            onAppPrivateCommandImpl(mAction, mData);
            return;
          } else if (flag_Tune_RecordPlay == 1) {
            // do nothings
            Log.d(TAG,
                "onAppPrivateCommand (flag_Tune_RecordPlay == 1) call onAppPrivateCommandImpl_DVR()");
            onAppPrivateCommandImpl_DVR(mAction, mData);
            return;
          }
        }
      });
      t.start();
      return;
    }

    public void onAppPrivateCommandImpl_DVR(String action, Bundle data) {
      Log.d(TAG, "action: " + action);
      int ret = 0;
      if (MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_SETPIN.equals(action)) {
        int pin = data.getInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_PIN_VALUE);
        ret = mRecordPlayView.setUnLockPin(pin);

        Log.d(TAG,
            "onAppPrivateCommandImpl_DVR, "
                + "mRecordPlayView.setUnLockPin(" + pin + ")" + ", ret = ("
                + ret + ")\n");
      }
      return;
    }

    public void onAppPrivateCommandImpl(String action, Bundle data) {
      Log.d(TAG, "action: " + action);

      final String mAction = action;

      if (MtkTvTISMsgBase.MTK_TIS_MSG_RESET.equals(action)) {
        isBarkerChannel = MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE;
      } else if (MtkTvTISMsgBase.MTK_TIS_MSG_CHANNEL.equals(action)) {
        if (null != data) {
          isBarkerChannel = data
              .getByte(MtkTvTISMsgBase.MSG_CHANNEL_IS_BARKER_CHANNEL);
          Log.d(TAG, "isBarkerChannel: " + isBarkerChannel);
        }
      } else {
        Thread t = new Thread(new Runnable() {
          @Override
          public void run()
          {
            int ret = 0;
            if (timeshift != null) {
              if ((mAction
                  .compareTo("session_event_timeshift_stop_mmp_and_select_tv")) == 0) {
                ret = timeshift
                    .stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV);
                Log.d(
                    TAG,
                    "onAppPrivateCommand, "
                        + "timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_MMP_AND_SELECT_TV), ret = ("
                        + ret + ")\n");
                return;
              } else if ((mAction.compareTo("session_event_timeshift_stop_rec")) == 0) {
                ret = timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
                Log.d(TAG,
                    "onAppPrivateCommand, "
                        + "timeshift.stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD), ret = ("
                        + ret + ")\n");
                return;
              }
            }
          }
        });
        t.start();
      }
      return;
    }

    private void handlePrepare() {

    }

    private void handleComplete() {
      String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK;
      Bundle IContextData = new Bundle();
      IContextData
          .putString(
              MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
              MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_COMPLETE_VALUE);
      if (mRecordPlayView != null) {
        Log.d(TAG, "handleComplete, call mRecordPlayView.stopPlayback");
        mRecordPlayView.stopPlayback();
      }
      notifySessionEvent(ISessionEvent, IContextData);
      Log.d(TAG,
          "handleComplete, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
              + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_COMPLETE_VALUE)");
    }

    private boolean handleInfo(int what) {
      Log.d(TAG, "handleInfo: what:" + what);
      List<TvTrackInfo> track_list = new ArrayList<TvTrackInfo>();
      String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK;
      Bundle IContextData = new Bundle();

      switch (what) {
        case RecordPlayView.MTK_MEDIA_INFO_VIDEO_ENCODE_FORMAT_UNSUPPORT:
        case RecordPlayView.MTK_MEDIA_INFO_AUDIO_ENCODE_FORMAT_UNSUPPORT:
          Log.d(
              TAG,
              "handleInfo: what = MTK_MEDIA_INFO_VIDEO_ENCODE_FORMAT_UNSUPPORT/MTK_MEDIA_INFO_AUDIO_ENCODE_FORMAT_UNSUPPORT");
          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_VIDEO_ENCODE_FORMAT_UNSUPPORT);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(
              TAG,
              "handleInfo, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_VIDEO_ENCODE_FORMAT_UNSUPPORT)");
          break;
        case RecordPlayView.MTK_MEDIA_INFO_METADATA_UPDATE:
          Log.d(TAG, "handleInfo: what = MTK_MEDIA_INFO_METADATA_UPDATE");
          track_list = mRecordPlayView.getAllTrackInfo();
          notifyTracksChanged(track_list);
          break;
        case RecordPlayView.MTK_MEDIA_INFO_VIDEO_RENDERING_START:
          Log.d(TAG,
              "handleInfo: what = MTK_MEDIA_INFO_VIDEO_RENDERING_START");
          track_list = mRecordPlayView.getAllTrackInfo();
          notifyTracksChanged(track_list);

          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_VALUE);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleInfo, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_PLAY_VALUE)");
          break;
        case RecordPlayView.MTK_MEDIA_INFO_VIDEO_RATING_LOCKED:
        case RecordPlayView.MTK_MEDIA_INFO_VIDEO_LOCKED:
          Log.d(TAG,
              "handleInfo: what = MTK_MEDIA_INFO_VIDEO_RATING_LOCKED/MTK_MEDIA_INFO_VIDEO_LOCKED");

          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_LOCKED);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleInfo, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_LOCKED)");
          break;
        case RecordPlayView.MTK_MEDIA_INFO_ON_REPLAY:
          Log.d(TAG,
              "handleInfo: what = MTK_MEDIA_INFO_ON_REPLAY");

          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ON_REPLY_VALUE);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleInfo, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ON_REPLY_VALUE)");
          break;
        case RecordPlayView.MTK_MEDIA_INFO_VIDEO_REPLAY_DONE:
          Log.d(TAG,
              "handleInfo: what = MTK_MEDIA_INFO_VIDEO_REPLAY_DONE");

          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_REPLY_DONE_VALUE);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleInfo, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_REPLY_DONE_VALUE)");
          break;
        /*
         * case VideoManager.MEDIA_INFO_ON_REPLAY: case VideoManager.MEDIA_INFO_VIDEO_REPLAY_DONE:
         * case VideoManager.MEDIA_INFO_AUDIO_ONLY_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MEDIA_INFO_AUDIO_ONLY_SERVICE"); case
         * VideoManager.MEDIA_INFO_VIDEO_ENCODE_FORMAT_UNSUPPORT: case
         * VideoManager.MEDIA_INFO_VIDEO_ONLY_SERVICE: case
         * VideoManager.MEDIA_INFO_AUDIO_ENCODE_FORMAT_UNSUPPORT: case
         * VideoManager.MEDIA_INFO_POSITION_UPDATE: case
         * VideoManager.MTK_MEDIA_INFO_SCRAMBLED_AUDIO_VIDEO_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MTK_MEDIA_INFO_SCRAMBLED_AUDIO_VIDEO_SERVICE"); featureNotWork(mResource
         * .getString(R.string.mmp_media_info_scrambled_audio_video_service )); break; case
         * VideoManager.MTK_MEDIA_INFO_SCRAMBLED_AUDIO_CLEAR_VIDEO_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MTK_MEDIA_INFO_SCRAMBLED_AUDIO_CLEAR_VIDEO_SERVICE" );
         * featureNotWork(mResource .getString(R.string.
         * mmp_media_info_scrambled_audio_clear_video_service)); break; case
         * VideoManager.MTK_MEDIA_INFO_SCRAMBLED_AUDIO_NO_VIDEO_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MTK_MEDIA_INFO_SCRAMBLED_AUDIO_NO_VIDEO_SERVICE"); featureNotWork(mResource
         * .getString(R.string.mmp_media_info_scrambled_audio_no_video_service )); break; case
         * VideoManager.MTK_MEDIA_INFO_SCRAMBLED_VIDEO_CLEAR_AUDIO_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MTK_MEDIA_INFO_SCRAMBLED_VIDEO_CLEAR_AUDIO_SERVICE" );
         * featureNotWork(mResource .getString(R.string.
         * mmp_media_info_scrambled_video_clear_audio_service)); break; case
         * VideoManager.MTK_MEDIA_INFO_SCRAMBLED_VIDEO_NO_AUDIO_SERVICE: MtkLog.d(TAG,
         * "enter onInfo:MTK_MEDIA_INFO_SCRAMBLED_VIDEO_NO_AUDIO_SERVICE"); featureNotWork(mResource
         * .getString(R.string.mmp_media_info_scrambled_video_no_audio_service )); break; case
         * VideoManager.MTK_MEDIA_INFO_VID_INFO_UPDATE: break;
         */
        default:
          break;
      }
      return true;
    }

    private boolean handleError(int what) {
      String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK;
      Bundle IContextData = new Bundle();

      switch (what) {
        case RecordPlayView.MTK_MEDIA_ERROR_UNKNOWN:
          Log.d(TAG, "handleError: what = MTK_MEDIA_ERROR_UNKNOWN");
          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ERROR_UNKNOWN);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleError, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_ERROR_UNKNOWN)");
          break;
        case RecordPlayView.MTK_MEDIA_ERROR_FILE_CORRUPT:
          Log.d(TAG, "handleError: what = MTK_MEDIA_ERROR_FILE_CORRUPT");
          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_CORRUPT);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleError, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_CORRUPT)");
          break;
        case RecordPlayView.MTK_MEDIA_ERROR_FILE_NOT_SUPPORT:
        case RecordPlayView.MTK_MEDIA_ERROR_OPEN_FILE_FAILED:
          Log.d(TAG,
              "handleError: what = MTK_MEDIA_ERROR_FILE_NOT_SUPPORT/MTK_MEDIA_ERROR_OPEN_FILE_FAILED");
          IContextData
              .putString(
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
                  MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_NOTSUPPORT);
          notifySessionEvent(ISessionEvent, IContextData);
          Log.d(TAG,
              "handleError, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
                  + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_FILE_NOTSUPPORT)");
          break;
        default:
          Log.d(TAG, "handleError: what = " + what + ", default break!");
          break;
      }
      return true;
    }

    private void handleSeekComplete() {
      String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK;
      Bundle IContextData = new Bundle();
      Log.d(TAG, "handleSeekComplete:");
      IContextData
          .putString(
              MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_KEY,
              MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_SEEK_COMPLETE_VALUE);
      notifySessionEvent(ISessionEvent, IContextData);
      Log.d(TAG,
          "handleSeekComplete, notifySessionEvent(MTK_TIS_SESSION_EVENT_DVR_PLAYBACK,"
              + " MTK_TIS_SESSION_EVENT_DVR_PLAYBACK_STATUS_SEEK_COMPLETE_VALUE)");
    }

    private final MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "mPreparedListener, onPrepared: ");
        handlePrepare();
      }
    };

    private final MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
      @Override
      public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "mCompletionListener, onCompletion: ");
        handleComplete();
      }
    };

    private final MediaPlayer.OnInfoListener mInfoListener = new MediaPlayer.OnInfoListener() {
      @Override
      public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "mInfoListener, onInfo, what: " + what + ", extra: "
            + extra);
        return handleInfo(what);
      }
    };

    private final MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
      @Override
      public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "mErrorListener onError, what: " + what
            + ", extra: " + extra);
        return handleError(what);
      }
    };

    private final MediaPlayer.OnSeekCompleteListener mSeekCompletionListener = new MediaPlayer.OnSeekCompleteListener() {
      @Override
      public void onSeekComplete(MediaPlayer mp) {
        Log.d(TAG, "OnSeekCompleteListener onSeekComplete");
        handleSeekComplete();
      }
    };

    public void onTimeShiftPlay(Uri recordedProgramUri) {
      TunerInputService tunerInputService = (TunerInputService) getTunerInputService();
      List<DataSyncThread> syncThreads = tunerInputService.syncThreads;
      if (syncThreads != null) {
        for (DataSyncThread thread : syncThreads) {
          if (thread.getClass() != ChannelSelStatusThread.class) {
            continue;
          }
          thread.setMonitor(false);
        }
      }
      flag_Tune_RecordPlay = 1;
      synchronized (getTuneSyncObject()) {
        setTuneSyncCondition();
        getTuneSyncObject().notifyAll();
        Log.d(TAG, "onTimeShiftPlay, Thread " + Thread.currentThread().getName()
            + ", tune_sync_object notifyAll");
      }
      Log.d(TAG, "onTimeShiftPlay, Thread " + Thread.currentThread().getName()
          + ", tune_sync_object release mutex");

      RecordedProgram mRecordedProgram = getRecordedProgram(recordedProgramUri);
      String path = mRecordedProgram.getPath();
      Log.d(TAG, "onTimeShiftPlay, mRecordedProgram.getPath(), path = ("
          + path + ")\n");
      String startWith = ContentResolver.SCHEME_FILE + "://";
      if (path == null || !path.startsWith(startWith)) {
        Log.d(TAG, "onTimeShiftPlay, path invalid, return\n");
        return;
      }
      String mPath = path.substring(startWith.length());
      Log.d(TAG,
          "onTimeShiftPlay, mRecordPlayView.setVideoPath(), mPath = ("
              + mPath + ")\n");
      mRecordPlayView.setVideoPath(mPath);
      Log.d(TAG, "onTimeShiftPlay, mRecordPlayView.start() \n");
      mRecordPlayView.start();
    }

    public long onTimeShiftGetStartPosition() {
      if (flag_Tune_RecordPlay == 0) {
        long startPostion = timeshift.getStartPosition();
        Log.d(TAG,
            "onTimeShiftGetStartPosition, timeshift.getStartPosition(), startPostion = ("
                + startPostion + ")\n");
        return startPostion;
      }
      return 0;
    }

    public long onTimeShiftGetCurrentPosition() {
      long currentPostion = 0;
      if (flag_Tune_RecordPlay == 0) {
        currentPostion = timeshift.getCurrentPosition();
        Log.d(TAG,
            "onTimeShiftGetCurrentPosition, timeshift.getCurrentPosition(), currentPostion = ("
                + currentPostion + ")\n");
        return currentPostion;
      } else if (flag_Tune_RecordPlay == 1) {
        currentPostion = mRecordPlayView.getCurrentPosition();
        Log.d(TAG,
            "onTimeShiftGetCurrentPosition, mRecordPlayView.getCurrentPosition(), currentPostion = ("
                + currentPostion + ")\n");
        return currentPostion;
      }
      return 0;
    }

    public void onTimeShiftPause() {
      int ret = 0;
      if (flag_Tune_RecordPlay == 0) {
        ret = timeshift.setPlaybackPause();
        Log.d(TAG,
            "onTimeShiftPause, timeshift.setPlaybackPause(), ret = ("
                + ret + ")\n");

        if (ret != 0) {
          Log.d(TAG, "onTimeShiftPause, timeshift.getErrorID() = "
              + timeshift.getErrorID());
          if (timeshift.getErrorID() == 7) {
            Log.d(TAG,
                "onTimeShiftPause, not create Timeshift file, "
                    + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
            getTunerInputSession().notifyTimeShiftStatusChanged(
                TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

            Intent intent = (this.mInfo).createSetupIntent();
            if (intent == null) {
              Toast.makeText(
                  (Context) (this.inputService),
                  "The input doesn\'t support setup activity",
                  Toast.LENGTH_SHORT).show();
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
      } else if (flag_Tune_RecordPlay == 1) {
        mRecordPlayView.pause();
        Log.d(TAG, "onTimeShiftPause, mRecordPlayView.pause()" + "\n");
      }
      return;
    }

    public void onTimeShiftResume() {
      int ret = 0;
      if (flag_Tune_RecordPlay == 0) {
        ret = timeshift.setPlaybackResume();
        Log.d(TAG,
            "onTimeShiftResume, timeshift.setPlaybackResume(), ret = ("
                + ret + ")\n");

        if (ret != 0) {
          Log.d(TAG, "onTimeShiftResume, timeshift.getErrorID() = "
              + timeshift.getErrorID());
          if (timeshift.getErrorID() == 7) {
            Log.d(TAG,
                "onTimeShiftResume, not create Timeshift file, "
                    + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
            getTunerInputSession().notifyTimeShiftStatusChanged(
                TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

            Intent intent = (this.mInfo).createSetupIntent();
            if (intent == null) {
              Toast.makeText(
                  (Context) (this.inputService),
                  "The input doesn\'t support setup activity",
                  Toast.LENGTH_SHORT).show();
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
      } else if (flag_Tune_RecordPlay == 1) {
        mRecordPlayView.start();
        Log.d(TAG, "onTimeShiftResume, mRecordPlayView.start()" + "\n");
      }
      return;
    }

    public void onTimeShiftSeekTo(long timeMs) {
      int ret = 0;
      if (flag_Tune_RecordPlay == 0) {
        ret = timeshift.seekTo(timeMs);
        Log.d(TAG, "onTimeShiftSeekTo, timeshift.seekTo(" + timeMs
            + "), ret = (" + ret + ")\n");
      } else if (flag_Tune_RecordPlay == 1) {
        mRecordPlayView.seekTo((int) timeMs);
        Log.d(TAG, "onTimeShiftSeekTo, mRecordPlayView.seekTo("
            + timeMs + ")\n");
      }
      return;
    }

    public void onDvrPlaybackSetPlaybackParams(PlaybackParams params) {
      float mSpeed = 0.0f;
      try {
        mSpeed = params.getSpeed();
      } catch (Exception e) {
        Log.e(TAG,
            "onDvrPlaybackSetPlaybackParams params.getSpeed Exception:");
      }
      Log.d(TAG, "onDvrPlaybackSetPlaybackParams, mSpeed(" + mSpeed
          + ")\n");

      if (mSpeed - PlaybackSpeed_SF1_2 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SF_1_2X);
      } else if (mSpeed - PlaybackSpeed_SF1_4 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SF_1_4X);
      } else if (mSpeed - PlaybackSpeed_SF1_8 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SF_1_8X);
      } else if (mSpeed - PlaybackSpeed_SF1_16 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SF_1_16X);
      } else if (mSpeed - PlaybackSpeed_SF1_32 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SF_1_32X);
      } else if (mSpeed - PlaybackSpeed_SR1_2 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SR_1_2X);
      } else if (mSpeed - PlaybackSpeed_SR1_4 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SR_1_4X);
      } else if (mSpeed - PlaybackSpeed_SR1_8 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SR_1_8X);
      } else if (mSpeed - PlaybackSpeed_SR1_16 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SR_1_16X);
      } else if (mSpeed - PlaybackSpeed_SR1_32 == 0.0f) {
        mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_SR_1_32X);
      } else {
        switch ((int) mSpeed) {
          case PlaybackSpeed_XF1:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_1X);
            break;
          case PlaybackSpeed_XF2:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_2X);
            break;
          case PlaybackSpeed_XF4:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_4X);
            break;
          case PlaybackSpeed_XF8:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_8X);
            break;
          case PlaybackSpeed_XF16:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_16X);
            break;
          case PlaybackSpeed_XF32:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_32X);
            break;
          case PlaybackSpeed_XF64:
            //mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FF_64X);
            break;
          case PlaybackSpeed_XR1:
            //mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_1X);
            break;
          case PlaybackSpeed_XR2:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_2X);
            break;
          case PlaybackSpeed_XR4:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_4X);
            break;
          case PlaybackSpeed_XR8:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_8X);
            break;
          case PlaybackSpeed_XR16:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_16X);
            break;
          case PlaybackSpeed_XR32:
            mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_32X);
            break;
          case PlaybackSpeed_XR64:
            //mRecordPlayView.setPlayMode(PlayerSpeed.SPEED_FR_64X);
            break;
          default:
            Log.e(TAG,
                "onDvrPlaybackSetPlaybackParams, mSpeed:invalid value!\n");
            return;
        }
      }
    }

    public void onTimeShiftSetPlaybackParams(PlaybackParams params) {
      float mSpeed = 0.0f;
      int ret = 0;
      if (flag_Tune_RecordPlay == 0) {
        try {
          mSpeed = params.getSpeed();
        } catch (Exception e) {
          Log.e(TAG,
              "onTimeShiftSetPlaybackParams params.getSpeed Exception:");
        }
        Log.d(TAG, "onTimeShiftSetPlaybackParams, mSpeed(" + mSpeed
            + ")\n");
        switch ((int) mSpeed) {
          case PlaybackSpeed_XF1:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_1X);
            break;
          case PlaybackSpeed_XF2:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_2X);
            break;
          case PlaybackSpeed_XF4:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_4X);
            break;
          case PlaybackSpeed_XF8:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_8X);
            break;
          case PlaybackSpeed_XF12:
          case PlaybackSpeed_XF16:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_16X);
            break;
          case PlaybackSpeed_XF32:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_32X);
            break;
          case PlaybackSpeed_XF48:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_64X);
            break;
          case PlaybackSpeed_XF128:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_FORWARD_128X);
            break;
          case PlaybackSpeed_XR1:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_1X);
            break;
          case PlaybackSpeed_XR2:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_2X);
            break;
          case PlaybackSpeed_XR4:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_4X);
            break;
          case PlaybackSpeed_XR8:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_8X);
            break;
          case PlaybackSpeed_XR12:
          case PlaybackSpeed_XR16:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_16X);
            break;
          case PlaybackSpeed_XR32:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_32X);
            break;
          case PlaybackSpeed_XR48:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_64X);
            break;
          case PlaybackSpeed_XR128:
            ret = timeshift
                .setPlaybackSpeed(TimeshiftPlaybackSpeed.TIMESHIFT_PLAY_SPEED_REWIND_128X);
            break;
          default:
            Log.e(TAG,
                "onTimeShiftSetPlaybackParams, mSpeed:invalid value!\n");
            return;
        }
        if (ret != 0) {
          Log.d(TAG,
              "onTimeShiftSetPlaybackParams, timeshift.getErrorID() = "
                  + timeshift.getErrorID());
          if (timeshift.getErrorID() == 7) {
            Log.d(TAG,
                "onTimeShiftSetPlaybackParams, not create Timeshift file, "
                    + "notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
            getTunerInputSession().notifyTimeShiftStatusChanged(
                TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);

            Intent intent = (this.mInfo).createSetupIntent();
            if (intent == null) {
              Toast.makeText(
                  (Context) (this.inputService),
                  "The input doesn\'t support setup activity",
                  Toast.LENGTH_SHORT).show();
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
      } else if (flag_Tune_RecordPlay == 1) {
        onDvrPlaybackSetPlaybackParams(params);
      }
      return;
    }

    public void setStreamVolume_RecordPlay(float volume) {
      // waiting for implement for pvr playback
      Log.d(TAG, "setStreamVolume_RecordPlay (volume =" + volume
          + "), do nothings, return");
      return;
    }

    @Override
    public void onSetStreamVolume(float volume) {
      final float mVolume = volume;
      if (flag_Tune_RecordPlay == 0) {
        Log.d(TAG,
            "onSetStreamVolume (flag_Tune_RecordPlay == 0) call super.onSetStreamVolume(volume ="
                + volume + ")");
        super.onSetStreamVolume(volume);
        return;
      } else if (flag_Tune_RecordPlay == 1) {
        // do nothings
        Log.d(TAG, "onSetStreamVolume (flag_Tune_RecordPlay == 1)"
            + ", call setStreamVolume_RecordPlay(mVolume = "
            + volume + ")");
        setStreamVolume_RecordPlay(volume);
        return;
      }

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          synchronized (tune_sync_object) {
            try {
              while (!tune_sync_condition) {
                Log.d(TAG, "onSetStreamVolume Thread "
                    + Thread.currentThread().getName()
                    + ", tune_sync_object wait");
                tune_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onSetStreamVolume Thread " + Thread.currentThread().getName()
                + ", tune_sync_object get mutex");
          }
          if (flag_Tune_RecordPlay == 0) {
            Log.d(TAG,
                "onSetStreamVolume (flag_Tune_RecordPlay == 0) call onSetStreamVolume(mVolume ="
                    + mVolume + ")");
            onSetStreamVolume(mVolume);
            return;
          } else if (flag_Tune_RecordPlay == 1) {
            // do nothings
            Log.d(TAG,
                "onSetStreamVolume (flag_Tune_RecordPlay == 1)"
                    + ", call setStreamVolume_RecordPlay(mVolume = "
                    + mVolume + ")");
            setStreamVolume_RecordPlay(mVolume);
            return;
          }
        }
      });
      t.start();
      return;
    }

    public void setCaptionEnabled_RecordPlay(boolean enabled) {
      // waiting for implement for pvr playback
      if (enabled == false) {
        mRecordPlayView.onSubtitleTrack(0);
      } else if (enabled == true) {
        mRecordPlayView.onSubtitleTrack(1);
      }
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {
      final boolean mEnabled = enabled;
      if (flag_Tune_RecordPlay == 0) {
        Log.d(TAG,
            "onSetCaptionEnabled (flag_Tune_RecordPlay == 0) call super.onSetCaptionEnabled(enabled ="
                + enabled + ")");
        super.onSetCaptionEnabled(enabled);
        return;
      } else if (flag_Tune_RecordPlay == 1) {
        // do nothings
        Log.d(TAG, "onSetCaptionEnabled (flag_Tune_RecordPlay == 1)"
            + ", call setCaptionEnabled_RecordPlay(enabled = "
            + enabled + ")");
        setCaptionEnabled_RecordPlay(enabled);
        return;
      }

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          synchronized (tune_sync_object) {
            try {
              while (!tune_sync_condition) {
                Log.d(TAG, "onSetCaptionEnabled Thread "
                    + Thread.currentThread().getName()
                    + ", tune_sync_object wait");
                tune_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onSetCaptionEnabled Thread " + Thread.currentThread().getName()
                + ", tune_sync_object get mutex");
          }
          if (flag_Tune_RecordPlay == 0) {
            Log.d(TAG,
                "onSetCaptionEnabled (flag_Tune_RecordPlay == 0) call onSetCaptionEnabled(mEnabled ="
                    + mEnabled + ")");
            onSetCaptionEnabled(mEnabled);
            return;
          } else if (flag_Tune_RecordPlay == 1) {
            // do nothings
            Log.d(TAG,
                "onSetCaptionEnabled (flag_Tune_RecordPlay == 1)"
                    + ", call setCaptionEnabled_RecordPlay(mEnabled = "
                    + mEnabled + ")");
            setCaptionEnabled_RecordPlay(mEnabled);
            return;
          }
        }
      });
      t.start();
      return;
    }

    public void setSurfaceChanged_RecordPlay(int format, int width,
        int height) {
      // waiting for implement for pvr playback
      Log.d(TAG, "setSurfaceChanged_RecordPlay (width = " + width
          + ", height = " + height + ")");
      // mRecordPlayView.setSurface(surface);
      mRecordPlayView.surfaceChanged();
      return;
    }

    @Override
    public void onSurfaceChanged(int format, int width, int height) {
      final int mFormat = format;
      final int mWidth = width;
      final int mHeight = height;
      if (flag_Tune_RecordPlay == 0) {
        Log.d(TAG,
            "onSurfaceChanged (flag_Tune_RecordPlay == 0) call super.onSurfaceChanged(width ="
                + width + ", height = " + height + ")");
        super.onSurfaceChanged(format, width, height);
        return;
      } else if (flag_Tune_RecordPlay == 1) {
        Log.d(TAG,
            "onSurfaceChanged (flag_Tune_RecordPlay == 1) call setSurfaceChanged_RecordPlay(width ="
                + width + ", height = " + height + ")");
        setSurfaceChanged_RecordPlay(format, width, height);
        return;
      }

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          synchronized (tune_sync_object) {
            try {
              while (!tune_sync_condition) {
                Log.d(TAG, "onSurfaceChanged Thread "
                    + Thread.currentThread().getName()
                    + ", tune_sync_object wait");
                tune_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onSurfaceChanged Thread " + Thread.currentThread().getName()
                + ", tune_sync_object get mutex");
          }

          synchronized (surface_sync_object) {
            try {
              while (!surface_sync_condition) {
                Log.d(TAG, "onSurfaceChanged Thread "
                    + Thread.currentThread().getName()
                    + ", surface_sync_object wait");
                surface_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onSurfaceChanged Thread " + Thread.currentThread().getName()
                + ", surface_sync_object get mutex");
          }
          if (flag_Tune_RecordPlay == 0) {
            Log.d(TAG,
                "onSurfaceChanged (flag_Tune_RecordPlay == 0) call onSurfaceChanged(mWidth ="
                    + mWidth + ", mHeight = " + mHeight
                    + ")");
            onSurfaceChanged(mFormat, mWidth, mHeight);
          } else if (flag_Tune_RecordPlay == 1) {
            // do nothings
            Log.d(TAG,
                "onSurfaceChanged (flag_Tune_RecordPlay == 1) call setSurfaceChanged_RecordPlay()");
            setSurfaceChanged_RecordPlay(mFormat, mWidth, mHeight);
          }
          return;
        }
      });
      t.start();
      return;
    }

    public boolean setSurface_RecordPlay(Surface surface) {
      // waiting for implement for pvr playback
      Log.d(TAG, "setSurface_RecordPlay (surface = " + surface + ")");
      mRecordPlayView.setSurface(surface);
      return true;
    }

    @Override
    public boolean onSetSurface(Surface surface) {
      final Surface mSurface = surface;
      if (flag_Tune_RecordPlay == 0) {
        Log.d(TAG,
            "onSetSurface (flag_Tune_RecordPlay == 0) call super.onSetSurface(surface ="
                + surface + ")");
        return super.onSetSurface(surface);
      } else if (flag_Tune_RecordPlay == 1) {
        Log.d(TAG,
            "onSetSurface (flag_Tune_RecordPlay == 1) call setSurface_RecordPlay(surface)");
        return setSurface_RecordPlay(surface);
      }

      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          synchronized (tune_sync_object) {
            try {
              while (!tune_sync_condition) {
                Log.d(TAG, "onSetSurface Thread "
                    + Thread.currentThread().getName()
                    + ", tune_sync_object wait");
                tune_sync_object.wait();
              }
            } catch (InterruptedException e) {
            }
            Log.d(TAG, "onSetSurface Thread " + Thread.currentThread().getName()
                + ", tune_sync_object get mutex");
          }

          if (flag_Tune_RecordPlay == 0) {
            Log.d(TAG,
                "onSetSurface (flag_Tune_RecordPlay == 0) call onSetSurface(mSurface ="
                    + mSurface + ")");
            onSetSurface(mSurface);
          } else if (flag_Tune_RecordPlay == 1) {
            // do nothings
            Log.d(TAG,
                "onSetSurface (flag_Tune_RecordPlay == 1) call setSurface_RecordPlay(surface)");
            setSurface_RecordPlay(mSurface);
          }

          synchronized (getSurfaceSyncObject()) {
            setSurfaceSyncCondition();
            getSurfaceSyncObject().notifyAll();
            Log.d(TAG, "onSetSurface, Thread " + Thread.currentThread().getName()
                + ", surface_sync_object notifyAll");
          }
          return;
        }
      });
      t.start();
      return true;
    }

    @Override
    public boolean onTune(Uri uri) {
      TunerInputService tunerInputService = (TunerInputService) getTunerInputService();
      List<DataSyncThread> syncThreads = tunerInputService.syncThreads;
      if (syncThreads != null) {
        for (DataSyncThread thread : syncThreads) {
          if (thread.getClass() != ChannelSelStatusThread.class) {
            continue;
          }
          thread.setMonitor(true);
        }
      }
      flag_Tune_RecordPlay = 0;
      synchronized (getTuneSyncObject()) {
        setTuneSyncCondition();
        getTuneSyncObject().notifyAll();
        Log.d(TAG, "onTune, Thread " + Thread.currentThread().getName()
            + ", tune_sync_object notifyAll");
      }
      Log.d(TAG, "onTune, Thread " + Thread.currentThread().getName()
          + ", tune_sync_object release mutex");
      // call super to switch to DTV input
      final boolean b_status = super.onTune(uri);

      Uri tmpUri = uri;
      boolean tmpfilterChannel = false;

      if (uri == null) {
        return false;
      }

      if (uri.toString().startsWith(TvContract.Channels.CONTENT_URI.toString())) {
        // do nothings
      }
      else if (uri.toString().startsWith(MtkTvTISMsgBase.SVL_CONTENT_URI.toString())) {
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

      Log.d(TAG, "filterChannel:" + filterChannel + ", tmpUri=" + tmpUri);

      /*
       * workaround for monkey test throw exception when tune exceed 2000ms
       */
      if (b_status == false) {
        Log.d(TAG,
            "b_status = false, indicated Tv is played before this tune!");
      } else {
        Log.d(TAG,
            "b_status = true, indicated Tv is played by this tune!");
      }
      // TODO Need twoworld API
      Thread t = new Thread(new Runnable() {
        @Override
        public void run() {
          MtkTvChannelInfoBase mtkTvChannelInfo = null;
          long channelId = -1;
          int ret = 0;
          MtkTvTimeshiftBase.TimeshiftRecordStatus timeshif_tstatus =
              TimeshiftRecordStatus.TIMESHIFT_RECORD_UNKNOWN;

          if (uri_1.toString().startsWith(TvContract.Channels.CONTENT_URI.toString())) {
            // Process channel url and change channel
            Channel c = getChannel(uri_1);
            if (c == null) {
              getTunerInputSession().notifyVideoUnavailable(
                  TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              Log.d(TAG, "notifyVideoUnavailable, before channelselect, invalid uri!\n");
              return;
            }
            Log.d(TAG, c.toString());
            mtkTvChannelInfo = getMtkChannelinfo(c);
          }
          else if (uri_1.toString().startsWith(MtkTvTISMsgBase.SVL_CONTENT_URI.toString())) {
            channelId = MtkTvTISMsgBase.parseSvlChannelId(uri_1);
          }

          if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
            if (timeshift != null) {
              timeshif_tstatus = timeshift.getRecordStatus();
              if (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) {
                Log.d(TAG,
                    "onTune, (timeshif_tstatus == MtkTvTimeshiftBase.TIMESHIFT_RECORD_STARTED) "
                        + "stop rec\n");
                timeshift
                    .stop(TimeshiftStopFlag.TIMESHIFT_STOP_RECORD);
              }
            }
          }
          boolean b_ts_rec = (timeshif_tstatus == TimeshiftRecordStatus.TIMESHIFT_RECORD_STARTED) ? true
              : false;

          if (b_status == true) {
            synchronized (sync_object) {
              try {
                while (!sync_condition) {
                  Log.d(TAG, "Thread "
                      + Thread.currentThread().getName()
                      + ", sync_object wait");
                  sync_object.wait();
                }
              } catch (InterruptedException e) {
              }
              Log.d(TAG, "Thread "
                  + Thread.currentThread().getName()
                  + ", sync_object get mutex");
            }
          }
          if (b_ts_rec == true) {
            synchronized (ts_sync_object) {
              try {
                while (!ts_sync_condition) {
                  Log.d(TAG, "Thread "
                      + Thread.currentThread().getName()
                      + ", ts_sync_object wait");
                  ts_sync_object.wait();
                }
              } catch (InterruptedException e) {
              }
              Log.d(TAG, "Thread "
                  + Thread.currentThread().getName()
                  + ", ts_sync_object get mutex");
            }
          }

          if (mtkTvChannelInfo != null) {
            MtkTvMultiView mview = MtkTvMultiView.getInstance();
            int focus = mview.getPOPTunerFocus();
            if (focus == -1) {
              Log.d(TAG, "call broadcastControl.channelSelect ");
              ret = filterChannel ? 0 : broadcastControl
                  .channelSelect(
                      mtkTvChannelInfo,
                      ((isBarkerChannel == MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE) ? false
                          : true));
            } else if (focus == 0) {
              Log.d(TAG, "call broadcastControl.channelSelect(main) ");
              ret = filterChannel ? 0 : broadcastControl
                  .channelSelect(
                      mtkTvChannelInfo,
                      ((isBarkerChannel == MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE) ? false
                          : true), 0);
            } else if (focus == 1) {
              Log.d(TAG, "call broadcastControl.channelSelect(sub)");
              ret = filterChannel ? 0 : broadcastControl
                  .channelSelect(
                      mtkTvChannelInfo,
                      ((isBarkerChannel == MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE) ? false
                          : true), 1);
            }
          } else if (channelId != -1) {
            ret = broadcastControl.channelSelect((int) channelId,
                ((isBarkerChannel == MtkTvTISMsgBase.MTK_TIS_VALUE_FALSE) ? false : true));
          } else {
            Log.d(TAG, "mtkTvChannelInfo is null,return");
            getTunerInputSession().notifyVideoUnavailable(
                TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
            return;
          }

          Log.d(TAG, "channelSelect return ret:" + ret);

          if (ret == 0) {
            getTunerInputSession().notifyVideoUnavailable(
                TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);
            // getTunerInputSession().notifyChannelRetuned(uri_1);
            if (SystemProperties.get("sys.mtk.tif.timeshift").equals("1")) {
              Log.d(TAG, "onTune, channelSelect return ret:" + ret
                  + ", b_TS_switchChannel = true");
              b_TS_switchChannel = true;
              Log.d(TAG,
                  "onTune, notifyTimeShiftStatusChanged(TIME_SHIFT_STATUS_UNAVAILABLE)\n");
              getTunerInputSession().notifyTimeShiftStatusChanged(
                  TvInputManager.TIME_SHIFT_STATUS_UNAVAILABLE);
            }
          }else {
                getTunerInputSession().notifyVideoUnavailable(
                    TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
          }           

          TunerInputService tunerInputService = (TunerInputService) getTunerInputService();
          List<DataSyncThread> syncThreads = tunerInputService.syncThreads;
          if (syncThreads != null) {
            for (DataSyncThread thread : syncThreads) {
              if (thread.getClass() != ProgramSyncThread.class) {
                continue;
              }

              if (mtkTvChannelInfo != null) {
                ((ProgramSyncThread) thread).notifyChannel(mtkTvChannelInfo);
              }
              else if (channelId != -1) {
                ((ProgramSyncThread) thread).notifyChannel((int) channelId);
              }
            }
          }
        }
      });
      t.start();
      return true;
    }

    // TODO Need twoworld API

  }

}
