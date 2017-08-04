package com.product.adpater.infobar;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.tv.TvInputInfo;
import android.media.tv.TvTrackInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Date;
import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.EpgManager.EpgEvent;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TimeManager.TimeManager;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.platformsupport.PlatformManager;
import skyworth.platformsupport.PlatformSourceManager;
import skyworth.platformsupport.componentSupport.PlatformTvView;
import skyworth.platformsupport.componentSupport.TifChannelInfo;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.info.adpater.InfoBarViewAdapterBase;

import static android.media.tv.TvTrackInfo.TYPE_AUDIO;
import static android.media.tv.TvTrackInfo.TYPE_VIDEO;
import static com.platform.IPlatformEpgManager.EPG_ONE_DAY_MILLISECOND;
import static skyworth.platformsupport.PlatformSourceManager.TIF_OBJ;
import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;

/**
 * Created by sky057509 on 2017/5/23.
 */

public class InfoBarViewAdapter extends InfoBarViewAdapterBase{
    public InfoBarViewAdapter(Context context)
    {
        super(context);
    }
    @Override
    public Drawable GetServiceTypeImage()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return null;
        }
        switch (currentChannel.getType())
        {
            case E_SERVICE_ATV:
                return m_pContext.getDrawable(R.drawable.icon_atv);
            case E_SERVICE_DTV_DTMB:
            case E_SERVICE_DTV_ISDB:
            case E_SERVICE_DTV_ATSC:
                return m_pContext.getDrawable(R.drawable.icon_dtv);
            case E_SERVICE_DTV_DVBC:
                return m_pContext.getDrawable(R.drawable.icon_dvbc);
            case E_SERVICE_DTV_DVBT:
                return m_pContext.getDrawable(R.drawable.icon_dvbt);
            case E_SERVICE_DTV_DVBS:
                return m_pContext.getDrawable(R.drawable.icon_dvbs);
            case E_SERVICE_OTHER_APP:
            {
                TifChannelInfo tifChannelInfo = (TifChannelInfo) currentChannel.getDtvAttr().getOtherInfo(TIF_OBJ);
                TvInputInfo inputInfo = PlatformManager.getInstance().GetTvInputManager().getTvInputInfo(tifChannelInfo.getInputId());
                return inputInfo != null ? inputInfo.loadIcon(m_pContext) : null;
            }
            default:
                break;
        }
        return null;
    }
    @Override
    public String GetCurrentTime()
    {
        EN_INPUT_SOURCE_TYPE type = PlatformSourceManager.getInstance().GetCurrentSource().getType();
        Date date = null;
        if(type == EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_APPLICATION)
        {
            date= TimeManager.getInstance().getSystemTime();
        }
        else
        {
            date= TimeManager.getInstance().getTvTime();
        }
        return TimeManager.getFormatterTimeStringByHHmmMMDDYY(date);
    }
    @TargetApi(Build.VERSION_CODES.N)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public String GetCurrentResolution()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null) {
            if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
            {
                PlatformTvView view = (PlatformTvView) PlatformManager.getInstance().GetPlatformView();
                String video_track = view.getSelectedTrack(TYPE_VIDEO);
                List<TvTrackInfo> trackInfos = view.getTracks(TYPE_VIDEO);
                if(trackInfos!=null)
                {
                    for(TvTrackInfo trackInfo:trackInfos)
                    {
                        if(video_track.compareTo(trackInfo.getId()) == 0)
                        {
                            return getScale(trackInfo.getVideoHeight(),trackInfo.getVideoWidth());
                        }
                    }
                }
            }
            else
            {
                String video_info = TvCommonManager.getInstance().getCurrentVideoInfo();
                if(video_info.compareTo("") == 0)
                {
                    return null;
                }
                return video_info;
            }
        }
        return null;
    }
    @Override
    public String GetAudioType()
    {
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if (currentChannel != null) {
            if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
            {
                PlatformTvView view = (PlatformTvView) PlatformManager.getInstance().GetPlatformView();
                String audio_track = view.getSelectedTrack(TYPE_AUDIO);
                if( audio_track== null)
                {
                    return null;
                }
                for(TvTrackInfo trackInfo:view.getTracks(TYPE_AUDIO))
                {
                    if(trackInfo.getId().compareTo(audio_track) == 0)
                    {
                        return trackInfo.getLanguage();
                    }
                }
                return null;
            }
            else
            {
                String audio_info = TvCommonManager.getInstance().getCurrentAudioInfo();
                if(audio_info.compareTo("") == 0)
                {
                    return null;
                }
                return audio_info;
            }
        }
        return null;
    }
    @Override
    public String GetCurrentChannelServiceTypeName()
    {
        String serviceType = "";
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null)
        {
            return serviceType;
        }
        if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV)
        {
            ATV.EN_COLOR_SYSTEM system = TvCommonManager.getInstance().getAtvColorSystem();
            Log.d(DEBUG_TAG,"SYSTEM: " + system.toString());
            switch (system)
            {
                case E_COLOR_STANDARD_NTSC:
                case E_COLOR_STANDARD_NTSC_443:
                    serviceType = "NTSC";
                    break;
                case E_COLOR_STANDARD_PAL:
                case E_COLOR_STANDARD_PAL_60:
                case E_COLOR_STANDARD_PAL_M:
                case E_COLOR_STANDARD_PAL_N:
                    serviceType = "PAL";
                    break;
                case E_COLOR_STANDARD_SECAM:
                case E_COLOR_STANDARD_SECAM_L:
                    serviceType = "SECAM";
                    break;
                default:serviceType = "ATV";
                    break;
            }
            ATV.EN_SOUND_SYSTEM sound = TvCommonManager.getInstance().getAtvSoundSystem();
            Log.d(DEBUG_TAG,"sound: " + sound.toString());
            switch (sound)
            {
                case E_SOUND_SYSTEM_BG:serviceType = serviceType + "/BG";break;
                case E_SOUND_SYSTEM_DK:serviceType = serviceType + "/DK";break;
                case E_SOUND_SYSTEM_I:serviceType = serviceType + "/I";break;
                case E_SOUND_SYSTEM_L:serviceType = serviceType + "/L";break;
                case E_SOUND_SYSTEM_M:serviceType = serviceType + "/M";break;
                case E_SOUND_SYSTEM_N:serviceType = serviceType + "/N";break;
                default:break;
            }
        }
        else if(currentChannel.getType() == EN_CHANNEL_SERVICE_TYPE.E_SERVICE_OTHER_APP)
        {
            TifChannelInfo tifChannel = (TifChannelInfo) currentChannel.getDtvAttr().getOtherInfo(TIF_OBJ);
            TvInputInfo inputInfo = PlatformManager.getInstance().GetTvInputManager().getTvInputInfo(tifChannel.getInputId());
            if (inputInfo != null) {
                serviceType = String.valueOf(inputInfo.loadLabel(m_pContext));
            }
        }
        else
        {
            serviceType = currentChannel.getType().getName();
        }
        return serviceType;
    }
    public String GetCurrentProgramDetial()
    {
        Channel channel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(channel!=null)
        {
            EpgEvent event = PlatformManager.getInstance().GetEpgManager().getPresentEvent(channel);
            if (event != null) {
                return event.getShortDescription();
            }
        }
        return null;
    }
    public String GetCurrentProgramTimeInfo()
    {
        Channel channel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(channel!=null)
        {
            EpgEvent epgEvent = PlatformManager.getInstance().GetEpgManager().getPresentEvent(channel);
            Date startTime = null;
            Date endTime = null;
            if (epgEvent != null) {
                startTime = epgEvent.getStartTime();
                endTime = epgEvent.getEndTime();
                if(epgEvent.getDuration() == EPG_ONE_DAY_MILLISECOND)
                {
                    return TimeManager.getFormatterTimeStringByHHmmMMDD(startTime) + " - " + TimeManager.getFormatterTimeStringByHHmmMMDD(endTime);
                }
                return TimeManager.getFormatterTimeStringByHHmm(startTime) + " - " + TimeManager.getFormatterTimeStringByHHmm(endTime);
            }
        }
        return null;
    }
    public String GetCurrentProgramTitle()
    {
        Channel channel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(channel!=null)
        {
            EpgEvent event = PlatformManager.getInstance().GetEpgManager().getPresentEvent(channel);
            if (event != null) {
                return event.getEventName()!=null?event.getEventName():event.getmProgramName();
            }
        }
        return null;
    }

    @Override
    public String GetNextProgramTitle() {
        Channel channel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(channel!=null)
        {
            EpgEvent event = PlatformManager.getInstance().GetEpgManager().getFollowEvent(channel);
            if (event != null) {
                return event.getEventName()!=null?event.getEventName():event.getmProgramName();
            }
        }
        return null;
    }

    @Override
    public String GetNextProgramTimeInfo() {
        Channel channel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(channel!=null)
        {
            EpgEvent epgEvent = PlatformManager.getInstance().GetEpgManager().getFollowEvent(channel);
            Date startTime = null;
            Date endTime = null;
            if (epgEvent != null) {
                startTime = epgEvent.getStartTime();
                endTime = epgEvent.getEndTime();
                return TimeManager.getFormatterTimeStringByHHmm(startTime) + "-" + TimeManager.getFormatterTimeStringByHHmm(endTime);
            }
        }
        return null;
    }
}
