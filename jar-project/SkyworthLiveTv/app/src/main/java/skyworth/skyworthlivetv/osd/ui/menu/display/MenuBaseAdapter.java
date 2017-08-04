package skyworth.skyworthlivetv.osd.ui.menu.display;

/**
 * Created by xeasy on 2017/5/2.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import java.util.List;

import skyworth.skyworthlivetv.global.GlobalDefinitions;


public abstract class MenuBaseAdapter<VH extends skyworth.skyworthlivetv.osd.ui.menu.display.MenuBaseViewHolder> extends RecyclerView.Adapter<VH>
        implements skyworth.skyworthlivetv.osd.ui.menu.display.OnItemFocusChangeListener {

    public interface MenuItemOnkeyListener
    {
        public boolean onItemOnKeyLeft(int itemID, MenuItemData currentData);

        public boolean onItemOnKeyRight(int itemID, MenuItemData currentData);

        public void  onItemFocusChangeListener(int itemID, MenuItemData currentData,boolean focus);

        public boolean onItemOnClick(int itemIndex, MenuItemData currentData);

        public boolean onItemOnKeyBack(int itemID,MenuItemData currentData);

        public boolean onItemOnKeyOther(int itemID, int keyCode);
    }
    protected List<MenuItemData> menuItemDataList;
    protected MenuItemOnkeyListener menuItemOnkeyListener;
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
        if((focusedPosition >=0)  &&  (focusedPosition < menuItemDataList.size()))
        {
            menuItemOnkeyListener.onItemFocusChangeListener(focusedPosition,menuItemDataList.get(focusedPosition),hasFocus);
        }
    }

    public void setData(List<MenuItemData> menuItemDataList) {
        this.menuItemDataList = menuItemDataList;
    }

    public void setMenuItemOnkeyListener(MenuItemOnkeyListener menuItemOnkeyListener)
    {
        this.menuItemOnkeyListener = menuItemOnkeyListener;
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

