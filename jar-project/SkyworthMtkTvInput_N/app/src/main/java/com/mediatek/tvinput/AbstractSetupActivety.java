
package com.mediatek.tvinput;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.mediatek.tvinput.dtv.DTVChanelSetup;
import com.mediatek.tvinput.dtv.TunerInputService;

public abstract class AbstractSetupActivety extends Activity {
  protected static final String TAG = "(MtkTvInput)AbstractSetupActivety";
  protected ProgressDialog mProgressDialog;
  protected List<Class<?>> clazzList = new ArrayList<Class<?>>();
  protected AsyncTask<Void, Void, Void> buildDefaultChannel = null;
  protected TvInputManager tvInputManager = null;
  // protected ComponentName componentName;
  protected String inputId;

  @Override
  protected void onCreate(Bundle arg0) {
    super.onCreate(arg0);
    tvInputManager = (TvInputManager) getSystemService(Context.TV_INPUT_SERVICE);

    mProgressDialog = new ProgressDialog(this);
    mProgressDialog.setMessage("Processing  ...");
    mProgressDialog.setCancelable(false);
    mProgressDialog.show();

    inputId = getIntent().getStringExtra(TvInputInfo.EXTRA_INPUT_ID);
    Log.d(TAG, "inputId=" + inputId);

    buildDefaultChannel = new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        for (Class<?> clazz : clazzList) {
          if (hasChannel(clazz) == false) {
            buildInputChannel(clazz);
          }
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void result) {
        mProgressDialog.hide();
        mProgressDialog.dismiss();
        AbstractSetupActivety.this.finish();
      }
    };
    buildDefaultChannel.execute();
  }

  protected boolean hasChannel(Class<?> clazz) {
    return Utils.hasChannel(this, getContentResolver(), inputId);
  }

  // protected void clearInputChannel(Class<?> clazz) {
  // Utils.clearInputChannel(getContentResolver(), inputId);
  // }

  protected void buildInputChannel(Class<?> clazz) {
    String serviceName = clazz.getName();
    Log.d(TAG, "buildInputChannel for service:" + serviceName);

    // For TV source ,build channel from SVL
    if (serviceName.equalsIgnoreCase(TunerInputService.class.getName())) {
      // Here get DTV channel and save to content provider
      DTVChanelSetup dtvChannelSetup = new DTVChanelSetup(this);
      // dtvChannelSetup.checkChannelDataBase();//TODO Need twoworld API
    } else {
      String dispName = clazz.getSimpleName().replace("InputService", "");
      String dispNumber = "1";
      // String inputId = getInputIdFromComponentName(componentName);
      Channel c = new Channel.Builder()//
          .setInputId(inputId)//
          .setDisplayNumber(dispNumber)//
          .setDisplayName(dispName)//
          .build();
      getContentResolver().insert(TvContract.Channels.CONTENT_URI, c.toContentValues());
    }
  }

  public String getInputId() {
    return inputId;
  }

}
