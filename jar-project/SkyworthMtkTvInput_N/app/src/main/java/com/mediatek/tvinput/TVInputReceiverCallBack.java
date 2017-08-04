
package com.mediatek.tvinput;

import android.util.Log;

import com.mediatek.tvinput.TVInputReceiver;
import com.mediatek.tvinput.AbstractInputService.AbstractRutineTaskThread;

public class TVInputReceiverCallBack {
  private final String TAG = "TVInputReceiverCallBack";
  private AbstractRutineTaskThread mReceiverThread = null;

  public TVInputReceiverCallBack(AbstractRutineTaskThread receiverThread) {
    Log.d(TAG, "TVInputReceiverCallBack Init.\n");
    this.mReceiverThread = receiverThread;
    TVInputReceiver.registerCallBack(this);
  }

  public int notifyTVRatingChangedMsg() {
    Log.d(TAG, "notifyTVRatingChangedMsg enter.\n");

    if (mReceiverThread != null) {
      mReceiverThread.notifyRatingChangedMsg();
    }

    return 0;
  }

  public int notifyTVParentalEnabledChangedMsg() {
    Log.d(TAG, "notifyTVParentalEnabledChangedMsg enter.\n");

    if (mReceiverThread != null) {
      mReceiverThread.notifyParentalEnabledChangedMsg();
    }
    return 0;
  }
}
