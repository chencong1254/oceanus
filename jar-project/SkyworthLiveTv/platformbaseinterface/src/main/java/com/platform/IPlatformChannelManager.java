package com.platform;

import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelListInfo;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.SourceManager.Source;

/**
 * Created by sky057509 on 2017/5/3.
 */

public interface IPlatformChannelManager {
    @Nullable
    List<Channel> GetChannelList(EN_CHANNEL_LIST_TYPE listType, int startIndex, int number);
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    List<Channel> GetChannelList(Source source, int startIndex, int number);
    boolean GotoChannel(Channel channel);
    boolean GotoChannelByNumber(int channelNumber);
    void GotoNextChannel();
    void GotoPrevChannel();
    void GotoLastChannel();
    @Nullable
    Channel GetCurrentChannel();
    @Nullable
    List<ChannelListInfo> queryFavChannelListInfo();

    void createFavList(String mTopName, boolean b);
    @Nullable
    List<Channel> queryFavChannelList(ChannelListInfo channelListInfo);

    void saveChannel(Channel channel);

    void addToFavChannelList(String s, Channel curEditChannel);

    void removeToFavChannelList(String FavListName, Channel channel);

    void renameFavList(String listName, String s);
}
