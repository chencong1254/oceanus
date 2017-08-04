package skyworth.skyworthlivetv.osd.ui.menu.base;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xeasy on 2017/5/2.
 */

public abstract class MenuFuncSet {

    protected Context mContext = null;
    private List<MenuFunction> menuFunctionList = new ArrayList<MenuFunction>();

    public MenuFuncSet()
    {

    }
    public void init(Context context)
    {
        mContext = context;
        Field[] fields = getClass().getDeclaredFields();
        for (Field field : fields)
        {
            try
            {
                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj != null)
                {
                     if (MenuFunction.class.isInstance(obj))
                    {
                        menuFunctionList.add((MenuFunction) obj);
                    }
                }
            } catch (IllegalArgumentException e)
            {
                Log.e("","IllegalArgumentException");
                e.printStackTrace();
            } catch (IllegalAccessException e)
            {
                Log.e("","IllegalAccessException");
                e.printStackTrace();
            }
        }
    }


    public final List<MenuFunction> getMenuFunctionList()
    {
        return menuFunctionList;
    }
}
