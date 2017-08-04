
package com.mediatek.tvinput;

import android.util.Log;
import android.text.TextUtils;

import com.mediatek.twoworlds.tv.model.MtkTvRatingConvert2Goo;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.SparseArray;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Hashtable;

import android.app.LoaderManager;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.AbstractInputService.AbstractInputSession;

import android.content.Context;
import android.os.Looper;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import android.media.tv.TvInputService;
import android.media.tv.TvInputManager;
import android.media.tv.TvContentRating;
import android.media.tv.ITvInputManager;

import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfo;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.MtkTvISDBRating;
import com.mediatek.twoworlds.tv.MtkTvDVBRating;

import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.MtkTvInputSource;

public class TVInputRatingProcesser {
  public String TAG = "TVInputRatingProcesser";

  private AbstractInputService service = null;
  private AbstractInputSession inputSession = null;

  /* sync mtk lock status and TIF lock status */
  public boolean b_svctx_locked = false;

  public TVInputRatingProcesser(String name) {
    TAG = TAG + "(" + name + ")";
    Log.d(TAG, "constructed");
  }

  public void setTVInputService(AbstractInputService service) {
    this.service = service;
  }

  public void setTVInputSession(AbstractInputSession session) {
    this.inputSession = session;
  }

  /*
   * try to notify content allowed
   */
  public void tryToNotifyContentAllowed() {
    Log.d(TAG, "tryToNotifyContentAllowed Enter.\n");
    if ((service != null) && (inputSession != null))
    {
      inputSession.notifyContentAllowed();
    }
    else
    {
      Log.d(TAG, "ERROR: service is null or inputSession is null!\n");
    }
  }

  /*
   * try to notify content blocked
   */
  public void tryToNotifyContentBlocked(TvContentRating contentRating) {
    Log.d(TAG, "tryToNotifyContentBlocked.\n");
    if ((service != null) && (inputSession != null))
    {
      inputSession.notifyContentBlocked(contentRating);
    }
    else
    {
      Log.d(TAG, "ERROR: service is null or inputSession is null!\n");
    }
  }

  public void checkContentBlocked() {
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

    if (!TextUtils.isEmpty(ratingMapped.getRatingSystem())
        && (ratingMapped.getRatingType(ratingMapped.getRatingSystem())
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

    if (contentRating != null
        && tvInputMgr.isRatingBlocked(contentRating)) {
      Log.d(TAG, "checkContentBlocked: Content Blocked. b_svctx_locked=" + b_svctx_locked + "\n");
      tryToNotifyContentBlocked(contentRating);
    }
    else {
      Log.d(TAG, "checkContentBlocked Content Allowed. b_svctx_locked=" + b_svctx_locked + "\n");
      tryToNotifyContentAllowed();
    }
  }

  public void syncParentalControlSettings() {
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

}
