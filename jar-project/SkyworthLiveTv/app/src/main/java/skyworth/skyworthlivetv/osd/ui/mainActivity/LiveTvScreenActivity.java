package skyworth.skyworthlivetv.osd.ui.mainActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.platform.IPlatformChannelManager;
import com.platform.IPlatformEpgManager;
import com.platform.IPlatformSourceManager;
import com.product.KeyMap;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.EpgManager.EpgEvent;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.EventManager.Tv_EventListener;
import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TimeManager.TimeManager;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.platformsupport.PlatformEpgManager;
import skyworth.platformsupport.PlatformManager;
import skyworth.platformsupport.componentSupport.PlatformTvActivity;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.info.InfoBarView;
import skyworth.skyworthlivetv.osd.ui.inputSource.InputSourceDialog;
import skyworth.skyworthlivetv.osd.ui.menu.MenuManager;

import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;


/**
 * Created by sky057509 on 2017/4/26.
 */

public class LiveTvScreenActivity extends PlatformTvActivity{
    private static final int TV_MSG_REFESH_SERVICE_STATUS = 0;
    private static final int TV_MSG_REFESH_SHOW_TV_AFTER_BOOT = 1;
    private static final int TV_MSG_SHOW_INFO_BAR = 2;
    private FrameLayout m_MainFrameLayout = null;
    private MenuManager menuManager = null;
    private boolean disableKeyEvent = true;
    private LiveTvScreenTvEventListener m_pTvEventListener = null;
    private IPlatformSourceManager m_pSourceManager = null;
    private IPlatformChannelManager m_pChannelManager = null;
    private LiveTvActivityHandler m_pHandler = null;
    private InfoBarView m_pInfoBar = null;
    private LiveTvScreenServiceView m_pScreenServiceView = null;
    private class LiveTvActivityHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.arg1)
            {
                case TV_MSG_REFESH_SERVICE_STATUS:
                {
                    if(TvCommonManager.getInstance().getCurrentSignalStatus() == EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL)
                    {
                        if(!ChannelScanManager.getInstance().isScanning())
                        {
                            m_pInfoBar.Show();
                        }
                    }
                    m_pScreenServiceView.RefreshSignalStatus(TvCommonManager.getInstance().getCurrentSignalStatus());
                }
                break;
                case TV_MSG_REFESH_SHOW_TV_AFTER_BOOT:
                {
                    ShowTv();
                }
                break;
                case TV_MSG_SHOW_INFO_BAR:
                {
                    m_pInfoBar.Show();
                }
                break;
                default:
                    break;
            }
        }
    }
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livetvscreen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        m_pHandler = new LiveTvActivityHandler();
        m_pTvEventListener = new LiveTvScreenTvEventListener("LiveTvScreenEventListener");
        EventManager.getInstance().registeEventListener(m_pTvEventListener, EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE);
        EventManager.getInstance().registeEventListener(m_pTvEventListener, EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_CHANNEL_CHANGE);
        Log.d(DEBUG_TAG,"LiveTvScreenActivity onCreate");
        InitUi();
        if(IsStartOnBoot())
        {
            Log.d(DEBUG_TAG,"Start on boot!");
            Message message = new Message();
            message.arg1 = TV_MSG_REFESH_SHOW_TV_AFTER_BOOT;
            m_pHandler.sendMessageDelayed(message,3500);
        }
        else
        {
            ShowTv();
        }

    }
    private void InitUi()
    {
        InputSourceDialog.getInstance(this,getWindowManager()).BindMainActivity(this);
        m_MainFrameLayout = (FrameLayout)findViewById(R.id.MainScreenLayout);
        PlatformManager.getInstance().GetPlatformView().BindLayout(m_MainFrameLayout);
        m_pInfoBar = (InfoBarView)findViewById(R.id.InfoBar);
        m_pScreenServiceView = (LiveTvScreenServiceView) findViewById(R.id.screen_service_view);
        FrameLayout layer_menu  = (FrameLayout) findViewById(R.id.layer_menu);
        menuManager = new MenuManager();
        menuManager.init(layer_menu,this);
    }
    private void ShowTv()
    {
        m_pSourceManager = PlatformManager.getInstance().GetSourceManager();
        m_pSourceManager.RefreshSource();//must refreshSource every time when this activity create!
        m_pChannelManager = PlatformManager.getInstance().GetChannelManager();
        Source CurrentSource = m_pSourceManager.GetCurrentSource();
        m_pSourceManager.SetSource(CurrentSource);
        if(CurrentSource.getType() != EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            Message msg = new Message();
            msg.arg1 = TV_MSG_REFESH_SERVICE_STATUS;
            m_pHandler.sendMessage(msg);
        }
        disableKeyEvent = false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(DEBUG_TAG,"LiveTvScreenActivity--------->onDestroy");
        if(m_MainFrameLayout!=null)
        {
            m_MainFrameLayout.removeAllViews();
        }
        if(m_pTvEventListener!=null)
        {
            EventManager.getInstance().unregisteEventListener(m_pTvEventListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE);
            EventManager.getInstance().unregisteEventListener(m_pTvEventListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_CHANNEL_CHANGE);
        }
    }
    private boolean IsCurrentSourceCanSwitchChannel()
    {
        EN_INPUT_SOURCE_TYPE type = m_pSourceManager.GetCurrentSource().getType();
        return type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_ATSC
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_ISDB
                || type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(DEBUG_TAG,"keyevent: " + event.getKeyCode());
        if(disableKeyEvent)
        {
            Log.w(DEBUG_TAG,"disableKeyEvent is true");
            return true;
        }
        switch(keyCode) {
            case KeyMap.KEYCODE_MTKIR_INFO:
                if(m_pInfoBar.IsShow())
                {
                    m_pInfoBar.Hide();
                }
                else
                {
                    m_pInfoBar.Show();
                }
                return true;
            case KeyMap.KEYCODE_MENU:
                menuManager.toggleShowMainMenu();
                return true;
            case KeyMap.KEYCODE_DPAD_UP:
            case KeyMap.KEYCODE_MTKIR_CHUP:
                if(menuManager.isMenuShown()) {
                    break;
                }
                if (IsCurrentSourceCanSwitchChannel()) {
                    m_pChannelManager.GotoNextChannel();
                    return true;
                }
                break;
            case KeyMap.KEYCODE_DPAD_DOWN:
            case KeyMap.KEYCODE_MTKIR_CHDN:
                if(menuManager.isMenuShown()) {
                    break;
                }
                EN_INPUT_SOURCE_TYPE type = m_pSourceManager.GetCurrentSource().getType();
                if (IsCurrentSourceCanSwitchChannel()) {
                    m_pChannelManager.GotoPrevChannel();
                    return true;
                }
                break;
            case KeyMap.KEYCODE_BACK:
                if (IsCurrentSourceCanSwitchChannel()) {
                    m_pChannelManager.GotoLastChannel();
                }
                return true;
            case KeyMap.KEYCODE_MTKIR_BLUE:
            {
                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
                        long start = 0;
                        if (currentChannel != null) {
                            if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
                            {
                                start = TimeManager.getInstance().getSystemTime().getTime();
                            }
                            else
                            {
                                start = TimeManager.getInstance().getTvTime().getTime();
                            }
                        }
                        List<EpgEvent> list =  PlatformEpgManager.getInstance().getEventsByChannel(PlatformChannelManager.getInstance().GetCurrentChannel(),start, IPlatformEpgManager.EPG_ONE_WEEK_MILLISECOND);
                        for(EpgEvent epg:list)
                        {
                            if(epg == null)
                            {
                                Log.d(DEBUG_TAG,"epg is null!");
                                continue;
                            }
                            Log.d(DEBUG_TAG,"###################################");
                            Log.d(DEBUG_TAG,"getStartTime: "+epg.getStartTime());
                            Log.d(DEBUG_TAG,"getEndTime: "+epg.getEndTime());
                            Log.d(DEBUG_TAG,"getEventName: "+epg.getEventName());
                            Log.d(DEBUG_TAG,"getmProgramName: "+epg.getmProgramName());
                            Log.d(DEBUG_TAG,"getShortDescription: "+epg.getShortDescription());
                            Log.d(DEBUG_TAG,"getLongDescription: "+epg.getLongDescription());
                        }
                    }
                }).start();
                return true;
            }
            case  KeyEvent.KEYCODE_FUNCTION:
            {
                Log.d(DEBUG_TAG,"SACN CODE: " + event.getScanCode());
                switch (event.getScanCode())
                {
                        case KeyMap.KEYCODE_SKYWORTH_CHLIST:
                        {
                            Intent intent = new Intent(this, ChannelListActivity.class);
                            startActivity(intent);
                            return true;
                        }
                        case KeyMap.KEYCODE_SUBTITLE:
                        {
                            Log.d(DEBUG_TAG,"KEYCODE_SUBTITLE");
                            if(PlatformManager.getInstance().GetSubtitleManager().IsSubtitleExist())
                            {
                                Log.d(DEBUG_TAG,"EnableSubtitle");
                                PlatformManager.getInstance().GetSubtitleManager().EnableSubtitle(true);
                            }
                        }
                        return true;
                        default:
                            break;
                }
                break;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    private class LiveTvScreenTvEventListener extends Tv_EventListener
    {
        LiveTvScreenTvEventListener(String name)
        {
            super(name);
        }
        @Override
        protected void onEvnet(final Tv_EventInfo tv_eventInfo) {
            m_pScreenServiceView.post(new Runnable() {
                @Override
                public void run() {
                    m_pScreenServiceView.ProcessTvEvent(tv_eventInfo);
                }
            });
            EN_OSYSTEM_EVENT_LIST event = EN_OSYSTEM_EVENT_LIST.values()[tv_eventInfo.getEventType()];
            switch (event)
            {
                case E_SYSTEM_EVENT_CHANNEL_CHANGE:
                {
                    Log.d(DEBUG_TAG,"E_SYSTEM_EVENT_CHANNEL_CHANGE");
                    if(m_pChannelManager.GetCurrentChannel().getType()!= EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
                    {
                        Message msg = new Message();
                        msg.arg1 = TV_MSG_SHOW_INFO_BAR;
                        m_pHandler.sendMessageDelayed(msg,500);
                        PlatformManager.getInstance().GetSubtitleManager().EnableSubtitle(false);
                    }
                }
                default:break;
            }
        }
    }
}
