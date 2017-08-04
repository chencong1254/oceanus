package skyworth.platformsupport.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.platform.CommonDefinitions.EMPTY;

/**
 * Created by yangxiong on 2017/5/10.
 */

public class SPUtil {
    public static final String SP_FILE_NAME = "sp_file_name";
    private static SPUtil spUtil = null;
    private  Context context;

    private SPUtil(Context context) {
        this.context = context;
    }

    public static SPUtil getInstance(Context contex){
        if (spUtil == null){
            return new SPUtil(contex);
        }
        return spUtil;
    }

    public  void putListMap(String key, List<Map<String, String>> datas) {
        JSONArray mJsonArray = new JSONArray( );
        for (int i = 0; i < datas.size( ); i++) {
            Map<String, String> itemMap = datas.get(i);
            Iterator<Map.Entry<String, String>> iterator = itemMap.entrySet( ).iterator( );

            JSONObject object = new JSONObject( );

            while (iterator.hasNext( )) {
                Map.Entry<String, String> entry = iterator.next( );
                try {
                    object.put(entry.getKey( ), entry.getValue( ));
                } catch (JSONException e) {
                    e.printStackTrace( );
                }
            }
            mJsonArray.put(object);
        }

        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit( );
        editor.putString(key, mJsonArray.toString( ));
        editor.commit( );
    }

    public  List<Map<String, String>> getListMap(String key) {
        List<Map<String, String>> datas = new ArrayList<Map<String, String>>( );
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        String result = sp.getString(key,EMPTY);
        if(result.compareTo(EMPTY) == 0)
        {
            return null;
        }
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map<String, String> itemMap = new HashMap<String, String>( );
                JSONArray names = itemObject.names( );
                if (names != null) {
                    for (int j = 0; j < names.length( ); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {
            e.printStackTrace( );
            return null;
        }
        return datas;
    }

    public void putInt(String key,String inputID) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit( );
        edit.putString(key,inputID);
        edit.commit();
    }

    public int getInt(String key){
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key,-1);
    }
    public void putString(String key,String inputID) {
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit( );
        edit.putString(key,inputID);
        edit.commit();
    }

    public String getString(String key){
        SharedPreferences sp = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key,EMPTY);
    }

}
