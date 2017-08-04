package skyworth.skyworthlivetv.osd.ui.menu.display;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by xeasy on 2017/5/20.
 */

public class SelectList  extends RecyclerView {
    public SelectList(Context context) {
        this(context, null);
    }

    public SelectList(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SelectList(Context context, AttributeSet attrs, int defStyle) {
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
        if (adapter instanceof SelectListAdapter) {
            super.setAdapter(adapter);
        } else {
            throw new RuntimeException("SelectList only accept SelectListAdapter.");
        }
    }

    @Override
    public SelectListAdapter getAdapter() {
        return (SelectListAdapter) super.getAdapter();
    }
}