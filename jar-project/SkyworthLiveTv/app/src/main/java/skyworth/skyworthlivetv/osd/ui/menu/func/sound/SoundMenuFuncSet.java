package skyworth.skyworthlivetv.osd.ui.menu.func.sound;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.PictureManager.PictureManager;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.PictureManager.PictureMode;
import Oceanus.Tv.Service.SoundManager.SoundManager;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_MODE_FREQ_TYPE;
import Oceanus.Tv.Service.SoundManager.SoundManagerDefinitions.EN_SOUND_SPDIF_MODE;
import Oceanus.Tv.Service.SoundManager.SoundMode;
import Oceanus.Tv.Service.SoundManager.SoundSetting;
import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.TVMENU_COMMAND;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;

/**
 * Created by xeasy on 2017/5/3.
 */

public class SoundMenuFuncSet extends MenuFuncSet {
    private SoundSetting currentSoundSetting = null;

    @Override
    public void init(Context context)
    {
        super.init(context);
        currentSoundSetting = SoundManager.getInstance().getCurrentSoundSetting();
    }



    private MenuFunction getSoundModeSetting = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_GET_SOUND_MODE_SETTING.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_SOUND_MODE, SoundMode>> iter = supportSoundModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_SOUND_MODE enSoundMode = (EN_SOUND_MODE) entry.getKey();
                enumList.add(enSoundMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_SOUND_MODE currentValue  = currentSoundSetting.getSoundMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            return BooleanData.TRUE;
        }
    };

    private MenuFunction soundMode = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SOUND_MODE.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_SOUND_MODE, SoundMode>> iter = supportSoundModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_SOUND_MODE enSoundMode = (EN_SOUND_MODE) entry.getKey();
                enumList.add(enSoundMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_SOUND_MODE currentValue  = currentSoundSetting.getSoundMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {

            EnumData enumData = (EnumData)(typedData);
            EN_SOUND_MODE enSoundMode = EN_SOUND_MODE.valueOf(enumData.getCurrent());
            currentSoundSetting.setSoundMode(enSoundMode);
            return BooleanData.TRUE;
        }
    };
    private MenuFunction eq120 = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_EQUALIZER_120HZ.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(soundMode.getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_120HZ));
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = (RangeData) typedData;
            soundMode.setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_120HZ,rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction eq500 = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_EQUALIZER_500HZ.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(soundMode.getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_500HZ));
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = (RangeData) typedData;
            soundMode.setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_500HZ,rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction eq1500 = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_EQUALIZER_1500HZ.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(soundMode.getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_1500HZ));
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = (RangeData) typedData;
            soundMode.setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_1500HZ,rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction eq5k = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_EQUALIZER_5KHZ.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(soundMode.getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_5KHZ));
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = (RangeData) typedData;
            soundMode.setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_5KHZ,rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction eq10k = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_EQUALIZER_10KHZ.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = new RangeData();
            rangeData.setMin(0);
            rangeData.setCurrent(soundMode.getSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_10kHZ));
            rangeData.setMax(100);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            SoundMode  soundMode = supportSoundModes.get(currentSoundSetting.getSoundMode());
            RangeData rangeData = (RangeData) typedData;
            soundMode.setSoundModeHz(EN_SOUND_MODE_FREQ_TYPE.E_SOUND_MODE_FREQ_TYPE_10kHZ,rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };

    private MenuFunction balance = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_BALANCE.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            RangeData rangeData = new RangeData();
            rangeData.setMin(-50);
            rangeData.setCurrent(currentSoundSetting.getBalance());
            rangeData.setMax(50);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            RangeData rangeData = (RangeData) typedData;
            currentSoundSetting.setBalance(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };

    private MenuFunction surroundSound = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SURROUND_SOUND.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            SwitchData switchData = new SwitchData();
            switchData.setOn(currentSoundSetting.isSurroundMode());
            return switchData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData) typedData;
            currentSoundSetting.setSurroundMode(switchData.isOn());
            return BooleanData.TRUE;
        }
    };

    private MenuFunction autoVolumeCtrl = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_AUTO_VOLUME_CONTROL.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            SwitchData switchData = new SwitchData();
            switchData.setOn(currentSoundSetting.isAutoVolume());
            return switchData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData)typedData;
            currentSoundSetting.setAutoVolume(switchData.isOn());
             return BooleanData.TRUE;
        }
    };

    private MenuFunction adSwitch = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_AD_SWITCH.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            SwitchData switchData = new SwitchData();
            switchData.setOn(currentSoundSetting.isAdEnable());
            return switchData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData)typedData;
            currentSoundSetting.setAdEnable(switchData.isOn());
            return BooleanData.TRUE;
        }
    };

    private MenuFunction adVolume = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_AD_VOLUME.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            RangeData rangeData = new RangeData();
            rangeData.setMin(-10);
            rangeData.setCurrent(currentSoundSetting.getAdAbsoluteVolume());
            rangeData.setMax(10);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            RangeData rangeData = (RangeData) typedData;
            currentSoundSetting.setAdAbsoluteVolume(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };
    private MenuFunction spdifType = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SPDIF_TYPE.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            for(EN_SOUND_SPDIF_MODE mode : EN_SOUND_SPDIF_MODE.values()) {
                enumList.add(mode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_SOUND_SPDIF_MODE currentValue  = currentSoundSetting.getSpdifMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {

            EnumData enumData = (EnumData)(typedData);
            EN_SOUND_SPDIF_MODE enSpdifMode = EN_SOUND_SPDIF_MODE.valueOf(enumData.getCurrent());
            currentSoundSetting.setSpdifMode(enSpdifMode);
            return BooleanData.TRUE;
        }
    };

    private MenuFunction spdifDelay = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SPDIF_DELAY.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            RangeData rangeData = new RangeData();
            rangeData.setMin(-50);
            rangeData.setCurrent(currentSoundSetting.getSpdifdelay());
            rangeData.setMax(50);
            return rangeData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            RangeData rangeData = (RangeData) typedData;
            currentSoundSetting.setSpdifdelay(rangeData.getCurrent());
            return BooleanData.TRUE;
        }
    };

    private MenuFunction audioOut = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_AUDIO_OUT.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
//            EnumData ret = new EnumData();
//            return ret;
            Log.i("20170614", "audioOut get:"+cmd+",typedData:"+typedData);
            Map<EN_SOUND_MODE, SoundMode> supportSoundModes = SoundManager.getInstance().getSupportSoundModes();
            EnumData ret = new EnumData();
            List<String> enumList = new ArrayList<String>();
            Iterator<Map.Entry<EN_SOUND_MODE, SoundMode>> iter = supportSoundModes.entrySet().iterator();
            while (iter.hasNext())
            {
                Map.Entry entry = (Map.Entry) iter.next();
                EN_SOUND_MODE enSoundMode = (EN_SOUND_MODE) entry.getKey();
                enumList.add(enSoundMode.toString());
            }
            ret.setEnumList(enumList);
            ret.setEnumCount(enumList.size());
            EN_SOUND_MODE currentValue  = currentSoundSetting.getSoundMode();
            ret.setCurrent(currentValue.toString());
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            EnumData enumData = (EnumData)(typedData);
            Log.i("20170614", "audioOut set:"+enumData.getCurrent());
            EN_SOUND_MODE enSoundMode = EN_SOUND_MODE.valueOf(enumData.getCurrent());
            currentSoundSetting.setSoundMode(enSoundMode);
            return BooleanData.TRUE;
        }
    };
}