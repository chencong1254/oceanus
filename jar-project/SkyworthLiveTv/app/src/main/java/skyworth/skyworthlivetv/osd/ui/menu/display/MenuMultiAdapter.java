package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.SkyScreenParams;
import skyworth.skyworthlivetv.osd.common.data.DataUtil;

/**
 * Created by xeasy on 2017/5/2.
 */
public class MenuMultiAdapter extends MenuBaseAdapter<MenuMultiHolder> {
    private Context mContext;

    public MenuMultiAdapter(Context context) {
        this.mContext = context;
    }
    @Override
    public MenuMultiHolder onCreateViewHolder(ViewGroup parent, int viewType){
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuMultiAdapter onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.menu_item_multi_root, parent, false);
        int menuItemWidth = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_WIDTH);
        int menuItemHeight = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENUITEM_HEIGHT);
        view.setLayoutParams(new RecyclerView.LayoutParams(menuItemWidth,menuItemHeight));
        view.setBackgroundResource(R.drawable.menu_focus);
        view.setVisibility(View.INVISIBLE);
        return new MenuMultiHolder(view,mContext);
    }
    @Override
    public void onBindViewHolder(MenuMultiHolder holder, int position) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuMultiAdapter onBindViewHolder position:"+position);
        MenuItemData menuItemData = menuItemDataList.get(position);
        holder.initData(menuItemData);
        super.onBindViewHolder(holder, position);
    }

    @Override
    protected String adapterName() {
        return "MenuMultiAdapter";
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
                    if(DataUtil.changeMenuItemData(menuItemData.getTypedData(),true))
                    {
                        menuItemOnkeyListener.onItemOnKeyLeft(focusedPosition,menuItemData);
                        notifyItemChanged(focusedPosition);
                    }
                }
                return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                {
                    if(DataUtil.changeMenuItemData(menuItemData.getTypedData(),false))
                    {
                        menuItemOnkeyListener.onItemOnKeyLeft(focusedPosition,menuItemData);
                        notifyItemChanged(focusedPosition);
                    }
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
        Log.d(GlobalDefinitions.DEBUG_TAG,"MenuMultiAdapter getItemCount:"+menuItemDataList.size());
        return menuItemDataList.size();
    }
}