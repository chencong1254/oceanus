
package com.mediatek.tvinput.vga;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * VGAInputServiceSetup Setup activity
 */
public class VGAInputServiceSetup extends Activity {
  protected static final String TAG = "VGAInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(VGAInputService.class);
    super.onCreate(savedInstanceState);
  }
}
