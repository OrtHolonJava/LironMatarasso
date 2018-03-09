
import java.awt.Point;
import java.io.File;
import java.util.HashMap;
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
	private HashMap<Point, BitMask> _hmap, _heffects, _hbackgrounds;

	public Map(int height, int width, String mapFileName, String effectsFileName, String backgroundsFileName)
	{
		_hmap = new HashMap<Point, BitMask>();
		_heffects = new HashMap<Point, BitMask>();
		_hbackgrounds = new HashMap<Point, BitMask>();
		_width = width;
		_height = height;
		readFile(mapFileName, _hmap);
		readFile(effectsFileName, _heffects);
		readFile(backgroundsFileName, _hbackgrounds);
	}

	public void setBitMasks(HashMap<Point, BitMask> map)
	{
		for (Entry<Point, BitMask> e : map.entrySet())
		{
			e.getValue().setBitMask(BitMask.computeTile(map, _width, e.getKey().y, e.getKey().x, e.getValue().getBlockID()));
		}
	}

	private void readFile(String fileName, HashMap<Point, BitMask> hmap)
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

	private void readNode(NodeList nodeList, HashMap<Point, BitMask> map)
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
							map.put(new Point(_counter % _width, _counter / _width),
									new BitMask(Integer.parseInt(node.getNodeValue()), -1));
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

	public int getCounter()
	{
		return _counter;
	}

	public void setCounter(int counter)
	{
		_counter = counter;
	}

	public HashMap<Point, BitMask> getHmap()
	{
		return _hmap;
	}

	public void setHmap(HashMap<Point, BitMask> hmap)
	{
		_hmap = hmap;
	}

	public HashMap<Point, BitMask> getHeffects()
	{
		return _heffects;
	}

	public void setHeffects(HashMap<Point, BitMask> heffects)
	{
		_heffects = heffects;
	}

	public HashMap<Point, BitMask> getHbackgrounds()
	{
		return _hbackgrounds;
	}

	public void setHbackgrounds(HashMap<Point, BitMask> hbackgrounds)
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
