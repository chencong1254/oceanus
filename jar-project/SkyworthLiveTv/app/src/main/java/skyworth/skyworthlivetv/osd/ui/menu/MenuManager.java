package skyworth.skyworthlivetv.osd.ui.menu;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import skyworth.skyworthlivetv.global.GlobalDefinitions;
import skyworth.skyworthlivetv.osd.common.TextResource;
import skyworth.skyworthlivetv.osd.common.data.BooleanData;
import skyworth.skyworthlivetv.osd.common.data.DataUtil;
import skyworth.skyworthlivetv.osd.common.data.EnumData;
import skyworth.skyworthlivetv.osd.common.data.MultiSelectListData;
import skyworth.skyworthlivetv.osd.common.data.RangeData;
import skyworth.skyworthlivetv.osd.common.data.SingleSelectListData;
import skyworth.skyworthlivetv.osd.common.data.SwitchData;
import skyworth.skyworthlivetv.osd.common.data.TypedData;
import skyworth.skyworthlivetv.osd.ui.menu.base.MenuFunction;
import skyworth.skyworthlivetv.osd.ui.menu.base.menudata.xml.MenuItemXmlNode;
import skyworth.skyworthlivetv.osd.ui.menu.base.menudata.xml.MenuItemXmlParser;
import skyworth.skyworthlivetv.osd.ui.menu.display.CommonMenu;
import skyworth.skyworthlivetv.osd.ui.menu.display.MenuConstant;
import skyworth.skyworthlivetv.osd.ui.menu.display.MenuItemData;
import skyworth.skyworthlivetv.osd.ui.menu.func.MenuFuncManager;


/**
 * Created by xeasy on 2017/5/2.
 */

public class MenuManager implements CommonMenu.CommonMenuListener {
    private Context mContext = null;
    private FrameLayout rootMenuView = null;
    private MenuItemXmlNode rootNode = null;//

//    private List<MenuItemXmlNode> menuFirstList = null;
//    private List<MenuItemXmlNode> menuSecondList = null;
//    private List<MenuItemXmlNode> menuThirdList = null;
//    private List<MenuItemXmlNode> menuFourthList = null;
    private HashMap<Integer,List<MenuItemXmlNode>> menuList = new HashMap<Integer,List<MenuItemXmlNode>>();

    private MenuItemXmlNode  selectlistMenuItemXmlNode = null;

    private HashMap<Integer,List<MenuItemData>>  menuItemDataList = new HashMap<Integer,List<MenuItemData>>() ;
//    private List<MenuItemData> menuItemDataFirstList = null;
//    private List<MenuItemData> menuItemDataSecondList = null;
//    private List<MenuItemData> menuItemDataThirdList = null;
//    private List<MenuItemData> menuItemDataFourthList = null;

    private volatile  boolean backFromSecondMenu = false;

    private CommonMenu tvMenu = null;
    private TextResource textResource;
    public MenuManager()
    {

    }
    public void init(FrameLayout rootMenuView,Context mContext)
    {
        this.rootMenuView = rootMenuView;
        this.mContext = mContext;
        backFromSecondMenu = false;
        textResource = new TextResource(mContext);
        textResource.init();
    }
    public boolean isMenuShown()
    {
       if(tvMenu ==null)
       {
           return false;
       }else {
           return tvMenu.isShown();
       }
    }
    public boolean toggleShowMainMenu()
    {
        if(!MenuFuncManager.getInstance().isInited())
        {
            MenuFuncManager.getInstance().init(mContext);
        }
        if(rootNode == null)
        {
            initRootNode();
            if(rootNode == null)
            {
                return false;
            }
            List<MenuItemXmlNode> menuFirstList = rootNode.getChildList();
            if(menuFirstList ==null || menuFirstList.size()==0)
            {
                return false;
            }
            menuList.put(0,menuFirstList);
        }
        if(tvMenu ==null)
        {
            tvMenu = new CommonMenu(mContext);
                if (rootMenuView != null)
                    rootMenuView.addView(tvMenu);
            tvMenu.setOncommonMenuListener(this);
            List<MenuItemData> menuItemDataFirstList = getMenuItemDataList(menuList.get(0));
            menuItemDataList.put(0,menuItemDataFirstList);
            return tvMenu.showFirstMenu("Menu",menuItemDataFirstList);
        }else if(tvMenu.isShown())
            {
                Log.d(GlobalDefinitions.DEBUG_TAG,"  toggleShowMainMenu,hideMenu");
                return tvMenu.hideMenu();
            }
        else
            {
                Log.d(GlobalDefinitions.DEBUG_TAG,"  toggleShowMainMenu,showFirstMenu");
                List<MenuItemData> menuItemDataFirstList = getMenuItemDataList(menuList.get(0));
                menuItemDataList.put(0,menuItemDataFirstList);
                return tvMenu.showFirstMenu("Menu",menuItemDataFirstList);
             }
    }

     // need fix
    private String  xmlName()
    {
        return "atv_menu.xml";
    }
    private void initRootNode()
    {
        try
        {
            Long start2 = System.currentTimeMillis();
            File xmlFile = new File(MenuConstant.MENU_CONFIG_PATH
                    + xmlName());
            if (xmlFile.exists())
            {
                rootNode = new MenuItemXmlParser().parse(xmlFile);
            } else
            {
                rootNode = new MenuItemXmlParser()
                        .parse((mContext.getResources().getAssets()
                                .open(xmlName())));
            }
            Long end2 = System.currentTimeMillis();
            System.out.println("parse xml cost " + (end2 - start2) + " ms.");
            System.out.println("parsed : " + rootNode);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onFirstMenuItemOnKeyRight(int itemID, TypedData typedData) {
        return false;
    }

    @Override
    public boolean onFirstMenuItemOnClick(int itemIndex, TypedData typedData) {
        return false;
    }

    @Override
    public boolean onFirstMenuItemFocusChangeListener(int itemID, TypedData typedData, boolean focus) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"onFirstMenuItemFocusChangeListener itemID:"+itemID+"  focus:"+focus+"  backFromSecondMenu:"+ backFromSecondMenu);
        if (menuList.get(0) == null || menuList.get(0).size() < itemID + 1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG,"onFirstMenuItemFocusChangeListener itemIndex error !!!");
            return false;
        }
        if(!focus)
        {
           return true;
        }
        if(backFromSecondMenu)
        {
            backFromSecondMenu = !backFromSecondMenu;
            return true;
        }
        MenuItemXmlNode currentMenuItemXmlNode = menuList.get(0).get(itemID);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onFirstMenuItemFocusChangeListener currentMenuItemXmlNode:"+currentMenuItemXmlNode+"  currentMenuItemXmlNode.hasChildNode():"+currentMenuItemXmlNode.hasChildNode());
        if(currentMenuItemXmlNode!= null  && currentMenuItemXmlNode.hasChildNode())
        {
            List<MenuItemXmlNode> menuSecondList = currentMenuItemXmlNode.getChildList();
            menuList.put(1,menuSecondList);
            List<MenuItemData> menuItemDataSecondList = getMenuItemDataList(menuSecondList);
            if(menuItemDataSecondList!=null)
            {
                menuItemDataList.put(1,menuItemDataSecondList);
                tvMenu.showSecondMenu("Menu",menuItemDataSecondList);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onFirstMenuItemOnKeyBack(int itemIndex, MenuItemData menuItemData) {
        backFromSecondMenu = false;
        return false;
    }

    @Override
    public boolean onSecondMenuItemOnKeyLeft(int itemID,TypedData typedData) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"onSecondMenuItemOnKeyLeft itemID:"+itemID);
        backFromSecondMenu = true;
        return true;
    }

    @Override
    public boolean onSecondMenuItemOnKeyRight(int itemID, TypedData typedData) {
        return onSecondMenuItemOnClick(itemID,typedData);
    }

    @Override
    public boolean onSecondMenuItemOnClick(int itemIndex, TypedData typedData) {
        if (menuList.get(1) == null || menuList.get(1).size() < itemIndex + 1)
        {
            Log.e("oversea","onSecondMenuItemOnClick itemIndex error !!!");
            return false;
        }
        MenuItemXmlNode currentMenuItemXmlNode = menuList.get(1).get(itemIndex);
        if(currentMenuItemXmlNode!= null  && currentMenuItemXmlNode.hasChildNode())
        {
            List<MenuItemXmlNode> menuThirdList = currentMenuItemXmlNode.getChildList();
            if(menuThirdList!=null)
            {
                menuList.put(2,menuThirdList);
            }
            if(menuThirdList!=null && menuThirdList.size() ==1  && menuThirdList.get(0).type.equalsIgnoreCase(MenuConstant.TYPE_FLIPOUT) )
            {
                MenuFunction menuSetFunc = MenuFuncManager.getInstance().getMenuFunction(menuThirdList.get(0).cmd);
                Log.d(GlobalDefinitions.DEBUG_TAG,"onSecondMenuItemOnClick command:"+menuThirdList.get(0).cmd+"  menuSetFunc:"+menuSetFunc);
                if(menuSetFunc !=null)
                {
                    menuSetFunc.set(menuThirdList.get(0).cmd,null);
                }
                return true;
            }

            List<MenuItemData> menuItemDataThirdList = getMenuItemDataList(menuThirdList);
            if(menuItemDataThirdList!=null)
            {
                menuItemDataList.put(2,menuItemDataThirdList);
                if(menuItemDataThirdList.size() ==1)
                {
                    TypedData itemData0 = menuItemDataThirdList.get(0).getTypedData();
                   if( itemData0 !=null)
                   {
                       Log.d(GlobalDefinitions.DEBUG_TAG,"  itemData0.getType():"+itemData0.getType());
                       if(itemData0.getType().equals(TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST)||itemData0.getType().equals(TypedData.SkyDataType.DATA_TYPE_MULTI_SELECT_LIST))
                       {
                           selectlistMenuItemXmlNode = menuThirdList.get(0);
                           return  tvMenu.showSelectList(menuItemDataThirdList.get(0).getItemTitle(),menuItemDataThirdList.get(0),1);
                       }else
                       {
                           return  tvMenu.showThirdPopMenu(menuItemDataThirdList.get(0));
                       }
                   }
                }else {
                    return tvMenu.showThirdMenu(textResource.getText(currentMenuItemXmlNode.name),menuItemDataThirdList);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onSecondMenuItemOnKeyBack(int itemIndex, MenuItemData menuItemData) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"onSecondMenuItemOnKeyBack itemID:"+itemIndex);
        backFromSecondMenu = true;
        return true;
    }
    @Override
    public boolean onThirdMenuItemOnKeyLeft(int itemIndex, TypedData typedData) {
        if (menuList.get(2) == null || menuList.get(2).size() < itemIndex + 1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG,"onThirdMenuItemOnKeyLeft itemIndex error !!!");
            return false;
        }
        MenuItemXmlNode  currentMenuItemXmlNode = menuList.get(2).get(itemIndex);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onThirdMenuItemOnKeyLeft currentMenuItemXmlNode.cmd:"+currentMenuItemXmlNode.cmd);
        MenuFunction menuSetFunc = MenuFuncManager.getInstance().getMenuFunction(currentMenuItemXmlNode.cmd);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onThirdMenuItemOnKeyLeft menuSetFunc:"+menuSetFunc);
        BooleanData resultData = MenuFuncManager.getInstance().getMenuFunction(currentMenuItemXmlNode.cmd).set(currentMenuItemXmlNode.cmd,typedData);
        refreshAssociateItems(currentMenuItemXmlNode,menuList.get(2),menuItemDataList.get(2));
        return resultData.isSuccess();
    }

    @Override
    public boolean onThirdMenuItemOnKeyRight(int itemID, TypedData typedData) {
        if (menuList.get(2) == null || menuList.get(2).size() < itemID + 1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG,"onThirdMenuItemOnKeyRight itemIndex error !!!");
            return false;
        }
        MenuItemXmlNode  currentMenuItemXmlNode = menuList.get(2).get(itemID);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onThirdMenuItemOnKeyRight currentMenuItemXmlNode.cmd:"+currentMenuItemXmlNode.cmd);
        BooleanData resultData = MenuFuncManager.getInstance().getMenuFunction(currentMenuItemXmlNode.cmd).set(currentMenuItemXmlNode.cmd,typedData);
        refreshAssociateItems(currentMenuItemXmlNode,menuList.get(2),menuItemDataList.get(2));
        return resultData.isSuccess();
    }

    @Override
    public List<Integer> onThirdMenuItemOnKeyBack(int itemID, MenuItemData menuItemData) {
        List<Integer> posList = new ArrayList<Integer>();
        int index = menuItemDataList.get(2).indexOf(menuItemData);
        if(index>=0){
            MenuItemXmlNode menuItemXmlNode = menuList.get(2).get(index);
            Log.d(GlobalDefinitions.DEBUG_TAG,"  onThirdPopMenuItemOnKeyBack menuItemXmlNode:"+menuItemXmlNode.cmd);
            int samepos = refreshAssociateItemsOnBack(menuItemXmlNode);
            if(samepos >=0)
            {
                DataUtil.getTypedDataFromSameDataType(menuItemData.getTypedData(),menuItemDataList.get(1).get(samepos).getTypedData());
                posList.add(samepos);
            }

        }
        return posList;
    }

    @Override
    public boolean onThirdMenuItemOnClick(int itemIndex, TypedData typedData) {
        if (menuList.get(2) == null || menuList.get(2).size() < itemIndex + 1)
        {
            Log.e("oversea","onSecondMenuItemOnClick itemIndex error !!!");
            return false;
        }

        MenuItemXmlNode currentMenuItemXmlNode = menuList.get(2).get(itemIndex);

        if(currentMenuItemXmlNode!= null  && currentMenuItemXmlNode.hasChildNode())
        {
            List<MenuItemXmlNode> menuFourthList = currentMenuItemXmlNode.getChildList();
            if(menuFourthList!=null)
            {
                menuList.put(3,menuFourthList);
            }
            if(menuFourthList!=null && menuFourthList.size() ==1  && menuFourthList.get(0).type.equalsIgnoreCase(MenuConstant.TYPE_FLIPOUT) )
            {
                MenuFunction menuSetFunc = MenuFuncManager.getInstance().getMenuFunction(menuFourthList.get(0).cmd);
                Log.d(GlobalDefinitions.DEBUG_TAG,"onSecondMenuItemOnClick command:"+menuFourthList.get(0).cmd+"  menuSetFunc:"+menuSetFunc);
                if(menuSetFunc !=null)
                {
                    menuSetFunc.set(menuFourthList.get(0).cmd,null);
                }
                return true;
            }

            List<MenuItemData>  menuItemDataFourthList = getMenuItemDataList(menuFourthList);
            if(menuItemDataFourthList!=null)
            {
                menuItemDataList.put(3,menuItemDataFourthList);
                if(menuItemDataFourthList.size() ==1)
                {
                    TypedData itemData0 = menuItemDataFourthList.get(0).getTypedData();
                    if( itemData0 !=null)
                    {
                        Log.d(GlobalDefinitions.DEBUG_TAG,"  itemData0.getType():"+itemData0.getType());
                        if(itemData0.getType().equals(TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST)||itemData0.getType().equals(TypedData.SkyDataType.DATA_TYPE_MULTI_SELECT_LIST))
                        {
                            selectlistMenuItemXmlNode = menuFourthList.get(0);
                            return  tvMenu.showSelectList(menuItemDataFourthList.get(0).getItemTitle(),menuItemDataFourthList.get(0),2);
                        }else
                        {
                            ;
                        }
                    }
                }
                else {
                    ;
                }
            }
        }
        return true;
    }

    @Override
    public boolean onThirdPopMenuItemOnKeyLeft(TypedData typedData) {
        if (menuList.get(2) == null || menuList.get(2).size() < 1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG,"onThirdPopMenuItemOnKeyLeft itemIndex error !!!");
            return false;
        }
        MenuItemXmlNode currentMenuItemXmlNode = menuList.get(2).get(0);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onThirdPopMenuItemOnKeyLeft currentMenuItemXmlNode.cmd:"+currentMenuItemXmlNode.cmd);
        MenuFuncManager.getInstance().getMenuFunction(currentMenuItemXmlNode.cmd).set(currentMenuItemXmlNode.cmd,typedData);
        return true;
    }

    @Override
    public boolean onThirdPopMenuItemOnKeyRight(TypedData typedData) {
        if (menuList.get(2) == null || menuList.get(2).size() <1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG,"onThirdPopMenuItemOnKeyRight itemIndex error !!!");
            return false;
        }
        MenuItemXmlNode  currentMenuItemXmlNode = menuList.get(2).get(0);
        Log.d(GlobalDefinitions.DEBUG_TAG,"onThirdPopMenuItemOnKeyRight currentMenuItemXmlNode.cmd:"+currentMenuItemXmlNode.cmd);
        MenuFuncManager.getInstance().getMenuFunction(currentMenuItemXmlNode.cmd).set(currentMenuItemXmlNode.cmd,typedData);
        return true;
    }

    @Override
    public List<Integer> onThirdPopMenuItemOnKeyBack(MenuItemData menuItemData) {
        List<Integer> posList = new ArrayList<Integer>();
        int index = menuItemDataList.get(2).indexOf(menuItemData);
        Log.d(GlobalDefinitions.DEBUG_TAG,"  onThirdPopMenuItemOnKeyBack index:"+index);
        if(index>=0){
            MenuItemXmlNode menuItemXmlNode = menuList.get(2).get(index);
            Log.d(GlobalDefinitions.DEBUG_TAG,"  onThirdPopMenuItemOnKeyBack menuItemXmlNode:"+menuItemXmlNode.cmd);
            int samepos = refreshAssociateItemsOnBack(menuItemXmlNode);
            Log.d(GlobalDefinitions.DEBUG_TAG,"  onThirdPopMenuItemOnKeyBack samepos:"+samepos);
            if(samepos >=0)
            {
                DataUtil.getTypedDataFromSameDataType(menuItemData.getTypedData(), menuItemDataList.get(1).get(samepos).getTypedData());
                posList.add(samepos);
            }
        }
        return posList;
    }

    @Override
    public boolean onSelectListItemOnClick(int itemIndex, TypedData typedData,int mLevelBeforePopMenu) {
        Log.d(GlobalDefinitions.DEBUG_TAG,"onSelectListItemOnClick cmd:"+selectlistMenuItemXmlNode.cmd);
        BooleanData resultData = MenuFuncManager.getInstance().getMenuFunction(selectlistMenuItemXmlNode.cmd).set(selectlistMenuItemXmlNode.cmd,typedData);
//        refreshAssociateItems(selectlistMenuItemXmlNode,menuThirdList,menuItemDataThirdList);
        return resultData.isSuccess();
    }

    @Override
    public List<Integer> onSelectListItemOnKeyBack(int itemID, MenuItemData menuItemData,int mLevelBeforePopMenu) {
        List<Integer> posList = new ArrayList<Integer>();
        int index = menuItemDataList.get(mLevelBeforePopMenu+1).indexOf(menuItemData);
        if(index>=0){
            MenuItemXmlNode menuItemXmlNode = menuList.get(mLevelBeforePopMenu+1).get(index);
            int samepos = refreshAssociateItemsOnBack(menuItemXmlNode);
            if(samepos >=0)
            {
                DataUtil.getTypedDataFromSameDataType(menuItemData.getTypedData(), menuItemDataList.get(mLevelBeforePopMenu).get(samepos).getTypedData());
                posList.add(samepos);
            }
        }
        return posList;
    }

    @Override
    public boolean onSelectListItemOnKeyOther(int itemID, int keyCode,int mLevelBeforePopMenu) {
        return false;
    }


    private List<MenuItemData>  getMenuItemDataList(List<MenuItemXmlNode> menuNodeList)
    {
        List<MenuItemData>  menuItemDataList = new ArrayList<MenuItemData>();
        for(MenuItemXmlNode tmpMenuItemXmlNode:menuNodeList)
        {
            MenuItemData  tmpMenuData = new MenuItemData();
            tmpMenuData.setItemTitle(textResource.getText(tmpMenuItemXmlNode.name));
            tmpMenuData.setEnabled(getIsMenuEnabled(tmpMenuItemXmlNode));
            tmpMenuData.setIsShow(getIsMenuItemShowed(tmpMenuItemXmlNode));
            MenuFunction menuGetFunc = MenuFuncManager.getInstance().getMenuFunction(tmpMenuItemXmlNode.cmd);
            if(menuGetFunc !=null)
            {
                Log.d(GlobalDefinitions.DEBUG_TAG,"getMenuItemDataList menuGetFunc.cmd:"+menuGetFunc.cmd);
                TypedData typedData = menuGetFunc.get(tmpMenuItemXmlNode.cmd,null);
                Log.d(GlobalDefinitions.DEBUG_TAG,"getMenuItemDataList typedData:"+typedData);

                try {
                    switch (typedData.getType()) {
                        case DATA_TYPE_ENUM:
                            EnumData enumData = (EnumData) typedData;
                            List<String> enumTitleList = new ArrayList<>();
                            for (String enumStr : enumData.getEnumList()) {
                                String emunTitle = textResource.getText(enumStr);
                                Log.d(GlobalDefinitions.DEBUG_TAG, "  enumStr:" + enumStr + "  emunTitle:" + emunTitle);
                                enumTitleList.add(emunTitle);
                            }
                            enumData.setEnumTitleList(enumTitleList);
                            break;
                        case DATA_TYPE_SINGLE_SELECT_LIST:
                            SingleSelectListData singleSelectListData = (SingleSelectListData) typedData;
                            List<String> enumTitleList1 = new ArrayList<>();
                            for (String enumStr : singleSelectListData.getEnumList()) {
                                String emunTitle = textResource.getText(enumStr);
                                Log.d(GlobalDefinitions.DEBUG_TAG, "  enumStr:" + enumStr + "  emunTitle:" + emunTitle);
                                enumTitleList1.add(emunTitle);
                            }
                            singleSelectListData.setEnumTitleList(enumTitleList1);
                            break;
                        case DATA_TYPE_MULTI_SELECT_LIST:
                            MultiSelectListData multiSelectListData = (MultiSelectListData) typedData;
                            List<String> enumTitleList2 = new ArrayList<>();
                            for (String enumStr : multiSelectListData.getEnumList()) {
                                String emunTitle = textResource.getText(enumStr);
                                Log.d(GlobalDefinitions.DEBUG_TAG, "  enumStr:" + enumStr + "  emunTitle:" + emunTitle);
                                enumTitleList2.add(emunTitle);
                            }
                            multiSelectListData.setEnumTitleList(enumTitleList2);
                            break;
                        default:
                            break;
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
                tmpMenuData.setTypedValue(typedData);
            }else {
                Log.d(GlobalDefinitions.DEBUG_TAG,"getMenuItemDataList menuGetFunc null");
            }
            menuItemDataList.add(tmpMenuData);
        }
        return menuItemDataList;
    }

    private int refreshAssociateItemsOnBack(MenuItemXmlNode menuItemXmlNode)
    {
        int sameCmdPos = -1;
        List<Integer> associateCmdPosList = new ArrayList<Integer>();
        List<MenuItemXmlNode> associateCmdNodeList = new ArrayList<MenuItemXmlNode>();
        List<MenuItemXmlNode> preMenuItemXmlNodeList = new ArrayList<MenuItemXmlNode>();
        MenuItemXmlNode parentNode = menuItemXmlNode.parentNode;
        if(parentNode == null)
        {
            return -1;
        }
        if(parentNode.cmd == null || !(parentNode.cmd.equals(menuItemXmlNode.cmd)))
        {
            return -1;
        }
        if(parentNode.parentNode ==null || parentNode.parentNode.getChildList()==null ||parentNode.parentNode.getChildList().size()==0)
        {
            return -1;

        }
        preMenuItemXmlNodeList = parentNode.parentNode.getChildList();

        int pos =0;
        for(MenuItemXmlNode xmlNode:preMenuItemXmlNodeList)
        {
            if(xmlNode.cmd!=null && xmlNode.cmd.equals(menuItemXmlNode.cmd))
            {
                sameCmdPos =pos;
                break;
            }
            pos++;
        }
        if(sameCmdPos == -1)
        {
            Log.e(GlobalDefinitions.DEBUG_TAG," exception");
            return -1;
        }
        Log.e(GlobalDefinitions.DEBUG_TAG," sameCmdPos:"+sameCmdPos);
//        for(MenuItemXmlNode xmlNode:preMenuItemXmlNodeList)
//        {
//            if(xmlNode.cmd!=null && xmlNode.cmd.equals(menuItemXmlNode.cmd))
//            {
//                sameCmdPos =pos;
//                break;
//            }
//            pos++;
//        }
        return sameCmdPos;
    }
    /**
     * 刷新关联项
     */
    private void refreshAssociateItems(MenuItemXmlNode currentNode,List<MenuItemXmlNode> nodeList,List<MenuItemData> menuItemDataList) {
        List<String> associationList = AssociationUtils.getInstance().getAssociateNode(currentNode);
        if (associationList == null || associationList.size() == 0) {
            Log.d(GlobalDefinitions.DEBUG_TAG,"refreshAssociateItems associationList null");
        } else {
            Log.d(GlobalDefinitions.DEBUG_TAG,"refreshAssociateItems associationList size:"+associationList.size());
            List<Integer> posList = new ArrayList<Integer>();
            int pos = 0;
            for (MenuItemXmlNode menuItemXmlNodeTmp : nodeList) {
                for (String assocationTmp : associationList) {
                    if (menuItemXmlNodeTmp.cmd != null && menuItemXmlNodeTmp.cmd.equals(assocationTmp)) {
                        posList.add(pos);
                        break;
                    }
                }
                pos++;
            }
            Log.d(GlobalDefinitions.DEBUG_TAG,"refreshAssociateItems posList size:"+posList.size());
            for (Integer posTmp:posList) {
                MenuItemXmlNode nodeTmp = nodeList.get(posTmp);
                MenuFunction menuSetFunc = MenuFuncManager.getInstance().getMenuFunction(nodeTmp.cmd);
                Log.d(GlobalDefinitions.DEBUG_TAG,"refreshAssociateItems menuSetFunc:"+menuSetFunc);
                MenuFuncManager.getInstance().getMenuFunction(nodeTmp.cmd).get(nodeTmp.cmd,menuItemDataList.get(posTmp).getTypedData());
            }
            tvMenu.refreshMenuItems(posList);
        }
    }
    private boolean getIsMenuEnabled(MenuItemXmlNode currentMenuItemXmlNode)
    {
        return true;
    }
    private boolean getIsMenuItemShowed(MenuItemXmlNode currentMenuItemXmlNode)
    {
        return true;
    }
}