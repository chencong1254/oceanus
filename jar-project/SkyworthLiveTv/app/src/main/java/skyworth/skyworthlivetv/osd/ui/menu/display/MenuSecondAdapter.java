package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import skyworth.skyworthlivetv.osd.common.SkyScreenParams;
import skyworth.skyworthlivetv.R;

/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuSecondAdapter extends MenuBaseAdapter<skyworth.skyworthlivetv.osd.ui.menu.display.MenuSecondHolder> {

    private Context mContext;

    public MenuSecondAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MenuSecondHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LinearLayout menuItemtLayout = new LinearLayout(mContext);
        int menuItemWidth = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENU_WIDTH);
        int menuItemHeight = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENUITEM_HEIGHT);
        menuItemtLayout.setLayoutParams(new RecyclerView.LayoutParams(menuItemWidth,menuItemHeight));
        menuItemtLayout.setOrientation(LinearLayout.VERTICAL);
        menuItemtLayout.setGravity(Gravity.LEFT);
        menuItemtLayout.setBackgroundResource(R.drawable.menu_focus);
        menuItemtLayout.setVisibility(View.INVISIBLE);

        TextView menuName = new TextView(mContext);
        menuName.setFocusable(false);
        menuName.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENU_MAINTEXT_SIZE));
        menuName.setTextColor(MenuConstant.SECOND_MENU_MAINTEXT_UNFOCUS_COLOR);
        menuName.setTag(111);
        LinearLayout.LayoutParams menuNameLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        menuItemtLayout.addView(menuName, menuNameLp);

        TextView menuValue = new TextView(mContext);
        menuValue.setFocusable(false);
        menuValue.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENU_SUBTEXT_SIZE));
        menuValue.setTextColor(MenuConstant.SECOND_MENU_SUBTEXT_UNFOCUS_COLOR);
        menuValue.setTag(222);
        LinearLayout.LayoutParams menuValueLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        menuItemtLayout.addView(menuValue, menuValueLp);

        return new MenuSecondHolder(menuItemtLayout,mContext);
    }
    @Override
    public void onBindViewHolder(MenuSecondHolder holder, int position) {
        MenuItemData menuParentItem = menuItemDataList.get(position);
        holder.initData(menuParentItem);
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected String adapterName() {
        return "MenuSecondAdapter";
    }

    @Override
    public boolean dispatchKeyDownCode(View view,int keyCode) {
        int focusedPosition = getFocusedPosition();
        if (focusedPosition != RecyclerView.NO_POSITION &&
                getFocused() != null) {

            MenuItemData menuItemData = menuItemDataList.get(focusedPosition);
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                {
                    menuItemOnkeyListener.onItemOnKeyLeft(focusedPosition,menuItemData);
                }
                return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                {
                    menuItemOnkeyListener.onItemOnKeyRight(focusedPosition,menuItemData);
                }
                return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                {
                    menuItemOnkeyListener.onItemOnClick(focusedPosition,menuItemData);
                }
                return true;
                case KeyEvent.KEYCODE_BACK:
                {
                    menuItemOnkeyListener.onItemOnKeyBack(focusedPosition,menuItemData);
                }
                return true;
                default:break;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return menuItemDataList.size();
    }
}
