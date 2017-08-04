package Oceanus.Tv.Service.PictureManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.FuncUtil;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;

/**
 * Created by heji@skyworth.com on 2016/9/8.
 */
public class PictureMode {
    private PictureManager pictureManager = PictureManager.getInstance();
    private EN_PICTURE_MODE mode = EN_PICTURE_MODE.E_PICTURE_MODE_STANDARD;
    private int brightness = FuncUtil.VALUE_INVALID;
    private int contrast = FuncUtil.VALUE_INVALID;
    private int saturation = FuncUtil.VALUE_INVALID;
    private int hue = FuncUtil.VALUE_INVALID;
    private int sharpness = FuncUtil.VALUE_INVALID;
    private int backLight = FuncUtil.VALUE_INVALID;

    public PictureMode(JSONObject jPictureMode) throws JSONException {
        if(jPictureMode!=null)
        {
            this.mode = EN_PICTURE_MODE.values()[jPictureMode.getInt("mode")];
            this.brightness = jPictureMode.getInt("brightness");
            this.contrast = jPictureMode.getInt("contrast");
            this.saturation = jPictureMode.getInt("saturation");
            this.hue = jPictureMode.getInt("hue");
            this.sharpness = jPictureMode.getInt("sharpness");
            this.backLight = jPictureMode.getInt("backLight");
        }
    }
    protected String toJsonString() throws JSONException {
        JSONObject jPictureMode = new JSONObject();
        jPictureMode.put("mode",this.mode.ordinal());
        jPictureMode.put("brightness",this.brightness);
        jPictureMode.put("contrast",this.contrast);
        jPictureMode.put("saturation",this.saturation);
        jPictureMode.put("hue",this.hue);
        jPictureMode.put("sharpness",this.sharpness);
        jPictureMode.put("backLight",this.backLight);
        return jPictureMode.toString();
    }
    public EN_PICTURE_MODE getMode()
    {
        return this.mode;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
        if(pictureManager!=null)
        {
            pictureManager.setBrightness(this.brightness);
        }
    }


    public void setContrast(int contrast) {
        this.contrast = contrast;
        if(pictureManager!=null)
        {
            pictureManager.setContrast(this.contrast);
        }
    }



    public void setHue(int hue) {
        this.hue = hue;
        if(pictureManager!=null)
        {
            pictureManager.setHue(this.hue);
        }
    }


    public void setSaturation(int saturation) {
        this.saturation = saturation;
        if(pictureManager!=null)
        {
            pictureManager.setSaturation(this.saturation);
        }
    }



    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
        if(pictureManager!=null)
        {
            pictureManager.setSharpness(this.sharpness);
        }
    }



    public void setBackLight(int backLight) {
        this.backLight = backLight;
        if(pictureManager!=null)
        {
            pictureManager.setBackLight(this.backLight);
        }
    }

    public int getBrightness() {
        if(notValid(this.brightness)) {
            this.brightness = pictureManager.getBrightness();
        }
        return this.brightness;
    }
    public int getContrast() {
        if(notValid(this.contrast)) {
            this.contrast = pictureManager.getContrast();
        }
        return this.contrast;
    }
    public int getHue() {
        if(notValid(this.hue)){
            this.hue = pictureManager.getHue();
        }
        return this.hue;
    }
    public int getSaturation() {
        if(notValid(this.saturation)){
            this.saturation = pictureManager.getSaturation();
        }
        return this.saturation;
    }
    public int getSharpness() {
        if(notValid(this.sharpness)){
            this.sharpness = pictureManager.getSharpness();
        }
        return this.sharpness;
    }
    public int getBackLight() {
        if(notValid(this.backLight)){
            this.backLight = pictureManager.getBackLight();
        }
        return backLight;
    }

    public void applyToUser() throws JSONException {
        this.mode = EN_PICTURE_MODE.E_PICTURE_MODE_USER;
        pictureManager.setPictureMode(this);
    }
    private boolean notValid(int inputValue)
    {
        return (inputValue ==FuncUtil.VALUE_INVALID)?true:false;
    }
}