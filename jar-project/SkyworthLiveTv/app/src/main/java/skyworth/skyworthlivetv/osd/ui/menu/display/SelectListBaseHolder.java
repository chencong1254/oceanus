package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import skyworth.skyworthlivetv.global.GlobalDefinitions;

/**
 * Created by xeasy on 2017/5/20.
 */

public abstract class SelectListBaseHolder extends RecyclerView.ViewHolder
        implements  View.OnFocusChangeListener{
    private skyworth.skyworthlivetv.osd.ui.menu.display.OnItemFocusChangeListener onItemFocusChangeListener;
    protected skyworth.skyworthlivetv.osd.ui.menu.display.SelectItemData selectItemData;

    public SelectListBaseHolder(View itemView) {
        super(itemView);
        itemView.setFocusable(true);
        itemView.setOnFocusChangeListener(this);
    }
    protected  abstract  String viewHolderName();
    protected void initData(SelectItemData multiSelectItemData){
        this.selectItemData = multiSelectItemData;
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
