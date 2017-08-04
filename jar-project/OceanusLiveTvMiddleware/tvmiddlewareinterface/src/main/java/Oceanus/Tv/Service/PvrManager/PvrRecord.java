package Oceanus.Tv.Service.PvrManager;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sky057509 on 2016/12/7.
 */
public class PvrRecord {
    private int index = 0;
    private int size = 0;
    private int channelNumber = 0;
    private int lcnNumber = 0;
    private long during = 0;//sec
    private long duration = 0;//sec
    private String filePath = "";
    private String recordTime = "00:00:00-01-01-2016";
    private String fileName = "OceanusRecord";
    private String channelName = "OceanusChannel";
    public PvrRecord(JSONObject jPvrRecord) throws JSONException {
        if(jPvrRecord!=null)
            {
                this.index = jPvrRecord.getInt("index");
                this.size = jPvrRecord.getInt("size");
                this.channelNumber = jPvrRecord.getInt("channelNumber");
                this.lcnNumber = jPvrRecord.getInt("lcnNumber");
                this.during = jPvrRecord.getLong("during");
                this.duration = jPvrRecord.getLong("duration");
                this.filePath = jPvrRecord.getString("filePath");
                this.recordTime = jPvrRecord.getString("recordTime");
                this.fileName = jPvrRecord.getString("fileName");
                this.channelName = jPvrRecord.getString("channelName");
            }
    }
    public int getIndex()
    {
        return this.index;
    }
    public int getSize()
    {
        return this.size;
    }
    public int getLcnNumber()
    {
        return this.lcnNumber;
    }
    public long getDuringTime()
    {
        return this.during;
    }
    public long getDuration()
    {
        return this.duration;
    }
    public int getChannelNumber()
    {
        return this.channelNumber;
    }
    public String getFilePath()
    {
        return this.filePath;
    }
    public String getFileName()
    {
        return this.fileName;
    }
    public String getChannelName()
    {
        return this.channelName;
    }
    public String getRecordTime()
    {
        return this.recordTime;
    }
    public void setDuringTime(long duringTime)
    {
        this.during = duringTime;
    }
    public void renameFile(String newName)
    {
        this.fileName = newName;
    }
}
