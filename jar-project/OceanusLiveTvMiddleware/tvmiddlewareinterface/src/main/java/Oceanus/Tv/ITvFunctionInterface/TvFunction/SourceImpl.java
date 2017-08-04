package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvInputSourceBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.ISource;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.SourceManager.Source;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;

/**
 * Created by heji@skyworth.com on 2016/12/9.
 */
public class SourceImpl implements ISource {
    private final static String MTK_SOURCE_ID = "mtk_source_id";
    public static final int BRDCST_TYPE_ATV = 1;
    public static final int BRDCST_TYPE_DTV = 0;
    private static MtkTvInputSource mObj_MtkSource = null;
    private static int SourceNumber;
    private static List<Source> mObj_SourceList = null;
    private static Source mObj_CurrentSource = null;
    private static SourceImpl mObj_This = null;
    private SourceImpl(Context mContext) {
        mObj_MtkSource = MtkTvInputSource.getInstance();
        if(mObj_MtkSource.init() == 0)
        {
            SourceNumber = mObj_MtkSource.getInputSourceTotalNumber();
            mObj_SourceList = new ArrayList<Source>();
            getSourceList();
            String currentSourceName = mObj_MtkSource.getCurrentInputSourceName();
            EN_INPUT_SOURCE_TYPE currentSourceType = EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV;
            Log.d("Oceanus","current source name:" + currentSourceName);
            if(currentSourceName.equals("DTV") || currentSourceName.equals(("TV")))
            {
                int TUNER_MODE = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_BS_BS_SRC);
                Log.d("Oceanus","Tuner mode: " + TUNER_MODE);
                if(TUNER_MODE == 0)
                {//dvb-t
                    currentSourceType = EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T;
                }
                else if(TUNER_MODE == 1)
                {//dvb-c
                    currentSourceType = EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C;
                }
                else if(TUNER_MODE == 2)
                {//dvb-s
                    currentSourceType = EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S;
                }
            }
            for(int i=0;i<mObj_SourceList.size();i++)
            {
                Log.d("Oceanus","CurrentType:" + currentSourceType.toString());
                Log.d("Oceanus","get type:" + mObj_SourceList.get(i).getType().toString());
                if(mObj_SourceList.get(i).getType() == currentSourceType)
                {
                    mObj_CurrentSource = mObj_SourceList.get(i);
                    Log.d("Oceanus","current source:" + mObj_CurrentSource.getName());
                    break;
                }
            }
        }
        else
        {
            SourceNumber = 0;
            mObj_SourceList = null;
        }

    }
    public static SourceImpl getInstance(Context mContext)
    {
        if(mObj_This == null)
        {
            mObj_This = new SourceImpl(mContext);
            return mObj_This;
        }
        else
        {
            return mObj_This;
        }
    }

    @Override
    public List<Source> getSourceList() {
        if (mObj_SourceList.size()==0)
        {
            Source objDtvSource = null;
            for(int i=0;i<SourceNumber;i++)
            {
                MtkTvInputSourceBase.InputSourceRecord mtkSource = new MtkTvInputSourceBase.InputSourceRecord();
                Source objSource = null;
                if(mObj_MtkSource.getInputSourceRecbyidx(i,mtkSource) == 0)
                {
                    Log.d("Oceanus",i+"-"+"["+mObj_MtkSource.getInputSourceNamebySourceid(mtkSource.getId())+"]");
                    String name = mObj_MtkSource.getInputSourceNamebySourceid(mtkSource.getId());
                    Log.d("Oceanus",i+"-"+"name = ["+name+"]" + "type = [" + mtkSource.getInputType()+"]");
                    if(mtkSource.getInputType() == MtkTvInputSourceBase.InputDeviceType.TV)
                    {
                        if(name.compareTo("ATV") == 0)
                        {
                            objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV,name);
                        }
                        else if (name.compareTo("DTV") == 0)
                        {
                            objDtvSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_NUM,name);
                            objDtvSource.putOtherInfo(MTK_SOURCE_ID,mtkSource.getId());
                        }
                        else if (name.compareTo("TV") == 0)
                        {
                            objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV,name);
                            objDtvSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_NUM,name);
                            objDtvSource.putOtherInfo(MTK_SOURCE_ID,mtkSource.getId());
                        }
                    }
                    else if(mtkSource.getInputType() == MtkTvInputSourceBase.InputDeviceType.COMPOSITE)
                    {
                        objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_CVBS,name);
                    }
                    else if(mtkSource.getInputType() == MtkTvInputSourceBase.InputDeviceType.HDMI)
                    {
                        if(name.compareTo("HDMI 1 / MHL") == 0)
                        {
                            objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI1,name);
                        }
                        else if (name.compareTo("HDMI 2") == 0)
                        {
                            objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI2,name);
                        }
                        else if (name.compareTo("HDMI 3") == 0)
                        {
                            objSource = new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_HDMI3,name);
                        }
                    }
                }
                if(objSource != null)
                {
                    objSource.putOtherInfo(MTK_SOURCE_ID,mtkSource.getId());
                    mObj_SourceList.add(objSource);
                }
            }
            if(objDtvSource != null)
            {
                Source objDvbt =  new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T,"DVB-T");
                Source objDvbc =  new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C,"DVB-C");
                Source objDvbs =  new Source(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S,"DVB-S");
                objDvbt.putOtherInfo(MTK_SOURCE_ID,objDtvSource.getOtherInfo(MTK_SOURCE_ID));
                objDvbc.putOtherInfo(MTK_SOURCE_ID,objDtvSource.getOtherInfo(MTK_SOURCE_ID));
                objDvbs.putOtherInfo(MTK_SOURCE_ID,objDtvSource.getOtherInfo(MTK_SOURCE_ID));
                mObj_SourceList.add(objDvbt);
                mObj_SourceList.add(objDvbc);
                mObj_SourceList.add(objDvbs);
            }
        }
        return mObj_SourceList;
    }

    @Override
    public boolean setSource(Source source) {
        if(mObj_MtkSource.changeInputSourcebySourceid((Integer) source.getOtherInfo(MTK_SOURCE_ID),MtkTvInputSource.INPUT_OUTPUT_MAIN) == 0)
        {
            mObj_CurrentSource = source;
            if(source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
            {
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_BRDCST_TYPE, BRDCST_TYPE_ATV);
                ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV,0,0);
            }
            else if(source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
            {
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_BRDCST_TYPE, BRDCST_TYPE_DTV);
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_SRC,0);
                ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT,0,0);
            }
            else  if(source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
            {
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_BRDCST_TYPE, BRDCST_TYPE_DTV);
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_SRC,1);
                ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC,0,0);
            }
            else if(source.getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_S)
            {
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_BRDCST_TYPE, BRDCST_TYPE_DTV);
                MtkTvConfig.getInstance().setConfigValue(MtkTvConfigType.CFG_BS_BS_SRC,3);
                ChannelImpl.getInstance().queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBS,0,0);
            }
            return true;
        }
        Log.e("Oceanus","Set source error~ Source Name:" + source.getName());
        return false;
    }

    @Override
    public Source getCurrentSource() {
        return mObj_CurrentSource;
    }

    @Override
    public boolean blockSource(EN_INPUT_SOURCE_TYPE source) {
        return false;
    }

    @Override
    public int getSourceNumber() {
        return SourceNumber;
    }
}
