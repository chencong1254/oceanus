
package com.mediatek.tvinput.dvi;

import java.util.Iterator;
import java.util.Map;

import android.media.tv.TvInputInfo;
import android.util.Log;
import com.android.internal.os.SomeArgs;
import android.content.ComponentName;
import android.app.Service;
import android.os.ServiceManager;
import android.os.IBinder;
import android.os.SystemProperties;
import android.content.Context;
import android.content.Intent;

import com.mediatek.tvinput.AbstractInputService;
import com.mediatek.tvinput.TvInputConst;
import com.mediatek.tvinput.AbstractInputService.ServiceHandler;

import com.mediatek.twoworlds.tv.TVRemoteService;

/**
 * This class implement DVI service in TIS
 */
public class DVIInputService extends AbstractInputService {
  private int index = 1;
  //private TVRemoteService tvRemoteService = null;

  public DVIInputService() {
    super();
    TAG += "(DVI)";

    deviceFilter = TvInputConst.TV_INPUT_TYPE_DVI;
    clazz = this.getClass();
  }

  @Override
  public void onCreate() {
    super.onCreate();

    new Thread(new Runnable() {
      @Override
      public void run() {
        // start TV Remote Service
        //tvRemoteService = new TVRemoteService(DVIInputService.this);
        //ServiceManager.addService(TVRemoteService.SERVICE_NAME, tvRemoteService.getBinder());
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.mediatek.agent", "com.mediatek.agent.TVRemoteServiceAgent"));
        DVIInputService.this.startService(intent);
        intent = new Intent();
        intent.setComponent(new ComponentName("com.mediatek.dmagent", "com.mediatek.dmagent.DMRemoteServiceAgent"));
        DVIInputService.this.startService(intent);
        //tvRemoteService.systemReady();
      }
    }).start();
  }

  @Override
  public void onDestroy() {
    Intent intent = new Intent();
    intent.setComponent(new ComponentName("com.mediatek.agent", "com.mediatek.agent.TVRemoteServiceAgent"));
	  this.stopService(intent);
    intent = new Intent();
    intent.setComponent(new ComponentName("com.mediatek.dmagent", "com.mediatek.dmagent.DMRemoteServiceAgent"));
    this.stopService(intent);
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

    DVIInputSessionImpl session = new DVIInputSessionImpl(this, info, mHardwareDeviceId, index);
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
   * DVI <BR>
   */
  class DVIInputSessionImpl extends AbstractInputSession {
    protected DVIInputSessionImpl(AbstractInputService mtkTvCommonInputService, TvInputInfo info,
        int HardwareDeviceId, int index) {
      super(mtkTvCommonInputService, info, HardwareDeviceId, index);
      TAG += "(DVI)";
    }
  }

}
