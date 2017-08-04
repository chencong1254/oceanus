package Oceanus.Tv.Service.SoundManager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.FuncUtil;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE_FREQ_TYPE;


/**
 * Created by yangxiong on 2016/9/9.
 */
public class SoundMode {

    private SoundManager mSoundManager = SoundManager.getInstance();

    private EN_SOUND_MODE soundMode = EN_SOUND_MODE.E_SOUND_MODE_STANDARD;
    private HashMap<EN_SOUND_MODE_FREQ_TYPE,Integer> values = new HashMap<EN_SOUND_MODE_FREQ_TYPE, Integer>();

    public SoundMode(JSONObject jSoundMode) throws JSONException {
        if (jSoundMode != null) {
            this.soundMode = EN_SOUND_MODE.values( )[jSoundMode.getInt("soundMode")];
            values.put(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_120HZ,jSoundMode.getInt("value120hz"));
            values.put(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_500HZ,jSoundMode.getInt("value500hz"));
            values.put(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_1500HZ,jSoundMode.getInt("value1500hz"));
            values.put(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_5KHZ,jSoundMode.getInt("value5khz"));
            values.put(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_10kHZ,jSoundMode.getInt("value10khz"));
        }
    }

    private String toJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject( );
        jsonObject.put("soundMode", this.soundMode);
        jsonObject.put("value120hz", this.values.get(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_120HZ));
        jsonObject.put("value500hz", this.values.get(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_500HZ));
        jsonObject.put("value1500hz", this.values.get(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_1500HZ));
        jsonObject.put("value5khz", this.values.get(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_5KHZ));
        jsonObject.put("value10khz", this.values.get(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_10kHZ));
        return jsonObject.toString( );
    }

    public EN_SOUND_MODE getSoundMode() {
        return this.soundMode;
    }

    public void setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE freq_type, int hzValue)
        {
            if (mSoundManager != null)
                {
                    mSoundManager.setEqualizer(freq_type.freq(), hzValue);
                    values.put(freq_type,hzValue);
                }
        }

    public int getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE freq_type) {
        int soundModeHz;
        Integer  soundModeHzInteger = this.values.get(freq_type);
        if(soundModeHzInteger == null){
            soundModeHz = mSoundManager.getEqualizer(freq_type);
            values.put(freq_type,soundModeHz);
        }else {
            soundModeHz = soundModeHzInteger;
            if(notValid(soundModeHz))
            {
                soundModeHz = mSoundManager.getEqualizer(freq_type);
                values.put(freq_type,soundModeHz);
            }
        }
        return soundModeHz;
    }

    public void applyToUser()
    {
        this.soundMode = EN_SOUND_MODE.E_SOUND_MODE_USER;
        try {
            mSoundManager.setSoundMode(this.soundMode.ordinal(),this.toJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean notValid(int inputValue)
    {
        return (inputValue == FuncUtil.VALUE_INVALID)?true:false;
    }
}
