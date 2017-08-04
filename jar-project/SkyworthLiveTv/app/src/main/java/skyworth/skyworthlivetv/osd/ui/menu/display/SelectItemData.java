package skyworth.skyworthlivetv.osd.ui.menu.display;

import java.io.Serializable;

/**
 * Created by xeasy on 2017/5/20.
 */

public class SelectItemData implements Serializable {
    public enum  SELECTITEM_TYPE {
        SINGLE,MULTI
    }
    private static final long serialVersionUID = 1L;
    private String title = "";
    private boolean isSelected =false;
    private SELECTITEM_TYPE  selectitemType = SELECTITEM_TYPE.SINGLE;
    public void setItemSelectitemType(SELECTITEM_TYPE selectitemType)
    {
        this.selectitemType = selectitemType;
    }
    public SELECTITEM_TYPE getItemCheckBoxType()
    {
        return selectitemType;
    }
    public void setItemSelected(boolean isSelected)
    {
        this.isSelected = isSelected;
    }
    public boolean isItemSelected()
    {
        return isSelected;
    }
    public void setItemTitle(String title)
    {
        this.title = title;
    }
    public String getItemTitle()
    {
        return title;
    }
}
