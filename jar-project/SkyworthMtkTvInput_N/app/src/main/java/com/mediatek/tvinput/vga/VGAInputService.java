
package com.mediatek.tvinput.vga;

import java.util.Iterator;
import java.util.Map;

import android.media.tv.TvInputInfo;
import android.util.Log;
import com.android.internal.os.SomeArgs;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

/**
 * This class implement VGA service in TIS
 */
public class VGAInputService extends AbstractInputService {
  private int index = 1;

  public VGAInputService() {
    super();
    TAG += "(VGA)";

    deviceFilter = TvInputConst.TV_INPUT_TYPE_VGA;
    clazz = this.getClass();
  }

  @Override
  public void onCreate() {
    super.onCreate();
    // contentResolver = getContentResolver();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
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

    VGAInputSessionImpl session = new VGAInputSessionImpl(this, info, mHardwareDeviceId, index);

    SomeArgs args = SomeArgs.obtain();
    args.arg1 = inputId;
    args.arg2 = session;

    mHandler.obtainMessage(ServiceHandler.DO_ADD_SESSION, args).sendToTarget();
    Log.d(TAG, "onCreateSession() send DO_ADD_SESSION message, return");
    index = index + 1;
    return session;
  }

  /**
   * VGA <BR>
   */
  class VGAInputSessionImpl extends AbstractInputSession {
    protected VGAInputSessionImpl(AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(VGA)";
    }
  }

}
