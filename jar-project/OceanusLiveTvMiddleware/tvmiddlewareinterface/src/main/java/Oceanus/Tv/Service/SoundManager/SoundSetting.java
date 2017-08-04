package Oceanus.Tv.Service.SoundManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;

/**
 * Created by yangxiong on 2016/9/9.
 */
public class SoundSetting {
    private SoundManager mSoundManager = SoundManager.getInstance();

    private EN_SOUND_MODE soundMode= EN_SOUND_MODE.E_SOUND_MODE_STANDARD;
    private EN_SOUND_SPDIF_MODE spdifMode = EN_SOUND_SPDIF_MODE.E_SOUND_SPDIF_MODE_AUTO;
    private int spdifdelay;
    private int treble;
    private int bass;
    private int balance;
    private boolean isAvcEnable;
    private boolean isSurroundMode;
    private int adAbsoluteVolume;
    private boolean adEnable;
    private boolean autoHOHEnable;
    private boolean isAutoVolume;

    public SoundSetting(JSONObject jSoundSetting) throws JSONException {
        if(jSoundSetting!=null)
        {
            this.soundMode = EN_SOUND_MODE.values()[jSoundSetting.getInt("soundMode")];
            this.spdifMode = EN_SOUND_SPDIF_MODE.values()[jSoundSetting.getInt("spdifMode")];
            this.spdifdelay = jSoundSetting.optInt("spdifdelay");
            this.bass = jSoundSetting.getInt("bass");
            this.treble = jSoundSetting.getInt("treble");
            this.balance = jSoundSetting.getInt("balance");
            this.isAvcEnable =  (jSoundSetting.getInt("isAvcEnable") == 0)?false:true;
            this.isSurroundMode = (jSoundSetting.getInt("isSurroundMode") == 0)?false:true;
            this.adAbsoluteVolume = jSoundSetting.getInt("adAbsoluteVolume");
            this.adEnable =  (jSoundSetting.getInt("adEnable") == 0)?false:true;
            this.autoHOHEnable = (jSoundSetting.getInt("autoHOHEnable") == 0)?false:true;
            this.isAutoVolume = (jSoundSetting.getInt("isAutoVolume") == 0)?false:true;
        }
    }

    public EN_SOUND_MODE getSoundMode() {
        return this.soundMode;
    }
    public void setSoundMode(EN_SOUND_MODE soundMode) {
        this.soundMode = soundMode;
        mSoundManager.setSoundMode(this.soundMode);
    }

    public int getSpdifdelay() {
        return this.spdifdelay;
    }
    public void setSpdifdelay(int spdifdelay) {
        this.spdifdelay = spdifdelay;
        mSoundManager.setSpdifDelay(spdifdelay);
    }

    public EN_SOUND_SPDIF_MODE getSpdifMode() {
        return this.spdifMode;
    }
    public void setSpdifMode(EN_SOUND_SPDIF_MODE spdifMode) {
        this.spdifMode = spdifMode;
        mSoundManager.setSpdifOutMode(this.spdifMode);
    }

    public int getTreble() {
        return this.treble;
    }
    public void setTreble(int treble) {
        this.treble = treble;
        mSoundManager.setTreble(this.treble);
    }
    public int getBass()
    {
        return this.bass;
    }
    public void setBass(int bass)
    {
        this.bass = bass;
        mSoundManager.setBass(bass);
    }
    public int getBalance() {
        return this.balance;
    }
    public void setBalance(int balance) {
        this.balance = balance;
        mSoundManager.setBalance(this.balance);
    }

    public boolean isAvcEnable() {
        return this.isAvcEnable;
    }
    public void setAvcEnable(boolean avcEnable) {
        this.isAvcEnable = avcEnable;
        mSoundManager.setAvcMode(this.isAvcEnable);
    }

    public boolean isSurroundMode() {
        return this.isSurroundMode;
    }
    public void setSurroundMode(boolean surroundMode) {
        this.isSurroundMode = surroundMode;
        mSoundManager.setAudioSurroundMode(this.isSurroundMode);
    }

    public int getAdAbsoluteVolume() {
        return this.adAbsoluteVolume;
    }
    public void setAdAbsoluteVolume(int adAbsoluteVolume) {
        this.adAbsoluteVolume = adAbsoluteVolume;
        mSoundManager.setADAbsoluteVolume(this.adAbsoluteVolume);
    }

    public boolean isAdEnable() {
        return this.adEnable;
    }
    public void setAdEnable(boolean adEnable) {
        this.adEnable = adEnable;
        mSoundManager.setADEnable(this.adEnable);
    }

    public boolean isAutoHOHEnable() {
        return this.autoHOHEnable;
    }
    public void setAutoHOHEnable(boolean autoHOHEnable) {
        this.autoHOHEnable = autoHOHEnable;
        mSoundManager.setAutoHOHEnable(this.autoHOHEnable);
    }

    public boolean isAutoVolume() {
        return this.isAutoVolume;
    }
    public void setAutoVolume(boolean autoVolume) {
        this.isAutoVolume = autoVolume;
        mSoundManager.setAutoVolume(this.isAutoVolume);
    }
}
