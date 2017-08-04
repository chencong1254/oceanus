package skyworth.skyworthlivetv.osd.ui.info.adpater;

import android.content.Context;
import android.graphics.drawable.Drawable;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_ANTENNA_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.TeletextManager.TeletextManager;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.platformsupport.PlatformManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalDefinitions;

/**
 * Created by sky057509 on 2017/5/23.
 */

public abstract class InfoBarViewAdapterBase{
    protected Context m_pContext = null;
    public InfoBarViewAdapterBase(Context context) {
        m_pContext = context;
    }
    protected String getScale(int h,int w)
    {
        String Scale = "";
        if((h*16)/(w*9) <= 1)
        {
            Scale =  "16:9";
        }
        else if((h*4)/(w*3) <= 1)
        {
            Scale =  "4:3";
        }
        else if((h*16)/(w*10) <= 1)
        {
            Scale =  "16:10";
        }
        if(h <=480)
        {
            Scale =  Scale + " " + m_pContext.getString(R.string.SD);
        }
        else if(h>480 && h<=720)
        {
            Scale =  Scale + " " + m_pContext.getString(R.string.HD);
        }
        else if(h>720 && h<=1080)
        {
            Scale = Scale+" " + m_pContext.getString(R.string.FHD);
        }
        else if(h>1080 && h<=2160)
        {
            Scale = Scale+" " + m_pContext.getString(R.string.UHD);
        }
        return Scale;

    }
    public abstract Drawable GetServiceTypeImage();

    public abstract String GetCurrentTime();
    public abstract String GetCurrentResolution();
    public abstract String GetAudioType();
    public abstract String GetCurrentChannelServiceTypeName();
    public String GetCurrenChannelName() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return m_pContext.getString(R.string.NoChannel);
        }
        return currentChannel.getName();
    }

    public String GetCurrentChannelNumber() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return "0";
        }
        if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP&& currentChannel.getLogicNumber()!=0)
        {
            return currentChannel.getChannelNumber() + " - " + currentChannel.getLogicNumber();
        }
        return String.valueOf(currentChannel.getChannelNumber());
    }

    public int GetCurrentChannelLcnNumber() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return 0;
        }
        return currentChannel.getLogicNumber();
    }

    public boolean IsLocked() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        return currentChannel != null && currentChannel.getIsLock();
    }

    public boolean IsFav() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        return currentChannel != null && currentChannel.getIsFav();
    }

    public boolean IsSkip() {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        return currentChannel != null && currentChannel.getIsSkip();
    }

    public boolean HasGinga() {
        return false;
    }
    public EN_ANTENNA_TYPE GetAntennaType()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return EN_ANTENNA_TYPE.E_ANTENNA_TYPE_NONE;
        }
        return currentChannel.getAntennaType();
    }
    public boolean IsScramble() {
        Channel current = PlatformChannelManager.getInstance().GetCurrentChannel();
        return current != null && current.getType() != EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP && current.getType() != EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV && PlatformChannelManager.getInstance().GetCurrentChannel().getDtvAttr().getbIsScramble();
    }
    public String GetCurrentSubtitleInfo()
    {
        if(PlatformManager.getInstance().GetSubtitleManager().IsSubtitleEnable())
        {
            String SubtitleInfo = "Subtitle: ";
            return SubtitleInfo + PlatformManager.getInstance().GetSubtitleManager().GetSubtitleInfoById(PlatformManager.getInstance().GetSubtitleManager().GetCurrentSubtitleIndex());
        }
        return null;
    }
    public boolean IsHasSubtitle()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return false;
        }
        switch (currentChannel.getType())
        {
            case E_SERVICE_ATV:
            {
                if(GlobalDefinitions.IsTargetAtvSystem(GlobalDefinitions.atv_all,m_pContext))
                {
                    return PlatformManager.getInstance().GetSubtitleManager().IsSubtitleExist();
                }
                else if (GlobalDefinitions.IsTargetAtvSystem(GlobalDefinitions.atv_ntsc,m_pContext))
                {
                    return true;
                }
            }
            case E_SERVICE_DTV_DVBT:
            case E_SERVICE_DTV_DVBC:
            case E_SERVICE_DTV_DVBS:
            {
                return PlatformManager.getInstance().GetSubtitleManager().IsSubtitleExist();
            }
            default:
                break;
        }
        return false;
    }
    public int IsHasCCOrTT()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return 0;
        }
        switch (currentChannel.getType())
        {
            case E_SERVICE_ATV:
            {
                if(GlobalDefinitions.IsTargetAtvSystem(GlobalDefinitions.atv_all,m_pContext))
                {
                    if(TeletextManager.getInstance().IsTeletextExist())
                    {
                        return R.drawable.icon_text;
                    }
                    return 0;
                }
                else if (GlobalDefinitions.IsTargetAtvSystem(GlobalDefinitions.atv_ntsc,m_pContext))
                {
                        return R.drawable.icon_cc;
                }
            }
            case E_SERVICE_DTV_ISDB:
            case E_SERVICE_DTV_ATSC:
            {
                return R.drawable.icon_cc;
            }
            case E_SERVICE_DTV_DVBT:
            case E_SERVICE_DTV_DVBC:
            case E_SERVICE_DTV_DVBS:
            {
                if(TeletextManager.getInstance().IsTeletextExist())
                {
                    return R.drawable.icon_text;
                }
                return 0;
            }
            default:
                break;
        }
        return 0;
    }
    public String GetCurrentChannelRating()
    {
        String RatingInfo = TvCommonManager.getInstance().getCurrentChannelRating();
        if(RatingInfo.compareTo("")==0)
        {
            return null;
        }
        return RatingInfo;
    }
    public int GetSignalQuality()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null && currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
            return -1;
        }
        return TvCommonManager.getInstance().getSignalQuality();
    }
    public int GetSignalStrength()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null && currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP) {
            return -1;
        }
        return TvCommonManager.getInstance().getSignalLevel();
    }
    public abstract String GetCurrentProgramDetial();

    public abstract String GetCurrentProgramTimeInfo();
    public abstract String GetCurrentProgramTitle();
    public abstract String GetNextProgramTitle();
    public abstract String GetNextProgramTimeInfo();
}
