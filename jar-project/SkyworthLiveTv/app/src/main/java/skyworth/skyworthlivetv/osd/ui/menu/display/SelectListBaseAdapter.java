package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.MultiSelectListData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;

/**
 * Created by xeasy on 2017/5/20.
 */

public abstract class SelectListBaseAdapter<VH extends  SelectListBaseHolder> extends RecyclerView.Adapter<VH>
        implements skyworth.skyworthlivetv.osd.ui.menu.display.OnItemFocusChangeListener
{

    public interface SelectListItemOnkeyListener
    {
        public boolean onItemOnClick(int itemIndex, MenuItemData currentData);

        public boolean onItemOnKeyBack(int itemID,MenuItemData currentData);

        public boolean onItemOnKeyOther(int itemID, int keyCode);
    }
    protected MenuItemData menuItemData;
    protected TypedData.SkyDataType  skyDataType = TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST;
    protected SingleSelectListData  singleSelectListData = null;
    protected MultiSelectListData   multiSelectListData = null;
    protected SelectListItemOnkeyListener selectListItemOnkeyListener;
    private RecyclerView hostView;
    private View focused;
    private int focusedPosition = RecyclerView.NO_POSITION;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Log.d(GlobalDefinitions.DEBUG_TAG,adapterName()+"  onAttachedToRecyclerView hostView:"+hostView+"  recyclerView:"+recyclerView+"  focusedPosition:"+focusedPosition);
        hostView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        Log.d(GlobalDefinitions.DEBUG_TAG,adapterName()+" onDetachedFromRecyclerView hostView:"+hostView+"  focused:"+focused+"  focusedPosition:"+focusedPosition);
        hostView = null;
        focused = null;
        focusedPosition = RecyclerView.NO_POSITION;
    }

    @Override
    public void onBindViewHolder(VH holder, final int position) {
        holder.setOnItemFocusChangeListener(this);
        holder.itemView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() ==KeyEvent.ACTION_DOWN)
                {
                    Log.d(GlobalDefinitions.DEBUG_TAG,adapterName()+"  on keyCode:"+keyCode+"  position:"+position);
                    switch(keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if(position == 0)
                            {
                                return true;
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if(position == getItemCount() -1)
                            {
                                return true;
                            }
                            break;
                        default:
                            return dispatchKeyDownCode(v,keyCode);
                    }

                }
                return false;
            }
        });
        Log.d(GlobalDefinitions.DEBUG_TAG,adapterName()+"  onBindViewHolder focusedPosition:"+focusedPosition+"  position:"+position);
        if (focusedPosition == position) {
            holder.itemView.requestFocus();
        }
    }


    @Override
    public void onItemFocusChange(View view, boolean hasFocus) {
        if (hasFocus) {
            focused = view;
            focusedPosition = hostView.getChildAdapterPosition(focused);
            Log.d(GlobalDefinitions.DEBUG_TAG,adapterName()+" onItemFocusChange focusedPosition:"+focusedPosition);

        }
    }
    public void setData(MenuItemData menuItemData) {
        this.menuItemData = menuItemData;
        skyDataType =  menuItemData.getTypedData().getType();
        switch(skyDataType)
        {
            case DATA_TYPE_SINGLE_SELECT_LIST:
                singleSelectListData = (SingleSelectListData)menuItemData.getTypedData();
                break;
            case DATA_TYPE_MULTI_SELECT_LIST:
                multiSelectListData = (MultiSelectListData)menuItemData.getTypedData();
                break;
        }
    }

    public void setSelectListItemOnkeyListener(SelectListItemOnkeyListener selectListItemOnkeyListener)
    {
        this.selectListItemOnkeyListener = selectListItemOnkeyListener;
    }

    public int getFocusedPosition() {
        return getFocused() == null ? RecyclerView.NO_POSITION : hostView.getChildAdapterPosition(hostView.findFocus());
    }

    public View getFocused() {
        return hostView == null ? null : hostView.findFocus();
    }

    public void focus(int position) {
        Log.d(GlobalDefinitions.DEBUG_TAG, adapterName()+" focus, position:"+position+"   hostView:"+hostView+"  focusedPosition:"+focusedPosition);
        if (position > -1 && position < getItemCount()) {
            if (position != focusedPosition && hostView != null) {
                hostView.scrollToPosition(position);
            }
            focusedPosition = position;
            if (getFocusedPosition() != focusedPosition)
            {
                notifyItemChanged(focusedPosition);
            }
        }
    }
    protected  abstract  String adapterName();
    public abstract boolean dispatchKeyDownCode(View view,int keyCode);
}