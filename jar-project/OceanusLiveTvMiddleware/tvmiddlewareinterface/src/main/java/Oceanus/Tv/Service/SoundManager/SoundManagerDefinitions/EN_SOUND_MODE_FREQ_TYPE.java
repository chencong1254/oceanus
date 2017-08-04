package Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions;

/**
 * Created by yangxiong on 2016/9/9.
 */
public enum EN_SOUND_MODE_FREQ_TYPE {
    E_SOUND_MODE_FREQ_TYPE_120HZ(120),
    E_SOUND_MODE_FREQ_TYPE_500HZ(500),
    E_SOUND_MODE_FREQ_TYPE_1500HZ(1500),
    E_SOUND_MODE_FREQ_TYPE_5KHZ(5000),
    E_SOUND_MODE_FREQ_TYPE_10kHZ(10000);
    private int valueFreq;
    private EN_SOUND_MODE_FREQ_TYPE(int freq)
    {
        this.valueFreq = freq;
    }
    public int freq()
    {
        return this.valueFreq;
    }
}
