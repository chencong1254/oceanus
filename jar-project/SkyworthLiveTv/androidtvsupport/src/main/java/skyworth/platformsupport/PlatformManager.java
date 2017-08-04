package skyworth.platformsupport;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.tv.TvInputManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.platform.IPlatformApplication;
import com.platform.IPlatformChannelManager;
import com.platform.IPlatformEpgManager;
import com.platform.IPlatformManager;
import com.platform.IPlatformSourceManager;
import com.platform.IPlatformSubtitleManager;
import com.platform.ui.IPlatformView;
import com.platform.service.IPlatformService;

import Oceanus.Tv.Service.ChannelManager.ChannelManager;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.CiManager.CiManager;
import Oceanus.Tv.Service.EpgManager.EpgManager;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.PictureManager.PictureManager;
import Oceanus.Tv.Service.PvrManager.PvrManager;
import Oceanus.Tv.Service.SoundManager.SoundManager;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import Oceanus.Tv.Service.SubtitleManager.SubtitleManager;
import Oceanus.Tv.Service.TeletextManager.TeletextManager;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import Oceanus.Tv.Service.VgaManager.VgaManager;
import skyworth.platformsupport.componentSupport.AndroidTvSupportTvInputCallBack;
import skyworth.platformsupport.componentSupport.AndroidTvSupportTvInputManager;
import skyworth.platformsupport.componentSupport.PlatformTvView;
import skyworth.platformsupport.service.PlatformService;

import static com.platform.CommonDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class PlatformManager implements IPlatformManager{
    static private boolean b_IsServiceBind = false;
    static private PlatformManager m_pThis = null;
    private PlatformTvView m_pMainScreenView = null;
    static private AndroidTvSupportTvInputCallBack m_pTvInputManagerCallBack = null;
    static private IPlatformSourceManager m_pSourceManager = null;
    static private IPlatformChannelManager m_pChannelManager = null;
    static private IPlatformSubtitleManager m_pSubtitleManager = null;
    static private IPlatformEpgManager m_pEpgManager = null;
    static private IPlatformService m_pPlatformService = null;
    static private AndroidTvSupportTvInputManager m_pTvInputManager = null;
    static private Context m_pApplicationContext = null;
    private PlatformManager()
    {
        Log.i(DEBUG_TAG,"init AndroidTvSupport!");
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
    private void BindPlatformService(){
        Intent intent = new Intent(m_pApplicationContext,PlatformService.class);
        intent.setComponent(new ComponentName(m_pApplicationContext.getPackageName(),PlatformService.class.getName()));
        Log.i(DEBUG_TAG, "BindPlatformService()");
        m_pApplicationContext.bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }

    private void UnbindPlatformService(){
        Log.i(DEBUG_TAG, "unBindService() start....");
        m_pApplicationContext.unbindService(conn);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            Log.i(DEBUG_TAG, "onServiceDisconnected()");
            b_IsServiceBind = false;
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            Log.i(DEBUG_TAG, "onServiceConnected()");
            PlatformService.ServiceBinder binder = (PlatformService.ServiceBinder)service;
            m_pPlatformService = binder.getService();
            b_IsServiceBind = true;
            if(m_pApplicationContext!=null)
            {
                IPlatformApplication application = (IPlatformApplication) m_pApplicationContext;
                application.onPlatformServiceBind(m_pPlatformService);
            }
        }
    };

    @Override
    public IPlatformService GetService() {
        return m_pPlatformService;
    }
    public TvInputManager GetTvInputManager()
    {
        return m_pTvInputManager.getTIF();
    }

    @Override
    public IPlatformSourceManager GetSourceManager() {
        return m_pSourceManager;
    }

    @Override
    public IPlatformChannelManager GetChannelManager() {
        return m_pChannelManager;
    }

    @Override
    public IPlatformSubtitleManager GetSubtitleManager() {
        return m_pSubtitleManager;
    }

    @Override
    public IPlatformEpgManager GetEpgManager() {
        return m_pEpgManager;
    }

    @Override
    public Context GetApplicationContext() {
        return m_pApplicationContext;
    }

    @Override
    public IPlatformView GetPlatformView() {
        return m_pMainScreenView;
    }
    @Override
    public void onCreate(Context ApplicationContext) {
        m_pApplicationContext = ApplicationContext;
        m_pTvInputManagerCallBack = new AndroidTvSupportTvInputCallBack(m_pApplicationContext);
        m_pTvInputManager = AndroidTvSupportTvInputManager.getInstance(m_pApplicationContext);
        m_pTvInputManager.getTIF().registerCallback(m_pTvInputManagerCallBack,new Handler());
        BindPlatformService();
        m_pMainScreenView = new PlatformTvView(m_pApplicationContext);
        m_pMainScreenView.InitView();
        EventManager.getInstance();
        TvCommonManager.getInstance();
        SourceManager.getInstance().init(m_pApplicationContext);
        ChannelManager.getInstance();
        SoundManager.getInstance().init(m_pApplicationContext);
        PictureManager.getInstance().init(m_pApplicationContext);
        ChannelScanManager.getInstance();
        CiManager.getInstance();
        PvrManager.getInstance();
        EpgManager.getInstance();
        SubtitleManager.getInstance();
        TeletextManager.getInstance();
        VgaManager.getInstance();
        m_pSourceManager = PlatformSourceManager.getInstance();
        m_pChannelManager = PlatformChannelManager.getInstance();
        m_pSubtitleManager = PlatformSubtitleManager.getInstance();
        m_pEpgManager = PlatformEpgManager.getInstance();

    }
    @Override
    public void onDestory() {
        if(b_IsServiceBind)
        {
            UnbindPlatformService();
        }
        m_pTvInputManager.getTIF().unregisterCallback(m_pTvInputManagerCallBack);

    }
}
