package skyworth.skyworthlivetv.osd.common;

import android.content.Context;
import android.util.Log;
import java.lang.reflect.Field;
import skyworth.skyworthlivetv.R.string;
import skyworth.skyworthlivetv.R;
import skyworth.skyworthlivetv.global.GlobalDefinitions;

/**
 * Created by xeasy on 2017/5/09.
 */

public class TextResource {

    Context context = null;
    Field[] fields = null;
    string obj = new string();
    public TextResource(Context cxt){
        this.context = cxt;
    }

    public void init(){
        Class resClazz = R.string.class;
        fields = resClazz.getDeclaredFields();
    }

    public String getText(String resKey){
        if(resKey == null){
            Log.d(GlobalDefinitions.DEBUG_TAG,"getText resid=null");
            return null;
        }
        if(fields == null){
            init();
        }
        int resId = 0;
        try{
            resId = getResidByReskey(resKey);
        }catch(Exception e){
            e.printStackTrace();
        }
        if(resId == 0){
            Log.d(GlobalDefinitions.DEBUG_TAG,"not found " + resKey + " in xml");
            return "";
        }

        if(context != null){
            String text = context.getString(resId);
            if(text !=null)
            {
                return text;
            }
        }
        return resKey;
    }

    public String getText(int resid){
        if(context != null){
            String text = context.getString(resid);
            return text;
        }
        return null;
    }

    private boolean contains(String resid){
        if(fields != null){
            for (Field field: fields) {
                if(field.getName().equals(resid)){
                    return true;
                }
            }
        }
        return false;
    }

    private int getResidByReskey(String resKey){
        try {
            if(resKey != null){
                Field field = getField(resKey);
                if(field != null){
                    return field.getInt(obj);
                }
            }
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    private Field getField(String resKey){
        if(resKey != null){
            if(fields != null){
                for (Field field: fields) {
                    if(field.getName().equals(resKey)){
                        return field;
                    }
                }
            }
        }
        return null;
    }

    public void setContext(Context cxt){
        this.context = cxt;
    }


}
