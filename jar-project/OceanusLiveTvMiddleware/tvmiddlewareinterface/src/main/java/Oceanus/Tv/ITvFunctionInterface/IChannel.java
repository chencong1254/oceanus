package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_OCHANNEL_COUNT_TYPE;

/**
 * Created by sky057509 on 2016/12/7.
 */
public interface IChannel {
    public abstract boolean saveChannel(Channel objChannel);
    public abstract boolean deleteChannel(Channel objChannel);
    public abstract int getChannelCount(EN_OCHANNEL_COUNT_TYPE type);
    public abstract boolean gotoNextChannel(EN_CHANNEL_LIST_TYPE type);
    public abstract boolean gotoPrevChannel(EN_CHANNEL_LIST_TYPE type);
    public abstract boolean gotoLastChannel();
    public abstract boolean gotoChannel(Channel objChannel);
    public abstract List<Channel> queryChannels(EN_CHANNEL_LIST_TYPE type, int start_index, int number);
    public abstract Channel getCurrentChannelInfo();
    public abstract void cleanChannelList(EN_CHANNEL_LIST_TYPE type);
    public abstract List<Channel> queryFavChannelList();
    public abstract boolean addToFavChannelList(Channel objChannel);
    public abstract boolean removeToFavChannelList(Channel objChannel);
}
