package skyworth.platformsupport;

import android.content.Context;
import android.util.Log;

import com.platform.IPlatformChannelManager;
import com.platform.IPlatformEpgManager;
import com.platform.IPlatformManager;
import com.platform.IPlatformSourceManager;
import com.platform.IPlatformSubtitleManager;
import com.platform.ui.IPlatformView;
import com.platform.service.IPlatformService;

import skyworth.platformsupport.service.Implements.PlatformSourceManager;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class PlatformManager implements IPlatformManager {
    static private PlatformManager m_pThis = null;
    static private PlatformSourceManager m_pSourceManager = null;
    private PlatformManager()
    {
        Log.i(DEBUG_TAG,"init AospSupport!");
        m_pThis = this;
    }
    static public PlatformManager getInstance()
    {
        if(m_pThis == null)
        {
            new PlatformManager();
        }
        return m_pThis;
    }

    @Override
    public IPlatformService GetService() {
        return null;
    }

    @Override
    public IPlatformSourceManager GetSourceManager() {
        return m_pSourceManager;
    }

    @Override
    public IPlatformChannelManager GetChannelManager() {
        return null;
    }

    @Override
    public IPlatformSubtitleManager GetSubtitleManager() {
        return null;
    }

    @Override
    public IPlatformEpgManager GetEpgManager() {
        return null;
    }

    @Override
    public Context GetApplicationContext() {
        return null;
    }

    @Override
    public IPlatformView GetPlatformView() {
        return null;
    }

    @Override
    public void onCreate(Context ApplicationContext) {

    }
    @Override
    public void onDestory() {

    }
}
