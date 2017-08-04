package skyworth.skyworthlivetv.osd.ui.menu.channel.Listener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManager;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.AtvScanResult;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.DtvScanResult;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.EventManager.Tv_EventListener;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import skyworth.skyworthlivetv.osd.common.CommonConst;

/**
 * Created by yangjianjun on 2017/5/4.
 */

public class ChannelScanListener extends Tv_EventListener {
    private static final String LOG_TAG = ChannelScanListener.class.getSimpleName();

    private Handler messageHandler;
    private ChannelManager channelManager;

    public Handler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    public ChannelScanListener(String name, Handler handler) {
        super(name);
        this.messageHandler = handler;
        this.channelManager = ChannelManager.getInstance();
    }

    @Override
    protected void onEvnet(Tv_EventInfo tv_eventInfo) {
        Log.i("Oceanus", tv_eventInfo.getJsonObject().toString());
        if (tv_eventInfo.getEventType() == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN.ordinal()) {
            AtvScanResult result = null;
            try {
                result = new AtvScanResult(tv_eventInfo.getJsonObject());
                Log.d(LOG_TAG, "Precent:[" + result.getPercent() + "]");
                Log.d(LOG_TAG, "CurScanedChNum:[" + result.getCurScanedChNum() + "]");
                Log.d(LOG_TAG, "FrequencyKhz:[" + result.getFreq() + "]");
                Log.d(LOG_TAG, "ScanedChNum:[" + result.getScanedChNum() + "]");
                Log.d(LOG_TAG, "Mode:[" + result.getMode().toString() + "]");
                Message msg = Message.obtain();
                msg.what = CommonConst.m_atv_step;
                msg.obj = result;
                this.messageHandler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tv_eventInfo.getEventType() == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_AUTO_SCAN.ordinal()
                || tv_eventInfo.getEventType() == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_MANUAL_SCAN.ordinal()) {
            DtvScanResult result = null;
            try {
                result = new DtvScanResult(tv_eventInfo.getJsonObject());
                Log.d(LOG_TAG, "Precent:[" + result.getPercent() + "]");
                Log.d(LOG_TAG, "CurScanedChNum:[" + result.getCurScanedChNum() + "]");
                Log.d(LOG_TAG, "CurScanedDTVNum:[" + result.getCurScanedDtvNum() + "]");
                Log.d(LOG_TAG, "CurScanedRADIONum:[" + result.getCurScanedRadioNum() + "]");
                Log.d(LOG_TAG, "CurScanedDATANum:[" + result.getCurScanedDataNum() + "]");
                Log.d(LOG_TAG, "FrequencyKhz:[" + result.getFreq() + "]");
                Log.d(LOG_TAG, "ScanedChNum:[" + result.getScanedChNum() + "]");
                Message msg = Message.obtain();
                msg.what = CommonConst.m_dtv_step;
                msg.arg1 = result.getCurScanedChNum();
                msg.obj = result;
                this.messageHandler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tv_eventInfo.getEventType() == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_ATV_SCAN_DONE.ordinal()) {
            Log.d("Oceanus", "E_SYSTEM_EVENT_ATV_SCAN_DONE~~~");
            try {
                if (tv_eventInfo.getJsonObject().getInt("mode") == EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP.ordinal()) {
                    Log.d("Oceanus", "EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP");
                    //AtvScanResult result = null;
                    //result = new AtvScanResult(tv_eventInfo.getJsonObject());
                    //Channel channel = new Channel(EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV);
                    //channel.setFreq(result.getFreq());
                    //channelManager.gotoChannel(channel);
                }
                channelManager.playFristChannel(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV);
                Message msg = Message.obtain();
                msg.what = CommonConst.m_atv_scan_done;
                this.messageHandler.sendMessage(msg);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (tv_eventInfo.getEventType() == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_DTV_SCAN_DONE.ordinal()) {
            Log.d("Oceanus", "E_SYSTEM_EVENT_DTV_SCAN_DONE~~~");
            try {
                EN_CHANNEL_LIST_TYPE ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_NONE;
                switch (SourceManager.getInstance().getCurSource().getType()) {
                    case E_INPUT_SOURCE_ATV: {
                        ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV;
                    }
                    break;
                    case E_INPUT_SOURCE_DTV_DVB_T: {
                        ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT;
                    }
                    break;
                    case E_INPUT_SOURCE_DTV_DVB_C: {
                        ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC;
                    }
                    break;
                    default: {
                        ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL;
                    }
                    break;
                }
                boolean ret = channelManager.playFristChannel(ListType);
                Log.d("Oceanus", "resualt:" + ret);
                Message msg = Message.obtain();
                msg.what = CommonConst.m_dtv_scan_done;
                this.messageHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d("Oceanus", "Unknow event");
        }
    }
}
