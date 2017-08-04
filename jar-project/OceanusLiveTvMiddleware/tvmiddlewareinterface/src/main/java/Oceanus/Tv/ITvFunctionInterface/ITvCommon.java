package Oceanus.Tv.ITvFunctionInterface;

import java.util.Date;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_COUNTRY;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;

/**
 * Created by sky057509 on 2016/12/28.
 */
public interface ITvCommon {
    public abstract EN_COUNTRY getCurrentCountry();
    public abstract boolean setCurrentCountry(EN_COUNTRY country);
    ATV.EN_ATV_MTS_MODE getAtvMtsMode();
    ATV.EN_COLOR_SYSTEM getAtvColorSystem();
    ATV.EN_SOUND_SYSTEM getAtvSoundSystem();
    /*<string-array name="menu_tv_color_system_array">
           <item>Auto</item>0
           <item>PAL</item>1
           <item>SECAM</item>2
           <item>NTSC</item>3*/
    void changeAtvMtsMode(ATV.EN_ATV_MTS_MODE mode);

    /*<string-array name="menu_tv_color_system_array">
                   <item>Auto</item>0
                   <item>PAL</item>1
                   <item>SECAM</item>2
                   <item>NTSC</item>3*/
    void changeAtvColorSystem(ATV.EN_COLOR_SYSTEM system, Channel channel);

    /*<string-array name="menu_tv_sound_system_array">
        <item>B/G</item>0
        <item>A2 B/G</item>1
        <item>I</item>2
        <item>D/K</item>3
        <item>A2 D/K</item>4
        <item>A2 D/K1</item>5
        <item>A2 D/K2</item>6
        <item>M</item>7
        </string-array>*/
    void changeAtvSoundSystem(ATV.EN_SOUND_SYSTEM system, Channel channel);

    public abstract EN_SERVICE_STATUS getCurrentSignalStatus();
    public abstract int getSignalLevel();
    public abstract int getSignalQuality();
    String getVideoInfo();
    String getTvVideoInfo();
    String getAudioInfo();
    String getRating();

}
