package skyworth.skyworthlivetv.osd.ui.channellist.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platform.IPlatformChannelManager;

import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelListInfo;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelListActivity;
import skyworth.skyworthlivetv.osd.ui.channellist.ChannelUtil;

/**
 * Created by yangxiong on 2017/5/16.
 */

public class TridFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private static final String TAG = "TridFragment";

    private LinearLayout mRootView;
    private FragmentManager mFragmentManager;
    private SecondFragment mSecondFragment;
    private ChannelFragment mChannelFragment;
    private LinearLayout llTridFirst, llTridSecond, llTridTrid, llTridFour;
    private TextView tvFavNameFisrt, tvFavNameSecond, tvFavNameTrid, tvFavNameFour;
    private ImageView ivfav1, ivfav2, ivfav3, ivfav4;
    private ChannelListActivity mActivity;
    private IPlatformChannelManager mChannelManager;
    private Drawable mNoFavIcon, mFavIcon, mFavCurSelectIcon;
    private Channel mCurEditChannel;
    private List<Channel> mCh1, mCh2, mCh3, mCh4;
    private List<ChannelListInfo> mChListInfos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = (LinearLayout) inflater.inflate(R.layout.chlist_trid_fragment, null);

        findView( );

        setListener( );

        initData( );

        initFavCircle( );

        return mRootView;
    }

    private void initData() {


        mNoFavIcon = getResources( ).getDrawable(R.drawable.chlist_icon_no_fav_point);
        mFavIcon = getResources( ).getDrawable(R.drawable.chlist_icon_fav_point_current);
        mFavCurSelectIcon = getResources( ).getDrawable(R.drawable.chlist_icon_fav_point_current_select);

        mFragmentManager = getFragmentManager( );
        mChannelManager = PlatformChannelManager.getInstance( );
        mChannelFragment = (ChannelFragment) mFragmentManager.findFragmentByTag(
                ChannelUtil.CH_FRAGMENT_TAG);
        mCurEditChannel = mChannelFragment.getCurEditChannel( );
    }

    private void setListener() {
        llTridFirst.setOnClickListener(this);
        llTridSecond.setOnClickListener(this);
        llTridTrid.setOnClickListener(this);
        llTridFour.setOnClickListener(this);

        llTridFirst.setOnFocusChangeListener(this);
        llTridSecond.setOnFocusChangeListener(this);
        llTridTrid.setOnFocusChangeListener(this);
        llTridFour.setOnFocusChangeListener(this);
    }

    private void findView() {
        llTridFirst = (LinearLayout) mRootView.findViewById(R.id.ll_trid_first);
        llTridSecond = (LinearLayout) mRootView.findViewById(R.id.ll_trid_second);
        llTridTrid = (LinearLayout) mRootView.findViewById(R.id.ll_trid_trid);
        llTridFour = (LinearLayout) mRootView.findViewById(R.id.ll_trid_four);

        tvFavNameFisrt = (TextView) mRootView.findViewById(R.id.tv_fav_name_first);
        tvFavNameSecond = (TextView) mRootView.findViewById(R.id.tv_fav_name_second);
        tvFavNameTrid = (TextView) mRootView.findViewById(R.id.tv_fav_name_trid);
        tvFavNameFour = (TextView) mRootView.findViewById(R.id.tv_fav_name_four);

        ivfav1 = (ImageView) mRootView.findViewById(R.id.iv_fav_type_1);
        ivfav2 = (ImageView) mRootView.findViewById(R.id.iv_fav_type_2);
        ivfav3 = (ImageView) mRootView.findViewById(R.id.iv_fav_type_3);
        ivfav4 = (ImageView) mRootView.findViewById(R.id.iv_fav_type_4);
    }

    private void initFavCircle() {;
        mChListInfos = mChannelManager.queryFavChannelListInfo( );

        mCh1 = mChannelManager.queryFavChannelList(mChListInfos.get(0));
        mCh2 = mChannelManager.queryFavChannelList(mChListInfos.get(1));
        mCh3 = mChannelManager.queryFavChannelList(mChListInfos.get(2));
        mCh4 = mChannelManager.queryFavChannelList(mChListInfos.get(3));

        if (mCh1 != null) {
            for (Channel channel : mCh1) {
                if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                    ivfav1.setBackground(mFavIcon);
                }
            }
        } else {
            ivfav1.setBackground(mNoFavIcon);
        }

        if (mCh2 != null) {
            for (Channel channel : mCh2) {
                if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                    ivfav2.setBackground(mFavIcon);
                }
            }
        } else {
            ivfav2.setBackground(mNoFavIcon);
        }

        if (mCh3 != null) {
            for (Channel channel : mCh3) {
                if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                    ivfav3.setBackground(mFavIcon);
                }
            }
        } else {
            ivfav3.setBackground(mNoFavIcon);
        }

        if (mCh4 != null) {
            for (Channel channel : mCh4) {
                if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                    ivfav4.setBackground(mFavIcon);
                }
            }
        } else {
            ivfav4.setBackground(mNoFavIcon);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (ChannelListActivity) getContext( );
    }

    @Override
    public void onStart() {
        super.onStart( );
        initFavName( );
        mSecondFragment = (SecondFragment) mFragmentManager.findFragmentByTag(
                ChannelUtil.SECOND_FRAGMENT_TAG);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mRootView.getLayoutParams( );
        lp.topMargin = mSecondFragment.mTopMargin;
        mRootView.setLayoutParams(lp);
        mRootView.getChildAt(0).requestFocus( );
    }

    private void initFavName() {

        List<ChannelListInfo> favChList = mChannelManager.queryFavChannelListInfo( );
        tvFavNameFisrt.setText(favChList.get(0).getListName( ));
        tvFavNameSecond.setText(favChList.get(1).getListName( ));
        tvFavNameTrid.setText(favChList.get(2).getListName( ));
        tvFavNameFour.setText(favChList.get(3).getListName( ));
    }

    public boolean isFirstFocus() {
        return llTridFirst == null ? false : llTridFirst.hasFocus( );
    }

    public boolean isFourFocus() {
        return llTridFour == null ? false : llTridFour.hasFocus( );
    }

    @Override
    public void onClick(View v) {

        switch (v.getId( )) {
            case R.id.ll_trid_first:
                mCh1 = mChannelManager.queryFavChannelList(mChListInfos.get(0));
                Log.v(TAG, "tv_fav_name_1:    " + tvFavNameFisrt.getText( ).toString( ));
                boolean isFav1 = false;
                if (mCh1 != null) {
                    for (Channel channel : mCh1) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            mChannelManager.removeToFavChannelList(tvFavNameFisrt.getText( ).
                                    toString( ), mCurEditChannel);
                            isFav1 = true;
                            ivfav1.setBackground(mNoFavIcon);
                        }
                    }
                    if (!isFav1) {
                        mChannelManager.addToFavChannelList(tvFavNameFisrt.getText( ).
                                toString( ), mCurEditChannel);
                        ivfav1.setBackground(mFavCurSelectIcon);
                    }
                }else{
                    mChannelManager.addToFavChannelList(tvFavNameFisrt.getText( ).
                            toString( ), mCurEditChannel);
                    ivfav1.setBackground(mFavCurSelectIcon);
                }

                break;
            case R.id.ll_trid_second:
                mCh2 = mChannelManager.queryFavChannelList(mChListInfos.get(1));
                Log.v(TAG, "tv_fav_name_2:    " + tvFavNameSecond.getText( ).toString( ));
                boolean isFav2 = false;
                if (mCh2 != null) {
                    for (Channel channel : mCh2) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            mChannelManager.removeToFavChannelList(tvFavNameSecond.getText( ).
                                    toString( ), mCurEditChannel);
                            isFav2 = true;
                            ivfav2.setBackground(mNoFavIcon);
                        }
                    }
                    if (!isFav2) {
                        mChannelManager.addToFavChannelList(tvFavNameSecond.getText( ).
                                toString( ), mCurEditChannel);
                        ivfav2.setBackground(mFavCurSelectIcon);
                    }
                }else{
                    mChannelManager.addToFavChannelList(tvFavNameSecond.getText( ).
                            toString( ), mCurEditChannel);
                    ivfav2.setBackground(mFavCurSelectIcon);
                }
                break;
            case R.id.ll_trid_trid:
                Log.v(TAG, "tv_fav_name_3:    " + tvFavNameTrid.getText( ).toString( ));
                mCh3 = mChannelManager.queryFavChannelList(mChListInfos.get(2));
                boolean isFav3 = false;
                if (mCh3 != null) {
                    for (Channel channel : mCh3) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            mChannelManager.removeToFavChannelList(tvFavNameTrid.getText( ).
                                    toString( ), mCurEditChannel);
                            isFav3 = true;
                            ivfav3.setBackground(mNoFavIcon);
                        }
                    }
                    if (!isFav3) {
                        mChannelManager.addToFavChannelList(tvFavNameTrid.getText( ).
                                toString( ), mCurEditChannel);
                        ivfav3.setBackground(mFavCurSelectIcon);
                    }
                }else{
                    mChannelManager.addToFavChannelList(tvFavNameTrid.getText( ).
                            toString( ), mCurEditChannel);
                    ivfav3.setBackground(mFavCurSelectIcon);
                }
                break;
            case R.id.ll_trid_four:
                Log.v(TAG, "tv_fav_name_4:    " + tvFavNameFour.getText( ).toString( ));
                mCh4 = mChannelManager.queryFavChannelList(mChListInfos.get(3));
                boolean isFav4 = false;
                if (mCh4 != null) {
                    for (Channel channel : mCh4) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            mChannelManager.removeToFavChannelList(tvFavNameFour.getText( ).
                                    toString( ), mCurEditChannel);
                            isFav4 = true;
                            ivfav4.setBackground(mNoFavIcon);
                        }
                    }
                    if (!isFav4) {
                        mChannelManager.addToFavChannelList(tvFavNameFour.getText( ).
                                toString( ), mCurEditChannel);
                        ivfav4.setBackground(mFavCurSelectIcon);
                    }
                }else{
                    mChannelManager.addToFavChannelList(tvFavNameFour.getText( ).
                            toString( ), mCurEditChannel);
                    ivfav4.setBackground(mFavCurSelectIcon);
                }
                break;
        }
        mChannelManager.saveChannel(mCurEditChannel);
        mChannelFragment.refresh( );

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        switch (v.getId( )) {
            case R.id.ll_trid_first:
                mCh1 = mChannelManager.queryFavChannelList(mChListInfos.get(0));
                if (mCh1 != null) {
                    for (Channel channel : mCh1) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            ivfav1.setBackground(hasFocus ? mFavCurSelectIcon : mFavIcon);
                            Log.d(TAG, "onFocusChange: "+hasFocus+"   1");
                        }
                    }
                }
                tvFavNameFisrt.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                        : mActivity.mChItemDefaultColor);

                break;
            case R.id.ll_trid_second:
                mCh2 = mChannelManager.queryFavChannelList(mChListInfos.get(1));
                if (mCh2 != null) {
                    for (Channel channel : mCh2) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            ivfav2.setBackground(hasFocus ? mFavCurSelectIcon : mFavIcon);
                            Log.d(TAG, "onFocusChange: "+hasFocus+"   2");
                        }
                    }
                }
                tvFavNameSecond.setTextColor(hasFocus ? mActivity.mChItemFocusColor :
                        mActivity.mChItemDefaultColor);

                break;
            case R.id.ll_trid_trid:
                mCh3 = mChannelManager.queryFavChannelList(mChListInfos.get(2));
                if (mCh3 != null) {
                    for (Channel channel : mCh3) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            ivfav3.setBackground(hasFocus ? mFavCurSelectIcon : mFavIcon);
                        }
                    }
                }
                tvFavNameTrid.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                        : mActivity.mChItemDefaultColor);

                break;
            case R.id.ll_trid_four:
                mCh4 = mChannelManager.queryFavChannelList(mChListInfos.get(3));
                if (mCh4 != null) {
                    for (Channel channel : mCh4) {
                        if (channel.getChannelNumber( ) == mCurEditChannel.getChannelNumber( )) {
                            ivfav4.setBackground(hasFocus ? mFavCurSelectIcon : mFavIcon);
                        }
                    }
                }
                tvFavNameFour.setTextColor(hasFocus ? mActivity.mChItemFocusColor
                        : mActivity.mChItemDefaultColor);
                break;
        }
    }
}
