package skyworth.skyworthlivetv.osd.ui.channellist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platform.IPlatformChannelManager;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.channellist.view.CustomEditText;

/**
 * Created by Administrator on 2017/4.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.MyBaseViewHolder> {

    private static final String TAG = "ChannelAdapter";
    private ChannelListActivity mActivity;
    private int mCurCHPosition;
    private List<Channel> mDatas;
    private final IPlatformChannelManager mChManager;
    private int mLastCHPosition;
    private int mCurEditPosition = -1;

    public ChannelAdapter(Context mActivity, List<Channel> mDatas, int curCHPosition) {
        this.mDatas = mDatas;
        this.mActivity = (ChannelListActivity) mActivity;
        mCurCHPosition = curCHPosition;
        mLastCHPosition =curCHPosition;
        mChManager = PlatformChannelManager.getInstance( );
    }

    public void setCurCHPosition(int mCurCHPosition) {
        this.mCurCHPosition = mCurCHPosition;
    }
    public int getLastCHPosition() {
        return mLastCHPosition;
    }
    public void settLastCHPosition(int mLastCHPosition) {
        this.mLastCHPosition = mLastCHPosition;
    }

    @Override
    public MyBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).
                inflate(R.layout.chlist_channel_rv_item, null);
        return new MyBaseViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyBaseViewHolder holder, final int position) {
        final Channel channel = mDatas.get(position);

        holder.llChannelItem.setOnFocusChangeListener(new View.OnFocusChangeListener( ) {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOnRvItemListener.setOnRvItemSelected(position, v);
                }
            }
        });

        holder.llChannelItem.setOnClickListener(new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                mOnRvItemListener.setOnRvItemClick(position, channel);
            }
        });

        holder.llChannelItem.setOnLongClickListener(new View.OnLongClickListener( ) {
            @Override
            public boolean onLongClick(View v) {
                mCurEditPosition = position;
                mOnRvItemListener.setOnRvItemLongClick(position, holder.etChName, channel);
                return true;
            }
        });

        holder.etChName.setOnFocusChangeListener(new View.OnFocusChangeListener( ) {
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

        holder.etChName.setOnEditorActionListener(new TextView.OnEditorActionListener( ) {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.v(TAG, "setOnEditorActionListener:" + "actionId:  " + actionId);
                mDatas.get(mCurEditPosition).setName(v.getText( ).toString( ));
                v.clearFocus( );
                holder.llChannelItem.setFocusable(true);
                holder.llChannelItem.requestFocus( );
                InputMethodManager imm = (InputMethodManager) mActivity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                holder.etChName.isInterceptKeyUp = true;
                mChManager.saveChannel(channel);
                return false;
            }

        });

        holder.etChName.setText(channel.getChannelNumber( ) + "  " + channel.getName( ));
        boolean isCurCh = false;
        if (mCurCHPosition != -1){
            isCurCh= (mDatas.get(mCurCHPosition).getChannelNumber( )
                    == channel.getChannelNumber( ));
        }
        if (ChannelUtil.isChFirstEnter && isCurCh) {
            holder.llChannelItem.requestFocus( );
            mOnRvItemListener.setOnRvItemSelected(position, holder.etChName);
            ChannelUtil.isChFirstEnter = false;
        }

        holder.etChName.setTextColor(isCurCh ? mActivity.mChItemFocusColor :
                mActivity.mChItemDefaultColor);
        holder.ivDtv.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivAtv.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivRadio.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivFav.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivLock.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivSkip.setAlpha(isCurCh ? 1 : 0.4f);
        holder.ivKey.setAlpha(isCurCh ? 1 : 0.4f);

        holder.mBgRightView.setVisibility(position == ChannelUtil.editChPosition ?
                View.VISIBLE : View.INVISIBLE);
        holder.ivFav.setVisibility(channel.getIsFav( ) ? View.VISIBLE : View.INVISIBLE);
        holder.ivSkip.setVisibility(channel.getIsSkip( ) ? View.VISIBLE : View.INVISIBLE);
        holder.ivLock.setVisibility(channel.getIsLock( ) ? View.VISIBLE : View.INVISIBLE);
        if (channel.getDtvAttr( ) != null && channel.getDtvAttr( ).getbIsScramble( )) {
            holder.ivKey.setVisibility(View.VISIBLE);
        } else {
            holder.ivKey.setVisibility(View.INVISIBLE);
        }
        switch (channel.getType( )) {
            case E_SERVICE_ATV:
                holder.ivAtv.setVisibility(View.VISIBLE);
                holder.ivDtv.setVisibility(View.GONE);
                holder.ivRadio.setVisibility(View.GONE);
                break;
            case E_SERVICE_DTV_DVBC:
            case E_SERVICE_DTV_DVBT:
            case E_SERVICE_DTV_DVBS:
                holder.ivAtv.setVisibility(View.GONE);
                holder.ivDtv.setVisibility(View.VISIBLE);
                holder.ivRadio.setVisibility(View.GONE);
                break;
            case E_SERVICE_OTHER_APP:
                break;
            default:
                holder.ivAtv.setVisibility(View.GONE);
                holder.ivDtv.setVisibility(View.GONE);
                holder.ivRadio.setVisibility(View.VISIBLE);
                break;
        }


    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size( );
    }

    class MyBaseViewHolder extends RecyclerView.ViewHolder {
        CustomEditText etChName;
        LinearLayout llChannelItem;
        ImageView ivDtv, ivAtv, ivRadio, ivFav, ivLock, ivSkip, ivKey;
        View mBgRightView;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            mBgRightView = itemView.findViewById(R.id.chlist_ch_bg_right);
            etChName = (CustomEditText) itemView.findViewById(R.id.tv_channel_name);
            llChannelItem = (LinearLayout) itemView.findViewById(R.id.ll_channel_item);
            ivDtv = (ImageView) itemView.findViewById(R.id.iv_ch_item_dtv);
            ivAtv = (ImageView) itemView.findViewById(R.id.iv_ch_item_atv);
            ivRadio = (ImageView) itemView.findViewById(R.id.iv_ch_item_radio);
            ivFav = (ImageView) itemView.findViewById(R.id.iv_ch_item_fav);
            ivLock = (ImageView) itemView.findViewById(R.id.iv_ch_item_lock);
            ivSkip = (ImageView) itemView.findViewById(R.id.iv_ch_item_skip);
            ivKey = (ImageView) itemView.findViewById(R.id.iv_ch_item_key);
        }
    }

    public void setOnRvItemListener(OnRvItemListener onRvItemListener) {
        this.mOnRvItemListener = onRvItemListener;
    }

    private OnRvItemListener mOnRvItemListener;

    public interface OnRvItemListener {
        void setOnRvItemSelected(int position, View v);

        void setOnRvItemClick(int position, Channel channel);

        void setOnRvItemLongClick(int position, View v, Channel channel);
    }

    public void setOnEditNoFocus(OnEditNoFocus mOnEditNoFocus) {
        this.mOnEditNoFocus = mOnEditNoFocus;
    }

    private OnEditNoFocus mOnEditNoFocus;

    public interface OnEditNoFocus {

        void setOnEditNoFocus(int curEditPosition);
    }
}
