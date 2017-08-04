package skyworth.skyworthlivetv.osd.ui.channellist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import skyworth.skyworthlivetv.R;

/**
 * Created by Administrator on 2017/4.
 */

public class SortAdapter extends RecyclerView.Adapter<SortAdapter.MyBaseViewHolder> {

    private static final String TAG = "SortAdapter" ;
    private Context mContext;
    private List<String> mDatas;

    public SortAdapter(Context mContext, List<String> mDatas) {
        this.mDatas = mDatas;
        this.mContext = mContext;

    }

    @Override
    public MyBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyBaseViewHolder viewHolder = new MyBaseViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.chlist_sort_rv_item, null));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyBaseViewHolder holder, final int position) {
        holder.tvChannelName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    mOnRvItemSelectorListener.setOnRvItemSelectorListener(position);
                } else {
                }
            }
        });
        holder.tvChannelName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View  view) {
            }
        });
        holder.tvChannelName.setText(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyBaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvChannelName;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            tvChannelName = (TextView) itemView.findViewById(R.id.tv_sort_name);
        }
    }
    public void setOnRvItemSelectorListener(OnRvItemSelectorListener onRvItemSelectorListener){
        this.mOnRvItemSelectorListener =onRvItemSelectorListener;
    }

    private OnRvItemSelectorListener mOnRvItemSelectorListener;

    public interface OnRvItemSelectorListener {
        void setOnRvItemSelectorListener(int position);
    }

}
