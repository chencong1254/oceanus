package Oceanus.Tv.Service.PictureManager;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_ANALOG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_MPEG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;


/**
 * Created by sky057509 on 2016/9/8.
 */
public class PictureSetting{
    private PictureManager pictureManager = PictureManager.getInstance();
    private EN_DISPLAY_MODE displayMode = EN_DISPLAY_MODE.E_DISPLAY_MODE_AUTO;
    private EN_MPEG_NR_MODE mpegNrMode = EN_MPEG_NR_MODE.E_MPEG_NR_INVALID;
    private EN_ANALOG_NR_MODE analogNrMode = EN_ANALOG_NR_MODE.E_ANALOG_NR_INVALID;
    private boolean bIsFilmMode = false;
    private boolean bIsDynamicContrast = false;
    private EN_PICTURE_MODE pictureMode = EN_PICTURE_MODE.E_PICTURE_MODE_STANDARD;
    private EN_COLOR_TEMPERATURE colorTemperatureMode = EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;
    protected PictureSetting(JSONObject jPictureSetting) throws JSONException {
        if(jPictureSetting!=null)
        {
            this.displayMode = EN_DISPLAY_MODE.values()[jPictureSetting.getInt("displayMode")];
            this.mpegNrMode = EN_MPEG_NR_MODE.values()[jPictureSetting.getInt("mpegNrMode")];
            this.analogNrMode = EN_ANALOG_NR_MODE.values()[jPictureSetting.getInt("analogNrMode")];
            this.bIsFilmMode = (jPictureSetting.getInt("bIsFilmMode") == 0)?false:true;
            this.bIsDynamicContrast = (jPictureSetting.getInt("bIsDynamicContrast") == 0)?false:true;
            this.pictureMode = EN_PICTURE_MODE.values()[jPictureSetting.getInt("pictureMode")];
            this.colorTemperatureMode = EN_COLOR_TEMPERATURE.values()[jPictureSetting.getInt("colorTemperatureMode")];
            Log.e("PictureSetting","bIsDynamicContrast:"+bIsDynamicContrast+"  pictureMode:"+pictureMode+"  colorTemperatureMode:"+colorTemperatureMode);
        }
    }
    public boolean isDLC() {
        return this.bIsDynamicContrast;
    }

    public void setDLC(boolean bIsDynamicContrast) {
        this.bIsDynamicContrast = bIsDynamicContrast;
        pictureManager.setDynamicContrast(this.bIsDynamicContrast);
    }

    public boolean isFilmMode() {
        return this.bIsFilmMode;
    }

    public void setFilmMode(boolean bIsFilmMode) {
        this.bIsFilmMode = bIsFilmMode;
        pictureManager.setFilmMode(this.bIsFilmMode);
    }

    public EN_ANALOG_NR_MODE getAnalogNrMode() {
        return this.analogNrMode;
    }

    public void setAnalogNrMode(EN_ANALOG_NR_MODE analogNrMode) {
        this.analogNrMode = analogNrMode;
        pictureManager.setAnalogNrMode(this.analogNrMode.ordinal());
    }

    public EN_MPEG_NR_MODE getMpegNrMode()
    {
        return this.mpegNrMode;
    }
    public void setMpegNrMode(EN_MPEG_NR_MODE mpegNrMode) {
        this.mpegNrMode = mpegNrMode;
        pictureManager.setMpegNrMode(this.mpegNrMode.ordinal());
    }
    public EN_COLOR_TEMPERATURE getColorTemperatureMode() {
        return this.colorTemperatureMode;
    }

    public void setColoreTemperatureMode(EN_COLOR_TEMPERATURE colorTemperatureMode) {
        this.colorTemperatureMode = colorTemperatureMode;
        pictureManager.setColorTemperature(this.colorTemperatureMode);
    }

    public EN_PICTURE_MODE getPictureMode() {
        return this.pictureMode;
    }
    public void setPictureMode(EN_PICTURE_MODE pictureMode) {
        this.pictureMode = pictureMode;
        pictureManager.setPictureMode(this.pictureMode);
    }
    public EN_DISPLAY_MODE getDisplayMode() {
        return this.displayMode;
    }
    public void setDisplayMode(EN_DISPLAY_MODE displayMode) {
        this.displayMode = displayMode;
        pictureManager.setAspectRatio(this.displayMode.ordinal());
    }
}

