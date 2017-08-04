package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by yangjianjun on 2017/6/1.
 * 解决recyclerView在快速滑动时焦点错乱从而导致程序奔溃的BUG
 */

public class MyLinearLayoutManager extends LinearLayoutManager {
    public MyLinearLayoutManager(Context context) {
        super(context);
    }

    public MyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public View onFocusSearchFailed(View focused, int focusDirection, RecyclerView.Recycler recycler, RecyclerView.State state) {
        View nextFoucs = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        if(nextFoucs == null){
            return findViewByPosition(1);//null;
        }
        //当前焦点的位置
        int fromPos = getPosition(focused);
        //获取我们希望的下一个焦点的位置
        int nextPos = getNextViewPos(fromPos, focusDirection);
        Log.i("xxxxx","onFocusSearchFailed  fromPos:"+fromPos + ",nextPos:"+nextPos +", itemCount:"+getItemCount());
        return findViewByPosition(nextPos);
    }

    private int getNextViewPos(int fromPos, int direction){
        return 1;
//        if(direction == View.FOCUS_DOWN){
//            if(fromPos >= getItemCount()-1){
//                return getItemCount();
//            }else{
//                return fromPos+1;
//            }
//        }
//        return fromPos;
    }
}
