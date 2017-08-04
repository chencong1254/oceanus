package skyworth.platformsupport.componentSupport;

import android.database.Cursor;
import android.media.tv.TvContract;

import com.google.android.media.tv.companionlibrary.model.Channel;
import com.google.android.media.tv.companionlibrary.model.InternalProviderData;

import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_ANTENNA_TYPE;
import skyworth.androidtvsupport.R;
import skyworth.platformsupport.PlatformManager;

import static android.media.tv.TvContract.Channels.VIDEO_RESOLUTION_FHD;
import static android.media.tv.TvContract.Channels.VIDEO_RESOLUTION_HD;
import static android.media.tv.TvContract.Channels.VIDEO_RESOLUTION_SD;
import static android.media.tv.TvContract.Channels.VIDEO_RESOLUTION_UHD;
import static skyworth.platformsupport.PlatformSourceManager.TIF_OBJ;

/**
 * Created by sky057509 on 2017/5/9.
 */

public class TifChannelInfo {
    private Channel tif_channel =null;
    public static String[] TifChannelProject = Channel.PROJECTION;
    public TifChannelInfo(Oceanus.Tv.Service.ChannelManager.Channel skyChannel,Cursor cursor)
    {
        tif_channel = Channel.fromCursor(cursor);
        String channelNumber = tif_channel.getDisplayNumber();
        String dump = null;
        for(int i:channelNumber.toCharArray())
        {
            if(i >58||i<48)
            {
                dump = String.valueOf((char)i);
            }
        }
        if(dump!=null&&channelNumber.contains(String.valueOf(dump)))
        {
            int index = channelNumber.indexOf(dump);
            String channelNumber_master = channelNumber.substring(0,index) ;
            String channelNumber_minor = channelNumber.substring(index+1) ;
            skyChannel.setChannelNumber(Integer.parseInt(channelNumber_master));
            skyChannel.setLogicNumber(Integer.parseInt(channelNumber_minor));
        }
        else
        {
            skyChannel.setChannelNumber(Integer.parseInt(channelNumber));
        }
        skyChannel.setName(tif_channel.getDisplayName());
        skyChannel.setAntennaType(EN_ANTENNA_TYPE.E_ANTENNA_TYPE_NONE);
        skyChannel.getDtvAttr().putOtherInfo(TIF_OBJ,this);
    }
    public String getDisplayNumber()
    {
        return tif_channel.getDisplayNumber();
    }
    public String getChannelLogo()
    {
        return tif_channel.getChannelLogo();
    }
    public int getAppLinkColor()
    {
        return tif_channel.getAppLinkColor();
    }
    public String getAppLinkIntentUri()
    {
        return tif_channel.getAppLinkIntentUri();
    }
    public String getAppLinkPosterArtUri()
    {
        return tif_channel.getAppLinkPosterArtUri();
    }
    public String getAppLinkText()
    {
        return tif_channel.getAppLinkText();
    }
    public byte[] getInternalProviderDataByteArray()
    {
        return tif_channel.getInternalProviderDataByteArray();
    }
    public String getNetworkAffiliation()
    {
        return tif_channel.getNetworkAffiliation();
    }
    public int getOriginalNetworkId()
    {
        return tif_channel.getOriginalNetworkId();
    }
    public int getTransportStreamId()
    {
        return tif_channel.getTransportStreamId();
    }
    public String getType()
    {
        return tif_channel.getType();
    }
    public String getInputId() {
        return tif_channel.getInputId();
    }

    public String getVideoResolution() {
        String videoResolution = TvContract.Channels.getVideoResolution(tif_channel.getVideoFormat());
        if(videoResolution!=null)
        {
            if(videoResolution.compareTo(VIDEO_RESOLUTION_SD) == 0)
            {
                return PlatformManager.getInstance().GetApplicationContext().getString(R.string.SD);
            }
            else if(videoResolution.compareTo(VIDEO_RESOLUTION_HD) == 0)
            {
                return PlatformManager.getInstance().GetApplicationContext().getString(R.string.HD);
            }
            else if(videoResolution.compareTo(VIDEO_RESOLUTION_FHD) == 0)
            {
                return PlatformManager.getInstance().GetApplicationContext().getString(R.string.FHD);
            }
            else if(videoResolution.compareTo(VIDEO_RESOLUTION_UHD) == 0)
            {
                return PlatformManager.getInstance().GetApplicationContext().getString(R.string.UHD);
            }
            else
            {
                return PlatformManager.getInstance().GetApplicationContext().getString(R.string.Unknow);
            }
        }
        return null;
    }

    public String getPackageName() {
        return tif_channel.getPackageName();
    }

    public String getIconUrl() {
        return tif_channel.getAppLinkIconUri();
    }

    public long getId() {
        return tif_channel.getId();
    }

    public String getDescription()
    {
        return tif_channel.getDescription();
    }

    public String getServiceType() {
        return tif_channel.getServiceType();
    }
    public InternalProviderData getInternalProviderData()
    {
        return tif_channel.getInternalProviderData();
    }
}
