package skyworth.skyworthlivetv.osd.common.data;

import android.util.Log;

import java.util.List;

import skyworth.skyworthlivetv.global.GlobalDefinitions;

/**
 * Created by xeasy on 2017/5/23.
 */

public class SingleSelectListData extends TypedData{
    private int enumCount = 0;
    private List<String> enumList = null;
    private List<String> enumTitleList = null;
    private String current = null;
    private int currentIndex = -1;

    public SingleSelectListData()
    {
        super(TypedData.SkyDataType.DATA_TYPE_SINGLE_SELECT_LIST);
    }

    public int getEnumCount()
    {
        return enumCount;
    }

    public void setEnumCount(int enumCount)
    {
        this.enumCount = enumCount;
    }

    public List<String> getEnumList()
    {
        return enumList;
    }

    public void setEnumList(List<String> enumList)
    {
        this.enumList = enumList;
        if (enumList != null)
        {
            this.enumCount = enumList.size();
        }
    }
    public List<String> getEnumTitleList()
    {
        return enumTitleList;
    }

    public void setEnumTitleList(List<String> enumTitleList)
    {
        this.enumTitleList = enumTitleList;
    }


    public String getCurrent()
    {
        return current;
    }

    public void setCurrent(String current)
    {
        this.current = current;
        if(enumList != null){
            for (int i = 0; i < enumList.size(); i++){
                if(enumList.get(i).equals(current)){
                    this.currentIndex = i;
                    break;
                }
            }
        }
    }

    public int getCurrentIndex()
    {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex)
    {
        Log.d(GlobalDefinitions.DEBUG_TAG, "currentindex=" + currentIndex);
        if (currentIndex > -1)
        {
            this.currentIndex = currentIndex;
            if(enumList != null){
                try{
                    this.current = this.enumList.get(currentIndex);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
