package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.ChannelScanManager.DtvSearchRequirement;

/**
 * Created by sky057509 on 2016/12/7.
 */
public interface IChannelScan {
    abstract public boolean startAtvSearch(EN_ATV_SCAN_MODE mode, int freq);
    abstract public boolean startDtvSearch(DtvSearchRequirement requirement);
    abstract public boolean stopAtvSearch();
    abstract public boolean stopDtvSearch();
    List<FreqPoint> getDvbtCurrentFreqPointTable();
    boolean isScanning();
}
