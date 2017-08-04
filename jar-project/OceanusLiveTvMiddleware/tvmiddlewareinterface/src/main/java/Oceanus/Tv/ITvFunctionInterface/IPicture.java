package Oceanus.Tv.ITvFunctionInterface;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_OCHANNEL_COUNT_TYPE;

/**
 * Created by sky057509 on 2016/12/7.
 */
public interface IPicture {
    public void setBrightness(int value);
    public void setContrast(int value);
    public void setSaturation(int value);
    public void setHue(int value);
    public void setSharpness(int value);
    public void setBackLight(int backLight);
    public void setFilmMode(boolean enable);
    public void setDynamicContrast(boolean enable);
    public void setPictureMode(int mode ,String jStrPictureMode);
    public void setColorTemperature(int mode, String jStrColoreTemperature);
    public void setColorTemperatureRedGain(int value);
    public void setColorTemperatureRedOffset(int value);
    public void setColorTemperatureGreenGain(int value);
    public void setColorTemperatureGreenOffset(int value);
    public void setColorTemperatureBlueGain(int value);
    public void setColorTemperatureBlueOffset(int value);
    public void setDisplayFreeze(boolean enable);
    public void setAspectRatio(int mode);
    public void setMpegNrMode(int mode);
    public void setAnalogNrMode(int mode);

    public int getBrightness();
    public int getContrast();
    public int getSaturation();
    public int getHue();
    public int getSharpness();
    public int getBackLight();

    public int getColorTemperatureRedGain();
    public int getColorTemperatureRedOffset();
    public int getColorTemperatureGreenGain();
    public int getColorTemperatureGreenOffset();
    public int getColorTemperatureBlueGain();
    public int getColorTemperatureBlueOffset();

    public String getResolutionInfo();
    public String getCurrentPictureSettingJsonString();
    public String getSupportDisplayModeJsonList();
    public String getSupportPictureModeJsonList();
    public String getSupportColorTemperatureJsonList();

}
