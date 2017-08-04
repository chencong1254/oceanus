package skyworth.skyworthlivetv.osd.common;

import android.content.Context;
import android.content.SharedPreferences;

import skyworth.skyworthlivetv.global.GlobalApplication;

/**
 * @author dsl
 *         <p>
 *         accessing and modifying preference data.
 *         </p>
 */
public class OptPreferences {

	private SharedPreferences sets;
	private SharedPreferences.Editor editor;
	private static OptPreferences opt = new OptPreferences();

	private OptPreferences() {
		sets = GlobalApplication.getAppInstance().getSharedPreferences(
				CommonConst.p_ScanPreference, Context.MODE_PRIVATE);
		editor = sets.edit();
	}

	public static OptPreferences getInstance() {
		return opt;
	}

	/**
	 * Set a boolean value in the preferences editor, to be written back once
	 * commit is called.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return a reference to the same Editor object, so you can chain put calls
	 *         together.
	 * */
	public void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value).commit();
	}

	/**
	 * Retrieve a boolean value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * @return the preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean.
	 * @throws ClassCastException
	 * */
	public boolean getBoolean(String key, boolean defValue) {
		return sets.getBoolean(key, defValue);
	}

	/**
	 * Set a float value in the preferences editor, to be written back once
	 * commit is called.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return a reference to the same Editor object, so you can chain put calls
	 *         together.
	 * */
	public void setFloat(String key, float value) {
		editor.putFloat(key, value).commit();
	}

	/**
	 * Retrieve a float value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * @return the preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean.
	 * @throws ClassCastException
	 * */
	public float getFloat(String key, float defValue) {
		return sets.getFloat(key, defValue);
	}

	/**
	 * Set a int value in the preferences editor, to be written back once commit
	 * is called.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return a reference to the same Editor object, so you can chain put calls
	 *         together.
	 * */
	public void setInt(String key, int value) {
		editor.putInt(key, value).commit();
	}

	/**
	 * Retrieve a int value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * @return the preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean.
	 * @throws ClassCastException
	 * */
	public int getInt(String key, int defValue) {
		return sets.getInt(key, defValue);
	}

	/**
	 * Set a long value in the preferences editor, to be written back once
	 * commit is called.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return a reference to the same Editor object, so you can chain put calls
	 *         together.
	 * */
	public void setLong(String key, long value) {
		editor.putLong(key, value).commit();
	}

	/**
	 * Retrieve a long value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * @return the preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean.
	 * @throws ClassCastException
	 * */
	public long getLong(String key, long defValue) {
		return sets.getLong(key, defValue);
	}

	/**
	 * Set a String value in the preferences editor, to be written back once
	 * commit is called.
	 * 
	 * @param key
	 *            The name of the preference to modify.
	 * @param value
	 *            The new value for the preference.
	 * @return a reference to the same Editor object, so you can chain put calls
	 *         together.
	 * */
	public void setString(String key, String value) {
		editor.putString(key, value).commit();
	}

	/**
	 * Retrieve a String value from the preferences.
	 * 
	 * @param key
	 *            The name of the preference to retrieve.
	 * @param defValue
	 *            Value to return if this preference does not exist.
	 * @return the preference value if it exists, or defValue. Throws
	 *         ClassCastException if there is a preference with this name that
	 *         is not a boolean.
	 * @throws ClassCastException
	 * */
	public String getString(String key, String defValue) {
		return sets.getString(key, defValue);
	}

}
