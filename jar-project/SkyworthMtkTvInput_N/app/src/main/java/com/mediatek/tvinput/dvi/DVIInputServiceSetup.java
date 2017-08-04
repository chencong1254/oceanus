
package com.mediatek.tvinput.dvi;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * DVIInputServiceSetup Setup activity
 */
public class DVIInputServiceSetup extends Activity {
  protected static final String TAG = "DVIInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(DVIInputService.class);
    super.onCreate(savedInstanceState);
  }
}
