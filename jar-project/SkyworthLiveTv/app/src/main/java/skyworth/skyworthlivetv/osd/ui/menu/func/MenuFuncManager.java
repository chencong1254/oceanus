package skyworth.skyworthlivetv.osd.ui.menu.func;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;
import skyworth.skyworthlivetv.osd.ui.menu.func.advanced.AdvancedMenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.func.channel.ChannelMenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.func.lock.LockMenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.func.picture.PicMenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.func.setup.SetupMenuFuncSet;
import skyworth.skyworthlivetv.osd.ui.menu.func.sound.SoundMenuFuncSet;

/**
 * Created by xeasy on 2017/5/4.
 */

public class MenuFuncManager {

    private static MenuFuncManager instance = null;
    private Context mContext = null;
    private boolean isInited = false;

    private HashMap<String, MenuFunction> menuFuncHashMap = new HashMap<String, MenuFunction>();

    public static MenuFuncManager getInstance()
    {
        if (instance == null)
            instance = new MenuFuncManager();
        return instance;
    }
    private  void MenuFuncManager()
    {
        isInited = false;
    }
    private List<Class<?>> menuFuncClassList = new ArrayList<Class<?>>();


    public void init(Context context)
    {
        mContext = context;
        menuFuncClassList.add(PicMenuFuncSet.class);
        menuFuncClassList.add(SoundMenuFuncSet.class);
        menuFuncClassList.add(ChannelMenuFuncSet.class);
        menuFuncClassList.add(SetupMenuFuncSet.class);
        menuFuncClassList.add(LockMenuFuncSet.class);
        menuFuncClassList.add(AdvancedMenuFuncSet.class);

        for(Class<?> clazz:menuFuncClassList)
        {
        try {
            MenuFuncSet menuFunc = (MenuFuncSet)clazz.newInstance();
            menuFunc.init(mContext);
            List<MenuFunction> menuFunctionList = menuFunc.getMenuFunctionList();
            if(menuFunctionList!=null)
            {
                for (MenuFunction func : menuFunctionList)
                {
                    menuFuncHashMap.put(func.cmd,func);
                }
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        }
        isInited = true;
    }

    public MenuFunction getMenuFunction(String key)
    {
        return menuFuncHashMap.get(key);
    }

    public boolean isInited(){
        return isInited;
    }
}