package Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sky057509 on 2017/5/18.
 */

public class FreqPoint
{
    private int tableIndex = 0;
    private int freq = 0;
    private String name = " ";
    public FreqPoint(JSONObject jsonObject)
    {
        if(jsonObject!=null)
        {
            try {
                name = jsonObject.getString("name");
                freq = jsonObject.getInt("freq");
                tableIndex = jsonObject.getInt("tableIndex");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public FreqPoint(int tableIndex, int freq)
    {
        name = " ";
        this.freq = freq;
        this.tableIndex = tableIndex;
    }
    public String getName()
    {
        return this.name;
    }

    public int getFreq()
    {
        return freq;
    }
    public int getTableIndex() {
        return tableIndex;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    public JSONObject toJsonObject()
    {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tableIndex",tableIndex);
            jsonObject.put("freq",freq);
            jsonObject.put("name",name);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }
}
