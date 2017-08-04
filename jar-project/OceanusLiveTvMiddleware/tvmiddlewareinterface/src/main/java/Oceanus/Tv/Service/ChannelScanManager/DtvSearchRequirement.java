package Oceanus.Tv.Service.ChannelScanManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_MODULATION_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_TUNING_BAND_WIDTH;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DVB_SCAN_SCRAMBLE_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_COUNTRY;

/**
 * Created by sky057509 on 2016/9/6.
 */
public class DtvSearchRequirement {
    private boolean bIsAuto = false;
    private EN_COUNTRY country = EN_COUNTRY.TV_COUNTRY_NUMBER;
    private EN_DTV_SCAN_MODE mode = EN_DTV_SCAN_MODE.E_DTV_TUNE_MODE_UNDEFINE;
    private EN_DTV_TUNING_BAND_WIDTH bandWidth = EN_DTV_TUNING_BAND_WIDTH.E_DTV_TUNING_BAND_WIDTH_5_MHZ;
    private EN_DTV_MODULATION_MODE modulation = EN_DTV_MODULATION_MODE.E_DTV_MODULATION_MODE_AUTO;
    private EN_DVB_SCAN_SCRAMBLE_TYPE scrambleType = EN_DVB_SCAN_SCRAMBLE_TYPE.E_DVB_SCAN_SCRAMBLE_ALL;
    private FreqPoint freqPoint = null;
    private int freq = 0;
    private int symbolRate = 0;
    private int networkId = -1;
    public DtvSearchRequirement(EN_DTV_SCAN_MODE mode)
    {
        switch (mode)
        {
            case E_DTV_AUTO_TUNE_MODE_DVB_T:
            case E_DTV_AUTO_TUNE_MODE_DVB_C:
            case E_DTV_AUTO_TUNE_MODE_ATSC:
            case E_DTV_AUTO_TUNE_MODE_ISDB:
            case E_DTV_AUTO_TUNE_MODE_DTMB:
            {
                this.bIsAuto = true;
            }
            break;
            default:break;
        }
        this.mode = mode;
    }
    public JSONObject toJsonObj() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bIsAuto",bIsAuto);
        jsonObject.put("country",country.ordinal());
        jsonObject.put("mode",mode.ordinal());
        jsonObject.put("bandWidth",bandWidth.ordinal());
        jsonObject.put("modulation",modulation.ordinal());
        jsonObject.put("scrambleType",scrambleType.ordinal());
        jsonObject.put("freq",freq);
        jsonObject.put("symbolRate",symbolRate);
        jsonObject.put("networkId",networkId);
        if(freqPoint!=null)
        {
            jsonObject.put("freqPoint",freqPoint.toJsonObject());
        }
        return jsonObject;
    }
    public void setBandWidth(EN_DTV_TUNING_BAND_WIDTH bandWidth)
    {
        this.bandWidth = bandWidth;
    }
    public void setModulation(EN_DTV_MODULATION_MODE qam)
    {
        this.modulation = modulation;
    }
    public void setCountry(EN_COUNTRY country)
    {
        this.country = country;
    }
    public void setScrambleType(EN_DVB_SCAN_SCRAMBLE_TYPE scrambleType)
    {
        this.scrambleType = scrambleType;
    }
    public void setFreq(int freq)
    {
        this.freq = freq;
    }
    public void setSymbolRate(int symbolRate)
    {
        this.symbolRate = symbolRate;
    }
    public void setNetworkId(int networkId)
    {
        this.networkId = networkId;
    }

    public void setFreqPoint(FreqPoint freqPoint) {
        this.freqPoint = freqPoint;
    }
}
