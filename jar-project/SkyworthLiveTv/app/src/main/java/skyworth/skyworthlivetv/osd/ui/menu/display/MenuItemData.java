package skyworth.skyworthlivetv.osd.ui.menu.display;

import java.io.Serializable;

import skyworth.skyworthlivetv.osd.common.data.TypedData;


/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuItemData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String title = null;
    private TypedData typedData ;
    private boolean isEnabled = true;  // isEnabled 为 false 时，是否需要灰色显示。
    private boolean isShow = true;  	//此项是否需要显示


    public void setItemTitle(String title)
    {
        this.title = title;
    }
    public String getItemTitle()
    {
        return title;
    }
    public TypedData getTypedData() {
        return typedData;
    }

    public void setTypedValue(TypedData typedData) {
        this.typedData = typedData;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isShow() {
        return isShow;
    }
    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }
}
