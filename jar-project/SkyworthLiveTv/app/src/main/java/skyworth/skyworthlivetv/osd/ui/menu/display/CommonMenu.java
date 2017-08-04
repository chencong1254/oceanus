package skyworth.skyworthlivetv.osd.ui.menu.display;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.SkyScreenParams;
import skyworth.skyworthlivetv.osd.common.data.MultiSelectListData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;


/**
 * Created by sky057509 on 2017/4/27.
 */

public class CommonMenu extends FrameLayout {
    private FrameLayout leftMenuLayout;
    private MenuListView firstMenu;
    private MenuListView secondMenu;
    private MenuListView thirdMenu;
    private SelectList selectList;
    private PopMenu thirdPopMenu;
    private Context mContext;
    private CommonMenuListener commonMenuListener;
    private int mLevelBeforePopMenu = -1;

    private TextView menuTitleView;
    private ImageView titleLineView;
    private int focusedIndexOfFirstMenu = 0;//the focus position of the first position
    private int focusedIndexOfSecondMenu = 0;//the focus position of the second menu
    private int focusedIndexOfThirdMenu = 0;// the focus position of the third menu
    private int focusedIndexOfFourthMenu = 0;// the focus position of the fourth menu

    public interface CommonMenuListener {
        public boolean onFirstMenuItemOnKeyRight(int itemID, TypedData typedData);// 一级菜单项右键监听
        public boolean onFirstMenuItemOnClick(int itemIndex, TypedData typedData);// 一级菜单项点击监听
        public boolean onFirstMenuItemFocusChangeListener(int itemID,TypedData typedData,boolean focus);
        public boolean onFirstMenuItemOnKeyBack(int itemIndex, MenuItemData currentData);// 一级菜返回键监听

        public boolean onSecondMenuItemOnKeyLeft(int itemID, TypedData typedData);// 二级菜单项左键监听
        public boolean onSecondMenuItemOnKeyRight(int itemID, TypedData typedData);// 二级菜单项右键监听
        public boolean onSecondMenuItemOnClick(int itemIndex, TypedData typedData);// 二级菜单项点击监听
        public boolean onSecondMenuItemOnKeyBack(int itemIndex, MenuItemData currentData);// 二级菜单项返回键监听

        public boolean onThirdMenuItemOnKeyLeft(int itemIndex, TypedData typedData);//  三级菜单项左键监听
        public boolean onThirdMenuItemOnKeyRight(int itemID, TypedData typedData);// 三级菜单项右键监听
        public List<Integer> onThirdMenuItemOnKeyBack(int itemID, MenuItemData currentData);// 三级菜返回键监听
        public boolean onThirdMenuItemOnClick(int itemIndex, TypedData typedData);// 三级菜单项点击监听

        public boolean onThirdPopMenuItemOnKeyLeft(TypedData typedData);//  三级POP菜单项左键监听
        public boolean onThirdPopMenuItemOnKeyRight(TypedData typedData);// 三级POP菜单项右键监听
        public List<Integer> onThirdPopMenuItemOnKeyBack(MenuItemData currentData);// 三级POP菜单项返回 键监听

        public boolean onSelectListItemOnClick(int itemIndex, TypedData typedData,int mLevelBeforePopMenu); // MultiselectListItem
        public List<Integer> onSelectListItemOnKeyBack(int itemID,MenuItemData currentData,int mLevelBeforePopMenu);  // MultiselectListItem
        public boolean onSelectListItemOnKeyOther(int itemID, int keyCode,int mLevelBeforePopMenu);    // MultiselectListItem

    }

    public CommonMenu(Context context) {
        super(context);
        initView(context);

    }

    public void setOncommonMenuListener(CommonMenuListener commonMenuListener) {
        this.commonMenuListener = commonMenuListener;
    }

    private void initView(Context context) {
        mContext = context;
        this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        setBackgroundColor(Color.TRANSPARENT);
        leftMenuLayout = new FrameLayout(mContext);
        leftMenuLayout.setBackgroundColor(MenuConstant.MENU_LEFT_BACKGROUND_COLOR);
        leftMenuLayout.setAlpha(MenuConstant.MENU_LEFT_BACKGROUND_ALPHA);
        FrameLayout.LayoutParams leftMenuLayoutLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_LEFT_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT); //ViewGroup.LayoutParams.WRAP_CONTENT);
        leftMenuLayoutLp.leftMargin = 0;
        leftMenuLayoutLp.topMargin =  0;

        menuTitleView = new TextView(mContext);
        menuTitleView.setTextSize(SkyScreenParams.getInstence(mContext).getTextDpiValue(MenuConstant.MENU_TITLE_SIZE));
        menuTitleView.setTextColor(MenuConstant.MENU_TITLE_COLOR);
        menuTitleView.setFocusable(false);
        FrameLayout.LayoutParams menuTitleLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        menuTitleLp.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLE_TOP_MARGIN);
        menuTitleLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLE_LEFT_MARGIN);
        leftMenuLayout.addView(menuTitleView, menuTitleLp);

        titleLineView = new ImageView(mContext);
        titleLineView.setFocusable(false);
        titleLineView.setBackgroundColor(MenuConstant.MENU_TITLELINE_COLOR);
        titleLineView.setAlpha(MenuConstant.MENU_TITLELINE_ALPHA);
        FrameLayout.LayoutParams title_line_p = new FrameLayout.LayoutParams(
                SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLELINE_WIDTH),
                SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLELINE_HEIGHT));
        title_line_p.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLELINE_TOP_MARGIN);
        title_line_p.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.MENU_TITLELINE_LEFT_MARGIN);
        leftMenuLayout.addView(titleLineView, title_line_p);

        firstMenu = new MenuListView(mContext);

        FrameLayout.LayoutParams firstMenuLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_MENU_WIDTH),
                LayoutParams.WRAP_CONTENT);
        firstMenuLp.gravity = Gravity.TOP;
        firstMenuLp.leftMargin = 0;
        firstMenuLp.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_SECOND_MENU_TOP_MARGIN);
        firstMenu.setVisibility(GONE);
        leftMenuLayout.addView(firstMenu, firstMenuLp);

        secondMenu = new MenuListView(mContext);
        FrameLayout.LayoutParams secondMenuLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENU_WIDTH),
                LayoutParams.WRAP_CONTENT);
        secondMenuLp.gravity = Gravity.TOP;
        secondMenuLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SECOND_MENU_LEFT_MARGIN);
        secondMenuLp.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.FIRST_SECOND_MENU_TOP_MARGIN);
        secondMenu.setVisibility(GONE);
        leftMenuLayout.addView(secondMenu,secondMenuLp);


        thirdMenu = new MenuListView(mContext);
        FrameLayout.LayoutParams thirdMenuMenuLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_WIDTH),
                LayoutParams.WRAP_CONTENT);
        thirdMenuMenuLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_LEFT_MARGIN);
        thirdMenuMenuLp.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TOP_MARGIN);
        leftMenuLayout.addView(thirdMenu,thirdMenuMenuLp);
        thirdMenu.setVisibility(GONE);

        selectList = new SelectList(mContext);
        FrameLayout.LayoutParams selectListLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SELECT_LIST_WIDTH),
                SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.SELECT_LIST_HEIGHT));

        selectListLp.leftMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_LEFT_MARGIN);
        selectListLp.topMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_MENU_TOP_MARGIN);
        leftMenuLayout.addView(selectList,selectListLp);
        selectList.setVisibility(GONE);

        this.addView(leftMenuLayout,leftMenuLayoutLp);

        thirdPopMenu = new PopMenu(mContext);
        thirdPopMenu.setBackgroundColor(MenuConstant.THIRD_POPMENU_BACKGROUND_COLOR);
        thirdPopMenu.setAlpha(MenuConstant.THIRD_POPMENU_BACKGROUND_ALPHA);
        thirdPopMenu.setPopMenuOnkeyListener(thirdPopMenuListener);
        FrameLayout.LayoutParams thirdPopMenuLp = new FrameLayout.LayoutParams(SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_WIDTH),
                SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_HEIGHT));
        thirdPopMenuLp.bottomMargin = SkyScreenParams.getInstence(mContext).getResolutionValue(MenuConstant.THIRD_POPMENU_BOTTOM_MARGIN);
        thirdPopMenuLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        thirdPopMenu.setVisibility(GONE);
        this.addView(thirdPopMenu,thirdPopMenuLp);



    }
    public boolean showFirstMenu(String menuTitle,List<MenuItemData> mList) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"showFirstMenu mList size:"+mList.size());
        this.setVisibility(VISIBLE);
        leftMenuLayout.setVisibility(VISIBLE);
        menuTitleView.setVisibility(View.VISIBLE);
        firstMenu.setVisibility(VISIBLE);
        secondMenu.setVisibility(GONE);
        thirdMenu.setVisibility(GONE);
        thirdPopMenu.setVisibility(GONE);
        selectList.setVisibility(GONE);
        menuTitleView.setText(menuTitle);
        MenuFirstAdapter menuFirstAdapter = new MenuFirstAdapter(mContext);
        menuFirstAdapter.setMenuItemOnkeyListener(firstMenuListener);
        menuFirstAdapter.setData(mList);
        firstMenu.setAdapter(menuFirstAdapter);
            firstMenu.postDelayed(new Runnable(){

                @Override
                public void run()
                {
                    firstMenu.getAdapter().focus(0);
                }
            }, 0);

        return true;

    }
    public void showSecondMenu(String menuTitle,List<MenuItemData> menuChildItems) {
        Log.d(GlobalDefinitions.DEBUG_TAG," showSecondMenu menuChildItems size:"+menuChildItems.size());
        secondMenu.setVisibility(VISIBLE);
        menuTitleView.setText(menuTitle);
        MenuSecondAdapter menuSecondAdapter = new MenuSecondAdapter(mContext);
        menuSecondAdapter.setData(menuChildItems);
        menuSecondAdapter.setMenuItemOnkeyListener(secondMenuListener);
        secondMenu.setAdapter(menuSecondAdapter);
        try {
            secondMenu.getAdapter().notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public boolean showThirdMenu(String menuTitle,List<MenuItemData> menuItems) {
        Log.d(GlobalDefinitions.DEBUG_TAG," showThirdMenu menuItems size:"+menuItems.size());
        focusedIndexOfSecondMenu = secondMenu.getAdapter().getFocusedPosition();
        firstMenu.setVisibility(GONE);
        secondMenu.setVisibility(GONE);
        thirdMenu.setVisibility(VISIBLE);
        thirdPopMenu.setVisibility(GONE);
        selectList.setVisibility(GONE);

        menuTitleView.setText(menuTitle);
        MenuMultiAdapter menuThirdAdapter = new MenuMultiAdapter(mContext);
        menuThirdAdapter.setData(menuItems);
        menuThirdAdapter.setMenuItemOnkeyListener(thirdMenuListener);
        thirdMenu.setAdapter(menuThirdAdapter);
        thirdMenu.postDelayed(new Runnable(){

            @Override
            public void run()
            {
                thirdMenu.getAdapter().focus(0);
            }
        }, 100);
        return true;
    }

    public boolean showThirdPopMenu(MenuItemData itemData) {
        Log.d(GlobalDefinitions.DEBUG_TAG," showThirdPopMenu");
        focusedIndexOfSecondMenu = secondMenu.getAdapter().getFocusedPosition();
        leftMenuLayout.setVisibility(GONE);
        menuTitleView.setVisibility(GONE);
        titleLineView.setVisibility(GONE);
        firstMenu.setVisibility(GONE);
        secondMenu.setVisibility(GONE);
        thirdMenu.setVisibility(GONE);
        selectList.setVisibility(GONE);
        thirdPopMenu.setVisibility(VISIBLE);
        thirdPopMenu.setData(itemData);
        thirdPopMenu.setPopMenuOnkeyListener(thirdPopMenuListener);
        thirdPopMenu.postDelayed(new Runnable(){
            @Override
            public void run()
            {
                thirdPopMenu.requestFocus();
            }
        }, 100);
        return true;
    }
    public boolean showSelectList(String menuTitle, MenuItemData menuItemData, int mLevelBeforePopMenu) {
        Log.d(GlobalDefinitions.DEBUG_TAG," showSelectList menuTitle:"+menuTitle+"  mLevelBeforePopMenu:"+mLevelBeforePopMenu);
        this.mLevelBeforePopMenu = mLevelBeforePopMenu;
        switch(mLevelBeforePopMenu)
        {
            case 0:
                break;
            case 1:
                focusedIndexOfSecondMenu = secondMenu.getAdapter().getFocusedPosition();
                break;
            case 2:
                focusedIndexOfThirdMenu = thirdMenu.getAdapter().getFocusedPosition();
                break;
        }
        firstMenu.setVisibility(GONE);
        secondMenu.setVisibility(GONE);
        thirdMenu.setVisibility(GONE);
        thirdPopMenu.setVisibility(GONE);
        selectList.setVisibility(VISIBLE);

        menuTitleView.setText(menuTitle);
        SelectListAdapter selectListAdapter = new SelectListAdapter(mContext);
        selectListAdapter.setData(menuItemData);
        selectListAdapter.setSelectListItemOnkeyListener(selectListListener);
        selectList.setAdapter(selectListAdapter);
        selectList.postDelayed(new Runnable(){

            @Override
            public void run()
            {
                selectList.getAdapter().focus(0);
            }
        }, 100);
        return true;
    }
    public void refreshMenuItems(List<Integer> posList) {
        if(secondMenu!=null && secondMenu.getAdapter()!=null  && secondMenu.isShown())
        {
            for(Integer posTmp:posList)
            {
                secondMenu.getAdapter().notifyItemChanged(posTmp);
            }
        }
        if(thirdMenu!=null && thirdMenu.getAdapter()!=null  && thirdMenu.isShown())
        {
            for(Integer posTmp:posList)
            {
                thirdMenu.getAdapter().notifyItemChanged(posTmp);
            }
        }
    }

    public void refreshSecondMenu() {
        if(secondMenu!=null && secondMenu.getAdapter()!=null)
        {
            secondMenu.getAdapter().notifyDataSetChanged();
        }
    }
    public void refreshSecondMenuItems(List<Integer> posList) {
        if(secondMenu!=null && secondMenu.getAdapter()!=null)
        {
            for(Integer posTmp:posList)
            {
                secondMenu.getAdapter().notifyItemChanged(posTmp);
            }
        }
    }
    public void refreshThirdMenu() {
        if(thirdMenu!=null && thirdMenu.getAdapter()!=null)
        {
            thirdMenu.getAdapter().notifyDataSetChanged();
        }
    }
    public void refreshThirdMenuItems(List<Integer> posList) {
        if(thirdMenu!=null && thirdMenu.getAdapter()!=null)
        {
            for(Integer posTmp:posList)
            {
                thirdMenu.getAdapter().notifyItemChanged(posTmp);
            }
        }
    }

    public boolean hideMenu() {
        this.setVisibility(GONE);
        leftMenuLayout.setVisibility(GONE);
        firstMenu.setVisibility(GONE);
        secondMenu.setVisibility(GONE);
        thirdMenu.setVisibility(GONE);
        thirdPopMenu.setVisibility(GONE);
        selectList.setVisibility(GONE);
        focusedIndexOfFirstMenu = 0;
        focusedIndexOfSecondMenu = 0;
        focusedIndexOfThirdMenu = 0;
        return true;
    }

    @Override
    public boolean isShown() {
        return super.isShown() && (firstMenu.isShown() || secondMenu.isShown() || thirdMenu.isShown() || thirdPopMenu.isShown()||selectList.isShown());
    }



    // 一级菜单监听
    private MenuBaseAdapter.MenuItemOnkeyListener firstMenuListener = new MenuBaseAdapter.MenuItemOnkeyListener() {

        @Override
        public boolean onItemOnKeyLeft(int itemID, MenuItemData currentData) {
            return false;
        }

        @Override
        public boolean onItemOnKeyRight(int itemID, MenuItemData currentData) {
            focusedIndexOfFirstMenu = firstMenu.getAdapter().getFocusedPosition();

            secondMenu.getAdapter().focus(0);
            if(commonMenuListener!=null)
            {
                return  commonMenuListener.onFirstMenuItemOnKeyRight(itemID,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public void onItemFocusChangeListener(int itemID, MenuItemData currentData, boolean focus) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onFirstMenuItemFocusChangeListener(itemID,currentData.getTypedData(),focus);
            }
        }
        @Override
        public boolean onItemOnClick(int itemIndex, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                return commonMenuListener.onFirstMenuItemOnClick(itemIndex,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public boolean onItemOnKeyBack(int itemID,MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onSecondMenuItemOnKeyLeft(itemID,currentData.getTypedData());
            }
            backMenu(0);
            return true;
        }

        @Override
        public boolean onItemOnKeyOther(int itemID, int keyCode) {
            return false;
        }
    };
    // 二级菜单监听
    private MenuBaseAdapter.MenuItemOnkeyListener secondMenuListener = new MenuBaseAdapter.MenuItemOnkeyListener() {


        @Override
        public boolean onItemOnKeyLeft(int itemID, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onSecondMenuItemOnKeyLeft(itemID,currentData.getTypedData());
            }
            backMenu(1);
            return true;
        }

        @Override
        public boolean onItemOnKeyRight(int itemID, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                return commonMenuListener.onSecondMenuItemOnKeyRight(itemID,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public void onItemFocusChangeListener(int itemID, MenuItemData currentData, boolean focus) {

        }

        @Override
        public boolean onItemOnClick(int itemIndex, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                return commonMenuListener.onSecondMenuItemOnClick(itemIndex,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public boolean onItemOnKeyBack(int itemID,MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onSecondMenuItemOnKeyBack(itemID,currentData);
            }
            backMenu(1);
            return true;
        }

        @Override
        public boolean onItemOnKeyOther(int itemID, int keyCode) {
            return false;
        }
    };


    // 三级菜单监听
    private MenuBaseAdapter.MenuItemOnkeyListener thirdMenuListener = new MenuBaseAdapter.MenuItemOnkeyListener() {

        @Override
        public boolean onItemOnKeyLeft(int itemID, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onThirdMenuItemOnKeyLeft(itemID,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public boolean onItemOnKeyRight(int itemID, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                return commonMenuListener.onThirdMenuItemOnKeyRight(itemID,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public void onItemFocusChangeListener(int itemID, MenuItemData currentData, boolean focus) {

        }

        @Override
        public boolean onItemOnClick(int itemIndex, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                return commonMenuListener.onThirdMenuItemOnClick(itemIndex,currentData.getTypedData());
            }
            return false;
        }

        @Override
        public boolean onItemOnKeyBack(int itemID,MenuItemData currentData) {
            List<Integer> posList = null;
            if(commonMenuListener!=null)
            {
                posList = commonMenuListener.onThirdPopMenuItemOnKeyBack(currentData);
            }
            backMenu(2);
            if(posList!= null && posList.size()>0) {
                refreshMenuItems(posList);
            }
            return true;
        }

        @Override
        public boolean onItemOnKeyOther(int itemID, int keyCode) {
            return false;
        }
    };
    // 三级POP菜单监听
    private PopMenu.PopMenuOnkeyListener thirdPopMenuListener = new PopMenu.PopMenuOnkeyListener() {

        @Override
        public boolean onKeyLeft(View v, MenuItemData currentData) {

            if(commonMenuListener!=null)
            {
                return commonMenuListener.onThirdPopMenuItemOnKeyLeft(currentData.getTypedData());
            }
            return true;
        }

        @Override
        public boolean onKeyRight(View v, MenuItemData currentData) {
            if(commonMenuListener!=null)
            {
                commonMenuListener.onThirdPopMenuItemOnKeyRight(currentData.getTypedData());
            }
            return true;
        }

        @Override
        public boolean onKeyBack(MenuItemData currentData) {
            List<Integer> posList = null;
            if(commonMenuListener!=null)
            {
               posList = commonMenuListener.onThirdPopMenuItemOnKeyBack(currentData);
            }
            backMenu(2);
            if(posList!= null && posList.size()>0) {
                refreshMenuItems(posList);
            }
            return true;
        }

        @Override
        public boolean onKeyOther(View v, int keyCode) {
            return false;
        }
    };
    // MultiSelectList listener
    private SelectListBaseAdapter.SelectListItemOnkeyListener selectListListener = new SelectListBaseAdapter.SelectListItemOnkeyListener() {

        @Override
        public boolean onItemOnClick(int itemIndex, MenuItemData currentData) {
            switch(currentData.getTypedData().getType())
            {
                case DATA_TYPE_SINGLE_SELECT_LIST:
                    SingleSelectListData singleSelectListData = (SingleSelectListData)currentData.getTypedData();
                    singleSelectListData.setCurrentIndex(itemIndex);
                    if(commonMenuListener!=null)
                    {
                        commonMenuListener.onSelectListItemOnClick(itemIndex,singleSelectListData,mLevelBeforePopMenu);
                    }
                    break;
                case DATA_TYPE_MULTI_SELECT_LIST:
                    MultiSelectListData multiSelectListData = (MultiSelectListData)currentData.getTypedData();
                    multiSelectListData.selectedIndexToggle(itemIndex);
                    if(commonMenuListener!=null)
                    {
                        commonMenuListener.onSelectListItemOnClick(itemIndex,multiSelectListData,mLevelBeforePopMenu);
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean onItemOnKeyBack(int itemID, MenuItemData currentData) {
            List<Integer> posList = null;
            if(commonMenuListener!=null)
            {
                posList = commonMenuListener.onSelectListItemOnKeyBack(itemID,currentData,mLevelBeforePopMenu);
            }
            backMenu(mLevelBeforePopMenu+1);;
            if(posList!= null && posList.size()>0) {
                refreshMenuItems(posList);
            }
            return true;
        }

        @Override
        public boolean onItemOnKeyOther(int itemID, int keyCode) {
            return false;
        }
    };


    private void backMenu(int fromWhichLevel)
    {
        Log.d(GlobalDefinitions.DEBUG_TAG," backMenu fromWhichLevel:"+fromWhichLevel);
        switch (fromWhichLevel) {
            case 0:
                hideMenu();
                break;
            case 1:
                firstMenu.setVisibility(VISIBLE);
                secondMenu.setVisibility(VISIBLE);
                thirdMenu.setVisibility(GONE);
                thirdPopMenu.setVisibility(GONE);
                selectList.setVisibility(GONE);
                firstMenu.getAdapter().focus(focusedIndexOfFirstMenu);
                break;
            case 2:
                leftMenuLayout.setVisibility(VISIBLE);
                menuTitleView.setVisibility(VISIBLE);
                titleLineView.setVisibility(VISIBLE);
                firstMenu.setVisibility(VISIBLE);
                secondMenu.setVisibility(VISIBLE);
                thirdMenu.setVisibility(GONE);
                thirdPopMenu.setVisibility(GONE);
                selectList.setVisibility(GONE);
                if(commonMenuListener != null){
                    commonMenuListener.onFirstMenuItemFocusChangeListener(focusedIndexOfFirstMenu, null, true);
                    if(focusedIndexOfFirstMenu == 0){
                        View v = firstMenu.getChildAt(0);
                        MenuFirstHolder h = (MenuFirstHolder)firstMenu.getChildViewHolder(v);
                        //h.onFocusChange(v, false);
                        TextView menuName = (TextView)h.itemView.findViewWithTag(111);
                        ImageView rightArrow = (ImageView)h.itemView.findViewWithTag(222);
                        if(menuName != null)
                            menuName.setTextColor(MenuConstant.FIRST_MENU_TEXT_SELECT_COLOR);
                        if(rightArrow != null) {
                            rightArrow.setSelected(true);
                            rightArrow.setVisibility(View.VISIBLE);
                        }
                    }
                }
                secondMenu.getAdapter().focus(focusedIndexOfSecondMenu);
                break;
            case 3:
                leftMenuLayout.setVisibility(VISIBLE);
                menuTitleView.setVisibility(VISIBLE);
                titleLineView.setVisibility(VISIBLE);
                firstMenu.setVisibility(GONE);
                secondMenu.setVisibility(GONE);
                thirdMenu.setVisibility(VISIBLE);
                thirdPopMenu.setVisibility(GONE);
                selectList.setVisibility(GONE);
                thirdMenu.getAdapter().focus(focusedIndexOfThirdMenu);
                break;
            default:
                break;
        }
    }
}