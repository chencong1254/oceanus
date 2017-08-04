package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.R;


/**
 * Created by xeasy on 2017/4/28.
 */

public class MenuSecondHolder extends MenuBaseViewHolder {
    public View itemView;
    private TextView menuName;
    private TextView menuValue;
    private Context mContext;

    public MenuSecondHolder(View itemView,Context mContext) {
        super(itemView);
        this.mContext = mContext;
        this.itemView = itemView;
        this.itemView.setFocusable(true);
        this.itemView.setFocusableInTouchMode(true);
        menuName = (TextView)itemView.findViewWithTag(111);
        menuValue = (TextView)itemView.findViewWithTag(222);
        itemView.setBackgroundResource(0);
        itemView.setVisibility(View.VISIBLE);
    }

    @Override
    protected String viewHolderName() {
        return "MenuSecondHolder";
    }

    @Override
    protected void initData(MenuItemData menuItemData){
        super.initData(menuItemData);
        menuName.setText(menuItemData.getItemTitle());


        TypedData typedData = menuItemData.getTypedData();
        if (typedData == null) return;
        TypedData.SkyDataType dataType = typedData.getType();
        String menuValueTitle ="";
        switch(dataType)
        {
            case DATA_TYPE_ENUM:
                EnumData enumData= (EnumData)typedData;
                if(enumData.getEnumTitleList()!=null  &&   enumData.getCurrentIndex() >-1  && enumData.getCurrentIndex() <enumData.getEnumTitleList().size())
                {
                    menuValueTitle = enumData.getEnumTitleList().get(enumData.getCurrentIndex());
                }
                break;
            case DATA_TYPE_SWITCH:
                SwitchData switchData = (SwitchData) typedData;
                menuValueTitle = switchData.getCurrentStr();
                break;
            case DATA_TYPE_RANGE:
                RangeData rangeData = (RangeData)typedData;
                menuValueTitle = String.valueOf(rangeData.getCurrent());
                break;

        }
        if(menuValueTitle ==null ||menuValueTitle.trim().length()==0)
        {
            menuValue.setVisibility(View.GONE);
        }else {
            menuValue.setVisibility(View.VISIBLE);
            menuValue.setText(menuValueTitle);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if(hasFocus){
            itemView.setBackgroundResource(R.drawable.menu_focus);
            menuName.setTextColor(MenuConstant.SECOND_MENU_MAINTEXT_FOCUS_COLOR);
            menuValue.setTextColor(MenuConstant.SECOND_MENU_SUBTEXT_FOCUS_COLOR);
        }else{
            itemView.setBackgroundResource(0);
            menuName.setTextColor(MenuConstant.SECOND_MENU_MAINTEXT_UNFOCUS_COLOR);
            menuValue.setTextColor(MenuConstant.SECOND_MENU_SUBTEXT_UNFOCUS_COLOR);
        }
    }
}
