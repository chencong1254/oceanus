/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package skyworth.platformsupport.androidTvOsd;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * The settings activity for demonstrating TvInput app.
 */
public class SkyworthLiveTvInputSettingsActivity extends Activity {
  private boolean mIsOnResumeFinish;
  private static final String LIVE_TV_DATA_TAG = "live_tv_data_tag";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onCreate");
    PackageManager packageManager = this.getPackageManager();
    List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);
    Iterator<ApplicationInfo> iterator = applicationInfos.iterator();
    while (iterator.hasNext()) {
      ApplicationInfo info = iterator.next();
      Log.d(DEBUG_TAG,"APP NAME:["+info.packageName+"]");
      if (info.packageName.equals("com.mediatek.wwtv.setting")) {
        Intent intent = getIntent();
        intent.setAction(Intent.ACTION_MAIN);
        Bundle bundle = intent.getBundleExtra(LIVE_TV_DATA_TAG);
        android.util.Log.d(SkyworthLiveTvInputSettingsActivity.class.getSimpleName(), "onCreate() bundle:"
            + bundle);
        if (bundle != null) {
          android.util.Log.d(SkyworthLiveTvInputSettingsActivity.class.getSimpleName(), "onCreate() bundle2:"
              + bundle.getBoolean("to_scanpage", false));
        }
        intent.setClassName("com.mediatek.wwtv.setting",
            "com.mediatek.wwtv.setting.LiveTvSetting");
        intent.putExtra("from_livetv", true);

        this.startActivity(intent);
        this.finish();
        return;
      }

      if (info.packageName.equals("com.mediatek.ui")) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.mediatek.ui", "com.mediatek.ui.menu.MenuMain");

        this.startActivity(intent);
        this.finish();
        return;
      }

      android.util.Log.e(SkyworthLiveTvInputSettingsActivity.class.getSimpleName(), info.packageName);
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onDestroy");
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onPause");
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onRestart");
  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onResume");
    if (mIsOnResumeFinish) {
      this.finish();
    } else {
      mIsOnResumeFinish = true;
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onStart");
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->onStop");
  }

  @Override
  public void recreate() {
    super.recreate();
    Log.d(DEBUG_TAG,"SkyworthLiveTvInputSettingsActivity -------->recreate");
  }

}
