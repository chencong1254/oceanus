package skyworth.skyworthlivetv.osd.ui.menu.channel;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import Oceanus.Tv.Service.ChannelScanManager.AtvScanResult;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManager;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.AtvScanDefinitions.EN_ATV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_MODULATION_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_SCAN_MODE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DTV_TUNING_BAND_WIDTH;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.DtvScanDefinitions.EN_DVB_SCAN_SCRAMBLE_TYPE;
import Oceanus.Tv.Service.ChannelScanManager.ChannelScanManagerDefinitions.FreqPoint;
import Oceanus.Tv.Service.ChannelScanManager.DtvScanResult;
import Oceanus.Tv.Service.ChannelScanManager.DtvSearchRequirement;
import Oceanus.Tv.Service.SourceManager.SourceManagerDefinitions.EN_INPUT_SOURCE_TYPE;
import Oceanus.Tv.Service.TvCommonManager.TvCommonManager;
import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.osd.common.CommonConst;
import skyworth.skyworthlivetv.osd.common.OptPreferences;
import skyworth.skyworthlivetv.osd.ui.menu.channel.Listener.DlgScanCancelListener;

/**
 * Created by yangjianjun on 2017/5/4.
 */

public class ScanProgressActivity extends Activity implements DlgScanCancelListener {
    private static final String LOG_TAG = ScanProgressActivity.class.getSimpleName();
    @BindView(R.id.freqtxt)
    TextView mFreqtxt;
    @BindView(R.id.progresstxt)
    TextView mProgresstxt;
    @BindView(R.id.tuningProgress)
    ProgressBar mTuningProgress;
    @BindView(R.id.dtvtxt)
    TextView mDtvtxt;
    @BindView(R.id.dtvnumtxt)
    TextView mDtvnumtxt;
    @BindView(R.id.radiotxt)
    TextView mRadiotxt;
    @BindView(R.id.radionumtxt)
    TextView mRadionumtxt;
    @BindView(R.id.datatxt)
    TextView mDatatxt;
    @BindView(R.id.datanumtxt)
    TextView mDatanumtxt;
    @BindView(R.id.buttontxt)
    TextView mButtontxt;
    @BindView(R.id.stopbutton)
    LinearLayout mStopbutton;
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
    @BindView(R.id.tuningtxt)
    TextView mTuningtxt;
    @BindView(R.id.progresslayout)
    LinearLayout mProgresslayout;
    @BindView(R.id.signalqualitytxt)
    TextView mSignalqualitytxt;
    @BindView(R.id.signalqualityProgress)
    ProgressBar mSignalqualityProgress;
    @BindView(R.id.signalqualitynum)
    TextView mSignalqualitynum;
    @BindView(R.id.signalqualitylayout)
    LinearLayout mSignalqualitylayout;
    @BindView(R.id.signalstrengthtxt)
    TextView mSignalstrengthtxt;
    @BindView(R.id.signalstrengthProgress)
    ProgressBar mSignalstrengthProgress;
    @BindView(R.id.signalstrengthnum)
    TextView mSignalstrengthnum;
    @BindView(R.id.signalstrengthlayout)
    LinearLayout mSignalstrengthlayout;
    @BindView(R.id.dtvlayout)
    LinearLayout mDtvlayout;
    @BindView(R.id.radiolayout)
    LinearLayout mRadiolayout;
    @BindView(R.id.datalayout)
    LinearLayout mDatalayout;

    private ScanManagerService mScanManagerService;
    private OptPreferences myPreference;
    private int mCurMode,mCurType;
    private int mCurSignalQuality,mCurSignalLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tuningprogress);
        ButterKnife.bind(this);
        mScanManagerService = ScanManagerService.getmScanInstance();
        mScanManagerService.initManager(myHandler);
        myPreference = OptPreferences.getInstance();
        initScanParas();
    }

    private void initScanParas() {
        mCurMode = myPreference.getInt(CommonConst.p_tunermode, 0);
        mCurType = myPreference.getInt(CommonConst.p_tuningtype, 0);
        DtvSearchRequirement requirement;
        Log.d("Oceanus", "tunermode: " + mCurMode + ",tuningtype: " + mCurType);
        switch (mCurMode) {
            case 0://dvbc
                showDtvName(true);
                try {
                    if (mCurType == 0) {//auto
                        showAutoViews();
                        requirement = new DtvSearchRequirement(EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_C);
                    } else {
                        requirement = new DtvSearchRequirement(EN_DTV_SCAN_MODE.E_DTV_MANUAL_TUNE_MODE_DVB_C);
                        requirement.setFreq((int) myPreference.getFloat(CommonConst.p_frequency, 0.0f));
                        requirement.setModulation(EN_DTV_MODULATION_MODE.values()[myPreference.getInt(CommonConst.p_modulation, 0)]);
                        requirement.setSymbolRate((int) myPreference.getFloat(CommonConst.p_symbolnum, 0.0f));
                        if (mCurType == 1) {//mannual
                            showMannualViews(true);
                            requirement.setScrambleType(EN_DVB_SCAN_SCRAMBLE_TYPE.values()[myPreference.getInt(CommonConst.p_scanmode, 0)]);
                        } else {//networkid
                            showMannualViews(false);
                            requirement.setNetworkId((int) myPreference.getFloat(CommonConst.p_networkidnum, 0.0f));
                        }
                    }
                    mScanManagerService.startDtvSearch(requirement);
                } catch (Exception e) {
                }
                break;
            case 1://dvbt
                showDtvName(true);
                try {
                    if (mCurType == 0) {//auto
                        showAutoViews();
                        requirement = new DtvSearchRequirement(EN_DTV_SCAN_MODE.E_DTV_AUTO_TUNE_MODE_DVB_T);
                    } else {
                        showMannualViews(true);
                        requirement = new DtvSearchRequirement(EN_DTV_SCAN_MODE.E_DTV_MANUAL_TUNE_MODE_DVB_T);
                        int channelIndex = myPreference.getInt(CommonConst.p_channel, 0);
                        int freq = (int)myPreference.getFloat(CommonConst.p_frequency, 0.0f);
                        FreqPoint fp = ChannelScanManager.getInstance().getScanTable(EN_INPUT_SOURCE_TYPE.E_INPUT_SOURCE_DTV_DVB_T)
                                        .get(channelIndex);
                        Log.i("Oceanus","1111111111111111111111111111111111111111111111111111111111111111111111");
                        Log.i("Oceanus","freq:"+freq);
                        if(fp.getFreq() != freq){
                            Log.i("Oceanus","set frequency only");
                            requirement.setFreq((int) myPreference.getFloat(CommonConst.p_frequency, 0.0f));
                        }else{
                            Log.i("Oceanus","set FreqPoint");
                            requirement.setFreqPoint(fp);
                        }
                        requirement.setBandWidth(EN_DTV_TUNING_BAND_WIDTH.values()[myPreference.getInt(CommonConst.p_bandwidth, 0)]);
                        requirement.setScrambleType(EN_DVB_SCAN_SCRAMBLE_TYPE.values()[myPreference.getInt(CommonConst.p_scanmode, 0)]);
                    }
                    Log.i("Oceanus", requirement.toJsonObj().toString());
                    mScanManagerService.startDtvSearch(requirement);
                } catch (Exception e) {
                }
                break;
            case 2://dvbs
                break;
            case 3://atv
                int f = 0;
                EN_ATV_SCAN_MODE atv_scan_mode;
                showDtvName(false);
                if (mCurType == 0) {//auto
                    showAutoViews();
                    atv_scan_mode = EN_ATV_SCAN_MODE.E_ATV_TUNE_MODE_AUTO_TUNE;
                } else {//mannual
                    showMannualViews(true);
                    f = (int) myPreference.getFloat(CommonConst.p_frequency, 0.0f);
                    atv_scan_mode = EN_ATV_SCAN_MODE.E_ATV_MANUAL_TUNE_MODE_SEARCH_ONE_TO_UP;
                }
                mScanManagerService.startAtvSearch(atv_scan_mode, f);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void showSignalQuality(){
        if(mCurType != 0){
            mCurSignalQuality = TvCommonManager.getInstance().getSignalQuality();
            mCurSignalLevel = TvCommonManager.getInstance().getSignalLevel();
            Log.i("Oceanus", "quality:"+mCurSignalQuality);
            Log.i("Oceanus", "level:"+mCurSignalLevel);
            if(mCurSignalQuality < 0){mCurSignalQuality = 0;}
            if(mCurSignalLevel < 0){mCurSignalLevel = 0;}
            mSignalqualityProgress.setProgress(mCurSignalQuality);
            mSignalqualitynum.setText(mCurSignalQuality+"%");
            mSignalstrengthProgress.setProgress(mCurSignalLevel);
            mSignalstrengthnum.setText(mCurSignalLevel+"%");
        }
    }

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CommonConst.m_atv_step://atv
                    AtvScanResult Aresult = (AtvScanResult) msg.obj;
                    if(mCurType == 0){//auto
                        mFreqtxt.setText(Aresult.getFreq() / 1000000.0 + "MHZ");
                        mProgresstxt.setText((int)Aresult.getPercent() + "%");
                        mTuningProgress.setProgress((int) Aresult.getPercent());
                    }else{//mannual
                        showSignalQuality();
                    }
                    mDtvnumtxt.setText(Aresult.getCurScanedChNum() + "");
                    break;
                case CommonConst.m_dtv_step://dtv
                    DtvScanResult Dresult = (DtvScanResult) msg.obj;
                    if(mCurType == 0){//auto
                        mFreqtxt.setText(Dresult.getFreq() / 1000000.0 + "MHZ");
                        mProgresstxt.setText(Dresult.getPercent() + "%");
                        mTuningProgress.setProgress(Dresult.getPercent());
                    }else{//mannual
                        showSignalQuality();
                    }
                    mDtvnumtxt.setText(Dresult.getCurScanedChNum() + "");
                    mRadionumtxt.setText(Dresult.getCurScanedRadioNum() + "");
                    mDatanumtxt.setText(Dresult.getCurScanedDataNum() + "");
                    break;
                case CommonConst.m_atv_scan_done://complete
                case CommonConst.m_dtv_scan_done:
                    showSignalQuality();
                    mButtontxt.setText("Complete");
                    this.sendEmptyMessageDelayed(CommonConst.m_scanact_finish, 15 * 1000);//finish after 5 seconds
                    break;
                case CommonConst.m_scanact_finish:
                    showSignalQuality();
                    cancelScan();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onBackPressed() {
        ScanCancelDlg dlg = new ScanCancelDlg(this, this);
        dlg.show();
    }

    @OnClick(R.id.stopbutton)
    public void onViewClicked() {
        switch (mButtontxt.getText().toString()) {
            case "Stop":
                ///mButtontxt.setText("Continue");
                //mScanManagerService.stopDtvSearch();
                cancelScan();
                break;
            case "Continue":
                //mButtontxt.setText("Stop");
                //mScanManagerService.startDtvSearch(mdtvReq);
                break;
            case "Complete":
                cancelScan();
                break;
        }
    }

    @Override
    public void cancelScan() {
        boolean ret = false;
        if (myPreference.getInt(CommonConst.p_tunermode, 0) == 3) {
            ret = mScanManagerService.stopAtvSearch();
        } else {
            ret = mScanManagerService.stopDtvSearch();
        }
        if(ret){
            Log.i("Oceanus", "cancelscan success....................");
        }else{
            Log.i("Oceanus", "cancelscan fail....................");
        }
        mScanManagerService.unInitManager();
        this.finish();
    }

    private void showDtvName(boolean dtv){
        mDtvtxt.setText(dtv ? "DTV:" : "ATV:");
    }

    private void showAutoViews(){
        mTuningtxt.setText("Auto Tuning");
        mProgresslayout.setVisibility(View.VISIBLE);
        mSignalqualitylayout.setVisibility(View.GONE);
        mSignalstrengthlayout.setVisibility(View.GONE);
        mDatalayout.setVisibility(View.VISIBLE);
    }

    private void showMannualViews(boolean mannual){
        if(mannual){
            mTuningtxt.setText("Mannual Tuning");
        }else{
            mTuningtxt.setText("Network");
        }
        mProgresslayout.setVisibility(View.GONE);
        mSignalqualitylayout.setVisibility(View.VISIBLE);
        mSignalstrengthlayout.setVisibility(View.VISIBLE);
        mDatalayout.setVisibility(View.GONE);
    }

}
