package skyworth.skyworthlivetv.osd.ui.channellist.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platform.IPlatformChannelManager;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.ChannelListInfo;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.channellist.view.CustomEditText;

/**
 * Created by Administrator on 2017/4.
 */

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.MyBaseViewHolder> {

    private static final String TAG = "TopAdapter";
    private final int mTopItemFocusColor;
    private ChannelListActivity mActivity;
    private List<String> mDatas;
    private View itemView;
    private int mCurEditPosition;
    private final IPlatformChannelManager mChManager;
    private final List<ChannelListInfo> mChListInfos;

    public TopAdapter(Context mContext, List<String> mDatas) {
        this.mDatas = mDatas;
        this.mActivity = (ChannelListActivity) mContext;
        mTopItemFocusColor = mContext.getResources( ).getColor(R.color.channel_item_focus);
        mChManager = PlatformChannelManager.getInstance();
        mChListInfos = mChManager.queryFavChannelListInfo( );

    }

    @Override
    public MyBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(mActivity).inflate(R.layout.chlist_top_rv_item, null);
        return new MyBaseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyBaseViewHolder holder, final int position) {
        holder.etChannelName.setOnFocusChangeListener(new View.OnFocusChangeListener( ) {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.v(TAG, "etChName hasFocus:  " + hasFocus);
                if (!hasFocus) {
                    notifyItemChanged(mCurEditPosition);
                    if (mOnEditNoFocus != null) {
                        mOnEditNoFocus.setOnEditNoFocus(mCurEditPosition);
                    }
                }
            }
        });

        holder.etChannelName.setOnEditorActionListener(new TextView.OnEditorActionListener( ) {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG, "setOnEditorActionListener:" + "actionId:  " + actionId);
                String s = v.getText( ).toString( );
                mDatas.remove(mCurEditPosition);
                mDatas.add(mCurEditPosition,s);
                v.clearFocus( );
                holder.llTopItem.setFocusable(true);
                holder.llTopItem.requestFocus( );
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                holder.etChannelName.isInterceptKeyUp = true;
                mChManager.renameFavList(mChListInfos.get(mCurEditPosition-1).getListName(),s);
                return false;
            }

        });
        itemView.setOnLongClickListener(new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View v) {
                mCurEditPosition = position;
                mOnRvItemSelectorListener.setOnRvItemLongClick(mCurEditPosition,
                        holder.etChannelName,holder.etChannelName.getText().toString());
                return true;
            }
        });
        itemView.setOnFocusChangeListener(new View.OnFocusChangeListener( ) {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                holder.etChannelName.setTextColor(hasFocus ? mActivity.mChItemFocusColor :
                        mActivity.mChItemDefaultColor);
                if (mOnRvItemSelectorListener != null && hasFocus) {
                    mOnRvItemSelectorListener.setOnRvItemSelectorListener(position);
                    holder.llTopItem.setBackgroundColor(mTopItemFocusColor);
                } else {
                    holder.llTopItem.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
        holder.llTopItem.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {

            }
        });
        holder.etChannelName.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size( );
    }

    public void setTopItemFocus(boolean b) {
        itemView.setFocusable(b);
    }

    class MyBaseViewHolder extends RecyclerView.ViewHolder {
        CustomEditText etChannelName;
        LinearLayout llTopItem;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            etChannelName = (CustomEditText) itemView.findViewById(R.id.et_top_name);
            llTopItem = (LinearLayout) itemView.findViewById(R.id.ll_top_item);
        }
    }

    public void setOnRvItemSelectorListener(OnRvItemSelectorListener onRvItemSelectorListener) {
        this.mOnRvItemSelectorListener = onRvItemSelectorListener;
    }

    private OnRvItemSelectorListener mOnRvItemSelectorListener;

    public interface OnRvItemSelectorListener {
        void setOnRvItemSelectorListener(int position);
        void setOnRvItemLongClick(int mCurEditPosition,View v,String name);
    }

    public void setOnEditNoFocus(ChannelAdapter.OnEditNoFocus mOnEditNoFocus) {
        this.mOnEditNoFocus = mOnEditNoFocus;
    }

    private ChannelAdapter.OnEditNoFocus mOnEditNoFocus;

    public interface OnEditNoFocus {

        void setOnEditNoFocus(int curEditPosition);
    }
}
