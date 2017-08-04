package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import skyworth.skyworthlivetv.global.GlobalDefinitions;


/**
 * Created by xeasy on 2017/5/17.
 */

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent != null) {
            int childIndex = parent.getChildAdapterPosition(view);
            RecyclerView.Adapter adapter = parent.getAdapter();
            if (adapter != null) {
                int childCount = adapter.getItemCount();
//                outRect.left = 0;
//                outRect.right = 0;
                if (childIndex == 0) {   // the first one，第一个，左边缘间距
                    outRect.left = 0;  //  12dp;
                }
                if (childIndex == childCount - 1) {  // the last one,最后一个，右边缘间距
                    outRect.right =0;  // 12dp
                }
//                outRect.top = 0;
                outRect.bottom = space;

                Log.d(GlobalDefinitions.DEBUG_TAG, "--->getItemOffsets()--childIndex:" + childIndex + ",childCount=" + childCount);
            }
        }
    }
}
