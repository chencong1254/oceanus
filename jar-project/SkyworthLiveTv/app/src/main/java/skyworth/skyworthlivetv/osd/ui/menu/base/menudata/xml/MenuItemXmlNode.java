package skyworth.skyworthlivetv.osd.ui.menu.base.menudata.xml;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class MenuItemXmlNode implements Serializable
{
	private static final long serialVersionUID = 1L;
	public String name = null;   // 名称, to ensure uniqueness of name ,in order to act as id two.
	// 类型  "TYPE_ROOT":根;  "TYPE_FLIPOUT":弹出非菜单的UI  ;  // "TYPE_TITLE": 仅显示
	public String type ="";
	public String association = null;  // 关联项，调节此项的时候，被关联的项会刷新值
	public String cmd = null;  // 此菜单UI对应的命令
	public boolean needRefresh = false;  //是否需要刷新

	public MenuItemXmlNode parentNode;
	public List<MenuItemXmlNode> childList;
	public int level = -1;
	
	public MenuItemXmlNode()
	{
		
	}
	
	/**
	 * 是否有子节点
	 * @return
	 */
	public boolean hasChildNode()
	{
		return (childList != null && childList.size() > 0);
	}
	
	public void addChild(MenuItemXmlNode node)
	{
		if(childList == null)
			childList = new LinkedList<MenuItemXmlNode>();
		childList.add(node);
	}
	
	public List<MenuItemXmlNode> getChildList()
	{
		return childList;
	}
	
	/**
	 * 获取子节点在父节点中的索引，如果没有找到，则返回0
	 * @param childNode
	 * @return
	 */
	public int getChildNodeIndex(MenuItemXmlNode childNode)
	{
		int index = 0;
		if(childList != null && childNode != null)
		{
			int size = childList.size();
			for(int i=0; i<size; i++)
			{
				if(childList.get(i).name.equals(childNode.name))
				{
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
	/**
	 * 获取对应名称的子节点
	 * @param childName
	 * @return
	 */
	public MenuItemXmlNode getChildNode(String childName)
	{
		MenuItemXmlNode retNode = null;
		if(childName == null || childName.equals(""))
		{
			retNode = null;
		} else
		{
			if(name.equals(childName))
			{
				retNode = this;
			} else
			{
				if(childList == null || childList.size() == 0)
				{
//					retNode = null;
				} else
				{
					for(MenuItemXmlNode node : childList)
					{
						if(node.name.equals(childName))
						{
							retNode = node;
							break;
						} else
						{
							MenuItemXmlNode tmpNode = node.getChildNode(childName);
							if(tmpNode != null)
							{
								retNode = tmpNode;
								break;
							}
						}
					}
				}
			}
		}
		return retNode;
	}
	
	@Override
	public String toString()
	{
		return "name=" + name + "; type=" + type + "; level=" + level;
	}
	
	public void print()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append("@");
		sb.append(type);
		sb.append("@");
		sb.append(level);
		for(int i=0; i<level; i++)
		{
			System.out.print("\t");
		}
		System.out.println(sb.toString());
		if(hasChildNode())
		{
			int size = childList.size();
			for(int i=0; i<size; i++)
			{
				childList.get(i).print();
			}
			System.out.println("");
		} else
		{

		}
	}
	
	public static void main(String[] args)
	{
		MenuItemXmlNode rootNode = new MenuItemXmlNode();
		rootNode.name = "rootNode";
		
		MenuItemXmlNode node1 = new MenuItemXmlNode();
		node1.name = "node1";
		rootNode.addChild(node1);
		
		MenuItemXmlNode node2 = new MenuItemXmlNode();
		node2.name = "node2";
		rootNode.addChild(node2);
		
		MenuItemXmlNode node21 = new MenuItemXmlNode();
		node21.name = "node21";
		node2.addChild(node21);		
		
		MenuItemXmlNode node3 = new MenuItemXmlNode();
		node3.name = "node3";
		rootNode.addChild(node3);
		
		
		MenuItemXmlNode node = rootNode.getChildNode("node21");
		
		if(node == null)
		{
			System.out.println("null");
		} else
		{
			System.out.println("nn : " + node);
		}
	}
}