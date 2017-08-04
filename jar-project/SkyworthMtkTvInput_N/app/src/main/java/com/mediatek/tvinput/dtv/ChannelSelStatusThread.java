
package com.mediatek.tvinput.dtv;

import android.content.ContentResolver;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.OperationApplicationException;

import android.os.RemoteException;
import android.util.Log;
import android.text.TextUtils;

import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;

import com.mediatek.twoworlds.tv.model.TvProviderChannelEventBase;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvSubtitleBase;
import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;

import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvInputSource;

import com.mediatek.twoworlds.tv.model.TvProviderChannelInfoBase;
import com.mediatek.tvinput.dtv.ChannelPump;
import com.mediatek.tvinput.dtv.ProgramPump;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Arrays;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.SparseArray;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.media.tv.TvContract;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;

import com.mediatek.tvinput.Channel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Looper;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigMsg;

import android.media.tv.TvInputService;
import android.media.tv.TvInputManager;
import android.media.tv.TvContentRating;
import android.media.tv.ITvInputManager;

import android.media.tv.TvTrackInfo;
import android.media.tv.TvTrackInfo.Builder;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAVModeBase;
import com.mediatek.twoworlds.tv.model.TvProviderAudioTrackBase;
import com.mediatek.tvinput.dtv.DTVInputReceiver;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfo;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.MtkTvISDBRating;
import com.mediatek.twoworlds.tv.MtkTvDVBRating;

//TODO Need twoworld API
class ChannelSelCallbackHandler extends MtkTvTVCallbackHandler {
  private static String TAG = "MtkTvInput(ChannelSelCallbackHandler)";
  private final ChannelSelStatusThread channelSelStatusThread;

  public ChannelSelCallbackHandler(ChannelSelStatusThread channelSelStatusThread) {
    this.channelSelStatusThread = channelSelStatusThread;
  }

  @Override
  /**
   * [MTK Internal] Following is used to recieve the status of current channel select.
   */
  public int notifySvctxNotificationCode(int code) throws RemoteException {
    Log.d(TAG, "(Default Handler) notifySvctxNotificationCode=" + code + "\n");
    channelSelStatusThread.notifyChannelSelStatus(code);

    return 0;
  }

  @Override
  /**
   * [MTK Internal] Following is used to recieve ANALOG MTS CHANGED.
   */
  public int notifyAVModeMessage(int updateType, int argv1, int argv2, int argv3)
      throws RemoteException {
    // TODO Auto-generated method stub
    Log.d(TAG, "notifyAVModeMessage updateType=" + updateType + "argv1" + argv1);
    channelSelStatusThread.notifyAVModeMessage(updateType, argv1, argv2, argv3);
    return 0;
  }

  @Override
  /**
   * [MTK Internal] Following is used to recieve subtitle's message.
   */
  public int notifySubtitleMsg(int msg_id, int argv1, int argv2, int argv3) throws RemoteException {
    int ret = 0;
    Log.d(TAG, "(Default Handler) notifySubtitleMsg msg_id=" + msg_id + "argv1=" + argv1
        + "argv2=" + argv2 + "argv3=" + argv3);

    ret = channelSelStatusThread.notifySubtitleMsg(msg_id, argv1, argv2, argv3);
    if (0 != ret) {
      Log.d(TAG, "ret=" + ret + "\n");
    }

    return 0;
  }

  public int notifyConfigMessage(int notifyId, int data) throws RemoteException {
    int ret = 0;
    Log.d(TAG, "(Default Handler) notifyConfigMessage notifyId=" + notifyId + ", data=" + data);
    if (notifyId == MtkTvConfigMsg.ACFG_MSG_CHG_CHANNEL)
    {
      Log.d(TAG, "(Default Handler) notifyConfigMessage notifyChannelChange");
      ret = channelSelStatusThread.notifyChannelChange();
      if (0 != ret) {
        Log.d(TAG, "ret=" + ret + "\n");
      }
    }
    return 0;
  }
}

class DTVInputReceiverCallBack {
  private static String TAG = "MtkTvInput(DTVInputReceiverCallBack)";
  private ChannelSelStatusThread mChannelSelStatusThread = null;

  public DTVInputReceiverCallBack(ChannelSelStatusThread channelSelStatusThread) {
    Log.d(TAG, "DTVInputReceiverCallBack Init.\n");
    this.mChannelSelStatusThread = channelSelStatusThread;
    DTVInputReceiver.registerCallBack(this);
  }

  public int notifyDTVRatingChangedMsg() {
    Log.d(TAG, "notifyDTVRatingChangedMsg enter.\n");
    mChannelSelStatusThread.notifyRatingChangedMsg();
    return 0;
  }

  public int notifyDTVParentalEnabledChangedMsg() {
    Log.d(TAG, "notifyDTVParentalEnabledChangedMsg enter.\n");
    mChannelSelStatusThread.notifyParentalEnabledChangedMsg();
    return 0;
  }
}

public class ChannelSelStatusThread extends DataSyncThread {
  public static String TAG = "MtkTvInput(ChannelSelStatusThread)";

  /* sync mtk lock status and TIF lock status */
  private boolean b_svctx_locked = false;

  private Handler mHandler;

  private final TunerInputService service;

  private ChannelSelCallbackHandler tvCallback = null;

  public DTVInputReceiverCallBack tvRevCallback = null;

  /* notify onVideoAvailable */
  public static final int EVENT_NORMAL = 0;
  public static final int SERVICE_CHANGING = 7;
  public static final int SERVICE_BLOCKED = 9;
  public static final int SERVICE_UNBLOCKED = 12;
  public static final int EVENT_SIGNAL_LOCKED = 4;
  public static final int VIDEO_ONLY_SVC = 21;
  public static final int AUDIO_VIDEO_SVC = 22;
  public static final int SCRAMBLED_AUDIO_VIDEO_SVC = 23;
  public static final int SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC = 24;
  public static final int SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC = 26;
  public static final int SCRAMBLED_VIDEO_NO_AUDIO_SVC = 27;

  /* notify onVideoUnavailable */
  public static final int EVENT_SIGNAL_LOSS = 5;
  public static final int NO_AUDIO_VIDEO_SVC = 19;
  public static final int AUDIO_ONLY_SVC = 20;
  public static final int SCRAMBLED_AUDIO_NO_VIDEO_SVC = 25;
  public static final int EVENT_NO_RESOURCES = 59;
  public static final int EVENT_INTERNAL_ERROR = 60;

  /* notify notifyTracksChanged */
  public static final int SCDB_ADD = 56;
  public static final int SCDB_DEL = 57;
  public static final int SCDB_MODIFY = 58;

  /* notify notifyTrackSelected */
  public static final int STREAM_STARTED = 36;
  public static final int AUDIO_FMT_UPDATE = 38;

  public static final int MSG_SUBTITLE_TRACKS_UPDATE = 1000;
  public static final int MSG_SUBTITLE_TRACK_INFO_SELECTED = 1001;

  /* notify notifyRatingChangedMsg */
  public static final int MSG_RATING_BLOCKED_CHANGED = 2000;
  public static final int MSG_PARENTAL_CONTROLS_ENABLED_CHANGED = 2001;

  public static final int MSG_ANALOG_MTS_TRACKS_CHANGE = 3000;
  public static final int MSG_MTS_TRACKS_CHANGE = 3001;
  public static final int MSG_MTS_TRACKS_OTHER_CHANGE = 3002;

  public static final int MSG_CHANNEL_CHANGED = 4000;

  private final MtkTvAVModeBase mAudio = MtkTvAVMode.getInstance();
  private final MtkTvInputSourceBase mInput = MtkTvInputSource.getInstance();

  public ChannelSelStatusThread(String name, ContentResolver contentResolver,
      TunerInputService service) {
    super(name, contentResolver);
    this.service = service;
    Log.d(TAG, "constructed function");
    tvCallback = new ChannelSelCallbackHandler(this);
    tvRevCallback = new DTVInputReceiverCallBack(this);
  }

  private int getCurrentChannelId() {
    int chId = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_NAV_AIR_CRNT_CH);
    return chId;
  }

  private Uri getCurrentChannelUri() {
    Uri channelUri = null;
    int _id = -1;
    int channelId = getCurrentChannelId();
    long newId = (channelId & 0xffffffffL);
    Log.d(TAG, "channelId>>>" + channelId + ">>" + newId);
    if (service.getCurrentTvInputInfo() != null)
    {
      String[] projection = {
          TvContract.Channels._ID, TvContract.Channels.COLUMN_INPUT_ID,
          TvContract.Channels.COLUMN_TYPE
      };
      String selection = TvContract.Channels.COLUMN_INPUT_ID + " = ?";
      selection += " and substr(cast(" + TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA
          + " as varchar),19,10) = ?";
      String[] selectionArgs = {
          service.getCurrentTvInputInfo().getId(), String.format("%010d", newId)
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

  private void trackListUpdate() {

    String inputStr = mInput.getCurrentInputSourceName();
    Log.d(TAG, "trackListUpdate=======" + inputStr);
    if (inputStr.equals("TV") || inputStr.equals("DTV") || inputStr.equals("ATV"))
    {
      List<TvProviderAudioTrackBase> audio_list = mAudio.getAudioAvailableRecord();
      final List<TvTrackInfo> track_list = new ArrayList<TvTrackInfo>();

      int audio_size = audio_list.size();
      int i = 0;
      for (i = 0; i < audio_size; i++)
      {
        TvProviderAudioTrackBase audio_base = audio_list.get(i);
        Log.d(TAG, "index " + i + ":" + audio_base.toString());

        Bundle mExtraBundle = new Bundle();
        mExtraBundle.putString("key_AudioType", Integer.toString(audio_base.getAudioType()));
        mExtraBundle.putString("key_AudioMixType", Integer.toString(audio_base.getAudioMixType()));
        mExtraBundle.putString("key_AudioEditorialClass",
            Integer.toString(audio_base.getAudioEditorialClass()));
        mExtraBundle.putString("key_AudioEncodeType",
            Integer.toString(audio_base.getAudioEncodeType()));
        mExtraBundle.putString("key_AudioDecodeType",
            Integer.toString(audio_base.getAudioDecodeType()));

        TvTrackInfo audioTrack = new TvTrackInfo.Builder(TvTrackInfo.TYPE_AUDIO,
            Integer.toString(audio_base.getAudioId()))
            .setLanguage(audio_base.getAudioLanguage())
            .setAudioChannelCount(audio_base.getAudioChannelCount())
            .setAudioSampleRate(audio_base.getAudioChannelCount()).setExtra(mExtraBundle).build();

        Log.d(
            TAG,
            "index " + i + "key_AudioType:" + audioTrack.getExtra().getString("key_AudioType")
                + "key_AudioMixType:" + audioTrack.getExtra().getString("key_AudioMixType")
                + "key_AudioEditorialClass:"
                + audioTrack.getExtra().getString("key_AudioEditorialClass")
                + "key_AudioEncodeType:" + audioTrack.getExtra().getString("key_AudioEncodeType")
                + "key_AudioDecodeType:" + audioTrack.getExtra().getString("key_AudioDecodeType"));
        track_list.add(audioTrack);
      }

      Log.d(TAG, "enter MSG_SUBTITLE_TRACKS_UPDATE\n");
      int ret = 0;
      MtkTvSubtitleBase mtkSubtitle = new MtkTvSubtitleBase();
      TvTrackInfo subtitleTrack = null;
      ret = mtkSubtitle.getTracks();
      if (0 != ret) {
        Log.d(TAG, "getTracks fail\n");
        return;
      }
      for (i = 0; i < mtkSubtitle.nfySubtitle_trackNum; i++) {
        subtitleTrack = new TvTrackInfo.Builder(TvTrackInfo.TYPE_SUBTITLE,
            Integer.toString(mtkSubtitle.nfySubtitle_trackList[i].trackId))
            .setLanguage(mtkSubtitle.nfySubtitle_trackList[i].trackLanguage).build();
        track_list.add(subtitleTrack);
      }
      Log.d(TAG, "leave MSG_SUBTITLE_TRACKS_UPDATE\n");

      if ((service != null) && (service.getTunerInputSessionSize() > 0))
      {
        service.getTunerInputSession().notifyTracksChanged(track_list);
      }
      else
      {
        Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
      }
    }

  }

  /*
   * try to notify content allowed
   */
  private void tryToNotifyContentAllowed() {
    Log.d(TAG, "tryToNotifyContentAllowed Enter.\n");
    if ((service != null) && (service.getTunerInputSessionSize() > 0))
    {
      service.getTunerInputSession().notifyContentAllowed();
    }
    else
    {
      Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
    }
  }

  /*
   * try to notify content blocked
   */
  private void tryToNotifyContentBlocked(TvContentRating contentRating) {
    Log.d(TAG, "tryToNotifyContentBlocked.\n");
    if ((service != null) && (service.getTunerInputSessionSize() > 0))
    {
      service.getTunerInputSession().notifyContentBlocked(contentRating);
    }
    else
    {
      Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
    }
  }

  private void checkContentBlocked() {
    Log.d(TAG, "checkContentBlocked Enter\n");

    TvContentRating contentRating = null;

    if (null == service) {
      Log.d(TAG, "checkContentBlocked service is null.\n");
      return;
    }

    TvInputManager tvInputMgr = service.getTvInputManager();

    MtkTvRatingConvert2Goo ratingMapped = new MtkTvRatingConvert2Goo();
    MtkTvRatingConvert2Goo.getCurrentRating(ratingMapped);

    if (!TextUtils.isEmpty(ratingMapped.getDomain())
        && !TextUtils.isEmpty(ratingMapped.getRatingSystem())
        && !TextUtils.isEmpty(ratingMapped.getRating())) {

      contentRating = TvContentRating.createRating(ratingMapped.getDomain(),
          ratingMapped.getRatingSystem(),
          ratingMapped.getRating(),
          ratingMapped.getSubRating());
      Log.d(TAG,
          "Conditions is: isParentalControlsEnabled=" + tvInputMgr.isParentalControlsEnabled()
              + "\ncontentRating=[" + contentRating.flattenToString() + "]"
              + "\nisRatingBlocked=" + tvInputMgr.isRatingBlocked(contentRating) + "\n");
    }

    if ((ratingMapped.getRatingType(ratingMapped.getRatingSystem())
          == MtkTvRatingConvert2Goo.RATING_TYPE_USTV
        || ratingMapped.getRatingType(ratingMapped.getRatingSystem())
          == MtkTvRatingConvert2Goo.RATING_TYPE_USMV
        || ratingMapped.getRatingType(ratingMapped.getRatingSystem())
          == MtkTvRatingConvert2Goo.RATING_TYPE_CATV)
        && !tvInputMgr.isParentalControlsEnabled()) {
      Log.d(TAG, "checkContentBlocked: US-TV Content Allowed, b_svctx_locked=" + b_svctx_locked
          + "\n");
      tryToNotifyContentAllowed();
    }

    if (contentRating != null) {
      Log.d(TAG, "checkContentBlocked: contentRating.toString=" + contentRating.flattenToString());
      if (tvInputMgr.isRatingBlocked(contentRating) && b_svctx_locked) {
        Log.d(TAG, "checkContentBlocked: Content Blocked. b_svctx_locked=" + b_svctx_locked);
        tryToNotifyContentBlocked(contentRating);
      } else if (checkBlockByCustomRule(ratingMapped, contentRating) && b_svctx_locked) {
        Log.d(TAG, "checkContentBlocked --- checkBlockByCustomRule: Content Blocked");
        tryToNotifyContentBlocked(contentRating);
      } else {
        Log.d(TAG, "checkContentBlocked Content Allowed. b_svctx_locked=" + b_svctx_locked);
        tryToNotifyContentAllowed();
      }
    } else {
      Log.d(TAG, "checkContentBlocked --22 Content Allowed. b_svctx_locked=" + b_svctx_locked);
      tryToNotifyContentAllowed();
    }

  }

  private boolean checkBlockByCustomRule(MtkTvRatingConvert2Goo ratingMapped,
      TvContentRating contentRating) {
    TvInputManager tvInputMgr = service.getTvInputManager();
    List<TvContentRating> dvbRatings = tvInputMgr.getBlockedRatings();
    int streamAgeValue = ratingMapped.getRatingString2Age(contentRating.getMainRating());
    Log.d(TAG, "checkBlockByCustomRule streamAgeValue==" + streamAgeValue);
    if (dvbRatings.size() > 0) {
      int[] usersetAges = new int[dvbRatings.size()];
      for (int i = 0; i < usersetAges.length; i++) {
        int userAgeValue = ratingMapped.getRatingString2Age(dvbRatings.get(i).getMainRating());
        usersetAges[i] = userAgeValue;
      }
      for (int i = 0; i < usersetAges.length; i++) {
        Log.d(TAG, "checkBlockByCustomRule usersetAges[" + i + "]==" + usersetAges[i]);
      }
      Arrays.sort(usersetAges);
      if (usersetAges[0] <= streamAgeValue) {
        return true;
      }
    }
    return false;
  }

  private void syncParentalControlSettings() {
    Log.d(TAG, "syncParentalControlSettings Enter\n");

    if (null == service) {
      Log.d(TAG, "checkContentBlocked service is null.\n");
      return;
    }

    boolean isUSTVRating = false;
    int userSettingAge = 0xFF;
    MtkTvRatingConvert2Goo mtkRatingCvt = null;
    MtkTvRatingConvert2Goo tvRatingCvtUSMV = null;
    MtkTvRatingConvert2Goo tvRatingCvtCAEN = null;
    MtkTvRatingConvert2Goo tvRatingCvtCAFR = null;

    MtkTvUSTvRatingSettingInfoBase tvRatingInfo = new MtkTvUSTvRatingSettingInfoBase();

    List<TvContentRating> currRatings = service.getTvInputManager().getBlockedRatings();

    Log.d(TAG, "[MSG_RATING_BLOCKED_CHANGED]: currRatings.size=" + currRatings.size());

    if (currRatings != null && currRatings.size() > 0) {
      // step1 Loop all ratings to buffer
      for (TvContentRating tcrTmp : currRatings) {
        Log.d(TAG, "[MSG_RATING_BLOCKED_CHANGED]: rating=[" + tcrTmp.flattenToString() + "]");

        MtkTvRatingConvert2Goo tmpRatingCvt = new MtkTvRatingConvert2Goo(tcrTmp.getDomain(),
            tcrTmp.getRatingSystem(),
            tcrTmp.getMainRating(),
            tcrTmp.getSubRatings());

        if (tmpRatingCvt.getRatingType(tmpRatingCvt.getRatingSystem())
        == MtkTvRatingConvert2Goo.RATING_TYPE_USTV) {
          isUSTVRating = true;
          tmpRatingCvt.convert2USTVRatingInfo(tvRatingInfo, true);
        }
        else {
          int tmpAge = 0;

          /* get mim user setting age */
          tmpAge = tmpRatingCvt.getRatingString2Age();

          if (tmpRatingCvt.getRatingType(tmpRatingCvt.getRatingSystem())
          == MtkTvRatingConvert2Goo.RATING_TYPE_USMV) {
            if ((null == tvRatingCvtUSMV)
                || (tvRatingCvtUSMV != null && tmpAge < tvRatingCvtUSMV.getRatingString2Age())) {
              tvRatingCvtUSMV = tmpRatingCvt;
            }
          }
          else if (tmpRatingCvt.getRatingString2Age(tmpRatingCvt.getRatingSystem())
          == MtkTvRatingConvert2Goo.CA_TV_ENG) {
            if ((null == tvRatingCvtCAEN)
                || (tvRatingCvtCAEN != null && tmpAge < tvRatingCvtCAEN.getRatingString2Age())) {
              tvRatingCvtCAEN = tmpRatingCvt;
            }
          }
          else if (tmpRatingCvt.getRatingString2Age(tmpRatingCvt.getRatingSystem())
          == MtkTvRatingConvert2Goo.CA_TV_FRA) {
            if ((null == tvRatingCvtCAFR)
                || (tvRatingCvtCAFR != null && tmpAge < tvRatingCvtCAFR.getRatingString2Age())) {
              tvRatingCvtCAFR = tmpRatingCvt;
            }
          }
          else {
            Log.d(TAG, "[MSG_RATING_BLOCKED_CHANGED]:DVB tmpAge=" + tmpAge + ", userSettingAge="
                + userSettingAge + "\n");
            if (tmpAge < userSettingAge) {
              userSettingAge = tmpAge;
              mtkRatingCvt = tmpRatingCvt;
            }
          }
        }
      }
    }
    // step2 lock/unlock all ratings
    // US-TV
    if (isUSTVRating) {
      MtkTvATSCRating.getInstance().setUSTvRatingSettingInfo(tvRatingInfo);
    }
    else {
      MtkTvRatingConvert2Goo tmpMtkRatingCvt = new MtkTvRatingConvert2Goo();
      tmpMtkRatingCvt.setRatingSystem(MtkTvRatingConvert2Goo.RATING_SYS_STR_US_TV);
      tmpMtkRatingCvt.setRating(MtkTvRatingConvert2Goo.RATING_STR_TV_MA);
      tmpMtkRatingCvt.unBlockContent();
    }

    // US-MPAA
    if (tvRatingCvtUSMV != null) {
      tvRatingCvtUSMV.blockContent();
    }
    else {
      MtkTvATSCRating.getInstance().setUSMovieRatingSettingInfo(6);
    }

    // Can-english
    if (tvRatingCvtCAEN != null) {
      tvRatingCvtCAEN.blockContent();
    }
    else {
      MtkTvATSCRating.getInstance().setCANEngRatingSettingInfo(7);
    }

    // Can-french
    if (tvRatingCvtCAFR != null) {
      tvRatingCvtCAFR.blockContent();
    }
    else {
      MtkTvATSCRating.getInstance().setCANFreRatingSettingInfo(6);
    }

    // DVB
    if (mtkRatingCvt != null) {
      mtkRatingCvt.blockContent();
    }
    else {
      MtkTvDVBRating.getInstance().setDVBAgeRatingSetting(0);
      MtkTvISDBRating.getInstance().setISDBAgeRatingSetting(0);
      MtkTvISDBRating.getInstance().setISDBContentRatingSetting(0);
    }
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

        switch (msg.what)
        {
          case MSG_CHANNEL_CHANGED:
          {
            Log.d(TAG, "enter MSG_CHANNEL_CHANGED\n");
            Uri channelUri = null;
            if ((service != null) && (service.getTunerInputSessionSize() > 0))
            {
              channelUri = getCurrentChannelUri();
              if (channelUri != null)
              {
                service.getTunerInputSession().notifyChannelRetuned(channelUri);
                Log.d(TAG, "notifyChannelRetuned,channelUri = " + channelUri.toString());
              }
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            Log.d(TAG, "leave MSG_CHANNEL_CHANGED\n");
            break;
          }
          /* handle subtitle msg */
          case MSG_SUBTITLE_TRACKS_UPDATE:
          {
            Log.d(TAG, "enter MSG_SUBTITLE_TRACKS_UPDATE\n");
            trackListUpdate();
            Log.d(TAG, "leave MSG_SUBTITLE_TRACKS_UPDATE\n");
            break;
          }
          case MSG_SUBTITLE_TRACK_INFO_SELECTED:
          {
            Log.d(TAG, "enter MSG_SUBTITLE_TRACK_INFO_SELECTED\n");
            if ((service != null) && (service.getTunerInputSessionSize() > 0))
            {
              service.getTunerInputSession().notifyTrackSelected(TvTrackInfo.TYPE_SUBTITLE,
                  Integer.toString(msg.arg1));
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            Log.d(TAG, "leave MSG_SUBTITLE_TRACK_INFO_SELECTED\n");
            break;
          }
          /* notify notifyRatingChangedMsg */
          case MSG_PARENTAL_CONTROLS_ENABLED_CHANGED:
          {
            Log.d(TAG, "enter MSG_PARENTAL_CONTROLS_ENABLED_CHANGED\n");
            checkContentBlocked();
            break;
          }
          case MSG_RATING_BLOCKED_CHANGED:
          {
            Log.d(TAG, "enter MSG_RATING_BLOCKED_CHANGED\n");

            syncParentalControlSettings();
            checkContentBlocked();

            break;
          }
          /* notify onVideoAvailable */
          case SERVICE_BLOCKED:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);
            /*
             * service.getTunerInputSession().notifyVideoAvailable(); Log.d(TAG,
             * "notifyVideoAvailable,completed!");
             */

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
              tryToNotifyContentBlocked(contentRating);
            }

            b_svctx_locked = true;

            Log.d(TAG, "SERVICE_BLOCKED,Done!");

            break;
          }
          case SERVICE_UNBLOCKED:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);
            tryToNotifyContentAllowed();
            b_svctx_locked = false;
            Log.d(TAG, "SERVICE_UNBLOCKED,Done!");
            break;
          }
          case SERVICE_CHANGING:
          {
            /*Clear block flag for channel change*/
            Log.d(TAG, "enter handleMessage, msg.what = SERVICE_CHANGING");
            b_svctx_locked = false;
            break;
          }
          case EVENT_NORMAL:
            trackListUpdate();
            Log.d(TAG, "notifyTracksChanged,reason : svc change ok");
          case VIDEO_ONLY_SVC:
          case AUDIO_VIDEO_SVC:
          case SCRAMBLED_AUDIO_VIDEO_SVC:
          case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
          case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
          case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);
            if ((service != null) && (service.getTunerInputSessionSize() > 0))
            {
              Log.d(TAG, "notifyVideoAvailable,completed!");
              service.getTunerInputSession().notifyVideoAvailable();
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            checkContentBlocked();
            break;
          }
          case EVENT_SIGNAL_LOCKED:
          {
            Log.d(TAG, "enter EVENT_SIGNAL_LOCKED");
            if ((service != null)
                && ((service.getTunerInputSessionSize() > 0) ||
                (service.getTunerRecordingSessionSize() > 0)))
            {
              Log.d(TAG, "notifyVideoAvailable,completed");
              service.getTunerInputSession().notifyVideoAvailable();
              Log.d(TAG, "setTunerRecordingSessionVideoStatus(true)");
              service.setTunerRecordingSessionVideoStatus(true);
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            break;
          }

          /* notify onVideoUnavailable */
          case EVENT_SIGNAL_LOSS:
          {
            Log.d(TAG, "enter EVENT_SIGNAL_LOSS");
            if ((service != null)
                && ((service.getTunerInputSessionSize() > 0) ||
                (service.getTunerRecordingSessionSize() > 0)))
            {
              Log.d(TAG, "notifyVideoUnavailable,reason : VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL");
              service.getTunerInputSession().notifyVideoUnavailable(
                  TvInputManager.VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL);
              Log.d(TAG, "setTunerRecordingSessionVideoStatus(false)");
              service.setTunerRecordingSessionVideoStatus(false);
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            break;
          }
          case AUDIO_ONLY_SVC:
          {
            Log.d(TAG, "enter AUDIO_ONLY_SVC");
            if ((service != null)
                && ((service.getTunerInputSessionSize() > 0) ||
                (service.getTunerRecordingSessionSize() > 0)))
            {
              Log.d(TAG, "notifyVideoUnavailable,reason : VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY");
              service.getTunerInputSession().notifyVideoUnavailable(
                  TvInputManager.VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY);
              Log.d(TAG, "setTunerRecordingSessionVideoStatus(false)");
              service.setTunerRecordingSessionVideoStatus(false);
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            break;
          }
          case EVENT_NO_RESOURCES:
          case EVENT_INTERNAL_ERROR:
          case NO_AUDIO_VIDEO_SVC:
          case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);
            if ((service != null)
                && ((service.getTunerInputSessionSize() > 0) ||
                (service.getTunerRecordingSessionSize() > 0)))
            {
              Log.d(TAG, "notifyVideoUnavailable,reason : VIDEO_UNAVAILABLE_REASON_UNKNOWN");
              service.getTunerInputSession().notifyVideoUnavailable(
                  TvInputManager.VIDEO_UNAVAILABLE_REASON_UNKNOWN);
              Log.d(TAG, "setTunerRecordingSessionVideoStatus(false)");
              service.setTunerRecordingSessionVideoStatus(false);
            }
            else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
            break;
          }

          case SCDB_ADD:
          case SCDB_DEL:
          case SCDB_MODIFY:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what);
            trackListUpdate();
            Log.d(TAG, "notifyTracksChanged,reason : SCDB UPDATE");
            break;
          }
          case AUDIO_FMT_UPDATE:
          case MSG_MTS_TRACKS_OTHER_CHANGE:
            trackListUpdate();
          case STREAM_STARTED:
          case MSG_MTS_TRACKS_CHANGE:
          {
            String inputStr = mInput.getCurrentInputSourceName();
            if (inputStr.equals("TV") || inputStr.equals("DTV") || inputStr.equals("ATV"))
            {
              Log.d(TAG, "enter handleMessage, msg.what = " + msg.what
                    + "input_name==>" + inputStr);

              TvProviderAudioTrackBase m_current_audio = mAudio.getCurrentAudio();
              if ((service != null) && (service.getTunerInputSessionSize() > 0))
              {
                service.getTunerInputSession().notifyTrackSelected(0,
                    Integer.toString(m_current_audio.getAudioId()));
              }
              else
              {
                Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
              }

              Log.d(TAG, "notifyTrackSelected,reason : " + msg.what);
            }
            break;
          }
          case MSG_ANALOG_MTS_TRACKS_CHANGE:
          {
            Log.d(TAG, "enter handleMessage, msg.what = " + msg.what + "msg.arg1 " + msg.arg1);
            // trackListUpdate();
            if ((service != null) && (service.getTunerInputSessionSize() > 0))
            {
              // TvProviderAudioTrackBase m_current_audio = mAudio.getCurrentAudio();
              service.getTunerInputSession().notifyTrackSelected(0, Integer.toString(msg.arg1));
              Log.d(TAG, "notifyTrackSelected,reason : MSG_ANALOG_MTS_TRACKS_CHANGE");
            } else
            {
              Log.d(TAG, "ERROR: service is null or service.getTunerInputSession() is null!\n");
            }
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

  public void notifyChannelSelStatus(int code) {
    int isHandle = 0;
    Message msg = Message.obtain();
    switch (code)
    {
      case SERVICE_BLOCKED:
      case SERVICE_UNBLOCKED: {
        if ((service == null) || (service.getTunerInputSessionSize() <= 0)) {
          Log.d(TAG, "notifyChannelSelStatus: " + "service or session is null!");
          isHandle = 0;
        }
        else {
          isHandle = 1;
        }

        break;
      }
      case EVENT_NORMAL:
      case SERVICE_CHANGING:
      case SCRAMBLED_AUDIO_VIDEO_SVC:
      case SCRAMBLED_AUDIO_CLEAR_VIDEO_SVC:
      case SCRAMBLED_VIDEO_CLEAR_AUDIO_SVC:
      case SCRAMBLED_VIDEO_NO_AUDIO_SVC:
      case EVENT_SIGNAL_LOCKED:
      case EVENT_SIGNAL_LOSS:
      case NO_AUDIO_VIDEO_SVC:
      case AUDIO_ONLY_SVC:
      case SCRAMBLED_AUDIO_NO_VIDEO_SVC:
      case EVENT_NO_RESOURCES:
      case EVENT_INTERNAL_ERROR:
      case SCDB_ADD:
      case SCDB_DEL:
      case SCDB_MODIFY:
      case AUDIO_FMT_UPDATE:
      case STREAM_STARTED:
      case VIDEO_ONLY_SVC:
      case AUDIO_VIDEO_SVC: {
        isHandle = 1;
        break;
      }
      default:
        return;
    }

    if ((isHandle == 1) && (isMonitor()))
    {
      msg.what = msg.arg1 = msg.arg2 = code;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);
    }
  }

  public int notifySubtitleMsg(int msg_id, int argv1, int argv2, int argv3) {
    if ((service == null) || (service.getTunerInputSessionSize() <= 0)) {
      Log.e(TAG, "notifySubtitleMsg, " + "service or session is null!");
      return -1;
    }
    if (isMonitor()) {
      Message msg = Message.obtain();
      if (msg_id == MtkTvSubtitleBase.SubtitleCallBackType.SUBTITLE_CALL_BACK_TYPE_TRACKS_UPDATE
          .ordinal()) {
        msg.what = MSG_SUBTITLE_TRACKS_UPDATE;
      }
      else if (msg_id == MtkTvSubtitleBase.SubtitleCallBackType.SUBTITLE_CALL_BACK_TYPE_TRACK_INFO_SELECTED
          .ordinal()) {
        msg.what = MSG_SUBTITLE_TRACK_INFO_SELECTED;
        msg.arg1 = argv1;
      }
      else
      {
        Log.d(TAG, "unexcepted msg id=" + msg_id);
        return 0;
      }
      mHandler.sendMessage(msg);
    }
    return 0;
  }

  public int notifyChannelChange() {
    Log.d(TAG, "notifyChannelChange enter.\n");

    if ((service == null) || (service.getTunerInputSessionSize() <= 0))
    {
      Log.d(TAG, "notifyChannelChange: " + "service or session is null!");
      return 0;
    }
    if (isMonitor()) {
      Message msg = Message.obtain();
      msg.what = msg.arg1 = msg.arg2 = MSG_CHANNEL_CHANGED;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);
    }
    return 0;
  }

  public int notifyRatingChangedMsg() {
    Log.d(TAG, "notifyRatingChangedMsg enter.\n");

    if ((service == null) || (service.getTunerInputSessionSize() <= 0))
    {
      Log.d(TAG, "notifyRatingChangedMsg: " + "service or session is null!");
      return 0;
    }
    if (isMonitor()) {
      Message msg = Message.obtain();
      msg.what = msg.arg1 = msg.arg2 = MSG_RATING_BLOCKED_CHANGED;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);
    }
    return 0;
  }

  public int notifyParentalEnabledChangedMsg() {
    Log.d(TAG, "notifyParentalEnabledChangedMsg enter.\n");

    if ((service == null) || (service.getTunerInputSessionSize() <= 0))
    {
      Log.d(TAG, "notifyParentalEnabledChangedMsg: " + "service or session is null!");
      return 0;
    }
    if (isMonitor()) {
      Message msg = Message.obtain();
      msg.what = msg.arg1 = msg.arg2 = MSG_PARENTAL_CONTROLS_ENABLED_CHANGED;
      Log.d(TAG, "mHandler.sendMessage(msg) msg.what = " + msg.what + "msg.arg1 = msg.arg2 = "
          + msg.arg1);
      mHandler.sendMessage(msg);
    }
    return 0;
  }

  public int notifyAVModeMessage(int updateType, int argv1, int argv2, int argv3) {
    if (mThread == null || mHandler == null) {
      Log.d(TAG, "mThread or mHandler is null.wrong ");
      return -1;
    }

    if (isMonitor()) {
      if (updateType == 7) {
        Message msg = Message.obtain();
        msg.what = MSG_ANALOG_MTS_TRACKS_CHANGE;
        msg.arg1 = argv1;
        mHandler.sendMessage(msg);
      }
      else if (updateType == 6)
      {
        Message msg = Message.obtain();
        msg.what = MSG_MTS_TRACKS_CHANGE;
        // msg.arg1 = argv1;
        mHandler.sendMessage(msg);
      }
      else if (updateType == 8)
      {
        Message msg = Message.obtain();
        msg.what = MSG_MTS_TRACKS_OTHER_CHANGE;
        // msg.arg1 = argv1;
        mHandler.sendMessage(msg);
      }
    }
    return 0;
  }
}
