
package com.mediatek.tvinput.dtv;

import com.mediatek.twoworlds.tv.common.MtkTvIntentNotifyCode;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.os.ServiceManager;
import android.media.tv.TvInputManager;

import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.common.MtkTvIntent;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvConfigBase;

import com.mediatek.tvinput.dtv.ChannelSelStatusThread;
import com.mediatek.tvinput.dtv.DTVInputReceiverCallBack;

import java.util.Locale;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class DTVInputReceiver extends BroadcastReceiver {
  public static final int NOTIFY_RATING_ENABLE_CHANGED = 0;

  public static final int NOTIFY_RATING_BLOCKED_CHANGED = 1;

  private static final String TAG = "MtkTvInput(DTVInputReceiver)";

  private Context mContext;

  private TvInputManager tvInputManager = null;

  private MtkTvATSCRating tvAtscRatingInfo = null;

  private static List<DTVInputReceiverCallBack> mListCallBack = Collections
      .synchronizedList(new ArrayList<DTVInputReceiverCallBack>());

  @Override
  public void onReceive(Context arg0, Intent intent) {
    // TODO Auto-generated method stub
    String mIntentAction;
    int mNfyCode;
    int mNfyReason;

    mContext = arg0;

    if (tvInputManager == null) {
      tvInputManager = (TvInputManager) mContext.getSystemService(Context.TV_INPUT_SERVICE);
    }

    tvAtscRatingInfo = MtkTvATSCRating.getInstance();

    mIntentAction = intent.getAction();

    if (mIntentAction.compareTo(TvInputManager.ACTION_PARENTAL_CONTROLS_ENABLED_CHANGED) == 0)
    {
      Log.i(TAG, "Received " + mIntentAction);

      tvAtscRatingInfo.setRatingEnable(tvInputManager.isParentalControlsEnabled());

      Log.i(TAG, "isParentalControlsEnabled = " + tvInputManager.isParentalControlsEnabled());

      for (DTVInputReceiverCallBack tmp : mListCallBack) {
        tmp.notifyDTVParentalEnabledChangedMsg();
      }
    }
    else if (mIntentAction.compareTo(TvInputManager.ACTION_BLOCKED_RATINGS_CHANGED) == 0)
    {
      Log.i(
          TAG,
          "Received " + mIntentAction + ": Parental control is "
              + tvInputManager.isParentalControlsEnabled());

      for (DTVInputReceiverCallBack tmp : mListCallBack) {
        tmp.notifyDTVRatingChangedMsg();
      }
      // ChannelSelStatusThread.notifyATSCRatingChangedMsg();
    }
  }

  public static int registerCallBack(DTVInputReceiverCallBack callBack) {
    Log.d(TAG, "DTVInputReceiver registerCallBack.\n");
    mListCallBack.add(callBack);
    return 0;
  }

  public static int unregisterCallBack(DTVInputReceiverCallBack callBack) {
    Log.d(TAG, "DTVInputReceiver unregisterCallBack.\n");
    mListCallBack.remove(callBack);
    return 0;
  }
}
