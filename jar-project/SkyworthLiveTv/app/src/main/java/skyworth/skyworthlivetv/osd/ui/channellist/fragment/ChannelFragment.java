package skyworth.skyworthlivetv.osd.ui.channellist.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil;
import skyworth.skyworthlivetv.osd.ui.channellist.adapter.ChannelAdapter;
import skyworth.skyworthlivetv.osd.ui.channellist.view.VerticalRecyclerView;
import skyworth.skyworthlivetv.R;

import static skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil.editChPosition;

/**
 * Created by Administrator on 2017/5/14/014.
 */

public class ChannelFragment extends Fragment implements ChannelAdapter.OnRvItemListener {

    private static final String TAG = "ChannelFragment";
    private static final int MSG_EDITTEXT_NO_FOCUS = 1000;
    public View mRootView;
    private List<Channel> mChDatas;
    private VerticalRecyclerView rvChannel;

    private ChannelAdapter mAdapter;
    public int position;
    private ChannelListActivity mActivity;

    public void setData(List<Channel> datas) {
        this.mChDatas = datas;
        refresh( );
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.chlist_channel_fragment, null);
        return mRootView;
    }

    public void refresh() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged( );
        }
    }

    public void setBgRightViewVisible(int isVisible) {
        LinearLayout childAt = (LinearLayout) rvChannel.getChildAt(getCurSelectedVisiblePos( ));
        FrameLayout childAt1 = (FrameLayout) childAt.getChildAt(0);
        childAt1.getChildAt(0).setVisibility(isVisible);
    }

    private Handler mHandler = new Handler( ) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EDITTEXT_NO_FOCUS:
                    setRvlastSelectedItemFucos( );
                    if (mOnKeyboradIsDismissListener != null) {
                        mOnKeyboradIsDismissListener.afterDismiss( );
                    }
                    break;
            }
        }
    };

    public void setRvlastSelectedItemFucos() {
        int pos = getCurSelectedVisiblePos( );
        rvChannel.getChildAt(pos).requestFocus( );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (ChannelListActivity) getContext( );
        rvChannel = (VerticalRecyclerView) mRootView.findViewById(R.id.rv_channel);
        mAdapter = new ChannelAdapter(getContext( ), mChDatas, getCurCHPlayPosition( ));
        mAdapter.setOnRvItemListener(this);
        rvChannel.setLayoutManager(new LinearLayoutManager(getContext( )));
        rvChannel.setAdapter(mAdapter);
        mAdapter.setOnEditNoFocus(new ChannelAdapter.OnEditNoFocus( ) {
            @Override
            public void setOnEditNoFocus(int curEditPosition) {
                if (mOnKeyboradIsDismissListener != null) {
                    mOnKeyboradIsDismissListener.beforeDismiss( );
                }
                Message msg = Message.obtain( );
                msg.what = MSG_EDITTEXT_NO_FOCUS;
                mHandler.sendMessageDelayed(msg, 0);
            }
        });


    }

    public void setOnKeyboradIsDismissListener(OnKeyboradIsDismissListener mOnKeyboradIsDismissListener) {
        this.mOnKeyboradIsDismissListener = mOnKeyboradIsDismissListener;
    }

    private OnKeyboradIsDismissListener mOnKeyboradIsDismissListener;

    public Channel getCurEditChannel() {
        editChPosition = position;
        return mChDatas.get(position);
    }

    public interface OnKeyboradIsDismissListener {
        void beforeDismiss();

        void afterDismiss();
    }

    @Override
    public void onStart() {
        super.onStart( );
        if (ChannelUtil.isChFirstEnter) {
            rvChannel.requestFocus( );
            int posTemp = getCurCHPlayPosition( );
            rvChannel.scrollToPosition(posTemp);
        }
    }


    public int getCurCHPlayPosition() {
        Channel channel = PlatformChannelManager.getInstance( ).GetCurrentChannel( );
        if (mChDatas != null && channel != null) {
            for (int i = 0; i < mChDatas.size( ); i++) {
                if (mChDatas.get(i).getChannelNumber( ) == channel.getChannelNumber( )) {
                    return i;
                }
            }

        }
        return -1;
    }

    public int getCurSelectedVisiblePos() {
        LinearLayoutManager layoutMgr = (LinearLayoutManager) rvChannel.getLayoutManager( );
        int firstPosition = layoutMgr.findFirstVisibleItemPosition( );
        return position - firstPosition;
    }

    public RecyclerView getVerticalRv() {
        return rvChannel;
    }

    @Override
    public void setOnRvItemSelected(int position, View v) {
        this.position = position;
    }

    @Override
    public void setOnRvItemClick(int position, Channel channel) {
        Log.v(TAG, "current click channel name:   " + channel.getName( ));
        PlatformChannelManager.getInstance( ).GotoChannel(channel);
        mAdapter.setCurCHPosition(position);
        mAdapter.notifyItemChanged(mAdapter.getLastCHPosition());//refresh last  item
        mAdapter.notifyItemChanged(position);//refresh current selected item
        mAdapter.settLastCHPosition(position);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void setOnRvItemLongClick(int position, View v, Channel channel) {
        EditText tv = (EditText) v;
        tv.setInputType(InputType.TYPE_CLASS_TEXT);
        tv.setFocusable(true);
        tv.requestFocus( );
        tv.selectAll( );
        tv.setHint(channel.getName( ));
        InputMethodManager imm = (InputMethodManager) getContext( ).getSystemService
                (Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(tv, 0);
    }

    public int getCount() {
        return mAdapter.getItemCount( );
    }
}

