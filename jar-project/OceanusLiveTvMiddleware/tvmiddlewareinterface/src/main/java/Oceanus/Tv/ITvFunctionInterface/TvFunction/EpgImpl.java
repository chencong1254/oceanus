package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvEvent;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.common.MtkTvExceptionBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventGroupBase;
import com.mediatek.twoworlds.tv.model.MtkTvEventInfoBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.IEpg;
import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.EpgManager.EPGEventFilter;
import Oceanus.Tv.Service.EpgManager.EpgEvent;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EPG_EVENT_TYPE;

/**
 * Created by xeasy on 2016/12/12.
 */

public class EpgImpl implements IEpg {

    /* MarketRegion Info */
    private final int REGION_NULL = -1;
    private final int REGION_CN = 0;
    private final int REGION_US = 1;
    private final int REGION_SA = 2;
    private final int REGION_EU = 3;
    private int region = REGION_NULL;

    @Override
    public EpgEvent getEventByEventId(Channel channel, int eventId) {
        int currentRegion = getCurrentMarketRegion();
        MtkTvEventInfoBase epgInfo = null;
        switch (currentRegion) {
            case REGION_EU:
            case REGION_SA:
                epgInfo = MtkTvEvent.getInstance().getEventInfoByEventId(eventId, channel.getDbIndex());
                return transToEpgEvent(epgInfo);
            case REGION_CN:
                epgInfo = MtkTvEvent.getInstance().getEventInfoByEventId(eventId, channel.getDbIndex());
                return transToEpgEvent(epgInfo);
            case REGION_US:
                break;
            default:
                break;
        }
        return null;
    }

    /*
* get current date day as Second
*/
    public static long getCurrentDateDayAsMills(){
        MtkTvTimeFormatBase mtkTvTimeFormatBase =
                        MtkTvTime.getInstance().getBroadcastTime();
        long curTimeMillSeconds = mtkTvTimeFormatBase.toMillis();
        long result = curTimeMillSeconds - mtkTvTimeFormatBase.hour * 60 * 60 - mtkTvTimeFormatBase.minute * 60 - mtkTvTimeFormatBase.second;
        Log.d("Oceanus","getCurrentDateDayAsMills:"+result);
        return result;
    }

    public static long getCurrentDateDayAsMills1(){
        MtkTvTimeFormatBase mtkTvTimeFormatBase =
                MtkTvTime.getInstance().getLocalTime();
        long curTimeMillSeconds = mtkTvTimeFormatBase.toMillis();
        long result = curTimeMillSeconds - mtkTvTimeFormatBase.hour * 60 * 60 - mtkTvTimeFormatBase.minute * 60 - mtkTvTimeFormatBase.second;
        Log.d("Oceanus","getCurrentDateDayAsMills1:"+result);
        return result;
    }
    @Override
    public List<EpgEvent> getEvents(Channel channel, long startTime, long duration) {
        int currentRegion = getCurrentMarketRegion();
        List<EpgEvent> epgEventsList = new ArrayList<EpgEvent>();
        switch (currentRegion) {
            case REGION_EU:
            case REGION_SA:
                try {
                 List<MtkTvEventInfoBase> epgInfoList = MtkTvEvent.getInstance().getEventListByChannelId(channel.getDbIndex(), getCurrentDateDayAsMills(), getCurrentDateDayAsMills()+duration);
                    if(epgInfoList ==null)
                    {
                        Log.d("Oceanus", "getEvents epgInfoList ==null");
                    }else {
                        Log.d("Oceanus", "getEvents epgInfoList size:" + epgInfoList.size());
                    }
                    if (epgInfoList != null && epgInfoList.size() > 0) {
                        for (MtkTvEventInfoBase eventInfoTmp : epgInfoList) {
                            epgEventsList.add(transToEpgEvent(eventInfoTmp));
                        }
                    }
                } catch (MtkTvExceptionBase mtkTvExceptionBase) {
                    Log.d("Oceanus", "getEvents MtkTvExceptionBasel");
                    mtkTvExceptionBase.printStackTrace();
                }
            case REGION_CN:
                try {
                    List<MtkTvEventInfoBase> epgInfoList = MtkTvEvent.getInstance().getEventListByChannelId(channel.getDbIndex(), startTime, duration);
                    if (epgInfoList != null && epgInfoList.size() > 0) {
                        for (MtkTvEventInfoBase eventInfoTmp : epgInfoList) {
                            epgEventsList.add(transToEpgEvent(eventInfoTmp));
                        }
                    }
                } catch (MtkTvExceptionBase mtkTvExceptionBase) {
                    mtkTvExceptionBase.printStackTrace();
                }
            case REGION_US:

                break;
            default:
                break;
        }
        return epgEventsList;
    }

    @Override
    public EpgEvent getPresentEvent(Channel channel) {
        int currentRegion = getCurrentMarketRegion();
        MtkTvEventInfoBase epgInfo = null;
        switch (currentRegion) {
            case REGION_EU:
            case REGION_SA:
//                epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(5243009, true);
                  epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(5243009, true);
                return transToEpgEvent(epgInfo);
            case REGION_CN:
                epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(channel.getDbIndex(), true);
                return transToEpgEvent(epgInfo);
            case REGION_US:

                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public EpgEvent getFollowEvent(Channel channel) {
        int currentRegion = getCurrentMarketRegion();
        MtkTvEventInfoBase epgInfo = null;
        switch (currentRegion) {
            case REGION_EU:
            case REGION_SA:
//                epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(5243009, false);
                 epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(channel.getDbIndex(), false);
                return transToEpgEvent(epgInfo);
            case REGION_CN:
                epgInfo = MtkTvEvent.getInstance().getPFEventInfoByChannel(channel.getDbIndex(), false);
                return transToEpgEvent(epgInfo);
            case REGION_US:

                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public List<EpgEvent> getEvents(EPGEventFilter filter, int offset, int number) {
        return null;
    }

    @Override
    public boolean startEpg(Channel channel) {
        return false;
    }

    @Override
    public boolean stopEpg() {
        return false;
    }

    @Override
    public boolean setEventLang(String firstEvtLang, String secondEvtLang) {
        return false;
    }
    @Override
    public String getTvProgramTime()
    {
        Log.d("Oceanus","Current getTvProgramTime: "+ MtkTvBanner.getInstance().getProgTime());
        return MtkTvBanner.getInstance().getProgTime();
    }
    @Override
    public String getNextProgramTitle()
    {
        return MtkTvBanner.getInstance().getNextProgTitle();
    }
    @Override
    public String getNextProgramTime()
    {
        return MtkTvBanner.getInstance().getNextProgTime();
    }
    @Override
    public String getCurrentProgramDetial()
    {
        return MtkTvBanner.getInstance().getProgDetail();
    }

    @Override
    public String getProgramTitle() {
        return MtkTvBanner.getInstance().getProgTitle();
    }

    private int getCurrentMarketRegion() {
        if (region == REGION_NULL) {
            initMarketRegion();
        }
        Log.d("Oceanus", "getCurrentMarketRegion region:" + region);
        return region;
    }
    private void initMarketRegion() {
        region = REGION_EU;
    }
    private EpgEvent transToEpgEvent(MtkTvEventInfoBase mktInfo) {
        EpgEvent epgEvent = new EpgEvent(EN_EPG_EVENT_TYPE.EN_EPG_LIVE_TV);
        if (mktInfo == null) {
            Log.d("Oceanus", "transToEpgEvent mktInfo null:");
            return null;
        }
        Log.d("Oceanus", "mktInfo getEventDetail:" + mktInfo.getEventDetail());
        Log.d("Oceanus", "getEventDetailExtend:" + mktInfo.getEventDetailExtend());
        Log.d("Oceanus", "getEventTitle:" + mktInfo.getEventTitle());
        Log.d("Oceanus", "getCaSystemId:" + mktInfo.getCaSystemId());
        Log.d("Oceanus", "getChannelId:" + mktInfo.getChannelId());
        Log.d("Oceanus", "getDuration:" + mktInfo.getDuration());
        int[] categorys = mktInfo.getEventCategory();
        for (int category : categorys) {
            Log.d("Oceanus", "getEventCategory:" + category);
        }
        Log.d("Oceanus", "getEventCategoryNum:" + mktInfo.getEventCategoryNum());
        MtkTvEventGroupBase[] groupBases = mktInfo.getEventGroup();
        for (MtkTvEventGroupBase groupBase : groupBases) {
            Log.d("Oceanus", "getEventGroup:" + groupBase.toString());
        }
        Log.d("Oceanus", "getEventId:" + mktInfo.getEventId());
        Log.d("Oceanus", "getEventLinkage:" + mktInfo.getEventLinkage());
        Log.d("Oceanus", "getEventRating:" + mktInfo.getEventRating());
        Log.d("Oceanus", "getEventSeries:" + mktInfo.getEventSeries());
        Log.d("Oceanus", "getGuidanceMode:" + mktInfo.getGuidanceMode());
        Log.d("Oceanus", "getGuidanceText:" + mktInfo.getGuidanceText());
        Log.d("Oceanus", "getStartTime:" + mktInfo.getStartTime());
        Log.d("Oceanus", "getSvlId:" + mktInfo.getSvlId());
        Log.d("Oceanus", "isFreeCaMode:" + mktInfo.isFreeCaMode());
        epgEvent.setStartTime(new Date(mktInfo.getStartTime()));
        epgEvent.setEndTime(new Date(mktInfo.getStartTime() + mktInfo.getDuration()));
        epgEvent.setDuration(mktInfo.getDuration());
        epgEvent.setEventName(mktInfo.getEventTitle());
        epgEvent.setEventId(mktInfo.getEventId());
        epgEvent.setFreeCA(mktInfo.isFreeCaMode());
        epgEvent.setLongDescription(mktInfo.getEventDetail());
        epgEvent.setChannelIndex(mktInfo.getChannelId());
        return epgEvent;
    }
}