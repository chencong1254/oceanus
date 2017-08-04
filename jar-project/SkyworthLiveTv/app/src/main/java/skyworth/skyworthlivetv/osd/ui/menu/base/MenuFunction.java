package skyworth.skyworthlivetv.osd.ui.menu.base;


import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;

/**
 * Created by xeasy on 2017/5/2.
 */

public abstract class MenuFunction {
    public String cmd = null;

    public MenuFunction(String cmd)
    {
        this.cmd = cmd;
    }
    public abstract TypedData get(String cmd, TypedData typedData);
    public abstract BooleanData set(String cmd, TypedData typedData);
}
