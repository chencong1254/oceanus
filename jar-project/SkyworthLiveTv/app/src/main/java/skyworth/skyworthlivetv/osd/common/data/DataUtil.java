package skyworth.skyworthlivetv.osd.common.data;

import android.util.Log;
import android.util.Pair;


/**
 * Created by xeasy on 2017/5/11.
 */

public class DataUtil {
    public static boolean changeMenuItemData(TypedData typedData,boolean leftOrRight)
    {
        if(typedData == null || typedData.getType() == null)
        {
            return false;
        }
        TypedData.SkyDataType dataType;
        try {
            dataType = typedData.getType();
            switch(dataType)
            {
                case DATA_TYPE_RANGE:
                    RangeData rangeData = (RangeData)typedData;
                    int current = rangeData.getCurrent();
                    if(leftOrRight)
                    {
                        if(current>rangeData.getMin())
                        {
                            rangeData.setCurrent(current-1);
                            return true;
                        }
                    }else {
                        if(current<rangeData.getMax())
                        {
                            rangeData.setCurrent(current+1);
                            return true;
                        }
                    }
                    break;
                case DATA_TYPE_ENUM:
                    EnumData enumData = (EnumData)typedData;
                    int currentIndex = enumData.getCurrentIndex();
                    if(leftOrRight)
                    {
                        if(currentIndex>0)
                        {
                            enumData.setCurrentIndex(currentIndex -1);
                            return true;
                        }else {
                            int enumCount = enumData.getEnumCount();
                            if(enumCount>1) {
                                enumData.setCurrentIndex(enumCount - 1);
                                return true;
                            }
                        }
                    }else {
                        int enumCount = enumData.getEnumCount();
                        if(enumCount>1)
                        {
                            if(currentIndex<enumCount-1)
                            {
                                enumData.setCurrentIndex(currentIndex+1);
                                return true;
                            }else {
                                enumData.setCurrentIndex(0);
                                return true;//this will cause different answer between left key and right key
                            }
                        }
                    }
                    break;
                case DATA_TYPE_SWITCH:
                    SwitchData switchData = (SwitchData)typedData;
                    boolean isOn = switchData.isOn();
                    switchData.setOn(!isOn);
                    boolean isOn1 = switchData.isOn();
                    Log.d(skyworth.skyworthlivetv.global.GlobalDefinitions.DEBUG_TAG,"switchData  isOn:"+isOn+"  isOn1:"+isOn1);
                    return true;
                default:
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return false;
    }

    public static boolean changeSelectItemData(TypedData typedData,int itemIndex)
    {
        switch(typedData.getType())
        {
            case DATA_TYPE_SINGLE_SELECT_LIST:
                SingleSelectListData singleSelectListData = (SingleSelectListData)typedData;
                if(singleSelectListData.getCurrentIndex() == itemIndex)
                {
                    return false;
                }else {
                    singleSelectListData.setCurrentIndex(itemIndex);
                    return true;
                }
            case DATA_TYPE_MULTI_SELECT_LIST:
                MultiSelectListData multiSelectListData = (MultiSelectListData)typedData;
                multiSelectListData.selectedIndexToggle(itemIndex);
                return true;
        }
        return false;
    }

    public static void getTypedDataFromSameDataType(TypedData typedDataFrom,TypedData typedDataTo)
    {
        switch (typedDataTo.getType())
        {
            case DATA_TYPE_RANGE:
                RangeData  rangeData = (RangeData)typedDataTo ;
                RangeData  menuItemRangeData = (RangeData)typedDataFrom;
                rangeData.setCurrent(menuItemRangeData.getCurrent());
                break;
            case DATA_TYPE_ENUM:
                EnumData enumData =  (EnumData)typedDataTo ;
                EnumData  menuItemEnumData = (EnumData)typedDataFrom;
                enumData.setCurrentIndex(menuItemEnumData.getCurrentIndex());
                break;
            case DATA_TYPE_SINGLE_SELECT_LIST:
                SingleSelectListData singleSelectListData =  (SingleSelectListData)typedDataTo ;
                SingleSelectListData  menuItemSingleSelectListData= (SingleSelectListData)typedDataFrom;
                singleSelectListData.setCurrentIndex(menuItemSingleSelectListData.getCurrentIndex());
                break;
            case DATA_TYPE_MULTI_SELECT_LIST:
                break;
            case  DATA_TYPE_SWITCH:
                SwitchData switchData =  (SwitchData)typedDataTo ;
                SwitchData  menuItemSwitchData = (SwitchData)typedDataFrom;
                switchData.setOn(menuItemSwitchData.isOn());
                break;
            default:
                break;
        }
    }
}