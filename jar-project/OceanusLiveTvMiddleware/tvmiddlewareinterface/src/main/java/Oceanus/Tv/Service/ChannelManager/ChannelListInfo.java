package Oceanus.Tv.Service.ChannelManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_SORT_CHANNEL_TYPE;

/**
 * Created by sky057509 on 2017/4/13.
 */
public class ChannelListInfo {
    private String ListName = "";
    private EN_CHANNEL_LIST_TYPE ListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_NONE;
    private int DefaultChannelDbIndex = 0;
    private EN_SORT_CHANNEL_TYPE SortType = EN_SORT_CHANNEL_TYPE.E_SORT_BY_DEFAULT;
    private boolean IsActive = false;
    public ChannelListInfo(JSONObject jsonObject)
    {
        try {
            ListName = jsonObject.getString("ListName");
            ListType = EN_CHANNEL_LIST_TYPE.values()[jsonObject.getInt("ListType")];
            IsActive = jsonObject.getBoolean("IsActive");
            SortType = EN_SORT_CHANNEL_TYPE.values()[jsonObject.getInt("SortType")];
            DefaultChannelDbIndex = jsonObject.getInt("DefaultChannelDbIndex");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    protected String toJsonString()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ListName",this.ListName);
            jsonObject.put("ListType",this.ListType);
            jsonObject.put("IsActive",this.IsActive);
            jsonObject.put("SortType",this.SortType.ordinal());
            jsonObject.put("DefaultChannelDbIndex",this.DefaultChannelDbIndex);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
    public String getListName()
    {
        return ListName;
    }
    public EN_SORT_CHANNEL_TYPE getSortType()
    {
        return SortType;
    }
    public EN_CHANNEL_LIST_TYPE getListType()
    {
        return ListType;
    }
    public boolean isbIsActive()
    {
        return IsActive;
    }
    public int getDefaultChannelDbIndex()
    {
        return getDefaultChannelDbIndex();
    }
}
