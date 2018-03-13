
import java.io.File;

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
	private Block[][] _hmap, _heffects, _hbackgrounds;

	public Map(int height, int width, String mapFileName, String effectsFileName, String backgroundsFileName)
	{
		_width = width;
		_height = height;
		_hmap = new Block[_height][_width];
		_heffects = new Block[_height][_width];
		_hbackgrounds = new Block[_height][_width];
		readFile(mapFileName, _hmap);
		readFile(effectsFileName, _heffects);
		readFile(backgroundsFileName, _hbackgrounds);
	}

	public void printMat(Block[][] mat)
	{
		for (int y = 0; y < mat.length; y++)
		{
			for (int x = 0; x < mat[y].length; x++)
			{
				if (mat[y][x] != null)
				{
					System.out.println(mat[y][x].toString());
				}
			}
		}
	}

	public void setBitMasks(Block[][] map)
	{
		for (int y = 0; y < map.length; y++)
		{
			for (int x = 0; x < map[y].length; x++)
			{
				if (map[y][x] != null)
					map[y][x].getBitMask().setBitMask(BitMask.computeTile(map, _width, y, x, map[y][x].getBitMask().getBlockID()));
			}
		}
	}

	private void readFile(String fileName, Block[][] hmap)
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

	private void readNode(NodeList nodeList, Block[][] map)
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
							map[_counter / _width][_counter % _width] = new Block(	new BitMask(Integer.parseInt(node.getNodeValue()), -1),
																					_counter % _width, _counter / _width);
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

	public Block[][] getHmap()
	{
		return _hmap;
	}

	public void setHmap(Block[][] hmap)
	{
		_hmap = hmap;
	}

	public Block[][] getHeffects()
	{
		return _heffects;
	}

	public void setHeffects(Block[][] heffects)
	{
		_heffects = heffects;
	}

	public Block[][] getHbackgrounds()
	{
		return _hbackgrounds;
	}

	public void setHbackgrounds(Block[][] hbackgrounds)
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
