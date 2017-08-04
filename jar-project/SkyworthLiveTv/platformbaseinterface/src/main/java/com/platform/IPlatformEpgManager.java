package com.platform;

import android.support.annotation.Nullable;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.EpgManager.EPGEventFilter;
import Oceanus.Tv.Service.EpgManager.EpgEvent;

/**
 * Created by sky057509 on 2017/4/27.
 */

public interface IPlatformEpgManager {
    public final static long EPG_ONE_HOUR_MILLISECOND = 3600000;
    public final static long EPG_ONE_DAY_MILLISECOND  = 86400000;
    public final static long EPG_ONE_WEEK_MILLISECOND  = 604800000;
    public final static String Android_Tv_PosterArtUri = "Android_Tv_PosterArtUri";
    public final static String Android_Tv_ThumbnailUri = "Android_Tv_ThumbnailUri";
    public final static String Android_Tv_InternalProviderData = "Android_Tv_InternalProviderData";
    public boolean setEventLanguage(String firstEvtLang,String secondEvtLang);
    @Nullable
    public EpgEvent getPresentEvent(Channel channel);
    @Nullable
    public EpgEvent getFollowEvent(Channel channel);
    @Nullable
    public List<EpgEvent> getEventsByChannel(Channel channel,long startTime,long duration);
    @Nullable
    public List<EpgEvent> getEventsByFilter(EPGEventFilter filter);

}
