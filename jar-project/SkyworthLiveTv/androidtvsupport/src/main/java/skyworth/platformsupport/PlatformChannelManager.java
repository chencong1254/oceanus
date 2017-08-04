package skyworth.platformsupport;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.platform.IPlatformChannelManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelListInfo;
import Oceanus.Tv.Service.ChannelManager.ChannelManager;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.EventManager.Tv_EventListener;
import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import skyworth.platformsupport.componentSupport.PlatformTvActivity;
import skyworth.platformsupport.componentSupport.PlatformTvView;
import skyworth.platformsupport.componentSupport.TifChannelInfo;
import skyworth.platformsupport.util.SPUtil;

import static com.platform.CommonDefinitions.DEBUG_TAG;
import static skyworth.platformsupport.PlatformSourceManager.TIF_OBJ;

/**
 * Created by sky057509 on 2017/5/3.
 */

public class PlatformChannelManager extends Tv_EventListener implements IPlatformChannelManager {
    private static final String ORDERBY = "substr(cast(" +
            TvContract.Channels.COLUMN_INTERNAL_PROVIDER_DATA + " as varchar),19,10)";
    private static final String CUR_TIF_CHANNEL_POSITION_KEY = "cur_tif_channel_id_key";
    private static PlatformChannelManager m_pThis = null;
    private static ContentResolver mContextContentResolver;
    private static Channel m_pCurrentAppChannel = null;
    private static Channel m_pLastAppChannel = null;
    private static Cursor m_pCurrentSwitchAppChannelListCursor = null;
    private PlatformChannelManager() {
        super("PlatformChanneManagerEventListener");
        m_pThis = this;
        mContextContentResolver = PlatformManager.getInstance().GetApplicationContext().getContentResolver( );
        EventManager.getInstance().registeEventListener(this,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SOURCE_CHANGE);
        Source currentSource = PlatformSourceManager.getInstance().GetCurrentSource();
        if(currentSource!=null)
        {
            if(currentSource.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
            {
                if(ResetAppSwitchChannelCursorBySource(currentSource)<0)
                {
                    Log.d(DEBUG_TAG,"Current channel input source have no channel !");
                }
            }
        }

    }
    private boolean ResetCursor(String inputId)
    {
        if(m_pCurrentSwitchAppChannelListCursor!=null)
        {
            m_pCurrentSwitchAppChannelListCursor.close();
            m_pCurrentSwitchAppChannelListCursor = null;
        }
        String[] selectionArgs = {inputId};
        m_pCurrentSwitchAppChannelListCursor = mContextContentResolver.query(TvContract.Channels.CONTENT_URI,TifChannelInfo.TifChannelProject,
                TvContract.Channels.COLUMN_INPUT_ID + " = ?",
                selectionArgs, null);
        if (m_pCurrentSwitchAppChannelListCursor == null) {
            Log.e(DEBUG_TAG,"ResetAppSwitchChannelList Error can't get m_pCurrentSwitchAppChannelListCursor,maby you are not set up!");
            m_pCurrentAppChannel = null;
            m_pLastAppChannel = null;
            return false;
        }
        Log.d(DEBUG_TAG,"ResetCursor Success! ");
        return true;
    }
    @TargetApi(Build.VERSION_CODES.M)
    private int ResetAppSwitchChannelListByChannel(Channel channel) {
        if (m_pCurrentSwitchAppChannelListCursor != null) {
            TifChannelInfo tifChannel = (TifChannelInfo) channel.getDtvAttr().getOtherInfo(TIF_OBJ);
            String currentInputId = m_pCurrentSwitchAppChannelListCursor.getString(m_pCurrentSwitchAppChannelListCursor.getColumnIndex(TvContract.Channels.COLUMN_INPUT_ID));
            if (tifChannel.getInputId().compareTo(currentInputId) != 0) {
                if(!ResetCursor(tifChannel.getInputId()))
                {
                    Log.e(DEBUG_TAG,"Reset cursor false!");
                    return -1;
                }
            }
            m_pCurrentSwitchAppChannelListCursor.moveToFirst();
            while (m_pCurrentSwitchAppChannelListCursor.move(1)) {
                long id = m_pCurrentSwitchAppChannelListCursor.getLong(m_pCurrentSwitchAppChannelListCursor.getColumnIndex(TvContract.Channels._ID));
                if (id == tifChannel.getId()) {
                    break;
                }
            }
            if(m_pCurrentSwitchAppChannelListCursor.isAfterLast())
            {
                m_pCurrentSwitchAppChannelListCursor.moveToFirst();
                return 0;
            }
            m_pLastAppChannel = m_pCurrentAppChannel;
            m_pCurrentAppChannel = CreateTifAppChannel(m_pCurrentSwitchAppChannelListCursor);
            return m_pCurrentSwitchAppChannelListCursor.getPosition();
        }
        return -1;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private int ResetAppSwitchChannelCursorBySource(Source source)
    {
        Log.d(DEBUG_TAG,"Current source: " + source.getName());
        int pos = GetChannelPositionBySource(source);
        if(pos<0)
        {
            m_pCurrentAppChannel = null;
            m_pLastAppChannel = null;
            return -1;
        }
        TvInputInfo tifInfo = (TvInputInfo)source.getOtherInfo(TIF_OBJ);
        if(ResetCursor(tifInfo.getId()))
        {
            m_pCurrentSwitchAppChannelListCursor.moveToPosition(pos);
            m_pLastAppChannel = m_pCurrentAppChannel;
            m_pCurrentAppChannel = CreateTifAppChannel(m_pCurrentSwitchAppChannelListCursor);
            Log.d(DEBUG_TAG,"Current position:" + pos);
            return pos;
        }
       return -1;
    }

    @Override
    protected void finalize() throws Throwable {
        EventManager.getInstance().unregisteEventListener(this,EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SOURCE_CHANGE);
        if(m_pCurrentSwitchAppChannelListCursor!=null)
        {
            m_pCurrentSwitchAppChannelListCursor.close();
        }
        super.finalize();
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onEvnet(Tv_EventInfo tv_eventInfo) {
        EN_OSYSTEM_EVENT_LIST event = EN_OSYSTEM_EVENT_LIST.values()[tv_eventInfo.getEventType()];
        EN_INPUT_SOURCE_TYPE sourceType = EN_INPUT_SOURCE_TYPE.values()[(int) tv_eventInfo.getInfoNumber()];
        if(event == EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_SOURCE_CHANGE)
        {
            Log.d(DEBUG_TAG,"Source type: " + sourceType.toString());
            switch (sourceType)
            {
                case E_INPUT_SOURCE_APPLICATION:
                {
                    Log.d(DEBUG_TAG,"Platform Channel Manager revice event Source Channge to app");
                    ResetAppSwitchChannelCursorBySource(PlatformSourceManager.getInstance().GetCurrentSource());
                    GotoChannel(m_pCurrentAppChannel);
                }
                break;
                case E_INPUT_SOURCE_DTV_DVB_T:
                case E_INPUT_SOURCE_DTV_DVB_C:
                case E_INPUT_SOURCE_DTV_ATSC:
                case E_INPUT_SOURCE_DTV_ISDB:
                case E_INPUT_SOURCE_ATV:
                {
                    m_pCurrentAppChannel = null;
                    m_pLastAppChannel = null;
                    Channel currentChannel = GetCurrentChannel();
                    if (currentChannel != null) {
                        GotoChannel(currentChannel);
                    }
                }
                break;
                case E_INPUT_SOURCE_DTV_DVB_S:
                {
                    Log.e(DEBUG_TAG,"Type: "+sourceType.toString()+"is not in this case you must add it first");
                }
                break;
                default:break;
            }
        }
    }

    public static PlatformChannelManager getInstance() {
        if (m_pThis == null) {
            new PlatformChannelManager();
        }
        return m_pThis;
    }
    private List<Channel> GetAppChannelList(Source source)
    {
        List<Channel> list = new ArrayList<>();
        if (source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            TvInputInfo inputInfo = (TvInputInfo) source.getOtherInfo(TIF_OBJ);
            if (hasTIFChannelInfoBySource(inputInfo.getId())) {
                Log.d(DEBUG_TAG, "Source:" + source.getName() + "has channels");
                String[] selectionArgs = {inputInfo.getId()};
                Cursor ChannelResualt = mContextContentResolver.query(TvContract.Channels.CONTENT_URI, TifChannelInfo.TifChannelProject, TvContract.Channels.COLUMN_INPUT_ID + " = ?", selectionArgs, null);
                if (ChannelResualt != null)
                {
                    int count = ChannelResualt.getCount();
                    if (count > 0) {
                        Log.d(DEBUG_TAG, "ChannelResualt number:" + count);
                        while (ChannelResualt.moveToNext())
                        {
                            Channel appChannel = CreateTifAppChannel(ChannelResualt);
                            list.add(appChannel);
                        }
                        ChannelResualt.close();
                    }
                }
            }
        }
        return list;
    }
    private Channel CreateTifAppChannel(Cursor ChannelResult)
    {
        Channel channel = new Channel(EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP);
        new TifChannelInfo(channel,ChannelResult);
        return channel;
    }
    @Override
    public List<Channel> GetChannelList(EN_CHANNEL_LIST_TYPE listType, int startIndex, int number) {

        if(listType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_APP)
        {
            List<Channel> resultList = new ArrayList<>();
            List<Source> sourceList =  PlatformSourceManager.getInstance().GetSourceList();
            for(int i = 0; i<sourceList.size();i++)
            {
                List<Channel> temp = GetAppChannelList(sourceList.get(i));
                if(temp.size()>0)
                {
                    resultList.addAll(temp);
                }
            }
            if(number <=0)
            {
                return resultList;
            }
            return resultList.subList(startIndex,number);
        }
        else
        {
            return ChannelManager.getInstance().getChannelList(listType,startIndex,number);
        }
    }

    @Override
    public List<Channel> GetChannelList(Source source, int startIndex, int number) {
        if(source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            List<Channel> resultList;
            resultList = GetAppChannelList(source);
            if(number <= 0)
            {
                return resultList;
            }
            return resultList.subList(startIndex,startIndex+number-1);
        }
        else
        {
            return ChannelManager.getInstance().getChannelList( GetListTypeBySource(source),startIndex,number);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean GotoChannel(@NonNull Channel channel) {
        Log.d(DEBUG_TAG,"GotoChannel : " + channel.getName() + "--" + channel.getChannelNumber());
        if (channel.getType( ) == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
            final TifChannelInfo tifChannelInfo = (TifChannelInfo) channel.getDtvAttr().getOtherInfo(TIF_OBJ);
            PlatformTvActivity platformTvActivity = PlatformTvActivity.getInstance();
            if(platformTvActivity!=null)
            {
                platformTvActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PlatformTvView view = (PlatformTvView) PlatformManager.getInstance().GetPlatformView();
                        Uri channelUri = ContentUris.withAppendedId(TvContract.Channels.CONTENT_URI,tifChannelInfo.getId());
                        view.tune(tifChannelInfo.getInputId(),channelUri);
                    }
                });
            }
            ResetAppSwitchChannelListByChannel(channel);
            SaveCurTifSourceChannelPosition(tifChannelInfo.getInputId(),m_pCurrentSwitchAppChannelListCursor.getPosition());
            TvInputInfo tvInputInfo = (TvInputInfo) PlatformSourceManager.getInstance().GetCurrentSource().getOtherInfo(TIF_OBJ);
            if(tifChannelInfo.getInputId().compareTo(tvInputInfo.getId()) != 0)
            {
                Source switchSource = ((PlatformSourceManager)PlatformSourceManager.getInstance()).GetSourceByTifId(tifChannelInfo.getInputId());
                if(switchSource!=null)
                {
                    PlatformSourceManager.getInstance().SetSource(switchSource);
                }
            }
            Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_CHANNEL_CHANGE.ordinal());
            EventManager.getInstance().sendBroadcast(info);
            return true;
        } else {
            m_pLastAppChannel = null;
            return ChannelManager.getInstance( ).gotoChannel(channel);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean GotoChannelByNumber(int channelNumber) {
        Source source = PlatformSourceManager.getInstance().GetCurrentSource();
        EN_CHANNEL_LIST_TYPE listtype = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL;
        if (source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV) {
            listtype = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV;
        } else if (source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T) {
            listtype = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT;
        } else if (source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C) {
            listtype = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC;
        } else if (source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION) {
            listtype = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_APP;
        }
        List<Channel> channelList = null;
        if (listtype == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_APP)
        {
            channelList = PlatformChannelManager.getInstance().GetChannelList(source,0,0);
        }
        else
        {
            channelList = PlatformChannelManager.getInstance().GetChannelList(listtype,0,0);
        }
        for(Channel channel:channelList)
        {
            if(channel.getChannelNumber() == channelNumber)
            {
                return GotoChannel(channel);
            }
        }
        return false;
    }
    private boolean NeedResetCursor(String currentTifInputId)
    {
        String cursorInputId = m_pCurrentSwitchAppChannelListCursor.getString(m_pCurrentSwitchAppChannelListCursor.getColumnIndex(TvContract.Channels.COLUMN_INPUT_ID));
        Log.d(DEBUG_TAG,"current source id:" + currentTifInputId);
        Log.d(DEBUG_TAG,"current cursor input id:" + cursorInputId);
        return currentTifInputId.compareTo(cursorInputId) != 0;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void GotoNextChannel() {
        Source currentSource = PlatformSourceManager.getInstance().GetCurrentSource();
        TvInputInfo currentInputInfo = (TvInputInfo) currentSource.getOtherInfo(TIF_OBJ);
        if(currentSource.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            if(NeedResetCursor(currentInputInfo.getId()))
            {
                ResetCursor(currentInputInfo.getId());
            }
            if(m_pCurrentSwitchAppChannelListCursor.isLast())
            {
                m_pCurrentSwitchAppChannelListCursor.moveToFirst();
            }
            else
            {
                m_pCurrentSwitchAppChannelListCursor.moveToNext();
            }
            GotoChannel(CreateTifAppChannel(m_pCurrentSwitchAppChannelListCursor));
        }
        else
        {
            ChannelManager.getInstance().switchChannel( GetListTypeBySource(currentSource),ChannelManager.SWITCH_UP);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void GotoPrevChannel() {
        Source currentSource = PlatformSourceManager.getInstance().GetCurrentSource();
        TvInputInfo currentInputInfo = (TvInputInfo) currentSource.getOtherInfo(TIF_OBJ);
        if(currentSource.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            if(NeedResetCursor(currentInputInfo.getId()))
            {
                ResetCursor(currentInputInfo.getId());
            }
            if(m_pCurrentSwitchAppChannelListCursor.isFirst())
            {
                m_pCurrentSwitchAppChannelListCursor.moveToLast();
            }
            else
            {
                m_pCurrentSwitchAppChannelListCursor.moveToPrevious();
            }
            GotoChannel(CreateTifAppChannel(m_pCurrentSwitchAppChannelListCursor));
        }
        else
        {
            ChannelManager.getInstance().switchChannel( GetListTypeBySource(currentSource),ChannelManager.SWITCH_DOWN);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void GotoLastChannel() {
        if(m_pLastAppChannel != null)
        {
            GotoChannel(m_pLastAppChannel);
        }
        else
        {
            ChannelManager.getInstance().channelReturn();
        }
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public Channel GetCurrentChannel() {
        if(m_pCurrentAppChannel !=null)
        {
            m_pCurrentAppChannel = CreateTifAppChannel(m_pCurrentSwitchAppChannelListCursor);
            return m_pCurrentAppChannel;
        }
        return ChannelManager.getInstance().getCurrentChannelInfo();
    }
    @Nullable
    @Override
    public List<ChannelListInfo> queryFavChannelListInfo() {
        return ChannelManager.getInstance().queryFavChannelListInfo();
    }

    @Override
    public void createFavList(String mTopName, boolean b) {
        ChannelManager.getInstance().createFavList(mTopName,b);
    }
    @Nullable
    @Override
    public List<Channel> queryFavChannelList(ChannelListInfo channelListInfo) {
        return ChannelManager.getInstance().queryFavChannelList(channelListInfo);
    }

    @Override
    public void saveChannel(Channel channel) {
        Source currentSource = PlatformSourceManager.getInstance().GetCurrentSource();
        ChannelManager.getInstance().saveChannel(channel);
    }

    @Override
    public void addToFavChannelList(String s, Channel curEditChannel) {
        ChannelManager.getInstance().addToFavChannelList(s,curEditChannel);
    }

    @Override
    public void removeToFavChannelList(String FavListName, Channel channel) {
        ChannelManager.getInstance().removeToFavChannelList(FavListName,channel);
    }

    @Override
    public void renameFavList(String listName, String s) {
        ChannelManager.getInstance().renameFavList(listName,s);
    }

    private EN_CHANNEL_LIST_TYPE GetListTypeBySource(Source source)
{
    EN_CHANNEL_LIST_TYPE type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_NONE;
    switch (source.getType())
    {
        case E_INPUT_SOURCE_ATV:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV;
            break;
        case E_INPUT_SOURCE_DTV_DVB_C:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC;
            break;
        case E_INPUT_SOURCE_DTV_DVB_T:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT;
            break;
        case E_INPUT_SOURCE_DTV_DVB_S:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBS;
            break;
        case E_INPUT_SOURCE_DTV_ATSC:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATSC;
            break;
        case E_INPUT_SOURCE_DTV_ISDB:
            type = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ISDB;
            break;
        default:break;
    }
    return type;
}
    private void SaveCurTifSourceChannelPosition(String sourceId, int position) {

        Log.d(DEBUG_TAG,"Save current channel position: " + position + "[" +sourceId +"]");
        Context context = PlatformManager.getInstance( ).GetApplicationContext( );
        List<Map<String, String>> list = null;
        list = SPUtil.getInstance(context).getListMap(CUR_TIF_CHANNEL_POSITION_KEY);
        Map<String,String> stringStringMap = null;
        if(list!=null)
        {
            Log.d(DEBUG_TAG,"List not null!");
            for(int i= 0;i< list.size();i++)
            {
                if(list.get(i).containsKey(sourceId))
                {
                    stringStringMap = list.get(i);
                    Log.d(DEBUG_TAG,"already have key:"+ stringStringMap.keySet());
                    Log.d(DEBUG_TAG,"old key value:"+ stringStringMap.get(sourceId));
                    stringStringMap.remove(sourceId);
                    stringStringMap.put(sourceId,String.valueOf(position));
                    Log.d(DEBUG_TAG,"already hav key:"+ stringStringMap.keySet());
                    Log.d(DEBUG_TAG,"new key value:"+ stringStringMap.get(sourceId));
                    break;
                }
            }
        }
        else
        {
            Log.d(DEBUG_TAG,"Channel is first init");
            list = new ArrayList<>();
        }
        if(stringStringMap == null)
        {
            stringStringMap = new HashMap<>();
            stringStringMap.put(sourceId,String.valueOf(position));
            list.add(stringStringMap);
        }
        SPUtil.getInstance(context).putListMap(CUR_TIF_CHANNEL_POSITION_KEY,list);
    }

    private int GetChannelPositionBySource(Source source) {
        Context context = PlatformManager.getInstance( ).GetApplicationContext( );
        List<Map<String, String>> listMap = SPUtil.getInstance(context).getListMap(CUR_TIF_CHANNEL_POSITION_KEY);
        if(listMap!=null)
        {
            TvInputInfo tifSource = (TvInputInfo) source.getOtherInfo(TIF_OBJ);
            Log.d(DEBUG_TAG,"MAP SIZE: " + listMap.size());
            for(int i = 0;i<listMap.size();i++)
            {
                Map<String, String> stringStringMap = listMap.get(i);
                if(stringStringMap.containsKey(tifSource.getId()))
                {
                    String pos = stringStringMap.get(tifSource.getId());
                    Log.d(DEBUG_TAG,"POS:" + pos);
                    if(pos!=null)
                    {
                        int position = Integer.parseInt(pos);
                        Log.d(DEBUG_TAG,"Current Source["+tifSource.getId() + "];s lastChannelPos:" + position);
                        return position;
                    }
                }
            }
        }
        return 0;
    }
    public boolean hasTIFChannelInfoBySource(String sourceId) {
        if (sourceId == null) {
            return false;
        }
        String[] selectionArgs = {
                sourceId
        };
        Cursor c = mContextContentResolver.query(TvContract.Channels.CONTENT_URI, null,
                TvContract.Channels.COLUMN_INPUT_ID + " = ?",
                selectionArgs, null);
        if (c == null) {
            return false;
        }
        Log.d(DEBUG_TAG,"Current source has : " + c.getCount() + " Channels");
        if (c.moveToNext( )) {
            c.close( );
            return true;
        }
        return false;
    }
}
