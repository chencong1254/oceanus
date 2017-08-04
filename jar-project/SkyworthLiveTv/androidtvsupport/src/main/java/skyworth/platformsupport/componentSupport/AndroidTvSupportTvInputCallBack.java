package skyworth.platformsupport.componentSupport;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.util.Log;

import skyworth.platformsupport.androidTvOsd.SkyworthLiveTvSetupActivity;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.platformsupport.PlatformManager;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/9.
 */

public class AndroidTvSupportTvInputCallBack extends TvInputManager.TvInputCallback{
    private Application m_pLivtApplication = null;
    public AndroidTvSupportTvInputCallBack(Context context)
    {
        m_pLivtApplication = (Application) context;
    }
    @Override
    public void onInputStateChanged(String inputId, int state) {
        super.onInputStateChanged(inputId, state);
        Log.d(DEBUG_TAG,"AndroidTvSupportTvInputCallBack------------>onInputStateChanged: " + inputId);
    }

    @Override
    public void onInputAdded(String inputId) {
        super.onInputAdded(inputId);
        if(inputId.contains("/HW") || inputId.contains("hdmi"))
        {
            Log.d(DEBUG_TAG,"current input is hardware input sikp setup");
            return;
        }
        Log.d(DEBUG_TAG,"AndroidTvSupportTvInputCallBack------------>onInputAdded: "+inputId);
        TvInputInfo  addInfo = PlatformManager.getInstance().GetTvInputManager().getTvInputInfo(inputId);
        if(!PlatformChannelManager.getInstance().hasTIFChannelInfoBySource(addInfo.getId()))
        {
            Intent intent = new Intent(m_pLivtApplication, SkyworthLiveTvSetupActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("inputId",inputId);
            m_pLivtApplication.startActivity(intent);
        }
    }

    @Override
    public void onInputRemoved(String inputId) {
        Log.d(DEBUG_TAG,"AndroidTvSupportTvInputCallBack------------>onInputRemoved: "+inputId);
        super.onInputRemoved(inputId);
    }

    @Override
    public void onInputUpdated(String inputId) {
        Log.d(DEBUG_TAG,"AndroidTvSupportTvInputCallBack------------>onInputUpdated: " + inputId);
        super.onInputUpdated(inputId);
    }

    @Override
    public void onTvInputInfoUpdated(TvInputInfo inputInfo) {
        Log.d(DEBUG_TAG,"AndroidTvSupportTvInputCallBack------------>onTvInputInfoUpdated: " + inputInfo.getId());
        super.onTvInputInfoUpdated(inputInfo);
    }
}
