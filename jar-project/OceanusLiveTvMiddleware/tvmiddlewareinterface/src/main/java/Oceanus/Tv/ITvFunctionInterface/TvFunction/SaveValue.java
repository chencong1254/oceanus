package Oceanus.Tv.ITvFunctionInterface.TvFunction;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;


public class SaveValue {
	 private static  Object syncRoot = new Object();
    private static final String TAG = "SaveValue";
    private SharedPreferences mSharedPreferences;
    Context mContext;

    public static SaveValue save_data;

    private SaveValue(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static synchronized SaveValue getInstance(Context context) {
		if (save_data == null) {
			save_data = new SaveValue(context);
		}
	     return save_data;
	
    }

    public void saveValue(String name, int value) {
        mSharedPreferences.edit().putInt(name, value).commit();
//        TVContent.getInstance(mContext).flushMedia();
//        MtkLog.d(TAG,"save value: " + name + "--- " + value);
    }
    public void saveValue(String name, float value) {
    	mSharedPreferences.edit().putFloat(name, value).commit();
//    	TVContent.getInstance(mContext).flushMedia();
//    	MtkLog.d(TAG,"save value: " + name + "--- " + value);
    }
    public void saveStrValue(String name, String value) {
        mSharedPreferences.edit().putString(name, value).commit();
//        TVContent.getInstance(mContext).flushMedia();
    }
    public void saveBooleanValue(String name, boolean value){
    	mSharedPreferences.edit().putBoolean(name, value).commit();
//    	TVContent.getInstance(mContext).flushMedia();
    }

	public int readValue(String id) {
		int value = 0;

		if (id.equals(MenuConfigManager.DPMS)
				|| id.equals(MenuConfigManager.AUTO_SYNC)
				|| id.equals(MenuConfigManager.FAST_BOOT) || id.equals(MenuConfigManager.SETUP_WOW)) {
			value = mSharedPreferences.getInt(id, 1);
        }else if (id.equals(MenuConfigManager.DIGITAL_SUBTITLE_LANG)
                || id.equals(MenuConfigManager.DIGITAL_SUBTITLE_LANG_2ND)) {
            value = mSharedPreferences.getInt(id, 8);
        } else {
            value = mSharedPreferences.getInt(id, 0);    
        }
//        MtkLog.d("TVContent","read value: " + id + "--- " + value);
        return value;
    }
	public float readFloatValue(String id) {
		float value = 0f;
		
		if (id.equals(MenuConfigManager.DPMS)
				|| id.equals(MenuConfigManager.AUTO_SYNC)) {
			value = mSharedPreferences.getInt(id, 1);
		} else {
			value = mSharedPreferences.getFloat(id, 0f);    
		}
		//MtkLog.d(TAG,"read value: " + id + "--- " + value);
		return value;
	}
    public String readStrValue(String id){
    	String value = null;
    	if (id.equals(MenuConfigManager.TIMER1)||id.equals(MenuConfigManager.TIMER2) || id.equals(MenuConfigManager.PARENTAL_CFG_RATING_BL_START_TIME)) {
    		value = mSharedPreferences.getString(id, "00:00");
		}else if (id.equals(MenuConfigManager.PARENTAL_CFG_RATING_BL_END_TIME)) {
		    value = mSharedPreferences.getString(id, "23:59:59");
        }else if(id.equals("password")){
			value = mSharedPreferences.getString(id, "1234");
		}else if (id.equals(MenuConfigManager.TIME_START_TIME)||id.equals(MenuConfigManager.TIME_END_TIME)) {
		    value = mSharedPreferences.getString(id, "00:00");
        }else if (id.equals(MenuConfigManager.TIME_START_DATE)||id.equals(MenuConfigManager.TIME_END_DATE)) {
            value = mSharedPreferences.getString(id, "2008/12/01");
        }else {
			value = mSharedPreferences.getString(id, "0");
		}
//    	MtkLog.d("ybb","readStrValue value: " + id + "--- " + value);
    	return value;
    }
    public boolean readBooleanValue(String id){
    	Boolean value = false;
    	value = mSharedPreferences.getBoolean(id, false);
    	return value;
    }
    
    SharedPreferences worldPref ;
    public boolean readWorldBoolValue(String id){
    	//if(worldPref == null){
    		String cxtId = "com.mediatek.wwtv.tvcenter";
        	String name = "com.mediatek.wwtv.tvcenter_preferences";
        	try {
    			Context tContext = mContext.createPackageContext(cxtId, Context.CONTEXT_IGNORE_SECURITY);
    			worldPref = tContext.getSharedPreferences(name, Context.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS);
        	} catch (NameNotFoundException e) {
    			e.printStackTrace();
    		}
    	//}
    	return worldPref.getBoolean(id, false);
    }
    
    public String readWorldStringValue(String id){
    	//if(worldPref == null){
    		String cxtId = "com.mediatek.wwtv.tvcenter";
        	String name = "com.mediatek.wwtv.tvcenter_preferences";
        	try {
    			Context tContext = mContext.createPackageContext(cxtId, Context.CONTEXT_IGNORE_SECURITY);
    			worldPref = tContext.getSharedPreferences(name, Context.MODE_WORLD_READABLE|Context.MODE_MULTI_PROCESS);
        	} catch (NameNotFoundException e) {
    			e.printStackTrace();
    		}
    	//}
    	return worldPref.getString(id, "");
    }
    
    public void writeWorldStrValue(String id,String val){
    	String cxtId = "com.mediatek.wwtv.tvcenter";
    	String name = "com.mediatek.wwtv.tvcenter_preferences";
//    	String cxtId = "com.example.androiddemo";
//    	String name = "com.example.androiddemo_preferences";
    	try {
			Context tContext = mContext.createPackageContext(cxtId, Context.CONTEXT_IGNORE_SECURITY);
			worldPref = tContext.getSharedPreferences(name, Context.MODE_WORLD_WRITEABLE|Context.MODE_MULTI_PROCESS);
			worldPref.edit().putString(id, val).commit();
    	} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
    }

}
