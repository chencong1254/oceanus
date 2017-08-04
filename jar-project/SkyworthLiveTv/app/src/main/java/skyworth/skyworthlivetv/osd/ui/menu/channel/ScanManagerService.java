package skyworth.skyworthlivetv.osd.ui.menu.channel;

import android.os.Handler;

import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.DtvSearchRequirement;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import skyworth.skyworthlivetv.osd.ui.menu.channel.Listener.ChannelScanListener;

/**
 * Created by yangjianjun on 2017/5/4.
 * 所有的搜台逻辑放在这里面处理,避免Activity太过庞大,
 * 搜台结果以消息的形式发送给Activity,Activity收到后根据消息内容更新界面
 */

public class ScanManagerService {
    private static ScanManagerService mScanInstance;
    private EventManager mEventManager;
    private ChannelScanManager mChannelScanManager;
    private ChannelScanListener mScanListener;
    private ScanManagerService(){
    }

    public static ScanManagerService getmScanInstance(){
        if(mScanInstance == null){
            mScanInstance = new ScanManagerService();
        }
        return mScanInstance;
    }

    public void initManager(Handler handler){
        mEventManager = EventManager.getInstance();
        mChannelScanManager = ChannelScanManager.getInstance();
        mScanListener = new ChannelScanListener("ChannelScanListener", handler);
        mEventManager.registeEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN);
        mEventManager.registeEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_AUTO_SCAN);
        mEventManager.registeEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_MANUAL_SCAN);
        mEventManager.registeEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN_DONE);
        mEventManager.registeEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_SCAN_DONE);
    }

    public void unInitManager(){
        mEventManager.unregisteEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN);
        mEventManager.unregisteEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_AUTO_SCAN);
        mEventManager.unregisteEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_MANUAL_SCAN);
        mEventManager.unregisteEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN_DONE);
        mEventManager.unregisteEventListener(mScanListener,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_SCAN_DONE);
        //mScanListener.setMessageHandler(null);
    }

    public boolean startAtvSearch(EN_ATV_SCAN_MODE mode, int freq){
        boolean ret = false;
        ret = mChannelScanManager.startAtvSearch(mode, freq);
        return ret;
    }

    public boolean startDtvSearch(DtvSearchRequirement dtvreq){
        boolean ret = false;
        ret = mChannelScanManager.startDtvSearch(dtvreq);
        return ret;
    }

    public boolean stopAtvSearch(){
        boolean ret = false;
        ret = mChannelScanManager.stopAtvSearch();
        return ret;
    }

    public boolean stopDtvSearch(){
        boolean ret = false;
        ret = mChannelScanManager.stopDtvSearch();
        return ret;
    }
}
