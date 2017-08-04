package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import com.mediatek.twoworlds.tv.common.MtkTvCfgType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;

/**
 * Created by xeasy on 2017/1/9.
 */

public class TvUtil {

    public static EN_SOUND_MODE transToSkySoundMode(int mtkSoundMode) {
        switch (mtkSoundMode) {
            case MtkTvConfigType.AUDIO_MODE_LIVE1:
                return EN_SOUND_MODE.E_SOUND_MODE_MOVIE;
            case MtkTvConfigType.AUDIO_MODE_STANDARD:
                return EN_SOUND_MODE.E_SOUND_MODE_STANDARD;
            case MtkTvConfigType.AUDIO_MODE_USER:
                return EN_SOUND_MODE.E_SOUND_MODE_USER;
            default:
                return EN_SOUND_MODE.E_SOUND_MODE_STANDARD;

        }
    }
    public static int transToMtkSoundMode(EN_SOUND_MODE skySoundMode) {
        switch (skySoundMode) {
            case E_SOUND_MODE_STANDARD:
                return MtkTvConfigType.AUDIO_MODE_STANDARD;
            case E_SOUND_MODE_MUSIC:
                return MtkTvConfigType.AUDIO_MODE_LIVE1;
            case E_SOUND_MODE_MOVIE:
                return MtkTvConfigType.AUDIO_MODE_LIVE1;
            case E_SOUND_MODE_SPORT:
                return MtkTvConfigType.AUDIO_MODE_LIVE1;
            case E_SOUND_MODE_USER:
                return MtkTvConfigType.AUDIO_MODE_USER;
            default:
                return MtkTvConfigType.AUDIO_MODE_STANDARD;
        }
    }

    public static EN_PICTURE_MODE transToSkyPicMode(int mtkPicMode) {
        switch (mtkPicMode) {
            case MtkTvConfigType.PICTURE_MODE_USER:
                return EN_PICTURE_MODE.E_PICTURE_MODE_USER;
            case MtkTvConfigType.PICTURE_MODE_CINEMA:
                return EN_PICTURE_MODE.E_PICTURE_MODE_STANDARD;
            case MtkTvConfigType.PICTURE_MODE_SPORT:
                return EN_PICTURE_MODE.E_PICTURE_MODE_SPORTS;
            case MtkTvConfigType.PICTURE_MODE_VIVID:
                return EN_PICTURE_MODE.E_PICTURE_MODE_VIVID;
            case MtkTvConfigType.PICTURE_MODE_HI_BRIGHT:
                return EN_PICTURE_MODE.E_PICTURE_MODE_VIVID;
            default:
                return EN_PICTURE_MODE.E_PICTURE_MODE_STANDARD;

        }
    }


//    public static final int PICTURE_MODE_USER = 0;
//    public static final int PICTURE_MODE_CINEMA = 1;
//    public static final int PICTURE_MODE_SPORT = 2;
//    public static final int PICTURE_MODE_VIVID = 3;
//    public static final int PICTURE_MODE_HI_BRIGHT = 4;
    public static int transToMtkPicMode(EN_PICTURE_MODE skyPicMode) {
        switch (skyPicMode) {
            case E_PICTURE_MODE_STANDARD:
                return MtkTvConfigType.PICTURE_MODE_CINEMA;
            case E_PICTURE_MODE_VIVID:
                return MtkTvConfigType.PICTURE_MODE_VIVID;
            case E_PICTURE_MODE_SOFT:
                return MtkTvConfigType.PICTURE_MODE_CINEMA;
            case E_PICTURE_MODE_USER:
                return MtkTvConfigType.PICTURE_MODE_USER;
            case E_PICTURE_MODE_GAME:
                return MtkTvConfigType.PICTURE_MODE_HI_BRIGHT;
            case E_PICTURE_MODE_SPORTS:
                return MtkTvConfigType.PICTURE_MODE_SPORT;
            case E_PICTURE_MODE_AUTO:
            case E_PICTURE_MODE_NATURAL:
            case E_PICTURE_MODE_PC:
            case E_PICTURE_MODE_DYMANIC:
            case E_PICTURE_MODE_INVALID:
                default:
                return MtkTvConfigType.PICTURE_MODE_VIVID;
        }
    }
    public static EN_DISPLAY_MODE transToSkyDisplayMode(int mtkTvDisplayMode)
    {
        // need fix, need add 4:3 mode , 16:9 mode
        switch (mtkTvDisplayMode) {
            case MtkTvConfigType.SCREEN_MODE_CUSTOM_DEF_0:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_DEFAULT;
            case MtkTvConfigType.SCREEN_MODE_NORMAL:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_AUTO;
            case MtkTvConfigType.SCREEN_MODE_LETTERBOX:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_MOVIE;
            case MtkTvConfigType.SCREEN_MODE_PAN_SCAN:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_JUST_SCAN;
            case MtkTvConfigType.SCREEN_MODE_NON_LINEAR_ZOOM:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_ZOOM1;
            case MtkTvConfigType.SCREEN_MODE_DOT_BY_DOT:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_P2P;
            default:
                return EN_DISPLAY_MODE.E_DISPLAY_MODE_AUTO;
        }
    }
    public static int transToMtkTvDisplayMode(EN_DISPLAY_MODE skyDisplayMode)
    {
        switch (skyDisplayMode) {
            case  E_DISPLAY_MODE_DEFAULT:
                return MtkTvConfigType.SCREEN_MODE_CUSTOM_DEF_0;
            case  E_DISPLAY_MODE_16_9:
                // need fix
                return MtkTvConfigType.SCREEN_MODE_NORMAL;
            case  E_DISPLAY_MODE_4_3:
                // need fix
                return MtkTvConfigType.SCREEN_MODE_NORMAL;
            // ** Auto
            case  E_DISPLAY_MODE_AUTO:
                // need fix
                return MtkTvConfigType.SCREEN_MODE_NORMAL;
            // ** Movie
            case  E_DISPLAY_MODE_MOVIE:
                return MtkTvConfigType.SCREEN_MODE_LETTERBOX;
            // ** Caption
            case  E_DISPLAY_MODE_CAPTION:
                return MtkTvConfigType.SCREEN_MODE_LETTERBOX;
            // ** Panorama
            case  E_DISPLAY_MODE_PANORAMA:
                return MtkTvConfigType.SCREEN_MODE_NON_LINEAR_ZOOM;
            // ** Person
            case  E_DISPLAY_MODE_PERSON:
                return MtkTvConfigType.SCREEN_MODE_LETTERBOX;
            // ** Just Scan
            case  E_DISPLAY_MODE_JUST_SCAN:
                return MtkTvConfigType.SCREEN_MODE_PAN_SCAN;
            // ** P2P
            case  E_DISPLAY_MODE_P2P:
                return MtkTvConfigType.SCREEN_MODE_DOT_BY_DOT;
            // ** ZOOM1
            case E_DISPLAY_MODE_ZOOM1:
                return MtkTvConfigType.SCREEN_MODE_NON_LINEAR_ZOOM;
            // ** ZOOM2
            case E_DISPLAY_MODE_ZOOM2:
                return MtkTvConfigType.SCREEN_MODE_NON_LINEAR_ZOOM;
            // ** Invalid
            case E_DISPLAY_MODE_INVALID:
                return MtkTvConfigType.SCREEN_MODE_NORMAL;
            default:
                return MtkTvConfigType.SCREEN_MODE_NORMAL;
        }
    }
    public static EN_COLOR_TEMPERATURE transToSkyColorTempeMode(int mtkColorTempMode)
    {
        switch(mtkColorTempMode)
        {
            case MtkTvCfgType.VID_CLR_TEMP_COOL:
                return EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_COOL;
            case  MtkTvCfgType.VID_CLR_TEMP_STANDARD:
                return EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;
            case MtkTvCfgType.VID_CLR_TEMP_USER:
                return EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_USER;
            case MtkTvCfgType.VID_CLR_TEMP_WARM:
                return EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_WARM;
            default:
                return EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;
        }
    }
    public static int transToMtkColorTempMode(EN_COLOR_TEMPERATURE skyColorTempMode)
    {
        switch(skyColorTempMode)
        {
            case E_COLOR_TEMPERATURE_WARM:
                return MtkTvCfgType.VID_CLR_TEMP_WARM;
            case E_COLOR_TEMPERATURE_STANDARD:
                return MtkTvCfgType.VID_CLR_TEMP_STANDARD;
            case E_COLOR_TEMPERATURE_COOL:
                return MtkTvCfgType.VID_CLR_TEMP_COOL;
            case E_COLOR_TEMPERATURE_USER:
                return MtkTvCfgType.VID_CLR_TEMP_USER;
            case E_COLOR_TEMPERATURE_INVALID:
                return MtkTvCfgType.VID_CLR_TEMP_STANDARD;
            default:
                return MtkTvCfgType.VID_CLR_TEMP_STANDARD;
        }
    }

    public static String transToMtkSoundEqBand(int freq){
        String soundEqBand = null;
        switch (freq){
            case 120:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_1;
                break;
            case 500:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_2;
                break;
            case 1500:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_3;
                break;
            case 5000:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_4;
                break;
            case 10000:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_5;
                break;
            default:
                soundEqBand = MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_1;
        }
        return soundEqBand;
    }
    public static int transToMtkSpdifOutMode(EN_SOUND_SPDIF_MODE skySpdifMode)
    {
        switch(skySpdifMode)
        {
            case E_SOUND_SPDIF_MODE_AUTO:
                return MtkTvCfgType.SPDIF_MODE_FMT_RAW;
            case E_SOUND_SPDIF_MODE_PCM:
                return MtkTvCfgType.SPDIF_MODE_FMT_PCM16;
            default:
                return MtkTvCfgType.SPDIF_MODE_FMT_RAW;
        }
    }
}
