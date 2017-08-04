
package com.mediatek.tvinput.dtv;

import java.util.List;

import android.content.ContentResolver;
import android.media.tv.TvContract;
import android.util.Log;

import com.mediatek.tvinput.AbstractSetupActivety;
import com.mediatek.tvinput.Channel;
import com.mediatek.tvinput.Utils;
//TODO Need twoworld API
//import com.mediatek.twoworlds.tv.MtkTvChannelList;
//import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
//import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;

/**
 * For DTV channel setup<BR>
 * When first tune to DTV input,if no channels in database,then build channel database with SVL
 */
public class DTVChanelSetup {
  private static final String TAG = "MtkTvInput(InputSetup)";
  private final AbstractSetupActivety activity;

  public DTVChanelSetup(AbstractSetupActivety activity) {
    this.activity = activity;
  }

  // TODO Need twoworld API
  // public static List<MtkTvChannelInfoBase> getAllChannels(int svlId) {
  // int num = 0;
  // List<MtkTvChannelInfoBase> list;
  // // Turneky IF
  // {
  // MtkTvChannelList channelListCtrl = MtkTvChannelList.getInstance();
  // num = channelListCtrl.getChannelCountByFilter(svlId, MtkTvChCommonBase.SB_VNET_ALL);
  // list = channelListCtrl.getChannelListByFilter(svlId, MtkTvChCommonBase.SB_VNET_ALL, 0, 0, num);
  // }
  // return list;
  // }

  private boolean hasChannel() {
    return Utils.hasChannel(activity, activity.getContentResolver(), activity.getInputId());
  }

  // TODO Need twoworld API
  // public void checkChannelDataBase() {
  // // clearInputChannel();// just test
  // if (!hasChannel()) {
  // List<MtkTvChannelInfoBase> channels = getAllChannels(1);// TODO using correct SVL ID
  // int size = channels.size();
  // MtkTvChannelInfoBase channel;
  // for (int i = 0; i < size; i++) {
  // channel = channels.get(i);
  // Log.d(TAG, "ChannelNumber=" + channel.getChannelNumber() + " ChannelName" +
  // channel.getServiceName());
  // buildInputChannel(channel);
  // }
  // }
  // }

  // private void clearInputChannel() {
  // Utils.clearInputChannel(activity.getContentResolver(), activity.getInputId());
  // }

  // TODO Need twoworld API
  // private void buildInputChannel(MtkTvChannelInfoBase channel) {
  // ContentResolver contentResolver = activity.getContentResolver();
  // String data = channel.getSvlId() + "," + channel.getSvlRecId() + ",";
  //
  // String inputId = activity.getInputId();
  // Channel c = new Channel.Builder()//
  // .setInputId(inputId)//
  // .setDisplayNumber(String.valueOf(channel.getChannelNumber()))//
  // .setDisplayName(channel.getServiceName())//
  // .setData(data.getBytes())//
  // .build();
  // contentResolver.insert(TvContract.Channels.CONTENT_URI, c.toContentValues());
  // }
}
