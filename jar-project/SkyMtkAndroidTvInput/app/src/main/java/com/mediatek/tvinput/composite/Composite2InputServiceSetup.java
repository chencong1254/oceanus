
package com.mediatek.tvinput.composite;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

/**
 * CompositeInputServiceSetup
 */
public class Composite2InputServiceSetup extends Activity {
  protected static final String TAG = "Composite2InputServiceSetup";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());
    // clazzList.add(Composite2InputService.class);
    super.onCreate(savedInstanceState);
  }
}
