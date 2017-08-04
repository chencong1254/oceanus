package Oceanus.Tv.Service.EpgManager;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.EpgImpl;
import Oceanus.Tv.Service.ChannelManager.Channel;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class EpgManager {
    private static final String LOG_TAG = "EpgManager";
    private static EpgManager mObj_EpgManager = null;
    private static EpgImpl mInterface_Epg = null;
    public static EpgManager getInstance()
    {
        synchronized(EpgManager.class)
            {
                if (mObj_EpgManager == null)
                {
                    new EpgManager();
                }
            }
        return mObj_EpgManager;
    }
    private EpgManager()
    {
        Log.d(LOG_TAG,"EpgManager Created~");
        mObj_EpgManager = this;
        mInterface_Epg = new EpgImpl();
    }
    public boolean setEventLanguage(String firstEvtLang,String secondEvtLang)
    {
        return mInterface_Epg.setEventLang(firstEvtLang,secondEvtLang);
    }
    public EpgEvent getPresentEvent(Channel channel)
    {
        EpgEvent preEvent = null;
        if(channel  != null) {
            preEvent = mInterface_Epg.getPresentEvent(channel);
        }
        return preEvent;
    }
    public EpgEvent getFollowEvent(Channel channel)
    {
        EpgEvent followEvent = null;
        if(channel!=null)
        {
            followEvent = mInterface_Epg.getFollowEvent(channel);
        }
        return followEvent;
    }

    public List<EpgEvent> getEvents(Channel channel, long startTime, long duration)
    {
        List<EpgEvent> eventsLists = null;
        if(channel!=null)
        {
            eventsLists = mInterface_Epg.getEvents(channel,startTime,duration);
        }
        if(eventsLists ==null)
        {
            eventsLists = new ArrayList<EpgEvent>();
        }
        return  eventsLists;
    }

    public List<EpgEvent> getEvents(EPGEventFilter filter, int offset, int number)
    {
        List<EpgEvent> eventsLists =null;
        if(filter!=null)
        {
            eventsLists = mInterface_Epg.getEvents(filter,offset,number);
        }
        if(eventsLists == null)
        {
            eventsLists = new ArrayList<EpgEvent>();
        }
        return  eventsLists;
    }

    public EpgEvent getEventByEventId(Channel channel, int eventId)
    {
        EpgEvent event = null;
        if(channel!=null)
        {
            event = mInterface_Epg.getEventByEventId(channel, eventId);
        }
        return event;
    }
    public String getCurrentChannelProgramTime() {
        return mInterface_Epg.getTvProgramTime();
    }

    public String getNextProgramTitle() {
        return mInterface_Epg.getNextProgramTitle();
    }

    public String getNextProgramTime() {
        return mInterface_Epg.getNextProgramTime();
    }

    public String getCurrentProgramDetial()
    {
        return mInterface_Epg.getCurrentProgramDetial();
    }
    public String getCurrentProgramTitle()
    {
        return mInterface_Epg.getProgramTitle();
    }

 /*
    private native String getEventById(int channelIndex,int eventId);
    private native String getEvents(int channelIndex,int startTime,int duration);
    private native String getPresentEvent(int channelIndex);
    private native String getFollowEvent(int channelIndex);
    private native boolean startEpg(int channelIndex);
    private native boolean stopEpg();
    private native boolean setEventLang(String firstEvtLang, String secondEvtLang);
    private native void Connect();
    private native void Disconnect();
    */
}
