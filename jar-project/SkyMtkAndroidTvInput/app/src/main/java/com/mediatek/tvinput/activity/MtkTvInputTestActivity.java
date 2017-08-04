
package com.mediatek.tvinput.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.tv.ITvInputManager;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.SessionCallback;
import android.media.tv.TvInputManager.TvInputCallback;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.mediatek.tvinput.R;
import com.mediatek.tvinput.TvInputConst;

import java.util.ArrayList;
import java.util.List;

import static com.mediatek.tvinput.DebugTag.DEBUG_TAG;

//import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;//TODO Need twoworld API

public class MtkTvInputTestActivity extends Activity {
  private TvInputManager tvInputManager;
  private final List<TvInputInfo> avaliablePhysicalInputList = new ArrayList<TvInputInfo>();
  private TvInputManager.Session tvSessionMain = null;
  private TvInputManager.Session tvSessionSub = null;
  private TvSurfaceView surfaceViewMain;
  private TvSurfaceView surfaceViewSub;
  private Spinner spinnerMain;
  private Spinner spinnerSub;
  private int currentUsedOutType;
  /**
   * save current main sub input Info
   */
  private final TvInputInfo[] typeInputInfo = new TvInputInfo[TvInputConst.InputMax];

  private final ServiceConnection mConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      tvInputManager = new TvInputManager(ITvInputManager.Stub.asInterface(service), 0);
      populateServices();
    }

    @Override
    public void onServiceDisconnected(ComponentName className) {
      tvInputManager = null;
    }
  };

  private final SessionCallback mSessionCreated = new SessionCallback() {
    @Override
    public void onSessionCreated(TvInputManager.Session session) {
      int outType = currentUsedOutType;
      Uri uri = null;

      if (outType == TvInputConst.InputMain) {
        uri = Uri.parse("content://main");
        tvSessionMain = session;
        tvSessionMain.tune(uri);
        surfaceViewMain.setTvInputSession(tvSessionMain);
      } else if (outType == TvInputConst.InputSub) {
        uri = Uri.parse("content://sub");
        tvSessionSub = session;
        tvSessionSub.tune(uri);
        surfaceViewSub.setTvInputSession(tvSessionSub);
      } else {
        Log.e(DEBUG_TAG, "Can not go here");
        return;
      }

    }
  };

  private void populateServices() {
    if (tvInputManager == null) {
      return;
    }

    // Check whether the system has at least one TvInputService app installed.
    final List<ResolveInfo> services = getPackageManager().queryIntentServices(
        new Intent(TvInputService.SERVICE_INTERFACE),
        PackageManager.GET_SERVICES);
    if (services == null || services.isEmpty()) {
      Log.e(DEBUG_TAG, "services == null || services.isEmpty()");
      return;
    }

    TvInputInfo info = null;
    String serviceName = null;
    if (tvSessionMain != null) {
      // setVolumeByAudioFocusStatus();
    } else {
      List<TvInputInfo> totalInputList = tvInputManager.getTvInputList();
      for (int i = 0; i < totalInputList.size(); i++) {
        info = totalInputList.get(i);
        // serviceName = info.getServiceName();//TODO
        // if (serviceName.indexOf("com.mediatek") != -1) {// Ignore not MTK implement
        avaliablePhysicalInputList.add(info);
        // }
      }

      String[] inputNames = new String[avaliablePhysicalInputList.size() + 1];
      inputNames[0] = "";// First element is null
      ArrayAdapter<String> adapter = new ArrayAdapter<String>(MtkTvInputTestActivity.this,
          android.R.layout.simple_spinner_item,
          inputNames);

      for (int i = 0; i < avaliablePhysicalInputList.size(); i++) {
        info = avaliablePhysicalInputList.get(i);
        // Log.i(DEBUG_TAG, info.getComponent().toString());
        serviceName = info.getId();
        inputNames[i + 1] = serviceName;
        Log.i(DEBUG_TAG, "serviceName:" + serviceName + " sid=" + info.getId());
      }
      spinnerMain.setAdapter(adapter);
      spinnerSub.setAdapter(adapter);
    }
  }

  private final Handler mHandler = new Handler();

  private final TvInputCallback mInternalListener = new TvInputCallback() {
    @Override
    public void onInputStateChanged(String inputId, int state) {
      // mInputStateMap.put(inputId, state);//TODO
      // for (TvInputListener listener : mListeners) {
      // listener.onInputStateChanged(inputId, state);
      // }
    }
  };

  private void startSession(int currentUsedOutType, TvInputInfo inputInfo, long data) {
    Log.i(DEBUG_TAG, "startSession" + inputInfo.getId());
    // Create a new session and start.
    tvInputManager.registerCallback(mInternalListener, /* mAvailabilityChanged, */mHandler);
    tvInputManager.createSession(inputInfo.getId(), mSessionCreated, mHandler);
    typeInputInfo[currentUsedOutType] = inputInfo;
  }

  private void stopSession(int currentUsedOutType) {
    TvInputInfo inputInfo = typeInputInfo[currentUsedOutType];
    if (inputInfo == null) {
      Log.i(DEBUG_TAG, "stopSession fail,inputInfo is null");
      return;
    }
    Log.i(DEBUG_TAG, "stopSession " + inputInfo.getId());
    if (currentUsedOutType == TvInputConst.InputMain && tvSessionMain != null) {
      tvInputManager.unregisterCallback(mInternalListener);
      surfaceViewMain.setTvInputSession(null);
      // mTvSession.setVolume(AUDIO_MIN_VOLUME);
      // mAudioManager.abandonAudioFocus(this);
      tvSessionMain.release();
      tvSessionMain = null;
      typeInputInfo[currentUsedOutType] = null;
      // mTvInputInfo = null;
    }
    if (currentUsedOutType == TvInputConst.InputSub && tvSessionSub != null) {
      tvInputManager.unregisterCallback(mInternalListener);
      surfaceViewSub.setTvInputSession(null);
      // mTvSession.setVolume(AUDIO_MIN_VOLUME);
      // mAudioManager.abandonAudioFocus(this);
      tvSessionSub.release();
      tvSessionSub = null;
      typeInputInfo[currentUsedOutType] = null;
      // mTvInputInfo = null;
    }
  }

  private final TvInputManager.TvInputCallback mInputListener =
    new TvInputManager.TvInputCallback(){
    public void onAvailabilityChanged(final ComponentName name, final boolean isAvailable) {
    }
  };

  private final OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
      // Log.d(DEBUG_TAG, "parent=" + parent + " position=" + position + " id=" + id);
      Spinner spinner = (Spinner) parent;
      if (spinner == spinnerMain) {// select on main
        currentUsedOutType = TvInputConst.InputMain;
      } else if (spinner == spinnerSub) {
        currentUsedOutType = TvInputConst.InputSub;
      }
      String selectedItem = (String) spinner.getSelectedItem();
      Log.d(DEBUG_TAG, "SelectedItem=" + selectedItem);

      // Find Tv input info by full service name
      for (TvInputInfo info : avaliablePhysicalInputList) {
        if (info.getId() == selectedItem) {
          stopSession(currentUsedOutType);
          startSession(currentUsedOutType, info, 0);
          break;
        }
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {

    }
  };

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    surfaceViewMain = (TvSurfaceView) findViewById(R.id.input_view_main);
    surfaceViewSub = (TvSurfaceView) findViewById(R.id.input_view_sub);
    spinnerMain = (Spinner) findViewById(R.id.spinner_main);
    spinnerMain.setOnItemSelectedListener(spinnerListener);
    spinnerSub = (Spinner) findViewById(R.id.spinner_sub);
    spinnerSub.setOnItemSelectedListener(spinnerListener);

    if (false) {
      Log.i(DEBUG_TAG, "onCreate bindService:" + ITvInputManager.class.getName());
      bindService(new Intent(ITvInputManager.class.getName()), mConnection,
          Context.BIND_AUTO_CREATE);
    }

    ITvInputManager mTvInputManagerService = ITvInputManager.Stub.asInterface(ServiceManager
        .getService(Context.TV_INPUT_SERVICE));
    try {
      List<TvInputHardwareInfo> mDeviceInfo = mTvInputManagerService.getHardwareList();
      Log.d("Physical_tvinput_ButtonTest", "" + mDeviceInfo.size());
      for (TvInputHardwareInfo _info : mDeviceInfo) {
        Log.d("Physical_tvinput_ButtonTest _info=", _info.toString());
      }
    } catch (RemoteException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void onDestroy() {
    Log.i(DEBUG_TAG, "onDestroy ");
    if (false)
    {
      unbindService(mConnection);
    }
    if (tvSessionMain != null)
      tvSessionMain.release();
    if (tvSessionSub != null)
      tvSessionSub.release();

    for (TvInputInfo info : avaliablePhysicalInputList) {
      tvInputManager.unregisterCallback(mInputListener);
    }

    super.onDestroy();
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d("Test", "onResume");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d("Test", "onPause");
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    return super.onKeyUp(keyCode, event);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // TODO Auto-generated method stub
    Log.d("AAA", event.toString());
    return super.onTouchEvent(event);
  }

  class MyKeyHandler implements OnClickListener {
    @Override
    public void onClick(View arg0) {
      // TODO Auto-generated method stub
    }
  }
}

// TODO Need twoworld API
// class MyMtkTvTVCallbackHandler extends MtkTvTVCallbackHandler {
// @Override
// public int notifySvctxNotificationCode(int code) {
// Log.d("MyAPK", "(My Own handler) SvctxNotificationCode=" + code);
// return 0;
// }
// }
