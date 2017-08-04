package skyworth.skyworthlivetv.global;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/8.
 */

public class GlobalReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            Log.d(DEBUG_TAG,"revice: "+Intent.ACTION_BOOT_COMPLETED);
        }
    }
}
