package skyworth.skyworthlivetv.global;

import android.content.Context;

import skyworth.skyworthlivetv.R;

/**
 * Created by sky057509 on 2017/4/26.
 */

public class GlobalDefinitions {
    static public final String DEBUG_TAG = "SkyworthLiveTv";
    static public final String PlatformAndroidTv = "androidtv";
    static public final String PlatformAosp = "aosp";
    static public final String dvb = "dvb";
    static public final String atsc = "atsc";
    static public final String isdb = "isdb";
    static public final String dtmb = "dtmb";
    static public final String atv_all = "all";
    static public final String atv_ntsc = "ntsc";
    static public boolean IsTargetDtvSystem(String target, Context context)
    {
        return context.getString(R.string.dtv_system).compareTo(target) == 0;
    }
    static public boolean IsTargetAtvSystem(String target, Context context)
    {
        return context.getString(R.string.atv_system).compareTo(target) == 0;
    }
}
