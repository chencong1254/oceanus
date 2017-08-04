
package com.mediatek.tvinput.scart;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * SCARTInputServiceSetup Setup activity
 */
public class SCARTInputServiceSetup extends Activity {
  protected static final String TAG = "SCARTInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(SCARTInputService.class);
    super.onCreate(savedInstanceState);
  }
}
