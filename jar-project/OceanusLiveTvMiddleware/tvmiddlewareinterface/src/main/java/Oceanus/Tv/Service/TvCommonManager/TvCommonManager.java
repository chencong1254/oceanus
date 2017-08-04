package Oceanus.Tv.Service.TvCommonManager;

import android.util.Log;

import Oceanus.Tv.ITvFunctionInterface.ITvCommon;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.TvCommonImpl;
import Oceanus.Tv.Service.ChannelManager.ChannelManager;
import Oceanus.Tv.Service.SourceManager.SourceManager;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.EN_SERVICE_STATUS;

/**
 * Created by heji@skyworth.com on 2016/8/15.
 */
public class TvCommonManager {
    private static final String LOG_TAG = "TvCommonManager";
    private static TvCommonManager mObj_TvCommonManager = null;
    private ITvCommon mObj_TvCommon = null;

    public static TvCommonManager getInstance() {
        synchronized (TvCommonManager.class) {
            if (mObj_TvCommonManager == null) {
                new TvCommonManager();
            }
        }
        return mObj_TvCommonManager;
    }

    private TvCommonManager() {
        Log.d(LOG_TAG, "TvCommonManager Created~");
        mObj_TvCommonManager = this;
        mObj_TvCommon = TvCommonImpl.getInstance();
        // Connect();
    }

    /*
    static
    {
        Log.d(LOG_TAG,"TvCommonManager Load library~");
        try{
            System.loadLibrary("JNI_OceanusTv_OClt_TvCommonManager");
        }
        catch (UnsatisfiedLinkError e)
        {
            Log.d(LOG_TAG, "Cannot load JNI_OceanusTv_OClt_TvCommonManager library:\n" + e.toString());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    */
    public void changeAtvColoreSystem(ATV.EN_COLOR_SYSTEM system) {
        mObj_TvCommon.changeAtvColorSystem(system,ChannelManager.getInstance().getCurrentChannelInfo());
    }
    public ATV.EN_SOUND_SYSTEM getAtvSoundSystem()
    {
        return mObj_TvCommon.getAtvSoundSystem();
    }
    public ATV.EN_COLOR_SYSTEM getAtvColorSystem()
    {
        return mObj_TvCommon.getAtvColorSystem();
    }
    public ATV.EN_ATV_MTS_MODE getAtvMtsMode()
    {
        return mObj_TvCommon.getAtvMtsMode();
    }

    public void changeAtvSoundSystem(ATV.EN_SOUND_SYSTEM system) {
        mObj_TvCommon.changeAtvSoundSystem(system,ChannelManager.getInstance().getCurrentChannelInfo());
    }

    public EN_SERVICE_STATUS getCurrentSignalStatus() {
        return mObj_TvCommon.getCurrentSignalStatus();
    }

    public String getCurrentVideoInfo() {
        EN_INPUT_SOURCE_TYPE currentSourceType = SourceManager.getInstance().getCurSource().getType();
        switch (currentSourceType) {
            case E_INPUT_SOURCE_ATV:
            case E_INPUT_SOURCE_DTV_ATSC:
            case E_INPUT_SOURCE_DTV_DVB_C:
            case E_INPUT_SOURCE_DTV_DVB_T:
            case E_INPUT_SOURCE_DTV_DVB_S:
            case E_INPUT_SOURCE_DTV_ISDB: {
                return mObj_TvCommon.getTvVideoInfo();
            }
            case E_INPUT_SOURCE_HDMI1:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
            case E_INPUT_SOURCE_CVBS: {
                return mObj_TvCommon.getVideoInfo();
            }
            default:
                break;
        }
        return null;
    }

    public String getCurrentAudioInfo() {
        return mObj_TvCommon.getAudioInfo();
    }

    public String getCurrentChannelRating() {
        return mObj_TvCommon.getRating();
    }

    public int getSignalLevel() {
        return mObj_TvCommon.getSignalLevel();
    }
    public int getSignalQuality() {
        return mObj_TvCommon.getSignalQuality();
    }
}
