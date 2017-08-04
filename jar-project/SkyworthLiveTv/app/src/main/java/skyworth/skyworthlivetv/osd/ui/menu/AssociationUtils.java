package skyworth.skyworthlivetv.osd.ui.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import skyworth.skyworthlivetv.osd.ui.menu.base.menudata.xml.MenuItemXmlNode;

/**
 * Created by xeasy on 2017/5/18.
 */

public class AssociationUtils {
    private WeakHashMap<String, List<String>> cache;

    private static AssociationUtils instance = new AssociationUtils();

    private AssociationUtils()
    {
        cache = new WeakHashMap<String, List<String>>();
    }

    public static AssociationUtils getInstance()
    {
        return instance;
    }

    /**
     * 获取node的关联项目
     * @param node
     */
    public List<String> getAssociateNode(MenuItemXmlNode node)
    {
        List<String> associateNodeList = cache.get(node.name);
        if(associateNodeList == null)
        {
            if(node.association != null)
            {
                String[] associations = node.association.split(",");
                if(associations != null  && associations.length > 0)
                {
                    associateNodeList = new ArrayList<String>(associations.length);
                    for(String s : associations)
                    {
                        associateNodeList.add(s.trim());
                    }
                    cache.put(node.name, associateNodeList);
                }
            }
        }
        return associateNodeList;
    }
}