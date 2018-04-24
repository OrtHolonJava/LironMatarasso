
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.util.LinkedList;

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
	private Block[][] _hmap, _hbackgrounds;
	private LinkedList<Point> _clearBlock;
	private static DjikstraVertex[][] _dMap;
	private Rectangle _mapBorders;

	/**
	 * Init a new Map object with the following parameters:
	 * 
	 * @param height
	 * @param width
	 * @param mapFileName
	 * @param backgroundsFileName
	 */
	public Map(int height, int width, String mapFileName, String backgroundsFileName)
	{
		_width = width;
		_height = height;
		_hmap = new Block[_height][_width];
		_hbackgrounds = new Block[_height][_width];
		_clearBlock = new LinkedList<Point>();
		_mapBorders = new Rectangle(0, 0, width * Block.getSize(), height * Block.getSize());
		readFile(mapFileName, _hmap);
		readFile(backgroundsFileName, _hbackgrounds);
		_dMap = new DjikstraVertex[_height][_width];
		for (int y = 0; y < _height; y++)
		{
			for (int x = 0; x < _width; x++)
			{
				_dMap[y][x] = new DjikstraVertex(x, y, _hmap[y][x] == null
						|| GamePanel.getPassables().contains(_hmap[y][x].getBlockRelativeInfo().getBlockID()), 0, 0);
				if (_hmap[y][x] == null)
				{
					_clearBlock.add(new Point(x, y));
				}
			}
		}
	}

	/**
	 * prints a block matrix
	 * 
	 * @param mat
	 */
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

	/**
	 * computes all the relative info of all the blocks in the given matrix
	 * 
	 * @param map
	 */
	public void setBlockRelativeInfo(Block[][] map)
	{
		for (int y = 0; y < map.length; y++)
		{
			for (int x = 0; x < map[y].length; x++)
			{
				if (map[y][x] != null)
					map[y][x]	.getBlockRelativeInfo()
								.setBitMask(BlockRelativeInfo.computeBlock(	map, _width, y, x,
																			map[y][x].getBlockRelativeInfo().getBlockID()));
			}
		}
	}

	/**
	 * reads an xml file and fills the block matrix
	 * 
	 * @param fileName
	 * @param hmap
	 */
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
			setBlockRelativeInfo(hmap);
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * reads a nodelist and fills the block matrix
	 * 
	 * @param nodeList
	 * @param map
	 */
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
							map[_counter / _width][_counter
									% _width] = new Block(	new BlockRelativeInfo(Integer.parseInt(node.getNodeValue()), -1),
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

	/**
	 * 
	 * @param fileName
	 * @param name
	 * @return the amount of time the given element appears in the xml file
	 */
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

	public static DjikstraVertex[][] getdMap()
	{
		return _dMap;
	}

	public static void setdMap(DjikstraVertex[][] dMap)
	{
		_dMap = dMap;
	}

	public static DjikstraVertex[][] get_dMap()
	{
		return _dMap;
	}

	public static void set_dMap(DjikstraVertex[][] _dMap)
	{
		Map._dMap = _dMap;
	}

	public Rectangle getMapBorders()
	{
		return _mapBorders;
	}

	public void setMapBorders(Rectangle mapBorders)
	{
		_mapBorders = mapBorders;
	}

	public LinkedList<Point> getClearBlock()
	{
		return _clearBlock;
	}

	public void setClearBlock(LinkedList<Point> clearBlock)
	{
		_clearBlock = clearBlock;
	}
}
