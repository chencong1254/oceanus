package Oceanus.Tv.Service.ChannelScanManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;


/**
 * Created by heji@skyworth.com on 2016/8/30.
 */
public class AtvScanResult {
    private int freq = AtvScanGlobalDefinitions.ATV_MIN_FREQ;
    private int curScanedChNum = 0;
    private int scanedChNum = 0;
    private double percent = 0;
    private ATV.EN_SOUND_SYSTEM curScanedSoundSystem = ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_UNKNOW;
    private ATV.EN_COLOR_SYSTEM curScanedColorSystem = ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_UNKNOW;
    private EN_ATV_SCAN_MODE mode = EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE;
    public AtvScanResult(JSONObject result) throws JSONException {
        this.curScanedChNum = result.getInt("curScanedChNum");
        this.freq = result.getInt("freq");
        this.scanedChNum = result.getInt("scanedChNum");
        this.percent = result.getDouble("percent");
        this.mode = EN_ATV_SCAN_MODE.values()[result.getInt("mode")];
        if(this.mode != EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE)
        {
            this.curScanedSoundSystem = ATV.EN_SOUND_SYSTEM.values()[result.getInt("curScanedSoundSystem")];
            this.curScanedColorSystem = ATV.EN_COLOR_SYSTEM.values()[result.getInt("curScanedColorSystem")];
        }
    }
    public int getFreq()
    {
        return this.freq;
    }
    public int getCurScanedChNum ()
    {
        return this.curScanedChNum;
    }
    public int getScanedChNum()
    {
        return this.scanedChNum;
    }
    public double getPercent()
    {
        return this.percent;
    }
    public EN_ATV_SCAN_MODE getMode()
    {
        return this.mode;
    }
    public ATV.EN_COLOR_SYSTEM getVideoStd()
    {
        return this.curScanedColorSystem;
    }
    public ATV.EN_SOUND_SYSTEM getSoundStd()
    {
        return this.curScanedSoundSystem;
    }
}


