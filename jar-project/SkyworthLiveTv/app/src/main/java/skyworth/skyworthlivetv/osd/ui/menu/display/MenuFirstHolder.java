package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import skyworth.skyworthlivetv.R;

/**
 * Created by xeasy on 2017/4/28.
 */

public class MenuFirstHolder extends MenuBaseViewHolder {
    public View itemView;
    private TextView menuName;
    private ImageView rightArrow;
    private Context mContext;

    public MenuFirstHolder(View itemView,Context mContext) {
        super(itemView);
        this.itemView = itemView;
        this.itemView.setFocusable(true);
        this.itemView.setFocusableInTouchMode(true);
        this.mContext = mContext;
        menuName = (TextView)itemView.findViewWithTag(111);
        rightArrow = (ImageView)itemView.findViewWithTag(222);
        itemView.setBackgroundResource(0);
        itemView.setVisibility(View.VISIBLE);
    }

    @Override
    protected String viewHolderName() {
        return "MenuFirstHolder";
    }

    @Override
    protected void initData(MenuItemData menuItemData){
        super.initData(menuItemData);
        menuName.setText(menuItemData.getItemTitle());
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if(hasFocus){
            itemView.setBackgroundResource(R.drawable.menu_focus);
            if(menuName!=null)
              menuName.setTextColor(MenuConstant.FIRST_MENU_TEXT_FOCUS_COLOR);
            if(rightArrow!=null) {
                rightArrow.setSelected(false);
                rightArrow.setVisibility(View.VISIBLE);
            }
        }else{
            itemView.setBackgroundResource(0);
            if(!rightArrow.isSelected()) {
                rightArrow.setVisibility(View.INVISIBLE);
                menuName.setTextColor(MenuConstant.FIRST_MENU_TEXT_UNFOCUS_COLOR);
            }else {
                 menuName.setTextColor(MenuConstant.FIRST_MENU_TEXT_SELECT_COLOR);
            }
        }
    }

}
