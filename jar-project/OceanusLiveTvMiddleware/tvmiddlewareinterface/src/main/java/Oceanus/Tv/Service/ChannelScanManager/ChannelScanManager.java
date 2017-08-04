package Oceanus.Tv.Service.ChannelScanManager;

import android.util.Log;

import org.json.JSONException;

import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.IChannelScan;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.ChannelScanImpl;
import Oceanus.Tv.Service.ChannelManager.ChannelManager;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by shen on 2016/8/16.
 */
public class ChannelScanManager {
    public static final String ListenerName = "ChannelScanListener";
    private static final String LOG_TAG = "ChannelScanManager";
    private static ChannelScanManager mObj_ChannelScanManager = null;
    private static IChannelScan mObj_ChannelScanManagerInterface = null;
    public static  ChannelScanManager getInstance()
    {
        synchronized(ChannelScanManager.class)
            {
                if (mObj_ChannelScanManager == null)
                {
                    new ChannelScanManager();
                }
            }
        return mObj_ChannelScanManager;
    }
    private ChannelScanManager()
    {
        Log.d(LOG_TAG,"ChannelScanManager Created~");
        mObj_ChannelScanManager = this;
        mObj_ChannelScanManagerInterface = ChannelScanImpl.getInstance();
        //Connect();
    }
    /*
    static
    {
        Log.d(LOG_TAG,"ChannelScanManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_ChannelScanManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_ChannelScanManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    */
    // Atv
    public boolean startAtvSearch(EN_ATV_SCAN_MODE mode, int freq)
    {
        if(mode == EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE)
        {
            ChannelManager.getInstance().cleanChannelList(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV);
        }
        mObj_ChannelScanManagerInterface.startAtvSearch(mode,freq);
        return  true;
    }
    public boolean startAtvFreqFineCurFrequencyUp()
    {
        return mObj_ChannelScanManagerInterface.startAtvSearch(EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_UP,ChannelManager.getInstance().getCurrentChannelInfo().getFreq());
    }
    public boolean startAtvFreqFineCurFrequencyDown()
    {
        return mObj_ChannelScanManagerInterface.startAtvSearch(EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_DOWN,ChannelManager.getInstance().getCurrentChannelInfo().getFreq());
    }
    public boolean startDtvSearch(DtvSearchRequirement requirement)
    {
        try {
            if(requirement.toJsonObj().getInt("mode") == EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_T.ordinal())
            {
                ChannelManager.getInstance().cleanChannelList(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT);
            }
            else if(requirement.toJsonObj().getInt("mode") == EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_C.ordinal())
            {
                ChannelManager.getInstance().cleanChannelList(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC);
            }
            return mObj_ChannelScanManagerInterface.startDtvSearch(requirement);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean stopAtvSearch()
    {
        return mObj_ChannelScanManagerInterface.stopAtvSearch();
    }

    public boolean stopDtvSearch()
    {
        return mObj_ChannelScanManagerInterface.stopDtvSearch();
    }
    public List<FreqPoint> getScanTable(EN_INPUT_SOURCE_TYPE source_type)
    {
        switch (source_type)
        {
            case E_INPUT_SOURCE_DTV_DVB_T:
            {
                return mObj_ChannelScanManagerInterface.getDvbtCurrentFreqPointTable();
            }
            default:
                return null;
        }
    }
    public boolean isScanning()
    {
        return mObj_ChannelScanManagerInterface.isScanning();
    }
    /*
    public native boolean stopAtvSearch();
    public native boolean setDvbtPowerAntenna(boolean bAntennaStatus);
    public native boolean getDvbtPowerAntenna();
    public native boolean isTvScanning();
    public native boolean stopDtvSearch();
    public native boolean startDvbcAutoSearchWithNit(int iFreq, int networkkid,int scanScope, int symbolRateValue);
    private native boolean startAtvSearch(int mode,int freq);
    private native boolean startDtvSearch(String jsonRequirementStr);
    private native void Connect();
    private native void Disconnect();
    */
}
