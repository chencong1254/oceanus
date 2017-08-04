package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by xeasy on 2017/4/28.
 */

public class MenuListView extends RecyclerView {

    public MenuListView(Context context) {
        this(context, null);
    }

    public MenuListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MenuListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        setLayoutManager(manager);
        this.addItemDecoration(new SpacingItemDecoration(-62));
        setFocusable(false);
        setFocusableInTouchMode(false);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setItemAnimator(null);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof MenuBaseAdapter) {
            super.setAdapter(adapter);
        } else {
            throw new RuntimeException("MenuListView only accept MenuBaseAdapter.");
        }
    }

    @Override
    public MenuBaseAdapter getAdapter() {
        return (MenuBaseAdapter) super.getAdapter();
    }
}