package skyworth.skyworthlivetv.osd.ui.menu;

/**
 * Created by xeasy on 2017/5/2.
 */

public enum TVMENU_COMMAND {
	// picture
	MENUCMD_GET_PICTURE_MODE_SETTING,
	MENUCMD_PICTURE_MODE,
    MENUCMD_BRIGHTNESS,
    MENUCMD_CONTRAST,
	MENUCMD_SATURATION,
	MENUCMD_COLOR,
	MENUCMD_SHARPNESS,
	MENUCMD_BACKLIGHT,
	MENUCMD_COLORTEMP_MODE,
	MENUCMD_SCREEN_MODE,
	MENUCMD_MEMC,
	MENUCMD_NR,
	MENUCMD_MPEG_NR,
	MENUCMD_DYNAMIC_LUMINANCE_CTRL,
	MENUCMD_BLUE_MUTE,
	MENUCMD_FILM_MODE,
	MENUCMD_BLUE_STRETCH,
	MENUCMD_GAME_MODE,


	// sound
	MENUCMD_GET_SOUND_MODE_SETTING,
	MENUCMD_SOUND_MODE,
	MENUCMD_EQUALIZER_120HZ,
	MENUCMD_EQUALIZER_500HZ,
	MENUCMD_EQUALIZER_1500HZ,
	MENUCMD_EQUALIZER_5KHZ,
	MENUCMD_EQUALIZER_10KHZ,
	MENUCMD_BALANCE,
	MENUCMD_SURROUND_SOUND,
	MENUCMD_AUTO_VOLUME_CONTROL,
	MENUCMD_AD_SWITCH,
	MENUCMD_AD_VOLUME,
	MENUCMD_SPDIF_TYPE,
	MENUCMD_SPDIF_DELAY,
	MENUCMD_AUDIO_OUT,

	// channel
	MENUCMD_START_TUNING,
	MENUCMD_5V_ANTENNA,
	MENUCMD_START_CHANNEL_EDIT,
	MENUCMD_CI_INFORMATION,

	// setup
	MENUCMD_PRIMARY_AUDIO_LANGUAGE,
	MENUCMD_SECOND_AUDIO_LANGUAGE,
	MENUCMD_SLEEP_TIMER,
	MENUCMD_NO_SIGNAL_OFF,
	MENUCMD_NO_OPERATION_OFF,
	MENUCMD_HDMI_CEC,
	MENUCMD_HDMICEC_DEVICELIST,
	MENUCMD_HDMICEC_AUTO_POWER_ON,
	MENUCMD_HDMICEC_AUTO_STANDBY,
	MENUCMD_HDMICEC_SPEAKER_PREFERENCE,
	MENUCMD_RESET_DEFAULT,

	// lock
	TVMENU_CHANGE_PASSWORD,

	//  Advanced
	MENUCMD_STORE_MODE,
	MENUCMD_TIME_ZONE,
	MENUCMD_SUMMER_TIME,
	MENUCMD_OSD_LANGUAGE,
	MENUCMD_PRIMARY_SUBTITLE_LANGUAGE,
	MENUCMD_SECOND_SUBTITLE_LANGUAGE,
	MENUCMD_TELETEXT_LANGUAGE,
	MENUCMD_OAD,
	MENUCMD_OPEN_INSTALL_RESET,
	MENUCMD_HDMI_EDID,
}