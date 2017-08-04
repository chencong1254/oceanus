
package com.mediatek.tvinput;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.net.Uri;
import android.util.Log;

public class Utils {
  private static final String TAG = "Utils";

  public static String getServiceNameFromInputId(Context context, String inputId) {
    TvInputManager tim = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
    for (TvInputInfo info : tim.getTvInputList()) {
      if (info.getId().equals(inputId)) {
        return info.getServiceInfo().name;
      }
    }
    return null;
  }

  public static String getInputIdFromComponentName(Context context, ComponentName name) {
    TvInputManager tim = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
    Log.d(TAG, tim.toString());
    Log.d(TAG, "" + tim.getTvInputList().size());
    for (TvInputInfo info : tim.getTvInputList()) {
      Log.d(TAG, info.toString());
      ServiceInfo si = info.getServiceInfo();
      Log.d(TAG, "ServiceInfo=" + si.toString());
      Log.d(TAG, "ComponentName" + name.toString());
      if (new ComponentName(si.packageName, si.name).equals(name)) {
        return info.getId();
      }
    }
    return null;
  }

  public static void clearInputChannel(ContentResolver contentResolver, String inputId) {
    Log.d(TAG, "inputId = " + inputId);
    String selection = TvContract.Channels.COLUMN_INPUT_ID + " = ?";
    String[] selectionArgs = new String[] {
        inputId
    };
    contentResolver.delete(TvContract.Channels.CONTENT_URI, selection, selectionArgs);
  }

  public static boolean hasChannel(Context context, ContentResolver contentResolver,
                                   String inputId){
    String[] projection = {
        TvContract.Channels._ID
    };
    Log.d(TAG, "inputId = " + inputId);

    String serviceName = Utils.getServiceNameFromInputId(context, inputId);
    Log.d(TAG, "serviceName = " + serviceName);

    Uri uri = TvContract.buildChannelsUriForInput(inputId, false);
    Log.d(TAG, "uri = " + uri.toString());

    Cursor cursor = contentResolver.query(uri, projection, null, null, null);

    boolean hasChannel = (cursor != null && cursor.getCount() > 0);
    Log.d(TAG, "hasChannel " + inputId + "(" + hasChannel + ")");
    if (cursor != null) {
      cursor.close();
    }
    return hasChannel;
  }
}
