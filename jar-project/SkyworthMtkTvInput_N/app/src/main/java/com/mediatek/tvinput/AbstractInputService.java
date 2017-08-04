
package com.mediatek.tvinput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Canvas;
import android.hardware.hdmi.HdmiControlManager;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.ITvInputManager;
import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.HandlerThread;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import com.android.internal.os.SomeArgs;

import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.MtkTvMultiView;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvAnalogCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvSubtitleBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvISDBCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvRecord;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvIntent;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvTISMsgBase;
import com.mediatek.twoworlds.tv.model.MtkTvBookingBase;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordNotifyMsgType;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordStopReason;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordSrcType;
import com.mediatek.twoworlds.tv.MtkTvRecordBase.RecordBaseErrorID;
import com.mediatek.twoworlds.tv.MtkTvPvrBrowserBase;
import com.mediatek.twoworlds.tv.model.MtkTvPvrBrowserItemBase;

/**
 * Abstract Input service
 */
public abstract class AbstractInputService extends TvInputService {
  public static final int INVALID_BOUND = -1;
  public String TAG = "MtkTvInput";
  private static final boolean DEBUG = false;
  public static final String DtvChannelInputId = "com.mediatek.tvinput/.dtv.TunerInputService/HW0";
  // protected ITvInputHardware tvInputHardware;

  /**
   * The subclass Class,used to build channel in content provider
   */
  protected Class<?> clazz;

  /**
   * Reference PhysicalTvInputManager
   */
  protected TvInputManager tvInputManager = null;

  private AbstractRutineTaskThread signalThread = null;

  public ContentResolver contentResolver;

  private Timer signal_timer;// schedule time to create signal check thread ;
  /**
   * ITvInputManager service
   */
  protected ITvInputManager inputManagerService;

  /**
   * Available Main device id list for this type filter
   */
  protected List<Integer> availInputDeviceIds = new ArrayList<Integer>();

  /**
   * Maintain physical devices info for main and sub
   */
  protected int[] hardwareDeviceId = new int[TvInputConst.InputMax];

  /**
   * Key:hardwareDeviceId <BR>
   * Value: SourceId
   */
  public Map<Integer, Integer> deviceIdMapSourceId = new HashMap<Integer, Integer>();

  /**
   * Total Main device count
   */
  protected int totalMainDeviceLen;

  /**
   * Define device type filter for PhysicalTvInputManager
   */
  protected int deviceFilter = INVALID_BOUND;// initial invalid

  /**
   * Device is zero base,e.g. 0-input1 1-input2
   */
  // protected int deviceNumber = INVALID_BOUND;// initial invalid

  protected boolean available = false;

  /* protected String inputId; */

  public ResolveInfo resolveInfo;

  /* Receiver for input service */
  protected TVInputReceiver tvInputReceiver = null;

  // inputId -> TvInputInfo
  protected final Map<String, TvInputInfo> mInputMap = new HashMap<String, TvInputInfo>();

  /**
   * Key:hardwareDeviceId <BR>
   * Value: TvInputInfo
   */
  protected Map<Integer, TvInputInfo> deviceIdMap = new HashMap<Integer, TvInputInfo>();

  // /**
  // * Key: hardwareDeviceId,For HDMI<BR>
  // * Value: TvInputInfo
  // */
  // protected Map<Integer, TvInputInfo> hdmiDeviceIdMap = new HashMap<Integer, TvInputInfo>();

  protected int platDeviceMainTotalNumber = 1;// for device filter,total number of this filter,main
                                              // only

  protected int platDeviceMainIndex = 1;// 1 base,used for support 2 channel of Composite

  protected MtkTvAnalogCloseCaption analogCCEnable = MtkTvAnalogCloseCaption.getInstance();
  protected MtkTvATSCCloseCaption digitalCCEnable = MtkTvATSCCloseCaption.getInstance();
  protected MtkTvISDBCloseCaption ISDBDigitalCCEnable = MtkTvISDBCloseCaption.getInstance();

  protected final MtkTvInputSource input = MtkTvInputSource.getInstance();

  private static final TvStreamConfig[] EMPTY_STREAM_CONFIGS = {};

  private TvStreamConfig[] mStreamConfigs = EMPTY_STREAM_CONFIGS;
  public final ServiceHandler mHandler = new ServiceHandler();
  // inputId -> TvInputService.Session List
  public final Map<String, List<AbstractInputSession>> mSessionMap =
      new HashMap<String, List<AbstractInputSession>>();
  // TvInputService.Session List for one TIS
  public List<AbstractInputSession> SessionList = new ArrayList<AbstractInputSession>();

  // inputId -> TvInputService.RecordingSession List
  public final Map<String, List<AbstractRecordingSession>> mRecordSessionMap =
      new HashMap<String, List<AbstractRecordingSession>>();
  // TvInputService.RecordingSession List for one TIS
  public List<AbstractRecordingSession> RecordSessionList = new ArrayList<AbstractRecordingSession>();

  // for mutual exclusion access. mSessionMap\SessionList
  public final Object mLock = new Object();

  // for mutual exclusion access. mRecordSessionMap\RecordSessionList
  public final Object mLock_record = new Object();

  public ITvInputManager getService() {
    return inputManagerService;
  }

  public TVInputReceiver getTVInputReceiver() {
    return tvInputReceiver;
  }

  public void setTVInputReceiver(TVInputReceiver receiver) {
    tvInputReceiver = receiver;
  }

  public AbstractInputService() {
    super();
    inputManagerService = ITvInputManager.Stub.asInterface(ServiceManager
        .getService(Context.TV_INPUT_SERVICE));
  }

  public TvInputManager getTvInputManager() {
    return tvInputManager;
  }

  class SiganalThreadTask extends TimerTask {
    @Override
    public void run() {
      // check TvRemoteService is ready
      boolean tvRemoteServiceOK = false;
      tvRemoteServiceOK = (SystemProperties.get("sys.mtk.tvremoteservice.ready").equals("1"));
      Log.d(TAG, "tvRemoteServiceOK=" + tvRemoteServiceOK);

      if (tvRemoteServiceOK) {
        signalThread = new InputSignalRutineTaskThread("InputSignalRutineTaskThread",
            contentResolver, AbstractInputService.this);
        signalThread.start();
        Log.d(TAG, "Start thread " + signalThread.getName());

        signal_timer.cancel();
        Log.d(TAG, "signal_timer.cancel()");
      }
    }
  }

  /**
   * when client bind this service,<BR>
   * InputManager will create new instance of this object
   */
  @Override
  public void onCreate() {
    super.onCreate();
    contentResolver = getContentResolver();

    Log.d(TAG, "onCreate filter=" + deviceFilter + TvInputConst.getInputName(deviceFilter) //
        + " Device config info=" + platDeviceMainIndex + "/" + platDeviceMainTotalNumber);

    tvInputManager = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);

    resolveInfo = getPackageManager().resolveService(//
        new Intent(SERVICE_INTERFACE).setClass(this, getClass()),//
        PackageManager.GET_SERVICES | PackageManager.GET_META_DATA);
    if (deviceFilter != TvInputConst.TV_INPUT_TYPE_BUILD_IN_TUNER)
    {
      signal_timer = new Timer();
      signal_timer.schedule(new SiganalThreadTask(), 0, 100);
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "########onDestroy " + this.toString());
    if (signalThread != null) {
      signalThread.setStop(true);
    }
  }

  @Override
  public TvInputInfo onHardwareAdded(TvInputHardwareInfo hardwareInfo) {
    if (hardwareInfo.getType() != this.deviceFilter) {
      return null;
    }

    Log.d(TAG, "onHardwareAdded filter info=> type=" + TvInputConst.getInputName(deviceFilter));
    Log.d(TAG,
        "onHardwareAdded: " + hardwareInfo.toString() + "\t resolveInfo" + resolveInfo.toString());

    if (platDeviceMainIndex > platDeviceMainTotalNumber) {
      Log.d(TAG, "onHardwareAdded: Invalid Device platDeviceMainIndex=" //
          + platDeviceMainIndex + " platDeviceMainTotalNumber=" + platDeviceMainTotalNumber);
      return null;
    }
    int deviceId = hardwareInfo.getDeviceId();
    availInputDeviceIds.add(deviceId);

    Collections.sort(availInputDeviceIds);// main < sub
    if (TvInputConst.DEBUG) {
      for (int sortedDeviceId : availInputDeviceIds) {
        Log.d(TAG, "onHardwareAdded sorted device id:" + sortedDeviceId);
      }
    }

    if (deviceIdMap.containsKey(deviceId)) {
      Log.e(TAG, "Already created TvInputInfo for deviceId=" + deviceId);
      return null;
    }

    TvInputInfo info = null;
    try {
      Log.d(TAG, "resolveInfo:" + resolveInfo.toString());
      if (this.deviceFilter == TvInputConst.TV_INPUT_TYPE_HDMI) {
        info = TvInputInfo.createTvInputInfo(this, resolveInfo, hardwareInfo,//
            "HDMI " + (hardwareInfo.getHdmiPortId()), null);

      } else {
        StringBuffer dispName = new StringBuffer(clazz.getSimpleName().replace("InputService", ""));
        // get last later
        if (Character.isDigit(dispName.charAt(dispName.length() - 1))) {// e.g.
                                                                        // composite2=>composite 2
          dispName.insert(dispName.length() - 1, " ");
        }
        info = TvInputInfo.createTvInputInfo(this, resolveInfo, hardwareInfo, dispName.toString(),
            null);
      }
      deviceIdMap.put(deviceId, info);// collect device id and TvInputInfo into map.
      mInputMap.put(info.getId(), info);
      Log.d(TAG, "Created TvInputInfo for deviceId=" + deviceId + "\t info=" + info.getId() + "\t"
          + info.toString());

    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Mean all device of this filter have notified
    if (availInputDeviceIds.size() == platDeviceMainTotalNumber * 2/* main+sub */) {
      int index = platDeviceMainIndex - 1;// convert 1 based to 0 based ;
      hardwareDeviceId[TvInputConst.InputMain] = availInputDeviceIds.get(index);
      hardwareDeviceId[TvInputConst.InputSub] = availInputDeviceIds.get(index
          + (platDeviceMainTotalNumber));
      {
        deviceIdMapSourceId.put(hardwareDeviceId[TvInputConst.InputMain],
            hardwareDeviceId[TvInputConst.InputMain]);
        deviceIdMapSourceId.put(hardwareDeviceId[TvInputConst.InputSub],
            hardwareDeviceId[TvInputConst.InputMain]);
      }
      Log.d(TAG, "main device id =" + hardwareDeviceId[TvInputConst.InputMain] //
          + " sub device id =" + hardwareDeviceId[TvInputConst.InputSub] //
          + " type=" + TvInputConst.getInputName(deviceFilter)//
      );

      info = deviceIdMap.get(hardwareDeviceId[TvInputConst.InputMain]);
      /* inputId = info.getId(); */// save inputid for insert to tv.db

      return info;
    }

    return null;
  }

  @Override
  public String onHardwareRemoved(TvInputHardwareInfo hardwareInfo) {
    int deviceId = hardwareInfo.getDeviceId();
    TvInputInfo info = deviceIdMap.get(deviceId);
    if (info == null) {
      return null;
    }
    deviceIdMap.remove(deviceId);
    mInputMap.remove(info.getId());
    Log.d(TAG, "onHardwareRemoved id =" + info.getId() + "\t" + info.toString());
    return info.getId();
  }

  /*
   * protected boolean hasChannel() { return Utils.hasChannel(this, getContentResolver(), inputId);
   * } protected void clearInputChannel(Class<?> clazz) { String selection =
   * TvContract.Channels.COLUMN_INPUT_ID + " = ?"; String[] selectionArgs = null; ContentResolver
   * contentResolver = getContentResolver(); selectionArgs = new String[] { inputId };
   * contentResolver.delete(TvContract.Channels.CONTENT_URI, selection, selectionArgs); } protected
   * void buildInputChannel() { String serviceName = clazz.getName(); Log.d(TAG,
   * "buildInputChannel for service:" + serviceName); Log.d(TAG, "inputId:" + inputId); String
   * dispName = clazz.getSimpleName().replace("InputService", ""); String dispNumber = "" +
   * platDeviceMainIndex; Channel c = new Channel.Builder()// .setInputId(inputId)//
   * .setDisplayNumber(dispNumber)// .setDisplayName(dispName)// .build();
   * getContentResolver().insert(TvContract.Channels.CONTENT_URI, c.toContentValues()); }
   */
  private boolean checkSessionexist()
  {
    synchronized (mLock) {
      if (SessionList.size() == 0)
      {
        Log.d(TAG, "checkSessionexist SessionList.size() == 0");
        return false;
      }
      else
      {
        Log.d(TAG, "checkSessionexist SessionList.size() != 0");
        return true;
      }
    }
  }

  private List<AbstractInputSession> getSession() {
    synchronized (mLock) {
      if (SessionList.size() == 0)
      {
        Log.d(TAG, "getSession SessionList.size() == 0, return null");
        return null;
      }
    }
    Log.d(TAG, "getSession SessionList.size() != 0, return SessionList");
    return SessionList;
  }

  private List<AbstractInputSession> getSession(String inputId) {
    synchronized (mLock) {
      if (mSessionMap.size() == 0)
      {
        Log.d(TAG, "getSession mSessionMap.size() == 0");
        return null;
      }
      else
      {
        Iterator<Map.Entry<String, List<AbstractInputSession>>> iter = mSessionMap.entrySet()
            .iterator();
        while (iter.hasNext())
        {
          Map.Entry<String, List<AbstractInputSession>> entry = iter.next();
          Log.d(TAG, "getSession mSessionMap(" + entry.getKey() + ")");
          List<AbstractInputSession> session_list = entry.getValue();
          for (AbstractInputSession session : session_list) {
            Log.d(TAG, "getSession mSessionMap(" + session + ")" + "session.Session_idx = "
                + session.getSession_idx());
          }
        }
      }
    }
    return mSessionMap.get(inputId);
  }

  public int getCurrentChannelId() {
    int chId = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_NAV_AIR_CRNT_CH);
    return chId;
  }

  public class AbstractRecordingSession extends TvInputService.RecordingSession {
    protected String TAG = "AbsRecordingSession";
    protected AbstractInputService inputService = null;
    protected final TvInputInfo mInfo;
    private TvInputManager mManager = null;
    private ITvInputManager service = null;
    protected final int mHardwareDeviceId;
    private int Session_idx = 0;
    private boolean flag_dtvchannel = false;
    private boolean flag_Book_RecordingSession = false;
    // for mutual exclusion access. flag_Book_RecordingSession
    private final Object mLock_flag_Booking = new Object();

    private boolean flag_Recording = false;
    // for mutual exclusion access. flag_Recording
    private final Object mLock_flag_Recording = new Object();

    public Uri mChannelUri = null;
    public boolean videoAvailable = false;
    private int tune_status = -1; // 0:tuning ; 1:tuned
    private Program recordProgram = null;
    private String PvrFileName = null;

    protected AbstractRecordingSession(AbstractInputService mtkTvCommonInputService,
        TvInputInfo info, int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService);
      this.inputService = mtkTvCommonInputService;
      this.mInfo = info;
      this.mManager = inputService.tvInputManager;

      this.service = ITvInputManager.Stub.asInterface(ServiceManager
          .getService(Context.TV_INPUT_SERVICE));

      this.Session_idx = index;
      this.mHardwareDeviceId = HardwareDeviceId;
    }

    public int getSession_idx() {
      return this.Session_idx;
    }

    public boolean getDtvChannelFlag() {
      return flag_dtvchannel;
    }

    public boolean getBookFlag() {
      synchronized (mLock_flag_Booking) {
        return flag_Book_RecordingSession;
      }
    }

    public void setBookFlag() {
      synchronized (mLock_flag_Booking) {
        flag_Book_RecordingSession = true;
      }
    }

    public void resetBookFlag() {
      synchronized (mLock_flag_Booking) {
        flag_Book_RecordingSession = false;
      }
    }

    public boolean getRecordingFlag() {
      synchronized (mLock_flag_Recording) {
        return flag_Recording;
      }
    }

    public void setRecordingFlag() {
      synchronized (mLock_flag_Recording) {
        flag_Recording = true;
      }
    }

    public void resetRecordingFlag() {
      synchronized (mLock_flag_Recording) {
        flag_Recording = false;
      }
    }

    public void setRecordingFileName(String PvrFileName) {
      this.PvrFileName = PvrFileName;
    }

    public String getRecordingFileName() {
      return PvrFileName;
    }

    public void setRecordingSessionTunedResult(boolean mVideoAvailable) {
      Log.d(TAG,
          "setRecordingSessionTunedResult(videoAvailable=" + mVideoAvailable + ")");
      videoAvailable = mVideoAvailable;
      if (this.mChannelUri == null) {
        Log.d(TAG,
            "setRecordingSessionTunedResult(videoAvailable=" + mVideoAvailable + ")"
                + ", this.mChannelUri == null, return");
        return;
      }
      if ((videoAvailable) && (tune_status == 0)) {
        Log.d(TAG,
            "setRecordingSessionTunedResult(videoAvailable=true), notifyTuned, mChannelUri = "
                + mChannelUri.toString() + ", flag_dtvchannel = " + flag_dtvchannel);
        notifyTuned(this.mChannelUri);
        tune_status = 1;
      }
    }
    /**
     * Called when the application requests to tune to a given channel for TV program recording.
     * Override this method in order to handle domain-specific features that are only known between
     * certain TV inputs and their clients.
     * <p>
     * The application may call this method before starting or after stopping recording, but not
     * during recording. The default implementation calls {@link #onTune(Uri)}.
     * <p>
     * The session must call {@link #notifyTuned(Uri)} if the tune request was fulfilled, or
     * {@link #notifyError(int)} otherwise.
     *
     * @param channelUri The URI of a channel.
     * @param params Domain-specific data for this tune request. Keys <em>must</em> be a scoped
     *          name, i.e. prefixed with a package name you own, so that different developers will
     *          not create conflicting keys.
     */
    public void onTune(Uri channelUri, Bundle params) {
      Log.d(TAG,
          "onTune, channelUri = " + channelUri.toString());
      tune_status = 0;
      if (params != null) {
        Log.d(TAG,
            "onTune, channelUri = " + channelUri.toString() + ", params = " + params.toString());
        String pvrMP = params.getString("PvrMp");
        MtkTvRecord.getInstance().setDisk(pvrMP);
      }
      if (TextUtils.equals(DtvChannelInputId, getChannelInputId(channelUri))) {
        // if (DtvChannelInputId == getChannelInputId(channelUri)) {
        flag_dtvchannel = true;
      } else {
        flag_dtvchannel = false;
      }
      this.mChannelUri = channelUri;
      if ((videoAvailable) && (tune_status == 0)) {
        Log.d(TAG,
            "onTune(videoAvailable=true), notifyTuned, mChannelUri = "
                + mChannelUri.toString() + ", flag_dtvchannel = " + flag_dtvchannel);
        notifyTuned(this.mChannelUri);
        tune_status = 1;
      }
    }

    /**
     * Called when the application requests to tune to a given channel for TV program recording.
     * <p>
     * The application may call this method before starting or after stopping recording, but not
     * during recording.
     * <p>
     * The session must call {@link #notifyTuned(Uri)} if the tune request was fulfilled, or
     * {@link #notifyError(int)} otherwise.
     *
     * @param channelUri The URI of a channel.
     */
    public void onTune(Uri channelUri) {
      Log.d(TAG, "onTune, channelUri = " + channelUri.toString());
      tune_status = 0;
      if (TextUtils.equals(DtvChannelInputId, getChannelInputId(channelUri))) {
        // if (DtvChannelInputId == getChannelInputId(channelUri)) {
        flag_dtvchannel = true;
      } else {
        flag_dtvchannel = false;
      }
      this.mChannelUri = channelUri;
      if ((videoAvailable) && (tune_status == 0)) {
        Log.d(TAG,
            "onTune(videoAvailable=true), notifyTuned, mChannelUri = "
                + mChannelUri.toString() + ", flag_dtvchannel = " + flag_dtvchannel);
        notifyTuned(this.mChannelUri);
        tune_status = 1;
      }
    }

    public String getChannelInputId(Uri channelUri) {
      String[] projection = {//
          TvContract.Channels._ID,// 0
          TvContract.Channels.COLUMN_INPUT_ID,// 1
      };
      if (channelUri == null) {
        return null;
      }

      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(channelUri, projection, null, null, null);
      if (cursor == null) {
        return null;
      }
      if (cursor.getCount() < 1) {
        cursor.close();
        return null;
      }

      cursor.moveToFirst();

      long id = cursor.getInt(0);
      String channelInputId = cursor.getString(1);

      cursor.close();
      return channelInputId;
    }

    public Channel getChannel(Uri channelUri) {
      String[] projection = {//
          TvContract.Channels._ID,// 0
          TvContract.Channels.COLUMN_DISPLAY_NUMBER,// 1
          TvContract.Channels.COLUMN_DISPLAY_NAME,// 2
          TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,// 3
      };
      if (channelUri == null) {
        return null;
      }

      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(channelUri, projection, null, null, null);
      if (cursor == null) {
        return null;
      }
      if (cursor.getCount() < 1) {
        cursor.close();
        return null;
      }

      cursor.moveToFirst();

      long id = cursor.getInt(0);
      int displayNumber = cursor.getInt(1);
      String displayName = cursor.getString(2);
      byte[] data = cursor.getBlob(3);

      // cursor.moveToNext();
      cursor.close();
      return new Channel.Builder()//
          .setId(id)//
          .setDisplayNumber(String.valueOf(displayNumber))//
          .setDisplayName(displayName)//
          .setData(data)//
          .build();
    }

    public Program getProgram(Uri programUri) {
      String[] projection = {//
          TvContract.Programs._ID,// 0
          TvContract.Programs.COLUMN_CHANNEL_ID,
          TvContract.Programs.COLUMN_TITLE,
          TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS,
          TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS,
      };
      if (programUri == null) {
        return null;
      }

      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(programUri, projection, null, null, null);
      if (cursor == null) {
        return null;
      }
      if (cursor.getCount() < 1) {
        cursor.close();
        return null;
      }

      cursor.moveToFirst();

      long id = cursor.getLong(1);
      String programName = cursor.getString(2);
      long StartTime = cursor.getLong(3);
      long EndTime = cursor.getLong(4);

      // cursor.moveToNext();
      cursor.close();
      return new Program.Builder()//
          .setChannelId(id)//
          .setTitle(String.valueOf(programName))//
          .setStartTimeUtcMillis(StartTime)//
          .setEndTimeUtcMillis(EndTime)//
          .build();
    }

    /**
     * Called when the application requests to start TV program recording. Recording must start
     * immediately when this method is called.
     * <p>
     * The application may supply the URI for a TV program for filling in program specific data
     * fields in the {@link android.media.tv.TvContract.RecordedPrograms} table. A non-null
     * {@code programUri} implies the started recording should be of that specific program, whereas
     * null {@code programUri} does not impose such a requirement and the recording can span across
     * multiple TV programs. In either case, the application must call
     * {@link TvRecordingClient#stopRecording()} to stop the recording.
     * <p>
     * The session must call {@link #notifyError(int)} if the start request cannot be fulfilled.
     *
     * @param programUri The URI for the TV program to record, built by
     *          {@link TvContract#buildProgramUri(long)}. Can be {@code null}.
     */
    public void onStartRecording(Uri programUri) {
      if (programUri != null) {
        Log.d(TAG, "onStartRecording, programUri = " + programUri.toString());
      }
      recordProgram = getProgram(programUri);
      long currentTime = System.currentTimeMillis();
      long recordProgram_startTime;
      long recordProgram_endTime;
      MtkTvBookingBase item = new MtkTvBookingBase();
      SomeArgs args = SomeArgs.obtain();
      int ret = -1;
      if (programUri != null) {
        Log.d(TAG, "onStartRecording, programUri = " + programUri.toString() + ", currentTime = "
            + currentTime);
      } else {
        Log.d(TAG, "onStartRecording, currentTime = " + currentTime);
      }
      if (recordProgram == null) {
        Log.d(TAG, "onStartRecording " + ", (recordProgram == null)"
            + ", flag_dtvchannel = " + flag_dtvchannel);

        Log.d(TAG, "onStartRecording, send DO_START_RECORDING");
        args.arg1 = this;
        if (flag_dtvchannel) {
          args.argi1 = 1;
        } else {
          args.argi1 = 0;
        }
        mHandler.obtainMessage(ServiceHandler.DO_START_RECORDING, args).sendToTarget();
      } else {
        if (ContentUris.parseId(mChannelUri) != recordProgram.getChannelId()) {
          Log.e(TAG,
              "onStartRecording, the channel is of programUri is different with current Channel Uri");
          Log.d(TAG, "onStartRecording, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
          notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
          return;
        }

        recordProgram_startTime = recordProgram.getStartTimeUtcMillis();
        recordProgram_endTime = recordProgram.getEndTimeUtcMillis();

        Log.d(TAG, "onStartRecording, programUri = " + programUri.toString()
            + ", recordProgram_startTime = "
            + recordProgram_startTime + ", recordProgram_endTime = " + recordProgram_endTime);

        if ((recordProgram_startTime <= currentTime) &&
            (currentTime <= recordProgram_endTime)) {
          Log.d(TAG, "onStartRecording, programUri = " + programUri.toString()
              + ", (currentTime during between program start and end time)"
              + ", flag_dtvchannel = " + flag_dtvchannel);

          Log.d(TAG, "onStartRecording, send DO_START_RECORDING");
          args.arg1 = this;
          if (flag_dtvchannel) {
            args.argi1 = 1;
          } else {
            args.argi1 = 0;
          }
          mHandler.obtainMessage(ServiceHandler.DO_START_RECORDING, args).sendToTarget();
        } else if (recordProgram_startTime > currentTime) {
          Log.d(TAG, "onStartRecording, programUri = " + programUri.toString()
              + ", (currentTime less than program start and end time)"
              + ", flag_dtvchannel = " + flag_dtvchannel);
          item.setDeviceIndex(1);// workaround for multi module booking
          item.setChannelId(getCurrentChannelId());
          item.setRecordStartTime(recordProgram_startTime);
          item.setRecordDuration(recordProgram_endTime - recordProgram_startTime);
          item.setRecordMode(2);// RECD
          if (flag_dtvchannel) {
            item.setSourceType(2);// RECORD_PVR_SRC_TYPE_DTV
          } else {
            item.setSourceType(1);// RECORD_PVR_SRC_TYPE_ATV
          }

          Log.d(TAG, "onStartRecording, send DO_ADD_BOOKING");
          args.arg1 = this;
          args.arg2 = item;
          mHandler.obtainMessage(ServiceHandler.DO_ADD_BOOKING, args).sendToTarget();
        } else if ((recordProgram_startTime <= currentTime) &&
            (recordProgram_endTime <= currentTime)) {
          Log.e(TAG, "onStartRecording, exception case: programUri = " + programUri.toString()
              + ", (currentTime more than program start and end time)"
              + ", flag_dtvchannel = " + flag_dtvchannel);

          Log.d(TAG, "onStartRecording, send DO_START_RECORDING");
          args.arg1 = this;
          if (flag_dtvchannel) {
            args.argi1 = 1;
          } else {
            args.argi1 = 0;
          }
          mHandler.obtainMessage(ServiceHandler.DO_START_RECORDING, args).sendToTarget();
        }
      }
    }

    /**
     * Called when the application requests to stop TV program recording. Recording must stop
     * immediately when this method is called.
     * <p>
     * The session must create a new data entry in the
     * {@link android.media.tv.TvContract.RecordedPrograms} table that describes the newly recorded
     * program and call {@link #notifyRecordingStopped(Uri)} with the URI to that entry. If the stop
     * request cannot be fulfilled, the session must call {@link #notifyError(int)}.
     */
    public void onStopRecording() {
      SomeArgs args = SomeArgs.obtain();
      Log.d(TAG, "onStopRecording, send DO_STOP_RECORDING");
      args.arg1 = this;
      mHandler.obtainMessage(ServiceHandler.DO_STOP_RECORDING, args).sendToTarget();

      /*
       * flag_Recording = false; MtkTvPvrBrowserBase base = new MtkTvPvrBrowserBase();
       * MtkTvPvrBrowserItemBase recordItem = null; if (PvrFileName != null) { recordItem =
       * base.getPvrBrowserItemByPath(PvrFileName); } else { Log.d(TAG,
       * "onStopRecording, PvrFileName is null!"); Log.d(TAG,
       * "onStopRecording, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
       * notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN); }
       */

      return;
      /*
       * ContentValues values = new ContentValues(); String inputId =
       * getChannelInputId(mChannelUri); long channelId = ContentUris.parseId(mChannelUri);
       * values.put(TvContract.RecordedPrograms.COLUMN_INPUT_ID, inputId);
       * values.put(TvContract.RecordedPrograms.COLUMN_CHANNEL_ID, channelId);
       * values.put(TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI,
       * ContentResolver.SCHEME_FILE + "://" + recordItem.mPath);
       * values.put(TvContract.RecordedPrograms.COLUMN_TITLE, recordItem.mChannelName);
       * values.put(TvContract.RecordedPrograms.COLUMN_SHORT_DESCRIPTION, recordItem.mProgramName);
       * values.put(TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS,
       * recordItem.mStartTime); values.put(TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS,
       * recordItem.mEndTime); values
       * .put(TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS, recordItem.mDuration);
       * ContentResolver contentResolver = inputService.getContentResolver();
       * contentResolver.insert(TvContract.RecordedPrograms.CONTENT_URI, values); Uri
       * recordedProgramUri = getRecordedProgramUri(inputId, channelId, recordItem.mStartTime,
       * recordItem.mEndTime); if (recordedProgramUri != null) { Log.d(TAG,
       * "onStopRecording, notifyRecordingStopped recordedProgramUri = " + recordedProgramUri);
       * notifyRecordingStopped(recordedProgramUri); } return;
       */
    }

    public Uri getRecordedProgramUri(String inputId, long channelId, long startTime, long endTime) {
      Uri recordedProgramUri = null;
      int _id = -1;

      String[] projection = {
          TvContract.RecordedPrograms._ID, // 0
          TvContract.RecordedPrograms.COLUMN_INPUT_ID,
          TvContract.RecordedPrograms.COLUMN_CHANNEL_ID,
          TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS,
          TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS
      };
      String selection = TvContract.RecordedPrograms.COLUMN_INPUT_ID + " = ? AND ";
      selection += TvContract.RecordedPrograms.COLUMN_CHANNEL_ID + " = ? AND ";
      selection += TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS + " = ? AND ";
      selection += TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS + " = ?";

      String[] selectionArgs = {
          inputId, Long.toString(channelId), Long.toString(startTime), Long.toString(endTime)
      };

      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(TvContract.RecordedPrograms.CONTENT_URI, projection,
          selection, selectionArgs, null);
      if (cursor != null && cursor.getCount() > 0) {
        Log.d(TAG, "cursor count " + cursor.getCount());
        cursor.moveToFirst();
        do {
          int index = -1;
          index = cursor.getColumnIndex(TvContract.RecordedPrograms._ID);
          if (index >= 0) {
            _id = cursor.getInt(index);
            Log.d(TAG, " index _id = " + _id);
            break;
          }
        } while (cursor.moveToNext());
      }
      cursor.close();
      if (_id != -1) {
        recordedProgramUri = TvContract.buildRecordedProgramUri(_id);
      }

      return recordedProgramUri;
    }

    /**
     * Called when the application requests to release all the resources held by this recording
     * session.
     */
    public void onRelease() {
      flag_dtvchannel = false;
      resetBookFlag();
      resetRecordingFlag();
      mChannelUri = null;
      PvrFileName = null;
      videoAvailable = false;
      tune_status = -1;
      SomeArgs args_another = SomeArgs.obtain();

      args_another.arg1 = mInfo;
      args_another.argi1 = Session_idx;
      args_another.arg2 = this;
      mHandler.obtainMessage(ServiceHandler.DO_REMOVE_RECORDSESSION, args_another).sendToTarget();
      Log.d(TAG, "onRelease() send DO_REMOVE_RECORDSESSION message");
    }
  }

  @SuppressWarnings("unused")
  public class AbstractInputSession extends TvInputService.Session {
    protected String TAG = "AbstractInputSession";
    protected AbstractInputService inputService = null;
    protected final TvInputInfo mInfo;
    private TvInputManager mManager = null;
    private ITvInputManager service = null;
    // private MyHardwareCallBack[] hardwareCallback = new
    // MyHardwareCallBack[TvInputConst.InputMax];
    // private TvStreamConfig[] tvStreamConfigMain = null;
    // private TvStreamConfig[] tvStreamConfigSub = null;
    private TvStreamConfig[] mStreamConfigs = EMPTY_STREAM_CONFIGS;
    private Surface currentSurface = null;
    // private HdmiControlManager hdmiManager;
    private TvInputManager.Hardware mHardware = null;
    protected final int mHardwareDeviceId;
    private boolean mReleased = false;
    private float mSourceVolume = -1f;
    private final MtkTvMultiView multiview = MtkTvMultiView.getInstance();// TODO Need twoworld API

    private int Session_idx = 0;
    private int totalCount = 0;
    /**
     * Indicator Main Or Sub
     */
    protected int activeOutType = TvInputConst.InputInvalid;// InputInvalid;

    /**
     * true: turn is called, false: before turn is called
     */
    protected boolean bIsTune = false;

    /**
     * true: Current source is playing, false: Current source is not
     */
    protected boolean bPlaying = false;

    public Object sync_object = new Object();
    public boolean sync_condition = false;

    public void setSyncCondition() {
      sync_condition = true;
    }

    public Object getSyncObject() {
      return sync_object;
    }

    /*
     * public void setTvStreamConfigMain(TvStreamConfig[] tvStreamConfigMain) {
     * this.tvStreamConfigMain = tvStreamConfigMain; }
     */

    // protected Map<Integer, TvInputManager.Hardware> tvInputHardwareMap = new HashMap<Integer,
    // TvInputManager.Hardware>();

    /*
     * class MyHardwareCallBack extends TvInputManager.HardwareCallback { AbstractInputSession
     * inputSession = null; int inputType; public MyHardwareCallBack(AbstractInputSession
     * inputSession, int inputType) { this.inputSession = inputSession; this.inputType = inputType;
     * // TODO Auto-generated constructor stub }
     * @Override public void onReleased() { synchronized (TvInputService.class) {
     * inputSession.tvInputHardwareMap.remove(inputType); } }
     * @Override public void onStreamConfigChanged(TvStreamConfig[] configs) { synchronized
     * (TvInputService.class) { int len = configs.length; Log.d(TAG,
     * "onStreamConfigChanged inputType" + inputType + " configs=" + configs);
     * inputSession.tvStreamConfigMain = configs; inputSession.tvStreamConfigSub = configs; if
     * (configs != null) { for (int i = 0; i < configs.length; i++) { Log.d(TAG,
     * "onStreamConfigChanged type=" + inputType + "\t" + configs[i]); } } else { Log.d(TAG,
     * "onStreamConfigChanged = null"); } inputSession.reSelectSurface(); } } }
     */

    // private MtkTvVolCtrl mtkTvVolCtrl = null;//TODO Need twoworld API

    protected AbstractInputSession(AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService);
      this.inputService = mtkTvCommonInputService;
      this.mInfo = info;
      this.mManager = inputService.tvInputManager;
      // this.service = inputService.getService();
      this.service = ITvInputManager.Stub.asInterface(ServiceManager
          .getService(Context.TV_INPUT_SERVICE));
      // this.mtkTvVolCtrl = MtkTvVolCtrl.getInstance();//TODO Need twoworld API
      // this.hdmiManager = (HdmiControlManager) getApplicationContext()//
      // .getSystemService(Context.HDMI_CONTROL_SERVICE);

      totalCount = input.getInputSourceTotalNumber();
      Log.d(TAG, "AbstractInputSession() totalCount=" + totalCount);

      // getTvInputHardwareByDeviceId();
      this.Session_idx = index;
      this.mHardwareDeviceId = HardwareDeviceId;
    }

    public int getSession_idx() {
      return this.Session_idx;
    }

    @Override
    public void onSetCaptionEnabled(boolean arg0) {
      Log.d(TAG, "onSetCaptionEnabled  arg0==" + arg0);
      SomeArgs args = SomeArgs.obtain();
      args.arg1 = this;
      args.argi1 = arg0 ? 1 : 0;
      mHandler.obtainMessage(ServiceHandler.DO_SET_CAPTION_ENABLED, args).sendToTarget();
      Log.d(TAG, "onSetCaptionEnabled send DO_SET_CAPTION_ENABLED done. arg0==" + arg0);
    }

    private TvInputManager.Hardware getTvInputHardwareByDeviceId() {
      if (mHardware != null) {
        Log.d(TAG, "getTvInputHardwareByDeviceId(mHardware != null) return mHardware");
        return mHardware;
      }

      int deviceId_acquire = -1;
      if (activeOutType == TvInputConst.InputMain) {
        deviceId_acquire = mHardwareDeviceId;
      }
      else if (activeOutType == TvInputConst.InputSub)
      {
        if (("cn").equals(SystemProperties.get("ro.mtk.system.marketregion"))
            || ("eu").equals(SystemProperties.get("ro.mtk.system.marketregion")))
        {
          deviceId_acquire = mHardwareDeviceId + totalCount;
        }
        else
        {
          deviceId_acquire = mHardwareDeviceId + totalCount + 1;
        }
      }
      else
      {
        Log.d(TAG, "getTvInputHardwareByDeviceId acquireHardware()"
            + "activeOutType = TvInputConst.InputInvalid directly return");
        return null;
      }

      Log.d(TAG, "getTvInputHardwareByDeviceId acquireHardware() deviceId_acquire="
          + deviceId_acquire);
      TvInputManager.HardwareCallback callback = new TvInputManager.HardwareCallback() {
        @Override
        public void onReleased() {
          mHardware = null;
          mStreamConfigs = EMPTY_STREAM_CONFIGS;
          AbstractInputService.this.mStreamConfigs = EMPTY_STREAM_CONFIGS;
        }

        @Override
        public void onStreamConfigChanged(TvStreamConfig[] configs) {
          mStreamConfigs = configs;
          AbstractInputService.this.mStreamConfigs = configs;
          Log.d(TAG, "onStreamConfigChanged() mStreamConfigs.length=" + configs.length);
        }
      };
      mHardware = mManager.acquireTvInputHardware(deviceId_acquire, callback, mInfo);
      return mHardware;
    }

    private TvStreamConfig getStreamConfig() {
      int i = 0;
      while ((AbstractInputService.this.mStreamConfigs.length == 0)
    		  && (this.mStreamConfigs.length == 0))
      {
        i = i + 1;
        if (i >= 50)
        {
          Log.d(TAG, "getStreamConfig  sleep(2) wait 50 times(no any stream config), break");
          break;
        }
        Log.d(
            TAG, "getStreamConfig  sleep(2)," +
                "wait onStreamConfigChanged(TvStreamConfig[]) callback to be call in child thread ");
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
      for (TvStreamConfig config : AbstractInputService.this.mStreamConfigs) {
        if (config.getType() == TvStreamConfig.STREAM_TYPE_INDEPENDENT_VIDEO_SOURCE) {
          return config;
        }
      }
      return null;
    }

    @Override
    public void onRelease() {
      synchronized (TvInputService.class) {
        Log.d(TAG, "onRelease, mReleased = true");
        SomeArgs args = null;
        SomeArgs args_another = null;
        bPlaying = false;
        mReleased = true;
        int deviceId = -1;

        if (activeOutType == TvInputConst.InputMain) {
          deviceId = mHardwareDeviceId;
        }
        else if (activeOutType == TvInputConst.InputSub)
        {
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
          currentSurface = null;
          Log.d(TAG, "onRelease() activeOutType = TvInputConst.InputInvalid directly return");
          args_another = SomeArgs.obtain();
          args_another.arg1 = mInfo;
          args_another.argi1 = Session_idx;
          args_another.arg2 = this;
          mHandler.obtainMessage(ServiceHandler.DO_REMOVE_SESSION, args_another).sendToTarget();
          Log.d(TAG, "onRelease() send DO_REMOVE_SESSION message");
          return;
        }

        activeOutType = TvInputConst.InputInvalid;

        if (mHardware != null){
          if (currentSurface != null) {
            args = SomeArgs.obtain();
            args.arg1 = mHardware;
            args.arg2 = null;// surface
            args.arg3 = null;// config
            args.arg4 = mInfo;
            args.arg5 = this;
            mHandler.obtainMessage(ServiceHandler.DO_SET_SURFACE, args).sendToTarget();
            Log.d(TAG, "onRelease, (currentSurface != null), send DO_SET_SURFACE(null) message");
          }
        }

        currentSurface = null;

        Log.d(TAG, "onRelease deviceId = " + deviceId);
        if (mHardware != null) {
          args = SomeArgs.obtain();
          args.arg1 = mHardware;
          args.argi1 = deviceId;

          mHandler.obtainMessage(ServiceHandler.DO_RELEASE_HARDWARE, args).sendToTarget();
          Log.d(TAG, "onRelease() send DO_RELEASE_HARDWARE message");

          mHardware = null;
        }

        args_another = SomeArgs.obtain();
        args_another.arg1 = mInfo;
        args_another.argi1 = Session_idx;
        args_another.arg2 = this;
        mHandler.obtainMessage(ServiceHandler.DO_REMOVE_SESSION, args_another).sendToTarget();
        Log.d(TAG, "onRelease() send DO_REMOVE_SESSION message");
      }
    }

    protected void reSelectSurface() {
      if (currentSurface != null) {
        Log.d(TAG, "reSelectSurface (currentSurface != null) call onSetSurface(currentSurface)");
        this.onSetSurface(currentSurface);
      }
      else
      {
        Log.d(TAG, "reSelectSurface (currentSurface == null) return");
      }
    }

    private final Map<Integer, Surface> deviceSurfaceMap = new HashMap<Integer, Surface>();

    @Override
    public void onSurfaceChanged(int format, int width, int height) {
      Log.d(TAG, "onSurfaceChanged, format = " + format + " ,width = " + width + " ,height = "
          + height);

      Canvas canvas = null;

      if (null != currentSurface) {
        Log.d(TAG, "onSurfaceChanged, currentSurface != null,"
            + ", lockCanvas and unlockCanvas");
        try {
          canvas = currentSurface.lockCanvas(null);
        } catch (Exception ex) {
        }

        try {
          currentSurface.unlockCanvasAndPost(canvas);
        } catch (Exception ex) {
        }
      }
    }

    @Override
    public boolean onSetSurface(Surface surface) {
      synchronized (TvInputService.class) {
        TvInputManager mManager = inputService.tvInputManager;
        Log.d(TAG, "onSetSurface, activeOutType = " + activeOutType + ", mReleased = " + mReleased);
        if (mReleased) {
          Log.e(TAG, "onSetSurface, (mReleased) return false");
          return false;
        }
        int deviceId = -1;
        /*
         * if (inputService.activeOutType == TvInputConst.InputInvalid) {// Do not know main or
         * sub,change default to main inputService.activeOutType = TvInputConst.InputMain; }
         */

        currentSurface = surface;

        if ((activeOutType == TvInputConst.InputInvalid) && (surface != null)) {
          Log.d(TAG,
              "onSetSurface,"
                  + " (activeOutType == TvInputConst.InputInvalid) && (surface != null) return true");
          return true;
        }

        if (activeOutType == TvInputConst.InputMain) {
          deviceId = mHardwareDeviceId;
          Log.d(TAG, "onSetSurface, hardwareDeviceId(main) = " + deviceId);
        } else if (activeOutType == TvInputConst.InputSub) {
          if (("cn").equals(SystemProperties.get("ro.mtk.system.marketregion"))
              || ("eu").equals(SystemProperties.get("ro.mtk.system.marketregion")))
          {
            deviceId = mHardwareDeviceId + totalCount;
          }
          else
          {
            deviceId = mHardwareDeviceId + totalCount + 1;
          }
          Log.d(TAG, "onSetSurface, hardwareDeviceId(sub) = " + deviceId);
        }

        Log.d(TAG, "onSetSurface " + surface + " deviceId=" + deviceId);
        // try
        {
          TvInputManager.Hardware tvInputHardware = getTvInputHardwareByDeviceId();
          if (tvInputHardware != null) {
            Log.d(TAG, "tvInputHardware =" + tvInputHardware.toString() + " deviceId=" + deviceId);

            TvStreamConfig config = null;

            /*
             * if (tvStreamConfigMain != null && tvStreamConfigMain.length > 0 &&
             * inputService.activeOutType == TvInputConst.InputMain) { config =
             * tvStreamConfigMain[0];// why 0 } else if (tvStreamConfigSub != null &&
             * tvStreamConfigSub.length > 0 && inputService.activeOutType == TvInputConst.InputSub)
             * { config = tvStreamConfigSub[0];// why 0 }
             */

            if (currentSurface != null) {
              config = getStreamConfig();
              if (config == null) {
                Log.d(TAG, "onSetSurface() onSetSurface"
                    + "(surface != null)(config == null)return false"
                    + "notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)");
                notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
                return false;
              }
            }
            /*
             * if (config == null) { Log.e(TAG, "TvStreamConfig is null"); return false; }
             */

            boolean releaseFlag = false;// if device already release ,avoid double release it
            // if (deviceSurfaceMap.containsKey(deviceId) && deviceSurfaceMap.get(deviceId) == null)
            // {
            // releaseFlag = true;
            // }

            if (releaseFlag && surface == null) {// Do not allow double free device
              Log.d(TAG, "onSetSurface =" + "surface=" + surface + "\t double free device, return");
              return false;
            }

            if (config != null)
            {
              Log.d(TAG, "onSetSurface " + " surface=" + surface
                  + ", config =" + config.toString());
            }
            else
            {
              Log.d(TAG, "onSetSurface " + " surface=" + surface + ", config = null");
            }

            SomeArgs args = SomeArgs.obtain();
            args.arg1 = tvInputHardware;
            args.arg2 = surface;
            args.arg3 = config;
            args.arg4 = mInfo;
            args.arg5 = this;
            mHandler.obtainMessage(ServiceHandler.DO_SET_SURFACE, args).sendToTarget();

            // boolean ret = tvInputHardware.setSurface(surface, config);

            // bIsTune = false;
            bPlaying = true;
            deviceSurfaceMap.put(deviceId, surface);

            Log.d(TAG, "onSetSurface() send DO_SET_SURFACE message, return true");

            if (mSourceVolume != -1f)
            {
              Log.d(TAG, "onSetSurface() (mSourceVolume != -1f)"
                  + "recall tvInputHardware.setStreamVolume(mSourceVolume = "
                  + mSourceVolume + ")");
              tvInputHardware.setStreamVolume(mSourceVolume);
              mSourceVolume = -1f;
            }
            /*
             * boolean isTV = (deviceFilter == TvInputConst.TV_INPUT_TYPE_BUILD_IN_TUNER); if (ret)
             * { bIsTune = false; bPlaying = true; deviceSurfaceMap.put(deviceId, surface); if
             * ((!isTV)&&(surface != null)) { notifyVideoAvailable(); } } else { if
             * ((!isTV)&&(surface != null)) {
             * notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN); } }
             */

          } else {
            Log.e(TAG, "tvInputHardware is null");
            return false;
          }
        }
        /*
         * catch (RemoteException e) { e.printStackTrace(); return false; }
         */
      }

      return true;
    }

    @Override
    public void onSetStreamVolume(float volume) {
      Log.d(TAG, "onSetStreamVolume(" + volume + ")" + ", mReleased = " + mReleased);
      if (mReleased) {
        Log.e(TAG, "onSetStreamVolume, (mReleased) return");
        return;
      }
      // mtkTvVolCtrl.setVolume((int) volume);//TODO Need twoworld API
      mSourceVolume = volume;
      int deviceId = mHardwareDeviceId;
      int sourceId = 0;
      if ((SystemProperties.get("sys.mtk.livetv.ready").equals("0"))
          && (mSourceVolume == 1))
      {
        sourceId = deviceIdMapSourceId.get(deviceId);
        Log.d(TAG, "onSetStreamVolume(1) call MtkTvMultiView api(setAudioFocusbySourceid("
            + sourceId + ")" + ", deviceId = " + deviceId + ".");
        multiview.setAudioFocusbySourceid(sourceId);
      }
      try {
        TvInputManager.Hardware tvInputHardware = getTvInputHardwareByDeviceId();
        if (tvInputHardware != null)
        {
          Log.d(TAG,
              "onSetStreamVolume (tvInputHardware != null) call tvInputHardware.setStreamVolume("
                  + volume + ")");
          tvInputHardware.setStreamVolume(volume);
          mSourceVolume = -1f;
        }
      } catch (IllegalStateException e)
      {
        e.printStackTrace();
        return;
      }
    }

    /**
     * For None TV source,e.g. HDMI ...<BR>
     * First call tune(uri), indicator output is main or sub<BR>
     * if does not know main or sub through uri, change the default output to main.<BR>
     * Format is "content://main" or "content://sub"
     */
    @Override
    public boolean onTune(Uri uri) {
      // inputService.showHardwareInfoList();
      synchronized (TvInputService.class) {
        String urlStr = uri.toString();
        int newOutType = TvInputConst.InputInvalid;
        // int deviceid = -1;

        Log.d(TAG, "onTune(" + urlStr + ")");

        if (urlStr.indexOf("main") != -1) {// turn to Main
          newOutType = TvInputConst.InputMain;
        } else if (urlStr.indexOf("sub") != -1) {// turn to Sub
          newOutType = TvInputConst.InputSub;
        } else {
          if (urlStr.indexOf("com.mediatek.tvinput") != -1) {
            Log.d(TAG,
                "onTune, url contain (com.mediatek.tvinput), newOutType = TvInputConst.InputMain");
            newOutType = TvInputConst.InputMain;// default turn to Main
          } else {// may be tune channel, do not tune input source
            Log.d(TAG, "onTune, url not contain (com.mediatek.tvinput)");
            if (true == bPlaying) {
              Log.d(TAG, "onTune, url not contain (com.mediatek.tvinput) (true == bPlaying)"
                  + " return false!");
              return false;
            }
            else
            {
              if ((SystemProperties.get("sys.mtk.livetv.ready").equals("0")))
              {
                Log.d(TAG,
                    "onTune, Live channel app, url not contain (com.mediatek.tvinput)"
                        + " default turn main path!");
                newOutType = TvInputConst.InputMain;
              }
              else
              {
                Log.d(TAG, "onTune, Live tv app, url not contain (com.mediatek.tvinput)");
                int result = MtkTvConfig.getInstance().getConfigValue(
                    MtkTvConfigType.CFG_PIP_POP_TV_FOCUS_WIN);
                result = (0 == result) ? MtkTvConfigType.TV_FOCUS_WIN_MAIN
                    : MtkTvConfigType.TV_FOCUS_WIN_SUB;
                if (result == MtkTvConfigType.TV_FOCUS_WIN_MAIN)
                {
                  Log.d(
                      TAG,
                      "onTune, Live tv app, url not contain (com.mediatek.tvinput),"
                          + " (result == MtkTvConfigType.TV_FOCUS_WIN_MAIN),"
                          + " newOutType = TvInputConst.InputMain");
                  newOutType = TvInputConst.InputMain;
                }
                else if (result == MtkTvConfigType.TV_FOCUS_WIN_SUB)
                {
                  Log.d(
                      TAG,
                      "onTune, Live tv app, url not contain (com.mediatek.tvinput),"
                          + " (result == MtkTvConfigType.TV_FOCUS_WIN_SUB),"
                          + " newOutType = TvInputConst.InputSub");
                  newOutType = TvInputConst.InputSub;
                }
                else
                {
                  Log.d(
                      TAG,
                      "onTune, Live tv app, url not contain (com.mediatek.tvinput),"
                          + " (result != MtkTvConfigType.TV_FOCUS_WIN_MAIN/TV_FOCUS_WIN_SUB),"
                          + " newOutType = TvInputConst.InputMain");
                  newOutType = TvInputConst.InputMain;
                }
              }
            }
          }
        }

        // reset
        // bIsTune = true;

        // int activeOutType = inputService.activeOutType;

        if ((activeOutType == TvInputConst.InputInvalid) || (activeOutType != newOutType)) {
          // TODO On tune need release old surface ??
          if (activeOutType == TvInputConst.InputInvalid)
          {
            Log.d(TAG,
                "onTune, (activeOutType == TvInputConst.InputInvalid), activeOutType = newOutType("
                    + newOutType + ")");
          }

          if ((activeOutType != TvInputConst.InputInvalid)
              && (activeOutType != newOutType))
          {
            Log.d(TAG, "onTune, (activeOutType != newOutType), activeOutType = newOutType("
                + newOutType + ")");
          }
          activeOutType = newOutType;
        } else {
          Log.d(TAG, "onTune, same to old one, activeOutType = " + activeOutType);
        }

        // need enhance, when change channel, do not reSelectSurface
        Log.d(TAG, "onTune, call reSelectSurface");
        reSelectSurface();
      }
      return true;
    }

    public RecordedProgram getRecordedProgram(Uri recordedProgramUri) {
      String[] projection = {//
          TvContract.RecordedPrograms._ID,// 0
          TvContract.RecordedPrograms.COLUMN_INPUT_ID,// 1
          TvContract.RecordedPrograms.COLUMN_CHANNEL_ID,// 2
          TvContract.RecordedPrograms.COLUMN_TITLE,// 3
          TvContract.RecordedPrograms.COLUMN_START_TIME_UTC_MILLIS,// 4
          TvContract.RecordedPrograms.COLUMN_END_TIME_UTC_MILLIS,// 5
          TvContract.RecordedPrograms.COLUMN_RECORDING_DURATION_MILLIS,// 6
          TvContract.RecordedPrograms.COLUMN_RECORDING_DATA_URI,// 7
      };
      if (recordedProgramUri == null) {
        return null;
      }

      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(recordedProgramUri, projection, null, null, null);
      if (cursor == null) {
        return null;
      }
      if (cursor.getCount() < 1) {
        cursor.close();
        return null;
      }

      cursor.moveToFirst();

      long id = cursor.getInt(0);
      String inputId = cursor.getString(1);
      long channelId = cursor.getLong(2);
      String title = cursor.getString(3);
      long startTime = cursor.getLong(4);
      long endTime = cursor.getLong(5);
      long duration = cursor.getLong(6);
      String path = cursor.getString(7);
      // cursor.moveToNext();
      cursor.close();
      return new RecordedProgram.Builder()//
          .setInputId(inputId)//
          .setChannelId(channelId)//
          .setTitle(title)//
          .setStartTimeUtcMillis(startTime)//
          .setEndTimeUtcMillis(endTime)//
          .setDurationTimeMillis(duration)//
          .setPath(path)//
          .build();
    }

    public Channel getChannel(Uri channelUri) {
      String[] projection = {//
          TvContract.Channels._ID,// 0
          TvContract.Channels.COLUMN_DISPLAY_NUMBER,// 1
          TvContract.Channels.COLUMN_DISPLAY_NAME,// 2
          TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA,// 3
      };
      if (channelUri == null) {
        return null;
      }
      if(ContentUris.parseId(channelUri) < 0) {
        return null;
      }
      ContentResolver contentResolver = inputService.getContentResolver();
      Cursor cursor = contentResolver.query(channelUri, projection, null, null, null);
      if (cursor == null) {
        return null;
      }
      if (cursor.getCount() < 1) {
        cursor.close();
        return null;
      }

      cursor.moveToFirst();

      long id = cursor.getInt(0);
      int displayNumber = cursor.getInt(1);
      String displayName = cursor.getString(2);
      byte[] data = cursor.getBlob(3);

      // cursor.moveToNext();
      cursor.close();
      return new Channel.Builder()//
          .setId(id)//
          .setDisplayNumber(String.valueOf(displayNumber))//
          .setDisplayName(displayName)//
          .setData(data)//
          .build();
    }

    private int whichTypeTv() {
      int type = -1;
      int channelId = getCurrentChannelId();
      long newId = (channelId & 0xffffffffL);
      Log.d(TAG, "channelId>>>" + channelId + ">>" + newId);
      String[] projection = {
          TvContract.Channels._ID, TvContract.Channels.COLUMN_INPUT_ID,
          TvContract.Channels.COLUMN_TYPE
      };
      String selection = TvContract.Channels.COLUMN_INPUT_ID + " = ?";
      selection += " and substr(cast(" + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
          + " as varchar),19,10) = ?";
      String[] selectionArgs = {
          mInfo.getId(), String.format("%010d", newId)
      };
      Cursor cursor = getContentResolver().query(TvContract.Channels.CONTENT_URI, projection,
          selection, selectionArgs, null);
      if (cursor != null && cursor.getCount() > 0) {
        Log.d(TAG, "cursor count " + cursor.getCount());
        cursor.moveToFirst();
        do {
          int index = -1;
          index = cursor.getColumnIndex(TvContract.Channels.COLUMN_TYPE);
          if (index >= 0) {
            type = cursor.getInt(index);
            Log.d(TAG, " index mType = " + type);
            break;
          }
        } while (cursor.moveToNext());

      }
      cursor.close();
      return type;
    }
  }

  public class ServiceHandler extends Handler {
    public static final int DO_SET_SURFACE = 1;
    public static final int DO_RELEASE_HARDWARE = 2;
    public static final int DO_REMOVE_SESSION = 3;
    public static final int DO_ADD_SESSION = 4;
    public static final int DO_SET_CAPTION_ENABLED = 5;
    public static final int DO_ADD_RECORDSESSION = 6;
    public static final int DO_REMOVE_RECORDSESSION = 7;
    public static final int DO_START_RECORDING = 8;
    public static final int DO_STOP_RECORDING = 9;
    public static final int DO_ADD_BOOKING = 10;

    @Override
    public void handleMessage(Message msg) {
      SomeArgs args;
      TvInputManager.Hardware mHardware;
      TvInputInfo mInfo;
      AbstractInputSession session;
      AbstractRecordingSession record_session;
      int session_idx;
      int flag_dtv = -1;
      int ret_return = -1;
      String inputId;
      switch (msg.what) {
        case DO_SET_SURFACE:
          Log.d(TAG, "DO_SET_SURFACE.");
          args = (SomeArgs) msg.obj;
          mHardware = (TvInputManager.Hardware) args.arg1;
          Surface surface = (Surface) args.arg2;
          TvStreamConfig config = (TvStreamConfig) args.arg3;
          mInfo = (TvInputInfo) args.arg4;
          session = (AbstractInputSession) args.arg5;
          if (config == null)
          {
            Log.d(TAG, "DO_SET_SURFACE()  surface = " + surface + ", config = null");
          }
          else
          {
            Log.d(TAG, "DO_SET_SURFACE()  surface = " + surface
                + ", config = " + config.toString());
          }
          boolean ret = false;
          try {
            ret = mHardware.setSurface(surface, config);
            Log.d(TAG, "DO_SET_SURFACE mHardware.setSurface() return = " + ret);
          } catch (IllegalStateException e)
          {
            e.printStackTrace();
            return;
          }
          boolean isTV = (deviceFilter == TvInputConst.TV_INPUT_TYPE_BUILD_IN_TUNER);

          Log.d(
              TAG,
              "DO_SET_SURFACE  session = " + session + ", session.session_idx = "
                  + session.getSession_idx());
          getSession(mInfo.getId());// for print debug log
          if (session == null)
          {
            Log.d(TAG, "DO_SET_SURFACE getSession return null, mInfo.getId() = " + mInfo.getId());
            break;
          }
          if ((isTV) && (surface != null))
          {
            synchronized (session.getSyncObject()) {
              session.setSyncCondition();
              session.getSyncObject().notify();
              Log.d(TAG, "Thread " + Thread.currentThread().getName() + ", sync_object notify");
            }
            Log.d(TAG, "Thread " + Thread.currentThread().getName()
                + ", sync_object release mutex");
          }
          if (ret) {
            if ((!isTV) && (surface != null)) {
              session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_TUNING);
            }
          } else {
            if ((!isTV) && (surface != null)) {
              session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
            }
          }
          break;
        case DO_RELEASE_HARDWARE:
          Log.d(TAG, "DO_RELEASE_HARDWARE.");
          args = (SomeArgs) msg.obj;
          mHardware = (TvInputManager.Hardware) args.arg1;
          int device_Id = args.argi1;
          Log.d(TAG, "DO_RELEASE_HARDWARE. releaseTvInputHardware device_Id = " + device_Id);
          tvInputManager.releaseTvInputHardware(device_Id, mHardware);
          break;
        case DO_REMOVE_SESSION:
          Log.d(TAG, "DO_REMOVE_SESSION.");
          args = (SomeArgs) msg.obj;
          mInfo = (TvInputInfo) args.arg1;
          session_idx = args.argi1;
          session = (AbstractInputSession) args.arg2;
          Log.d(TAG, "DO_REMOVE_SESSION. remove mInfo.getId() = " + mInfo.getId()
              + ", session_idx = " + session_idx);
          Log.d(TAG, "DO_REMOVE_SESSION  session = " + session + ", session.session_idx = "
              + session.getSession_idx());
          synchronized (mLock) {
            SessionList.remove(session);
            mSessionMap.put(mInfo.getId(), SessionList);
          }
          getSession(mInfo.getId());// for print debug log
          break;
        case DO_ADD_SESSION:
          Log.d(TAG, "DO_ADD_SESSION.");
          args = (SomeArgs) msg.obj;
          inputId = (String) args.arg1;
          session = (AbstractInputSession) args.arg2;
          session_idx = session.getSession_idx();
          Log.d(TAG, "DO_ADD_SESSION. add inputId = " + inputId + ", session_idx = " + session_idx
              + ",session = " + session);
          synchronized (mLock) {
            SessionList.add(session);
            mSessionMap.put(inputId, SessionList);
          }
          getSession(inputId);// for print debug log
          break;
        case DO_SET_CAPTION_ENABLED:
          Log.d(TAG, "DO_SET_CAPTION_ENABLED.");
          args = (SomeArgs) msg.obj;
          session = (AbstractInputSession) args.arg1;
          boolean enabled = (args.argi1 == 1) ? true : false;
          MtkTvSubtitleBase mSubtitle = new MtkTvSubtitleBase();
          String marketregion = SystemProperties.get("ro.mtk.system.marketregion");
          Log.d(TAG, "DO_SET_CAPTION_ENABLED onSetCaptionEnabled  marketregion==" + marketregion);
          if (marketregion != null && marketregion.equals("us")) {
            int type = session.whichTypeTv();
            if (type == MtkTvChCommonBase.BRDCST_TYPE_ANALOG) {// analog
              analogCCEnable.analogCCSetCcVisible(enabled);
              Log.d(TAG, "DO_SET_CAPTION_ENABLED US analog CC enable = " + enabled);
            } else if (type == MtkTvChCommonBase.BRDCST_TYPE_UNKNOWN
                || type == MtkTvChCommonBase.BRDCST_TYPE_ATSC) {// atsc
              digitalCCEnable.atscCCSetCcVisible(enabled);
              Log.d(TAG, "DO_SET_CAPTION_ENABLED US atsc CC enable = " + enabled);
            }
          } else if (marketregion != null && marketregion.equals("eu")) {
            if (enabled == false) {
              Log.d(TAG, "DO_SET_CAPTION_ENABLED EU subtitle enable = " + enabled);
              mSubtitle.playStream(MtkTvSubtitleBase.SUBTITLE_TRACK_INVALID);
            }
          } else {
            /* ISDB digital CC */
            ISDBDigitalCCEnable.ISDBCCEnable(enabled);
            Log.d(TAG, "DO_SET_CAPTION_ENABLED ISDB digital CC enable = " + enabled);
          }
          break;
        case DO_ADD_RECORDSESSION:
          Log.d(TAG, "DO_ADD_RECORDSESSION.");
          args = (SomeArgs) msg.obj;
          inputId = (String) args.arg1;
          record_session = (AbstractRecordingSession) args.arg2;
          session_idx = record_session.getSession_idx();
          Log.d(TAG, "DO_ADD_RECORDSESSION. add inputId = " + inputId + ", session_idx = "
              + session_idx
              + ",record_session = " + record_session);
          synchronized (mLock_record) {
            RecordSessionList.add(record_session);
            mRecordSessionMap.put(inputId, RecordSessionList);
          }
          break;
        case DO_REMOVE_RECORDSESSION:
          Log.d(TAG, "DO_REMOVE_RECORDSESSION.");
          args = (SomeArgs) msg.obj;
          mInfo = (TvInputInfo) args.arg1;
          session_idx = args.argi1;
          record_session = (AbstractRecordingSession) args.arg2;
          Log.d(TAG, "DO_REMOVE_RECORDSESSION. remove mInfo.getId() = " + mInfo.getId()
              + ", session_idx = " + session_idx);
          Log.d(TAG, "DO_REMOVE_RECORDSESSION  session = " + record_session
              + ", session.session_idx = "
              + record_session.getSession_idx());
          synchronized (mLock_record) {
            RecordSessionList.remove(record_session);
            mRecordSessionMap.put(mInfo.getId(), RecordSessionList);
          }
          break;
        case DO_START_RECORDING:
          args = (SomeArgs) msg.obj;
          record_session = (AbstractRecordingSession) args.arg1;
          flag_dtv = args.argi1;
          record_session.setRecordingFlag();
          Log.d(TAG, "DO_START_RECORDING, flag_Recording = true ");
          if (flag_dtv == 1) {
            Log.d(TAG, "DO_START_RECORDING, MtkTvRecord.getInstance().start(1) ");
            ret_return = MtkTvRecord.getInstance().start(1);
          } else if (flag_dtv == 0) {
            Log.d(TAG, "DO_START_RECORDING, MtkTvRecord.getInstance().start(0) ");
            ret_return = MtkTvRecord.getInstance().start(0);
          }

          if (ret_return != 0) {
            record_session.resetRecordingFlag();
            int errId = MtkTvRecord.getInstance().getErrorID();
            Log.d(TAG, "DO_START_RECORDING, ret != 0, errId = " + errId
                + ", flag_Recording = false");
            if (errId < 0)
            {
              Log.e(TAG, "DO_START_RECORDING, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
              return;
            }
            if (RecordBaseErrorID.values()[errId]
            == RecordBaseErrorID.RECORD_PVR_ERR_ID_INSUFFICIENT_RESOURCE) {
              Log.d(TAG,
                  "DO_START_RECORDING, notifyError(TvInputManager.RECORDING_ERROR_RESOURCE_BUSY)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_RESOURCE_BUSY);
            } else if ((RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_TOO_SMALL) ||
                (RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_FULL) ||
                (RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_NOT_READY)) {
              Log.d(TAG,
                  "DO_START_RECORDING, notifyError(TvInputManager.RECORDING_ERROR_INSUFFICIENT_SPACE)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_INSUFFICIENT_SPACE);
            } else {
              Log.d(TAG, "DO_START_RECORDING, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
            }
            return;
          }

          if (record_session.getRecordingFlag() == true) {
            String ISessionEvent = MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING;
            Bundle IContextData = new Bundle();
            IContextData.putInt(MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING_KEY,
                MtkTvTISMsgBase.MTK_TIS_SESSION_EVENT_RECORDING_VALUE);

            Log.d(TAG,
                "DO_START_RECORDING, flag_Recording == true, "
                    + "notifySessionEvent(Recording_Session_event_recordingStart)");
            record_session.notifySessionEvent(ISessionEvent, IContextData);
          }
          break;
        case DO_STOP_RECORDING:
          args = (SomeArgs) msg.obj;
          record_session = (AbstractRecordingSession) args.arg1;
          Log.d(TAG, "DO_STOP_RECORDING, MtkTvRecord.getInstance().stop()");
          ret_return = MtkTvRecord.getInstance().stop();
          Log.d(TAG, "DO_STOP_RECORDING, MtkTvRecord.getInstance().stop() ret = " + ret_return);
          break;
        case DO_ADD_BOOKING:
          args = (SomeArgs) msg.obj;
          record_session = (AbstractRecordingSession) args.arg1;
          MtkTvBookingBase item = (MtkTvBookingBase) args.arg2;
          Log.d(TAG, "DO_ADD_BOOKING, addBooking, item = " + item.toString());
          ret_return = MtkTvRecord.getInstance().addBooking(item);
          Log.d(TAG, "DO_ADD_BOOKING, addBooking, ret_return = " + ret_return);
          record_session.setBookFlag();

          if (ret_return != 0) {
            record_session.resetBookFlag();
            int errId = MtkTvRecord.getInstance().getErrorID();
            Log.d(TAG, "DO_ADD_BOOKING, ret != 0, errId = " + errId
                + ", flag_Book_RecordingSession = false");
            if (errId < 0)
            {
              Log.e(TAG, "DO_ADD_BOOKING, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
              return;
            }
            if (RecordBaseErrorID.values()[errId]
            == RecordBaseErrorID.RECORD_PVR_ERR_ID_INSUFFICIENT_RESOURCE) {
              Log.d(TAG,
                  "DO_ADD_BOOKING, notifyError(TvInputManager.RECORDING_ERROR_RESOURCE_BUSY)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_RESOURCE_BUSY);
            } else if ((RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_TOO_SMALL) ||
                (RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_FULL) ||
                (RecordBaseErrorID.values()[errId]
                  == RecordBaseErrorID.RECORD_PVR_ERR_ID_DISK_NOT_READY)) {
              Log.d(TAG,
                  "DO_ADD_BOOKING, notifyError(TvInputManager.RECORDING_ERROR_INSUFFICIENT_SPACE)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_INSUFFICIENT_SPACE);
            } else {
              Log.d(TAG, "DO_ADD_BOOKING, notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN)");
              record_session.notifyError(TvInputManager.RECORDING_ERROR_UNKNOWN);
            }
            return;
          }
          break;
        default:
          Log.w(TAG, "Unsupported message:" + msg.what);
          break;
      }
    }
  }

  class SvctxNotifyCallbackHandler extends MtkTvTVCallbackHandler {
    private final String TAG = "SvcNtfCallbackHandler";
    private final AbstractRutineTaskThread mRutineThread;
    private final AbstractInputService service;

    public SvctxNotifyCallbackHandler(AbstractRutineTaskThread rutineThread,
        AbstractInputService service) {
      this.mRutineThread = rutineThread;
      this.service = service;
    }

    @Override
    /**
     * [MTK Internal]
     */
    public int notifySvctxNotificationCode(int code) throws RemoteException {
      if (DEBUG) {
        Log.d(TAG, "service = " + service.resolveInfo.toString() + ", notifySvctxNotificationCode="
            + code + "\n");
      }
      mRutineThread.notifyAVStatus(code);

      return 0;
    }
  }

  public class AbstractRutineTaskThread extends Thread /* process rutine notification or task */{
    protected String TAG = "AbsRutineTaskThread";
    private volatile boolean stop = false;
    protected ContentResolver contentResolver;
    protected HandlerThread mThread = null;

    /* notify content blocked/unblocked */
    public static final int SERVICE_BLOCKED = 9;
    public static final int SERVICE_UNBLOCKED = 12;

    /* notify onVideoAvailable */
    public static final int EVENT_NORMAL = 0;
    public static final int EVENT_SIGNAL_LOCKED = 4;
    public static final int AUDIO_ONLY_SVC = 20;
    public static final int SCRAMBLED_AUDIO_VIDEO_SVC = 23;
    public static final int SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC = 24;
    public static final int SCRAMBLED_AUDIO_NO_VIDEO_SVC = 25;
    public static final int SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC = 26;
    public static final int SCRAMBLED_VIDEO_NO_AUDIO_SVC = 27;

    /* notify onVideoUnavailable */
    public static final int EVENT_SIGNAL_LOSS = 5;
    public static final int NO_AUDIO_VIDEO_SVC = 19;
    public static final int EVENT_NO_RESOURCES = 59;
    public static final int EVENT_INTERNAL_ERROR = 60;

    /* notify notifyRatingChangedMsg */
    public static final int MSG_RATING_BLOCKED_CHANGED = 2000;
    public static final int MSG_PARENTAL_CONTROLS_ENABLED_CHANGED = 2001;

    // protected MtkTvTVCallbackHandler tvCallback = null;//TODO Need twoworld API

    protected void processSync() {
      Log.d(TAG, "processSync");
    };

    public AbstractRutineTaskThread(String name, ContentResolver contentResolver) {
      super(name);
      this.contentResolver = contentResolver;
    }

    @Override
    public void run() {
      mThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_DEFAULT);
      mThread.start();

      processSync();
      Log.d(TAG, "Stop thread " + getName());
    }

    public boolean isStop() {
      return stop;
    }

    public synchronized void setStop(boolean isStop) {
      this.stop = isStop;
    }

    public void notifyAVStatus(int code) {
      Log.d(TAG, "notifyAVStatus enter.\n");
    }

    public int notifyRatingChangedMsg() {
      Log.d(TAG, "notifyRatingChangedMsg enter.\n");
      return 0;
    }

    public int notifyParentalEnabledChangedMsg() {
      Log.d(TAG, "notifyParentalEnabledChangedMsg enter.\n");
      return 0;
    }
  }

  public class InputSignalRutineTaskThread extends AbstractRutineTaskThread {
    public String TAG = "RutineTaskThread";

    private Handler mHandler;
    private final AbstractInputService service;
    private SvctxNotifyCallbackHandler tvCallback = null;

    public InputSignalRutineTaskThread(String name, ContentResolver contentResolver,
        AbstractInputService service) {
      super(name, contentResolver);
      this.service = service;
      Log.d(TAG, "constructed function");

      tvCallback = new SvctxNotifyCallbackHandler(this, this.service);
    }

    @Override
    protected void processSync() {
      if (DEBUG) {
        Log.d(TAG, "processSync go go go");
      }
      if (mThread == null) {
        if (DEBUG) {
          Log.d(TAG, "mThread == null");
        }
        return;
      }

      mHandler = new Handler(mThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
          if (DEBUG) {
            Log.d(TAG, "service = " + service.resolveInfo.toString() + "enter handle message("
                + msg.what + ")");
          }

          List<AbstractInputSession> sessionList;

          switch (msg.what)
          {
          /* notify onVideoAvailable */
            case EVENT_NORMAL:
            case EVENT_SIGNAL_LOCKED:
            case SCRAMBLED_AUDIO_VIDEO_SVC:
            case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
            case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
            case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
            case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
            {
              sessionList = service.getSession();
              if (sessionList == null)
              {
                if (DEBUG) {
                  Log.d(TAG, "sessionList is null");
                }
                return;
              }
              for (AbstractInputSession session : sessionList) {
                if (DEBUG) {
                  Log.d(TAG, "service = " + service.resolveInfo.toString() + ", session(" + session
                      + ")" + ", session.session_idx = " + session.getSession_idx()
                      + ", notifyVideoAvailable");
                }
                session.notifyVideoAvailable();
              }
              break;
            }
            case AUDIO_ONLY_SVC:
            {
              sessionList = service.getSession();
              if (sessionList == null)
              {
                if (DEBUG) {
                  Log.d(TAG, "sessionList is null");
                }
                return;
              }
              for (AbstractInputSession session : sessionList) {
                if (DEBUG) {
                  Log.d(TAG, "service = " + service.resolveInfo.toString() + ", session(" + session
                      + ")" + ", session.session_idx = " + session.getSession_idx()
                      + ", notifyVideoUnavailable(VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY)");
                }
                session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY);
              }
              break;
            }
            /* notify onVideoUnavailable */
            case EVENT_SIGNAL_LOSS:
            {
              sessionList = service.getSession();
              if (sessionList == null)
              {
                if (DEBUG) {
                  Log.d(TAG, "sessionList is null");
                }
                return;
              }
              for (AbstractInputSession session : sessionList) {
                if (DEBUG) {
                  Log.d(
                      TAG,
                      "service = "
                          + service.resolveInfo.toString()
                          + ", session("
                          + session
                          + ")"
                          + ", session.session_idx = "
                          + session.getSession_idx()
                          + ", notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL)");
                }
                session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL);
              }
              break;
            }
            case NO_AUDIO_VIDEO_SVC:
            case EVENT_NO_RESOURCES:
            case EVENT_INTERNAL_ERROR:
            {
              sessionList = service.getSession();
              if (sessionList == null)
              {
                if (DEBUG) {
                  Log.d(TAG, "sessionList is null");
                }
                return;
              }
              for (AbstractInputSession session : sessionList) {
                if (DEBUG) {
                  Log.d(TAG, "service = " + service.resolveInfo.toString() + ", session(" + session
                      + ")" + ", session.session_idx = " + session.getSession_idx()
                      + ", notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN)");
                }
                session.notifyVideoUnavailable(TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              }
              break;
            }
            default:
            {
              if (DEBUG) {
                Log.d(TAG, " default msg.what" + msg.what);
              }
              break;
            }
          }
        }
      };
    }

    @Override
    public void notifyAVStatus(int code) {
      if (DEBUG) {
        Log.d(TAG, "service = " + service.resolveInfo.toString() + ", notifyAVStatus code:" + code);
      }
      if (!checkSessionIsExist())
        return;

      Message msg = Message.obtain();
      switch (code)
      {
      /* notify onVideoAvailable */
        case EVENT_NORMAL:
        case EVENT_SIGNAL_LOCKED:
        case AUDIO_ONLY_SVC:
        case SCRAMBLED_AUDIO_VIDEO_SVC:
        case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
        case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
        case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
        case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
          /* notify onVideoUnavailable */
        case EVENT_SIGNAL_LOSS:
        case NO_AUDIO_VIDEO_SVC:
        case EVENT_NO_RESOURCES:
        case EVENT_INTERNAL_ERROR: {
          msg.what = msg.arg1 = msg.arg2 = code;
          if (DEBUG) {
            Log.d(TAG, "service = " + service.resolveInfo.toString()
                + ", mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
                + msg.arg1);
          }
          mHandler.sendMessage(msg);
          break;
        }
        default: {
          break;
        }
      }
      return;
    }

    private boolean checkSessionIsExist() {
      if (service == null || service.checkSessionexist() == false) {
        if (DEBUG) {
          Log.d(TAG, "checkSessionIsExist service is null or session is null, return false");
        }
        return false;
      }
      if (DEBUG) {
        Log.d(TAG, "service = " + service.resolveInfo.toString()
            + ", checkSessionIsExist return true");
      }
      return true;
    }
  }
}
