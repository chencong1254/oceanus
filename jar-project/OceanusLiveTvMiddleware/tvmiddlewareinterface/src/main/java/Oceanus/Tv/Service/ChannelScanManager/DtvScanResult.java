package Oceanus.Tv.Service.ChannelScanManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_SERVICE_TYPE;

/**
 * Created by heji@skyworth.com on 2016/9/5.
 */
public class DtvScanResult {
    private EN_DTV_SCAN_SERVICE_TYPE type = EN_DTV_SCAN_SERVICE_TYPE.E_DTV_SCAN_NONE;
    private EN_DTV_SCAN_MODE mode = EN_DTV_SCAN_MODE.E_DTV_TUNE_MODE_UNDEFINE;
    private int freq = 0;
    private int  curScanedChNum = 0;
    private int curScanedDtvNum = 0;
    private int scanedChNum = 0;
    private int percent = 0;
    private int curScanedRadioNum = 0;
    private int curScanedDataNum = 0;
    private int signalQuality = 0;
    private int signaleLevel = 0;
    public DtvScanResult(JSONObject result) throws JSONException {
        this.scanedChNum = result.getInt("scanedChNum");
        this.percent = result.getInt("percent");
        this.type = EN_DTV_SCAN_SERVICE_TYPE.values()[result.getInt("type")];
        this.mode = EN_DTV_SCAN_MODE.values()[result.getInt("mode")];
        this.freq = result.getInt("freq");
        this.curScanedChNum = result.getInt("curScanedChNum");
        this.curScanedDtvNum = result.getInt("curScanedDtvNum");
        this.curScanedRadioNum = result.getInt("curScanedRadioNum");
        this.curScanedDataNum = result.getInt("curScanedDataNum");
        this.signalQuality = result.getInt("signalQuality");
        this.signaleLevel = result.getInt("signaleLevel");
    }
    public int getCurScanedChNum()
    {
        return this.curScanedChNum;
    }
    public int getFreq()
    {
        return this.freq;
    }
    public int getCurScanedDtvNum()
    {
        return this.curScanedDtvNum;
    }
    public int getScanedChNum()
    {
        return this.scanedChNum;
    }
    public int getPercent()
    {
        return this.percent;
    }
    public int getCurScanedRadioNum()
    {
        return this.curScanedRadioNum;
    }
    public int getCurScanedDataNum()
    {
        return this.curScanedDataNum;
    }
    public int getSignalQuality()
    {
        return this.signalQuality;
    }
    public int getSignaleLevel(){
        return this.signaleLevel;
    }
    public EN_DTV_SCAN_SERVICE_TYPE getType()
    {
        return this.type;
    }
    public EN_DTV_SCAN_MODE getMode()
    {
        return this.mode;
    }
}
