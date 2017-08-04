package skyworth.skyworthlivetv.osd.ui.inputSource.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import Oceanus.Tv.Service.SourceManager.Source;
import skyworth.platformsupport.PlatformSourceManager;
import skyworth.skyworthlivetv.R;

/**
 * Created by yangxiong on 2017/5/3.
 */

public class InputSourceAdapter extends RecyclerView.Adapter<InputSourceAdapter.InputSourceViewHolder> {
    private List<Source> mSourceList;
    private Context mContext;

    public InputSourceAdapter(Context context) {
        mContext = context;
        RefreshSourceList();
    }
    public void RefreshSourceList()
    {
        mSourceList = PlatformSourceManager.getInstance( ).GetSourceList( );
    }

    @Override
    public InputSourceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        InputSourceViewHolder holder = new InputSourceViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.tv_input_source_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final InputSourceViewHolder holder, int position) {
        holder.tvSourceName.setText(mSourceList.get(position).getName( ));
        if (mOnSourceItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener( ) {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition( );
                    mOnSourceItemClickListener.onItemClick(holder.itemView, pos,mSourceList.get(pos));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mSourceList.size( );
    }

    public void addSource(int position, Source source) {
        mSourceList.add(position, source);
        notifyItemInserted(position);
    }

    public void removeSource(int position) {
        mSourceList.remove(position);
        notifyItemRemoved(position);
    }

    public void updateSource(int position,Source source){
        mSourceList.set(position,source);
        notifyItemChanged(position);
    }

    class InputSourceViewHolder extends RecyclerView.ViewHolder {

        TextView tvSourceName;

        public InputSourceViewHolder(View view) {
            super(view);
            tvSourceName = (TextView) view.findViewById(R.id.tv_input_source_name);
        }
    }

    public interface OnSourceItemClickListener {
        void onItemClick(View view, int position,Source source);
    }

    private OnSourceItemClickListener mOnSourceItemClickListener;

    public void setSourceOnItemClick(OnSourceItemClickListener mOnItemClickListener) {
        this.mOnSourceItemClickListener = mOnItemClickListener;
    }
}
