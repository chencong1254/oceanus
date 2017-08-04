package skyworth.platformsupport.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import skyworth.platformsupport.PlatformManager;

import static com.platform.CommonDefinitions.ACTION_GLOBAL_BUTTON;
import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/6.
 */

public class AndroidTvGlobalReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ACTION_GLOBAL_BUTTON))
            {
                KeyEvent mGlobalKeyEvent = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if(mGlobalKeyEvent.getAction() == KeyEvent.ACTION_UP)
                {
                    return;
                }
                switch (mGlobalKeyEvent.getKeyCode())
                {
                    case KeyEvent.KEYCODE_TV_INPUT:
                    {
                        PlatformManager.getInstance().GetService().ShowInputSource();
                    }
                    break;
                    default:
                        Log.e(DEBUG_TAG,"KEY EVENT :" + mGlobalKeyEvent.getKeyCode() + "not handle!");
                        break;
                }
            }
        }
}
