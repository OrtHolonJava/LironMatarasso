
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

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
	private int _width, _height;
	private int _counter = 0;
	private HashMap<Integer, BitMask> _hmap, _heffects, _hbackgrounds;

	public Map(int height, int width, String mapFileName, String effectsFileName, String backgroundsFileName)
	{

		_hmap = new HashMap<Integer, BitMask>();
		_heffects = new HashMap<Integer, BitMask>();
		_hbackgrounds = new HashMap<Integer, BitMask>();
		_width = width;
		_height = height;
		readFile(mapFileName, _hmap);
		readFile(effectsFileName, _heffects);
		readFile(backgroundsFileName, _hbackgrounds);
	}

	public void setBitMasks(HashMap<Integer, BitMask> map)
	{
		for (Entry<Integer, BitMask> e : map.entrySet())
		{
			e.getValue().setBitMask(BitMask.computeTile(map, _width, e.getKey() / _width, e.getKey() % _width, e.getValue().getBlockID()));
		}
	}

	private void readFile(String fileName, HashMap<Integer, BitMask> hmap)
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
			setBitMasks(hmap);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	public int getCounter()
	{
		return _counter;
	}

	public void setCounter(int counter)
	{
		_counter = counter;
	}

	public HashMap<Integer, BitMask> getHmap()
	{
		return _hmap;
	}

	public void setHmap(HashMap<Integer, BitMask> hmap)
	{
		_hmap = hmap;
	}

	public HashMap<Integer, BitMask> getHeffects()
	{
		return _heffects;
	}

	public void setHeffects(HashMap<Integer, BitMask> heffects)
	{
		_heffects = heffects;
	}

	private void readNode(NodeList nodeList, HashMap<Integer, BitMask> map)
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
							map.put(_counter, new BitMask(Integer.parseInt(node.getNodeValue()), -1));
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

	public HashMap<Integer, BitMask> getHbackgrounds()
	{
		return _hbackgrounds;
	}

	public void setHbackgrounds(HashMap<Integer, BitMask> hbackgrounds)
	{
		_hbackgrounds = hbackgrounds;
	}

	public int getWidth()
	{
		return _width;
	}

	public void setWidth(int width)
	{
		_width = width;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setHeight(int height)
	{
		_height = height;
	}

}
