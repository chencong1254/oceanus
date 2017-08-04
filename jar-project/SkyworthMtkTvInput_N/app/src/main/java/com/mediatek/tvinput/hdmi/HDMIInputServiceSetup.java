
package com.mediatek.tvinput.hdmi;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * HDMIInputService1 Setup activity
 */
public class HDMIInputServiceSetup extends Activity {
  protected static final String TAG = "HDMIInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(HDMIInputService.class);
    super.onCreate(savedInstanceState);
  }
}
