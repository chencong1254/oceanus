
package com.mediatek.tvinput.component;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * ComponentInputServiceSetup
 */
public class ComponentInputServiceSetup extends Activity {
  protected static final String TAG = "ComponentInputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(ComponentInputService.class);
    super.onCreate(savedInstanceState);
  }
}
