
package com.mediatek.tvinput.svideo;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * SVIDEOInputServiceSetup Setup activity
 */
public class SVIDEOInputServiceSetup extends Activity {
  protected static final String TAG = "SVIDEOInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(SVIDEOInputService.class);
    super.onCreate(savedInstanceState);
  }
}
