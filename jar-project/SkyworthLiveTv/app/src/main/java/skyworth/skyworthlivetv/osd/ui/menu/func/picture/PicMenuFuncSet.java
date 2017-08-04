package skyworth.skyworthlivetv.osd.ui.menu.func.picture;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.PictureManager.ColorTemperature;
import Oceanus.Tv.Service.PictureManager.PictureManager;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_COLOR_TEMPERATURE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_DISPLAY_MODE;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.PictureManager.PictureMode;
import Oceanus.Tv.Service.PictureManager.PictureSetting;
import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.TVMENU_COMMAND;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;

/**
 * Created by xeasy on 2017/5/2.
 */

public class PicMenuFuncSet extends MenuFuncSet {
    private PictureSetting  currentPictureSetting = null;
    @Override
    public void init(Context context)
    {
        super.init(context);
        currentPictureSetting = PictureManager.getInstance().getCurrentPictureSetting();
    }


    private MenuFunction getPictureModeSetting = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_GET_PICTURE_MODE_SETTING.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode>  supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_PICTURE_MODE, PictureMode>> iter = supportPictureModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_PICTURE_MODE enPictureMode = (EN_PICTURE_MODE) entry.getKey();
                enumList.add(enPictureMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_PICTURE_MODE currentValue  = currentPictureSetting.getPictureMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            return BooleanData.TRUE;
        }
    };

    private MenuFunction pictureMode = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_PICTURE_MODE.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode>  supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_PICTURE_MODE, PictureMode>> iter = supportPictureModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_PICTURE_MODE enPictureMode = (EN_PICTURE_MODE) entry.getKey();
                enumList.add(enPictureMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_PICTURE_MODE currentValue  = currentPictureSetting.getPictureMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            EnumData enumData = (EnumData)(typedData);
            EN_PICTURE_MODE enPictureMode = EN_PICTURE_MODE.valueOf(enumData.getCurrent());
            currentPictureSetting.setPictureMode(enPictureMode);
            return BooleanData.TRUE;
        }
     };

    private MenuFunction brightness = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_BRIGHTNESS.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode>  supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode  pictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData  rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(pictureMode.getBrightness());
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode>  supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode  currentPictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData  rangeData = (RangeData)typedData;
            currentPictureMode.setBrightness(rangeData.getCurrent());
            return BooleanData.TRUE;
        }

    };

    private MenuFunction contrast = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_CONTRAST.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode pictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(pictureMode.getContrast());
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode currentPictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = (RangeData) typedData;
            currentPictureMode.setContrast(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction saturation = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SATURATION.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode pictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(pictureMode.getSaturation());
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode currentPictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = (RangeData) typedData;
            currentPictureMode.setSaturation(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction color = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_COLOR.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(50); // need fix
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            return null;
        }
    };
    private MenuFunction sharpness = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SHARPNESS.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode pictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(pictureMode.getSharpness());
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
            PictureMode currentPictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
            RangeData rangeData = (RangeData) typedData;
            currentPictureMode.setSharpness(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
        private MenuFunction backlight = new MenuFunction(
                TVMENU_COMMAND.MENUCMD_BACKLIGHT.toString()) {
            @Override
            public TypedData get(String cmd, TypedData typedData) {
                HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
                PictureMode pictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
                RangeData rangeData = new RangeData();
                rangeData.setMin(0);
                rangeData.setCurrent(pictureMode.getBackLight());
                rangeData.setMax(100);
                return rangeData;
            }

            @Override
            public BooleanData set(String cmd, TypedData typedData) {
                HashMap<EN_PICTURE_MODE, PictureMode> supportPictureModes = PictureManager.getInstance().getSupportPictureModes();
                PictureMode currentPictureMode = supportPictureModes.get(currentPictureSetting.getPictureMode());
                RangeData rangeData = (RangeData) typedData;
                currentPictureMode.setBackLight(rangeData.getCurrent());
                return BooleanData.TRUE;
            }
        };

    private MenuFunction colorTempMode = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_COLORTEMP_MODE.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            HashMap<EN_COLOR_TEMPERATURE, ColorTemperature>  supportColorTemperatureModes = PictureManager.getInstance().getSupportColorTemperatureModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_COLOR_TEMPERATURE, ColorTemperature>> iter = supportColorTemperatureModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_COLOR_TEMPERATURE enPictureMode = (EN_COLOR_TEMPERATURE) entry.getKey();
                enumList.add(enPictureMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_COLOR_TEMPERATURE currentValue  = currentPictureSetting.getColorTemperatureMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            EnumData enumData = (EnumData)(typedData);
            EN_COLOR_TEMPERATURE enColoreTemperatureMode = EN_COLOR_TEMPERATURE.valueOf(enumData.getCurrent());
            currentPictureSetting.setColoreTemperatureMode(enColoreTemperatureMode);
            return BooleanData.TRUE;
        }
    };


    private MenuFunction screenMode = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SCREEN_MODE.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            List<EN_DISPLAY_MODE> supportDisplayModes = PictureManager.getInstance().getSupportDisplayModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            for(EN_DISPLAY_MODE displayMode:supportDisplayModes)
            {
                enumList.add(displayMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_DISPLAY_MODE currentValue  = currentPictureSetting.getDisplayMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            EnumData enumData = (EnumData)typedData;
            EN_DISPLAY_MODE  enDisplayMode = EN_DISPLAY_MODE.valueOf(enumData.getCurrent());
            currentPictureSetting.setDisplayMode(enDisplayMode);
            return BooleanData.TRUE;
        }
    };


    private MenuFunction memc = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_MEMC.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            List<EN_DISPLAY_MODE> supportDisplayModes = PictureManager.getInstance().getSupportDisplayModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            for(EN_DISPLAY_MODE displayMode:supportDisplayModes)
            {
                enumList.add(displayMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_DISPLAY_MODE currentValue  = currentPictureSetting.getDisplayMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {

            return BooleanData.TRUE;
        }
    };
  }