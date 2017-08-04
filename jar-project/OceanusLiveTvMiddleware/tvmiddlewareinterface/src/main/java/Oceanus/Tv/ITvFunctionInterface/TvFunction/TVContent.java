package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.text.TextUtils;

import com.mediatek.twoworlds.tv.MtkTvATSCRating;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;
import com.mediatek.twoworlds.tv.MtkTvBroadcast;
import com.mediatek.twoworlds.tv.MtkTvChannelListBase;
import com.mediatek.twoworlds.tv.MtkTvConfig;
import com.mediatek.twoworlds.tv.MtkTvCI;
import com.mediatek.twoworlds.tv.MtkTvISDBRating;
import com.mediatek.twoworlds.tv.MtkTvDVBRating;
import com.mediatek.twoworlds.tv.MtkTvDvbsConfigBase;
import com.mediatek.twoworlds.tv.MtkTvInputSource;
import com.mediatek.twoworlds.tv.MtkTvScan;
import com.mediatek.twoworlds.tv.MtkTvScanDvbcBase;
import com.mediatek.twoworlds.tv.MtkTvScanPalSecamBase;
import com.mediatek.twoworlds.tv.MtkTvScanBase.ScanMode;
import com.mediatek.twoworlds.tv.MtkTvScanBase.ScanType;
import com.mediatek.twoworlds.tv.MtkTvScanDvbsBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase.RfDirection;
import com.mediatek.twoworlds.tv.MtkTvScanDvbtBase.RfInfo;
import com.mediatek.twoworlds.tv.MtkTvTime;
import com.mediatek.twoworlds.tv.MtkTvUtil;
import com.mediatek.twoworlds.tv.MtkTvUtilBase;
import com.mediatek.twoworlds.tv.common.MtkTvChCommonBase;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;
import com.mediatek.twoworlds.tv.model.MtkTvChannelQuery;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvATSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvCQAMScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbcScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvDvbsSatelliteSettingBase;
import com.mediatek.twoworlds.tv.model.MtkTvAnalogChannelInfo;
import com.mediatek.twoworlds.tv.model.MtkTvChannelInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvISDBScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanPara;
import com.mediatek.twoworlds.tv.model.MtkTvNTSCScanParaBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfo;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPPara;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfo;
import com.mediatek.twoworlds.tv.model.MtkTvOpenVCHIPSettingInfoBase;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfo;
import com.mediatek.twoworlds.tv.model.MtkTvUSTvRatingSettingInfoBase;
//import com.mediatek.twoworlds.tv.MtkTvScan.ScanMode;
//import com.mediatek.twoworlds.tv.MtkTvScan.ScanType;
import com.mediatek.twoworlds.tv.MtkTvMultiMediaBase;
import com.mediatek.twoworlds.tv.MtkTvAppTVBase;



import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import android.media.tv.TvInputManager;
import android.media.tv.TvContentRating;
import java.util.ArrayList;

public class TVContent {
	private static TVContent instance;
    public static final int VSH_SRC_TAG3D_2D  = 0;
    public static final int VSH_SRC_TAG3D_MVC = 1;      // MVC = Multi-View Codec
    public static final int VSH_SRC_TAG3D_FP  = 2;      // FP = Frame Packing
    public static final int VSH_SRC_TAG3D_FS  = 3;     // FS = Frame Sequential
    public static final int VSH_SRC_TAG3D_TB  = 4;       // TB = Top-and-Bottom
    public static final int VSH_SRC_TAG3D_SBS = 5;      // SBS = Side-by-Side
    public static final int VSH_SRC_TAG3D_REALD = 6;    //
    public static final int VSH_SRC_TAG3D_SENSIO = 7;   //
    public static final int VSH_SRC_TAG3D_LA = 8;      // LA = Line Alternative
    public static final int VSH_SRC_TAG3D_TTDO = 9;     // TTD only. It is 2D mode
    public static final int VSH_SRC_TAG3D_NOT_SUPPORT = 10;
	private MtkTvScan mScan;
	private MtkTvConfig mTvConfig;
	private MtkTvATSCRating mTvRatingSettingInfo;
	private MtkTvOpenVCHIPSettingInfoBase mOpenVCHIPSettingInfoBase;
	private MtkTvOpenVCHIPPara para;
	private MtkTvCI mCIBase;
	private MtkTvMultiMediaBase mMtkTvMultiMediaBase;
	private final MtkTvISDBScanPara mISDBScanPara = new MtkTvISDBScanPara();
	private final MtkTvATSCScanPara mATSCScanPara = new MtkTvATSCScanPara();
	private final MtkTvNTSCScanPara mNTSCScanPara = new MtkTvNTSCScanPara();
	private final boolean dumy = false;
	private final HashMap<String, Integer> dumyData = new HashMap<String, Integer>();
	private int region =3;
	private int mScanMode = 0;
	private final SaveValue saveV;
	private final Context mContext;
	private final String TAG = "TVContent";

	//add by sin_biaoqinggao
	//last input source's Name;
	private String lastInputSourceName ="" ;
	//current input source's Name;
	private String currInputSourceName ="";

	public int getmScanMode() {
		return saveV.readValue(MenuConfigManager.US_SCAN_MODE);
	}

	public void setmScanMode(int mScanMode) {
		this.mScanMode = mScanMode;
		saveV.saveValue(MenuConfigManager.US_SCAN_MODE, mScanMode);
	}

	protected TVContent(Context context) {
		mContext = context;
		saveV = SaveValue.getInstance(context);
		init();
	}

	private void init() {
		dumyData.clear();
		mScan = MtkTvScan.getInstance();
		mTvConfig = MtkTvConfig.getInstance();
		mCIBase = MtkTvCI.getInstance(0);
		mTvRatingSettingInfo = MtkTvATSCRating.getInstance();
		region = 3;
		// region = MarketRegionInfo.REGION_SA;//
		mMtkTvMultiMediaBase = new MtkTvMultiMediaBase();
	}

	static public synchronized TVContent getInstance(Context context) {
		if (instance == null) {
			instance = new TVContent(context);
		}
		return instance;
	}

	public String getSysVersion(int eType, String sVersion) {
		String version = MtkTvUtil.getInstance().getSysVersion(eType, sVersion);
		Log.d(TAG, "getSysVersion" + version);
		return version;
	}
	public int getBlockUnrated() {
		return 0;
	}

	public void setBlockUnrated(boolean isBlockUnrated) {
	}

	public MtkTvOpenVCHIPInfoBase getOpenVchip() {
		if (para == null) {
			para = new MtkTvOpenVCHIPPara();
		}
		return mTvRatingSettingInfo.getOpenVCHIPInfo(para);
	}
	public boolean isEURegion() {
		return region == 3;
	}
	public MtkTvOpenVCHIPPara getOpenVCHIPPara() {
		if (para == null) {
			para = new MtkTvOpenVCHIPPara();
		}
		return para;
	}

	public MtkTvOpenVCHIPSettingInfoBase getOpenVchipSetting() {
		if (mOpenVCHIPSettingInfoBase == null) {
			mOpenVCHIPSettingInfoBase = mTvRatingSettingInfo
					.getOpenVCHIPSettingInfo();
			;
		}
		return mOpenVCHIPSettingInfoBase;
	}

	public void setOpenVChipSetting(int regionIndex, int dimIndex, int levIndex) {
	}


	public int getRegion() {
		return region;
	}

/*
	 * ACFG Function
	 */
	public int getMinValue(String cfgId) {
		if (dumy) {
			return 0;
		} else {
			int value = mTvConfig.getMinMaxConfigValue(cfgId);
			return MtkTvConfig.getMinValue(value);
		}
	}

	public int getMaxValue(String cfgId) {
		if (dumy) {
			return 100;
		} else {
			int value = mTvConfig.getMinMaxConfigValue(cfgId);
			Log.d("TVContent", "value:" + value);
			return MtkTvConfig.getMaxValue(value);
		}
	}

	public int getConfigValue(String cfgId) {
		Log.d("TVContent",
				"getConfigValue(cfgId):" + mTvConfig.getConfigValue(cfgId)
						+ "cfgId:" + cfgId);
		return mTvConfig.getConfigValue(cfgId);
	}

	public String getConfigString(String cfgId) {
		return mTvConfig.getConfigString(cfgId);
	}

	public void setConfigValue(String cfgId, int value) {
		Log.d("TVContent", "setConfigValue cfgId:" + cfgId + "----value:"
				+ value);
		if (dumy) {
			dumyData.put(cfgId, value);
		} else {
			if (cfgId.equalsIgnoreCase(MtkTvConfigType.CFG_VIDEO_VID_MJC_DEMO)) {
				mTvConfig.setConfigValue(cfgId, value, 1);
			} else {
				mTvConfig.setConfigValue(cfgId, value);
			}
		}
	}

	public void setConfigValue(String cfgId, int value, boolean isUpate) {
		Log.d("TVContent", "setConfigValue cfgId:" + cfgId + "----value:"
				+ value);
		if (dumy) {
			dumyData.put(cfgId, value);
		} else {
			int update = 0;
			if (isUpate) {
				update = 1;
			}
			mTvConfig.setConfigValue(cfgId, value, update);
		}
	}

	public boolean isConfigEnabled(String cfgId) {
		return mTvConfig.isConfigEnabled(cfgId) == MtkTvConfigType.CFGR_ENABLE ? true
				: false;
	}



	public boolean isAnalog(MtkTvChannelInfoBase channel) {
		Log.d("TVContent", "isAnalog\n");
		if (channel instanceof MtkTvAnalogChannelInfo) {
			Log.d("TVContent", "isAnalog yes\n");
			return true;
		}
		Log.d("TVContent", "isAnalog no\n");
		return false;
	}

	public boolean isHaveScreenMode() {
		boolean flag = isConfigVisible(MenuConfigManager.SCREEN_MODE);
		Log.d("TVContent", "isHaveScreenMode flag:" + flag);
		return flag;
	}

	/**
	 * check is config visible
	 * @return
	 */
	public boolean isConfigVisible(String cfgid){
		boolean flag = mTvConfig.isConfigVisible(cfgid) == MtkTvConfigType.CFGR_VISIBLE ? true
				: false;
		return flag;
	}

	public boolean isFilmModeEnabled() {
		if (isConfigEnabled(MenuConfigManager.GAME_MODE)) {
			return false;
		}
		return false;
	}

	/**
	 * Get current signal level
	 *
	 * @return true(no signal)/false(with signal)
	 */
	public boolean isSignalLoss() {

		boolean hasSignal = false;
		hasSignal = MtkTvBroadcast.getInstance().isSignalLoss();

		Log.d("TVContent", "isSignalLoss()?," + hasSignal);
		return hasSignal;
	}

	/**
	 * Get current signal level
	 *
	 * @return 0-100
	 */
	public int getSignalLevel() {

		Log.d("TVContent", "Enter getSignalLevel\n");

		return MtkTvBroadcast.getInstance().getSignalLevel();
	}

	/**
	 * Get current signal Quality
	 *
	 * @return 0-100
	 */
	public int getSignalQuality() {

		Log.d("TVContent", "Enter getSignalQuality\n");
		return MtkTvBroadcast.getInstance().getSignalQuality();
	}

	public void updatePowerOn(String cfgID, int enable, String date) {
		int daySec = onTimeModified(date);
		int timerValue = ((((((enable)) & 0x01) << 31) & 0x80000000) | ((daySec) & 0x0001ffff));
		Log.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
		mTvConfig.setConfigValue(cfgID, timerValue);
	}

	public void updatePowerOff(String cfgID, int enable, String date) {
		int daySec = onTimeModified(date);
		int timerValue = ((((((enable)) & 0x01) << 31) & 0x80000000) | ((daySec) & 0x0001ffff));
		Log.d("TVContent", "timerValue:" + timerValue + "cfgID:" + cfgID);
		mTvConfig.setConfigValue(cfgID, timerValue);
	}

	public int onTimeModified(String time) {
		int hour = Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3, 5));
		int second = Integer.parseInt(time.substring(6));
		return hour * 3600 + minute * 60 + second;
	}

	public void setTimeInterval(int value) {
		mTvConfig.setConfigValue(MenuConfigManager.PARENTAL_CFG_RATING_BL_TYPE,
				value);
	}

	public void setTimeIntervalTime(String cfgID, String date) {
		int daySec = onTimeModified(date);
		mTvConfig.setConfigValue(cfgID, daySec * 1000);
	}

	/*
	 * true is right,false is left
	 */
	public int setSleepTimer(boolean direction) {
		Log.d("TVContent", "direction:" + direction);
		int valueIndex = 0;
		int leftmill = getSleepTimerRemaining();
		int mill = MtkTvTime.getInstance().getSleepTimer(direction);
		if (leftmill > 0) {
			int minute = leftmill / 60;
			if (minute > 0 && minute < 1) {
				valueIndex = direction ? 1 : 8;
			} else if (minute >= 1 && minute < 9) {
				valueIndex = direction ? 1 : 0;
			} else if (minute >= 9 && minute < 11) {
				valueIndex = direction ? 2 : 0;
			} else if (minute >= 11 && minute < 19) {
				valueIndex = direction ? 2 : 1;
			} else if (minute >= 19 && minute < 21) {
				valueIndex = direction ? 3 : 1;
			} else if (minute >= 21 && minute < 29) {
				valueIndex = direction ? 3 : 2;
			} else if (minute >= 29 && minute < 31) {
				valueIndex = direction ? 4 : 2;
			} else if (minute >= 31 && minute < 39) {
				valueIndex = direction ? 4 : 3;
			} else if (minute >= 39 && minute < 41) {
				valueIndex = direction ? 5 : 3;
			} else if (minute >= 41 && minute < 49) {
				valueIndex = direction ? 5 : 4;
			} else if (minute >= 49 && minute < 51) {
				valueIndex = direction ? 6 : 4;
			} else if (minute >= 51 && minute < 59) {
				valueIndex = direction ? 6 : 5;
			} else if (minute >= 59 && minute < 61) {
				valueIndex = direction ? 7 : 5;
			} else if (minute >= 61 && minute < 89) {
				valueIndex = direction ? 7 : 6;
			} else if (minute >= 89 && minute < 91) {
				valueIndex = direction ? 8 : 6;
			} else if (minute >= 91 && minute < 119) {
				valueIndex = direction ? 8 : 7;
			} else if (minute >= 119 && minute <= 120) {
				valueIndex = direction ? 0 : 7;
			}
			Log.d("TVContent", "minute:" + minute + "valueIndex:"
					+ valueIndex);
		} else {
			switch (mill / 60) {
			case 10:
				valueIndex = 1;
				break;
			case 20:
				valueIndex = 2;
				break;
			case 30:
				valueIndex = 3;
				break;
			case 40:
				valueIndex = 4;
				break;
			case 50:
				valueIndex = 5;
				break;
			case 60:
				valueIndex = 6;
				break;
			case 90:
				valueIndex = 7;
				break;
			case 120:
				valueIndex = 8;
				break;
			default:
				valueIndex = 0;
				break;
			}
		}
		return valueIndex;
	}

	/*
	 * true is right,false is left
	 */
	public int getSleepTimerRemaining() {
		return MtkTvTime.getInstance().getSleepTimerRemainingTime();
	}

	public void resetConfigValues() {
		mTvConfig.resetConfigValues(MtkTvConfigType.CFGU_FACTORY_RESET_ALL);
		// mTvConfig.resetConfigValues(MtkTvConfigType.CFGU_AUDIO_ITEMS);
		// mTvConfig.resetConfigValues(MtkTvConfigType.CFGU_SCREEN_ITEMS);
	}
	public int getDefaultNetWorkID() {
		// TODO Auto-generated method stub
		return 104000;
	}


	public int updateCIKey() {
		return mCIBase.updateCIKey();
	}

	public int eraseCIKey() {
		return mCIBase.eraseCIKey();
	}

	public String getCIKeyinfo() {
		return mCIBase.getCIKeyinfo();
	}

	public boolean isShowCountryRegion() {
		String country = mTvConfig.getCountry();
		Log.d(TAG, "isShowCountryRegion country*****************" + country);
		boolean isShow = false;
		if (country.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_AUS)
				|| country
						.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_ESP)
				|| country.equalsIgnoreCase(MtkTvConfigType.S639_CFG_LANG_POR)) {
			if (country.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_ESP)) {
				if (getConfigValue(MtkTvConfigType.CFG_TIME_TZ_SYNC_WITH_TS) == 1) {
					isShow = true;
				}
			} else {
				isShow = true;
			}
		}
		Log.d(TAG, "isShowCountryRegion*****************" + isShow);
		return isShow;
	}

	public boolean isAusCountry() {
		String country = mTvConfig.getCountry();
		if (country.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_AUS)) {
			return true;
		}
		return false;
	}

	public boolean isNorCountry() {
		String country = mTvConfig.getCountry();
		if (country
						.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_NOR)) {
			return true;
		}
		return false;
	}

	// is france
	public boolean isFraCountry() {
		String country = mTvConfig.getCountry();
		if (country
						.equalsIgnoreCase(MtkTvConfigType.S3166_CFG_COUNT_FRA)) {
			return true;
		}
		return false;
	}

	public int getWowEnable() {
		// boolean enable = MtkNetworkManager.getInstance().isEnanbleWoWL();
		// Log.d(TAG, "getWowEnable*****************"+enable);
		// return enable?1:0;
		return 0;
	}

	public void setWowEnable(int index) {
		Log.d(TAG, "setWowEnable*****************" + index);
		saveV.saveValue(MenuConfigManager.SETUP_WOW, index);
//		if (index == 0) {
//			MtkNetworkManager.getInstance().setEnableWoWL(false);
//		} else {
//			MtkNetworkManager.getInstance().setEnableWoWL(true);
//		}
	}

	public void setWolEnable(int index) {
		Log.d(TAG, "setWolEnable*****************" + index);

//		if (index == 0) {
//			MtkNetworkManager.getInstance().setEnableWol(false);
//		} else {
//			MtkNetworkManager.getInstance().setEnableWol(true);
//		}
	}

	public int getWolEnable() {
		return 1;
//	    boolean enable = MtkNetworkManager.getInstance().isEnableWol();
//	    Log.d(TAG, "getWowEnable*****************"+enable);
//	    return enable ? 1 : 0;
	}



	public static int snapshotID = -1;
	public static int dvbsLastOP = -1;
	public static int mDvbsSatSnapShotId = -1;

    public boolean isSourceType3D() {
        MtkTvAppTVBase apptv = new MtkTvAppTVBase();
//        int type = apptv.GetVideoSrcTag3DType(CommonIntegration.getInstance().getCurrentFocus());
//        Log.d(TAG, "isSourceType3D type:"+type);
		int type = VSH_SRC_TAG3D_2D;
        switch (type) {
            case VSH_SRC_TAG3D_TTDO:
            case VSH_SRC_TAG3D_NOT_SUPPORT:

                return false;
            case VSH_SRC_TAG3D_2D:
            case VSH_SRC_TAG3D_MVC:
            case VSH_SRC_TAG3D_FP:
            case VSH_SRC_TAG3D_FS:
            case VSH_SRC_TAG3D_TB:
            case VSH_SRC_TAG3D_SBS:
            case VSH_SRC_TAG3D_REALD:
            case VSH_SRC_TAG3D_SENSIO:
            case VSH_SRC_TAG3D_LA:
                return true;
            default:
                return false;
        }
    }


    //check last input source name is vga
    public boolean isLastInputSourceVGA(){
    	if(lastInputSourceName.equalsIgnoreCase(MtkTvInputSource.INPUT_TYPE_VGA)){
    		lastInputSourceName = "";//reset to aviod
    		return true;
    	}
    	return false;
    }




    public String getDrmRegistrationCode() {
	     return	mMtkTvMultiMediaBase.GetDrmRegistrationCode();
	}
	public String setDrmDeactivation(){
		 return	mMtkTvMultiMediaBase.SetDrmDeactivation();
	}
	public long getDrmUiHelpInfo(){
		 return	mMtkTvMultiMediaBase.GetDrmUiHelpInfo();
	}





}
