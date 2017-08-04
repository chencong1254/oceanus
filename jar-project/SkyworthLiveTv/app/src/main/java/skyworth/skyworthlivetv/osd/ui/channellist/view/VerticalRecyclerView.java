package skyworth.skyworthlivetv.osd.ui.channellist.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by yangxiong on 2016/9/26/026.
 */
public class VerticalRecyclerView extends RecyclerView {

    private static final String TAG = "VerticalRecyclerView";
    private Scroller mScroller;
    private int mLastY = 0;
    private int mTargetPos;
    public int firstItemPosition;

    public VerticalRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public VerticalRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VerticalRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);

        this.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    int lastItemPosition = linearManager.findLastVisibleItemPosition();
                    firstItemPosition = linearManager.findFirstVisibleItemPosition();
                    Log.v(TAG,lastItemPosition + "   " + firstItemPosition);
                }
            }
        });

    }

    @Override
    public void computeScroll() {
        super.computeScroll( );
        if (mScroller != null && mScroller.computeScrollOffset( )) {
            scrollBy(0, mLastY - mScroller.getCurrY( ));
            mLastY = mScroller.getCurrY( );
            postInvalidate( );
        }
    }

    public void smoothToCenter(int position) {
        int parentHeight = getHeight( );
        int firstvisiableposition = ((LinearLayoutManager) getLayoutManager( )).
                findFirstVisibleItemPosition( );
        int count = ((LinearLayoutManager) getLayoutManager( )).getItemCount( );
        mTargetPos = Math.max(0, Math.min(count - 1, position));
        View targetChild = getChildAt(mTargetPos - firstvisiableposition);
        int childTopPx = 0;
        int childBottomPx = 0;
        int childHeight = 0;
        if (targetChild != null) {
            childTopPx = targetChild.getTop( );
        }
        if (targetChild != null) {
            childBottomPx = targetChild.getBottom( );
            childHeight = targetChild.getHeight( );
        }

        int centerTop = parentHeight / 2 - childHeight / 2;
        int centerBottom = parentHeight / 2 + childHeight / 2;

        if (childTopPx > centerTop) {
            mLastY = childTopPx;
            mScroller.startScroll(0, childTopPx, 0, centerTop - childTopPx, 300);
            postInvalidate( );
        } else if (childBottomPx < centerBottom) {
            mLastY = childBottomPx;
            mScroller.startScroll(0, childBottomPx, 0, centerBottom - childBottomPx, 300);
            postInvalidate( );
        }

    }

}
