package skyworth.skyworthlivetv.osd.ui.menu.base.menudata.xml;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import skyworth.skyworthlivetv.global.GlobalDefinitions;


public class MenuItemXmlParser
{
	private DocumentBuilder docBuilder;
	private Document mDoc;

	public MenuItemXmlParser()
	{
		try
		{
			docBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
		} catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 解析菜单条目的xml文件，并返回根节点.
	 * @param file
	 * @return
	 */
	public MenuItemXmlNode parse(File file)
	{
		initDoc(file);
		return innerParse();
	}

	/**
	 * 解析菜单条目的xml文件，并返回根节点.
	 * @param filePath
	 * @return
	 */
	public MenuItemXmlNode parse(String filePath)
	{
		Log.d(GlobalDefinitions.DEBUG_TAG,"Parse XML : " + filePath);
		initDoc(new File(filePath));
		return innerParse();
	}
	
	/*
	 * 解析菜单条目的xml文件流，并返回根节点.
	 * @param filePath
	 * @return
	 */
	public MenuItemXmlNode parse(InputStream is)
	{
		initDoc(is);
		return innerParse();
	 }
	private void initDoc(Object obj)
	{
		if (docBuilder != null && mDoc == null)
		{
			try
			{
				if(obj instanceof InputStream)
					mDoc = docBuilder.parse((InputStream) obj);
				else if(obj instanceof File)
					mDoc = docBuilder.parse((File) obj);
			} catch (SAXException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	private MenuItemXmlNode innerParse()
	{
		Long start = System.currentTimeMillis();
		Element rootElement = mDoc.getDocumentElement();
		MenuItemXmlNode rootItem = new MenuItemXmlNode();
		rootItem.level = 0;
		recursiveParseNode(rootItem, rootElement, 0);
		Long end = System.currentTimeMillis();
		Log.d(GlobalDefinitions.DEBUG_TAG,"parse cost : " + (end - start) + " ms.");
		Log.d(GlobalDefinitions.DEBUG_TAG,"!!!!!! " + "parse cost : " + (end - start) + " ms.");
		return rootItem;
	}
	
	/**
	 * 递归解析节点
	 * @param node 当前数据节点
	 * @param element 当前xml节点
	 * @param level 当前节点的等级，0为root节点
	 * @return 该节点是否支持，false表示该节点不再支持
	 */
	private boolean recursiveParseNode(MenuItemXmlNode node, Element element, int level)
	{
		boolean ret = true;
		NamedNodeMap map = element.getAttributes();
		// 根据xml 条目属性来赋值
		if (null != map)
        {
			int mapCount = map.getLength();
            for (int i = 0; i < mapCount; i++ )
            {
                Attr attr = (Attr)map.item(i) ;
                String getName = attr.getName() ;
                String getValue = attr.getValue() ;
                Log.d(GlobalDefinitions.DEBUG_TAG,"recursiveParseNode getName:"+getName+"  getValue:"+getValue);
                try
				{
					MenuItemXmlNode.class.getField(getName).set(node, getValue);
				} catch (NoSuchFieldException e)
				{
					e.printStackTrace();
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
        }

		// 递归解析树状目录
		int childCount = element.getChildNodes().getLength();
		Log.d(GlobalDefinitions.DEBUG_TAG,"recursiveParseNode childCount:"+childCount);
		if(childCount <= 0)
		{
			// 该条目下没有子条目
		} else
		{ // 该条目有子条目
			int childLevel = level+1;
			for (int j = 0; j < childCount; j++ )
	        {
	            Node childNode = element.getChildNodes().item(j) ;
	            MenuItemXmlNode tmpItem = new MenuItemXmlNode();
	            tmpItem.level = childLevel;
	            tmpItem.parentNode = node;
	            //判断该节点是不是元素节点  ，递归路口
				Log.d(GlobalDefinitions.DEBUG_TAG,"recursiveParseNode childNode.getNodeType:"+childNode.getNodeType());
	            if (childNode.getNodeType() == Node.ELEMENT_NODE)
	            {
	            	if(recursiveParseNode(tmpItem, (Element)childNode, childLevel))
	            		node.addChild(tmpItem);
	            }
	        }
		}

		return ret;
	}
	public static void main(String[] args)
	{
		MenuItemXmlParser parser = new MenuItemXmlParser();
//		try
//		{
////			List<MenuParentItem> menuParentItemList = parser.parseXml(new FileInputStream("D:\\work\\atv_menu.xml"));
//			parser.parse(new FileInputStream("D:\\work\\atv_menu.xml")).print();
//		} catch (FileNotFoundException e)
//		{
//			e.printStackTrace();
//		}
	}

}
