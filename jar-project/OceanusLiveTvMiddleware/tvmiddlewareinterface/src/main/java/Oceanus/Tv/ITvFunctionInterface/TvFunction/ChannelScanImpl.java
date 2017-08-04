package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvITVCallback;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvScanPalSecamBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.IChannelScan;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.ChannelScanManager.DtvSearchRequirement;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

import static Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions.ATV_MAX_FREQ;
import static Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions.ATV_MIN_FREQ;
import static Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions.ATV_SCAN_STEP;

/**
 * Created by sky057509 on 2016/12/7.
 */
public class ChannelScanImpl implements IChannelScan {
    private static MtkTvScan mtkTvScan = null;
    private static MtkTvITVCallback callback = null;
    private static ChannelScanImpl mObj_This = null;
    private float centerFineTuneFreq = 0;
    private static boolean is_Scanning = false;
    private ChannelScanImpl()
    {
        mtkTvScan = MtkTvScan.getInstance();

    }
    public static ChannelScanImpl getInstance()
    {
        if(mObj_This == null)
        {
            mObj_This = new ChannelScanImpl();
            return mObj_This;
        }
        else
        {
            return mObj_This;
        }
    }
    @Override
    public boolean startAtvSearch(EN_ATV_SCAN_MODE mode, int freq) {
        is_Scanning = true;
        if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
        {
            SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV);
        }
        IEventAdapter.getInstance().setScanMode(false,mode.ordinal());
        Log.d("Oceanus","frequency: " + freq + "Mode:" + mode.toString());
        switch (mode)
        {
            case E_ATV_TUNE_MODE_AUTO_TUNE:
            {
                MtkTvScanPalSecamBase.ScanPalSecamRet rect=MtkTvScan.getInstance().getScanPalSecamInstance().startAutoScan();
                if(rect != MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR)
                {
                    return true;
                }
            }
            break;
            case E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_UP:
            {
                if(centerFineTuneFreq == 0)
                {
                    centerFineTuneFreq = freq;
                }
                centerFineTuneFreq += AtvScanGlobalDefinitions.ATV_FINE_TUNE_STEP;
                return (MtkTvAppTV.getInstance().setFinetuneFreq("main", (int) centerFineTuneFreq,false) == 0)? true:false;

            }
            case E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_DOWN:
            {
                if(centerFineTuneFreq == 0)
                {
                    centerFineTuneFreq = freq;
                }
                centerFineTuneFreq -= AtvScanGlobalDefinitions.ATV_FINE_TUNE_STEP;
                return (MtkTvAppTV.getInstance().setFinetuneFreq("main", (int) centerFineTuneFreq,false) == 0)? true:false;
            }
            case E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_DOWN:
            {
                MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase().new ScanPalSecamFreqRange();
                MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
                int minFreq = range.lower_freq;
                Log.d("Oceanus","minFreq: " + minFreq);
                MtkTvScanPalSecamBase.ScanPalSecamRet rect=MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(freq, minFreq);
                if(rect != MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR)
                {
                    return true;
                }
            }
            break;
            case E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP:
            {
                MtkTvScanPalSecamBase.ScanPalSecamFreqRange range = new MtkTvScanPalSecamBase().new ScanPalSecamFreqRange();
                MtkTvScan.getInstance().getScanPalSecamInstance().getFreqRange(range);
                int maxFreq = range.upper_freq;
                Log.d("Oceanus","maxFreq: " + maxFreq);
                MtkTvScanPalSecamBase.ScanPalSecamRet rect=MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(freq, Integer.MAX_VALUE);
                if(rect != MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR)
                {
                    return true;
                }
            }
            break;
            case E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_ONE_FREQ:
            {
                Log.d("Oceanus","E_ATV_MANUAL_TUNE_MODE_FINE_TUNE_ONE_FREQ!!!" + freq);
                int start_freq = freq - ATV_SCAN_STEP;
                if(start_freq<0)
                {
                    start_freq = ATV_MIN_FREQ;
                }
                int end_freq = freq+ATV_SCAN_STEP;
                if(end_freq>ATV_MAX_FREQ)
                {
                    end_freq = ATV_MAX_FREQ;
                }
                MtkTvScanPalSecamBase.ScanPalSecamRet rect=MtkTvScan.getInstance().getScanPalSecamInstance().startRangeScan(start_freq,end_freq);
                if(rect != MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR)
                {
                    return true;
                }
            }
            break;
            default:
                break;
        }
        is_Scanning = false;
        return false;
    }

    @Override
    public boolean startDtvSearch(DtvSearchRequirement requirement) {
        EN_DTV_SCAN_MODE mode = EN_DTV_SCAN_MODE.E_DTV_TUNE_MODE_UNDEFINE;
        JSONObject jrequirement = null;
        try {
            jrequirement = requirement.toJsonObj();
            mode = EN_DTV_SCAN_MODE.values()[jrequirement.getInt("mode")];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(mode != EN_DTV_SCAN_MODE.E_DTV_TUNE_MODE_UNDEFINE)
        {
            IEventAdapter.getInstance().setScanMode(true,mode.ordinal());
            switch (mode)
            {
                case E_DTV_AUTO_TUNE_MODE_DVB_C:
                {
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C);
                    }
                    MtkTvScanDvbcBase.MtkTvScanDvbcParameter mDvbcScanPara = mtkTvScan.getScanDvbcInstance().new MtkTvScanDvbcParameter();
                    mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF);
                    mDvbcScanPara.setNetWorkID(-1);
                    mDvbcScanPara.setStartFreq(-1);
                    mDvbcScanPara.setEndFreq(-1);
                    mDvbcScanPara.setCfgFlag(0x2000);//for full scan
                    mtkTvScan.getScanDvbcInstance().setDvbcScanParas(mDvbcScanPara);
                    MtkTvScanDvbcBase.ScanDvbcRet dvbcRet = mtkTvScan.getScanDvbcInstance().startAutoScan();
                    if (dvbcRet != MtkTvScanDvbcBase.ScanDvbcRet.SCAN_DVBC_RET_OK)
                    {
                        is_Scanning = false;
                        return false;
                    }
                }
                break;
                case E_DTV_AUTO_TUNE_MODE_DVB_T:
                {
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T);
                    }
                    MtkTvScanDvbtBase.ScanDvbtRet dvbtRet = mtkTvScan.getScanDvbtInstance().startAutoScan();
                    if (MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK != dvbtRet) {
                        is_Scanning = false;
                        return false;
                    }
                }
                break;
                case E_DTV_AUTO_TUNE_MODE_ISDB:
                {
                    is_Scanning = false;
                }
                break;
                case E_DTV_MANUAL_TUNE_MODE_DVB_C:
                {
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C);
                    }
                    MtkTvScanDvbcBase.MtkTvScanDvbcParameter mDvbcScanPara = mtkTvScan.getScanDvbcInstance().new MtkTvScanDvbcParameter();
                    mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_OFF);
                    mDvbcScanPara.setNetWorkID(-1);
                    try {
                        mDvbcScanPara.setStartFreq(jrequirement.getInt("freq"));
                        mDvbcScanPara.setEndFreq(jrequirement.getInt("freq"));
                        mDvbcScanPara.setCfgFlag(0);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        is_Scanning = false;
                        return false;
                    }
                    mtkTvScan.getScanDvbcInstance().setDvbcScanParas(mDvbcScanPara);
                    mtkTvScan.startScan(MtkTvScanBase.ScanType.SCAN_TYPE_DVBC,MtkTvScanBase.ScanMode.SCAN_MODE_MANUAL_FREQ,false);
                }
                break;
                case E_DTV_MANUAL_TUNE_MODE_DVB_T:
                {
                    getDvbtCurrentFreqPointTable();
                    Log.d("Oceanus","E_DTV_MANUAL_TUNE_MODE_DVB_T");
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T);
                    }
                    try {
                        if(jrequirement.isNull("freqPoint"))
                        {
                            Log.d("Oceanus","freqPoint not found use freq directly");
                            mtkTvScan.getScanDvbtInstance().startManualFreqScan(jrequirement.getInt("freq"));
                        }
                        else
                        {
                            Log.d("Oceanus","freqPoint found use freqPoint directly");
                            FreqPoint freqPiont = new FreqPoint(jrequirement.getJSONObject("freqPoint"));
                            int setrfIndex  = freqPiont.getTableIndex();
                            MtkTvScanDvbtBase.RfInfo currentrfInfo = mtkTvScan.getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.CURRENT);
                            Log.d("Oceanus","currentrfInfo rfIndex: " + currentrfInfo.rfIndex);
                            Log.d("Oceanus","set rfInfo rfIndex: " + setrfIndex);
                            while (currentrfInfo.rfIndex != setrfIndex)
                            {
                                Log.d("Oceanus","currentrfInfo rfIndex: " + currentrfInfo.rfIndex);
                                Log.d("Oceanus","set rfInfo rfIndex: " + setrfIndex);
                                if(currentrfInfo.rfIndex<setrfIndex)
                                {
                                    currentrfInfo = mtkTvScan.getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.NEXT);
                                }
                                else
                                {
                                    currentrfInfo = mtkTvScan.getScanDvbtInstance().gotoDestinationRf(MtkTvScanDvbtBase.RfDirection.PREVIOUS);
                                }
                            }
                            Log.d("Oceanus","start rf scan index: " + setrfIndex+"freq: " + currentrfInfo.rfFrequence);
                            mtkTvScan.getScanDvbtInstance().startRfScan();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        is_Scanning = false;
                        return false;
                    }
                }
                break;
                case E_DTV_TUNE_MODE_NIT_TABLE:
                {
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C);
                    }
                    MtkTvScanDvbcBase.MtkTvScanDvbcParameter mDvbcScanPara = mtkTvScan.getScanDvbcInstance().new MtkTvScanDvbcParameter();
                    mDvbcScanPara.setNitMode(MtkTvScanDvbcBase.ScanDvbcNitMode.DVBC_NIT_SEARCH_MODE_EX_QUICK);
                    try {
                        mDvbcScanPara.setStartFreq(jrequirement.getInt("freq"));
                        mDvbcScanPara.setEndFreq(jrequirement.getInt("freq"));
                        mDvbcScanPara.setNetWorkID(jrequirement.getInt("networkId"));
                        mDvbcScanPara.setCfgFlag(0);
                        mtkTvScan.getScanDvbcInstance().setDvbcScanParas(mDvbcScanPara);
                        mtkTvScan.startScan(MtkTvScanBase.ScanType.SCAN_TYPE_DVBC,MtkTvScanBase.ScanMode.SCAN_MODE_MANUAL_FREQ,false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        is_Scanning = false;
                        return false;
                    }
                }
                break;
                case E_DTV_TUNE_MODE_SATELLITE:
                {
                    if(SourceManager.getInstance().getCurSource().getType()!= EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S)
                    {
                        SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S);
                    }
                    is_Scanning = false;
                }
                break;
                default:
                    break;
            }
        }
        is_Scanning = true;
        return true;
    }

    @Override
    public boolean stopAtvSearch() {
        MtkTvScanPalSecamBase.ScanPalSecamRet ret = mtkTvScan.getScanPalSecamInstance().cancelScan();
        is_Scanning = false;
        return ret != MtkTvScanPalSecamBase.ScanPalSecamRet.SCAN_PAL_SECAM_RET_INTERNAL_ERROR;
    }

    @Override
    public boolean stopDtvSearch() {
        switch (IEventAdapter.getInstance().getDtvScanMode()) {
            case E_DTV_AUTO_TUNE_MODE_DVB_C: {
                MtkTvScanDvbcBase.ScanDvbcRet dvbcRet = mtkTvScan.getScanDvbcInstance().cancelScan();
                if (MtkTvScanDvbcBase.ScanDvbcRet.SCAN_DVBC_RET_OK == dvbcRet) {
                    is_Scanning = false;
                    return true;
                }
            }
            break;
            case E_DTV_AUTO_TUNE_MODE_DVB_T: {
                MtkTvScanDvbtBase.ScanDvbtRet dvbtRet = mtkTvScan.getScanDvbtInstance().cancelScan();
                if (MtkTvScanDvbtBase.ScanDvbtRet.SCAN_DVBT_RET_OK == dvbtRet) {
                    is_Scanning = false;
                    return true;
                }
            }
            break;
            case E_DTV_AUTO_TUNE_MODE_ISDB: {
                is_Scanning = false;
            }
            break;
        }
        return false;
    }
    @Override
    public List<FreqPoint> getDvbtCurrentFreqPointTable() {
        List<FreqPoint> dvbtScanTable = new ArrayList<>();
        MtkTvScanDvbtBase.RfInfo[] rfInfoList = MtkTvScan.getInstance().getScanDvbtInstance().getAllRf();
        for (MtkTvScanDvbtBase.RfInfo aRfInfoList : rfInfoList) {

            FreqPoint freqPiont = new FreqPoint(aRfInfoList.rfIndex, aRfInfoList.rfFrequence);
            int length = 0;
            char[] namebyte = new char[8];
            for (char aNamebyte : aRfInfoList.rfChannelName.toCharArray()) {
                if((int)aNamebyte!=0)
                {
                    namebyte[length] = aNamebyte;
                    length++;
                }
                else
                    break;
            }
            freqPiont.setName(String.valueOf(namebyte,0,length));
            dvbtScanTable.add(freqPiont);
        }
        return dvbtScanTable;
    }
    public void setScaning(boolean scaning)
    {
        is_Scanning = scaning;
    }
    @Override
    public boolean isScanning() {
        return is_Scanning;
    }
}
