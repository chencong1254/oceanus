package skyworth.skyworthlivetv.osd.ui.menu.channel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Oceanus.Tv.Service.ChannelManager.Channel;
import Oceanus.Tv.Service.ChannelManager.ChannelManagerDefinitions.EN_CHANNEL_SERVICE_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions.ATV;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skyworth.platformsupport.PlatformChannelManager;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.common.CommonConst;
import skyworth.skyworthlivetv.osd.common.KeyboardUtil;
import skyworth.skyworthlivetv.osd.common.OptPreferences;

import static skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG;

/**
 * Created by yangjianjun on 2017/5/2.
 */

public class ScanActivity extends Activity implements View.OnKeyListener {
    @BindView(R.id.headerview)
    LinearLayout mHeaderview;
    @BindView(R.id.tunermode)
    LinearLayout mTunermode;
    @BindView(R.id.tuningtype)
    LinearLayout mTuningtype;
    @BindView(R.id.frequency)
    LinearLayout mFrequency;
    @BindView(R.id.modulation)
    LinearLayout mModulation;
    @BindView(R.id.symbol)
    LinearLayout mSymbol;
    @BindView(R.id.scanmode)
    LinearLayout mScanmode;
    @BindView(R.id.scanbutton)
    LinearLayout mScanbutton;
    @BindView(R.id.tunermodename)
    TextView mTunermodename;
    @BindView(R.id.tuningtypename)
    TextView mTuningtypename;
    @BindView(R.id.frequencystring)
    TextView mFrequencystring;
    @BindView(R.id.modulationstring)
    TextView mModulationstring;
    @BindView(R.id.symbolstring)
    TextView mSymbolstring;
    @BindView(R.id.scanmodestring)
    TextView mScanmodestring;
    @BindArray(R.array.tuner_mode)
    String[] mTunerModeArray;
    @BindArray(R.array.tuning_type)
    String[] mTuningTypeArray;
    @BindArray(R.array.modulation_type)
    String[] mModulationTypeArray;
    @BindArray(R.array.symbol_type)
    String[] mSymbolTypeArray;
    @BindArray(R.array.scan_mode)
    String[] mScanModeArray;
    @BindArray(R.array.band_width)
    String[] mBandwidthArray;
    @BindArray(R.array.sound_system)
    String[] mSoundsystemArray;
    @BindArray(R.array.color_system)
    String[] mColorsystemArray;
    @BindView(R.id.networkidstring)
    TextView mNetworkidstring;
    @BindView(R.id.networkid)
    LinearLayout mNetworkid;
    @BindView(R.id.freqtxt)
    EditText mFrequencyedittext;
    @BindView(R.id.symboltxt)
    EditText mSymboledittxt;
    @BindView(R.id.networkidtxt)
    EditText mNetworkidedittxt;
    @BindView(R.id.channelnum)
    TextView mChannelnum;
    @BindView(R.id.channel)
    LinearLayout mChannel;
    @BindView(R.id.storagetostring)
    TextView mStoragetostring;
    @BindView(R.id.storagetonum)
    EditText mStoragetonum;
    @BindView(R.id.storageto)
    LinearLayout mStorageto;
    @BindView(R.id.bandwidthnum)
    TextView mBandwidthnum;
    @BindView(R.id.bandwidth)
    LinearLayout mBandwidth;
    @BindView(R.id.soundsystemname)
    TextView mSoundsystemname;
    @BindView(R.id.soundsystem)
    LinearLayout mSoundsystem;
    @BindView(R.id.colorsystemname)
    TextView mColorsystemname;
    @BindView(R.id.colorsystem)
    LinearLayout mColorsystem;
    @BindView(R.id.downothersview)
    RelativeLayout mDownothersview;
    @BindView(R.id.scanbuttontxt)
    TextView mScanbuttontxt;
    @BindView(R.id.symbolleftview)
    TextView mSymbolleftview;
    @BindView(R.id.symbolrightview)
    TextView mSymbolrightview;
    @BindView(R.id.netleftview)
    TextView mNetleftview;
    @BindView(R.id.netrightview)
    TextView mNetrightview;

    private OptPreferences mPreference;
    private int mCurTunerModeIndex;
    private InputMethodManager mMethodManager;
    private KeyboardUtil mKeyboard;
    private List<FreqPoint> mDVBTFreqPoints;
    private ScanManagerService mScanManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuningsetting);
        ButterKnife.bind(this);
        mPreference = OptPreferences.getInstance();
        mMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mKeyboard = new KeyboardUtil(this,this,null);
        mDVBTFreqPoints = ChannelScanManager.getInstance().getScanTable(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initValues();
        initListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initValues(){
        mTunermodename.setText(mTunerModeArray[mPreference.getInt(CommonConst.p_tunermode, 0)]);
        mTuningtypename.setText(mTuningTypeArray[mPreference.getInt(CommonConst.p_tuningtype, 0)]);
        mModulationstring.setText(mModulationTypeArray[mPreference.getInt(CommonConst.p_modulation, 0)]);
        mSymbolstring.setText(mSymbolTypeArray[mPreference.getInt(CommonConst.p_symbol, 0)]);
        mScanmodestring.setText(mScanModeArray[mPreference.getInt(CommonConst.p_scanmode, 0)]);
        mNetworkidstring.setText(mSymbolTypeArray[mPreference.getInt(CommonConst.p_networkid, 0)]);
        mSoundsystemname.setText(mSoundsystemArray[getCurrentChannelSoundSystem()]);
        mColorsystemname.setText(mColorsystemArray[getCurrentChannelColorSystem()]);
        mCurTunerModeIndex = mPreference.getInt(CommonConst.p_tunermode, 0);
        Log.i("yangjianjun", "p_channel:.............");
        if(mDVBTFreqPoints != null && mDVBTFreqPoints.size() > 0){
            Log.i("yangjianjun", "p_channel:"+mPreference.getInt(CommonConst.p_channel, 0));
            Log.i("yangjianjun", "p_channel:"+mDVBTFreqPoints.get(mPreference.getInt(CommonConst.p_channel, 0)).getName());

            mChannelnum.setText(mDVBTFreqPoints.get(mPreference.getInt(CommonConst.p_channel, 0)).getName());
            if(mCurTunerModeIndex == 1) {//dvbt
                mFrequencystring.setText(""+mDVBTFreqPoints.get(mPreference.getInt(CommonConst.p_channel, 0)).getFreq());
            }
        }
        refreshDown4View(mCurTunerModeIndex, mPreference.getInt(CommonConst.p_tuningtype, 0), false);
    }

    private void initListener(){
        mTunermode.setOnKeyListener(this);
        mTuningtype.setOnKeyListener(this);
        mModulation.setOnKeyListener(this);
        mSymbol.setOnKeyListener(this);
        mScanmode.setOnKeyListener(this);
        mNetworkid.setOnKeyListener(this);
        mFrequencyedittext.setOnKeyListener(this);
        mSymboledittxt.setOnKeyListener(this);
        mNetworkidedittxt.setOnKeyListener(this);
        mChannel.setOnKeyListener(this);
        mBandwidth.setOnKeyListener(this);
        mSoundsystem.setOnKeyListener(this);
        mColorsystem.setOnKeyListener(this);
        mStoragetonum.setOnKeyListener(this);
        mScanbutton.setOnKeyListener(this);
        mFrequency.setOnKeyListener(this);
        mStorageto.setOnKeyListener(this);
    }

    private void registerEditText(){
        mKeyboard.registerEditText(mFrequencyedittext);
        mKeyboard.registerEditText(mSymboledittxt);
        mKeyboard.registerEditText(mNetworkidedittxt);
        mKeyboard.registerEditText(mSymboledittxt);
    }

    private int getChannelMapFirstKey(Map<Integer,FreqPoint> fMap){
        if(fMap == null || fMap.size() == 0){
            return 1;
        }
        Iterator<Integer> it = fMap.keySet().iterator();
        if(it.hasNext()){
            return it.next();
        }else{
            return 1;
        }
    }

    @OnClick({R.id.scanbutton, R.id.frequency, R.id.symbol, R.id.networkid, R.id.storageto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.scanbutton:
                //搜台参数的合理性需要在此做出判断
                //走搜台流程,搜台相关的参数暂时就用基本类型数据来传
                if(mScanbuttontxt.getText().toString().equals("Scan")){
                    if (mPreference.getInt(CommonConst.p_tuningtype, 0) != 0) {//mannual
                        int freq = (int) mPreference.getFloat(CommonConst.p_frequency, 0.0f);
                        if(freq > 1E+9){
                            Toast.makeText(this, "Frequency is too large !!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        switch (mCurTunerModeIndex) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3://atv搜台不需要展示结果页面,在此直接返回
                                EN_ATV_SCAN_MODE atv_scan_mode = EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP;
                                mScanManager = ScanManagerService.getmScanInstance();
                                mScanManager.initManager(new Handler());
                                mScanManager.startAtvSearch(atv_scan_mode,freq);
                                mScanbuttontxt.setText("Save");
                                return;
                        }
                    }
                    Intent intent = new Intent(this, ScanProgressActivity.class);
                    startActivity(intent);
                    this.finish();
                }else{//Save
                    //执行保存操作
                    //mScanbuttontxt.setText("Scan");
                }
                break;
            case R.id.frequency:
                mFrequencyedittext.setVisibility(View.VISIBLE);
                mFrequencystring.setVisibility(View.GONE);
                mFrequency.setSelected(true);
                mFrequencyedittext.requestFocus();
                mFrequencyedittext.setSelection(mFrequencyedittext.getText().length());
                mMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.symbol:
                if (!mSymbolstring.getText().toString().equals(mSymbolTypeArray[0])) {
                    mSymbolleftview.setVisibility(View.GONE);
                    mSymbolrightview.setVisibility(View.GONE);
                    mSymbolstring.setVisibility(View.GONE);
                    mSymboledittxt.setVisibility(View.VISIBLE);
                    mSymbol.setSelected(true);
                    mSymboledittxt.requestFocus();
                    mSymboledittxt.setSelection(mSymboledittxt.getText().length());
                    mMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.networkid:
                if (!mNetworkidstring.getText().toString().equals(mSymbolTypeArray[0])) {
                    mNetleftview.setVisibility(View.GONE);
                    mNetrightview.setVisibility(View.GONE);
                    mNetworkidstring.setVisibility(View.GONE);
                    mNetworkidedittxt.setVisibility(View.VISIBLE);
                    mNetworkid.setSelected(true);
                    mNetworkidedittxt.requestFocus();
                    mNetworkidedittxt.setSelection(mNetworkidedittxt.getText().length());
                    mMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
            case R.id.storageto:
                mStoragetonum.setVisibility(View.VISIBLE);
                mStoragetostring.setVisibility(View.GONE);
                mStorageto.setSelected(true);
                mStoragetonum.requestFocus();
                mStoragetonum.setSelection(mStoragetonum.getText().length());
                mMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                break;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(mKeyboard.getKeyboardView().getVisibility() == View.VISIBLE){
            return false;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            int index;
            switch (v.getId()) {
                case R.id.tunermode:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_tunermode, 0) == 0) {
                            index = mTunerModeArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_tunermode, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_tunermode, 0) == mTunerModeArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_tunermode, 0) + 1;
                        }
                    }
                    mCurTunerModeIndex = index;
                    mTunermodename.setText(mTunerModeArray[index]);
                    mPreference.setInt(CommonConst.p_tunermode, index);
                    if (!refreshTuningTypeView()) {
                        refreshDown4View(mCurTunerModeIndex, mPreference.getInt(CommonConst.p_tuningtype, 0),true);
                    }
                    break;
                case R.id.tuningtype:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_tuningtype, 0) <= 0) {
                            index = mTuningTypeArray.length - 2;
                            if (mCurTunerModeIndex == 0) {
                                index++;//DVB-C比其它制式多了一种NetworkID的模式
                            }
                        } else {
                            index = mPreference.getInt(CommonConst.p_tuningtype, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_tuningtype, 0) >= mTuningTypeArray.length - 1) {
                            index = 0;
                        } else {
                            if (mCurTunerModeIndex != 0 && mPreference.getInt(CommonConst.p_tuningtype, 0) >= mTuningTypeArray.length - 2) {
                                index = 0;
                            } else {
                                index = mPreference.getInt(CommonConst.p_tuningtype, 0) + 1;
                            }
                        }
                    }
                    mTuningtypename.setText(mTuningTypeArray[index]);
                    mPreference.setInt(CommonConst.p_tuningtype, index);
                    refreshDown4View(mCurTunerModeIndex, index, false);
                    break;
                case R.id.modulation:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_modulation, 0) == 0) {
                            index = mModulationTypeArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_modulation, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_modulation, 0) == mModulationTypeArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_modulation, 0) + 1;
                        }
                    }
                    mModulationstring.setText(mModulationTypeArray[index]);
                    mPreference.setInt(CommonConst.p_modulation, index);
                    break;
                case R.id.symbol:
                    if (mSymbolstring.getVisibility() != View.VISIBLE) {
                        mSymbolstring.setVisibility(View.VISIBLE);
                        mSymboledittxt.setVisibility(View.GONE);
                        //break;
                    }
                    mSymbolleftview.setVisibility(View.VISIBLE);
                    mSymbolrightview.setVisibility(View.VISIBLE);
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_symbol, 0) == 0) {
                            index = mSymbolTypeArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_symbol, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_symbol, 0) == mSymbolTypeArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_symbol, 0) + 1;
                        }
                    }
                    mSymbolstring.setText(mSymbolTypeArray[index]);
                    mPreference.setInt(CommonConst.p_symbol, index);
                    break;
                case R.id.scanmode:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_scanmode, 0) == 0) {
                            index = mScanModeArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_scanmode, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_scanmode, 0) == mScanModeArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_scanmode, 0) + 1;
                        }
                    }
                    mScanmodestring.setText(mScanModeArray[index]);
                    mPreference.setInt(CommonConst.p_scanmode, index);
                    break;
                case R.id.networkid:
                    if (mNetworkidstring.getVisibility() != View.VISIBLE) {
                        mNetworkidstring.setVisibility(View.VISIBLE);
                        mNetworkidedittxt.setVisibility(View.GONE);
                        //break;
                    }
                    mNetleftview.setVisibility(View.VISIBLE);
                    mNetrightview.setVisibility(View.VISIBLE);
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_networkid, 0) == 0) {
                            index = mSymbolTypeArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_networkid, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_networkid, 0) == mSymbolTypeArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_networkid, 0) + 1;
                        }
                    }
                    mNetworkidstring.setText(mSymbolTypeArray[index]);
                    mPreference.setInt(CommonConst.p_networkid, index);
                    break;
                case R.id.channel:
                    index = mPreference.getInt(CommonConst.p_channel, 0);
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if(index > 0){
                            index--;
                        }
                    } else {
                        if(index < mDVBTFreqPoints.size()-2){
                            index++;
                        }
                    }
                    mChannelnum.setText(mDVBTFreqPoints.get(index).getName());
                    mFrequencystring.setText(""+mDVBTFreqPoints.get(index).getFreq());
                    mPreference.setInt(CommonConst.p_channel, index);
                    mPreference.setFloat(CommonConst.p_frequency, mDVBTFreqPoints.get(index).getFreq());
                    break;
                case R.id.bandwidth:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_bandwidth, 0) == 0) {
                            index = mBandwidthArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_bandwidth, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_bandwidth, 0) == mBandwidthArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_bandwidth, 0) + 1;
                        }
                    }
                    mBandwidthnum.setText(mBandwidthArray[index]);
                    mPreference.setInt(CommonConst.p_bandwidth, index);
                    break;
                case R.id.soundsystem:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_soundsystem, 0) == 0) {
                            index = mSoundsystemArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_soundsystem, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_soundsystem, 0) == mSoundsystemArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_soundsystem, 0) + 1;
                        }
                    }
                    TvCommonManager.getInstance().changeAtvSoundSystem(ATV.EN_SOUND_SYSTEM.values()[index]);
                    mSoundsystemname.setText(mSoundsystemArray[index]);
                    mPreference.setInt(CommonConst.p_soundsystem, index);
                    break;
                case R.id.colorsystem:
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        if (mPreference.getInt(CommonConst.p_colorsystem, 0) == 0) {
                            index = mColorsystemArray.length - 1;
                        } else {
                            index = mPreference.getInt(CommonConst.p_colorsystem, 1) - 1;
                        }
                    } else {
                        if (mPreference.getInt(CommonConst.p_colorsystem, 0) == mColorsystemArray.length - 1) {
                            index = 0;
                        } else {
                            index = mPreference.getInt(CommonConst.p_colorsystem, 0) + 1;
                        }
                    }
                    TvCommonManager.getInstance().changeAtvColoreSystem(ATV.EN_COLOR_SYSTEM.values()[index]);
                    mColorsystemname.setText(mColorsystemArray[index]);
                    mPreference.setInt(CommonConst.p_colorsystem, index);
                    break;
                case R.id.freqtxt://不能删
                case R.id.storagetonum:
                case R.id.scanbutton:
                    break;
                case R.id.symboltxt:
                    mSymbol.requestFocus();
                    mSymbol.setSelected(false);
                    mSymbolleftview.setVisibility(View.VISIBLE);
                    mSymbolrightview.setVisibility(View.VISIBLE);
                    mSymbol.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    break;
                case R.id.networkidtxt:
                    mNetleftview.setVisibility(View.VISIBLE);
                    mNetrightview.setVisibility(View.VISIBLE);
                    mNetworkid.requestFocus();
                    mNetworkid.setSelected(false);
                    mNetworkid.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyCode));
                    break;
            }
            return true;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN &&
                (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN)) {
            String fnum;
            switch (v.getId()) {
                case R.id.freqtxt:
                    mFrequency.requestFocus();
                    mFrequency.setSelected(false);
                    mFrequencyedittext.setVisibility(View.GONE);
                    mFrequencystring.setVisibility(View.VISIBLE);
                    if(TextUtils.isEmpty(mFrequencyedittext.getText().toString())){
                        if(mCurTunerModeIndex == 1){//dvbt
                            int ff = mDVBTFreqPoints.get(mPreference.getInt(CommonConst.p_channel, 0)).getFreq();
                            mFrequencystring.setText("" + ff);
                            mPreference.setFloat(CommonConst.p_frequency, ff);
                        }else{
                            mFrequencystring.setText("Enter");
                            mPreference.setFloat(CommonConst.p_frequency, 0.0F);
                        }
                    }else{
                        mFrequencystring.setText(mFrequencyedittext.getText().toString());
                        fnum = mFrequencyedittext.getText().toString();
                        mPreference.setFloat(CommonConst.p_frequency, Float.parseFloat(fnum));
                    }
                    if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        mFrequencyedittext.setSelection(0);
                    }
                    break;
                case R.id.symboltxt:
                    mSymbol.requestFocus();
                    mSymbol.setSelected(false);
                    fnum = mSymboledittxt.getText().toString().equals("") ? "" + 0.0 : mSymboledittxt.getText().toString();
                    mPreference.setFloat(CommonConst.p_symbolnum, Float.parseFloat(fnum));
                    mSymboledittxt.setVisibility(View.GONE);
                    mSymbolstring.setVisibility(View.VISIBLE);
                    //mSymbolrightview.setVisibility(View.INVISIBLE);
                    //mSymbolleftview.setVisibility(View.INVISIBLE);
                    if(TextUtils.isEmpty(mSymboledittxt.getText().toString())){
                        mSymbolrightview.setVisibility(View.VISIBLE);
                        mSymbolleftview.setVisibility(View.VISIBLE);
                        mSymbolstring.setText(mSymbolTypeArray[1]);
                        mPreference.setInt(CommonConst.p_symbol, 1);
                    }else{
                        mSymbolrightview.setVisibility(View.INVISIBLE);
                        mSymbolleftview.setVisibility(View.INVISIBLE);
                        mSymbolstring.setText(mSymboledittxt.getText().toString());
                    }
                    if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        mSymboledittxt.setSelection(0);
                    }
                    break;
                case R.id.networkidtxt:
                    mNetworkid.requestFocus();
                    mNetworkid.setSelected(false);
                    fnum = mNetworkidedittxt.getText().toString().equals("") ? "" + 0.0 : mNetworkidedittxt.getText().toString();
                    mPreference.setFloat(CommonConst.p_networkidnum, Float.parseFloat(fnum));
                    mNetworkidedittxt.setVisibility(View.GONE);
                    mNetworkidstring.setVisibility(View.VISIBLE);
                    //mNetleftview.setVisibility(View.INVISIBLE);
                    //mNetrightview.setVisibility(View.INVISIBLE);
                    if(TextUtils.isEmpty(mNetworkidedittxt.getText().toString())){
                        mNetleftview.setVisibility(View.VISIBLE);
                        mNetrightview.setVisibility(View.VISIBLE);
                        mNetworkidstring.setText(mSymbolTypeArray[1]);
                        mPreference.setInt(CommonConst.p_networkid, 1);
                    }else{
                        mNetleftview.setVisibility(View.INVISIBLE);
                        mNetrightview.setVisibility(View.INVISIBLE);
                        mNetworkidstring.setText(mNetworkidedittxt.getText().toString());
                    }
                    if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        mNetworkidedittxt.setSelection(0);
                    }
                    break;
                case R.id.storagetonum:
                    mStorageto.requestFocus();
                    mStorageto.setSelected(false);
                    fnum = mStoragetonum.getText().toString().equals("") ? "" + 0.0 : mStoragetonum.getText().toString();
                    mPreference.setFloat(CommonConst.p_storage, Float.parseFloat(fnum));
                    mStoragetonum.setVisibility(View.GONE);
                    mStoragetostring.setVisibility(View.VISIBLE);
                    if(TextUtils.isEmpty(mStoragetonum.getText().toString())){
                        mStoragetostring.setText("Enter");
                    }else{
                        mStoragetostring.setText(mStoragetonum.getText().toString());
                    }
                    if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        mStoragetonum.setSelection(0);
                    }
                    break;
            }
        }
        return false;
    }

    private boolean refreshTuningTypeView() {
        if (!mTunermodename.getText().toString().equals("DVB-C")) {
            if (mTuningtypename.getText().toString().equals("Network")) {
                mTuningtypename.setText(mTuningTypeArray[0]);
                mPreference.setInt(CommonConst.p_tuningtype, 0);
                showOthersViews(false);
                resetFreqency();
                return true;
            }
        }
        return false;
    }

    private void refreshDown4View(int modeindex, int typeindex, boolean reset) {
        if (typeindex == 0) {//自动搜索
            showOthersViews(false);
            return;
        }
        if(reset){
            resetFreqency();
        }
        //手动搜索
        switch (modeindex) {
            case 0://-C
                if (typeindex == 1) {
                    showDvbcViews(true);
                } else {//networkid
                    showDvbcViews(false);
                }
                break;
            case 1://-T
                showDvbtViews(true);
                break;
            case 2://-S
                showDvbsViews(true);
                showOthersViews(false);
                break;
            case 3://ATV
                showAtvViews(true);
                break;
        }

    }

    private void showOthersViews(boolean show) {
        mDownothersview.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showAtvViews(boolean show) {
        showOthersViews(true);
        mSoundsystem.setVisibility(View.VISIBLE);
        mColorsystem.setVisibility(View.VISIBLE);
        mFrequency.setVisibility(View.VISIBLE);
        mStorageto.setVisibility(View.VISIBLE);
        mModulation.setVisibility(View.GONE);
        mSymbol.setVisibility(View.GONE);
        mScanmode.setVisibility(View.GONE);
        mNetworkid.setVisibility(View.GONE);
        mChannel.setVisibility(View.GONE);
        mBandwidth.setVisibility(View.GONE);
        //resetFreqency();
    }

    private void showDvbcViews(boolean show) {
        showOthersViews(true);
        mSoundsystem.setVisibility(View.GONE);
        mColorsystem.setVisibility(View.GONE);
        mFrequency.setVisibility(View.VISIBLE);
        mStorageto.setVisibility(View.GONE);
        mModulation.setVisibility(View.VISIBLE);
        mSymbol.setVisibility(View.VISIBLE);
        mChannel.setVisibility(View.GONE);
        mBandwidth.setVisibility(View.GONE);
        if (show) {
            mScanmode.setVisibility(View.VISIBLE);
            mNetworkid.setVisibility(View.GONE);
        } else {
            mScanmode.setVisibility(View.GONE);
            mNetworkid.setVisibility(View.VISIBLE);
        }
        //resetFreqency();
    }

    private void showDvbtViews(boolean show) {
        showOthersViews(true);
        mSoundsystem.setVisibility(View.GONE);
        mColorsystem.setVisibility(View.GONE);
        mFrequency.setVisibility(View.VISIBLE);
        mStorageto.setVisibility(View.GONE);
        mModulation.setVisibility(View.GONE);
        mSymbol.setVisibility(View.GONE);
        mScanmode.setVisibility(View.VISIBLE);
        mNetworkid.setVisibility(View.GONE);
        mChannel.setVisibility(View.VISIBLE);
        mBandwidth.setVisibility(View.VISIBLE);
        if(mDVBTFreqPoints != null && mDVBTFreqPoints.size() > 0){
            mFrequencystring.setText(""+mDVBTFreqPoints.get(mPreference.getInt(CommonConst.p_channel, 0)).getFreq());
        }
    }

    private void showDvbsViews(boolean show) {

    }

    private void resetFreqency(){
        mFrequencystring.setVisibility(View.VISIBLE);
        mFrequencyedittext.setVisibility(View.GONE);
        mFrequencyedittext.setText("");
        mPreference.setFloat(CommonConst.p_frequency, 0.0F);
        mFrequencystring.setText("Enter");
    }

    @Override
    public void onBackPressed() {
        if(mKeyboard.getKeyboardView().getVisibility() == View.VISIBLE){
            mKeyboard.hideKeyboard();
        }else{
            if(mScanManager != null){
                mScanManager.stopAtvSearch();
                mScanManager.unInitManager();
                mScanManager = null;
            }
            super.onBackPressed();
        }
    }

    private int getCurrentChannelSoundSystem()
    {
        int soundsystem = 0;
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null || currentChannel.getType() != EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV){
            return 0;
        }

        ATV.EN_SOUND_SYSTEM sound = TvCommonManager.getInstance().getAtvSoundSystem();
        Log.d(DEBUG_TAG,"sound: " + sound.toString());
        switch (sound)
        {
            case E_SOUND_SYSTEM_BG:
                soundsystem = 4;
                break;
            case E_SOUND_SYSTEM_DK:
                soundsystem = 2;
                break;
            case E_SOUND_SYSTEM_I:
                soundsystem = 1;
                break;
            case E_SOUND_SYSTEM_L:
                soundsystem = 0;
                break;
            case E_SOUND_SYSTEM_M:
                soundsystem = 3;
                break;
            case E_SOUND_SYSTEM_N:
                soundsystem = 0;
                break;
            default:
                soundsystem = 0;
                break;
        }
        return soundsystem;
    }

    private int getCurrentChannelColorSystem(){
        int colorsystem = 0;
        Channel currentChannel = PlatformChannelManager.getInstance().GetCurrentChannel();
        if(currentChannel == null || currentChannel.getType() != EN_CHANNEL_SERVICE_TYPE.E_SERVICE_ATV){
            return 0;
        }

        ATV.EN_COLOR_SYSTEM system = TvCommonManager.getInstance().getAtvColorSystem();
        Log.d(DEBUG_TAG,"SYSTEM: " + system.toString());
        switch (system)
        {
            case E_COLOR_STANDARD_NTSC:
            case E_COLOR_STANDARD_NTSC_443:
                colorsystem = 2;
                break;
            case E_COLOR_STANDARD_PAL:
            case E_COLOR_STANDARD_PAL_60:
            case E_COLOR_STANDARD_PAL_M:
            case E_COLOR_STANDARD_PAL_N:
                colorsystem = 1;
                break;
            case E_COLOR_STANDARD_SECAM:
            case E_COLOR_STANDARD_SECAM_L:
                colorsystem = 3;
                break;
            default:
                colorsystem = 0;
                break;
        }
        return colorsystem;
    }
}
