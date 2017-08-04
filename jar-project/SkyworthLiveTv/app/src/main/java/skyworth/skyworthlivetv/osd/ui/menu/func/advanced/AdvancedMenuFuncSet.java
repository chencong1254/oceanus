package skyworth.skyworthlivetv.osd.ui.menu.func.advanced;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.PictureManager.PictureManager;
import Oceanus.Tv.Service.PictureManager.PictureManagerDefinitions.EN_PICTURE_MODE;
import Oceanus.Tv.Service.PictureManager.PictureMode;
import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.TVMENU_COMMAND;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;

/**
 * Created by xeasy on 2017/5/11.
 */

public class AdvancedMenuFuncSet extends MenuFuncSet {
    private String currentTtLanguage ="English";  // need fix
    private String currentOsdLanguage ="English";  // need fix
    private String currentPrimarySubtitleLanguage ="English";  // need fix
    private String currentSecondSubtitleLanguage ="English";  // need fix
    @Override
    public void init(Context context)
    {
        // TODO Auto-generated method stub
        super.init(context);
    }
    private MenuFunction teletextLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_TELETEXT_LANGUAGE.toString())
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
            ret.setCurrent(currentTtLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentTtLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };
    private MenuFunction  osdLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_OSD_LANGUAGE.toString())
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
            ret.setCurrent(currentOsdLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentOsdLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };
    private MenuFunction  primarySubtitleLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_PRIMARY_SUBTITLE_LANGUAGE.toString())
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
            ret.setCurrent(currentPrimarySubtitleLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentPrimarySubtitleLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };
    private MenuFunction  secondSubtitleLanguage = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_SECOND_SUBTITLE_LANGUAGE.toString())
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
            ret.setCurrent(currentSecondSubtitleLanguage);
            return ret;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SingleSelectListData singleSelectListData = (SingleSelectListData)(typedData);
            currentSecondSubtitleLanguage = singleSelectListData.getCurrent();
            return BooleanData.TRUE;
        }
    };
}