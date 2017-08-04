package Oceanus.Tv.Service.SoundManager;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.SoundImpl;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE_FREQ_TYPE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class SoundManager {
    private static final String LOG_TAG = "Oceanus";
    private static SoundManager mObj_SoundManager;
    private static SoundImpl mInterface_Sound= null;
    private static Map<EN_SOUND_MODE, SoundMode> soundModes;

    public static SoundManager getInstance()
    {
        synchronized(SoundManager.class)
        {
            if (mObj_SoundManager == null)
            {
                new SoundManager();
            }
        }
        return mObj_SoundManager;
    }

    private SoundManager() {
        Log.d(LOG_TAG, "SoundManager Created~");
        mObj_SoundManager = this;
    }
    public void init(Context mContext)
    {
        mInterface_Sound = SoundImpl.getInstance(mContext);
        Connect( );
        getSupportSoundModes( );
    }
    @Override
    protected void finalize() throws Throwable {
        super.finalize( );
        if (soundModes != null) {
            soundModes.clear( );
        }
        Disconnect( );
    }
    public Map<EN_SOUND_MODE, SoundMode> getSupportSoundModes() {
        if (soundModes == null) {
            soundModes = new HashMap<EN_SOUND_MODE,SoundMode>();
            try {
                JSONArray soundModesJsonArray = new JSONArray(getSupportSoundModesJsonString( ));
                Log.d("Oceanus","soundModesJsonArray.length( )"+soundModesJsonArray.length( ));
                for (int i = 0; i < soundModesJsonArray.length( ); i++) {
                    JSONObject jSoundMode = soundModesJsonArray.getJSONObject(i);
                    Log.d("Oceanus","jSoundMode"+jSoundMode);
                    SoundMode mode = new SoundMode(jSoundMode);
                    soundModes.put(mode.getSoundMode( ), mode);
                }
                return soundModes;
            } catch (JSONException e) {
                e.printStackTrace( );
            }
        }
        return soundModes;
    }


    public SoundSetting getCurrentSoundSetting() {
        SoundSetting curSoundSetting = null;
        try {
            JSONObject pictureSetting = new JSONObject(getCurrentSoundSettingJsonString( ));
            curSoundSetting = new SoundSetting(pictureSetting);
            return curSoundSetting;
        } catch (JSONException e) {
            e.printStackTrace( );
        }
        return curSoundSetting;
    }

    public void setSoundMode(EN_SOUND_MODE soundMode) {
        setSoundMode(soundMode.ordinal( ), null);
    }

    public SoundMode getSoundMode(EN_SOUND_MODE soundMode) {
        if (soundModes != null) {
            return soundModes.get(soundMode);

        }
        return null;
    }
    public int getVolume()
    {
        return getAudioVolume();
    }
    public boolean setVolume(int volume)
    {
        if(volume>100||volume<0)
        {
            return false;
        }
        return setAudioVolume(volume);
    }
    public boolean isMute()
    {
        return getMuteFlag();
    }
    public boolean Mute()
    {
        return setMuteFlag(true);
    }
    public boolean unMute()
    {
        return setMuteFlag(false);
    }
    private boolean setAudioVolume(int volume)
    {
        return mInterface_Sound.setAudioVolume(volume);
    }
    private int  getAudioVolume()
    {
        return mInterface_Sound.getAudioVolume();
    }
    private boolean setEarPhoneVolume(int volume)
    {
        return mInterface_Sound.setEarPhoneVolume(volume);
    }
    private int  getEarPhoneVolume()
    {
        return mInterface_Sound.getEarPhoneVolume();
    }
    private boolean setMuteFlag(boolean muteFlag)
    {
        return mInterface_Sound.setMuteFlag(muteFlag);
    }
    private boolean getMuteFlag()
    {
        return mInterface_Sound.getMuteFlag();
    }
    //-----------------------------------------------------------
    protected boolean setSoundMode(int soundmode, String jStrSoundMode)
    {
        return mInterface_Sound.setSoundMode(soundmode,jStrSoundMode);
    }
    protected boolean setTreble(int trebleValue)
    {
        return mInterface_Sound.setTreble(trebleValue);
    }
    protected int getTreble()
    {
        return mInterface_Sound.getTreble();
    }
    protected boolean setBass(int bassValue)
    {
        return mInterface_Sound.setBass(bassValue);
    }
    protected int getBass()
    {
        return mInterface_Sound.getBass();
    }
    protected boolean setBalance(int balanceValue)
    {
        return mInterface_Sound.setBalance(balanceValue);
    }
    protected int getBalance()
    {
        return mInterface_Sound.getBalance();
    }
    protected boolean setAvcMode(boolean isAvcEnable)
    {
        return mInterface_Sound.setAvcMode(isAvcEnable);
    }
    protected boolean setAudioSurroundMode(boolean isSurroundMode)
    {
        return mInterface_Sound.setAudioSurroundMode(isSurroundMode);
    }
    protected boolean enableMute(boolean isEnableMute)
    {
        return mInterface_Sound.enableMute(isEnableMute);
    }
    protected void setADAbsoluteVolume(int volume)
    {
        mInterface_Sound.setADAbsoluteVolume(volume);
    }
    protected void setADEnable(boolean enable)
    {
        mInterface_Sound.setADEnable(enable);
    }
    protected void setAutoHOHEnable(boolean enable)
    {
        mInterface_Sound.setAutoHOHEnable(enable);
    }
    protected boolean setSpdifOutMode(EN_SOUND_SPDIF_MODE spdifMode)
    {
        return mInterface_Sound.setSpdifOutMode(spdifMode);
    }
    protected boolean setSpdifDelay(int spdifdelay)
    {
        return mInterface_Sound.setSpdifDelay(spdifdelay);
    }
    protected void setEqualizer(int freq, int hzValue)//freq:20~200000 hzVale:0~100
    {
        mInterface_Sound.setEqualizer(freq,hzValue);
    }
    protected int getEqualizer(EN_SOUND_MODE_FREQ_TYPE freq_type)
    {
        return mInterface_Sound.getEqualizer(freq_type);
    }
    protected void setAutoVolume(boolean isAutoVolume)
    {
        mInterface_Sound.setAutoVolume(isAutoVolume);
    }
    private String getSupportSoundModesJsonString()
    {
        return mInterface_Sound.getSupportSoundModesJsonString();
    }
    private String getCurrentSoundSettingJsonString()
    {
        return mInterface_Sound.getCurrentSoundSettingJsonString();
    }
    private void Connect()
    {

    }
    private void Disconnect()
    {

    }
}