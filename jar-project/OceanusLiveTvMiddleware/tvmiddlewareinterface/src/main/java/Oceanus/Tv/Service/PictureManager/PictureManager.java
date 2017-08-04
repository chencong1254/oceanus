package Oceanus.Tv.Service.PictureManager;

import android.content.Context;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import Oceanus.Tv.ITvFunctionInterface.TvFunction.PictureImpl;
import Oceanus.Tv.ITvFunctionInterface.TvFunction.SoundImpl;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;

/**
 * @ClassName PictureManager
 * @Description TODO
 * @author vary
 * @date 2016
 * @version TODO
 */
public class PictureManager {
    private static final String LOG_TAG = "PictureManager";
    private static PictureManager mObj_PictureManager = null;
    private static PictureImpl mInterface_Picture = null;
    private static HashMap<EN_PICTURE_MODE,PictureMode> pictureModes = null;
    private static HashMap<EN_COLOR_TEMPERATURE,ColorTemperature> ColorTemperatureModes = null;

    public static PictureManager getInstance()
    {
        synchronized(PictureManager.class)
            {
                if (mObj_PictureManager == null)
                {
                    new PictureManager();
                }
            }
        return mObj_PictureManager;
    }
    private PictureManager()
    {
        Log.d(LOG_TAG,"PictureManager Created~");
        mObj_PictureManager = this;

    }
    public void init(Context mContext)
    {
        Connect();
        mInterface_Picture = PictureImpl.getInstance(mContext);
        getSupportPictureModes();

        getSupportColorTemperatureModes();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Disconnect();
    }
    public List<EN_DISPLAY_MODE> getSupportDisplayModes()
    {
        List<EN_DISPLAY_MODE> displayModes = new ArrayList<EN_DISPLAY_MODE>();
        try {
            String json = getSupportDisplayModeJsonList();
            Log.e(LOG_TAG,"getSupportDisplayModes json:"+json);
            if(json!=null)
            {
                JSONArray displayModesJsonArray = new JSONArray(json);
                Log.e(LOG_TAG,"getSupportPictureModes displayModesJsonArray:"+displayModesJsonArray);
                if(displayModesJsonArray!=null)
                {
                    Log.e(LOG_TAG,"getSupportDisplayModes displayModesJsonArray.length():"+displayModesJsonArray.length());
                    for(int i = 0;i<displayModesJsonArray.length();i++)
                    {
                        JSONObject jDisplayMode = displayModesJsonArray.getJSONObject(i);
                        Log.e(LOG_TAG,"getSupportDisplayModes jDisplayMode:"+jDisplayMode);
                        EN_DISPLAY_MODE mode = EN_DISPLAY_MODE.values()[jDisplayMode.getInt("mode")];
                        displayModes.add(mode);
                    }
                }
                else
                {
                    Log.e(LOG_TAG,"Get displayModesJsonArray error~~~~~");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return displayModes;
    }
    public HashMap<EN_PICTURE_MODE,PictureMode> getSupportPictureModes()
    {
        if(pictureModes == null)
        {
            pictureModes = new HashMap<EN_PICTURE_MODE,PictureMode>();
            try {
                String json = getSupportPictureModeJsonList();
                Log.e(LOG_TAG,"getSupportPictureModes json:"+json);
                if(json!=null)
                {
                    JSONArray pictureModesJsonArray = new JSONArray(json);
                    Log.e(LOG_TAG,"getSupportPictureModes pictureModesJsonArray:"+pictureModesJsonArray);
                    if(pictureModesJsonArray!=null)
                    {
                        Log.e(LOG_TAG,"getSupportPictureModes pictureModesJsonArray.length():"+pictureModesJsonArray.length());
                        for(int i = 0;i<pictureModesJsonArray.length();i++)
                        {
                            JSONObject jPictureMode = pictureModesJsonArray.getJSONObject(i);
                            Log.e(LOG_TAG,"getSupportPictureModes jPictureMode:"+jPictureMode);
                            PictureMode mode = new PictureMode(jPictureMode);
                            pictureModes.put(mode.getMode(),mode);
                        }
                    }
                    else
                    {
                        Log.e(LOG_TAG,"Get pictureModesJsonArray error~~~~~");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return pictureModes;
    }
    public HashMap<EN_COLOR_TEMPERATURE, ColorTemperature> getSupportColorTemperatureModes()
    {
        if(ColorTemperatureModes == null)
        {
            ColorTemperatureModes = new HashMap<EN_COLOR_TEMPERATURE,ColorTemperature>();
            try {
                String  supportColoreTemperatureJsonList = getSupportColorTemperatureJsonList();
                Log.d("Oceanus","supportColoreTemperatureJsonList:"+supportColoreTemperatureJsonList);
                if(supportColoreTemperatureJsonList!=null)
                {
                    JSONArray ColoreTemperatureJsonArray = new JSONArray(supportColoreTemperatureJsonList);
                    if(ColoreTemperatureJsonArray!=null)
                    {
                        for(int i = 0;i<ColoreTemperatureJsonArray.length();i++)
                        {
                            JSONObject jColorTemperature = ColoreTemperatureJsonArray.getJSONObject(i);
                            ColorTemperature mode = new ColorTemperature(jColorTemperature);
                            ColorTemperatureModes.put(mode.getMode(),mode);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ColorTemperatureModes;
    }
    public PictureSetting getCurrentPictureSetting()
    {
        PictureSetting curPicSetting = null;
        try {
            String currentPictureSettingJsonString = getCurrentPictureSettingJsonString();
            Log.d(LOG_TAG,"currentPictureSettingJsonString:"+currentPictureSettingJsonString);
            if(currentPictureSettingJsonString!=null)
            {
                JSONObject pictureSetting = new JSONObject(currentPictureSettingJsonString);
                curPicSetting = new PictureSetting(pictureSetting);

                Log.d(LOG_TAG,"currentPictureSettingJsonString:"+curPicSetting.getPictureMode());

                return curPicSetting;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"getCurrentPictureSetting error~~~~~");
            e.printStackTrace();
        }
        return curPicSetting;
    }
    protected void setPictureMode(EN_PICTURE_MODE mode)
    {
        setPictureMode(mode.ordinal(),null);
    }
    protected void setPictureMode(PictureMode mode) throws JSONException {
        pictureModes.remove(mode.getMode());
        pictureModes.put(mode.getMode(),mode);
        setPictureMode(mode.getMode().ordinal(),mode.toJsonString());
    }
    protected void setColorTemperature(EN_COLOR_TEMPERATURE mode)
    {
        setColorTemperature(mode.ordinal(),null);
    }
    public PictureMode getPictureMode(EN_PICTURE_MODE mode)
    {
        Log.d(LOG_TAG,"getPictureMode pictureModes:"+pictureModes);
        if(pictureModes != null)
        {
            Log.d(LOG_TAG,"pictureModes size:"+pictureModes.size());
            return pictureModes.get(mode);
        }
        Log.d(LOG_TAG,"getPictureMode null");
        return null;
    }

    protected void setBrightness(int value){ mInterface_Picture.setBrightness(value); }
    protected void setContrast(int value)
    {
        mInterface_Picture.setContrast(value);
    }
    protected void setSaturation(int value)
    {
        mInterface_Picture.setSaturation(value);
    }
    protected void setHue(int value)
    {
        mInterface_Picture.setHue(value);
    }
    protected void setSharpness(int value)
    {
        mInterface_Picture.setSharpness(value);
    }
    protected void setBackLight(int backLight)
    {
        mInterface_Picture.setBackLight(backLight);
    }
    protected void setFilmMode(boolean enable)
    {
        mInterface_Picture.setFilmMode(enable);
    }
    protected void setDynamicContrast(boolean enable){ mInterface_Picture.setDynamicContrast(enable);}
    protected int getBrightness(){ return mInterface_Picture.getBrightness(); }
    protected int getContrast() {  return mInterface_Picture.getContrast();  }
    protected int getSaturation()
    {
        return mInterface_Picture.getSaturation();
    }
    protected int getHue() { return mInterface_Picture.getHue(); }
    protected int getSharpness()
    {
        return mInterface_Picture.getSharpness();
    }
    protected int getBackLight()
    {
        return mInterface_Picture.getBackLight();
    }

    protected void setPictureMode(int mode ,String jStrPictureMode)
    {
        mInterface_Picture.setPictureMode(mode, jStrPictureMode);
    }
    protected void setColorTemperature(int mode, String jStrColorTemperature)
    {
        mInterface_Picture.setColorTemperature(mode, jStrColorTemperature);
    }
    protected void setColorTemperatureRedGain(int value)
    {
        mInterface_Picture.setColorTemperatureRedGain(value);
    }
    protected void setColorTemperatureRedOffset(int value)
    {
        mInterface_Picture.setColorTemperatureRedOffset(value);
    }
    protected void setColorTemperatureGreenGain(int value)
    {
        mInterface_Picture.setColorTemperatureGreenGain(value);
    }
    protected void setColorTemperatureGreenOffset(int value)
    {
        mInterface_Picture.setColorTemperatureGreenOffset(value);
    }
    protected void setColorTemperatureBlueGain(int value)
    {
        mInterface_Picture.setColorTemperatureBlueGain(value);
    }
    protected void setColorTemperatureBlueOffset(int value)
    {
        mInterface_Picture.setColorTemperatureBlueOffset(value);
    }
    protected int getColorTemperatureRedGain(){
        return mInterface_Picture.getColorTemperatureRedGain();
    }
    protected int getColorTemperatureRedOffset() {
        return mInterface_Picture.getColorTemperatureRedOffset();
    }
    protected int getColorTemperatureGreenGain(){
        return mInterface_Picture.getColorTemperatureGreenGain();
    }
    protected int getColorTemperatureGreenOffset(){
        return mInterface_Picture.getColorTemperatureGreenOffset();
    }
    protected int getColorTemperatureBlueGain(){
        return mInterface_Picture.getColorTemperatureBlueGain();
    }
    protected int getColorTemperatureBlueOffset(){
        return mInterface_Picture.getColorTemperatureBlueOffset();
    }
    protected void setDisplayFreeze(boolean enable)
    {
        mInterface_Picture.setDisplayFreeze(enable);
    }
    protected void setAspectRatio(int mode)
    {
        mInterface_Picture.setAspectRatio(mode);
    }
    protected void setMpegNrMode(int mode)
    {
        mInterface_Picture.setMpegNrMode(mode);
    }
    protected void setAnalogNrMode(int mode)
    {
        mInterface_Picture.setAnalogNrMode(mode);
    }
    private String getResolutionInfo()
    {
       return mInterface_Picture.getResolutionInfo();
    }
    private String getCurrentPictureSettingJsonString()
    {
        return mInterface_Picture.getCurrentPictureSettingJsonString();
    }
    private String getSupportDisplayModeJsonList()
    {
        return mInterface_Picture.getSupportDisplayModeJsonList();
    }
    private String getSupportPictureModeJsonList()
    {
        return mInterface_Picture.getSupportPictureModeJsonList();
    }
    private String getSupportColorTemperatureJsonList()
    {
        return mInterface_Picture.getSupportColorTemperatureJsonList();
    }
    private void Connect()
    {

    }
    private void Disconnect()
    {

    }
}
