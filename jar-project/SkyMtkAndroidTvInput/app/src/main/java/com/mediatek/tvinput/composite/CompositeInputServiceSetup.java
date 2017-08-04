
package com.mediatek.tvinput.composite;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * CompositeInputServiceSetup
 */
public class CompositeInputServiceSetup extends Activity {
  protected static final String TAG = "CompositeInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(CompositeInputService.class);
    super.onCreate(savedInstanceState);
  }
}
