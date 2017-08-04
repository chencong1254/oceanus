package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import skyworth.skyworthlivetv.global.GlobalDefinitions;


/**
 * Created by xeasy on 2017/4/28.
 */

public abstract class MenuBaseViewHolder extends RecyclerView.ViewHolder
        implements  View.OnFocusChangeListener{

    private skyworth.skyworthlivetv.osd.ui.menu.display.OnItemFocusChangeListener onItemFocusChangeListener;
    private skyworth.skyworthlivetv.osd.ui.menu.display.MenuItemData menuItemData;

    public MenuBaseViewHolder(View itemView) {
        super(itemView);
        itemView.setFocusable(true);
        itemView.setOnFocusChangeListener(this);
    }
    protected  abstract  String viewHolderName();
    protected void initData(MenuItemData menuItemData){
        this.menuItemData = menuItemData;
    };

    public void setOnItemFocusChangeListener(OnItemFocusChangeListener listItemSelectedListener) {
        this.onItemFocusChangeListener = listItemSelectedListener;
    }

    public OnItemFocusChangeListener getOnItemFocusChangeListener() {
        return onItemFocusChangeListener;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"viewHolderName:"+viewHolderName()+"  hasFocus:"+hasFocus);
        if (getOnItemFocusChangeListener() != null) {
            getOnItemFocusChangeListener().onItemFocusChange(v, hasFocus);
        }
    }
}
