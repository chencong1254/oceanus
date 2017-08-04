package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvBanner;
import com.mediatek.twoworlds.tv.MtkTvBroadcastBase;
import com.mediatek.twoworlds.tv.MtkTvChannelList;
import com.mediatek.twoworlds.tv.MtkTvScreenSaverBase;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvTimeFormatBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Oceanus.Tv.ITvFunctionInterface.ITvCommon;
import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_COUNTRY;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;

import static Oceanus.Tv.ITvFunctionInterface.TvFunction.ChannelImpl.MTK_SV_ID;
import static Oceanus.Tv.ITvFunctionInterface.TvFunction.ChannelImpl.MTK_SV_REC_ID;
import static Oceanus.Tv.ITvFunctionInterface.TvFunction.MenuConfigManager.TV_MTS_MODE;

/**
 * Created by sky057509 on 2016/12/28.
 */
public class TvCommonImpl implements ITvCommon{
    public static final int COLOR_SYS_UNKNOWN = -1;
    public static final int COLOR_SYS_NTSC = 0;
    public static final int COLOR_SYS_PAL = 1;
    public static final int COLOR_SYS_SECAM = 2;
    public static final int COLOR_SYS_NTSC_443 = 3;
    public static final int COLOR_SYS_PAL_M = 4;
    public static final int COLOR_SYS_PAL_N = 5;
    public static final int COLOR_SYS_PAL_60 = 6;
    public static final int AUDIO_SYS_UNKNOWN = 0;
    public static final int AUDIO_SYS_AM = 1;
    public static final int AUDIO_SYS_FM_MONO = 2;
    public static final int AUDIO_SYS_FM_EIA_J = 4;
    public static final int AUDIO_SYS_FM_A2 = 8;
    public static final int AUDIO_SYS_FM_A2_DK1 = 16;
    public static final int AUDIO_SYS_FM_A2_DK2 = 32;
    public static final int AUDIO_SYS_FM_RADIO = 64;
    public static final int AUDIO_SYS_NICAM = 128;
    public static final int AUDIO_SYS_BTSC = 256;
    public static final int SCC_AUD_MTS_UNKNOWN = 0;
    public static final int SCC_AUD_MTS_MONO = 1;
    public static final int SCC_AUD_MTS_STEREO = 2;
    public static final int SCC_AUD_MTS_SUB_LANG = 3;
    public static final int SCC_AUD_MTS_DUAL1 = 4;
    public static final int SCC_AUD_MTS_DUAL2 = 5;
    public static final int SCC_AUD_MTS_NICAM_MONO = 6;
    public static final int SCC_AUD_MTS_NICAM_STEREO = 7;
    public static final int SCC_AUD_MTS_NICAM_DUAL1 = 8;
    public static final int SCC_AUD_MTS_NICAM_DUAL2 = 9;
    public static final int SCC_AUD_MTS_FM_MONO = 10;
    public static final int SCC_AUD_MTS_FM_STEREO = 11;
    private static TvCommonImpl mObj_This = null;
    private MtkTvScreenSaverBase mObj_MtkScrrenService = null;
    private EN_SERVICE_STATUS currentSignalStatus = EN_SERVICE_STATUS.E_SERVICE_STATUS_UNKNONW_STATE;
    private MtkTvBroadcastBase mtkTvBroadcastBase = null;
    private Context m_pContext;
    private TvCommonImpl()
    {
        mtkTvBroadcastBase = new MtkTvBroadcastBase();
        mObj_MtkScrrenService = new MtkTvScreenSaverBase();
    }
    public static TvCommonImpl getInstance()
    {
        if(mObj_This == null)
        {
            mObj_This = new TvCommonImpl();
        }
        return mObj_This;
    }
    public void init(Context context)
    {
        m_pContext = context;
    }

    @Override
    public EN_COUNTRY getCurrentCountry() {
        return null;
    }

    @Override
    public boolean setCurrentCountry(EN_COUNTRY country) {
        return false;
    }

    @Override
    public ATV.EN_ATV_MTS_MODE getAtvMtsMode() {
        return getAtvMtsMode(MenuConfigManager.getInstance(m_pContext).getDefault(TV_MTS_MODE));
    }

    @Override
    public ATV.EN_COLOR_SYSTEM getAtvColorSystem() {
        return getAtvColorSystem(MtkTvUtil.getInstance().getColorSys());
    }

    @Override
    public ATV.EN_SOUND_SYSTEM getAtvSoundSystem() {
        Channel channel = ChannelImpl.getInstance().getCurrentChannelInfo();
        int id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_ID);
        int rec_id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_REC_ID);
        MtkTvChannelInfoBase info = MtkTvChannelList.getInstance().getChannelInfoBySvlRecId(id,rec_id);
        int tvsys  = ((MtkTvAnalogChannelInfo)info).getTvSys();
        return getAtvSoundSystem(tvsys);
    }

    int getMtkMtsMode(ATV.EN_ATV_MTS_MODE mode)
    {
        /*
        public static final int SCC_AUD_MTS_MONO = 1;
        public static final int SCC_AUD_MTS_STEREO = 2;
        public static final int SCC_AUD_MTS_SUB_LANG = 3;
        public static final int SCC_AUD_MTS_DUAL1 = 4;
        public static final int SCC_AUD_MTS_DUAL2 = 5;
        public static final int SCC_AUD_MTS_NICAM_MONO = 6;
        public static final int SCC_AUD_MTS_NICAM_STEREO = 7;
        public static final int SCC_AUD_MTS_NICAM_DUAL1 = 8;
        public static final int SCC_AUD_MTS_NICAM_DUAL2 = 9;
        public static final int SCC_AUD_MTS_FM_MONO = 10;
        public static final int SCC_AUD_MTS_FM_STEREO = 11;
        */
        switch (mode)
        {
            case E_ATV_MTS_MODE_NICAM_FORCED_MONO:
            case E_ATV_MTS_MODE_BTSC_MONO:
            {
                return SCC_AUD_MTS_MONO;
            }
            case E_ATV_MTS_MODE_BTSC_SAP:
            case E_ATV_MTS_MODE_BTSC_STEREO:
                return SCC_AUD_MTS_STEREO;
            case E_ATV_MTS_MODE_NICAM_DUAL_A:
                return SCC_AUD_MTS_NICAM_DUAL1;
            case E_ATV_MTS_MODE_NICAM_DUAL_B:
                return SCC_AUD_MTS_NICAM_DUAL2;
            case E_ATV_MTS_MODE_NICAM_DUAL_AB:
            case E_ATV_MTS_MODE_NICAM_STEREO:
            {
                return SCC_AUD_MTS_NICAM_STEREO;
            }
            case E_ATV_MTS_MODE_NICAM_MONO:
            {
                return SCC_AUD_MTS_NICAM_MONO;
            }

            default:
                return SCC_AUD_MTS_STEREO;
        }
    }
    @Override
    public void changeAtvMtsMode(ATV.EN_ATV_MTS_MODE mode) {
        MenuConfigManager.getInstance(m_pContext).setValue(TV_MTS_MODE,getMtkMtsMode(mode));
    }

    public ATV.EN_COLOR_SYSTEM getAtvColorSystem(int system) {
        switch (system)
        {
            case COLOR_SYS_UNKNOWN:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_UNKNOW;
            case COLOR_SYS_NTSC:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_NTSC;
            case COLOR_SYS_NTSC_443:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_NTSC_443;
            case COLOR_SYS_PAL:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_PAL;
            case COLOR_SYS_PAL_60:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_PAL_60;
            case COLOR_SYS_PAL_M:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_PAL_M;
            case COLOR_SYS_PAL_N:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_PAL_N;
            case COLOR_SYS_SECAM:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_SECAM;
            default:
                return ATV.EN_COLOR_SYSTEM.E_COLOR_STANDARD_UNKNOW;
        }

    }
    public ATV.EN_SOUND_SYSTEM getAtvSoundSystem(int tvsys) {
        tvsys = tvsys & 0x0000ffff;
        if (tvsys == MenuConfigManager.TV_SYS_MASK_L || tvsys == MenuConfigManager.TV_SYS_MASK_L_PRIME)
        {
            return ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_L;
        }
        else if (tvsys == MenuConfigManager.TV_SYS_MASK_I)
        {
            return ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_I;
        }
        else if (tvsys == (MenuConfigManager.TV_SYS_MASK_D | MenuConfigManager.TV_SYS_MASK_K))
        {
            return ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_DK;
        }
        else if(tvsys == (MenuConfigManager.TV_SYS_M | MenuConfigManager.TV_SYS_N))
        {
            return ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_M;
        }
        else /* TV_SYS_MASK_B | TV_SYS_MASK_G */
        {
                return ATV.EN_SOUND_SYSTEM.E_SOUND_SYSTEM_BG;
        }
    }
    public ATV.EN_ATV_MTS_MODE getAtvMtsMode(int audiosys) {
        Log.d("Oceanus","getAtvMtsMode: " + audiosys);
        switch (audiosys)
        {
            case AUDIO_SYS_AM:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_FM_MONO:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_MONO;
            case AUDIO_SYS_FM_EIA_J:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_FM_A2:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_FM_A2_DK1:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_FM_A2_DK2:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_FM_RADIO:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            case AUDIO_SYS_NICAM:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_NICAM_DUAL_AB;
            case AUDIO_SYS_BTSC:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_BTSC_STEREO;
            case AUDIO_SYS_UNKNOWN:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
            default:
                return ATV.EN_ATV_MTS_MODE.E_ATV_MTS_MODE_UNKNOW;
        }
    }
    /*<string-array name="menu_tv_color_system_array">
           <item>Auto</item>0
           <item>PAL</item>1
           <item>SECAM</item>2
           <item>NTSC</item>3*/
    int getMtkColoreSystem(ATV.EN_COLOR_SYSTEM system)
    {
        int mtk_colorSystem = 0;
        switch (system)
        {
            case E_COLOR_STANDARD_AUTO:
            {
                mtk_colorSystem = 0;
            }
            break;
            case E_COLOR_STANDARD_NTSC:
            case E_COLOR_STANDARD_NTSC_443:
            {
                mtk_colorSystem = 3;
            }
            break;
            case E_COLOR_STANDARD_PAL:
            case E_COLOR_STANDARD_PAL_60:
            case E_COLOR_STANDARD_PAL_M:
            case E_COLOR_STANDARD_PAL_N:
            {
                mtk_colorSystem = 1;
            }
            break;
            case E_COLOR_STANDARD_SECAM:
            case E_COLOR_STANDARD_SECAM_L:
            {
                mtk_colorSystem = 2;
            }
            break;
            default:mtk_colorSystem = 0;break;
        }
        return mtk_colorSystem;
    }
    @Override
    public void changeAtvColorSystem(ATV.EN_COLOR_SYSTEM system, Channel channel) {
        if(channel.getType()!= EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            return;
        }
        int id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_ID);
        int rec_id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_REC_ID);
        MtkTvAnalogChannelInfo analogChannelInfo;
        MtkTvChannelInfoBase info = MtkTvChannelList.getInstance().getChannelInfoBySvlRecId(id,rec_id);
        if(info instanceof MtkTvAnalogChannelInfo)
        {
            analogChannelInfo = (MtkTvAnalogChannelInfo) info;
            analogChannelInfo.setColorSys(getMtkColoreSystem(system));
            channel.getAtvAttr().setColorSystem(system);
            List<MtkTvChannelInfoBase> list = new ArrayList<MtkTvChannelInfoBase>();
            list.add(analogChannelInfo);
            MtkTvChannelList.getInstance().setChannelList(MtkTvChannelList.CHLST_OPERATOR_MOD, list);
        }
    }
    int[] getMtkSoundSystem(ATV.EN_SOUND_SYSTEM system)
    {
        int[] resualt = new int[2];
        switch (system)
        {
            case E_SOUND_SYSTEM_AUTO:
                resualt[0]    = MenuConfigManager.TV_SYS_MASK_B | MenuConfigManager.TV_SYS_MASK_G;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            case E_SOUND_SYSTEM_BG:
                resualt[0] = MenuConfigManager.TV_SYS_MASK_B | MenuConfigManager.TV_SYS_MASK_G;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            case E_SOUND_SYSTEM_L:
                resualt[0] = MenuConfigManager.TV_SYS_MASK_L;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_AM | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            case E_SOUND_SYSTEM_I:
                resualt[0]= MenuConfigManager.TV_SYS_MASK_I;
                resualt[1]= MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            case E_SOUND_SYSTEM_DK:
                resualt[0] = MenuConfigManager.TV_SYS_MASK_D | MenuConfigManager.TV_SYS_MASK_K;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            case E_SOUND_SYSTEM_M:
                resualt[0] = MenuConfigManager.TV_SYS_M;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
            default:
                resualt[0] = MenuConfigManager.TV_SYS_MASK_B | MenuConfigManager.TV_SYS_MASK_G;
                resualt[1] = MenuConfigManager.AUDIO_SYS_MASK_FM_MONO | MenuConfigManager.AUDIO_SYS_MASK_NICAM;
                break;
        }
        return resualt;
    }
    @Override
    public void changeAtvSoundSystem(ATV.EN_SOUND_SYSTEM system, Channel channel) {
        if(channel.getType()!= EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            return;
        }
        int id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_ID);
        int rec_id = (int) channel.getAtvAttr().getOtherInfo(MTK_SV_REC_ID);
        MtkTvAnalogChannelInfo analogChannelInfo;
        MtkTvChannelInfoBase info = MtkTvChannelList.getInstance().getChannelInfoBySvlRecId(id,rec_id);
        if(info instanceof MtkTvAnalogChannelInfo)
        {
            analogChannelInfo = (MtkTvAnalogChannelInfo) info;
            int ui4_reserve_mask =  analogChannelInfo.getTvSys()& 0xffff0000;
            int ui4_tv_sys = 0;
            int ui4_audio_sys = 0;
            ATV.EN_SOUND_SYSTEM currentChannelSoundSystem = channel.getAtvAttr().getSoundSystem();
            int[] result =  getMtkSoundSystem(currentChannelSoundSystem);
            ui4_tv_sys    = result[0] | ui4_reserve_mask;
            (analogChannelInfo).setTvSys(ui4_tv_sys);
            (analogChannelInfo).setAudioSys(result[1]);
            channel.getAtvAttr().setSoundSystem(system);
            List<MtkTvChannelInfoBase> list = new ArrayList<MtkTvChannelInfoBase>();
            list.add(analogChannelInfo);
            MtkTvChannelList.getInstance().setChannelList(MtkTvChannelList.CHLST_OPERATOR_MOD, list);
        }
    }

    @Override
    public EN_SERVICE_STATUS getCurrentSignalStatus() {
        int id = mObj_MtkScrrenService.getScrnSvrMsgID();
        Log.d("Oceanus","id: " + id);
        switch (id)
        {
            case 0:
            {
                if(ChannelImpl.getInstance().getCurrentChannelInfo() == null)
                {
                    return EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_CHANNEL;
                }
                else
                {
                    return EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL;
                }
            }
            case 7:
            case 8:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_HAS_SIGNAL;
            case 1:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_SIGNAL;
            case 2:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_NO_CHANNEL;
            case 3:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_UNSTABLE;
            case 4:
            case 5:
            case 6:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_LOCK_SIGNAL;
            case 9:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_AUDIO_ONLY;
            case 10:
                return EN_SERVICE_STATUS.E_SERVICE_STATUS_UNSUPPORT_SIGNAL;
            default:break;
        }
        return EN_SERVICE_STATUS.E_SERVICE_STATUS_UNKNONW_STATE;
    }

    @Override
    public int getSignalLevel() {
        return mtkTvBroadcastBase.getSignalLevel();
    }

    @Override
    public int getSignalQuality() {
        return mtkTvBroadcastBase.getSignalQuality();
    }
    @Override
    public String getTvVideoInfo()
    {
        return MtkTvBanner.getInstance().getVideoInfo();
    }
    @Override
    public String getAudioInfo()
    {
        return MtkTvBanner.getInstance().getAudioInfo();
    }
    @Override
    public String getRating()
    {
        Log.d("Oceanus","Current getRating: "+MtkTvBanner.getInstance().getRating());
        return MtkTvBanner.getInstance().getRating();
    }
    @Override
    public String getVideoInfo()
    {
        Log.d("Oceanus","Current getIptsRslt: "+MtkTvBanner.getInstance().getIptsRslt());
        return MtkTvBanner.getInstance().getIptsRslt();
    }
}
