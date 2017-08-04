package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.os.RemoteException;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvTVCallbackHandler;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIEnqBase;
import com.mediatek.twoworlds.tv.model.MtkTvCIMMIMenuBase;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;

/**
 * Created by sky057509 on 2016/12/21.
 */
public class IEventAdapter extends MtkTvTVCallbackHandler{
    static final int NOTIFY_SIGNAL_HASSIGNAL = 0;
    static final int NOTIFY_SIGNAL_NOSIGNAL = 1;
    static final int NOTIFY_SIGNAL_NOCHANNEL = 2;
    static final int NOTIFY_SIGNAL_DETECTED_SIGNAL = 3;
    static final int NOTIFY_SIGNAL_AUDIO_ONLY = 9;
    static final int NOTIFY_SIGNAL_UNSUPPORT = 10;
    static final int NOTIFY_SIGNAL_NOCICARD = 21;
    static private EventManager mObj_EventManager = null;
    static private  IEventAdapter mObj_This = null;
    private IEventAdapter()
    {
        super();
        mObj_This = this;
        mObj_EventManager = EventManager.getInstance();
    }
    static public IEventAdapter getInstance()
    {
        if(mObj_This == null)
        {
            new IEventAdapter();
        }
        return mObj_This;
    }

    @Override
    public int notifySvlIdUpdateMsg(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTslIdUpdateMsg(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifySatlListUpdateMsg(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifySvctxNotificationCode(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyOtherMessage(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyConfigMessage(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyBisskeyUpdateMsg(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyChannelListUpdateMsg(int i, int i1, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyListModeUpdateMsg(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyOclScanInfo(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTvproviderUpdateMsg(int i, int i1, int[] ints, int[] ints1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyShowOSDMessage(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyHideOSDMessage() throws RemoteException {
        return 0;
    }

    @Override
    public int notifyNativeAppStatus(int i, boolean b) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyAmpVolCtrlMessage(int i, boolean b) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCecNotificationCode(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCecFrameInfo(int i, int i1, int i2, int[] ints, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifySysAudMod(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCecActiveSource(int i, int i1, boolean b) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyMhlScratchpadData(int i, int i1, int i2, int[] ints) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyDeviceDiscovery() throws RemoteException {
        return 0;
    }

    @Override
    public int notifySpdInfoFrame(int i, int i1, int i2, int i3, int[] ints, int[] ints1, int i4) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyUiMsDisplay(int i, boolean b) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyEventNotification(int i, int i1, int i2, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyRecordPBNotification(int i, int i1, int i2, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyRecordNotification(int i, int i1, int i2) throws RemoteException {
        return 0;
    }
    final static private  int SCAN_UNKNOW = 0;
    final static private int SCAN_COMPLETE = 1;
    final static private int SCAN_PROGRESS = 2;
    final static private int SCAN_CANCEL = 4;
    final static private int SCAN_ABORT = 8;
    final static private int SCAN_REPORT_FREQ = 16;
    final static private int ATV_result = 1;
    final static private int DTV_result = 2;
    private int currentPercent = 0;
    private int currentScanNumber = 0;
    private int currentFreq = 0;
    private EN_ATV_SCAN_MODE mEn_AtvScanMode = EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE;
    private EN_DTV_SCAN_MODE mEn_DtvScanMode = EN_DTV_SCAN_MODE.E_DTV_TUNE_MODE_UNDEFINE;
    private boolean b_IsDtvSearch = false;
    public void setScanMode(boolean isDtv,int scanMode)
    {
        b_IsDtvSearch = isDtv;
        if (b_IsDtvSearch) {
            mEn_DtvScanMode = EN_DTV_SCAN_MODE.values()[scanMode];
        }
        else
        {
            mEn_AtvScanMode = EN_ATV_SCAN_MODE.values()[scanMode];
        }
    }


    public EN_DTV_SCAN_MODE getDtvScanMode()
    {
        return  mEn_DtvScanMode;
    }
    @Override
    public int notifyScanNotification(int msg_id, int scanProgress, int channelNum, int argv4) throws RemoteException {
        Log.d("Oceanus", "(Default Handler) notifyScanNotification msg_id=" + msg_id + "scanProgress=" + scanProgress + "channelNum=" + channelNum + "argv4=" + argv4);
        JSONObject jresult = new JSONObject();
        switch (msg_id)
        {
            case SCAN_PROGRESS:
            {
                currentPercent = scanProgress;
                currentScanNumber = channelNum;
                if(b_IsDtvSearch)
                {
                    try {
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",scanProgress);
                        jresult.put("type",0);
                        jresult.put("mode",mEn_DtvScanMode.ordinal());
                        jresult.put("freq",currentFreq);
                        jresult.put("curScanedChNum",channelNum);
                        jresult.put("curScanedDtvNum",0);
                        jresult.put("curScanedRadioNum",0);
                        jresult.put("curScanedDataNum",0);
                        jresult.put("signalQuality",0);
                        jresult.put("signaleLevel", 0);
                        Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_AUTO_SCAN.ordinal(),jresult);
                        mObj_EventManager.sendEvent(info, ChannelScanManager.ListenerName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        jresult.put("curScanedChNum",0);
                        jresult.put("freq",currentFreq);
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",scanProgress);
                        jresult.put("curScanedChNum",channelNum);
                        jresult.put("mode",mEn_AtvScanMode.ordinal());
                        if(mEn_AtvScanMode != EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE)
                        {
                            jresult.put("curScanedSoundSystem",0);
                            jresult.put("curScanedColoreSystem",0);
                        }
                        Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN.ordinal(),jresult);
                        mObj_EventManager.sendEvent(info, ChannelScanManager.ListenerName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case SCAN_REPORT_FREQ:
            {
                currentFreq = scanProgress;
                if(argv4 == ATV_result && (!b_IsDtvSearch))
                {
                    try {
                        jresult.put("curScanedChNum",0);
                        jresult.put("freq",currentFreq);
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",currentPercent);
                        jresult.put("curScanedChNum",channelNum);
                        jresult.put("mode",mEn_AtvScanMode.ordinal());
                        if(mEn_AtvScanMode != EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE)
                        {
                            jresult.put("curScanedSoundSystem",0);
                            jresult.put("curScanedColoreSystem",0);
                        }
                        Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN.ordinal(),jresult);
                        mObj_EventManager.sendEvent(info, ChannelScanManager.ListenerName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else if(argv4 == DTV_result && (b_IsDtvSearch))
                {
                    try {
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",currentPercent);
                        jresult.put("type",0);
                        jresult.put("mode",mEn_DtvScanMode.ordinal());
                        jresult.put("freq",currentFreq);
                        jresult.put("curScanedChNum",currentScanNumber);
                        jresult.put("curScanedDtvNum",currentScanNumber);
                        jresult.put("curScanedRadioNum",0);
                        jresult.put("curScanedDataNum",0);
                        jresult.put("signalQuality",0);
                        jresult.put("signaleLevel", 0);
                        Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_AUTO_SCAN.ordinal(),jresult);
                        mObj_EventManager.sendEvent(info, ChannelScanManager.ListenerName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
            case SCAN_COMPLETE:
            {
                try {
                    if(b_IsDtvSearch)
                    {
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",currentPercent);
                        jresult.put("type",0);
                        jresult.put("mode",mEn_DtvScanMode.ordinal());
                        jresult.put("freq",currentFreq);
                        jresult.put("curScanedChNum",channelNum);
                        jresult.put("curScanedDtvNum",0);
                        jresult.put("curScanedRadioNum",0);
                        jresult.put("curScanedDataNum",0);
                        if(mEn_DtvScanMode == EN_DTV_SCAN_MODE.E_DTV_MANUAL_TUNE_MODE_DVB_T)
                        {
                            jresult.put("signalQuality",TvCommonImpl.getInstance().getSignalQuality());
                            jresult.put("signaleLevel",TvCommonImpl.getInstance().getSignalLevel());
                        }
                        else
                        {
                            jresult.put("signalQuality",0);
                            jresult.put("signaleLevel",0);
                        }

                        if(mEn_DtvScanMode == EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_C || mEn_DtvScanMode == EN_DTV_SCAN_MODE.E_DTV_MANUAL_TUNE_MODE_DVB_C )
                        {
                            ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC,0,0);
                        }
                        else if(mEn_DtvScanMode == EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_T || mEn_DtvScanMode == EN_DTV_SCAN_MODE.E_DTV_MANUAL_TUNE_MODE_DVB_T)
                        {
                            ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT,0,0);
                        }
                    }
                    else
                    {
                        jresult.put("curScanedChNum",0);
                        jresult.put("freq",currentFreq);
                        jresult.put("scanedChNum",0);
                        jresult.put("percent",currentPercent);
                        jresult.put("curScanedChNum",channelNum);
                        jresult.put("mode",mEn_AtvScanMode.ordinal());
                        if(mEn_AtvScanMode != EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE)
                        {
                            jresult.put("curScanedSoundSystem",0);
                            jresult.put("curScanedColoreSystem",0);
                        }
                        ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV,0,0);
                    }
                    Log.d("Oceanus","Send sacn resual : " + jresult.toString());
                    Tv_EventInfo info = null;
                    if(b_IsDtvSearch)
                    {
                        info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_SCAN_DONE.ordinal(),jresult);
                    }
                    else
                    {
                        info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN_DONE.ordinal(),jresult);

                    }
                    ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL,0,0);
                    currentFreq = 0;
                    currentPercent = 0;
                    ChannelScanImpl.getInstance().setScaning(false);
                    mObj_EventManager.sendEvent(info, ChannelScanManager.ListenerName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case SCAN_UNKNOW:
            default:
                return 0;
        }
        return 0;
    }

    @Override
    public int notifyPipPopMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyAVModeMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyGingaMessage(int i, String s, String s1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyMHEG5Message(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyMHEG5LanuchHbbtv(String s, int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyMHEG5MimeTypeSupport(String s, boolean[] booleen, int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyEWSPAMessage(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyMHPMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCIMessage(int i, int i1, int i2, int i3, MtkTvCIMMIMenuBase mtkTvCIMMIMenuBase, MtkTvCIMMIEnqBase mtkTvCIMMIEnqBase) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyOADMessage(int i, String s, int i1, boolean b, int i2) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyHBBTVMessage(int i, int[] ints, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyWarningMessage(int i, int i1, String s, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyEASMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyInputSourceMessage(int i, int i1, int i2, int i3) throws RemoteException {
        Log.d("Oceanus_Notify","notifyInputSourceMessage: " + i + "|" + i1 + "|" + i2 + "|" + i3);
        return 0;
    }

    @Override
    public int notifyTeletextMessage(int i, int i1, int i2, int i3) throws RemoteException {
        Log.d("Oceanus_Notify","notifyTeletextMessage: " + i + "|" + i1 + "|" + i2 + "|" + i3);
        if(i == 2)
        {
            TeletexImpl.getInstance().setTeletextStatus(true);
        }
        else if(i == 3)
        {
            TeletexImpl.getInstance().setTeletextStatus(false);
        }
        return 0;
    }

    @Override
    public int notifyFeatureMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyBroadcastMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCDTLogoMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyUpgradeMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyBannerMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyCCMessage(int i, int i1, int i2, int i3) throws RemoteException {
        Log.d("Oceanus_Notify","notifyCCMessage: " + i + "|" + i1 + "|" + i2 + "|" + i3);
        return 0;
    }

    @Override
    public int notifyPWDDialogMessage(int i, int i1, int i2, int i3) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyScreenSaverMessage(int i, int i1, int i2, int i3) throws RemoteException {
        Log.d("Oceanus_Notify","notifyScreenSaverMessage: " + i + "|" + i1 + "|" + i2 + "|" + i3);
        EN_SERVICE_STATUS service = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNKNONW_STATE;
        if(i == NOTIFY_SIGNAL_HASSIGNAL)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL;
        }
        else if (i == NOTIFY_SIGNAL_NOCHANNEL)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_CHANNEL;
        }
        else if(i == NOTIFY_SIGNAL_NOCICARD)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_CI;
        }
        else if(i == NOTIFY_SIGNAL_DETECTED_SIGNAL)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNSTABLE;
        }
        else if(i == NOTIFY_SIGNAL_AUDIO_ONLY)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_AUDIO_ONLY;
        }
        else if(i == NOTIFY_SIGNAL_NOSIGNAL)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_SIGNAL;
        }
        else if(i == NOTIFY_SIGNAL_UNSUPPORT)
        {
            service = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNSUPPORT_SIGNAL;
        }
        Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SCREEN_SERVER_MODE_CHANGE.ordinal(),service.ordinal());
        mObj_EventManager.sendBroadcast(info);
        return 0;
    }

    @Override
    public int notifyATSCEventMessage(int i, int i1, int i2, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyOpenVCHIPMessage(int i, int i1, int i2, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyNoUsedkeyMessage(int i, int i1, int i2, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifySimulated3dAutoTurnOff() throws RemoteException {
        return 0;
    }

    @Override
    public int notifySleepTimerChange(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftNotification(int i, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftRecordStatus(int i, long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftNoDiskFile(long l) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftSpeedUpdate(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftPlaybackStatusUpdate(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyTimeshiftStorageRemoved() throws RemoteException {
        return 0;
    }

    @Override
    public int notifyGpioStatus(int i, int i1) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyUARTSerialPortCallback(int i, int i1, int i2, byte[] bytes) {
        return 0;
    }

    @Override
    public int notifySubtitleMsg(int i, int i1, int i2, int i3) throws RemoteException {
        Log.d("Oceanus_Notify","notifySubtitleMsg: " + i + "|" + i1 + "|" + i2 + "|" + i3);
        return 0;
    }

    @Override
    public int notifyInputSignalChanged(int i, boolean b) throws RemoteException {
        Log.d("Oceanus_Notify","notifyInputSignalChanged: " + i + "|" + b);
        return 0;
    }

    @Override
    public int notifyVdpMuteAllFinished(int i) throws RemoteException {
        return 0;
    }

    @Override
    public int notifyConfigValuechanged(String s) throws RemoteException {
        return 0;
    }
}
