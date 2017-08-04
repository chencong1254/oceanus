
package com.mediatek.tvinput.scart;

import java.util.Iterator;
import java.util.Map;

import android.media.tv.TvInputInfo;
import android.media.tv.TvInputService.Session;
import android.util.Log;
import com.android.internal.os.SomeArgs;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

/**
 * This class implement SCART service in TIS
 */
public class SCARTInputService extends AbstractInputService {
  private int index = 1;

  public SCARTInputService() {
    super();
    TAG += "(SCART)";

    deviceFilter = TvInputConst.TV_INPUT_TYPE_SCART;
    clazz = this.getClass();
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

    SCARTInputSessionImpl session = new SCARTInputSessionImpl(this, info, mHardwareDeviceId, index);
    // mSessionMap.put(inputId, session);
    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = session;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args).sendToTarget();
    Log.d(TAG, "onCreateSession() send DO_ADD_SESSION message, return");
    index = index + 1;
    return session;
  }

  /**
   * SCART <BR>
   */
  class SCARTInputSessionImpl extends AbstractInputSession {
    protected SCARTInputSessionImpl(AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(SCART)";
    }
  }

}
