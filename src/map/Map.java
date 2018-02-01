package map;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Map class for the map of the game
 * 
 * @author liron
 *
 */
public class Map
{
	private int _size;
	private int _counter = 0;
	private HashMap<Integer, Integer> _hmap, _heffects, _hbackgrounds;

	public Map(int size, int sizeW, String mapFileName, String effectsFileName, String backgroundsFileName)
	{
		_hmap = new HashMap<Integer, Integer>();
		_heffects = new HashMap<Integer, Integer>();
		_hbackgrounds = new HashMap<Integer, Integer>();
		_size = sizeW;
		readFile(mapFileName, _hmap);
		readFile(effectsFileName, _heffects);
		readFile(backgroundsFileName, _hbackgrounds);
	}

	private void readFile(String fileName, HashMap<Integer, Integer> hmap)
	{
		_counter = 0;
		try
		{
			File file = new File(fileName);

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = docBuilder.parse(file);

			/*
			 * if (doc.hasChildNodes()) { readNode(doc.getChildNodes(), _map); }
			 * _counter = 0;
			 */
			if (doc.hasChildNodes())
			{
				readNode(doc.getChildNodes(), hmap);
			}

		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public int getSize()
	{
		return _size;
	}

	public void setSize(int size)
	{
		_size = size;
	}

	public int getCounter()
	{
		return _counter;
	}

	public void setCounter(int counter)
	{
		_counter = counter;
	}

	public HashMap<Integer, Integer> getHmap()
	{
		return _hmap;
	}

	public void setHmap(HashMap<Integer, Integer> hmap)
	{
		_hmap = hmap;
	}

	public HashMap<Integer, Integer> getHeffects()
	{
		return _heffects;
	}

	public void setHeffects(HashMap<Integer, Integer> heffects)
	{
		_heffects = heffects;
	}

	private void readNode(NodeList nodeList, HashMap<Integer, Integer> map)
	{
		for (int count = 0; count < nodeList.getLength(); count++)
		{
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE)
			{
				if (tempNode.hasAttributes())
				{
					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++)
					{
						Node node = nodeMap.item(i);
						if (Integer.parseInt(node.getNodeValue()) != 0)
						{
							map.put(_counter, Integer.parseInt(node.getNodeValue()));
						}
						_counter++;
					}
				}
				if (tempNode.hasChildNodes())
				{
					readNode(tempNode.getChildNodes(), map);
				}
			}
		}
	}

	public static int getElementCountByName(String fileName, String name)
	{
		try
		{

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);
			NodeList list = doc.getElementsByTagName(name);
			return list.getLength();
		}
		catch (Exception e)
		{
			System.out.println("exception: " + e.getMessage());
			return -1;
		}
	}

	public HashMap<Integer, Integer> getHbackgrounds()
	{
		return _hbackgrounds;
	}

	public void setHbackgrounds(HashMap<Integer, Integer> hbackgrounds)
	{
		_hbackgrounds = hbackgrounds;
	}

}
