package Oceanus.Tv.Service.ChannelManager;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.IChannel;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.ChannelImpl;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_OCHANNEL_COUNT_TYPE;


/**
 * @ClassName ChannelManager
 */
public class ChannelManager {
    private static final String LOG_TAG = "ChannelManager";
    public static final String SWITCH_UP = "SWITCH_UP";
    public static final String SWITCH_DOWN = "SWITCH_DOWN";
    private static ChannelManager mObj_ChannelManager = null;
    private static IChannel mObj_ChannelInterface = null;
    public static ChannelManager getInstance()
    {
        synchronized(ChannelManager.class)
            {
                if (mObj_ChannelManager == null)
                {
                    new ChannelManager();
                }
            }
        return mObj_ChannelManager;
    }
    private ChannelManager()
    {
        Log.d(LOG_TAG,"ChannelManager Created~");
        mObj_ChannelManager = this;
        mObj_ChannelInterface = ChannelImpl.getInstance();
        Connect();
    }
    static
    {
        Log.d(LOG_TAG,"ChannelManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_ChannelManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_ChannelManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    public boolean playFristChannel(EN_CHANNEL_LIST_TYPE type)
    {
        List<Channel> channelList = mObj_ChannelInterface.queryChannels(type,0,1);
        if(channelList != null && channelList.size()>0)
        {
            return gotoChannel(channelList.get(0));
        }
        return false;
    }
    public List<Channel> getChannelList(EN_CHANNEL_LIST_TYPE type , int start_index , int number)
    {
        return mObj_ChannelInterface.queryChannels(type,start_index,number);
    }
    public boolean switchChannel(EN_CHANNEL_LIST_TYPE type,String directron)
    {
        if(directron.equals(SWITCH_DOWN))
        {
            return  mObj_ChannelInterface.gotoPrevChannel(type);
        }
        else if (directron.equals(SWITCH_UP))
        {
            return  mObj_ChannelInterface.gotoNextChannel(type);
        }
        else
        {
            Log.d("Oceanus","Unknow directron!!!");
            return false;
        }
    }
    public boolean channelReturn()
    {
        return mObj_ChannelInterface.gotoLastChannel();
    }
    public int getChannelCount(EN_OCHANNEL_COUNT_TYPE type)
    {
       return mObj_ChannelInterface.getChannelCount(type);
    }
    public  Channel getCurrentChannelInfo()
    {
        return mObj_ChannelInterface.getCurrentChannelInfo();
    }
    public boolean gotoChannel(Channel channel)
    {
        if(channel == null)
        {
            return false;
        }
        return mObj_ChannelInterface.gotoChannel(channel);
    }
    public void cleanChannelList(EN_CHANNEL_LIST_TYPE type)
    {
        mObj_ChannelInterface.cleanChannelList(type);
    }
    public boolean createFavList(String ListName,boolean bIsActive)
    {
        return CreateFavChannelList(ListName,bIsActive);
    }
    public void renameFavList(String oldName,String newName)
    {
        RenameFavList(oldName,newName);
    }
    public void deleteFavList(String ListName)
    {
        DeleteFavList(ListName);
    }
    public void addToFavChannelList(String FavListName,Channel channel){
        channel.setIsFav(true);
        if(mObj_ChannelInterface.saveChannel(channel))
        {
            Log.d("Oceanus","mObj_ChannelInterface saveChannel Success!");
            SaveFavChannel(channel.toJsonString(),FavListName);
        }
    }
    public void removeToFavChannelList(String FavListName,Channel channel){
        channel.setIsFav(false);
        RemoveFavChannel(channel.toJsonString(),FavListName);
    }
    public boolean saveChannel(Channel channel)
    {
        return mObj_ChannelInterface.saveChannel(channel);
    }
    public List<ChannelListInfo> queryFavChannelListInfo()
    {
        String JsonArryListStr =  GetChannelListInfoJsonStr(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_FAV.ordinal());
        if(JsonArryListStr.compareTo("")!=0)
        {
            try {
                JSONArray JsonArryList = new JSONArray(JsonArryListStr);
                List<ChannelListInfo> listInfos = new ArrayList<ChannelListInfo>();
                for(int i = 0; i< JsonArryList.length();i++)
                {
                    JSONObject JsonListInfo = JsonArryList.getJSONObject(i);
                    Log.d("Oceanus",JsonListInfo.toString());
                    ChannelListInfo ListInfo = new ChannelListInfo(JsonListInfo);
                    listInfos.add(i,ListInfo);
                }
                return listInfos;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public List<Channel> queryFavChannelList(ChannelListInfo ListInfo)
    {
        List<Channel> resultList = null;
        String JsonArryListStr = QueryChannels(ListInfo.toJsonString(),0,0);
        if(JsonArryListStr.compareTo("") != 0)
        {
            resultList = new ArrayList<Channel>();
            try {
                JSONArray JsonArryList = new JSONArray(JsonArryListStr);
                for(int i = 0;i<JsonArryList.length();i++)
                {
                    JSONObject jsonObject = JsonArryList.getJSONObject(i);
                    Channel objChannel = new Channel(jsonObject);
                    resultList.add(i,objChannel);
                }
                return resultList;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private native void Connect();
    private native void Disconnect();
    private native boolean GotoNextChannel(int type);
    private native void SaveChannel(String jChannelStr);
    private native int GetChannelCount(int type);
    private native String QueryChannels(String JsonListInfoStr,int start_index,int number);
    private native String GetCurrentChannelInfoJstr();
    private native boolean CreateFavChannelList(String ListName,boolean bIsActive);
    private native void SaveDvbsChannel(String ChannelJstr,String Satellite);
    private native void SaveFavChannel(String ChannelJstr,String ListName);
    private native void RemoveFavChannel(String ChannelJstr,String ListName);
    private native int GetFavChannelCount(String ListName);
    private native int GetDvbsChannelCount(String Satellite);
    private native String GetChannelListInfoJsonStr(int list_type);
    private native void RenameFavList(String NewName,String OldName);
    private native void DeleteFavList(String ListName);
    private native void SetDefaultFavList(String ListName);
}
