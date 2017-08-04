package skyworth.skyworthlivetv.osd.ui.channellist.fragment;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import skyworth.skyworthlivetv.osd.ui.channellist.adapter.SortAdapter;
import skyworth.skyworthlivetv.R;

public class SortFragment extends Fragment
{
	private View mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mRootView = inflater.inflate(R.layout.chlist_sort_fragment, null);
		return  mRootView;
	
	}

	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		final List<String> datas0 = new ArrayList<>();
		datas0.add("Default");
		datas0.add("Number");
		datas0.add("Number");
		datas0.add("Signal type");
		datas0.add("Programe name");

		RecyclerView rvChannel = (RecyclerView) mRootView.findViewById(R.id.rv_sort);
		SortAdapter sortAdapter = new SortAdapter(getContext(),datas0);
		sortAdapter.setOnRvItemSelectorListener(new SortAdapter.OnRvItemSelectorListener() {
			@Override
			public void setOnRvItemSelectorListener(int position) {

			}
		});
		rvChannel.setLayoutManager(new LinearLayoutManager(getContext()));
		rvChannel.setAdapter(sortAdapter);
	}

}
