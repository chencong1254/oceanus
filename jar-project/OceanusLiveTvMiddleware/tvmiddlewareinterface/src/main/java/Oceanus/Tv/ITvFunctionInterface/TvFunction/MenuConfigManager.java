package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.media.tv.TvInputManager;
import android.util.Log;

import com.mediatek.twoworlds.tv.MtkTvATSCCloseCaption;
import com.mediatek.twoworlds.tv.MtkTvHighLevel;
import com.mediatek.twoworlds.tv.common.MtkTvConfigType;
import com.mediatek.twoworlds.tv.common.MtkTvConfigTypeBase;

import java.util.ArrayList;
import java.util.HashMap;

public class MenuConfigManager {
	 private static final String TAG = "Oceanus";

	    private static MenuConfigManager mConfigManager;
	    public static final int MSG_SCAN_UNKNOW = 0x00000000;
	    public static final int MSG_SCAN_COMPLETE = 0x00000001;
	    public static final int MSG_SCAN_PROGRESS = 0x0000002;
	    public static final int MSG_SCAN_CANCEL = 0x00000004;
	    public static final int MSG_SCAN_ABORT = 0x00000008;
		public static final int TV_SYS_MASK_B = 2;//(1<<1)
		public static final int TV_SYS_MASK_D = 8;//(1<<3)
		public static final int TV_SYS_MASK_G = 64;//(1<<6)
		public static final int TV_SYS_MASK_I = 256;//(1<<8)
		public static final int TV_SYS_MASK_K = 1024;//(1<<10)
		public static final int TV_SYS_MASK_L = 4096;//(1<<12)
		public static final int TV_SYS_MASK_L_PRIME = 8192;//(1<<13)
		public static final int TV_SYS_M = 16384;//(1<<14)
		public static final int TV_SYS_N = 32768;//(1<<14)


	public static final int AUDIO_SYS_MASK_AM        = 1;//(1<<0)
	    public static final int AUDIO_SYS_MASK_FM_MONO   = 2;//(1<<1)
	    public static final int AUDIO_SYS_MASK_FM_A2     = 8;//(1<<3)
	    public static final int AUDIO_SYS_MASK_FM_A2_DK1 = 16;//(1<<4)
	    public static final int AUDIO_SYS_MASK_FM_A2_DK2 = 32;//(1<<5)
	    public static final int AUDIO_SYS_MASK_NICAM     = 128;//(1<<7)


	public static final int SCC_AUD_MTS_UNKNOWN = 0;
	    public static final int SCC_AUD_MTS_MONO = 1;
	    public static final int SCC_AUD_MTS_STEREO = 2;
	    public static final int SCC_AUD_MTS_SUB_LANG = 3;
	    public static final int SCC_AUD_MTS_DUAL1 = 4;
	    public static final int SCC_AUD_MTS_DUAL2 = 5;
	    public static final int SCC_AUD_MTS_NICAM_MONO = 6;
	    public static final int SCC_AUD_MTS_NICAM_STEREO = 7;
	    public static final int SCC_AUD_MTS_NICAM_DUAL1 = 8;
	    public static final int SCC_AUD_MTS_NICAM_DUAL2 = 9;
	    public static final int SCC_AUD_MTS_FM_MONO = 10;
	    public static final int SCC_AUD_MTS_FM_STEREO = 11;

	    public static final int EDIT_CHANNEL_LENGTH = 16;

	    TVContent mTV;
	    SaveValue save; // save some UI value
//	    TVCommonInterface comm;
//	    ListenTime listenTime;
//	    SleepTimerOff sleepTimerOff;

	    int minValue = 0;
	    int maxValue = 0;
	    int defaultValue = 0;
	    int tvOptionValue = 0;
	    Context mContext;

	public static final String TV_FOCUS_WIN_MAIN = "main";
	public static final String TV_FOCUS_WIN_SUB = "sub";

	public static final int TV_NORMAL_MODE = 0;
	public static final int TV_PIP_MODE = 1;
	public static final int TV_POP_MODE = 2;

	public static final int SUPPORT_THIRD_PIP_MODE = 1;

	public static final int NOT_SUPPORT_THIRD_PIP_MODE = 0;

        private TvInputManager mTvInputManager = null;
	private static MtkTvHighLevel instanceMtkTvHighLevel;
	private static int mCurrentTvMode = -1;

	/**
	 * current TV mode is POP state or not
	 * @return
	 */
	public boolean isPOPState() {
		if(mCurrentTvMode != TV_NORMAL_MODE){
			mCurrentTvMode = instanceMtkTvHighLevel.getCurrentTvMode();
		}
		else{
			return false;
		}
		if (TV_POP_MODE == mCurrentTvMode) {
			return true;
		}
		return false;
	}


	private MenuConfigManager(Context context) {
	        mContext = context;
	        mTV = TVContent.getInstance(context);
	        save = SaveValue.getInstance(context);
	        if(mScreenMode.isEmpty()){
	        	init();
	        }
	    }

	    private void init() {
			// TODO Auto-generated method stub
//	    	String[] array = mContext.getResources().getStringArray(R.array.screen_mode_array_us);
//	    	for(int i=0;i< array.length;i++){
//	    		mScreenMode.put(array[i], i);
//	    		mScreenModeReverse.put(i,array[i]);
//	    	}
			instanceMtkTvHighLevel = new MtkTvHighLevel();

		}
//	    public void reloadScreenModes(){
//	    	String[] array = mContext.getResources().getStringArray(R.array.screen_mode_array_us);
//	    	for(int i=0;i< array.length;i++){
//	    		mScreenMode.put(array[i], i);
//	    		mScreenModeReverse.put(i,array[i]);
//	    	}
//	    }

	    public static MenuConfigManager getInstance(Context context) {
	        if (mConfigManager == null) {
	            mConfigManager = new MenuConfigManager(context);
	        }
	        return mConfigManager;
	    }

	    /** Video */
	    public static final String PICTURE_MODE = MtkTvConfigType.CFG_VIDEO_PIC_MODE;
	    public static final String BACKLIGHT = MtkTvConfigType.CFG_DISP_DISP_DISP_BACK_LIGHT;// CFG_DISP_DISP_DISP_BACK_LIGHT;
	    public static final String BRIGHTNESS = MtkTvConfigType.CFG_VIDEO_VID_BRIGHTNESS;
	    public static final String CONTRAST = MtkTvConfigType.CFG_VIDEO_VID_CONTRAST;
	    public static final String SATURATION = MtkTvConfigType.CFG_VIDEO_VID_SAT;
	    public static final String HUE = MtkTvConfigType.CFG_VIDEO_VID_HUE;
	    public static final String SHARPNESS = MtkTvConfigType.CFG_VIDEO_VID_SHP;
	    public static final String GAMMA = MtkTvConfigType.CFG_DISP_DISP_DISP_GAMMA;
	    public static final String COLOR_TEMPERATURE = MtkTvConfigType.CFG_VIDEO_CLR_TEMP;//

		public static final String AUTO_VIEW = MtkTvConfigType.CFG_DISP_DISP_ADP_BACK_LIGHT;
	    public static final String COLOR_G_R = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_R;
	    public static final String COLOR_G_G = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_G;
	    public static final String COLOR_G_B = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_B;
	    public static final String DNR = MtkTvConfigType.CFG_VIDEO_VID_NR;
	    public static final String MPEG_NR = MtkTvConfigType.CFG_VIDEO_VID_MPEG_NR;
	    public static final String ADAPTIVE_LUMA_CONTROL = MtkTvConfigType.CFG_VIDEO_VID_LUMA;
	    public static final String FLESH_TONE = MtkTvConfigType.CFG_VIDEO_VID_FLESH_TONE;
	    public static final String DI_FILM_MODE = MtkTvConfigType.CFG_VIDEO_VID_DI_FILM_MODE;
	    public static final String BLUE_STRETCH = MtkTvConfigType.CFG_VIDEO_VID_BLUE_STRETCH;
	    public static final String GAME_MODE = MtkTvConfigType.CFG_VIDEO_VID_GAME_MODE;
	    public static final String PQ_SPLIT_SCREEN_DEMO_MODE = MtkTvConfigType.CFG_VIDEO_VID_PQ_DEMO;// MtkTvConfigType.CFG_VIDEO_PQ_DEMO;
	    public static final String BLACK_BAR_DETECTION = MtkTvConfigType.CFG_VIDEO_VID_BLACK_BAR_DETECT;
	    public static final String SUPER_RESOLUTION = MtkTvConfigType.CFG_VIDEO_VID_SUPER_RESOLUTION;
	    public static final String GRAPHIC = MtkTvConfigType.CFG_VIDEO_VID_SUPER_RESOLUTION;
	    public static final String FV_VIDEO_VID_XVYCC = MtkTvConfigType.CFG_VIDEO_VID_XVYCC;//
	    public static final String CFG_MENU_XVYCC = "g_menu__xvYCC";

	    //CFG_GRP_SUBTITLE_PREFIX
	    public static final String SUBTITLE_GROUP = "SUBTITLE_GROUP";
	    public static final String ANALOG_SUBTITLE = MtkTvConfigType.CFG_SUBTITLE_SUBTITLE_ENABLED;
		public static final String DTV_SUBTITLE_ON_OFF = "Subtitle on-off";
	    public static final String DIGITAL_SUBTITLE_LANG = MtkTvConfigType.CFG_SUBTITLE_SUBTITLE_LANG;
	    public static final String DIGITAL_SUBTITLE_LANG_2ND = MtkTvConfigType.CFG_SUBTITLE_SUBTITLE_LANG_2ND;
	    public static final String SUBTITLE_TYPE = MtkTvConfigType.CFG_SUBTITLE_SUBTITLE_ATTR;

	    // Config for vga input source
	    public static final String VGA_MODE = MtkTvConfigType.CFG_VIDEO_VID_VGA_MODE;

	    // HDMI mode
	    public static final String HDMI_MODE = MtkTvConfigType.CFG_VIDEO_VID_HDMI_MODE;

	    // Configs for VGA mode
	    public static final String HPOSITION = MtkTvConfigType.CFG_VGA_VGA_POS_H;
	    public static final String VPOSITION = MtkTvConfigType.CFG_VGA_VGA_POS_V;
	    public static final String PHASE = MtkTvConfigType.CFG_VGA_VGA_PHASE;
	    public static final String CLOCK = MtkTvConfigType.CFG_VGA_VGA_CLOCK;
	    // VGA
	    public static final String VGA = "SUB_VGA";
	    // auto adjust
	    public static final String AUTO_ADJUST = "SUB_AUTO_ADJUST";

	    public static final String VIDEO_3D = "g_video__vid_3d_item";
	    public static final String VIDEO_3D_MODE = MtkTvConfigType.CFG_VIDEO_VID_3D_MODE;// no
	    public static final String VIDEO_3D_NAV = MtkTvConfigType.CFG_VIDEO_VID_3D_NAV_AUTO;
	    public static final String VIDEO_3D_3T2 = MtkTvConfigType.CFG_VIDEO_VID_3D_TO_2D;
	    public static final String VIDEO_3D_FIELD = MtkTvConfigType.CFG_VIDEO_VID_3D_FLD_DEPTH;
	    public static final String VIDEO_3D_PROTRUDE = MtkTvConfigType.CFG_VIDEO_VID_3D_PROTRUDEN;
	    public static final String VIDEO_3D_DISTANCE = MtkTvConfigType.CFG_VIDEO_VID_3D_DISTANCE;
	    public static final String VIDEO_3D_IMG_SFTY = MtkTvConfigType.CFG_VIDEO_VID_3D_IMG_SFTY;
	    public static final String VIDEO_3D_LF = MtkTvConfigType.CFG_VIDEO_VID_3D_LR_SWITCH;
	    public static final String VIDEO_3D_OSD_DEPTH = MtkTvConfigType.CFG_VIDEO_VID_3D_OSD_DEPTH;
	    /**
	     * CECFUN
	     */
	    public static final String CEC_CEC_FUN = MtkTvConfigType.CFG_CEC_CEC_FUNC;
	    public static final int CEC_FUNTION_ON = MtkTvConfigType.CEC_FUNC_ON;
	    public static final int CEC_FUNTION_OFF = MtkTvConfigType.CEC_FUNC_OFF;
	    public static final String CEC_SAC_OFUN = MtkTvConfigType.CFG_CEC_CEC_SAC_FUNC;
	    public static final String CEC_AUTO_ON = MtkTvConfigType.CFG_CEC_CEC_AUTO_ON;
	    public static final String CEC_AUTO_OFF = MtkTvConfigType.CFG_CEC_CEC_AUTO_OFF;
	    public static final String CEC_DEVICE_DISCOVERY = "cec_device";

	    // MJC
	    public static final String MJC = "UNDEFINE_MJC";
	    // EFFECT
	    public static final String EFFECT = MtkTvConfigType.CFG_VIDEO_VID_MJC_EFFECT;
	    public static final String MENU_MJC_MODE = MtkTvConfigType.CFG_VIDEO_VID_MJC_MODE;
	    public static final String DEMO = "UNDEFINE_DEMO";
	    // DEMO PARTITION
	    public static final String DEMO_PARTITION = MtkTvConfigType.CFG_VIDEO_VID_MJC_DEMO;
	    public static final String CFG_VIDEO_VID_MJC_DEMO_STATUS = "g_video__vid_mjc_status";

	    // EU frequent
	    public static final String FREQUENCY_LEN_6 = "frequency_length_six";

	    /** factory */
	    /*
	     * factory_video
	     */
	    // AUTO_COLOR
	    public static final String FV_AUTOCOLOR = "SUB_FV_AUTOCOLOR";
	    // COLOR_TEMPERATURE
	    public static final String FV_COLORTEMPERATURE = "SUB_FV_COLORTEMPERATURE";
	    // COLOR_TEMPERATURE_CHILD
	    public static final String FV_COLORTEMPERATURECHILD = MtkTvConfigType.CFG_VIDEO_CLR_TEMP;
	    // H.Position
	    public static final String FV_HPOSITION = MtkTvConfigType.CFG_VIDEO_VID_POS_H;
	    // V.Position
	    public static final String FV_VPOSITION = MtkTvConfigType.CFG_VIDEO_VID_POS_V;
	    // AUTO PHASE
	    public static final String FV_AUTOPHASE = "SUB_FV_AUTOPHASE";
	    // PHASE
	    public static final String FV_VGA_PHASE = MtkTvConfigType.CFG_VGA_VGA_PHASE;
	    public static final String FV_YPBPR_PHASE = MtkTvConfigType.CFG_VIDEO_VID_YPBPR_PHASE;
	    // DIMA
	    public static final String FV_DIMA = MtkTvConfigType.CFG_VIDEO_VID_DI_MA;
	    // DIEDGE
	    public static final String FV_DIEDGE = MtkTvConfigType.CFG_VIDEO_VID_DI_EDGE;
	    // WCG
	    public static final String FV_WCG = MtkTvConfigType.CFG_VIDEO_VID_WCG;
	    // FLIP
	    public static final String FV_FLIP = MtkTvConfigType.CFG_MISC_EX_FLIP;// TODO
	    // MIRROR
	    public static final String FV_MIRROR = MtkTvConfigType.CFG_MISC_EX_MIRROR;// TODO
	    // Local dimming
	    public static final String FV_LOCAL_DIMMING = MtkTvConfigType.CFG_MISC_EX_DIMMING;// TODO
	    // factory_video_COLOR TEMPERATURE
	    // r gain
	    public static final String FV_COLOR_G_R = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_R;
	    // g gain
	    public static final String FV_COLOR_G_G = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_G;
	    // b gain
	    public static final String FV_COLOR_G_B = MtkTvConfigType.CFG_VIDEO_CLR_GAIN_B;
	    // r offset
	    public static final String FV_COLOR_O_R = MtkTvConfigType.CFG_VIDEO_CLR_OFFSET_R;
	    // g offset
	    public static final String FV_COLOR_O_G = MtkTvConfigType.CFG_VIDEO_CLR_OFFSET_G;
	    // b offset
	    public static final String FV_COLOR_O_B = MtkTvConfigType.CFG_VIDEO_CLR_OFFSET_B;

	    /*
	     * factory_audio
	     */
	    // DOLBY BANNER
	    public static final String FA_DOLBYBANNER = MtkTvConfigType.CFG_AUD_DOLBY_CERT_MODE;
	    // COMPRESSION
	    public static final String FA_COMPRESSION = MtkTvConfigType.CFG_AUD_DOLBY_CMPSS;
	    // COMPRESSION FACTOR
	    public static final String FA_COMPRESSIONFACTOR = MtkTvConfigType.CFG_AUD_DOLBY_DRC;
	    // MTS SYSTEM
	    public static final String FA_MTS_SYSTEM = "SUB_FA_MTS_SYSTEM";
	    // A2 SYSTEM
	    public static final String FA_A2SYSTEM = "SUB_FA_A2SYSTEM";
	    // PAL SYSTEM
	    public static final String FA_PALSYSTEM = "SUB_FA_PALSYSTEM";
	    // EU SYSTEM
	    public static final String FA_EUSYSTEM = "SUB_FA_EUSYSTEM";
	    // LATENCY
	    public static final String FA_LATENCY = MtkTvConfigType.CFG_AUD_AUD_LATENCY;
	    /*
	     * factory_audio_MTS_system
	     */
	    // NUMBERS OF CHECK
	    public static final String FAMTS_NUMBERSOFCHECK = MtkTvConfigType.CFG_MISC_EX_NUM_OF_CHECK;
	    // NUMBERS OF Pilot
	    public static final String FAMTS_NUMBERSOFPILOT = MtkTvConfigType.CFG_MISC_EX_NUM_OF_PILOT;
	    // NUMBERS OF PILOT_THRESHOLD_HIGH
	    public static final String FAMTS_PILOT_THRESHOLD_HIGH = MtkTvConfigType.CFG_MISC_EX_PILOT_THRESHOD_HIGH;
	    // NUMBERS OF PILOT_THRESHOLD_LOW
	    public static final String FAMTS_PILOT_THRESHOLD_LOW = MtkTvConfigType.CFG_MISC_EX_PILOT_THRESHOD_LOW;
	    // NUMBERS OF FAMTS_NUMBERSOFSAP
	    public static final String FAMTS_NUMBERSOFSAP = MtkTvConfigType.CFG_MISC_EX_NUM_OF_SAP;
	    // NUMBERS OF FAMTS_SAP_THRESHOLD_HIGH
	    public static final String FAMTS_SAP_THRESHOLD_HIGH = MtkTvConfigType.CFG_MISC_EX_SAP_THRESHOLD_HIGH;
	    // NUMBERS OF FAMTS_SAP_THRESHOLD_HIGH
	    public static final String FAMTS_SAP_THRESHOLD_LOW = MtkTvConfigType.CFG_MISC_EX_SAP_THRESHOLD_LOW;

	    // NUMBERS OF HIGH deviation mode
	    public static final String FAMTS_HIGH_DEVIATION_MODE = MtkTvConfigType.CFG_MISC_EX_HIGH_DEVIATION_MODE;

	    // NUMBERS OF ARRIER_SHIFT_FUNCTION
	    public static final String FAMTS_CARRIER_SHIFT_FUNCTION = MtkTvConfigType.CFG_MISC_EX_CARRIER_SHIFT_FUNCTION;

	    // NUMBERS OF FM Stauration mute
	    public static final String FAMTS_FM_STAURATION_MODE = MtkTvConfigType.CFG_MISC_EX_FM_SATURATION_MUTE;
	    // NUMBERS OF FM Carrier mute
	    public static final String FAMTS_FM_CARRIER_MUTE_MODE = MtkTvConfigType.CFG_MISC_EX_FM_CARRIER_MUTE_MODE;
	    // NUMBERS OF FM Carrier mute threshold high
	    public static final String FAMTS_FM_CARRIER_MUTE_THRESHOLD_HIGH = MtkTvConfigType.CFG_MISC_EX_FM_CARRIER_MUTE_THRESHOLD_HIGH;
	    // NUMBERS OF FM Carrier mute threshold low
	    public static final String FAMTS_FM_CARRIER_MUTE_THRESHOLD_LOW = MtkTvConfigType.CFG_MISC_EX_FM_CARRIER_MUTE_THRESHOLD_LOW;

	    // NUMBERS OF Mono Stero Fine Tune Volume
	    public static final String FAMTS_MONO_STERO_FINE_TUNE_VOLUME = MtkTvConfigType.CFG_MISC_EX_MONO_STERO_FINE_TUNE_VOLUME;
	    // NUMBERS OF Mono Stero Fine Tune Volume
	    public static final String FAMTS_SAP_FINE_TUNE_VOLUME = MtkTvConfigType.CFG_MISC_EX_SAP_FINE_TUNE_VOLUME;

	    /*
	     * factory_audio_A2_system
	     */
	    // NUMBERS OF CHECK
	    public static final String FAA2_NUMBERSOFCHECK = MtkTvConfigType.CFG_MISC_EX_A2_SYS_NUM_CHECK;
	    // NUMBERS OF DOUBLE
	    public static final String FAA2_NUMBERSOFDOUBLE = MtkTvConfigType.CFG_MISC_EX_A2_SYS_NUM_DOUBLE;
	    // MONO WEIGHT
	    public static final String FAA2_MONOWEIGHT = MtkTvConfigType.CFG_MISC_EX_A2_SYS_NOMO_WIGHT;
	    // STEREO WEIGHT
	    public static final String FAA2_STEREOWEIGHT = MtkTvConfigType.CFG_MISC_EX_A2_SYS_STERO_WEIGHT;
	    // DUAL WEIGHT
	    public static final String FAA2_DUALWEIGHT = MtkTvConfigType.CFG_MISC_EX_A2_SYS_DUAL_WEIGHT;
	    // HIGH DEVIATION MODE
	    public static final String FAA2_HIGHDEVIATIONMODE = MtkTvConfigType.CFG_MISC_EX_A2_SYS_HIGHT_DVTN_MODE;
	    // CARRIER SHIFT FUNCTION
	    public static final String FAA2_CARRIERSHIFTFUNCTION = MtkTvConfigType.CFG_MISC_EX_A2_SYS_CRER_SHIIFT_FUN;
	    // FM CARRIER MUTE MODE
	    public static final String FAA2_FMCARRIERMUTEMODE = MtkTvConfigType.CFG_MISC_EX_A2_SYS_FM_CRER_MUTE_MODE;
	    // FM CARRIER MUTE THRESHOLD HIGH
	    public static final String FAA2_FMCARRIERMUTETHRESHOLDHIGH = MtkTvConfigType.CFG_MISC_EX_A2_SYS_FM_CRER_MUTE_THSHD_HIGHT;
	    // FM CARRIER MUTE THRESHOLD LOW
	    public static final String FAA2_FMCARRIERMUTETHRESHOLDLOW = MtkTvConfigType.CFG_MISC_EX_A2_SYS_FM_CRER_MUTE_THSHD_LOW;
	    // FINE TUNE VOLUME
	    public static final String FAA2_FINETUNEVOLUME = MtkTvConfigType.CFG_MISC_EX_A2_SYS_FINE_TUNE_VOLUME;

	    /*
	     * factory_audio_pal_system
	     */
	    // CORRECT THRESHOLD
	    public static final String FAPAL_CORRECTTHRESHOLD = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_CORRECT_THSHD;
	    // TOTAL SYNC LOOP
	    public static final String FAPAL_TOTALSYNCLOOP = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_TOTAL_SYNC_LOOP;
	    // ERROR THRESHOLD
	    public static final String FAPAL_ERRORTHRESHOLD =MtkTvConfigType.CFG_MISC_EX_PAL_SYS_ERROR_THSHD;
	    // PARITY ERROR THRESHOLD
	    public static final String FAPAL_PARITYERRORTHRESHOLD = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_PARITY_ERROR_THSHD;
	    // EVERY NUMBER FRAMES
	    public static final String FAPAL_EVERYNUMBERFRAMES = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_EVERY_NUMBER_FRAMES;
	    // HIGH DEVIATION MODE
	    public static final String FAPAL_HIGHDEVIATIONMODE = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_HIGH_DEVIATION_MODE;
	    // AM CARRIER MUTE MODE
	    public static final String FAPAL_AMCARRIERMUTEMODE = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_AM_CARRIER_MUTE_MODE;
	    // AM CARRIER MUTE THRESHOLD HIGH
	    public static final String FAPAL_AMCARRIERMUTETHRESHOLDHIGH = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_AM_CARRIER_MUTE_THSHD_HIGH;
	    // AM CARRIER MUTE THRESHOLD LOW
	    public static final String FAPAL_AMCARRIERMUTETHRESHOLDLOW = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_AM_CARRIER_MUTE_THSHD_LOW;
	    // CARRIER SHIFT FUNCTION
	    public static final String FAPAL_CARRIERSHIFTFUNCTION = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_CARRIER_SHIFT_FUN;
	    // FM CARRIER MUTE MODE
	    public static final String FAPAL_FMCARRIERMUTEMODE = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_FM_CARRIER_MUTE_MODE;
	    // FM CARRIER MUTE THRESHOLD HIGH
	    public static final String FAPAL_FMCARRIERMUTETHRESHOLDHIGH = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_FM_CARRIER_MUTE_THSHD_HIGH;
	    // FM CARRIER MUTE THRESHOLD LOW
	    public static final String FAPAL_FMCARRIERMUTETHRESHOLDLOW = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_FM_CARRIER_MUTE_THSHD_LOW;
	    // PAL FINE TUNE VOLUME
	    public static final String FAPAL_PALFINETUNEVOLUME = MtkTvConfigType.CFG_MISC_EX_PAL_SYS_PAL_FINE_TUNE_VOL;
	    // AM FINE TUNE VOLUME
	    public static final String FAPAL_AMFINETUNEVOLUME =  MtkTvConfigType.CFG_MISC_EX_PAL_SYS_AM_FINE_TUNE_VOL;
	    // NICAM FINE TUNE VOLUME
	    public static final String FAPAL_NICAMFINETUNEVOLUME =  MtkTvConfigType.CFG_MISC_EX_PAL_SYS_NICAM_FINE_TUNE_VOL;

	    /*
	     * factory_audio_EU_system
	     */
	    // EU FM Saturation Mute
	    public static final String FAEU_FM = MtkTvConfigType.CFG_MISC_EX_FM_SATURATION_MUTE;
	    // EU FM EU NON EU SYSTEM
	    public static final String FAEU_EU_NON = "UNDEFINE_FAEU_EU_NON";

	    /** Audio */
	    public static final String BALANCE = MtkTvConfigType.CFG_AUD_AUD_BALANCE;
	    public static final String BASS = MtkTvConfigType.CFG_AUD_AUD_BASS;
	    public static final String SRS_MODE = MtkTvConfigType.CFG_AUD_AUD_SURROUND;
		public static final String AUTO_VOLUME = MtkTvConfigType.CFG_AUD_AUTO_VOLUME;
		public static final String AD_VOLUME = MtkTvConfigType.CFG_AUD_AUD_AD_VOLUME;
	    public static final String EQUALIZE = MtkTvConfigType.CFG_AUD_AUD_EQUALIZER;
	    public static final String SPEAKER_MODE = MtkTvConfigType.CFG_AUD_AUD_OUT_PORT;//fix cr DTV576838,not CFG_AUD_AUD_AD_VOLUME
	    public static final String SPDIF_MODE = MtkTvConfigType.CFG_AUD_SPDIF;
	    public static final String SPDIF_DELAY = MtkTvConfigType.CFG_AUD_SPDIF_DELAY;
	    public static final String AVCMODE = MtkTvConfigType.CFG_AUD_AGC;
	    public static final String TYPE = MtkTvConfigType.CFG_AUD_AUD_TYPE;

	    public static final String DOWNMIX_MODE = MtkTvConfigType.CFG_AUD_DOLBY_DMIX;
	    // Treble high-pitched voice
	    public static final String TREBLE = MtkTvConfigType.CFG_AUD_AUD_TREBLE;
	    // Speaker volume
	    public static final String VISUALLY_SPEAKER = MtkTvConfigType.CFG_AUD_AUD_AD_SPEAKER;
	    // Headphone volume
	    public static final String VISUALLY_HEADPHONE = MtkTvConfigType.CFG_AUD_AUD_AD_HDPHONE;
	    public static final String VISUALLY_VOLUME = MtkTvConfigType.CFG_AUD_AUD_AD_VOLUME;// TODO
	    public static final String VISUALLY_PAN_FADE = MtkTvConfigType.CFG_AUD_AUD_AD_FADE_PAN;// TODO
	    public static final String VISUALLY_IMPAIRED_AUDIO = "VISUALLY_IMPAIRED_AUDIO";// TODO
	    // Visually Impaired
	    public static final String VISUALLY_IMPAIRED = "SUB_VISUALLYIMPAIRED";
	    public static final String SOUND_TRACKS = MtkTvConfigType.CFG_MENU_SOUNDTRACKS;
	    public static final String SOUNDTRACKS_GET_ENABLE = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_GET_ENABLE;
	    public static final String SOUNDTRACKS_GET_TOTAL = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_GET_TOTAL;
	    public static final String SOUNDTRACKS_SET_INIT = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_SET_INIT;
	    public static final String SOUNDTRACKS_SET_DEINIT = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_SET_DEINIT;
	    public static final String SOUNDTRACKS_GET_CURRENT = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_GET_CURRENT;
	    public static final String SOUNDTRACKS_GET_STRING = "soundtracksgetstring";
	    public static final String SOUNDTRACKS_SET_SELECT = MtkTvConfigType.CFG_MENU_SOUNDTRACKS_SET_SELECT;

	    public static final String CFG_MENU_AUDIOINFO                        ="g_menu__audioinfo";//MtkTvConfigType.CFG_MENU_AUDIOINFO;
	    public static final String CFG_MENU_AUDIOINFO_SET_INIT               ="g_menu__audioinfoinit";//MtkTvConfigType.CFG_MENU_AUDIOINFO_SET_INIT;
	    public static final String CFG_MENU_AUDIOINFO_SET_DEINIT             ="g_menu__audioinfodeinit";//MtkTvConfigType.CFG_MENU_AUDIOINFO_SET_DEINIT;
	    public static final String CFG_MENU_AUDIOINFO_SET_SELECT             ="g_menu__audioinfoselect";//MtkTvConfigType.CFG_MENU_AUDIOINFO_SET_SELECT;
	    public static final String CFG_MENU_AUDIOINFO_GET_TOTAL              ="g_menu__audioinfototal";//MtkTvConfigType.CFG_MENU_AUDIOINFO_GET_TOTAL;
	    public static final String CFG_MENU_AUDIOINFO_GET_CURRENT            ="g_menu__audioinfocurrent";//MtkTvConfigType.CFG_MENU_AUDIOINFO_GET_CURRENT;
	    //public static final String CFG_MENU_AUDIOINFO_GET_STRING             ="g_menu__audioinfostring";//MtkTvConfigType.CFG_MENU_AUDIOINFO_GET_STRING;
	    public static final String CFG_MENU_AUDIOINFO_GET_STRING             ="audioinfogetstring";

	    public static final String CFG_AUD_AUD_BBE_MODE = MtkTvConfigType.CFG_AUD_AUD_BBE_MODE;

	    /** TV */
		public static final String BRDCST_TYPE = MtkTvConfigTypeBase.CFG_BS_BS_BRDCST_TYPE;
	    public static final String TUNER_MODE = MtkTvConfigType.CFG_BS_BS_SRC;
		public static final String ANTENNA_POWER_5V = "5V Antenna Power";
		public static final String TUNER_MODE_CN_USER_SET = MtkTvConfigType.CFG_BS_BS_USER_SRC;
	    public static final String SVL_ID = MtkTvConfigTypeBase.CFG_BS_SVL_ID;
	    public static final String TUNER_MODE_PREFER_SAT = MtkTvConfigTypeBase.CFG_TWO_SAT_CHLIST_PREFERRED_SAT;
	    public static final String COUNTRY_REGION_ID = MtkTvConfigType.CFG_COUNTRY_COUNTRY_RID;
	    public static final String TV_MTS_MODE = MtkTvConfigType.CFG_AUD_AUD_MTS;// TVConfigurer.MENU_MTS_OPTION;
	    public static final String TV_AUDIO_LANGUAGE = MtkTvConfigType.CFG_GUI_AUD_LANG_AUD_LANGUAGE;
	    public static final String CFG_MENU_AUDIO_LANGUAGE_ATTR = MtkTvConfigType.CFG_MENU_AUDIO_LANGUAGE_ATTR;
	    public static final String CFG_MENU_AUDIO_AD_TYP = MtkTvConfigType.CFG_MENU_AUDIO_AD_TYP;
	    public static final String TV_AUDIO_LANGUAGE_2 = MtkTvConfigType.CFG_GUI_AUD_LANG_AUD_2ND_LANGUAGE;
	    public static final String TV_SYSTEM = "SCAN_OPTION_TV_SYSTEM";// TVScanner.SCAN_OPTION_TV_SYSTEM;
	    public static final String COLOR_SYSTEM = "SCAN_OPTION_COLOR_SYSTEM";// TVScanner.SCAN_OPTION_COLOR_SYSTEM;
	    public static final String SCAN_MODE = MtkTvConfigType.CFG_SCAN_MODE_SCAN_MODE;
	    public static final String SCAN_MODE_DVBC = "cfg_scan_mode_scan_mode_dvbc";
	    public static final String SYM_RATE = "dvbc_single_rf_scan_sym_rate";// TVScanner.SCAN_OPTION_SYM_RATE;
	    public static final String TV_FREEZE_CHANNEL = MtkTvConfigType.CFG_MENU_CH_FRZ_CHG;
	    public static final String DTV_TSHIFT_OPTION = "DTV_TSHIFT_OPTION";
	    public static final String DTV_DEVICE_INFO = "DTV_DEVICE_INFO";
	    public static final String US_SCAN_MODE = "us"+MtkTvConfigType.CFG_SCAN_MODE_SCAN_MODE;
	    public static final String CHANNEL_LIST_TYPE = MtkTvConfigType.CFG_MISC_CH_LST_TYPE;
	    public static final String CHANNEL_LIST_SLOT = MtkTvConfigType.CFG_MISC_CH_LST_SLOT;
	    public static final String CHANNEL_CAM_PROFILE_SCAN = "g_misc__cam_profile_scan";
	    public static final String FREQUENEY_PLAN = "US_single_RF_plan";// MtkTvConfigType.CFG_BS_BS_PLN
	    public static final String FAV_US_RANGE_FROM_CHANNEL = "US_range_frome_channel";// TVScanner.SCAN_OPTION_SYM_RATE;
	    public static final String FAV_US_RANGE_TO_CHANNEL = "US_range_to_channel";
	    public static final String FAV_US_SINGLE_RF_CHANNEL = "US_single_rf_channel";
	    public static final String FAV_SA_SINGLE_RF_CHANNEL = "SA_single_rf_channel";

	    public static final String DVBC_SINGLE_RF_SCAN_FREQ = "dvbc_single_rf_scan_freq";
	    public static final String DVBC_SINGLE_RF_SCAN_MODULATION = "dvbc_single_rf_scan_modulation";
	    public static final String SCHEDULE_PVR_SRCTYPE = "SCHEDULE_PVR_SRCTYPE";
	    public static final String SCHEDULE_PVR_CHANNELLIST = "SCHEDULE_PVR_CHANNELLIST";
	    public static final String SCHEDULE_PVR_REMINDER_TYPE = "SCHEDULE_PVR_REMINDER_TYPE";
	    public static final String SCHEDULE_PVR_REPEAT_TYPE = "SCHEDULE_PVR_REPEAT_TYPE";
	    /** setup */
	    public static final String OSD_LANGUAGE = MtkTvConfigType.CFG_GUI_LANG_GUI_LANGUAGE;// "SETUP_osd_language";
	    public static final String SCREEN_MODE = MtkTvConfigType.CFG_VIDEO_SCREEN_MODE;
	    public static final String DPMS = MtkTvConfigType.CFG_MISC_DPMS;
	    public static final String WAKEUP_VGA = MtkTvConfigType.CFG_MISC_WAKEUP_REASON;
	    public static final String BLUE_MUTE = MtkTvConfigType.CFG_VIDEO_VID_BLUE_MUTE;
	    public static final String POWER_ON_MUSIC = "POWER_ON_MUSIC";
	    public static final String POWER_OFF_MUSIC = "POWER_OFF_MUSIC";
	    public static final String CAPTURE_LOGO_SELECT = "SETUP_capture_logo";
	    public static final String FAST_BOOT = "SETUP_fast_boot";
	    public static final String GINGA_ENABLE = MtkTvConfigType.CFG_GINGA_GINGA_ENABLE;
	    public static final String AUTO_START_APPLICATION = MtkTvConfigType.CFG_GINGA_GINGA_AUTO_START;
	    public static final String MODE_LIST_STYLE ="SETUP_sundry_mode_style";
	    public static final String MODE_DMR_CONTROL ="SETUP_dmr_contrl";
	   //for EU
	    public static final String INTERACTION_CHANNEL = MtkTvConfigType.CFG_MISC_MHEG_INTER_CH;
	    public static final String MHEG_PIN_PROTECTION = MtkTvConfigType.CFG_MISC_MHEG_PIN_PROTECTION;
	    public static final String HBBTV_SUPPORT = MtkTvConfigType.CFG_MENU_HBBTV;

	    //for oceania
	    public static final String OCEANIA_FREEVIEW = MtkTvConfigType.CFG_MISC_FREEVIEW_MODE;
	    public static final String OCEANIA_POSTAL = MtkTvConfigType.CFG_EAS_LCT_ST;

	    public static final String SETUP_US_TIME_ZONE = "SETUP_us_time_zone";
	    public static final String SETUP_TIME_ZONE = "SETUP_time_zone";
	    public static final String SETUP_US_SUB_TIME_ZONE = MtkTvConfigType.CFG_TIME_ZONE;
	    public static final String SETUP_TIME_SET = "SETUP_time_set";
	    public static final String AUTO_SYNC = "SETUP_auto_syn";
	    public static final String POWER_ON_TIMER = MtkTvConfigType.CFG_TIMER_TIMER_ON;
	    public static final String SETUP_POWER_ON_CH = "SETUP_PowerOnCh";
	    public static final String POWER_OFF_TIMER = MtkTvConfigType.CFG_TIMER_TIMER_OFF;
	    public static final String POWER_ON_CH_CABLE_MODE = MtkTvConfigType.CFG_NAV_CABLE_ON_TIME_CH;
	    public static final String POWER_ON_CH_AIR_MODE = MtkTvConfigType.CFG_NAV_AIR_ON_TIME_CH;
	    public static final String POWER_ON_VALID_CHANNELS = "SETUP_poweron_valid_channels";
	    public static final String SLEEP_TIMER = "SETUP_sleep_timer";
	    public static final String AUTO_SLEEP = MtkTvConfigType.CFG_MISC_AUTO_SLEEP;

	    public static final String TIME_DATE = "SETUP_date";
	    public static final String TIME_TIME = "SETUP_time";
	    public static final String TIMER1 = "SETUP_timer1";
	    public static final String TIMER2 = "SETUP_timer2";

	    public static final String TIME_START_DATE = "SETUP_start_date";
	    public static final String TIME_START_TIME = "SETUP_start_time";
	    public static final String TIME_END_DATE = "SETUP_end_date";
	    public static final String TIME_END_TIME = "SETUP_end_time";

	    public static final String ADDRESS_TYPE = "UNDEFINE_address_type";
	    public static final String DLNA = "SETUP_dlna";
	    public static final String MY_NET_PLACE = "SETUP_net_place";

	    public static final String CAPTION = "SETUP_caption_setup";
	    public static final String DIVX_REG = "SETUP_divx_reg";
	    public static final String DIVX_DEA = "SETUP_divx_dea";
	    public static final String SCART = MtkTvConfigType.CFG_SCARD_SCART;
	    public static final String SCART1 = MtkTvConfigType.CFG_SCARD_SCART0;
	    public static final String SCART2 = MtkTvConfigType.CFG_SCARD_SCART1;
	    public static final String GINGA_SETUP = "SETUP_ginga_setup";
	    public static final String COMMON_INTERFACE = "SETUP_common_interface";
	    public static final String SETUP_TIME_SETUP = "SETUP_time_setup";
	    public static final String SETUP_TELETEXT = "SETUP_teletext";
	    public static final String SETUP_NETWORK = "SETUP_network";
	    public static final String SETUP_POWER_ONCHANNEL_LIST = "SETUP_power_onchannel";
	    public static final String SETUP_DIGITAL_STYLE = "SETUP_digital_style";
	    public static final String SETUP_RECORD_SETTING = "SETUP_recordSetting";
	    public static final String SETUP_DEVICE_INFO = "SETUP_deivce_info";
	    public static final String SETUP_SCHEDUCE_LIST = "SETUP_schedule_list";
	    public static final String SETUP_OAD_SETTING = "SETUP_OADSetting";
	    public static final String SETUP_PIP_POP = "SETUP_pip_pop";
	    public static final String SETUP_PIP_POP_MODE = "SETUP_pip_pop_mode";
	    public static final String SETUP_PIP_POP_SOURCE = "SETUP_pip_pop_source";
	    public static final String SETUP_PIP_POP_POSITION = "SETUP_pip_pop_position";
	    public static final String SETUP_PIP_POP_SIZE = "SETUP_pip_pop_size";

	    //pvr timeshift start
	    public static final String PVR_START = "pvr_start";
	    public static final String TIMESHIFT_START = "timeshift_start";

	    public static final String SETUP_OAD_DETECT = "SETUP_oad_detect";
	    public static final String SETUP_OAD_SET_AUTO_DOWNLOAD = MtkTvConfigType.CFG_OAD_OAD_SEL_OPTIONS_AUTO_DOWNLOAD;
	    public static final String SETUP_DIGITAL_TELETEXT_LANGUAGE = MtkTvConfigType.CFG_TTX_LANG_TTX_DIGTL_ES_SELECT;
	    public static final String SETUP_DECODING_PAGE_LANGUAGE = MtkTvConfigType.CFG_TTX_LANG_TTX_DECODE_LANG;
	    public static final String SETUP_TTX_PRESENTATION_LEVEL = MtkTvConfigType.CFG_TTX_LANG_TTX_PRESENTATION_LEVEL;

	    public static final String SETUP_ENABLE_CAPTION = MtkTvConfigType.CFG_CC_CC_CAPTION;//fix CR DTV00581238
	    public static final String SETUP_ANALOG_CAPTION = MtkTvConfigType.CFG_CC_ANALOG_CC;
	    public static final String SETUP_DIGITAL_CAPTION = MtkTvConfigType.CFG_CC_DIGITAL_CC;
	    public static final String SETUP_SUPERIMPOSE_SETUP = MtkTvConfigType.CFG_CC_CC_SI;
	    public static final String SETUP_CAPTION_STYLE = MtkTvConfigType.CFG_CC_DCS;// no
	    public static final String SETUP_FONT_SIZE = MtkTvConfigType.CFG_CC_DISP_OPT_FT_SIZE;
	    public static final String SETUP_FONT_STYLE = MtkTvConfigType.CFG_CC_DISP_OPT_FT_STYLE;
	    public static final String SETUP_FONT_COLOR = MtkTvConfigType.CFG_CC_DISP_OPT_FT_COLOR;
	    public static final String SETUP_FONT_OPACITY = MtkTvConfigType.CFG_CC_DISP_OPT_FT_OPACITY;
	    public static final String SETUP_BACKGROUND_COLOR = MtkTvConfigType.CFG_CC_DISP_OPT_BK_COLOR;
	    public static final String SETUP_BACKGROUND_OPACITY = MtkTvConfigType.CFG_CC_DISP_OPT_BK_OPACITY;
	    public static final String SETUP_WINDOW_COLOR = MtkTvConfigType.CFG_CC_DISP_OPT_WIN_COLOR;
	    public static final String SETUP_WINDOW_OPACITY = MtkTvConfigType.CFG_CC_DISP_OPT_WIN_OPACITY;
	    public static final String SETUP_SHIFTING_MODE = MtkTvConfigType.CFG_RECORD_REC_TSHIFT_MODE;
	    public static final String SETUP_RECORD_MODE = MtkTvConfigType.CFG_RECORD_AV_REC_MODE;
	    public static final String SETUP_RECORD_QUALITY = MtkTvConfigType.CFG_VIDEO_VID_REC_QUALITY;
	    public static final String DTV_SCHEDULE_LIST = "DTV_SCHEDULE_LIST";
	    public static final String LICENSE_INFO = "SETUP_license_info";
	    public static final String SYSTEM_INFORMATION = "SETUP_system_information";
	    public static final String VERSION_INFO = "SETUP_version_info";
	    public static final String SETUP_MSI = "g_misc__msi";
	    public static final String RESET_DEFAULT = "RESET_DEFAULT";
	    public static final String SETUP_POSTAL_CODE = "SETUP_postal_code";
	    public static final String DOWNLOAD_FIRMWARE = "download_firmware";
	    public static final String BISS_KEY = "biss_key";
	    public static final String BISS_KEY_ITEM = "biss_key_item";
	    public static final String BISS_KEY_ITEM_ADD = "biss_key_item_add";
	    public static final String BISS_KEY_ITEM_SAVE = "biss_key_item_save";
	    public static final String BISS_KEY_ITEM_UPDATE = "biss_key_item_update";
	    public static final String BISS_KEY_ITEM_DELETE = "biss_key_item_delete";

	    public static final String BISS_KEY_FREQ = "biss_freqency";
	    public static final String BISS_KEY_SYMBOL_RATE = "biss_sysbol_rate";
	    public static final String BISS_KEY_POLAZATION = "biss_key_polazation";
	    public static final String BISS_KEY_SVC_ID = "biss_key_sevice_id";
	    public static final String BISS_KEY_CW_KEY = "biss_key_cw_key";
	    public static final int BISS_KEY_SYMBOL_RATE_MIN = 2000;
	    public static final int BISS_KEY_SYMBOL_RATE_MAX = 45000;
	    public static final int BISS_KEY_FREQ_MIN = 3000;
	    public static final int BISS_KEY_FREQ_MAX = 13000;
	    public static final int BISS_KEY_SVC_ID_MIN = 0;
	    public static final int BISS_KEY_SVC_ID_MAX = 0xFFFF;
	    /*which biss key operate*/
	    public static final int BISS_KEY_OPERATE_ADD = 0x00F1;
	    public static final int BISS_KEY_OPERATE_UPDATE = 0x00F2;
	    public static final int BISS_KEY_OPERATE_DELETE = 0x00F3;
  /**
   * TKGS config ID
   */
  public static String TKGS_FAC_SETUP_AVAIL_CONDITION =
    MtkTvConfigType.CFG_MISC_TKGS_AVAILABILITY_COND;
  public static final String TKGS_SETTING = "tkgs_setting";
  public static final String TKGS_OPER_MODE = MtkTvConfigType.CFG_MISC_TKGS_OPERATING_MODE;
  public static final String TKGS_HIDD_LOCS = "tkgs_hidden_locs";
  public static final String TKGS_RESET_TAB_VERSION = "tkgs_reset_tab_version";
  public static final String TKGS_PREFER_LIST = "tkgs_prefer_list";
  public static final String TKGS_LOC_LIST = "tkgs_loc_list";
  public static final String TKGS_LOC_ITEM = "tkgs_loc_item";
  public static final String TKGS_LOC_ITEM_ADD = "tkgs_loc_item_add";
  public static final String TKGS_LOC_ITEM_SAVE = "tkgs_loc_item_save";
  public static final String TKGS_LOC_ITEM_UPDATE = "tkgs_loc_item_update";
  public static final String TKGS_LOC_ITEM_DELETE = "tkgs_loc_item_delete";
  public static final String TKGS_LOC_ITEM_HIDD_CLEANALL = "tkgs_loc_item_clean_all_hidd";

  public static final String TKGS_LOC_FREQ = "tkgs_loc_freqency";
  public static final String TKGS_LOC_SYMBOL_RATE = "tkgs_loc_sysbol_rate";
  public static final String TKGS_LOC_POLAZATION = "tkgs_loc_polazation";
  public static final String TKGS_LOC_SVC_ID = "tkgs_loc_sevice_id";

  public static final int TKGS_LOC_SVC_ID_MIN = 0;
  public static final int TKGS_LOC_SVC_ID_MAX = 8191;
  // TKGS operate type
  public static final int TKGS_LOC_OPERATE_ADD = 0x00E1;
  public static final int TKGS_LOC_OPERATE_UPDATE = 0x00E2;
  public static final int TKGS_LOC_OPERATE_DELETE = 0x00E3;
	    /** having sub page items are defined here */
	    public static final String VIDEO_COLOR_TEMPERATURE = "colorTemprature";
	    public static final String VIDEO_ADVANCED_VIDEO = "advancedVideo";
	    public static final String TV_CHANNEL = "tv_channel";
	    public static final String TV_EU_CHANNEL = "tveuChannel";
	    public static final String TV_CHANNEL_SCAN = "channel_scan";
		public static final String TV_CHANNEL_SCAN_DVBT = "channel_scan_dvbt_full";
		public static final String TV_CHANNEL_SCAN_DVBC = "channel_scan_dvbc_fulls";
		public static final String TV_CHANNEL_AFTER_SCAN_UK_REGION = "tv_channel_after_scan_UK_region";
		public static final String TV_CHANNEL_SCAN_DVBC_OPERATOR = "channel_scan_dvbc_fulls_operator";
		public static final String TV_UPDATE_SCAN_DVBT_UPDATE = "channel_scan_dvbt_UPDATE";
	    public static final String TV_UPDATE_SCAN = "update_scan";
	    public static final String TV_ANALOG_SCAN = "analog_scan";
	    public static final String TV_SINGLE_RF_SCAN_CN = "single_rf_scan_cn";
	    public static final String TV_DVBT_SINGLE_RF_SCAN = "tv_dvbt_single_rf_scan";
	    public static final String TV_DVBC_SINGLE_RF_SCAN = "tv_dvbc_single_rf_scan";
	    public static final String TV_CI_CAM_SCAN = "tv_ci_cam__scan";
	    public static final String TV_CHANNEL_EDIT = "channel_edit";
	    public static final String TV_SA_CHANNEL_EDIT = "channel_sa_edit";
	    public static final String TV_CHANNEL_CLEAR = "channel_clean";
	    public static final String SETUP_UPGRADENET = "SETUP_upgradeNet";
	    public static final String SETUP_APPLICATION = "application";
	    public static final String SETUP_WOW = "WOW";
		public static final String SETUP_WOL = "WOL";
	    public static final String TV_CHANNEL_SKIP = "channel_skip";
	    public static final String TV_CHANNEL_SORT = "channel_sort";
	    public static final String TV_CHANNEL_DECODE = "channel_decode";
	    public static final String TV_CHANNEL_DECODE_LIST = "channel_decode_list";

	    public static final String TV_CHANNEL_START_FREQUENCY = "UNDEFINE_channel_start_frequency";
	    public static final String TV_CHANNEL_END_FREQUENCY = "UNDEFINE_channel_end_frequency";
	    public static final String TV_CHANNEL_STARTSCAN = "start_scan";
	    public static final String TV_CHANNEL_STARTSCAN_CEC_CN = "start_scan_dvbc_cn";
	    public static final String TV_CHANNEL_EDIT_LIST = "UNDEFINE_channel_edit_list";
	    public static final String TV_CHANNEL_INACTIVE_LIST = "inactive_channel_list";
	    public static final String TV_CHANNELFINE_TUNE_EDIT_LIST = "UNDEFINE_ChannelFine Tune";
	    public static final String TV_CHANNEL_SORT_CHANNELLIST = "tv_channel_sort_channellist";
	    public static final String TV_CHANNEL_NW_NAME = "UNDEFINE_channel_nw_name";
	    public static final String TV_CHANNEL_NW_ANALOG_NAME = "UNDEFINE_channel_nw_analog_name";
	    public static final String TV_CHANNEL_NO = "UNDEFINE_channel_edit_no";
	    public static final String TV_CHANNEL_NAME = "UNDEFINE_channel_edit_name";
	    public static final String TV_FREQ = "UNDEFINE_channel_edit_frequency";
		public static final String TV_SINGLE_RF_SCAN_CHANNELS = "single_rf_scan_rf_channel";
		public static final String TV_DVBC_CHANNELS_START_SCAN = "dvbc_scan_channel_start";

	    public static final String TV_CHANNEL_SA_TSNAME = "UNDEFINE_channel_edit_sa_tsname";
	    public static final String TV_CHANNEL_SA_NO = "UNDEFINE_channel_edit_sa_no";
	    public static final String TV_CHANNEL_SA_NAME = "UNDEFINE_channel_edit_sa_name";
	    public static final String TV_FREQ_SA = "UNDEFINE_channel_edit_frequency_sa";

	    public static final String TV_CHANNEL_COLOR_SYSTEM = "CHANNELEDIT_color_system";
	    public static final String TV_SOUND_SYSTEM = "CHANNELEDIT_sound_system";
	    public static final String TV_AUTO_FINETUNE = "UNDEFINE_channel_edit_aft";
	    public static final String TV_CHANNELFINE_TUNE = "Analog ChannelFine Tune";
	    public static final String TV_FINETUNE = "channel_edit_finetune";
	    public static final String TV_SKIP = "UNDEFINE_channel_edit_skip";
	    public static final String TV_CHANNEL_SKIP_CHANNELLIST = "tv_channel_skip_channellist";
	    public static final String TV_STORE = "channel_edit_store";
		public static final String TV_ANANLOG_SCAN_UP = "scan_up";
		public static final String TV_ANANLOG_SCAN_DOWN = "scan_down";
		public static final String TV_CHANNEL_SATELLITE_ADD = "Satellite Add";
	    /** DTMB Single RF Channel */
	    public static final String TV_SINGLE_SCAN_RF_CHANNEL = "UNDEFINE_tv_single_rf_channel";
	    public static final String TV_SINGLE_SCAN_SIGNAL_LEVEL = "UNDEFINE_tv_single_scan_signal_level";
	    public static final String TV_SINGLE_SCAN_MODULATION = "UNDEFINE_tv_single_scan_modu";
	    public static final String TV_SINGLE_SCAN_SIGNAL_QUALITY = "UNDEFINE_tv_singl_scan_signal_quality";
	    /** Parental Part */
	    public static final String PARENTAL_PASSWORD = "parental_password";
	    public static final String PARENTAL_CHANNEL_BLOCK = "parental_channel_block";
	    public static final String PARENTAL_TIME_INTERVAL_BLOCK = "parental_time_interval_block";
	    public static final String PARENTAL_PROGRAM_BLOCK = "parental_program_block";
	    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK = "parental_channel_schedule_block";

	    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK_OPERATION_MODE = "parental_channel_schedule_block_MOde";

	    public static final String PARENTAL_US_TV_RATINGS = "parental_us_tv_ratings";
	    public static final String PARENTAL_US_MOVIE_RATINGS = "parental_us_movie_ratings";
	    public static final String PARENTAL_CANADIAN_ENGLISH_RATINGS = "parental_canadian_english_ratings";
	    public static final String PARENTAL_CANADIAN_FRENCH_RATINGS = "parental_canadian_french_ratings";
	    public static final String PARENTAL_AGE_RATINGS = "PARENTAL_AGE_RATINGS";// TVConfigurer.MENU_MTS_OPTION;
	    public static final String PARENTAL_AGE_RATINGS_EU = "PARENTAL_AGE_RATINGS_EU";
	    public static final String PARENTAL_AGE_RATINGS_EU_OCEANIA_AUS = "PARENTAL_AGE_RATINGS_EU_EU_OCEANIA_AUS";
	    public static final String PARENTAL_CONTENT_RATINGS = "PARENTAL_CONTENT_RATINGS";// TVConfigurer.MENU_MTS_OPTION;
	    public static final String PARENTAL_OPEN_VCHIP = "parental_open_vchip";// MtkTvConfigType.CFG_RATING_VCHIP_CA;
	    public static final String PARENTAL_BLOCK_UNRATED = "parental_block_unrated";// MtkTvConfigType.CFG_DPMS;
	    public static final String PARENTAL_RATINGS_ENABLE = "parental_ratings_enable";// MtkTvConfigType.CFG_DPMS;
	    public static final String PARENTAL_INPUT_BLOCK = "parental_input_block";
	    public static final String PARENTAL_INPUT_BLOCK_SOURCE = "UNDEFINE_parental_input_block_source";
	    public static final String PARENTAL_CHANGE_PASSWORD = "parental_change_password";
	    public static final String PARENTAL_CLEAN_ALL = "parental_clean_all";
	    public static final String PARENTAL_PASSWORD_NEW = "parental_password_new";
	    public static final String PARENTAL_PASSWORD_NEW_RE = "parental_password_new_re";
	    public static final String PARENTAL_CHANNEL_BLOCK_CHANNELLIST = "parental_block_channellist";
	    public static final String PARENTAL_CHANNEL_SCHEDULE_BLOCK_CHANNELLIST = "parental_channel_schedule_block_channellist";
	    public static final String PARENTAL_OPEN_VCHIP_REGIN = "parental_open_vchip_regin";// MtkTvConfigType.CFG_RATING_VCHIP_CA;
	    public static final String PARENTAL_OPEN_VCHIP_DIM = "parental_open_vchip_regin_dim";// MtkTvConfigType.CFG_RATING_VCHIP_CA;
	    public static final String PARENTAL_OPEN_VCHIP_LEVEL = "parental_open_vchip_regin_level";// MtkTvConfigType.CFG_RATING_VCHIP_CA;
            public static final String PARENTAL_TIF_CONTENT_RATGINS = "parental_tif_content_ratings";
            public static final String PARENTAL_TIF_CONTENT_RATGINS_SYSTEM = "parental_tif_content_ratings_system";

	    public static final String PARENTAL_CFG_RATING_BL_TYPE =  MtkTvConfigType.CFG_RATING_BL_TYPE;
	    public static final String PARENTAL_CFG_RATING_BL_START_TIME = MtkTvConfigType.CFG_RATING_BL_START_TIME;
	    public static final String PARENTAL_CFG_RATING_BL_END_TIME = MtkTvConfigType.CFG_RATING_BL_END_TIME;


	    /** Factory part */
	    public static final String FACTORY_VIDEO = "SUB_factory_video";
	    public static final String FACTORY_AUDIO = "SUB_factory_audio";
	    public static final String FACTORY_TV = "SUB_factory_TV";
	    public static final String FACTORY_SETUP = "SUB_factory_setup";
	    public static final String FACTORY_PRESET_CH = "SUB_preset_ch";

	    public static final String FACTORY_TV_RANGE_SCAN = "tuner_range_scan";
	    public static final String FACTORY_TV_RANGE_SCAN_DIG = "tuner_range_scan_dig";
	    public static final String FACTORY_TV_RANGE_SCAN_ANA = "tuner_range_scan_ana";
	    public static final String FACTORY_TV_SINGLE_RF_SCAN = "tuner_single_rf_scan";
	    public static final String FACTORY_TV_FACTORY_SCAN = "tuner_factory_scan";
	    public static final String FACTORY_TV_TUNER_DIAGNOSTIC = "tuner_diagnostic";
	    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_NOINFO = "tuner_diagnostic_noinfo";
	    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_VERSION = "tuner_diagnostic_version";
	    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_RF = "tuner_diagnostic_rf";
	    public static final String FACTORY_TV_TUNER_DIAGNOSTIC_LOCK = "tuner_diagnostic_lock";
	    public static final String FACTORY_SETUP_EVENT_FORM = MtkTvConfigType.CFG_MISC_EVT_FORM;
	    public static final String FACTORY_SETUP_BURNING_MODE = MtkTvConfigType.CFG_MISC_EX_BRUNING_MODE;
	    public static final String FACTORY_SETUP_UART_MODE = MtkTvConfigType.CFG_MISC_EX_UART_FACTORY_MODE;
	    public static final String FACTORY_SETUP_CLEAN_STORAGE = "factory_setup_clean_storage";
	    public static final String FACTORY_PRESET_CH_DUMP = "preset_ch_dump";
	    public static final String FACTORY_PRESET_CH_PRINT = "preset_ch_print";
	    public static final String FACTORY_PRESET_CH_RESTORE = "preset_ch_restore";
	    public static final String FACTORY_SETUP_CI_UPDATE = "factory_updateCi";
	    public static final String FACTORY_SETUP_CI_ERASE = "factory_eraseCi";
	    public static final String FACTORY_SETUP_CI_QUERY = "factory_queryCi";
	    public static final String FACTORY_SETUP_DATA_SERVICE_SUPPORT = "g_misc__fac_data_service";
	    public static final String FACTORY_SETUP_CAPTION = "UNDEFINE_mts_factory_setup_cap";
	    public static final String FACTORY_SETUP_EXTERN = "g_cc__cc_attr_ex_size_idx";//MtkTvConfigType.CFG_CC_CC_ATTR_EX_SIZE_IDX;
	    public static final String FACTORY_SETUP_EQUAL = "g_cc__cc_attr_equal_width_idx";//MtkTvConfigType.CFG_CC_CC_ATTR_EQUAL_WIDTH_IDX;
	    public static final String FACTORY_SETUP_AUTO = "g_cc__cc_attr_auto_line_feed_idx";//MtkTvConfigType.CFG_CC_CC_ATTR_AUTO_LINE_FEED_IDX;
	    public static final String FACTORY_SETUP_ROLL = "g_cc__cc_attr_roll_up_mode_idx";//MtkTvConfigType.CFG_CC_CC_ATTR_ROLL_UP_MODE_IDX;

	    /*DVBS*/
	    public static final String DVBS_SAT_PREFIX = "DVBS_SAT_";
	    public static final String DVBS_SAT_OP = "DVBS_SAT_OP";
  public static final String DVBS_SAT_ATENNA_TYPE_SET = "Satellite atenna type set";
  public static final String DVBS_SAT_ATENNA_TYPE = "Satellite atenna type";
  public static final String DVBS_SAT_ATENNA_TYPE_TUNER = "Satellite atenna type tuner";
  public static final String DVBS_SAT_ATENNA_TYPE_USERDEF = "Satellite atenna type user define";
  public static final String DVBS_SAT_ATENNA_TYPE_BANDFREQ = "Satellite atenna type band freq";
	    public static final String DVBS_SAT_RE_SCAN = "Satellite Re-scan";
	    public static final String DVBS_SAT_DEDATIL_INFO = "DVBS_SAT_DEDATIL_INFO";
	    public static final String DVBS_SAT_DEDATIL_INFO_ITEMS = "DVBS_SAT_DEDATIL_INFO_ITEMS";
	    public static final String DVBS_SAT_ADD = "Satellite Add";
	    public static final String DVBS_SAT_UPDATE_SCAN = "Satellite Update";
	    public static final String DVBS_SAT_MANUAL_TURNING = "DVBS_SAT_MANUAL_TURNING";
	    public static final String DVBS_SAT_DEDATIL_INFO_SCAN = "DVBS_SAT_DEDATIL_INFO_SCAN";
	    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN = "DVBS_SAT_DEDATIL_INFO_START_SCAN";
	    public static final String DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG = "DVBS_SAT_DEDATIL_INFO_START_SCAN_CONFIG";
	    public static final String DVBS_SAT_DEDATIL_INFO_TP_ITEMS = "DVBS_SAT_DEDATIL_INFO_TP_ITEMS";
	    public static final String DVBS_SAT_MANUAL_TURNING_TP = "DVBS_SAT_MANUAL_TURNING_TP";
	    public static final String DVBS_SAT_COMMON_TP = "DVBS_SAT_COMMON_TP";
	    public static final String DVBS_SIGNAL_QULITY = "DVBS_SIGNAL_QULITY";
	    public static final String DVBS_SIGNAL_LEVEL = "DVBS_SIGNAL_LEVEL";
	    public static final String DVBS_DETAIL_POSITION = "DVBS_DETAIL_POSITION";
	    public static final String DVBS_DETAIL_LNB_POWER = "DVBS_DETAIL_LNB_POWER";
	    public static final String DVBS_DETAIL_DISEQC12_SET = "DVBS_DETAIL_DISEQC12_SET";
	    public static final String DVBS_DETAIL_DISEQC10_PORT = "DVBS_DETAIL_DISEQC10_PORT";
	    public static final String DVBS_DETAIL_DISEQC11_PORT = "DVBS_DETAIL_DISEQC11_PORT";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR = "DVBS_DETAIL_DISEQC_MOTOR";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL = "DVBS_DETAIL_DISEQC_MOTOR_MOVEMENT_CONTROL";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS = "DVBS_DETAIL_DISEQC_MOTOR_DISABLE_LIMITS";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST = "DVBS_DETAIL_DISEQC_MOTOR_LIMIT_EAST";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST = "DVBS_DETAIL_DISEQC_MOTOR_LIMIT_WEST";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION = "DVBS_DETAIL_DISEQC_MOTOR_STORE_POSITION";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION = "DVBS_DETAIL_DISEQC_MOTOR_GOTO_POSITION";
	    public static final String DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE = "DVBS_DETAIL_DISEQC_MOTOR_GOTO_REFERENCE";
	    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE = "DVBS_DETAIL_DISEQC_MOVEMENT_STEP_SIZE";
	    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS = "DVBS_DETAIL_DISEQC_MOVEMENT_TIMEOUTS";
	    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST = "DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_EAST";
	    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST = "DVBS_DETAIL_DISEQC_MOVEMENT_MOVE_WEST";
	    public static final String DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT = "DVBS_DETAIL_DISEQC_MOVEMENT_STOP_MOVEMENT";
	    public static final int INVALID_VALUE = 10004;
	    public static final int STEP_VALUE = 1;
	    public static final int STEP_BIG_VALUE = 10;

	    /** wifi part */
	    public static final int WIFI_COMMON_BIND = 0;
	    public static final int WIFI_COMMON_NODONGLE = 1;
	    public static final int WIFI_COMMON_SCAN_FAIL = 2;
	    public static final int WIFI_COMMON_SCAN_INVALID = 3;
	    public static final int WIFI_COMMON_SCAN_SUCCESS = 4;
	    public static final int WIFI_COMMON_MANUAL_INVASSID = 6;
	    public static final int WIFI_COMMON_MANUAL_INVAPASS = 7;
	    public static final int WIFI_COMMON_MANUAL_SUCCESS = 8;
	    public static final int WIFI_COMMON_MANUAL_FAIL = 9;
	    // public static final int WIFI_COMMON_MANUAL_PASSERR = 10;
	    public static final int WIFI_COMMON_PIN_FAIL = 11;
	    public static final int WIFI_COMMON_PIN_SUCCESS = 12;
	    public static final int WIFI_COMMON_PBC_FAIL = 13;
	    public static final int WIFI_COMMON_PBC_SUCCESS = 14;
	    public static final int WIFI_COMMON_PBC_HINT = 15;
	    public static final int WIFI_COMMON_SCAN_TIMEOUT = 16;
	    public static final int WIFI_COMMON_MANUAL_TIMEOUT = 17;
	    public static final int WIFI_COMMON_NO_AP = 18;
	    public static final int WIFI_COMMON_NO_WPS_AP = 19;

	    public static final int WIFI_INPUT_SCAN_PASS = 0;
	    public static final int WIFI_INPUT_MANUAL_SSID = 1;
	    public static final int WIFI_INPUT_MANUAL_PASS = 2;

	    public static final int W_CONFIRM_UNKNOWN = 0;
	    public static final int W_CONFIRM_NONE = 1;
	    public static final int W_CONFIRM_WEP = 2;
	    public static final int W_CONFIRM_WPA_PSK_TKIP = 7;
	    public static final int W_CONFIRM_WPA_PSK_AES = 4;
	    public static final int W_CONFIRM_WPA2_PSK_TKIP = 5;
	    public static final int W_CONFIRM_WPA2_PSK_AES = 6;
	    public static final int W_CONFIRM_AUTO = 3;

	    public static final int W_SECURITY_NONE = 0;
	    public static final int W_SECURITY_WEP = 1;
	    public static final int W_SECURITY_TKIP = 2;
	    public static final int W_SECURITY_AES = 3;

	    public static final int WIFI_CONNECT_WPS_SCANING = 5;
	    public static final int WIFI_CONNECT_SCANING = 0;
	    public static final int WIFI_CONNECT_SCAN = 1;
	    public static final int WIFI_CONNECT_MANUAL = 2;
	    public static final int WIFI_CONNECT_PIN_AUTO = 3;
	    public static final int WIFI_CONNECT_PIN_AP = 4;
	    public static final int WIFI_CONNECT_PBC = 6;

	    public static final int WIFI_SCAN_NORMAL = 0;
	    public static final int WIFI_SCAN_WPS = 1;

	    public static final int FOCUS_OPTION_CHANGE_CHANNEL = 0;
		public static final String TV_DVBC_SCAN_FREQUENCY = "tv_dvbc_scan_frequency";
	    public static final String TV_DVBC_SCAN_NETWORKID = "tv_dvbc_scan_networkid";
	    public static int MAX_TIME_ZONE = 34;
	    public static HashMap<String,Integer> mScreenMode = new HashMap<String,Integer>();// MAX_TIME_ZONE = 34;
	    public static HashMap<Integer,String> mScreenModeReverse = new HashMap<Integer,String>();// MAX_TIME_ZONE = 34;
	    private String[] mScreenModeList;// = new HashMap<Integer,String>();// MAX_TIME_ZONE = 34;
	    public static int[] zoneValue = {
	            0,
	             0,
	            (1 * 3600),
	            (2 * 3600),
	            (3 * 3600),
	            (3 * 3600 + 30 * 60),
	            (4 * 3600),
	            (4 * 3600 + 30 * 60),
	            (5 * 3600),
	            (5 * 3600 + 30 * 60),
	            (5 * 3600 + 45 * 60),
	            (6 * 3600),
	            (6 * 3600 + 30 * 60),
	            (7 * 3600),
	            (8 * 3600),
	            (9 * 3600),
	            (9 * 3600 + 30 * 60),
	            (10 * 3600),
	            (11 * 3600),
	            (12 * 3600),
	            (12 * 3600 + 45 * 60),
	            (13 * 3600),
	            (-12 * 3600),
	            (-11 * 3600),
	            (-10 * 3600),
	            (-9 * 3600),
	            (-8 * 3600),
	            (-7 * 3600),
	            (-6 * 3600),
	            (-5 * 3600),
	            (-4 * 3600),
	            (-3 * 3600 + (-30) * 60),
	            (-3 * 3600),
	            (-2 * 3600),
	            (-1 * 3600)};
	    public ArrayList<Boolean> get3DConfig() {
	        ArrayList<Boolean> m3DList = new ArrayList<Boolean>();
	        boolean m3DModeFlag = false;
	        boolean m3DNavFlag = false;
	        boolean m3D2DFlag = false;
	        boolean m3DDepthFieldFlag = false;
	        boolean m3DProtrudeFlag = false;
	        boolean m3DDistanceFlag = false;
	        boolean m3DImgSafetyFlag = false;
	        boolean m3DLrSwitchFlag = false;
	        boolean m3DOsdDepthFlag = false;
	        m3DModeFlag = mTV.isConfigEnabled(VIDEO_3D_MODE);
	        m3DNavFlag = mTV.isConfigEnabled(VIDEO_3D_NAV);
	        m3D2DFlag = mTV.isConfigEnabled(VIDEO_3D_3T2);
	        m3DDepthFieldFlag = mTV.isConfigEnabled(VIDEO_3D_FIELD);
	        m3DProtrudeFlag = mTV.isConfigEnabled(VIDEO_3D_PROTRUDE);
	        m3DDistanceFlag = mTV.isConfigEnabled(VIDEO_3D_DISTANCE);
	        m3DImgSafetyFlag = mTV.isConfigEnabled(VIDEO_3D_IMG_SFTY);
	        m3DLrSwitchFlag = mTV.isConfigEnabled(VIDEO_3D_LF);
	        m3DOsdDepthFlag = mTV.isConfigEnabled(VIDEO_3D_OSD_DEPTH);

	        m3DList.add(m3DModeFlag);
	        m3DList.add(m3DNavFlag);
	        m3DList.add(m3D2DFlag);
	        m3DList.add(m3DDepthFieldFlag);
	        m3DList.add(m3DProtrudeFlag);
	        m3DList.add(m3DDistanceFlag);
	        m3DList.add(m3DImgSafetyFlag);
	        m3DList.add(m3DLrSwitchFlag);
	        m3DList.add(m3DOsdDepthFlag);
	        return m3DList;
	    }

	    public int getMin(String itemID) {
	        int min = 0;
	        if (mTV != null) {
	            min = mTV.getMinValue(itemID);
	        }
	        return min;
	    }

	    public int getScanMin(String itemID) {
	        int scanMin = 0;
	        if (mTV != null) {
	        	scanMin = mTV.getMinValue(itemID);
	        }
	        Log.d(TAG, "scanMin>>>" + scanMin);
	        return scanMin;
	    }

	    public int getMax(String itemID) {
	        if (itemID.equals(FA_LATENCY)) {
	            return 680;
	        }
	        int max = 0;
	        if (mTV != null) {
	            max = mTV.getMaxValue(itemID);
	        }
	        Log.d("TVContent1", "max:" + max + "itemID:" + itemID);
	        if (max == 0) {
//	            max = DataItem.getMax(itemID);temp for biaoqing
	        }
	        Log.d("TVContent", "max:" + max);
	        return max;

	    }

	    public int getScanMax(String itemID) {
	        int scanMax = 0;
	        if (mTV != null) {
	        	scanMax = mTV.getMaxValue(itemID);
	        }
	        Log.d(TAG, "scanMax>>>" + scanMax);
	        return scanMax;
	    }

	    public int getDefaultScan(String itemID) {
	    	int value = 0;
	        if (mTV != null) {
	            value = mTV.getConfigValue(itemID);
	        }
	        Log.d(TAG, "getDefaultScanvalue>>>" + value);
	        return value;
	    }

	    public String[] getSupporScreenMode(int[] array){
		    if(array == null){
			    return null;
			}
	    	mScreenModeList = new String[array.length];
	    	for(int i=0;i<array.length;i++){
	    		mScreenModeList[i] = mScreenModeReverse.get(array[i]);
	    	}
	    	return mScreenModeList;
	    }

	    //fix cr DTV00596941
	    public int getScreenMode(String[] screenMode,String itemID){
	        int value = 0;
	        if (mTV != null) {
	            value = mTV.getConfigValue(itemID);
	        }
	    	 String key = mScreenModeReverse.get(value);
	    	 for(int i=0;i<screenMode.length;i++){
	    		 if(key.equals(screenMode[i])){
	    			 value = i;
	    			 break;
	    		 }
	    	 }
	    	 return value;
	    }


	    public int getDefault(String itemID) {
	        int value = 0;
	        if (mTV != null) {
	            value = mTV.getConfigValue(itemID);
	        }
	        Log.d(TAG, "[" + itemID + "] get value: " + value + "   Min value: "
	                + getMin(itemID));
	        if (itemID.equals(FA_COMPRESSION)) {
	            switch(value)
	            {
	                case MtkTvConfigType.AUD_CMPSS_MDOE_LINE:
	                    value = 0;
	                    break;
	                case MtkTvConfigType.AUD_CMPSS_MDOE_RF:
	                    value = 1;
	                    break;
	                default:
	                    value = 0;
	                    if (mTV.isEURegion()) {
	                        value = 1;
	                    }
	                    break;
	            }

	            return value;
	        }

	        if (itemID.equals(MenuConfigManager.SPDIF_DELAY)) {
	            if (value > -1 && value < 26) {
	                return value* 10;
	            }
	            return 140;
	        }
	        if (itemID.equals(MenuConfigManager.SETUP_US_SUB_TIME_ZONE)) {
	            if (value != 0) {
	                value = 8 - value;
	            }
	            if (value < 0 || value >7) {
	                value = 0;
	            }
	            return value;
	        }


	        if (itemID.equals(VGA_MODE)) {
	        	//vga's cfg value is 1,2 but ap's array idx is 0,1
	        	//so we must change to 0,1
	            if (value == 1 || value == 2) {
	                value --;
	            }else{
	            	value = 1;
	            }
	            return value;
	        }
	        if (itemID.equals(TV_MTS_MODE)) {
	            switch (value) {
	                case SCC_AUD_MTS_MONO:
	                case SCC_AUD_MTS_NICAM_MONO:
	                    value =0;
	                    break;
	                case SCC_AUD_MTS_STEREO:
	                case SCC_AUD_MTS_NICAM_STEREO:
	                    value =1;
	                    break;
	                case SCC_AUD_MTS_SUB_LANG:
	                case SCC_AUD_MTS_NICAM_DUAL1:
	                    value =2;
	                    break;
	                case SCC_AUD_MTS_NICAM_DUAL2:
	                    value = 3;
	                    break;
	                default:
	                    value =1;
	                    break;
	            }
	            return value;
	        }
	        if (itemID.equals(DI_FILM_MODE)) {
	            if (value > 1) {
	                value = 1;
	            }
	            return value;
	        }
	        //fix CR DTV00581891 581809
	        if (itemID.equals(SCREEN_MODE)) {
//	            if (!mTV.isCurrentSourceVGA()) {
//	                if (CommonIntegration.getInstance().isPOPState()) {
//	                    if (value > 3) {
//	                        value = 0;
//	                    }
//	                    return value;
//	                }else if (CommonIntegration.getInstance().isPIPState() && ("sub").equalsIgnoreCase(CommonIntegration.getInstance().getCurrentFocus())) {
//	                   if (mTV.isUSRegion()) {
//	                       if (value > 3) {
//	                           value = 0;
//	                       }
//	                       return value;
//	                   }
//	                    if (value > 2) {
//	                        value = 0;
//	                    }
//	                    return value;
//	                }
//
//
//	            }else
				{
	                if (value == 3) {
	                    value = 2;
	                }else if (value == 6) {
	                    value = 2;
	                }else if (value == 2) {
	                    value = 1;
	                }else if (value == 1) {
	                    value = 0;
	                }else {
	                    value = 0;
	                }
	                return value;
	            }
	        }
	        //fix CR DTV00580884
	        if (itemID.equalsIgnoreCase(SPDIF_MODE)) {
      // in mw value 2,3 mean PCM16(2) PCM24(3)
      if (value == 3 || value == 2) {
        value = 2;
      } else if (value > 3) {
        value = 3;
      }
	            return value;
	        }
	        if (itemID.equalsIgnoreCase(DOWNMIX_MODE)) {
	            if (mTV.isEURegion()) {
	                return value;
	            }
	            if (value == 2) {
	                value = 0;
	            }else if (value == 11) {
	                value = 1;
	            }else if (value == 1) {
	                value = 2;
	            }else {
	                value = 0;
	            }
	            return value;
	        }
	        if (itemID.equals(GAMMA)) {
	            if (value<1) {
	                return 0;
	            }
	            return value - 1;
	        }

	        if (itemID.equals(AUTO_SLEEP)) {
	            switch (value) {
	                case 0://OFF
	                    value = 0;
	                    break;
	                case 3600:
	                    value = 1;
	                    break;
	                case 7200:
	                    value = 2;
	                    break;
	                case 18000://5HR
	                    value = 3;
	                    break;
	                case 14400://EU 4HR
	                    value = 1;
	                    break;
	                case 21600://EU 6HR
	                    value = 2;
	                    break;
	                case 28800://EU 8HR
	                    value = 3;
	                    break;
	                default:
	                    value = 0;
	                    if (mTV.isEURegion()) {
	                        value = 1;
	                    }
	                    break;
	            }
	            return value;
	        }
	        if (itemID.equals(POWER_ON_CH_AIR_MODE) || itemID.equals(POWER_ON_CH_CABLE_MODE)) {
	            if (value <= 0) {
	                value = 0;
	            }else if (value > 0) {
	                value = 1;
	            }
	            Log.d(TAG, "value:"+value);
	            return value;
	        }
	        if (itemID.equals(MenuConfigManager.POWER_ON_TIMER)) {
	            Log.d(TAG, "value:"+value+"ON_ONCE:"+mTV.getConfigValue(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE));
	            if (value < 0) {
	                value = 1;
	                if (mTV.getConfigValue(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE) < 0) {
	                    value = 2;
	                }
	            } else {
	                value = 0;
	                if (mTV.getConfigValue(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE) < 0) {
	                    value = 2;
	                }
	            }
	            save.saveValue(itemID, value);
	            return value;
	        }


	        if (itemID.equals(MenuConfigManager.POWER_OFF_TIMER)) {
	            if (value < 0) {
	                value = 1;
	                if (mTV.getConfigValue(MtkTvConfigType.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
	                    value = 2;
	                }
	            } else {
	                value = 0;
	                if (mTV.getConfigValue(MtkTvConfigType.CFG_TIMER_TIMER_OFF_ONCE) < 0) {
	                    value = 2;
	                }
	            }
	            save.saveValue(itemID, value);
	            return value;
	        }
	        if (itemID.equals(MenuConfigManager.SHARPNESS)) {
	            int min = getMin(itemID);
	            int max = getMax(itemID);

	            if (value >= min && value <= max) { // hzy fix
	                                                                      // CR:363304
	                Log.v(TAG, "Normal Case.--------------");
	                return value;
	            } else if (value < min) {
	                Log.v(TAG, "Minimum Case.--------------");
	                return min;
	            } else {
	                Log.v(TAG, "Maximum Case.--------------");
	                return max;
	            }
	        }
	        if (value >= getMin(itemID)) {
	            return value;
	        } else {
	            return getMin(itemID);
	        }

	    }

	    public int getValueFromPrefer(String itemID) {
	        return save.readValue(itemID);
	    }

	    public void setValueToPrefer(String itemID, int value) {
	        save.saveValue(itemID, value);
	        // if (null!=TimeShiftManager.getInstance()&value==0) {
	        // TimeShiftManager.getInstance().stopAllRunning();
	        // }
	    }

	    public int getScanDefault(String itemID) {
        int value = 0;
		int minValue = getScanMin(itemID);
		int maxValue = getScanMax(itemID);
        if (value >= minValue && value <= maxValue) {
            return value;
        } else {
            Log.d(TAG, "[" + itemID + "] get value: " + value
                    + "   Min value: " + minValue + "   Max value"
                    + maxValue);
            return minValue;
	        }
	    }

	    public void setValue(String itemID, int value) {
	        Log.d(TAG, "set value: " + itemID + "---" + value);
	        /*
	        if (itemID.equalsIgnoreCase(VISUALLY_SPEAKER) || itemID.equalsIgnoreCase(VISUALLY_HEADPHONE)) {
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            MenuDataContainer.getInstance(mContext).changevisuallyvolume(value,itemID);
	            return;
	        }
	        */


	        if (itemID.equalsIgnoreCase(SETUP_WOW)) {
	            mTV.setWowEnable(value);
	            return;
	        }

			if (itemID.equalsIgnoreCase(SETUP_WOL)) {
				mTV.setWolEnable(value);
				return;
			}

			if (itemID.equalsIgnoreCase(MenuConfigManager.AUTO_VIEW)) {
				Log.d("renwj","set auto view value ="+ value);
				mTV.setConfigValue(itemID,value);
			}

	        if (itemID.equals(TV_MTS_MODE)) {
	            if (mTV.isEURegion()) {
	                switch (value) {
	                    case 0:
	                        value =SCC_AUD_MTS_MONO;
	                        break;
	                    case 1:
	                        value =SCC_AUD_MTS_NICAM_STEREO;
	                        break;
	                    case 2:
	                        value =SCC_AUD_MTS_NICAM_DUAL1;
	                        break;
	                    case 3:
	                        value = SCC_AUD_MTS_NICAM_DUAL2;
	                        break;
	                    default:
	                        value =SCC_AUD_MTS_NICAM_STEREO;
	                        break;
	                }
	            }else {
	                switch (value) {
	                    case 0:
	                        value =SCC_AUD_MTS_MONO;
	                        break;
	                    case 1:
	                        value =SCC_AUD_MTS_STEREO;
	                        break;
	                    case 2:
	                        value =SCC_AUD_MTS_SUB_LANG;
	                        break;
	                    default:
	                        value =SCC_AUD_MTS_STEREO;
	                        break;
	                }
	            }
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            return;
	            }
	        if (itemID.equals(FA_COMPRESSION)) {
	            switch(value)
	            {
	                case 0:
	                    value = MtkTvConfigType.AUD_CMPSS_MDOE_LINE;
	                    break;
	                case 1:
	                    value = MtkTvConfigType.AUD_CMPSS_MDOE_RF;
	                    break;
	                default:
	                    value = MtkTvConfigType.AUD_CMPSS_MDOE_LINE;
	                    if (mTV.isEURegion()) {
	                        value = MtkTvConfigType.AUD_CMPSS_MDOE_RF;
	                    }
	                    break;
	            }
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            return;
	        }

	        if (itemID.equals(MenuConfigManager.SPDIF_DELAY)) {
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value/10);
	            }
	            return;
	        }

//	        if (itemID.equals(SCREEN_MODE)) {
//	        	String key = item.mOptionValue[value];
//	        	int stored_value = mScreenMode.get(key);
//	        	if (mTV != null) {
//	                  mTV.setConfigValue(itemID, stored_value);
//	        	}
//	        	return;
//
//	        }
	        if (itemID.equalsIgnoreCase(FA_LATENCY)) {
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            return;
	        }
	        if (itemID.equalsIgnoreCase(DOWNMIX_MODE)) {
	            if (mTV.isEURegion()) {
	                if (mTV != null) {
	                    mTV.setConfigValue(itemID, value);
	                }
	                return;
	            }else {
	                if (value == 0) {
	                    value = 2;
	                }else if (value == 1) {
	                    value = 11;
	                }else if (value == 2) {
	                    value = 1;
	                }else {
	                    value = 0;
	                }
	                if (mTV != null) {
	                    mTV.setConfigValue(itemID, value);
	                }
	                return;
	            }
	        }

	        if (itemID.equals(GAMMA)) {
	            value = value + 1;
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            return;
	        }
	    	if(itemID.equals(VGA_MODE)){
				//vga's cfg value is not same as middleware
            	//we can't send 0,1 must change to 1,2
				value += 1;
				if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	            return;
			}

	        if (itemID.equals(MenuConfigManager.PARENTAL_BLOCK_UNRATED)) {
	            if (value == 0) {
	                mTV.setBlockUnrated(false);
	            } else {
	                mTV.setBlockUnrated(true);
	            }
	            return;
	        }
	        if (itemID.equals(MenuConfigManager.POWER_ON_TIMER)) {
	            Log.d(TAG, "[" + itemID + "] POWER_ON_TIMER value: " + value);
	            if (value == 0) {
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE, 0,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON, 0,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	            } else if (value == 1) {
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE, 0,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON, 1,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	            } else {
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON, 1,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	                mTV.updatePowerOn(MtkTvConfigType.CFG_TIMER_TIMER_ON_ONCE, 1,
	                        save.readStrValue(MenuConfigManager.TIMER1));
	            }
	            save.saveValue(itemID, value);
	            return;
	        }
	        if (itemID.equals(MenuConfigManager.POWER_OFF_TIMER)) {
	            if (value == 0) {
	                 mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF_ONCE,
	                         0,
	                         save.readStrValue(MenuConfigManager.TIMER2));
	                mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF, 0,
	                        save.readStrValue(MenuConfigManager.TIMER2));
	            } else if (value == 1) {
	                mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF, 1,
	                        save.readStrValue(MenuConfigManager.TIMER2));
	                mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF_ONCE, 0,
	                        save.readStrValue(MenuConfigManager.TIMER2));
	            } else {
	                mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF_ONCE, 1,
	                        save.readStrValue(MenuConfigManager.TIMER2));
	                mTV.updatePowerOff(MtkTvConfigType.CFG_TIMER_TIMER_OFF, 1,
	                        save.readStrValue(MenuConfigManager.TIMER2));
	            }
	            save.saveValue(itemID, value);
	            return;
	        }
	        if (itemID.equalsIgnoreCase(MenuConfigManager.PARENTAL_CFG_RATING_BL_TYPE)) {
	            mTV.setTimeInterval(value);
	            return;
	        }
	        if(itemID.equals(MenuConfigManager.SETUP_CAPTION_STYLE)){
	            //DEMO_TYPE_IS_SHOWING,   /*0, 0: hide,1:show*/
	            Log.d(TAG, "captionStyle, " + value);
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(0, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_FONT_SIZE)){
	            //DEMO_TYPE_FONT_SIZE,      /*1, Font-szie  0:small, 1:middle, 2:large */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(1, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_FONT_STYLE)){
	            //DEMO_TYPE_FONT_STYLE,     /*2, Font-style 0~1: style1 ~ style7 */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(2, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_FONT_COLOR)){
	            //DEMO_TYPE_FONT_COLOR,
	            /*3, Font-color 0:black,1:white,2:green,3:blue,4:red,5:cyan,6:yellow,7:magenta */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(3, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_FONT_OPACITY)){
	            //DEMO_TYPE_FONT_OPACITY,   /*4, Font-opacity 0:solid,1:transl,2:transp,3:solid flash */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(4, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_BACKGROUND_COLOR)){
	            //DEMO_TYPE_BG_COLOR
	            /*5, backgroud-color 0:black,1:white,2:green,3:blue,4:red,5:cyan,6:yellow,7:magenta */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(5, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_BACKGROUND_OPACITY)){
	            //DEMO_TYPE_BG_OPACITY,     /*6, backgroud-opacity 0:solid,1:transl,2:transp,3:solid flash */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(6, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_WINDOW_COLOR)){
	            //DEMO_TYPE_WC_COLOR,       /*7, window-color 0:black,1:white,2:green,3:blue,4:red,5:cyan,6:yellow,7:magenta */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(7, value);
	        }
	        else if(itemID.equals(MenuConfigManager.SETUP_WINDOW_OPACITY)){
	            //DEMO_TYPE_WC_OPACITY,     /*8, window-opacity 0:solid,1:transl,2:transp,3:solid flash */
	            MtkTvATSCCloseCaption.getInstance().atscCCDemoSet(8, value);
	        }

	        if (itemID.equals(AUTO_SLEEP)) {
	            int auto_sleep = 0;
	            if (mTV.isEURegion()) {
	                switch (value) {
	                    case 0:
	                        auto_sleep = 0;
	                        break;
	                    case 1:
	                        auto_sleep = 14400;
	                        break;
	                    case 2:
	                        auto_sleep = 21600;
	                        break;
	                    case 3:
	                        auto_sleep = 28800;
	                        break;
	                    default:
	                        auto_sleep = 14400;
	                        break;
	                }
	            }else {
	                switch (value) {
	                    case 0:
	                        auto_sleep = 0;
	                        break;
	                    case 1:
	                        auto_sleep = 3600;
	                        break;
	                    case 2:
	                        auto_sleep = 7200;
	                        break;
	                    case 3:
	                        auto_sleep = 18000;
	                        break;
	                    default:
	                        auto_sleep = 0;
	                        break;
	                }
	            }
	            mTV.setConfigValue(itemID, auto_sleep);
	            return;
	        }

	        if (itemID.equals(POWER_ON_CH_AIR_MODE) || itemID.equals(POWER_ON_CH_CABLE_MODE)) {
	            return ;
	        }


	        if (itemID.equals(MenuConfigManager.SHARPNESS)) {
	            int min = getMin(itemID);
	            int max = getMax(itemID);

	            if (value >= min && value <= max) { // hzy fix
	                                                                      // CR:363304
	                Log.v(TAG, "Normal Case.--------------");
	                if (mTV != null) {
	                    mTV.setConfigValue(itemID, value);
	                }
	            } else if (value < min) {
	                Log.v(TAG, "Minimum Case.--------------");
	                if (mTV != null) {
	                    mTV.setConfigValue(itemID, max);
	                }
	            } else {
	                Log.v(TAG, "Maximum Case.--------------");
	                if (mTV != null) {
	                    mTV.setConfigValue(itemID, min);
	                }
	            }
	            return;
	        }else if (itemID.equals(TUNER_MODE))
			{
				if (mTV != null)
				{
					Log.v(TAG, "set TUNER_MODE,"+value);
					mTV.setConfigValue(itemID, value);
					return;
				}
			}

	        if (value >= getMin(itemID) && value <= getMax(itemID)) {
				Log.d(TAG, "[" + itemID + "] set value: " + value
						+ "   Min value: " + getMin(itemID) + "   Max value"
						+ getMax(itemID));
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	        } else {
	            Log.d(TAG, "[" + itemID + "] set value: " + value
	                    + "   Min value: " + getMin(itemID) + "   Max value"
	                    + getMax(itemID));
	            if (mTV != null) {
	                mTV.setConfigValue(itemID, value);
	            }
	        }
	    }

	    public void setValueCec(String itemID, int value) {
	        if (mTV != null) {
	            mTV.setConfigValue(itemID, value);
	        }

	        Log.d(TAG, "[" + itemID + "] set value: " + value
	                + "   Min value: " + getMin(itemID) + "   Max value"
	                + getMax(itemID));
	    }

	    public int getDefaultCec(String itemID) {
	        int value = 0;
	        if (mTV != null) {
	            value = mTV.getConfigValue(itemID);
	        }

	        Log.d(TAG, "[" + itemID + "] get value: " + value + "   Min value: "
	                + getMin(itemID) + "   Max value: " + getMax(itemID));
	        return value;
	    }

	    public void setScanValue(String itemID, int value) {
	        if (TV_SINGLE_SCAN_MODULATION.equals(itemID)
	        		|| FREQUENEY_PLAN.equals(itemID)
	        		|| US_SCAN_MODE.equals(itemID)
	        		|| DVBC_SINGLE_RF_SCAN_MODULATION.equals(itemID)) {
	            save.saveValue(itemID, value);
	        }
	    }

	    public void setSetup(String id, int value) {
	        Log.d(TAG, "set Setup: " + id + "---" + value);

	        if (id.equals(SLEEP_TIMER)) {
//	        	mTV.setSleepTimer(value);not use for baioqinggao
	        } else if (id.equals(AUTO_SLEEP)) {
//	            sleepTimerOff = new SleepTimerOff(mContext);not use for baioqinggao
//	            sleepTimerOff.shutDownAuto(value);not use for baioqinggao
	        }


	        save.saveValue(id, value);
	    }

	    public int get3DInitValue(boolean left) {
	        int value = 0;
	        try {
	            // value = cfg.check3DModeSubTpye(left);
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return value;
	    }

        //cts verify start
        private TvInputManager getTvInputManager(Context context) {
          if(mTvInputManager == null) {
          mTvInputManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
          }

          return mTvInputManager;
        }

        //cts verify end
}
