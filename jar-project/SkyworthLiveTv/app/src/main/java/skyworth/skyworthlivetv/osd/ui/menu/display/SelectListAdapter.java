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
import skyworth.skyworthlivetv.osd.common.data.DataUtil;
import skyworth.skyworthlivetv.osd.common.data.TypedData;


/**
 * Created by xeasy on 2017/5/20.
 */

public class SelectListAdapter extends SelectListBaseAdapter<SelectListHolder> {
    private Context mContext;
    public SelectListAdapter(Context context) {
        this.mContext = context;
    }
    @Override
    public SelectListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"SelectListAdapter onCreateViewHolder");
        FrameLayout listItemtLayout = new FrameLayout(mContext);
        int listItemWidth = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SELECT_LIST_WIDTH);
        int listItemHeight = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SELECT_LIST_ITEM_HEIGHT);
        listItemtLayout.setLayoutParams(new RecyclerView.LayoutParams(listItemWidth,listItemHeight));
        listItemtLayout.setBackgroundResource(R.drawable.menu_focus);
        listItemtLayout.setVisibility(View.INVISIBLE);

        ImageView chechedBox = new ImageView(mContext);
        chechedBox.setFocusable(false);
        chechedBox.setTag(111);
        FrameLayout.LayoutParams chechedBoxLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        chechedBoxLp.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
        chechedBoxLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MULTI_SELECT_LIST_CHECKBOX_LEFT_MARGIN);
        chechedBox.setImageResource(R.drawable.list_single);
        chechedBox.setVisibility(View.VISIBLE);
        listItemtLayout.addView(chechedBox, chechedBoxLp);

        TextView itemName = new TextView(mContext);
        itemName.setFocusable(false);
        itemName.setTextSize(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENU_TEXT_SIZE));
        itemName.setTextColor(mContext.getResources().getColorStateList(R.color.color_selectlist,null));
        itemName.setTag(222);
        FrameLayout.LayoutParams itemNameLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        itemNameLp.gravity = Gravity.CENTER_VERTICAL|Gravity.LEFT;
        itemNameLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MULTI_SELECT_LIST_ITEMNAME_RIGHT_MARGIN);
        listItemtLayout.addView(itemName, itemNameLp);
        return new SelectListHolder(listItemtLayout,mContext);
    }
    @Override
    public void onBindViewHolder(SelectListHolder holder, int position) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"SelectListAdapter onBindViewHolder position:"+ position);
        SelectItemData selectItemData = new SelectItemData();
        if(skyDataType == TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST){
            selectItemData.setItemSelectitemType(SelectItemData.SELECTITEM_TYPE.SINGLE);
            if(singleSelectListData.getCurrentIndex() == position) {
                selectItemData.setItemSelected(true);
            }
            else {
                selectItemData.setItemSelected(false);
            }
            selectItemData.setItemTitle(singleSelectListData.getEnumTitleList().get(position));
        }else if(skyDataType == TypedData.SkyDataType.DATA_TYPE_MULTI_SELECT_LIST){
            selectItemData.setItemSelectitemType(SelectItemData.SELECTITEM_TYPE.MULTI);
            selectItemData.setItemSelected(multiSelectListData.isSelected(position));
            selectItemData.setItemTitle(multiSelectListData.getEnumTitleList().get(position));
        }
        holder.initData(selectItemData);
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        int itemCount =0;
        if(skyDataType == TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST){
            itemCount = singleSelectListData.getEnumCount();
        }else if(skyDataType == TypedData.SkyDataType.DATA_TYPE_MULTI_SELECT_LIST){
            itemCount = multiSelectListData.getEnumCount();
        }
        return itemCount;
    }
    @Override
    protected String adapterName() {
        return "SelectListAdapter";
    }

    @Override
    public boolean dispatchKeyDownCode(View view, int keyCode) {
        int focusedPosition = getFocusedPosition();
        if (focusedPosition != RecyclerView.NO_POSITION &&
                getFocused() != null) {
            switch(keyCode)
            {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                     return true;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return true;
                case KeyEvent.KEYCODE_DPAD_CENTER:
                {
                    int preIndex = -1;
                    if(skyDataType == TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST){
                        preIndex = singleSelectListData.getCurrentIndex();
                    }
                    if(DataUtil.changeSelectItemData(menuItemData.getTypedData(),focusedPosition))
                    {
                        selectListItemOnkeyListener.onItemOnClick(focusedPosition,menuItemData);
                        if(skyDataType == TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST){
                            Log.d(GlobalDefinitions.DEBUG_TAG,"  focusedPosition:"+focusedPosition+"  preIndex:"+preIndex);
                            notifyItemChanged(focusedPosition);
                            if(preIndex!= focusedPosition) {
                                notifyItemChanged(preIndex);
                            }
                        }else {
                            notifyItemChanged(focusedPosition);
                        }
                    }
                }
                return true;
                case KeyEvent.KEYCODE_BACK:
                {
                    selectListItemOnkeyListener.onItemOnKeyBack(focusedPosition,menuItemData);
                }
                return true;
                default:break;
            }
        }
        return false;
    }


}
