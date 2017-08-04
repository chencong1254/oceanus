
package com.mediatek.tvinput.dvi;

import android.os.Bundle;
import android.util.Log;

import android.app.Activity;

import static com.mediatek.tvinput.DebugTag.DEBUG_TAG;

/**
 * DVIInputServiceSetup Setup activity
 */
public class DVIInputServiceSetup extends Activity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(DEBUG_TAG, "onCreate:" + this.toString());
    // clazzList.add(DVIInputService.class);
    super.onCreate(savedInstanceState);
  }
}
