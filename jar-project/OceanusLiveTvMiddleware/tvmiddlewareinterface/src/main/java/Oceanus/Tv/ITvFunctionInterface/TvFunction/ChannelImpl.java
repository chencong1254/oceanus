package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.IChannel;
import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_ANTENNA_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_AUDIO_FORMAT;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_OCHANNEL_COUNT_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_VIDEO_FORMAT;
import Oceanus.Tv.Service.EventManager.EventManager;
import Oceanus.Tv.Service.EventManager.EventManagerDefinitions.EN_OSYSTEM_EVENT_LIST;
import Oceanus.Tv.Service.EventManager.Tv_EventInfo;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;

import static com.mediatek.twoworlds.tv.MtkTvChannelListBase.CHLST_OPERATOR_MOD;

/**
 * Created by sky057509 on 2016/12/9.
 */
public class ChannelImpl implements IChannel{
    private static ChannelImpl mObj_This;
    static final String MTK_SV_ID = "MTK_SV_ID";
    static final String MTK_SV_REC_ID = "MTK_SV_REC_ID";
    public static final int DB_AIR_SVLID = 1;
    public static final int DB_CAB_SVLID = 2;
    public static final int DB_SAT_SVLID = 3;
    public static final int DB_SAT_PRF_SVLID = 4;
    public static final int DB_CI_PLUS_SVLID_AIR = 5;
    public static final int DB_CI_PLUS_SVLID_CAB = 6;
    public static final int DB_CI_PLUS_SVLID_SAT = 7;
    public static final int BRDCST_TYPE_UNKNOWN = 0;
    public static final int BRDCST_TYPE_ANALOG = 1;
    public static final int BRDCST_TYPE_DVB = 2;
    public static final int BRDCST_TYPE_ATSC = 3;
    public static final int BRDCST_TYPE_SCTE = 4;
    public static final int BRDCST_TYPE_ISDB = 5;
    public static final int BRDCST_TYPE_FMRDO = 6;
    public static final int BRDCST_TYPE_DTMB = 7;
    public static final int BRDCST_TYPE_MHP = 8;
    public static final int BRDCST_MEDIUM_UNKNOWN = 0;
    public static final int BRDCST_MEDIUM_DIG_TERRESTRIAL = 1;
    public static final int BRDCST_MEDIUM_DIG_CABLE = 2;
    public static final int BRDCST_MEDIUM_DIG_SATELLITE = 3;
    public static final int BRDCST_MEDIUM_ANA_TERRESTRIAL = 4;
    public static final int BRDCST_MEDIUM_ANA_CABLE = 5;
    public static final int BRDCST_MEDIUM_ANA_SATELLITE = 6;
    public static final int BRDCST_MEDIUM_1394 = 7;
    /** tuner mode value, 0:T, 1:C, 2:S, 3:used by set general S in wizard and menu */
    public static final int DB_AIR_OPTID = 0;
    public static final int DB_CAB_OPTID = 1;
    public static final int DB_SAT_OPTID = 2;
    public static final int DB_GENERAL_SAT_OPTID = 3;// maybe used by set tuner mode, but should not
    // be used by get tuner mode
    private static MtkTvChannelList mObj_MtkTvChannelList = null;
    private static List<Channel> mObj_ChannelList = null;
    private static List<Channel> mObj_ChannelList_Atv = null;
    private static List<Channel> mObj_ChannelList_Dvbc = null;
    private static List<Channel> mObj_ChannelList_Dvbt = null;
    private static int mObj_CurrentChannelId = 1;
    private static int mObj_CurrentAtvChannelId = 1;
    private static int mObj_CurrentDvbcChannelId = 1;
    private static int mObj_CurrentDvbtChannelId = 1;
    private static int mObj_LastChannelId = 1;
    private static int mObj_LastAtvChannelId = 1;
    private static int mObj_LastDvbcChannelId = 1;
    private static int mObj_LastDvbtChannelId = 1;
    private static EN_CHANNEL_LIST_TYPE mCurrentChListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_NONE;
    private MtkTvBroadcast mObj_MtkTvChannelSelecter = null;

    private ChannelImpl()
    {
        mObj_MtkTvChannelList = MtkTvChannelList.getInstance();
        mObj_MtkTvChannelSelecter = MtkTvBroadcast.getInstance();
        mObj_ChannelList = new ArrayList<Channel>();
        mObj_ChannelList_Atv = new ArrayList<Channel>();
        mObj_ChannelList_Dvbc = new ArrayList<Channel>();
        mObj_ChannelList_Dvbt = new ArrayList<Channel>();
        mObj_ChannelList.clear();
        mObj_ChannelList_Atv.clear();
        mObj_ChannelList_Dvbc.clear();
        mObj_ChannelList_Dvbt.clear();
        queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV,0,0);
        queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC,0,0);
        queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT,0,0);
        queryChannels(EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL,0,0);
        int chId = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_NAV_AIR_CRNT_CH);
        //int chId_calbe = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_NAV_CABLE_CRNT_CH); //as same as CFG_NAV_AIR_CRNT_CH
        //Log.d("Oceanus","Current channel id cable:" + chId_calbe + "air:" + chId);
        mObj_CurrentAtvChannelId = getChannelIndexByMtkChannelId(chId,EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV);
        mObj_CurrentDvbcChannelId = getChannelIndexByMtkChannelId(chId,EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC);
        mObj_CurrentDvbtChannelId = getChannelIndexByMtkChannelId(chId,EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT);
        mObj_CurrentChannelId = getChannelIndexByMtkChannelId(chId,EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL);
        mObj_LastChannelId = mObj_CurrentChannelId;
        mObj_LastAtvChannelId = mObj_CurrentAtvChannelId;
        mObj_LastDvbcChannelId = mObj_CurrentDvbcChannelId;
        mObj_LastDvbtChannelId = mObj_CurrentDvbtChannelId;
        Log.d("Oceanus","mObj_CurrentChannelId: " + mObj_CurrentChannelId);
        Log.d("Oceanus","mObj_CurrentAtvChannelId: " + mObj_CurrentAtvChannelId);
        Log.d("Oceanus","mObj_CurrentDvbcChannelId: " + mObj_CurrentDvbcChannelId);
        Log.d("Oceanus","mObj_CurrentDvbtChannelId: " + mObj_CurrentDvbtChannelId);
    }
    public static ChannelImpl getInstance()
    {
        if(mObj_This == null)
        {
            mObj_This = new ChannelImpl();
        }
        return mObj_This;
    }
    @Override
    public boolean saveChannel(Channel objChannel) {
        int svId = 0;
        int svrecId = 0;
        if(objChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            svId = (int) objChannel.getAtvAttr().getOtherInfo(MTK_SV_ID);
            svrecId = (int) objChannel.getAtvAttr().getOtherInfo(MTK_SV_REC_ID);
        }
        else
        {
            svId = (int) objChannel.getDtvAttr().getOtherInfo(MTK_SV_ID);
            svrecId = (int) objChannel.getDtvAttr().getOtherInfo(MTK_SV_REC_ID);
        }
        MtkTvChannelInfoBase saveChannelInfo = mObj_MtkTvChannelList.getChannelInfoBySvlRecId(svId,svrecId);
        if(saveChannelInfo!=null)
        {
            Log.d("Oceanus","get mtk channel freq :" + saveChannelInfo.getFrequency());
            Log.d("Oceanus","get mtk channel name :" + saveChannelInfo.getServiceName());
            Log.d("Oceanus","get mtk channel number :" + saveChannelInfo.getChannelNumber());
            Log.d("Oceanus","current saved channel freq: "+objChannel.getFreq());
            Log.d("Oceanus","current saved channel name: "+objChannel.getName());
            Log.d("Oceanus","current saved channel number: "+objChannel.getChannelNumber());
            saveChannelInfo.setBlock(objChannel.getIsLock());
            saveChannelInfo.setChannelNumber(objChannel.getChannelNumber());
            saveChannelInfo.setFrequency(objChannel.getFreq());
            saveChannelInfo.setServiceName(objChannel.getName());
            saveChannelInfo.setSkip(objChannel.getIsSkip());
            saveChannelInfo.setChannelDeleted(objChannel.getIsDelete());
            byte[] data = new byte[8];
            data[0] = (byte) (objChannel.getIsFav()?1:0);
            saveChannelInfo.setCustomData(data);
            if(saveChannelInfo instanceof MtkTvAnalogChannelInfo)
            {
                MtkTvAnalogChannelInfo saveAtvInfo = (MtkTvAnalogChannelInfo) saveChannelInfo;
                saveAtvInfo.setColorSys(TvCommonImpl.getInstance().getMtkColoreSystem(objChannel.getAtvAttr().getColorSystem()));
                int[] result =  TvCommonImpl.getInstance().getMtkSoundSystem(objChannel.getAtvAttr().getSoundSystem());
                saveAtvInfo.setTvSys(result[0]);
                saveAtvInfo.setAudioSys(result[1]);
            }
            List<MtkTvChannelInfoBase> list = new ArrayList<>();
            list.add(saveChannelInfo);
            if(mObj_MtkTvChannelList.setChannelList(CHLST_OPERATOR_MOD,list)==0)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteChannel(Channel objChannel) {
        return false;
    }

    @Override
    public int getChannelCount(EN_OCHANNEL_COUNT_TYPE type) {
        switch (type)
        {
            case E_OCOUNT_ALL:
                return mObj_ChannelList_Atv.size()+mObj_ChannelList_Dvbc.size()+mObj_ChannelList_Dvbt.size();
            case E_OCOUNT_DTV:
                return mObj_ChannelList_Dvbc.size()+mObj_ChannelList_Dvbt.size();
            case E_OCOUNT_DTV_DVBC_ONLY:
                return mObj_ChannelList_Dvbc.size();
            case E_OCOUNT_DTV_DVBT_ONLY:
                return mObj_ChannelList_Dvbt.size();
            default:
                break;
        }
        return 0;
    }

    @Override
    public boolean gotoNextChannel(EN_CHANNEL_LIST_TYPE type) {
        Log.d("Oceanus","----------->gotoNextChannel");
        int selectId = 0;
        List<Channel> list = null;
        mCurrentChListType = type;
        if (type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL) {
            if (getChannelCount(EN_OCHANNEL_COUNT_TYPE.E_OCOUNT_ALL) == 0) {
                return false;
            }
            if (mObj_CurrentChannelId < mObj_ChannelList.size() - 1) {
                selectId = mObj_CurrentChannelId + 1;
            } else {
                selectId = 0;
            }
            list = mObj_ChannelList;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
        {
            if (mObj_CurrentAtvChannelId < (mObj_ChannelList_Atv.size()-1)) {
                    selectId = mObj_CurrentAtvChannelId + 1;
            } else {
                selectId = 0;
            }
            list = mObj_ChannelList_Atv;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
        {
            if (mObj_CurrentDvbtChannelId <(mObj_ChannelList_Dvbt.size()-1)) {
                selectId = mObj_CurrentDvbtChannelId + 1;
            } else {
                selectId = 0;
            }
            list = mObj_ChannelList_Dvbt;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
        {
            if (mObj_CurrentDvbcChannelId < (mObj_ChannelList_Dvbc.size()-1)) {
                selectId = mObj_CurrentDvbcChannelId + 1;
            } else {
                selectId = 0;
            }
            list = mObj_ChannelList_Dvbc;
        }
        else
        {
            return false;
        }
        if(list.size() == 0)
        {
            return false;
        }
        return gotoChannel(list.get(selectId));
    }

    @Override
    public boolean gotoPrevChannel(EN_CHANNEL_LIST_TYPE type) {
        Log.d("Oceanus","----------->gotoPrevChannel");
        int selectId = 0;
        List<Channel> list = null;
        mCurrentChListType = type;
        if (type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL) {
            if (getChannelCount(EN_OCHANNEL_COUNT_TYPE.E_OCOUNT_ALL) == 0) {
                return false;
            }
            if (mObj_CurrentChannelId > 0) {
                selectId = mObj_CurrentChannelId - 1;
            } else {
                selectId = mObj_ChannelList.size() - 1;
            }
            list = mObj_ChannelList;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
        {
            if (mObj_CurrentAtvChannelId > 0) {
                selectId = mObj_CurrentAtvChannelId - 1;
            } else {
                selectId = mObj_ChannelList_Atv.size() - 1;
            }
            list = mObj_ChannelList_Atv;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
        {
            if (mObj_CurrentDvbtChannelId > 0) {
                selectId = mObj_CurrentDvbtChannelId - 1;
            } else {
                selectId = mObj_ChannelList_Dvbt.size() - 1;
            }
            list = mObj_ChannelList_Dvbt;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
        {
            if (mObj_CurrentDvbcChannelId > 0) {
                selectId = mObj_CurrentDvbcChannelId - 1;
            } else {
                selectId = mObj_ChannelList_Dvbc.size() - 1;
            }
            list = mObj_ChannelList_Dvbc;
        }
        else
        {
            return false;
        }
        if(list.size() == 0)
        {
            return false;
        }
        return gotoChannel(list.get(selectId));
    }

    @Override
    public boolean gotoLastChannel() {
        switch (mCurrentChListType)
        {
            case E_CHANNEL_LIST_TYPE_ALL:
            {
                if(mObj_ChannelList.size()==0)
                {
                    return false;
                }
                return gotoChannel(mObj_ChannelList.get(mObj_LastChannelId));
            }
            case E_CHANNEL_LIST_TYPE_ATV:
            {
                if(mObj_ChannelList_Atv.size()==0)
                {
                    return false;
                }
                return gotoChannel(mObj_ChannelList_Atv.get(mObj_LastAtvChannelId));
            }
            case E_CHANNEL_LIST_TYPE_DVBC:
            {
                if(mObj_ChannelList_Dvbc.size()==0)
                {
                    return false;
                }
                return gotoChannel(mObj_ChannelList_Dvbc.get(mObj_LastDvbcChannelId));
            }
            case E_CHANNEL_LIST_TYPE_DVBT:
            {
                if(mObj_ChannelList_Dvbt.size()==0)
                {
                    return false;
                }
                return gotoChannel(mObj_ChannelList_Dvbt.get(mObj_LastDvbtChannelId));
            }
            default:break;
        }
       return false;
    }

    @Override
    public boolean gotoChannel(Channel objChannel) {
        boolean result = false;
        EN_INPUT_SOURCE_TYPE sourceType = SourceManager.getInstance().getCurSource().getType();
        Log.d("Oceanus","gotoChannel-----current source: " + sourceType.toString());
        switch (objChannel.getType())
        {
            case E_SERVICE_ATV:
            {
                if(sourceType!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
                {
                    SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV);
                }
                if(mCurrentChListType!=EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
                {
                    mCurrentChListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV;
                }
            }
            break;
            case E_SERVICE_DTV_DVBC:
            {
                if(sourceType!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
                {
                    SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C);
                }
                if(mCurrentChListType!=EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
                {
                    mCurrentChListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC;
                }
            }
            break;
            case E_SERVICE_DTV_DVBT:
            {
                if(sourceType!=EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
                {
                    SourceManager.getInstance().setSource(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T);
                }
                if(mCurrentChListType!=EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
                {
                    mCurrentChListType = EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT;
                }
            }
            break;
            default:{
                Log.e("Oceanus","Current source: " + sourceType.toString() + " is not support now");
            }return false;
        }
        Log.d("Oceanus","goto channel Name:" + objChannel.getName());
        int selectIndex = objChannel.getDbIndex();
        if(selectIndex!=0)
        {
            if(mObj_MtkTvChannelSelecter.channelSelect(selectIndex,false) == 0)
            {
                if(mCurrentChListType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
                {
                    int selectId = getChannelIndexByMtkChannelId(selectIndex,mCurrentChListType);
                    if(selectId != mObj_CurrentChannelId)
                    {
                        mObj_LastChannelId = mObj_CurrentChannelId;
                        mObj_CurrentChannelId = selectId;
                    }
                    result = true;
                }
                else//other
                {
                    int selectId = getChannelIndexByMtkChannelId(selectIndex,mCurrentChListType);
                    if(mCurrentChListType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
                    {
                        if(selectId != mObj_CurrentAtvChannelId)
                        {
                            mObj_LastAtvChannelId = mObj_CurrentAtvChannelId;
                            mObj_CurrentAtvChannelId = selectId;
                        }
                        result = true;
                    }
                    else if(mCurrentChListType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
                    {
                        if(selectId != mObj_CurrentDvbtChannelId)
                        {
                            mObj_LastDvbtChannelId = mObj_CurrentDvbtChannelId;
                            mObj_CurrentDvbtChannelId = selectId;
                        }
                        result = true;
                    }
                    else if(mCurrentChListType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
                    {
                        if(selectId != mObj_CurrentDvbcChannelId)
                        {
                            mObj_LastDvbcChannelId = mObj_CurrentDvbcChannelId;
                            mObj_CurrentDvbcChannelId = selectId;
                        }
                        result = true;
                    }
                    else
                    {
                        Log.e("Oceanus","Current channel list type: " + mCurrentChListType.toString()+" is not support!");

                    }
                }
                Log.d("Oceanus","mObj_CurrentChannelId" + mObj_CurrentChannelId);
                Log.d("Oceanus","mObj_LastChannelId" + mObj_LastChannelId);
                Log.d("Oceanus","mObj_CurrentAtvChannelId" + mObj_CurrentAtvChannelId);
                Log.d("Oceanus","mObj_LastAtvChannelId" + mObj_LastAtvChannelId);
                Log.d("Oceanus","mObj_CurrentDvbtChannelId" + mObj_CurrentDvbtChannelId);
                Log.d("Oceanus","mObj_LastDvbtChannelId" + mObj_LastDvbtChannelId);
                Log.d("Oceanus","mObj_CurrentDvbcChannelId" + mObj_CurrentDvbcChannelId);
                Log.d("Oceanus","mObj_LastDvbcChannelId" + mObj_LastDvbcChannelId);
                if(result)
                {
                    Tv_EventInfo info = new Tv_EventInfo(EN_OSYSTEM_EVENT_LIST.E_SYSTEM_EVENT_CHANNEL_CHANGE.ordinal());
                    EventManager.getInstance().sendBroadcast(info);
                }
            }
        }
        return result;
    }

    @Override
    public List<Channel> queryChannels(EN_CHANNEL_LIST_TYPE type, int start_index, int number) {
        if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
        {
            if(start_index == 0 && number == 0)
            {
                if(mObj_ChannelList_Atv.size()==0 && mObj_ChannelList_Dvbc.size()==0 && mObj_ChannelList_Dvbt.size() ==0)
                {
                    return null;
                }
                else
                {
                    mObj_ChannelList.clear();
                    if(mObj_ChannelList_Atv.size()>0)
                    {
                        mObj_ChannelList.addAll(mObj_ChannelList_Atv);
                    }
                    if(mObj_ChannelList_Dvbt.size()>0)
                    {
                        mObj_ChannelList.addAll(mObj_ChannelList_Dvbt);
                    }
                    if(mObj_ChannelList_Dvbc.size()>0)
                    {
                        mObj_ChannelList.addAll(mObj_ChannelList_Dvbc);
                    }
                    return mObj_ChannelList;
                }
            }
            else
            {
               return queryChannels(mObj_ChannelList,start_index,number);
            }
        }
        else if (type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
        {
            if(SourceManager.getInstance().getCurSource().getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
            {
                Log.d("Oceanus","queryChannels-------------->Query Dvbt");
                mObj_ChannelList_Dvbt.clear();
                getChannelList();
                return queryChannels(mObj_ChannelList_Dvbt,start_index,number);
            }
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
        {
            if(SourceManager.getInstance().getCurSource().getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_ATV)
            {
                Log.d("Oceanus","queryChannels-------------->Query Atv");
                mObj_ChannelList_Atv.clear();
                getChannelList();
                return queryChannels(mObj_ChannelList_Atv,start_index,number);
            }
        }
        else if (type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
        {
            if(SourceManager.getInstance().getCurSource().getType() == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_C)
            {
                Log.d("Oceanus","queryChannels-------------->Query Dvbc");
                mObj_ChannelList_Dvbc.clear();
                getChannelList();
                return queryChannels(mObj_ChannelList_Dvbc,start_index,number);
            }
        }
        return null;
    }
    private List<Channel> queryChannels(List<Channel> chList,int start_index,int number)
    {
        int end_index = start_index + number -1;
        if(end_index <=0)
        {
            return chList;
        }
        List<Channel> currentQueryList = null;
        if(start_index < chList.size())
        {
            currentQueryList = new ArrayList<Channel>();
            int count = (end_index<=chList.size()-1)?(end_index+1):(chList.size());
            for(int i = start_index;i<count;i++)
            {
                currentQueryList.add(chList.get(i));
            }
            return currentQueryList;
        }
        return null;
    }
    private EN_CHANNEL_LIST_TYPE GetCurrentChannelListType()
    {
        if(mCurrentChListType == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
        {
            return EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL;
        }
        EN_INPUT_SOURCE_TYPE currentSourceType = SourceManager.getInstance().getCurSource().getType();
        switch (currentSourceType)
        {
            case E_INPUT_SOURCE_ATV:
                 return EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV;
            case E_INPUT_SOURCE_DTV_DVB_C:
                return EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC;
            case E_INPUT_SOURCE_DTV_DVB_T:
                return EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT;
            default:
            {
                Log.e("Oceanus","Current source type :" + currentSourceType + " is not support now!");
            }
            break;
        }
        return EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_NONE;
    }
    @Override
    public Channel getCurrentChannelInfo() {
        mCurrentChListType = GetCurrentChannelListType();
        switch (mCurrentChListType)
        {
            case E_CHANNEL_LIST_TYPE_ALL:
            {
                if(mObj_ChannelList.size()==0)
                {
                    return null;
                }
                return mObj_ChannelList.get(mObj_CurrentChannelId);
            }
            case E_CHANNEL_LIST_TYPE_ATV:
            {
                if(mObj_ChannelList_Atv.size()==0)
                {
                    return null;
                }
                return mObj_ChannelList_Atv.get(mObj_CurrentAtvChannelId);
            }
            case E_CHANNEL_LIST_TYPE_DVBC:
            {
                if(mObj_ChannelList_Dvbc.size()==0)
                {
                    return null;
                }
                return mObj_ChannelList_Dvbc.get(mObj_CurrentDvbcChannelId);
            }
            case E_CHANNEL_LIST_TYPE_DVBT:
            {
                if(mObj_ChannelList_Dvbt.size()==0)
                {
                    return null;
                }
                return mObj_ChannelList_Dvbt.get(mObj_CurrentDvbtChannelId);
            }
            default:break;
        }
        return null;
    }
    @Override
    public void cleanChannelList(EN_CHANNEL_LIST_TYPE type)
    {
        switch (type)
        {
            case E_CHANNEL_LIST_TYPE_ATV:
            {
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_AIR_SVLID,1);
                mObj_ChannelList_Atv.clear();
            }
            break;
            case E_CHANNEL_LIST_TYPE_DVBT:
            {
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_AIR_SVLID,2);
                mObj_ChannelList_Dvbt.clear();
            }
            break;
            case E_CHANNEL_LIST_TYPE_DVBC:
            {
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_CAB_SVLID,2);
                mObj_ChannelList_Dvbc.clear();
            }
            break;
            case E_CHANNEL_LIST_TYPE_ALL:
            default:
            {
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_AIR_SVLID,1);
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_AIR_SVLID,2);
                mObj_MtkTvChannelList.deleteChannelByBrdcstType(DB_CAB_SVLID,2);
                mObj_ChannelList.clear();
            }
            break;
        }
        clearCurrentChNumbers(type);
    }

    @Override
    public List<Channel> queryFavChannelList() {
        return null;
    }

    @Override
    public boolean addToFavChannelList(Channel objChannel) {
        return false;
    }

    @Override
    public boolean removeToFavChannelList(Channel objChannel) {
        return false;
    }

    private List<MtkTvChannelInfoBase> getChannelListByMaskFilter(int chId, int dir,int mask, int val)
    {
        Log.d("Oceanus", "getChannelListByMaskFilter chId = " + chId + "  dir = " + dir + "mask = " + mask + " val = " + val);
        int chLen = mObj_MtkTvChannelList.getChannelCountByMask(getSvl(), mask, val);
        if (chLen <= 0)
        {
            return new ArrayList<MtkTvChannelInfoBase>();
        }
        List<MtkTvChannelInfoBase> chList = mObj_MtkTvChannelList.getChannelListByMask(getSvl(), mask, val, dir, chId, chLen);
        return chList;
    }
    private void getChannelList()
    {
        if(mObj_MtkTvChannelList.getChannelCountByMask(getSvl(), MtkTvChCommonBase.SB_VNET_ACTIVE | MtkTvChCommonBase.SB_VNET_FAKE,MtkTvChCommonBase.SB_VNET_ACTIVE)>0)
        {
                List<MtkTvChannelInfoBase> chList = getChannelListByMaskFilter(0, MtkTvChannelList.CHLST_ITERATE_DIR_FROM_FIRST, MtkTvChCommonBase.SB_VNET_ALL, MtkTvChCommonBase.SB_VNET_ALL);
                for (int i=0;i<chList.size();i++)
                {
                    /*
                    Log.d("Oceanus","CH "+ i +": ChannelNumber : "+chList.get(i).getChannelNumber());
                    Log.d("Oceanus","CH "+ i +": ChannelIndex : "+chList.get(i).getChannelId());
                    Log.d("Oceanus","CH "+ i +": getSvlId : "+chList.get(i).getSvlId());
                    Log.d("Oceanus","CH "+ i +": getSvlRecId : "+chList.get(i).getSvlRecId());
                    Log.d("Oceanus","CH "+ i +": getFrequency : "+chList.get(i).getFrequency());
                    Log.d("Oceanus","CH "+ i +": getServiceName : "+chList.get(i).getServiceName());
                    Log.d("Oceanus","CH "+ i +": getBrdcstMedium : "+chList.get(i).getBrdcstMedium());
                    Log.d("Oceanus","CH "+ i +": getBrdcstType : "+chList.get(i).getBrdcstType());
                    Log.d("Oceanus","CH "+ i +": getCustomData : "+chList.get(i).getCustomData());
                    */
                    try {
                        pushChannel(chList.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
    private void pushChannel(MtkTvChannelInfoBase mtkChannel) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        if(mtkChannel.getServiceName() == null)
        {
            jsonObject.put("name",Channel.AtvName + String.valueOf(mtkChannel.getChannelNumber()));
        }
        else
        {
            jsonObject.put("name",mtkChannel.getServiceName());
        }
        jsonObject.put("dbIndex",mtkChannel.getChannelId());
        jsonObject.put("channelNumber",mtkChannel.getChannelNumber());
        jsonObject.put("freq",mtkChannel.getFrequency());
        jsonObject.put("isDelete",mtkChannel.isUserDelete());
        jsonObject.put("isFav", (mtkChannel.getCustomData()[0]==1));
        jsonObject.put("isLock",mtkChannel.isBlock());
        jsonObject.put("isSkip",mtkChannel.isSkip());
        jsonObject.put("logicNumber",mtkChannel.getChannelNumber());
        jsonObject.put("antennaType",mtkBroadcastMediumToOceanus(mtkChannel.getBrdcstMedium()).ordinal());
        if(mtkChannel.getBrdcstType() == BRDCST_TYPE_ANALOG)
        {
            jsonObject.put("type",EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV.ordinal());
            if(mtkChannel instanceof MtkTvAnalogChannelInfo)
            {
                jsonObject.put("coloreSystem", ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_AUTO.ordinal());
                jsonObject.put("soundSystem", ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_AUTO.ordinal());
                jsonObject.put("mtsMode", ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_AUTO.ordinal());
                jsonObject.put("soundCarrierFreq",-1);
            }
            else
            {
                jsonObject.put("coloreSystem", ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_AUTO.ordinal());
                jsonObject.put("soundSystem", ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_AUTO.ordinal());
                jsonObject.put("mtsMode", ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_AUTO.ordinal());
                jsonObject.put("soundCarrierFreq",-1);
            }
            //Log.d("Oceanus","################E_CHANNEL_LIST_TYPE_ATV###################");
            //Log.d("Oceanus",jsonObject.toString());
            Channel channel = new Channel(jsonObject);
            channel.getAtvAttr().putOtherInfo(MTK_SV_ID,mtkChannel.getSvlId());
            channel.getAtvAttr().putOtherInfo(MTK_SV_REC_ID,mtkChannel.getSvlRecId());
            mObj_ChannelList_Atv.add(channel);
            //Log.d("Oceanus","#######################################################");
        }
        else if (mtkChannel.getBrdcstType() == BRDCST_TYPE_DVB)
        {
            int MtkMedium = mtkChannel.getBrdcstMedium();
            if(MtkMedium == BRDCST_MEDIUM_DIG_TERRESTRIAL)
            {
                jsonObject.put("type",EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBT.ordinal());
                jsonObject.put("bIsScramble",((mtkChannel.getNwMask()|MtkTvChCommonBase.SB_VNET_SCRAMBLED)>0));
                jsonObject.put("videoformat",EN_VIDEO_FORMAT.E_VIDEO_FORMAT_UNKNOW.ordinal());
                jsonObject.put("audioformat",EN_AUDIO_FORMAT.EN_AUDIO_FORMAT_UNKNOW.ordinal());
                jsonObject.put("rev_3",false);
                //Log.d("Oceanus","################E_CHANNEL_LIST_TYPE_DVBT###################");
                //Log.d("Oceanus",jsonObject.toString());
                Channel channel = new Channel(jsonObject);
                channel.getDtvAttr().putOtherInfo(MTK_SV_ID,mtkChannel.getSvlId());
                channel.getDtvAttr().putOtherInfo(MTK_SV_REC_ID,mtkChannel.getSvlRecId());
                mObj_ChannelList_Dvbt.add(channel);
                //Log.d("Oceanus","#######################################################");
            }
            else if (MtkMedium == BRDCST_MEDIUM_DIG_CABLE)
            {
                jsonObject.put("type",EN_CHANNEL_SERVICE_TYPE.E_SERVICE_DTV_DVBC.ordinal());
                jsonObject.put("bIsScramble",((mtkChannel.getNwMask()|MtkTvChCommonBase.SB_VNET_SCRAMBLED)>0));
                jsonObject.put("videoformat",EN_VIDEO_FORMAT.E_VIDEO_FORMAT_UNKNOW.ordinal());
                jsonObject.put("audioformat",EN_AUDIO_FORMAT.EN_AUDIO_FORMAT_UNKNOW.ordinal());
                jsonObject.put("rev_3",false);
                //Log.d("Oceanus","################E_CHANNEL_LIST_TYPE_DVBC###################");
                //Log.d("Oceanus",jsonObject.toString());
                Channel channel = new Channel(jsonObject);
                channel.getDtvAttr().putOtherInfo(MTK_SV_ID,mtkChannel.getSvlId());
                channel.getDtvAttr().putOtherInfo(MTK_SV_REC_ID,mtkChannel.getSvlRecId());
                mObj_ChannelList_Dvbc.add(channel);
                //Log.d("Oceanus","#######################################################");
            }
            else
            {
                Log.e("Oceanus","unKnow service type");
            }
        }
    }
    private EN_ANTENNA_TYPE mtkBroadcastMediumToOceanus(int mtkMedium)
    {
        switch (mtkMedium)
        {
            case BRDCST_MEDIUM_ANA_TERRESTRIAL:
            case BRDCST_MEDIUM_DIG_TERRESTRIAL:
                return EN_ANTENNA_TYPE.E_ANTENNA_TYPE_AIR;
            case BRDCST_MEDIUM_DIG_CABLE:
            case BRDCST_MEDIUM_ANA_CABLE:
                return EN_ANTENNA_TYPE.E_ANTENNA_TYPE_CABLE;
            default:
                return EN_ANTENNA_TYPE.E_ANTENNA_TYPE_NONE;
        }
    }
    private int getSvl() {
        int svl = -1;
        int tunerMode = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_BS_BS_SRC);
        boolean flag =
                MtkTvConfig.getInstance().isConfigVisible(MtkTvConfigType.CFG_MISC_CH_LST_TYPE)
                        == MtkTvConfigType.CFGR_VISIBLE ? true : false;
        boolean hasCAM = false;
        if (flag) {
            int value = MtkTvConfig.getInstance().getConfigValue(MtkTvConfigType.CFG_MISC_CH_LST_TYPE);
            if (value > 0) {
                hasCAM = true;
            }
            Log.d("Ocecanus", "getSvl>>>>" + value);
        }
        switch (tunerMode) {
            case DB_AIR_OPTID:// T
                if (hasCAM) {
                    svl = DB_CI_PLUS_SVLID_AIR;
                } else {
                    svl = DB_AIR_SVLID;
                }
                break;
            case DB_CAB_OPTID:// C
                if (hasCAM) {
                    svl = DB_CI_PLUS_SVLID_CAB;
                } else {
                    svl = DB_CAB_SVLID;
                }
                break;
            case DB_SAT_OPTID:// S
            case DB_GENERAL_SAT_OPTID:// maybe has no this value when get tuner mode
                if (hasCAM) {
                    svl = DB_CI_PLUS_SVLID_SAT;
                } else {
                    int prefer = MtkTvConfig.getInstance().getConfigValue(
                            MtkTvConfigTypeBase.CFG_TWO_SAT_CHLIST_PREFERRED_SAT);
                    Log.d("Oceanus", "getSvl tunerMode,prefer =" + prefer);
                    if (prefer != 0) {
                        svl = DB_SAT_PRF_SVLID;
                    } else {
                        svl = DB_SAT_SVLID;
                    }
                }
                break;
            default:// default is T
                if (hasCAM) {
                    svl = DB_CI_PLUS_SVLID_AIR;
                } else {
                    svl = DB_AIR_SVLID;
                }
                break;
        }
        Log.d("Oceanus", "getSvl tunerMode =" + tunerMode + " svl =" + svl);
        return svl;
    }
    private int getChannelIndexByMtkChannelId(int mtkChannelId,EN_CHANNEL_LIST_TYPE type)
    {
        Log.d("Ocenaus","getChannelIndexByMtkChannelId: " + type.toString());
        List<Channel> getList = null;
        if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
        {
            getList = mObj_ChannelList_Atv;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
        {
            getList = mObj_ChannelList_Dvbt;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
        {
            getList = mObj_ChannelList_Dvbc;
        }
        else if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL)
        {
            getList = mObj_ChannelList;
        }
        else
        {
            Log.e("Oceanus","unknow list type" + type.toString());
            return 0;
        }
        for(int i = 0;i<getList.size();i++)
            {
                if(getList.get(i).getDbIndex() == mtkChannelId)
                {
                    Log.d("Oceanus","channel Name:" + getList.get(i).getName());
                    Log.d("Oceanus","channel type:" + getList.get(i).getType().toString());
                    Log.d("Oceanus","channel getFreq:" + getList.get(i).getFreq());
                    Log.d("Oceanus","channel Number:" + getList.get(i).getChannelNumber());
                    Log.d("Oceanus","channel MtkDbIndex:" + getList.get(i).getDbIndex());
                    return i;
                }
            }
        return 0;
    }
    private void clearCurrentChNumbers(EN_CHANNEL_LIST_TYPE type)
    {
        if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL || type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV)
        {
            mObj_CurrentAtvChannelId = 1;
            mObj_LastAtvChannelId = 1;
        }
        if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL || type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT)
        {
            mObj_CurrentDvbtChannelId = 1;
            mObj_LastDvbtChannelId = 1;
        }
        if(type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ALL || type == EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC)
        {
            mObj_CurrentDvbcChannelId = 1;
            mObj_LastDvbcChannelId = 1;
        }
    }
}
