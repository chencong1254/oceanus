package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.graphics.Color;

/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuConstant {
    public final static int MENU_LEFT_WIDTH = 800;
    public final static int MENU_LEFT_BACKGROUND_COLOR = Color.BLACK;
    public final static float MENU_LEFT_BACKGROUND_ALPHA = 0.85f;//0.7f;

    public final static int MENU_TITLE_SIZE = 60;
    public final static int MENU_TITLE_COLOR = 0xff777777;
    public final static int MENU_TITLE_LEFT_MARGIN = 40;
    public final static int MENU_TITLE_TOP_MARGIN = 40;

    public final static int MENU_TITLELINE_COLOR = 0xffffffff;
    public final static float MENU_TITLELINE_ALPHA = 1.0f;
    public final static int MENU_TITLELINE_LEFT_MARGIN = 40;
    public final static int MENU_TITLELINE_TOP_MARGIN = 120;
    public final static int MENU_TITLELINE_WIDTH = 720;
    public final static int MENU_TITLELINE_HEIGHT = 1;

    public final static int FIRST_SECOND_MENU_TOP_MARGIN = 170;

    public final static int FIRST_MENU_WIDTH = 334;
    public final static int FIRST_MENUITEM_HEIGHT = 180;
    public final static int FIRST_MENU_TEXT_SIZE = (int)(44*0.54);

    public final static int FIRST_MENU_TEXT_FOCUS_COLOR = 0xffffffff;
    public final static int FIRST_MENU_TEXT_UNFOCUS_COLOR = 0xff777777;
    public final static int FIRST_MENU_TEXT_SELECT_COLOR = 0xff6c63fe;
    public final static int FIRST_MENU_ARROW_LEFT_MARGIN = 0;
    public final static int FIRST_MENU_ARROW_RIGHT_MARGIN = 20;


    public final static int SECOND_MENU_WIDTH = 506;
    public final static int SECOND_MENUITEM_HEIGHT = 180;
    public final static int SECOND_MENU_LEFT_MARGIN = 322;
    public final static int SECOND_MENU_MAINTEXT_SIZE = (int)(36*0.55);
    public final static int SECOND_MENU_SUBTEXT_SIZE = (int)(24*0.55);


    public final static int SECOND_MENU_MAINTEXT_FOCUS_COLOR = Color.WHITE;
    public final static int SECOND_MENU_MAINTEXT_UNFOCUS_COLOR = 0xffcccccc;
    public final static int SECOND_MENU_SUBTEXT_FOCUS_COLOR = 0xffeaeaea;
    public final static int SECOND_MENU_SUBTEXT_UNFOCUS_COLOR = 0xff999999;

    public final static int THIRD_MENU_WIDTH = 800;
    public final static int THIRD_MENU_TOP_MARGIN = 220;
    public final static int THIRD_MENU_LEFT_MARGIN = 0;
    public final static int THIRD_MENUITEM_HEIGHT = 180;
    public final static int THIRD_MENU_TEXT_SIZE = (int)(36*0.55);



    public final static int THIRD_POPMENU_WIDTH = 800;
    public final static int THIRD_POPMENU_HEIGHT = 120;
    public final static int THIRD_POPMENU_BOTTOM_MARGIN = 56;
    public final static int  THIRD_POPMENU_BACKGROUND_COLOR = Color.BLACK;
    public final static float THIRD_POPMENU_BACKGROUND_ALPHA = 0.85f;//0.6f;

    public final static int THIRD_POPMENU_TEXT_SIZE = (int)(36*0.55);
    public final static int THIRD_POPMENU_LEFTTEXT_COLOR = 0xffcccccc;
    public final static int THIRD_POPMENU_RIGHTTEXT_COLOR = 0xffffffff;



    public final static int SELECT_LIST_WIDTH = 800;
    public final static int SELECT_LIST_HEIGHT = 800;
    public final static int SELECT_LIST_ITEM_HEIGHT = 160;
    public final static int MULTI_SELECT_LIST_TEXT_SIZE = (int)(36*0.54);

    public final static int MULTI_SELECT_LIST_TEXT_FOCUS_COLOR = 0xffffffff;
    public final static int MULTI_SELECT_LIST_TEXT_UNFOCUS_COLOR = 0xff777777;
    public final static int MULTI_SELECT_LIST_CHECKBOX_LEFT_MARGIN = 0;
    public final static int MULTI_SELECT_LIST_ITEMNAME_RIGHT_MARGIN = 176;


    public final static String  MENU_CONFIG_PATH = "/system/pcfg/";

    // 类型  "TYPE_ROOT":根;  "TYPE_FLIPOUT":弹出非菜单的UI  ;  // "TYPE_TITLE": 仅显示
    public final static String  TYPE_ROOT = "TYPE_ROOT";
    public final static String  TYPE_FLIPOUT = "TYPE_FLIPOUT";
    public final  static String  TYPE_TITLE = "TYPE_TITLE";
}
