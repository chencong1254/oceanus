package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_OCHANNEL_COUNT_TYPE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE_FREQ_TYPE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;

/**
 * Created by sky057509 on 2016/12/7.
 */
public interface ISound {
    public abstract boolean setAudioVolume(int volume);
    public abstract int  getAudioVolume();
    public abstract boolean setEarPhoneVolume(int volume);
    public abstract int  getEarPhoneVolume();
    public abstract boolean setMuteFlag(boolean muteFlag);
    public abstract boolean getMuteFlag();

    public abstract boolean setSoundMode(int soundmode, String jStrSoundMode);
    public abstract boolean setTreble(int trebleValue);
    public abstract int getTreble();
    public abstract boolean setBass(int bassValue);
    public abstract int getBass();
    public abstract boolean setBalance(int balanceValue);
    public abstract int getBalance();
    public abstract boolean setAvcMode(boolean isAvcEnable);
    public abstract boolean setAudioSurroundMode(boolean isSurroundMode);
    public abstract boolean enableMute(boolean isEnableMute);
    public abstract void setADAbsoluteVolume(int volume);
    public abstract void setADEnable(boolean enable);
    public abstract void setAutoHOHEnable(boolean enable);
    public abstract boolean setSpdifOutMode(EN_SOUND_SPDIF_MODE spdifMode);
    public abstract boolean setSpdifDelay(int delay);
    public abstract void setEqualizer(int freq, int hzValue);//freq:20~200000 hzVale:0~100
    public abstract void setAutoVolume(boolean isAutoVolume);
    public abstract String getSupportSoundModesJsonString();
    public abstract String getCurrentSoundSettingJsonString();
    public abstract int getEqualizer(EN_SOUND_MODE_FREQ_TYPE freq_type);
}
