package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalDefinitions;

/**
 * Created by xeasy on 2017/5/20.
 */

public class SelectListHolder extends  SelectListBaseHolder {
    public View itemView;
    private ImageView chechedBox;
    private TextView itemName;
    private Context mContext;

    public SelectListHolder(View itemView, Context mContext) {
        super(itemView);
        this.mContext = mContext;
        this.itemView = itemView;
        this.itemView.setFocusable(true);
        this.itemView.setFocusableInTouchMode(true);
        chechedBox = (ImageView) itemView.findViewWithTag(111);
        itemName = (TextView) itemView.findViewWithTag(222);
        itemView.setBackgroundResource(0);
        itemView.setVisibility(View.VISIBLE);
    }

    @Override
    protected String viewHolderName() {
        return "SelectListHolder";
    }

    @Override
    protected void initData(SelectItemData selectItemData) {
        super.initData(selectItemData);
        itemName.setText(selectItemData.getItemTitle());
        chechedBox.setImageResource(getCheckBoxImage(itemView.isFocused(),selectItemData));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Log.d(GlobalDefinitions.DEBUG_TAG, "viewHolderName:" + viewHolderName() + "  hasFocus:" + hasFocus);
        super.onFocusChange(v, hasFocus);
        chechedBox.setImageResource(getCheckBoxImage(hasFocus,selectItemData));
        if (hasFocus) {
            itemName.setSelected(true);
            itemView.setBackgroundResource(R.drawable.menu_focus);

        } else {
            itemName.setSelected(false);
            itemView.setBackgroundResource(0);
        }
    }

    private int getCheckBoxImage(boolean hasFocus, SelectItemData currentSelectItemData) {
        if (hasFocus) {
            if (currentSelectItemData.isItemSelected()) {
                if (currentSelectItemData.getItemCheckBoxType() == SelectItemData.SELECTITEM_TYPE.SINGLE) {
                    return R.drawable.list_single_current_selected;
                } else {
                    return R.drawable.list_multiselect_current_selected;
                }
            } else {
                if (currentSelectItemData.getItemCheckBoxType() == SelectItemData.SELECTITEM_TYPE.SINGLE) {
                    return R.drawable.list_single;
                } else {
                    return R.drawable.list_multiselect;
                }
            }

        } else {
            if (currentSelectItemData.isItemSelected()) {
                if (currentSelectItemData.getItemCheckBoxType() == SelectItemData.SELECTITEM_TYPE.SINGLE) {
                    return R.drawable.list_single_selected;
                } else {
                    return R.drawable.list_multiselect_selected;
                }
            } else {
                if (currentSelectItemData.getItemCheckBoxType() == SelectItemData.SELECTITEM_TYPE.SINGLE) {
                    return R.drawable.list_single;
                } else {
                    return R.drawable.list_multiselect;
                }
            }
        }
    }
}
