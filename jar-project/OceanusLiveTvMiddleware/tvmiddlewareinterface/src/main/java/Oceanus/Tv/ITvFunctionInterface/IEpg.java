package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.EpgManager.EPGEventFilter;
import Oceanus.Tv.Service.EpgManager.EpgEvent;

/**
 * Created by xeasy on 2016/12/12.
 */

public interface IEpg {
    public abstract EpgEvent getEventByEventId(Channel channel, int eventId);
    public abstract List<EpgEvent> getEvents(Channel channel,long startTime,long duration);
    public abstract EpgEvent getPresentEvent(Channel channel);
    public abstract EpgEvent getFollowEvent(Channel channel);
    public abstract List<EpgEvent> getEvents(EPGEventFilter filter, int offset, int number);
    public abstract boolean startEpg(Channel channel);
    public abstract boolean stopEpg();
    public abstract boolean setEventLang(String firstEvtLang, String secondEvtLang);
    String getTvProgramTime();
    String getNextProgramTitle();
    String getNextProgramTime();
    String getCurrentProgramDetial();
    String getProgramTitle();
}
