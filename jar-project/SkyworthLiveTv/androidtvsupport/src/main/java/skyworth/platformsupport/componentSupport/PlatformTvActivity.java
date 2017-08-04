package skyworth.platformsupport.componentSupport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.platform.ui.IPlatformTvActivity;

import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import skyworth.androidtvsupport.R;
import skyworth.platformsupport.PlatformManager;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/16.
 */

public abstract class PlatformTvActivity extends Activity implements IPlatformTvActivity {
    private boolean b_IsActivityShowOnBoot = false;
    private boolean b_IsActivityStoped = false;
    private static PlatformTvActivity m_pThis = null;
    public static PlatformTvActivity getInstance()
    {
        return m_pThis;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        m_pThis = this;
        Log.d(DEBUG_TAG,"onCreate get intent action: " + getIntent().getAction());
        if(getIntent().getAction()!=null&&getIntent().getAction().equals(Intent.ACTION_VIEW)&&getIntent().getBooleanExtra("com.google.android.leanbacklauncher.extra.TV_APP_ON_BOOT",false))
        {
            Log.d(DEBUG_TAG,"b_IsActivityShowOnBoot set true!");
            b_IsActivityShowOnBoot = true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }
    @Override
    protected void onStop() {
        super.onStop();
        b_IsActivityStoped = true;
        Log.d(DEBUG_TAG,"LiveTvScreenActivity--------->onStop");
    }
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(DEBUG_TAG,"LiveTvScreenActivity--------->onPause");
        super.requestVisibleBehind(true);
    }
    @Override
    protected void onResume() {
        super.onResume();
        b_IsActivityStoped = false;
        Log.d(DEBUG_TAG,"LiveTvScreenActivity--------->onResume");
        String data = getIntent().getDataString();
        String action = getIntent().getAction();
        if(action == null)
        {
            return;
        }
        if(getIntent().getAction().equals(Intent.ACTION_VIEW)&&getIntent().getBooleanExtra("com.google.android.leanbacklauncher.extra.TV_APP_ON_BOOT",false))
        {
            Log.d(DEBUG_TAG,"b_IsActivityShowOnBoot set true! return");
            return;
        }
        if(data!=null&&(getIntent().getAction().compareTo(Intent.ACTION_VIEW)==0))
        {
            Log.d(DEBUG_TAG,"DATA: " + data);
            Source currentSource = PlatformManager.getInstance().GetSourceManager().GetCurrentSource();
            if(!data.contains("HW"))
            {
                Log.d(DEBUG_TAG,"Open 3rd live tv app select list!!!");
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_DVB_C_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C);

                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_DVB_T_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T);

                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_DVB_S_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S);

                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_ATV_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV);
                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_HDMI1_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI1)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI1);
                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_HDMI2_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI2)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI2);
                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_HDMI3_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI3)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI3);
                }
            }
            else if(data.contains(getApplicationContext().getString(R.string.TIF_AV_ID)))
            {
                if(currentSource.getType()!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_CVBS)
                {
                    PlatformManager.getInstance().GetSourceManager().SetSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_CVBS);
                }
            }
            else
            {
                Log.e(DEBUG_TAG,"UnHandler data: " + data);
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG,"------------------------>onDestroy");
    }

    @Override
    public boolean IsStartOnBoot() {
        return b_IsActivityShowOnBoot;
    }

    @Override
    public boolean IsStop() {
        return b_IsActivityStoped;
    }
}
