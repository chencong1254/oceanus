package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvAppTV;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvITVCallback;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvScanPalSecamBase;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.MtkTvVolCtrl;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.model.MtkTvDvbScanPara;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.ITvFunctionInterface.IChannelScan;
import Oceanus.Tv.ITvFunctionInterface.ISound;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.AtvScanGlobalDefinitions;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.DtvSearchRequirement;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_ANALOG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_MPEG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE_FREQ_TYPE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;
import Oceanus.Tv.Service.SoundManager.SoundMode;

/**
 * Created by sky057509 on 2016/12/7.
 */
public class SoundImpl implements ISound {
    private static final String TAG = "SoundImpl";
    private static SoundImpl mObj_This = null;
    //TV API
    private MtkTvVolCtrl mVolCtrl;
    private MtkTvConfig mConfig;
    private MtkTvAVMode mtkTvAVMode;
    private MtkTvUtil mtkTvUtil;

    private AudioManager mAudManager;
    private Context mContext;
    private int volumeMax = -1;
    private int volumeMin = -1;

    private boolean isAudio = false;
    private SoundImpl(Context context)
    {
        mContext = context;
        mAudManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
        isAudio = false;
        //        isAudio =  (1 == SystemProperties.getInt("ro.mtk.system.audiosync", 0)) ? true : false));
        mConfig = MtkTvConfig.getInstance();
        mVolCtrl = MtkTvVolCtrl.getInstance();
        mtkTvAVMode = MtkTvAVMode.getInstance();
        mtkTvUtil = MtkTvUtil.getInstance();
        int configVol = mConfig.getMinMaxConfigValue(MtkTvConfigType.CFG_AUD_VOLUME_ALL);

        if(isAudio){
            volumeMax = mAudManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            volumeMin = 0 ;// default 0
        }else{
            volumeMax = MtkTvConfig.getMaxValue(configVol);
            volumeMin = MtkTvConfig.getMinValue(configVol);

        }


    }
    public static SoundImpl getInstance(Context context)
    {
        if(mObj_This == null)
        {
            mObj_This = new SoundImpl(context);
            return mObj_This;
        }
        else
        {
            return mObj_This;
        }
    }

    @Override
    public boolean setAudioVolume(int volume) {
        int curVolume;
        if (volume <= this.volumeMax && volume >= volumeMin) {
            curVolume = volume;
        } else if(volume > volumeMax){
            curVolume = this.volumeMax;
        }else {
            curVolume = this.volumeMin;
        }
        Log.d("Oceanus","setVolume cur volume = "+curVolume +"isAudio = "+ isAudio);
        if(isAudio)
        {
            mAudManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
        }
        else{
            mAudManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
//            mConfig.setConfigValue(MtkTvConfigType.CFG_AUD_VOLUME_ALL, curVolume);
        }
        return false;
    }

    @Override
    public int getAudioVolume() {
        int volume = 0;
        if(isAudio){
            volume = mAudManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }else{
            volume = mConfig.getConfigValue(MtkTvConfigType.CFG_AUD_VOLUME_ALL);
        }
        Log.d("Oceanus","getVolume cur volume = "+volume);
        return volume;
    }

    @Override
    public boolean setEarPhoneVolume(int volume) {
        return false;
    }

    @Override
    public int getEarPhoneVolume() {
        return 0;
    }

    @Override
    public boolean setMuteFlag(boolean muteFlag) {
        mVolCtrl.setMute(muteFlag);
        return true;
    }

    @Override
    public boolean getMuteFlag() {
        boolean isMute = mVolCtrl.getMute();
        Log.d("Oceanus","isMute cur mute = "+isMute);
        return isMute;
    }

    @Override
    public boolean setSoundMode(int soundmode, String jStrSoundMode) {
        Log.i(TAG,"setSoundMode:"+soundmode);
        int mtkSndMode;
        SoundMode sndMode = null;
        EN_SOUND_MODE skySndMode = EN_SOUND_MODE.values()[soundmode];
        mtkSndMode = TvUtil.transToMtkSoundMode(skySndMode);
        mConfig.setConfigValue(MtkTvConfigType.CFG_AUD_SOUND_MODE, mtkSndMode);
        return true;
    }

    @Override
    public boolean setTreble(int trebleValue) {
        return false;
    }

    @Override
    public int getTreble() {
        return 0;
    }

    @Override
    public boolean setBass(int bassValue) {
        return false;
    }

    @Override
    public int getBass() {
        return 0;
    }

    @Override
    public boolean setBalance(int balanceValue) {
        Log.i(TAG,"setBalance");
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.BALANCE,balanceValue);
        return true;
    }

    @Override
    public int getBalance() {
        Log.i(TAG,"getBalance");
        return MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.BALANCE);
    }

    @Override
    public boolean setAvcMode(boolean isAvcEnable) {
        return false;
    }

    @Override
    public boolean setAudioSurroundMode(boolean isSurroundMode) {
        Log.i(TAG,"setAudioSurroundMode");
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.SRS_MODE, isSurroundMode ? 1 : 0);
        return true;
    }

    @Override
    public boolean enableMute(boolean isEnableMute) {
        return false;
    }

    @Override
    public void setADAbsoluteVolume(int volume) {
        Log.i(TAG,"setADAbsoluteVolume");
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.AD_VOLUME, volume);
    }

    @Override
    public void setADEnable(boolean enable) {
        //MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.AD);
    }

    @Override
    public void setAutoHOHEnable(boolean enable) {

    }

    @Override
    public boolean setSpdifOutMode(EN_SOUND_SPDIF_MODE spdifMode) {
        Log.i(TAG,"setSpdifMode:"+spdifMode);
        int spdifmode = TvUtil.transToMtkSpdifOutMode(spdifMode);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.SPDIF_MODE, spdifmode);
        return true;
    }

    @Override
    public boolean setSpdifDelay(int delay) {
        Log.i(TAG,"setSpdifDelay:"+delay);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.SPDIF_DELAY, delay);
        return true;
    }

    @Override
    public void setEqualizer(int freq, int hzValue) {
        Log.i(TAG,"setEqualizer:"+freq+",value:"+hzValue);
        String freqBand = TvUtil.transToMtkSoundEqBand(freq);
        mConfig.setConfigValue(freqBand, hzValue);
    }

    @Override
    public int getEqualizer(EN_SOUND_MODE_FREQ_TYPE freq_type) {
        Log.i(TAG,"getEqualizer");
        int equ = 0;
        switch (freq_type){
            case E_SOUND_MODE_FREQ_TYPE_120HZ:
                equ = MenuConfigManager.getInstance(mContext).getDefault(MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_1);
                break;
            case E_SOUND_MODE_FREQ_TYPE_500HZ:
                equ = MenuConfigManager.getInstance(mContext).getDefault(MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_2);
                break;
            case E_SOUND_MODE_FREQ_TYPE_1500HZ:
                equ = MenuConfigManager.getInstance(mContext).getDefault(MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_3);
                break;
            case E_SOUND_MODE_FREQ_TYPE_5KHZ:
                //equ = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.EQUALIZE);
                equ = MenuConfigManager.getInstance(mContext).getDefault(MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_4);
                break;
            case E_SOUND_MODE_FREQ_TYPE_10kHZ:
                equ = MenuConfigManager.getInstance(mContext).getDefault(MtkTvConfigType.CFG_AUD_AUD_EQ_BAND_5);
                break;
        }
        return equ;
    }

    @Override
    public void setAutoVolume(boolean isAutoVolume) {
        Log.i(TAG,"setAutoVolume");
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.AUTO_VOLUME, isAutoVolume ? 1 : 0);
    }

    @Override
    public String getSupportSoundModesJsonString() {
        JSONArray soundModesJsonArray = new JSONArray();
        JSONObject jSoundModeLive = getJSoundMode(TvUtil.transToSkySoundMode(MtkTvConfigType.AUDIO_MODE_LIVE1),0,0,0,0,0);
        JSONObject jSoundModeStandard = getJSoundMode(TvUtil.transToSkySoundMode(MtkTvConfigType.AUDIO_MODE_STANDARD),0,0,0,0,0);
        JSONObject jSoundModeUser = getJSoundMode(TvUtil.transToSkySoundMode(MtkTvConfigType.AUDIO_MODE_USER),0,0,0,0,0);
        soundModesJsonArray.put(jSoundModeLive);
        soundModesJsonArray.put(jSoundModeStandard);
        soundModesJsonArray.put(jSoundModeUser);
        return soundModesJsonArray.toString();
    }

    @Override
    public String getCurrentSoundSettingJsonString() {
        JSONObject jSoundetting = new JSONObject();
        jSoundetting = getJSoundSetting(EN_SOUND_MODE.E_SOUND_MODE_STANDARD,EN_SOUND_SPDIF_MODE.E_SOUND_SPDIF_MODE_AUTO, 50,
        50,50,true,true,
        50,true,true,true);
        return jSoundetting.toString();
    }

    private JSONObject getJSoundSetting(EN_SOUND_MODE soundMode,EN_SOUND_SPDIF_MODE spdifMode, int bass,
                                          int treble,int balance,boolean isAvcEnable,boolean isSurroundMode,
                                        int adAbsoluteVolume,boolean adEnable,boolean autoHOHEnable,boolean isAutoVolume)
    {
        JSONObject jSoundSetting = new JSONObject();
        try {
            jSoundSetting.put("soundMode",soundMode.ordinal());
            jSoundSetting.put("spdifMode", spdifMode.ordinal());
            jSoundSetting.put("bass",bass);
            jSoundSetting.put("treble",treble);
            jSoundSetting.put("balance",balance);
            jSoundSetting.put("isAvcEnable",isAvcEnable?1:0);
            jSoundSetting.put("isSurroundMode",isSurroundMode?1:0);
            jSoundSetting.put("adAbsoluteVolume",adAbsoluteVolume);
            jSoundSetting.put("adEnable",adEnable?1:0);
            jSoundSetting.put("autoHOHEnable",autoHOHEnable?1:0);
            jSoundSetting.put("isAutoVolume",isAutoVolume?1:0);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jSoundSetting;
    }
    private JSONObject getJSoundMode(EN_SOUND_MODE soundMode,int value120hz,int value500hz,int value1500hz,int value5khz,int value10khz)
    {
        JSONObject jSoundModeTmp = new JSONObject();
        try {
            jSoundModeTmp.put("soundMode",soundMode.ordinal());
            jSoundModeTmp.put("value120hz",value120hz);
            jSoundModeTmp.put("value500hz",value500hz);
            jSoundModeTmp.put("value1500hz",value1500hz);
            jSoundModeTmp.put("value5khz",value5khz);
            jSoundModeTmp.put("value10khz",value10khz);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jSoundModeTmp;
    }
}