package skyworth.skyworthlivetv.osd.ui.channellist.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.platform.IPlatformChannelManager;

import Oceanus.Tv.Service.ChannelManager.Channel;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil;
import skyworth.skyworthlivetv.R;

import static skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil.editChPosition;

/**
 * Created by yangxiong on 2017/5/16.
 */

public class SecondFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "SecondFragment";
    private View rootView;
    private LinearLayout llSecondView;
    private Button btnFav;
    private Button btnLock;
    private Button btnSkip;
    private Button btnTuning;
    private FragmentManager mFragmentManager;
    public int mTopMargin;
    private int mChItemRightFocusColor;
    private ChannelListActivity mActivity;
    private ChannelFragment channelFragment;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chlist_second_fragment, null);
        llSecondView = (LinearLayout) rootView.findViewById(R.id.ll_second_view);
        btnFav = (Button) rootView.findViewById(R.id.btn_secondefragment_favourite);
        btnLock = (Button) rootView.findViewById(R.id.btn_secondefragment_lock);
        btnSkip = (Button) rootView.findViewById(R.id.btn_secondefragment_skip);
        btnTuning = (Button) rootView.findViewById(R.id.btn_secondefragment_tuning);

        btnFav.setOnClickListener(this);
        btnLock.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
        btnTuning.setOnClickListener(this);

        btnFav.setOnFocusChangeListener(this);
        btnLock.setOnFocusChangeListener(this);
        btnSkip.setOnFocusChangeListener(this);
        btnTuning.setOnFocusChangeListener(this);


        mFragmentManager = getFragmentManager( );
        return rootView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (ChannelListActivity) getContext( );
        mChItemRightFocusColor = mActivity.getResources( ).getColor(R.color.channel_item_focus);
    }

    @Override
    public void onStart() {
        super.onStart( );
        btnFav.requestFocus( );
        channelFragment = (ChannelFragment) mFragmentManager.
                findFragmentByTag(ChannelUtil.CH_FRAGMENT_TAG);
        int position = channelFragment.getCurSelectedVisiblePos( );
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) llSecondView.getLayoutParams( );
        if (position > 4) {
            position = 4;
        }
        mTopMargin = 220 + position * 100;
        lp.topMargin = mTopMargin;
        llSecondView.setLayoutParams(lp);

    }


    public boolean isFavFocus() {
        return btnFav == null ? false : btnFav.hasFocus( );
    }

    public boolean isTunFocus() {
        return btnTuning == null ? false : btnTuning.hasFocus( );
    }

    @Override
    public void onClick(View v) {
        Channel channel = mActivity.mChannelDatas.get(channelFragment.position);
        editChPosition = channelFragment.position;
        IPlatformChannelManager manger = PlatformChannelManager.getInstance( );
        switch (v.getId( )) {
            case R.id.btn_secondefragment_favourite:
                FragmentTransaction ft = mFragmentManager.beginTransaction( );
                TridFragment tridFragment = new TridFragment( );
                ft.replace(R.id.trid_fragment, tridFragment, ChannelUtil.TRID_FRAGMENT_TAG);
                ft.commit( );
                break;
            case R.id.btn_secondefragment_lock:
                channel.setIsLock(!channel.getIsLock());
                channelFragment.refresh( );
                manger.saveChannel(channel);
                break;
            case R.id.btn_secondefragment_skip:
                channel.setIsSkip(!channel.getIsSkip());
                manger.saveChannel(channel);
                channelFragment.refresh( );
                break;
            case R.id.btn_secondefragment_tuning:

                break;
            default:
                break;
        }
    }

    public void setRightTextColor() {
        btnFav.setTextColor(mChItemRightFocusColor);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId( )) {
                case R.id.btn_secondefragment_favourite:
                    btnFav.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                            : mActivity.mChItemDefaultColor);
                    break;
                case R.id.btn_secondefragment_lock:
                    btnLock.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                            : mActivity.mChItemDefaultColor);
                    break;
                case R.id.btn_secondefragment_skip:
                    btnSkip.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                            : mActivity.mChItemDefaultColor);
                    break;
                case R.id.btn_secondefragment_tuning:
                    btnTuning.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                            : mActivity.mChItemDefaultColor);
                    break;
                default:
                    break;
            }
    }

    public void setFavFocus() {
        btnFav.requestFocus( );
    }
}