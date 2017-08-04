package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.util.Log;
import com.mediatek.twoworlds.tv.MtkTvAVMode;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.MtkTvVolCtrl;
import com.mediatek.twoworlds.tv.common.MtkTvCfgType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Oceanus.Tv.ITvFunctionInterface.IPicture;
import Oceanus.Tv.Service.PictureManager.ColorTemperature;
import Oceanus.Tv.Service.PictureManager.PictureManager;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_ANALOG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_MPEG_NR_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.PictureManager.PictureMode;

import static Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_COOL;
import static Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;
import static Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_USER;
import static Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_WARM;


/**
 * Created by sky057509 on 2016/12/9.
 */
public class PictureImpl implements IPicture{
    private static PictureImpl mObj_This = null;
    //TV API
    private MtkTvVolCtrl mVolCtrl;
    private MtkTvConfig mConfig;
    private MtkTvAVMode mtkTvAVMode;
    private MtkTvUtil mtkTvUtil;
    private Context mContext;
    private int picModeMax = -1;
    private int picModeMin = -1;
    private int scnModeMax = -1;
    private int scnModeMin = -1;

    private PictureManager pictureManager = PictureManager.getInstance();
    private EN_DISPLAY_MODE displayMode = EN_DISPLAY_MODE.E_DISPLAY_MODE_AUTO;
    private EN_MPEG_NR_MODE mpegNrMode = EN_MPEG_NR_MODE.E_MPEG_NR_INVALID;
    private EN_ANALOG_NR_MODE analogNrMode = EN_ANALOG_NR_MODE.E_ANALOG_NR_INVALID;
    private boolean bIsFilmMode = false;
    private boolean bIsDynamicContrast = false;
    private EN_PICTURE_MODE pictureMode = EN_PICTURE_MODE.E_PICTURE_MODE_STANDARD;
    private EN_COLOR_TEMPERATURE colorTemperatureMode = EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;

    private PictureImpl(Context context)
    {
        mContext = context;
        mConfig = MtkTvConfig.getInstance();
        mtkTvAVMode = MtkTvAVMode.getInstance();
        mtkTvUtil = MtkTvUtil.getInstance();
        int configPic = mConfig.getMinMaxConfigValue(MtkTvConfigType.CFG_VIDEO_PIC_MODE);
        picModeMax = MtkTvConfig.getMaxValue(configPic);
        picModeMin = MtkTvConfig.getMinValue(configPic);
        int configScn = mConfig.getMinMaxConfigValue(MtkTvConfigType.CFG_VIDEO_SCREEN_MODE);
        scnModeMax = MtkTvConfig.getMaxValue(configScn);
        scnModeMin = MtkTvConfig.getMinValue(configScn);
    }
    public static PictureImpl getInstance(Context context)
    {
        if(mObj_This == null)
        {
            mObj_This = new PictureImpl(context);
            return mObj_This;
        }
        else
        {
            return mObj_This;
        }
    }
    @Override
    public void setBrightness(int value) {
        Log.d("Oceanus","setBrightness mode ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.BRIGHTNESS,value);
    }
    @Override
    public void setContrast(int value) {
        Log.d("Oceanus","setContrast value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.CONTRAST,value);
    }
    @Override
    public void setSaturation(int value) {
        Log.d("Oceanus","setSaturation value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.SATURATION,value);
    }
    @Override
    public void setHue(int value) {
        Log.d("Oceanus","setHue mode ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.HUE,value);
    }
    @Override
    public void setSharpness(int value) {
        Log.d("Oceanus","setSharpness mode ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.SHARPNESS,value);
    }

    @Override
    public void setBackLight(int backLight) {
        Log.d("Oceanus","setBackLight backLight ="+backLight);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.BACKLIGHT,backLight);
    }

    @Override
    public void setFilmMode(boolean enable) {
        Log.d("Oceanus","setFilmMode enable ="+enable);
     // MenuConfigManager.getInstance(null).setValue(MenuConfigManager.DI_FILM_MODE,value);
    }

    @Override
    public void setDynamicContrast(boolean enable) {
        Log.d("Oceanus","setDynamicContrast enable ="+enable);
//        MenuConfigManager.getInstance(null).setValue(MenuConfigManager.BRIGHTNESS,value);
    }
    @Override
    public void setPictureMode(int mode, String jStrPictureMode) {
        Log.d("Oceanus","setPictureMode mode ="+mode);
        int mtkPicMode;
        PictureMode picMode = null;
        try {
            EN_PICTURE_MODE  skyPicMode = EN_PICTURE_MODE.values()[mode];
            mtkPicMode = TvUtil.transToMtkPicMode(skyPicMode);
            if(mtkPicMode == MtkTvCfgType.PICTURE_MODE_USER  && jStrPictureMode!=null)
            {
                JSONObject jsonObjectPicMode = new JSONObject(jStrPictureMode);
                if(jsonObjectPicMode !=null)
                {
                    try {
                        picMode = new PictureMode(jsonObjectPicMode);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            mtkPicMode = MtkTvCfgType.PICTURE_MODE_CINEMA;
            e.printStackTrace();
        }
//        setCurrentPictureMode(mtkPicMode);
        mConfig.setConfigValue(MtkTvConfigType.CFG_VIDEO_PIC_MODE, mtkPicMode);
    }
    @Override
    public void setColorTemperature(int mode, String jStrColoreTemperature) {
        int mtkColorTempMode;
        ColorTemperature coloreTemperature = null;
        try {
            EN_COLOR_TEMPERATURE  skyColorTempMode = EN_COLOR_TEMPERATURE.values()[mode];
            mtkColorTempMode = TvUtil.transToMtkColorTempMode(skyColorTempMode);
            if(mtkColorTempMode == MtkTvCfgType.VID_CLR_TEMP_USER  && jStrColoreTemperature!=null)
            {
                JSONObject jsonObjectColorTemp = new JSONObject(jStrColoreTemperature);
                if(jsonObjectColorTemp !=null)
                {
                    try {
                        coloreTemperature = new ColorTemperature(jsonObjectColorTemp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            mtkColorTempMode = MtkTvCfgType.VID_CLR_TEMP_STANDARD;
            e.printStackTrace();
        }
        Log.d("Oceanus","setColorTemperature ");
        switch(mtkColorTempMode)
        {
             case MtkTvCfgType.VID_CLR_TEMP_USER:
                 MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.COLOR_TEMPERATURE,mtkColorTempMode);
                break;
                case MtkTvCfgType.VID_CLR_TEMP_COOL:
                case  MtkTvCfgType.VID_CLR_TEMP_STANDARD:
                case MtkTvCfgType.VID_CLR_TEMP_WARM:
                default:
                    MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.COLOR_TEMPERATURE,mtkColorTempMode);
                break;
            }
    }

    @Override
    public void setColorTemperatureRedGain(int value) {
        Log.d("Oceanus","setColoreTemperatureRedGain value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_G_R,value);
    }
    @Override
    public void setColorTemperatureRedOffset(int value) {
        Log.d("Oceanus","setColoreTemperatureRedOffset value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_O_R,value);
    }
    @Override
    public void setColorTemperatureGreenGain(int value) {
        Log.d("Oceanus","setColoreTemperatureGreenGain value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_G_G,value);
    }
    @Override
    public void setColorTemperatureGreenOffset(int value) {
        Log.d("Oceanus","setColoreTemperatureGreenOffset value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_O_G,value);
    }
    @Override
    public void setColorTemperatureBlueGain(int value) {
        Log.d("Oceanus","setColoreTemperatureBlueGain value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_G_B,value);
    }
    @Override
    public void setColorTemperatureBlueOffset(int value) {
        Log.d("Oceanus","setColoreTemperatureBlueOffset value ="+value);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.FV_COLOR_O_B,value);
    }

    @Override
    public void setDisplayFreeze(boolean enable) {
        Log.d("Oceanus","setDisplayFreeze enable ="+enable);
//        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.HUE,value);
    }

    @Override
    public void setAspectRatio(int mode) {
        Log.d("Oceanus","setAspectRatio mode ="+mode);
        mConfig.setConfigValue(MtkTvConfigType.CFG_VIDEO_SCREEN_MODE, mode);
    }
    @Override
    public void setMpegNrMode(int mode) {
        Log.d("Oceanus","setMpegNrMode mode ="+mode);
        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.MPEG_NR,mode);
    }

    @Override
    public void setAnalogNrMode(int mode) {
        Log.d("Oceanus","setAnalogNrMode mode ="+mode);
//        MenuConfigManager.getInstance(mContext).setValue(MenuConfigManager.HUE,value);
    }

    @Override
    public int getBrightness() {
        int brightness = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.BRIGHTNESS);
        Log.d("Oceanus","getBrightness brightness:"+brightness);
        return brightness;
    }
    @Override
    public int getContrast() {
        int contrast = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.CONTRAST);
        Log.d("Oceanus","getContrast contrast:"+contrast);
        return contrast;
    }
    @Override
    public int getSaturation() {
        int saturation = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.SATURATION);
        Log.d("Oceanus","getContrast saturation:"+saturation);
        return saturation;
    }
    @Override
    public int getHue() {
        int hue = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.HUE);
        Log.d("Oceanus","getHue hue:"+hue);
        return hue;
    }
    @Override
    public int getSharpness()  {
        int sharpness = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.SHARPNESS);
        Log.d("Oceanus","getSharpness:"+sharpness);
        return sharpness;
    }
    @Override
    public int getBackLight() {
        int backLight = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.BACKLIGHT);
        Log.d("Oceanus","DgetBackLight:"+backLight);
        return backLight;
    }
    @Override
    public int getColorTemperatureRedGain() {
        int redGain = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_G_R);
        Log.d("Oceanus","getColorTemperatureRedGain:"+redGain);
        return redGain;
    }
    @Override
    public int getColorTemperatureRedOffset() {
        int redOffset = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_O_R);
        Log.d("Oceanus","getColorTemperatureRedOffset:"+redOffset);
        return redOffset;
    }
    @Override
    public int getColorTemperatureGreenGain() {
        int greenGain = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_G_G);
        Log.d("Oceanus","getColorTemperatureGreenGain:"+greenGain);
        return greenGain;
    }
    @Override
    public int getColorTemperatureGreenOffset() {
        int  greenOffset = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_O_G);
        Log.d("Oceanus","getColorTemperatureGreenOffset:"+greenOffset);
        return greenOffset;
    }
    @Override
    public int getColorTemperatureBlueGain() {
        int blueGain = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_G_B);
        Log.d("Oceanus","getColorTemperatureBlueGain:"+blueGain);
        return blueGain;
    }
    @Override
    public int getColorTemperatureBlueOffset() {
        int blueOffset = MenuConfigManager.getInstance(mContext).getDefault(MenuConfigManager.FV_COLOR_O_B);
        Log.d("Oceanus","getColorTemperatureBlueOffset:"+blueOffset);
        return blueOffset;
    }

    @Override
    public String getResolutionInfo() {
        return null;
    }



    @Override
    public String getCurrentPictureSettingJsonString() {
        JSONObject jPictureSetting = new JSONObject();
        EN_DISPLAY_MODE displayMode = EN_DISPLAY_MODE.E_DISPLAY_MODE_AUTO;
        EN_MPEG_NR_MODE mpegNrMode = EN_MPEG_NR_MODE.E_MPEG_NR_AUTO;

        int currentPictureModeMtk = getCurrentPictureModeMtk();

        jPictureSetting = getJPictureSetting(displayMode,EN_MPEG_NR_MODE.E_MPEG_NR_AUTO, EN_ANALOG_NR_MODE.E_ANALOG_NR_LOW,
            true,true,TvUtil.transToSkyPicMode(currentPictureModeMtk),EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD);

        return jPictureSetting.toString();
    }




    private JSONObject getJPictureSetting(EN_DISPLAY_MODE displayMode,EN_MPEG_NR_MODE mpegNrMode, EN_ANALOG_NR_MODE analogNrMode,
                                         boolean bIsFilmMode,boolean bIsDynamicContrast,EN_PICTURE_MODE pictureMode,EN_COLOR_TEMPERATURE colorTemperatureMode)
    {
        JSONObject jPictureSetting = new JSONObject();
        try {
            jPictureSetting.put("displayMode",displayMode.ordinal());
            jPictureSetting.put("mpegNrMode", mpegNrMode.ordinal());
            jPictureSetting.put("analogNrMode",analogNrMode.ordinal());
            jPictureSetting.put("bIsFilmMode",bIsFilmMode?1:0);
            jPictureSetting.put("bIsDynamicContrast",bIsDynamicContrast?1:0);
            jPictureSetting.put("pictureMode",pictureMode.ordinal());

            jPictureSetting.put("bIsDynamicContrast",bIsDynamicContrast?1:0);
            jPictureSetting.put("colorTemperatureMode",colorTemperatureMode.ordinal());

        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jPictureSetting;
    }
    private JSONObject getJDisplayMode(EN_DISPLAY_MODE mode)
    {
        JSONObject jDisplayModeTmp = new JSONObject();
        try {
            jDisplayModeTmp.put("mode",mode.ordinal());
        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jDisplayModeTmp;
    }
    private JSONObject getJPictureMode(EN_PICTURE_MODE mode,int brightness,int contrast,int saturation,int hue,int sharpness,int backLight)
    {
        JSONObject jPictureModeTmp = new JSONObject();
        try {
            jPictureModeTmp.put("mode",mode.ordinal());
            jPictureModeTmp.put("brightness",brightness);
            jPictureModeTmp.put("contrast",contrast);
            jPictureModeTmp.put("saturation",saturation);
            jPictureModeTmp.put("hue",hue);
            jPictureModeTmp.put("sharpness",sharpness);
            jPictureModeTmp.put("backLight",backLight);
        }catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jPictureModeTmp;
    }
    private JSONObject getJColoreTemperature(EN_COLOR_TEMPERATURE mode,int r_Value,int g_Value,int b_Value,int r_Offset,int g_Offset,int b_Offset) throws JSONException
    {
        JSONObject jColorTemperatureTmp = new JSONObject();
        try {
            jColorTemperatureTmp.put("mode",mode.ordinal());
            jColorTemperatureTmp.put("r_Value",r_Value);
            jColorTemperatureTmp.put("g_Value",g_Value);
            jColorTemperatureTmp.put("b_Value",b_Value);
            jColorTemperatureTmp.put("r_Offset",r_Offset);
            jColorTemperatureTmp.put("g_Offset",g_Offset);
            jColorTemperatureTmp.put("b_Offset",b_Offset);

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jColorTemperatureTmp;
    }

    @Override
    public String getSupportDisplayModeJsonList() {
        JSONArray displayModesJsonArray = new JSONArray();
        Set<EN_DISPLAY_MODE> displayModeset = new HashSet<EN_DISPLAY_MODE>();
        int[] allDisplayModes = mtkTvAVMode.getAllScreenMode();
        Log.d("Oceanus","getSupportDisplayModeJsonList allDisplayModes ="+allDisplayModes);
        if(allDisplayModes == null)
        {
            return null;
        }
        for(int tempDisplayMode:allDisplayModes)
        {
            displayModeset.add(TvUtil.transToSkyDisplayMode(tempDisplayMode));
        }
        Iterator<EN_DISPLAY_MODE> it = displayModeset.iterator();
        while (it.hasNext()) {
            EN_DISPLAY_MODE tempDisplayMode = it.next();
            JSONObject jDisplayModeUser = getJDisplayMode(tempDisplayMode);
            displayModesJsonArray.put(jDisplayModeUser);
        }
        return displayModesJsonArray.toString();
    }

    @Override
    public String getSupportPictureModeJsonList() {
        JSONArray pictureModesJsonArray = new JSONArray();
        Set<EN_PICTURE_MODE> pictureModeset = new HashSet<EN_PICTURE_MODE>();
        int[] allPictureModes = mtkTvAVMode.getAllPictureMode();
        for(int tempPictureMode:allPictureModes)
        {
            pictureModeset.add(TvUtil.transToSkyPicMode(tempPictureMode));
        }
        Iterator<EN_PICTURE_MODE> it = pictureModeset.iterator();
        while (it.hasNext()) {
            EN_PICTURE_MODE tempPictureMode = it.next();
            JSONObject jPictureModeUser = getJPictureMode(tempPictureMode,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID);
            pictureModesJsonArray.put(jPictureModeUser);
        }
        return pictureModesJsonArray.toString();
    }

    @Override
    public String getSupportColorTemperatureJsonList()
    {
        try{
            JSONArray colorTemperatureJsonArray = new JSONArray();
            JSONObject jColoreTemperatureCool = getJColoreTemperature(TvUtil.transToSkyColorTempeMode(MtkTvConfigType.VID_CLR_TEMP_COOL),FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID);
            JSONObject jColoreTemperatureStandard = getJColoreTemperature(TvUtil.transToSkyColorTempeMode(MtkTvConfigType.VID_CLR_TEMP_STANDARD),FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID);
            JSONObject jColoreTemperatureUser = getJColoreTemperature(TvUtil.transToSkyColorTempeMode(MtkTvConfigType.VID_CLR_TEMP_USER),FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID);
            JSONObject jColoreTemperatureWarm = getJColoreTemperature(TvUtil.transToSkyColorTempeMode(MtkTvConfigType.VID_CLR_TEMP_WARM),FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID,FuncUtil.VALUE_INVALID);
            colorTemperatureJsonArray.put(jColoreTemperatureCool);
            colorTemperatureJsonArray.put(jColoreTemperatureStandard);
            colorTemperatureJsonArray.put(jColoreTemperatureUser);
            colorTemperatureJsonArray.put(jColoreTemperatureWarm);
            Log.d("Oceanus","getSupportColorTemperatureJsonList colorTemperatureJsonArray:"+colorTemperatureJsonArray.toString());
            return colorTemperatureJsonArray.toString();
        }catch (JSONException e)
        {
            e.printStackTrace();;
        }
        return null;
    }
    /**
     * get the current picture mode
     * @return
     */
    private int getCurrentPictureModeMtk(){
        return mtkTvAVMode.getPictureMode();
    }

    /**
     * set the current picture mode
     * @param mode
     * @return
     */
    private int setCurrentPictureMode(int mode){
        int result = mtkTvAVMode.setPictureMode(mode);
        return result;
    }
    private static final int VID_SCREEN_MODE_AUTO = 7;
    private static final int VID_SCREEN_MODE_NORMAL = 1;
    private static final int VID_SCREEN_MODE_LETTER_BOX = 2;
    private static final int VID_SCREEN_MODE_PAN_SCAN = 3;
    private static final int VID_SCREEN_MODE_NON_LINEAR_ZOOM = 5;
    private static final int VID_SCREEN_MODE_DOT_BY_DOT = 6;
    /**
     * Get screen mode min value.
     * @return
     */
    private int getScreenModeMin() {
        Log.d("Oceanus","getScreenModeMin ScnModeMin ="+scnModeMin);
        return scnModeMin;
    }
    /**
     * Get screen mode max value.
     * @return
     */
    private int getScreenModeMax() {
        Log.d("Oceanus","getScreenModeMax ScnModeMax ="+scnModeMax);
        return scnModeMax;
    }
    /**
     * Get available Screen mode.
     *
     * @return
     * VID_SCREEN_MODE_AUTO
     * VID_SCREEN_MODE_NORMAL
     * VID_SCREEN_MODE_LETTER_BOX
     * VID_SCREEN_MODE_PAN_SCAN
     * VID_SCREEN_MODE_NON_LINEAR_ZOOM
     * VID_SCREEN_MODE_DOT_BY_DOT
    0TV
     *<item>Unknown</item>
    <item>Normal</item>
    <item>Letterbox</item>
    <item>Pan Scan</item>
    <item>User Defined</item>
    <item>Non Linear</item>
    <item>Dot by Dot</item>
    <item>Auto</item>

    MMP

    <item>Auto</item>
    <item>Normal</item>
    <item>Letter box</item>
    <item>Scan</item>
    <item>Zoom</item>
    <item>Dot by Dot</item>
     */
    private int[] getAvailableScreenMode() {
        /*int[] scrMode = new int[6];
        scrMode[0] = VID_SCREEN_MODE_AUTO;
        scrMode[1] = VID_SCREEN_MODE_NORMAL;
        scrMode[2] = VID_SCREEN_MODE_LETTER_BOX;
        scrMode[3] = VID_SCREEN_MODE_PAN_SCAN;
        scrMode[4] = VID_SCREEN_MODE_NON_LINEAR_ZOOM;
        scrMode[5] = VID_SCREEN_MODE_DOT_BY_DOT;
*/
        int tmp[] = new int[6];
        for(int i = 0; i < 6; i++){
            tmp[i] = -1;
        }
        try {
            mtkTvAVMode.getAllPictureMode();

            int[] allMode = mtkTvAVMode.getAllScreenMode();
            for(int mode : allMode){

                Log.d("Oceanus","getAvailableScreenMode Mode = "+ mode);


            }


            // int allMode = mmpCfg.getCfg(ConfigType.CFG_GET_ALL_SCREEN_MODE)
            //      .getIntValue();

            for (int i = 0; i < allMode.length; i++) {
                switch(allMode[i]){
                    case VID_SCREEN_MODE_AUTO:
                        tmp[0] = 7;
                        break;
                    case VID_SCREEN_MODE_NORMAL:
                        tmp[1] = 1;
                        break;
                    case VID_SCREEN_MODE_LETTER_BOX:
                        tmp[2] = 2;
                        break;
                    case VID_SCREEN_MODE_PAN_SCAN:
                        tmp[3] = 3;
                        break;
                    case VID_SCREEN_MODE_NON_LINEAR_ZOOM:
                        tmp[4] = 5;
                        break;
                    case VID_SCREEN_MODE_DOT_BY_DOT:
                        tmp[5] = 6;
                        break;
                    default:
                        break;


                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        for(int mode : tmp){

            Log.d("Oceanus","getAvailableScreenMode tmp = "+ mode);


        }


        return tmp;
    }
    private EN_DISPLAY_MODE getCurScreenMode() {
        int curScnMode = mConfig.getConfigValue(MtkTvConfigType.CFG_VIDEO_SCREEN_MODE);
        Log.d("Oceanus","getCurScreenMode curScnMode = "+ curScnMode );
        return TvUtil.transToSkyDisplayMode(curScnMode);
    }
    /**
     * Set screen mode
     * @param: the screen type user want to set .
     */
    private void setScreenMode(EN_DISPLAY_MODE displayMode) {

        int mode = TvUtil.transToMtkTvDisplayMode(displayMode);
        Log.d("Oceanus","setScreenMode pram type = "+ displayMode +" set mode ="+mode);
        mConfig.setConfigValue(MtkTvConfigType.CFG_VIDEO_SCREEN_MODE, mode);
    }
}