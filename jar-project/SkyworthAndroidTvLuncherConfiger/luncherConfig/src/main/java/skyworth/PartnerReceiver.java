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

package skyworth;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

/**
 * This class posts notifications that are used to populate the Partner Row of the Leanback Launcher
 * It also allows the system/launcher to find the correct partner customization
 * package.
 *
 * Packages using this broadcast receiver must also be a system app to be used for
 * partner customization.
 */
public class PartnerReceiver extends BroadcastReceiver {
    private static final String ACTION_PARTNER_CUSTOMIZATION =
            "com.google.android.leanbacklauncher.action.PARTNER_CUSTOMIZATION";

    private static final String EXTRA_ROW_WRAPPING_CUTOFF =
            "com.google.android.leanbacklauncher.extra.ROW_WRAPPING_CUTOFF";

    private static final String PARTNER_GROUP = "partner_row_entry";
    private static final String BLACKLIST_PACKAGE =
      "com.google.android.leanbacklauncher.replacespackage";

    private static final String TED_PKG_NAME = "com.ted.android.tv";
    private static final String PLAY_MOVIES_PKG_NAME = "com.google.android.videos";

    private static final String TAG = "PartnerReceiver";

    private Context mContext;
    private NotificationManager mNotifMan;
    private PackageManager mPkgMan;

    // Cutoff value for when the Launcher displays the Partner row as a single
    // row, or a two row grid. Can be used for correctly positioning the partner
    // app entries.
    private int mRowCutoff = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mContext == null) {
            mContext = context;
            mNotifMan = (NotificationManager)
                    mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mPkgMan = mContext.getPackageManager();
        }

        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)||
                Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            //postNotification(getPackageName(intent));
        } else if (ACTION_PARTNER_CUSTOMIZATION.equals(action)) {
            //mtk add
            if(isNetflixBootup()) {
                Intent splashIntent = context.getPackageManager().getLaunchIntentForPackage(
                        "com.google.android.boot.appsplashscreen");
                if (splashIntent == null) {
                    Log.i(TAG, "partner customization -- starting splash with an intent");
                    splashIntent = new Intent();
                    splashIntent
                            .setAction("com.google.android.boot.appsplashscreen.action.LAUNCH_APP");
                    splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }

                try {
                    context.startActivity(splashIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Log.e(TAG, "ActivityNotFoundException.");
                }

                android.os.SystemProperties.set("sys.mtk.netflix.bootup", "1");

                Intent mtkintent = new Intent("mtk.intent.netflix.bootup");
                context.sendBroadcast(mtkintent);
            }
            //mtk end
        }
    }
    private Intent getBackupIntent(String pkgName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + pkgName));

        return intent;
    }

    private String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        String pkg = uri != null ? uri.getSchemeSpecificPart() : null;
        return pkg;
    }

    private boolean isNetflixBootup() {
        if (1 == android.os.SystemProperties.getInt("sys.mtk.netflix.cold.boot", 0)) {
            Log.d("PartnerReceiver", "sys.mtk.netflix.cold.boot = 1");
            return false;
        }
        else {
            android.os.SystemProperties.set("sys.mtk.netflix.cold.boot", "1");
        }

        int reason = com.mediatek.twoworlds.tv.MtkTvUtil.getWakeUpReason();
        Log.d("PartnerReceiver", "reason = " + reason);

        if(reason !=
            com.mediatek.twoworlds.tv.common.MtkTvWakeUpReasonTypeBase.MTKTV_WAKE_UP_REASON_IRRC) {
            return false;
        }

        int keycode = com.mediatek.twoworlds.tv.MtkTvUtil.getWakeUpIrKey();
        Log.d("PartnerReceiver", "keycode = " + keycode);

        if(com.mediatek.twoworlds.tv.MtkTvKeyEvent.getInstance().androidKeyToDFBkey(
        android.view.KeyEvent.KEYCODE_BUTTON_1) == keycode) {
            return true;
        }

        return false;
    }
}
