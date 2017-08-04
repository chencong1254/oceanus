package skyworth.skyworthlivetv.osd.common.data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by xeasy on 2017/5/22.
 */

public class MultiSelectListData extends TypedData{
    private int enumCount = 0;
    private List<String> enumList = null;
    private List<String> enumTitleList = null;
    private Set<Integer> selectedIndexList = new HashSet<>();

    public MultiSelectListData()
    {
        super(SkyDataType.DATA_TYPE_MULTI_SELECT_LIST);
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
        selectedIndexList.clear();
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

    public void selectedIndexAdd(int selectIndex)
    {
        if(enumList!=null  &&  selectIndex < enumList.size())
        {
            selectedIndexList.add(selectIndex);
        }
    }
    public void selectedIndexDel(int selectIndex)
    {
        if(enumList!=null  &&  selectIndex < enumList.size())
        {
            selectedIndexList.remove(selectIndex);
        }
    }
    public void selectedIndexToggle(int selectIndex)
    {
        if(enumList!=null  &&  selectIndex < enumList.size())
        {
            if(selectedIndexList.contains(selectIndex))
            {
                selectedIndexList.remove(selectIndex);
            }else {
                selectedIndexList.add(selectIndex);
            }
        }
    }
    public boolean isSelected(int position)
    {
        return selectedIndexList.contains(position);
    }
    public Set<Integer> getSelectedIndexList()
    {
        return this.selectedIndexList;
    }
}