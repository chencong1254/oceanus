package skyworth.skyworthlivetv.osd.ui.menu.func.channel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.channel.ScanActivity;
import skyworth.skyworthlivetv.osd.ui.menu.TVMENU_COMMAND;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;

/**
 * Created by xeasy on 2017/5/3.
 */

public class ChannelMenuFuncSet extends MenuFuncSet {
    @Override
    public void init(Context context)
    {
        // TODO Auto-generated method stub
        super.init(context);
    }

    private MenuFunction startTuning = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_START_TUNING.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            return null;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            Log.d(GlobalDefinitions.DEBUG_TAG,"startTuning");
            Intent intent = new Intent(mContext, ScanActivity.class);
            mContext.startActivity(intent);
            return BooleanData.TRUE;
        }

    };

    private MenuFunction get5vAntenna = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_5V_ANTENNA.toString()) {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            SwitchData switchData = new SwitchData();
            boolean get5vAntenna = true;  // need fix
            switchData.setOn(get5vAntenna);
            return switchData;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            SwitchData switchData = (SwitchData) (typedData);
            boolean needToSet = switchData.isOn();
            // need fix
            return BooleanData.TRUE;
        }
    };
    private MenuFunction startChannelEdit = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_START_CHANNEL_EDIT.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            // to start tuning activity , need fix
            return BooleanData.TRUE;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            return null;
        }
    };
    private MenuFunction showCiInfo = new MenuFunction(
            TVMENU_COMMAND.MENUCMD_CI_INFORMATION.toString())
    {
        @Override
        public TypedData get(String cmd, TypedData typedData) {
            return null;
        }

        @Override
        public BooleanData set(String cmd, TypedData typedData) {
            //  need fix
            return BooleanData.TRUE;
        }
    };
}