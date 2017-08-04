package skyworth.skyworthlivetv.osd.ui.menu.func.setup;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.TVMENU_COMMAND;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;

/**
 * Created by xeasy on 2017/5/3.
 */

public class SetupMenuFuncSet extends MenuFuncSet {
    @Override
    public void init(Context context)
    {
        // TODO Auto-generated method stub
        super.init(context);
    }
    private String currentPrimaryAudioLanguage ="English";  // need fix
    private String currentSecondAudioLanguage ="English";  // need fix
    private boolean bNoSignalOff = true;    // need fix
    private boolean bNoOperationOff = true;  // need fix

    private MenuFunction primaryAudioLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_PRIMARY_AUDIO_LANGUAGE.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // need fix
            SingleSelectListData ret = new SingleSelectListData();
            List<String> enumList = new ArrayList<String>();
            enumList.add("Chinese");
            enumList.add("English");
            enumList.add("French");
            enumList.add("Russian");
            enumList.add("Japanese");
            enumList.add("Korean");
            enumList.add("Swedish");
            enumList.add("Thailand");
            enumList.add("Israel");
            enumList.add("Germany");
            ret.setEnumList(enumList);
            ret.setCurrent(currentPrimaryAudioLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentPrimaryAudioLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };


    private MenuFunction secondAudioLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SECOND_AUDIO_LANGUAGE.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // need fix
            SingleSelectListData ret = new SingleSelectListData();
            List<String> enumList = new ArrayList<String>();
            enumList.add("Chinese");
            enumList.add("English");
            enumList.add("French");
            enumList.add("Russian");
            enumList.add("Japanese");
            enumList.add("Korean");
            enumList.add("Swedish");
            enumList.add("Thailand");
            enumList.add("Israel");
            enumList.add("Germany");
            ret.setEnumList(enumList);
            ret.setCurrent(currentSecondAudioLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentPrimaryAudioLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };

    private MenuFunction  noSignalOff = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_NO_SIGNAL_OFF.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // need fix
            SwitchData ret = new SwitchData();
            ret.setOn(bNoSignalOff);
            ret.setOnStr("Open");
            ret.setOffStr("Close");
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData)(typedData);
            bNoSignalOff = switchData.isOn();
            return BooleanData.TRUE;
        }
    };
    private MenuFunction noOperationOff = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_NO_OPERATION_OFF.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // need fix
            SwitchData ret = new SwitchData();
            ret.setOn(bNoOperationOff);
            ret.setOnStr("Open");
            ret.setOffStr("Close");
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData)(typedData);
            bNoOperationOff = switchData.isOn();
            return BooleanData.TRUE;
        }
    };
    private MenuFunction resetDefault = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_RESET_DEFAULT.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // need fix
            SwitchData ret = new SwitchData();
            ret.setOn(bNoOperationOff);
            ret.setOnStr("Open");
            ret.setOffStr("Close");
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData)(typedData);
            bNoOperationOff = switchData.isOn();
            return BooleanData.TRUE;
        }
    };
}