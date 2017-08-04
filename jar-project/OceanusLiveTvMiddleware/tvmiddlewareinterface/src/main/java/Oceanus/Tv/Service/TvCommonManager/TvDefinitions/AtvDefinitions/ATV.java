package Oceanus.Tv.Service.TvCommonManager.TvDefinitions.AtvDefinitions;

/**
 * Created by sky057509 on 2016/9/2.
 */
public final class ATV {
    public enum  EN_COLOR_SYSTEM
    {
        E_COLOR_STANDARD_NTSC,
        E_COLOR_STANDARD_NTSC_443,
        E_COLOR_STANDARD_PAL,
        E_COLOR_STANDARD_PAL_M,
		E_COLOR_STANDARD_PAL_N,
        E_COLOR_STANDARD_PAL_60,
        E_COLOR_STANDARD_SECAM,
        E_COLOR_STANDARD_SECAM_L,
        E_COLOR_STANDARD_NOTSTANDARD,
        E_COLOR_STANDARD_AUTO,
        E_COLOR_STANDARD_UNKNOW
    };
    public enum EN_SOUND_SYSTEM{
        E_SOUND_SYSTEM_BG,
        E_SOUND_SYSTEM_I,
        E_SOUND_SYSTEM_DK,
        E_SOUND_SYSTEM_L,
        E_SOUND_SYSTEM_M,
        E_SOUND_SYSTEM_N,
        E_SOUND_SYSTEM_AUTO,
        E_SOUND_SYSTEM_UNKNOW
    };
    public enum EN_ATV_MTS_MODE
    {
        E_ATV_MTS_MODE_MONO , /**<Audio Mode MONO*/
        E_ATV_MTS_MODE_STEREO           , /**<Audio Mode G Stereo*/
        E_ATV_MTS_MODE_DUAL_A           , /**<Audio Mode Dual A*/
        E_ATV_MTS_MODE_DUAL_B           , /**<Audio Mode Dual B*/
        E_ATV_MTS_MODE_DUAL_AB          , /**<Audio Mode Dual AB*/
        E_ATV_MTS_MODE_BTSC_MONO        , /**<Audio Mode BTSC MONO*/
        E_ATV_MTS_MODE_BTSC_STEREO      , /**<Audio Mode BTSC STEREO*/
        E_ATV_MTS_MODE_BTSC_SAP         , /**<Audio Mode BTSC SAP*/
        E_ATV_MTS_MODE_NICAM_FORCED_MONO, /**<Audio Mode NICAM Forced MONO*/
        E_ATV_MTS_MODE_NICAM_MONO       , /**<Audio Mode NICAM MONO*/
        E_ATV_MTS_MODE_NICAM_STEREO     , /**<Audio Mode NICAM Stereo*/
        E_ATV_MTS_MODE_NICAM_DUAL_A     , /**<Audio Mode NICAM DUAL A*/
        E_ATV_MTS_MODE_NICAM_DUAL_B     , /**<Audio Mode NICAM DUAL B*/
        E_ATV_MTS_MODE_NICAM_DUAL_AB    , /**<Audio Mode NICAM DUAL AB*/
        E_ATV_MTS_MODE_AUTO      		, /**<Audio Mode AUTO */
        E_ATV_MTS_MODE_UNKNOW
    };
}
