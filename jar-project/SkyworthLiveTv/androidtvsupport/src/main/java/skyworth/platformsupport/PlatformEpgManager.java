package skyworth.platformsupport;

import android.database.Cursor;
import android.media.tv.TvContract;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.android.media.tv.companionlibrary.model.Program;
import com.platform.IPlatformEpgManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.EpgManager.EPGEventFilter;
import Oceanus.Tv.Service.EpgManager.EpgEvent;
import Oceanus.Tv.Service.EpgManager.EpgManager;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EPG_EVENT_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EPG_FILTER_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgManagerDefinitions.EN_EVENT_CONTENT_TYPE;
import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.TimeManager.TimeManager;
import skyworth.platformsupport.componentSupport.TifChannelInfo;

import static com.platform.CommonDefinitions.DEBUG_TAG;
import static skyworth.platformsupport.PlatformSourceManager.TIF_OBJ;

/**
 * Created by sky057509 on 2017/6/5.
 */

@RequiresApi(api = Build.VERSION_CODES.N)
public class PlatformEpgManager implements IPlatformEpgManager {
    private static  PlatformEpgManager m_pThis = null;
    private EpgManager m_pEpgManager = null;
    static public IPlatformEpgManager getInstance()
    {
        if(m_pThis == null)
        {
            new PlatformEpgManager();
        }
        return m_pThis;
    }
    private PlatformEpgManager()
    {
        m_pThis = this;
        m_pEpgManager = EpgManager.getInstance();
        Log.d(DEBUG_TAG,"PlatformEpgManager Create!");
    }
    @Override
    public boolean setEventLanguage(String firstEvtLang, String secondEvtLang) {
        return m_pEpgManager.setEventLanguage(firstEvtLang,secondEvtLang);
    }

    @Override
    public EpgEvent getPresentEvent(Channel channel) {
        if(channel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
        {
            List<EpgEvent> epgList =  getEventsByChannel(channel, TimeManager.getInstance().getSystemTime().getTime(),EPG_ONE_HOUR_MILLISECOND);
            if(epgList!=null&& epgList.size()>0)
            {
                return epgList.get(0);
            }
            return null;
        }
        return m_pEpgManager.getPresentEvent(channel);
    }

    @Override
    public EpgEvent getFollowEvent(Channel channel) {
        if(channel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
        {
            List<EpgEvent> epgList =  getEventsByChannel(channel,TimeManager.getInstance().getSystemTime().getTime(),EPG_ONE_HOUR_MILLISECOND);
            if(epgList!=null&& epgList.size()>0)
            {
                Date currentEndTime = epgList.get(0).getEndTime();
                for(EpgEvent event:epgList)
                    {
                        if(currentEndTime.getTime() == event.getStartTime().getTime())
                        {
                            return event;
                        }
                    }
            }
            return null;
        }
        return m_pEpgManager.getFollowEvent(channel);
    }
    @Nullable
    @Override
    public List<EpgEvent> getEventsByChannel(Channel channel,long startTime,long duration) {
        if(channel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
        {
            startTime = TimeManager.getInstance().getSystemTime().getTime();
            List<EpgEvent> eventList = new ArrayList<>();
            TifChannelInfo tifChannelInfo = (TifChannelInfo) channel.getDtvAttr().getOtherInfo(TIF_OBJ);
            Cursor c;
            c = PlatformManager.getInstance().GetApplicationContext().getContentResolver().query(
                    TvContract.buildProgramsUriForChannel(tifChannelInfo.getId(), startTime,startTime+duration),
                    Program.PROJECTION, null, null, TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS);
            while (c != null && c.moveToNext())
            {
                Program program = Program.fromCursor(c);
                eventList.add(toEpgEvent(program));
            }
            return eventList;
        }
        else
        {
            return m_pEpgManager.getEvents(channel,startTime,duration);
        }
    }
    @Nullable
    @Override
    public List<EpgEvent> getEventsByFilter(EPGEventFilter filter) {
        List<EpgEvent> eventList = new ArrayList<>();
        if(filter.getmFilterType() == EN_EPG_FILTER_TYPE.EN_EPG_FILTER_BY_CHANNEL)
        {
            if(filter.getStartTime()!=null&&filter.getEndTime()!=null)
            {
                eventList = getEventsByChannel(filter.getChannel(),filter.getStartTime().getTime(),filter.getEndTime().getTime());
            }
            else if(filter.getStartTime()==null&&filter.getEndTime()==null)
            {

                eventList = getEventsByChannel(filter.getChannel(),filter.getStartTime().getTime(),filter.getEndTime().getTime());
            }
            else if(filter.getWeekday()!=null)
            {
                return eventList;
            }
        }
        else if(filter.getmFilterType() == EN_EPG_FILTER_TYPE.EN_EPG_FILTER_BY_CONTENT_TYPE)
        {
            String type = null;
            switch (filter.getContentType())
            {
                case EN_EVENT_CONTENT_ARTS:
                    type = TvContract.Programs.Genres.ARTS;
                    break;
                case EN_EVENT_CONTENT_ANIMAL_WILDLIFE:
                    type = TvContract.Programs.Genres.ANIMAL_WILDLIFE;
                    break;
                case EN_EVENT_CONTENT_COMEDY:
                    type = TvContract.Programs.Genres.COMEDY;
                    break;
                case EN_EVENT_CONTENT_DRAMA:
                    type = TvContract.Programs.Genres.DRAMA;
                    break;
                case EN_EVENT_CONTENT_EDUCATION:
                    type = TvContract.Programs.Genres.EDUCATION;
                    break;
                case EN_EVENT_CONTENT_ENTERTAINMENT:
                    type = TvContract.Programs.Genres.ENTERTAINMENT;
                    break;
                case EN_EVENT_CONTENT_FAMILY_KIDS:
                    type = TvContract.Programs.Genres.FAMILY_KIDS;
                    break;
                case EN_EVENT_CONTENT_GAME:
                    type = TvContract.Programs.Genres.GAMING;
                    break;
                case EN_EVENT_CONTENT_LIFE_STYLE:
                    type = TvContract.Programs.Genres.LIFE_STYLE;
                    break;
                case EN_EVENT_CONTENT_MOVE:
                    type = TvContract.Programs.Genres.MOVIES;
                    break;
                case EN_EVENT_CONTENT_MUSIC:
                    type = TvContract.Programs.Genres.MUSIC;
                    break;
                case EN_EVENT_CONTENT_NEWS:
                    type = TvContract.Programs.Genres.NEWS;
                    break;
                case EN_EVENT_CONTENT_SPORT:
                    type = TvContract.Programs.Genres.SPORTS;
                    break;
                case EN_EVENT_CONTENT_PREMIER:
                    type = TvContract.Programs.Genres.PREMIER;
                    break;
                case EN_EVENT_CONTENT_TECH_SCIENCE:
                    type = TvContract.Programs.Genres.TECH_SCIENCE;
                    break;
                case EN_EVENT_CONTENT_TRAVEL:
                    type = TvContract.Programs.Genres.TRAVEL;
                    break;
                default:
                    break;
            }
            if(type !=null)
            {
                type = "WHERE "+TvContract.Programs.COLUMN_CANONICAL_GENRE + " = " + type;
            }
            for(Source currentSource:PlatformSourceManager.getInstance().GetSourceList())
            {
                List<Channel> channelList = PlatformChannelManager.getInstance().GetChannelList(currentSource,0,0);
                if (channelList != null) {
                    for(Channel channel:channelList)
                        {
                            if(channel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
                                Log.d(DEBUG_TAG, "QUREY EPG FOR: " + channel.getName());
                                TifChannelInfo tifChannelInfo = (TifChannelInfo) filter.getChannel().getDtvAttr().getOtherInfo(TIF_OBJ);
                                Cursor c = PlatformManager.getInstance().GetApplicationContext().getContentResolver().query(
                                        TvContract.buildProgramsUriForChannel(tifChannelInfo.getId()),
                                        Program.PROJECTION, type, null, TvContract.Programs.COLUMN_CANONICAL_GENRE);
                                while (c != null && c.moveToNext()) {
                                    Program program = Program.fromCursor(c);
                                    eventList.add(toEpgEvent(program));
                                }
                            }
                            else
                            {
                                Log.d(DEBUG_TAG,"add logic here to solve live tv epgs");
                            }
                        }
                }
            }
        }
        else if(filter.getmFilterType() == EN_EPG_FILTER_TYPE.EN_EPG_FILTER_BY_TIME)
        {
            for(Source currentSource:PlatformSourceManager.getInstance().GetSourceList())
            {
                List<Channel> channelList = PlatformChannelManager.getInstance().GetChannelList(currentSource,0,0);
                if (channelList != null) {
                    for(Channel channel:channelList)
                    {
                        Log.d(DEBUG_TAG,"QUREY EPG FOR: "+ channel.getName());
                        if(channel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
                            TifChannelInfo tifChannelInfo = (TifChannelInfo) filter.getChannel().getDtvAttr().getOtherInfo(TIF_OBJ);
                            Cursor c = PlatformManager.getInstance().GetApplicationContext().getContentResolver().query(
                                    TvContract.buildProgramsUriForChannel(tifChannelInfo.getId(), filter.getStartTime().getTime(), filter.getEndTime().getTime()),
                                    Program.PROJECTION, null, null, TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS);
                            while (c != null && c.moveToNext()) {
                                Program program = Program.fromCursor(c);
                                eventList.add(toEpgEvent(program));
                            }
                        }
                        else
                        {
                            Log.d(DEBUG_TAG,"add logic here to solve live tv epgs");
                        }
                    }
                }
            }
        }
        return eventList;
    }
    private EpgEvent toEpgEvent(Program program)
    {
        EpgEvent epgEvent = new EpgEvent(EN_EPG_EVENT_TYPE.EN_EPG_ANDROID_TIF);
        epgEvent.setmProgramName(program.getTitle());
        epgEvent.setEventName(program.getEpisodeTitle());
        epgEvent.setEventId(program.getId());
        epgEvent.setChannelIndex(program.getChannelId());
        epgEvent.setLongDescription(program.getLongDescription());
        epgEvent.setShortDescription(program.getDescription());
        epgEvent.setStartTime(new Date(program.getStartTimeUtcMillis()));
        epgEvent.setEndTime(new Date(program.getEndTimeUtcMillis()));
        epgEvent.setDuration(program.getEndTimeUtcMillis()-program.getStartTimeUtcMillis());
        String[] ContentTypes = program.getCanonicalGenres();
        if(ContentTypes!=null)
        {
            if(ContentTypes.length>0)
            {
                EN_EVENT_CONTENT_TYPE[] content_types = new EN_EVENT_CONTENT_TYPE[ContentTypes.length];
                for (int i =0;i<ContentTypes.length;i++)
                {
                    switch (ContentTypes[i]) {
                        case TvContract.Programs.Genres.ANIMAL_WILDLIFE:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_ANIMAL_WILDLIFE;
                            break;
                        case TvContract.Programs.Genres.ARTS:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_ARTS;
                            break;
                        case TvContract.Programs.Genres.COMEDY:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_COMEDY;
                            break;
                        case TvContract.Programs.Genres.DRAMA:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_DRAMA;
                            break;
                        case TvContract.Programs.Genres.EDUCATION:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_EDUCATION;
                            break;
                        case TvContract.Programs.Genres.ENTERTAINMENT:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_ENTERTAINMENT;
                            break;
                        case TvContract.Programs.Genres.FAMILY_KIDS:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_FAMILY_KIDS;
                            break;
                        case TvContract.Programs.Genres.GAMING:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_GAME;
                            break;
                        case TvContract.Programs.Genres.LIFE_STYLE:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_LIFE_STYLE;
                            break;
                        case TvContract.Programs.Genres.MOVIES:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_MOVE;
                            break;
                        case TvContract.Programs.Genres.MUSIC:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_MUSIC;
                            break;
                        case TvContract.Programs.Genres.NEWS:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_NEWS;
                            break;
                        case TvContract.Programs.Genres.PREMIER:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_NEWS;
                            break;
                        case TvContract.Programs.Genres.SHOPPING:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVNET_CONTENT_SHOPPING;
                            break;
                        case TvContract.Programs.Genres.SPORTS:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_SPORT;
                            break;
                        case TvContract.Programs.Genres.TECH_SCIENCE:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_TECH_SCIENCE;
                            break;
                        case TvContract.Programs.Genres.TRAVEL:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_TRAVEL;
                            break;
                        default:
                            content_types[i] = EN_EVENT_CONTENT_TYPE.EN_EVENT_CONTENT_OTHER;
                    }
                }
                epgEvent.setContentType(content_types);
            }
            epgEvent.putOtherInfo(Android_Tv_PosterArtUri,program.getPosterArtUri());
            epgEvent.putOtherInfo(Android_Tv_ThumbnailUri,program.getThumbnailUri());
            epgEvent.putOtherInfo(Android_Tv_InternalProviderData,program.getInternalProviderData());
        }
        return epgEvent;
    }

}
