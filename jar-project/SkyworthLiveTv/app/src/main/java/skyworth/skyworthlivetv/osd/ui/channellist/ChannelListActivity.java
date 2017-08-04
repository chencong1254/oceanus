package skyworth.skyworthlivetv.osd.ui.channellist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.platform.IPlatformChannelManager;

import java.util.ArrayList;
import java.util.List;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelListInfo;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_LIST_TYPE;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.platformsupport.PlatformSourceManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.ui.channellist.adapter.TopAdapter;
import skyworth.skyworthlivetv.osd.ui.channellist.fragment.ChannelFragment;
import skyworth.skyworthlivetv.osd.ui.channellist.fragment.SecondFragment;
import skyworth.skyworthlivetv.osd.ui.channellist.fragment.SortFragment;
import skyworth.skyworthlivetv.osd.ui.channellist.fragment.TridFragment;
@SuppressWarnings("ResourceType")
public class ChannelListActivity extends Activity implements View.OnClickListener,
        View.OnFocusChangeListener, ChannelFragment.OnKeyboradIsDismissListener {
    private static final String TAG = "ChannelListActivity";
    private static final int MSG_WHAT_SET_RIGHT_TEXT_COLOR = 1;
    private static final int MSG_WHAT_SET_LEFT_FAV_FOCUS = 2;
    RecyclerView rvTop;
    TextView btnSort;
    View vSortFucos;
    TopAdapter mTopAdapter;

    FragmentManager mFragmentManager;

    private SortFragment mSortFragment;

    private SecondFragment mSecondFragment;
    private TridFragment mTridFragment;
    private ChannelFragment mChannelFragment, mFav1Fragment, mFav2Fragment,
            mFav3Fragment, mFav4Fragment;

    private RecyclerView verticalRv;
    private int mChItemRightColor;
    private LinearLayout verticalRvChildAt;
    private Drawable mChItemSelector;

    public int mChItemFocusColor;
    public int mChItemDefaultColor;

    private Handler mHandler = new Handler( ) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_SET_RIGHT_TEXT_COLOR:
                    mSecondFragment.setRightTextColor( );
                    break;
                case MSG_WHAT_SET_LEFT_FAV_FOCUS:
                    mSecondFragment.setFavFocus( );
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private IPlatformChannelManager mChManager;
    private String[] mTopNames;
    public List<Channel> mChannelDatas;
    public List<Channel> mFav1Channels;
    public List<Channel> mFav2Channels;
    public List<Channel> mFav3Channels;
    public List<Channel> mFav4Channels;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chlist_channel_list_main);

        initData( );

        initFragment( );

        initView( );

        setListener( );

    }

    private int mCurTopPosition = -1;

    private void setListener() {

        btnSort.setOnClickListener(this);
        btnSort.setOnFocusChangeListener(this);
        rvTop.setOnFocusChangeListener(this);
        mChannelFragment.setOnKeyboradIsDismissListener(this);

        mTopAdapter.setOnRvItemSelectorListener(new TopAdapter.OnRvItemSelectorListener( ) {
            @Override
            public void setOnRvItemSelectorListener(int position) {
                mCurTopPosition = position;
                FragmentTransaction ft = mFragmentManager.beginTransaction( );
                switch (position) {
                    case 0:
                        MyAsyncask allChAsyncask = new MyAsyncask(0);
                        allChAsyncask.execute(0);
                        break;
                    case 1:
                        MyAsyncask fav1Asyncask = new MyAsyncask(1);
                        fav1Asyncask.execute(1);
                        break;
                    case 2:
                        MyAsyncask fav2Asyncask = new MyAsyncask(2);
                        fav2Asyncask.execute(2);
                        break;
                    case 3:
                        MyAsyncask fav3Asyncask = new MyAsyncask(3);
                        fav3Asyncask.execute(3);
                        break;
                    case 4:
                        MyAsyncask fav4Asyncask = new MyAsyncask(4);
                        fav4Asyncask.execute(1);
                        break;
                    default:
                        break;
                }
                ft.commit( );
            }

            @Override
            public void setOnRvItemLongClick(int mCurEditPosition, View v, String name) {
                EditText tv = (EditText) v;
                tv.setInputType(InputType.TYPE_CLASS_TEXT);
                tv.setFocusable(true);
                tv.requestFocus( );
                tv.selectAll( );
                tv.setHint(name);
                InputMethodManager imm = (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(tv, 0);
            }
        });

    }


    private void initView() {
        rvTop = (RecyclerView) findViewById(R.id.rv_top);
        btnSort = (TextView) findViewById(R.id.btn_sort);
        vSortFucos = findViewById(R.id.nv_sort_focus);

        List<String> topNameList = new ArrayList<>( );
        for (String mTopName : mTopNames) {
            topNameList.add(mTopName);
        }
        mTopAdapter = new TopAdapter(this, topNameList);
        LinearLayoutManager layoutManger = new LinearLayoutManager(this);
        layoutManger.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvTop.setLayoutManager(layoutManger);
        rvTop.setAdapter(mTopAdapter);


        MyAsyncask myAsyncask = new MyAsyncask(0);
        myAsyncask.execute(0);

    }


    private void initFragment() {
        mFragmentManager = getFragmentManager( );
        mSortFragment = new SortFragment( );
        mChannelFragment = new ChannelFragment( );
        mFav1Fragment = new ChannelFragment( );
        mFav2Fragment = new ChannelFragment( );
        mFav3Fragment = new ChannelFragment( );
        mFav4Fragment = new ChannelFragment( );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentTransaction ft = mFragmentManager.beginTransaction( );
        switch (event.getKeyCode( )) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                verticalRv = mChannelFragment.getVerticalRv( );
                if (verticalRv.hasFocus( )) {
                    verticalRvChildAt = (LinearLayout) verticalRv.getChildAt(mChannelFragment.getCurSelectedVisiblePos( ));
                    mChannelFragment.setBgRightViewVisible(View.VISIBLE);
                    mSecondFragment = new SecondFragment( );
                    ft.replace(R.id.second_fragment, mSecondFragment,
                            ChannelUtil.SECOND_FRAGMENT_TAG);
                    ft.commit( );
                }
                if (rvTop.hasFocus( ) && mCurTopPosition == (mTopNames.length - 1)) {
                    return true;
                }
                if (mSecondFragment != null && mSecondFragment.isVisible( ) &&
                        mSecondFragment.isFavFocus( )) {

                    mTridFragment = new TridFragment( );
                    ft.replace(R.id.trid_fragment, mTridFragment, ChannelUtil.TRID_FRAGMENT_TAG);
                    ft.commit( );
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_SET_RIGHT_TEXT_COLOR, 50);
                }

                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mTridFragment = (TridFragment) mFragmentManager.findFragmentByTag
                        (ChannelUtil.TRID_FRAGMENT_TAG);
                if (mTridFragment == null) {
                    mTridFragment = new TridFragment( );
                }
                if (mTridFragment.isVisible( )) {
                    Log.v(TAG, "detach  mTridFragment");
                    ft.detach(mTridFragment);
                    mHandler.sendEmptyMessageDelayed(MSG_WHAT_SET_LEFT_FAV_FOCUS, 0);
                } else if (mSecondFragment != null && mSecondFragment.isVisible( )) {
                    ChannelUtil.editChPosition = -1;
                    mChannelFragment.setBgRightViewVisible(View.INVISIBLE);
                    mChannelFragment.setRvlastSelectedItemFucos( );
                    ft.detach(mSecondFragment);
                    Log.v(TAG, "detach  mSecondFragment");

                } else {
                    Log.v(TAG, "detach  else");
                }

                ft.commit( );
                break;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode( )) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mChannelFragment.getCurSelectedVisiblePos( ) == 0) {

                    Log.d(TAG, "dispatchKeyEvent: UP :" + "visible position-->" +
                            mChannelFragment.getCurSelectedVisiblePos( ) + "   setTopItemFocus");

                    mTopAdapter.setTopItemFocus(true);
                    for (int i = 0; i < rvTop.getChildCount( ); i++) {
                        rvTop.getChildAt(i).setFocusable(true);
                    }
                }
                if ((mSecondFragment != null && mSecondFragment.isFavFocus( )) ||
                        (mTridFragment != null && mTridFragment.isFirstFocus( ))) {
                    Log.d(TAG, "dispatchKeyEvent: UP :" + "first item 0f second or trid fragment has focus");
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mChannelFragment.getCount( ) > 0) {
                    Log.d(TAG, "dispatchKeyEvent: DOWN :" + "   setTopItemNoFocus");
                    mTopAdapter.setTopItemFocus(false);
                    for (int i = 0; i < rvTop.getChildCount( ); i++) {
                        rvTop.getChildAt(i).setFocusable(false);
                    }

                }
                if ((mSecondFragment != null && mSecondFragment.isTunFocus( )) ||
                        (mTridFragment != null && mTridFragment.isFourFocus( ))) {
                    Log.d(TAG, "dispatchKeyEvent: DOWN :" + "last item 0f second or trid fragment has focus");
                    return true;
                }
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initData() {
        ChannelUtil.editChPosition = -1;
        mChManager = PlatformChannelManager.getInstance( );

        initTopName( );

        ChannelUtil.isChFirstEnter = true;
        mChItemRightColor = getResources( ).getColor(R.color.chlist_ch_item_right);
        mChItemSelector = getResources( ).getDrawable(R.drawable.chlist_channel_item_selector);
        mChItemDefaultColor = getResources( ).getColor(R.color.chlist_ch_text_default);
        mChItemFocusColor = getResources( ).getColor(R.color.chlist_ch_text_focus);
    }

    private void initTopName() {
        mTopNames = getResources( ).getStringArray(R.array.chlist_top_name_default);
        List<ChannelListInfo> favChList = mChManager.queryFavChannelListInfo( );
        Log.v(TAG, "favListSize:  " + favChList.size( ));
        // mChManager.renameFavList("DefaultFavList", "test");
        if (favChList.size( ) < 4) {
            Log.v(TAG, "init create 4 favlist");
            //mChManager.renameFavList(favChList.get(0).getListName( ), mTopNames[1]);
            mChManager.createFavList(mTopNames[2], false);
            mChManager.createFavList(mTopNames[3], false);
            mChManager.createFavList(mTopNames[4], false);
        } else {
            Log.v(TAG, "had 4 favlist,get top name from favlist");
            for (int i = 0; i < favChList.size( ); i++) {
                mTopNames[i + 1] = favChList.get(i).getListName( );
                Log.v(TAG, "favListSize:  " + favChList.get(i).getListName( ));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId( )) {
            case R.id.btn_sort:
                FragmentTransaction ft = mFragmentManager.beginTransaction( );
                ft.setCustomAnimations(R.anim.fragment_slide_left_enter,
                        R.anim.fragment_slide_right_exit);
                ft.replace(R.id.content_fragment, mSortFragment, ChannelUtil.SORT_FRAGMENT_TAG);
                ft.commit( );
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId( )) {
            case R.id.btn_sort:
                if (hasFocus) {
                    vSortFucos.setVisibility(View.VISIBLE);
                } else {
                    vSortFucos.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.rv_top:

                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop( );
        mTopAdapter.setOnRvItemSelectorListener(null);
    }

    @Override
    public void beforeDismiss() {
        btnSort.setFocusable(false);
        mTopAdapter.setTopItemFocus(false);
    }

    @Override
    public void afterDismiss() {
        btnSort.setFocusable(true);
        mTopAdapter.setTopItemFocus(true);
    }

    public class MyAsyncask extends AsyncTask<Integer, Integer, SparseArray<List<Channel>>> {

        private EN_INPUT_SOURCE_TYPE mType;
        private List<ChannelListInfo> mFavListInfo;
        private int dataTypeId;

        public MyAsyncask(int dataTypeId) {
            this.dataTypeId = dataTypeId;
        }

        @Override
        protected void onPreExecute() {
            mType = PlatformSourceManager.getInstance( ).
                    GetCurrentSource( ).getType( );
        }

        @Override
        protected SparseArray<List<Channel>> doInBackground(Integer... params) {
    
            mFavListInfo = mChManager.queryFavChannelListInfo( );
            SparseArray<List<Channel>> channels = new SparseArray<>( );
            switch (dataTypeId) {
                case 0://all channels
                    switch (mType) {
                        case E_INPUT_SOURCE_ATV:
                            List<Channel> atvChannelList = mChManager.GetChannelList(
                                    EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_ATV, 0, 0);
                            channels.put(0, atvChannelList);
                            break;
                        case E_INPUT_SOURCE_DTV_DVB_T:
                            List<Channel> dvbtChannelList = mChManager.GetChannelList(
                                    EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBT, 0, 0);
                            channels.put(0, dvbtChannelList);
                            break;
                        case E_INPUT_SOURCE_DTV_DVB_C:
                            List<Channel> dvbcChannelList = mChManager.GetChannelList(
                                    EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBC, 0, 0);
                            channels.put(0, dvbcChannelList);
                            break;
                        case E_INPUT_SOURCE_DTV_DVB_S:
                            List<Channel> dvbsChannelList = mChManager.GetChannelList(
                                    EN_CHANNEL_LIST_TYPE.E_CHANNEL_LIST_TYPE_DVBS, 0, 0);
                            channels.put(0, dvbsChannelList);
                            break;
                        case E_INPUT_SOURCE_APPLICATION:
                             List<Channel> appChannelList = mChManager.GetChannelList(
                                    PlatformSourceManager.getInstance().GetCurrentSource( ), 0, 0);
                            channels.put(0, appChannelList);
                            break;
                    }
                    break;
                case 1:
                    List<Channel> channels1 = mChManager.queryFavChannelList(mFavListInfo.get(0));
                    channels.put(1, channels1);
                    break;
                case 2:
                    List<Channel> channels2 = mChManager.queryFavChannelList(mFavListInfo.get(1));
                    channels.put(2, channels2);
                    break;
                case 3:
                    List<Channel> channels3 = mChManager.queryFavChannelList(mFavListInfo.get(2));
                    channels.put(3, channels3);
                    break;
                case 4:
                    List<Channel> channels4 = mChManager.queryFavChannelList(mFavListInfo.get(3));
                    channels.put(4, channels4);
                    break;
            }
            return channels;
        }


        @Override
        protected void onPostExecute(SparseArray<List<Channel>> listChannels) {//Runs on the UI thread after
            if (listChannels == null) {
                Log.d(TAG, "onPostExecute: all channels is null");
                return;
            }
            Log.d(TAG, "onPostExecute: channel size is " + listChannels.size( ));
            FragmentTransaction ft = mFragmentManager.beginTransaction( );
            switch (dataTypeId) {
                case 0://all channels
                    mChannelDatas = listChannels.get(0);
                    mChannelFragment.setData(mChannelDatas);
                    ft.replace(R.id.content_fragment, mChannelFragment, ChannelUtil.CH_FRAGMENT_TAG);
                    break;
                case 1://fav1 channels
                    mFav1Channels = listChannels.get(1);
                    mFav1Fragment.setData(mFav1Channels);
                    ft.replace(R.id.content_fragment, mFav1Fragment,
                            ChannelUtil.FAV1_FRAGMENT_TAG);
                    break;
                case 2://fav2 channels
                    mFav2Channels = listChannels.get(2);
                    mFav2Fragment.setData(mFav2Channels);
                    ft.replace(R.id.content_fragment, mFav2Fragment,
                            ChannelUtil.FAV2_FRAGMENT_TAG);
                    break;
                case 3://fav3 channels
                    mFav3Channels = listChannels.get(3);
                    mFav3Fragment.setData(mFav3Channels);
                    ft.replace(R.id.content_fragment, mFav3Fragment,
                            ChannelUtil.FAV3_FRAGMENT_TAG);
                    break;
                case 4://fav4 channels
                    mFav4Channels = listChannels.get(4);
                    mFav4Fragment.setData(mFav4Channels);
                    ft.replace(R.id.content_fragment, mFav4Fragment,
                            ChannelUtil.FAV4_FRAGMENT_TAG);
                    break;
            }
            ft.commit( );
        }


    }
}
