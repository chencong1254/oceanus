
package com.mediatek.tvinput.dtv;

import java.util.List;
import java.util.Iterator;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/**
 * TunerInputServiceSetup
 */
public class TunerInputServiceSetup extends Activity {
  protected static final String TAG = "TunerInputServiceSetup";
  private static final String EXTRA_REQUEST_TIMESHIFT =
      "com.mediatek.tvinput.dtv.DTVInputService.REQUEST_TIMESHIFT";
  private static final String EXTRA_REQUEST_SCAN =
      "com.mediatek.tvinput.dtv.DTVInputService.REQUEST_SCAN";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    Log.d(TAG, "onCreate:" + this.toString());

    boolean requstTimeshift = getIntent().getBooleanExtra(EXTRA_REQUEST_TIMESHIFT, false);
    PackageManager packageManager = this.getPackageManager();
    List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
    Iterator<ApplicationInfo> iterator = applicationInfos.iterator();
    while (iterator.hasNext()) {
      ApplicationInfo info = iterator.next();
      if (info.packageName.equals("com.mediatek.wwtv.setupwizard")) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.mediatek.wwtv.setupwizard",
            "com.mediatek.wwtv.setupwizard.TvWizardActivity");
        intent.putExtra(EXTRA_REQUEST_SCAN, true);
        if (requstTimeshift)
        {
          intent.putExtra(EXTRA_REQUEST_SCAN, false);
          intent.putExtra(EXTRA_REQUEST_TIMESHIFT, true);
        }

        this.startActivity(intent);
        this.finish();
        break;
      }

      if (info.packageName.equals("com.mediatek.ui")) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.mediatek.ui", "com.mediatek.ui.wizard.SetupWizardActivity");

        this.startActivity(intent);
        this.finish();
        break;
      }

      android.util.Log.e(TunerInputServiceSetup.class.getSimpleName(), info.packageName);
    }

    super.onCreate(savedInstanceState);
  }
}
