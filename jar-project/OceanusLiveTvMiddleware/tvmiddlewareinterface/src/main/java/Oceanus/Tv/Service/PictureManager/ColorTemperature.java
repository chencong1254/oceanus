package Oceanus.Tv.Service.PictureManager;

import org.json.JSONException;
import org.json.JSONObject;

import Oceanus.Tv.ITvFunctionInterface.TvFunction.FuncUtil;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;

/**
 * Created by heji@skyworth.com on 2016/9/8.
 */
public class ColorTemperature
{
    private PictureManager pictureManager = PictureManager.getInstance();
    private EN_COLOR_TEMPERATURE mode = EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_STANDARD;
    private int r_Value = FuncUtil.VALUE_INVALID;
    private int g_Value = FuncUtil.VALUE_INVALID;
    private int b_Value = FuncUtil.VALUE_INVALID;
    private int r_Offset = FuncUtil.VALUE_INVALID;
    private int g_Offset = FuncUtil.VALUE_INVALID;
    private int b_Offset = FuncUtil.VALUE_INVALID;
    public ColorTemperature(JSONObject jColoreTemperautre) throws JSONException {
        if(jColoreTemperautre!=null)
        {
            this.mode = EN_COLOR_TEMPERATURE.values()[jColoreTemperautre.getInt("mode")];
            this.r_Value = jColoreTemperautre.getInt("r_Value");
            this.g_Value = jColoreTemperautre.getInt("g_Value");
            this.b_Value = jColoreTemperautre.getInt("b_Value");
            this.r_Offset = jColoreTemperautre.getInt("r_Offset");
            this.g_Offset = jColoreTemperautre.getInt("g_Offset");
            this.b_Offset = jColoreTemperautre.getInt("b_Offset");
        }
    }

    public void setR_Value(int r_Value) {
        this.r_Value = r_Value;
        pictureManager.setColorTemperatureRedGain(r_Value);
    }
    public void setB_Value(int b_Value) {
        this.b_Value = b_Value;
        pictureManager.setColorTemperatureBlueGain(b_Value);
    }

    public void setG_Value(int g_Value) {
        this.g_Value = g_Value;
        pictureManager.setColorTemperatureGreenGain(g_Value);
    }

    public void setR_Offset(int r_Offset) {
        this.r_Offset = r_Offset;
        pictureManager.setColorTemperatureRedOffset(r_Offset);
    }

    public void setG_Offset(int g_Offset) {
        this.g_Offset = g_Offset;
        pictureManager.setColorTemperatureGreenOffset(g_Offset);
    }

    public void setB_Offset(int b_Offset) {
        this.b_Offset = b_Offset;
        pictureManager.setColorTemperatureBlueOffset(b_Offset);
    }
    public int getR_Value() {
        if(notValid(this.r_Value)){
            this.r_Value = pictureManager.getColorTemperatureRedGain();
        }
        return this.r_Value;
    }

    public int getG_Value() {
        if(notValid(this.g_Value)){
            this.g_Value = pictureManager.getColorTemperatureGreenGain();
        }
        return this.g_Value;
    }

    public int getB_Value() {
        if(notValid(this.b_Value)){
            this.b_Value = pictureManager.getColorTemperatureBlueGain();
        }
        return this.b_Value;
    }

    public int getR_Offset()
    {
        if(notValid(this.r_Offset)){
            this.r_Offset = pictureManager.getColorTemperatureRedOffset();
        }
        return this.r_Offset;
    }

    public int getG_Offset()
    {
        if(notValid(this.g_Offset)){
            this.g_Offset = pictureManager.getColorTemperatureGreenOffset();
        }
        return this.g_Offset;
    }
    public int getB_Offset()
    {
        if(notValid(this.b_Offset)){
            this.b_Offset = pictureManager.getColorTemperatureBlueOffset();
        }
        return this.b_Offset;
    }

    public EN_COLOR_TEMPERATURE getMode()
    {
        return this.mode;
    }
    private String toJsonString() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("mode",this.mode.ordinal());
        jsonObject.put("r_Value",this.r_Value);
        jsonObject.put("g_Value",this.g_Value);
        jsonObject.put("b_Value",this.b_Value);
        jsonObject.put("r_Offset",this.r_Offset);
        jsonObject.put("g_Offset",this.g_Offset);
        jsonObject.put("b_Offset",this.b_Offset);
        return jsonObject.toString();
    }
    public void applyToUser()
    {
        this.mode = EN_COLOR_TEMPERATURE.E_COLOR_TEMPERATURE_USER;
        try {
            pictureManager.setColorTemperature(this.mode.ordinal(),this.toJsonString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private boolean notValid(int inputValue)
    {
        return (inputValue ==FuncUtil.VALUE_INVALID)?true:false;
    }
}