package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.SkyScreenParams;

/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuFirstAdapter extends MenuBaseAdapter<MenuFirstHolder>  {
    private Context mContext;

    public MenuFirstAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MenuFirstHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuFirstAdapter onCreateViewHolder");
        FrameLayout menuItemtLayout = new FrameLayout(mContext);
        int menuItemWidth = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENU_WIDTH);
        int menuItemHeight = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENUITEM_HEIGHT);
        menuItemtLayout.setLayoutParams(new RecyclerView.LayoutParams(menuItemWidth,menuItemHeight));
        menuItemtLayout.setBackgroundResource(R.drawable.menu_focus);
        menuItemtLayout.setVisibility(View.INVISIBLE);


        TextView menuName = new TextView(mContext);
        menuName.setFocusable(false);
        menuName.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENU_TEXT_SIZE));
        menuName.setTextColor(MenuConstant.FIRST_MENU_TEXT_UNFOCUS_COLOR);
        menuName.setTag(111);
        FrameLayout.LayoutParams menuNameLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        menuNameLp.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
        menuItemtLayout.addView(menuName, menuNameLp);

        ImageView rightArrow = new ImageView(mContext);
        rightArrow.setFocusable(false);
        rightArrow.setTag(222);
        FrameLayout.LayoutParams rightArrowLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        rightArrowLp.gravity = Gravity.CENTER_VERTICAL|Gravity.RIGHT;
        rightArrowLp.rightMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENU_ARROW_RIGHT_MARGIN);
        rightArrow.setImageResource(R.drawable.menufirst_arrow);
        rightArrow.setVisibility(View.INVISIBLE);
        menuItemtLayout.addView(rightArrow, rightArrowLp);
        return new MenuFirstHolder(menuItemtLayout,mContext);
    }
    @Override
    public void onBindViewHolder(MenuFirstHolder holder, int position) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuFirstAdapter onBindViewHolder position:"+position);
        MenuItemData menuItemData = menuItemDataList.get(position);
        holder.initData(menuItemData);
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected String adapterName() {
        return "MenuFirstAdapter";
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
                    TextView menuName = (TextView)view.findViewWithTag(111);
                    ImageView rightArrow = (ImageView)view.findViewWithTag(222);
                    rightArrow.setSelected(true);
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
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuFirstAdapter getItemCount:"+menuItemDataList.size());
        return menuItemDataList.size();
    }
}
