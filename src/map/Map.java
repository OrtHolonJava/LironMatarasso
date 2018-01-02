package map;

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
public class Map {
	private int _size;
	private int _counter = 0;
	private int[][] _map;
	private int[][] _effects;

	public Map(int size, int sizeW, String mapFileName,String effectsFileName) {
		_effects =new int[size][sizeW];
		_map = new int[size][sizeW];
		_size = sizeW;

		try {
			File file = new File(mapFileName);

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = docBuilder.parse(file);

			if (doc.hasChildNodes()) {
				readNode(doc.getChildNodes(),_map);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		try {
			File file = new File(effectsFileName);

			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = docBuilder.parse(file);
			_counter=0;
			if (doc.hasChildNodes()) {
				readNode(doc.getChildNodes(),_effects);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int[][] getMap() {
		return _map;
	}
	public int[][] getEffects()
	{
		return _effects;
	}

	private void readNode(NodeList nodeList,int[][] mat) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);

			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.hasAttributes()) {
					NamedNodeMap nodeMap = tempNode.getAttributes();
					for (int i = 0; i < nodeMap.getLength(); i++) {
						Node node = nodeMap.item(i);
						mat[_counter / _size][_counter % _size] = Integer.parseInt(node.getNodeValue());
						_counter++;
					}
				}

				if (tempNode.hasChildNodes()) {
					readNode(tempNode.getChildNodes(),mat);
				}
			}
		}
	}
		
		public static int getElementCountByName(String fileName,String name)
		{
			try{
		
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(fileName);
			NodeList list = doc.getElementsByTagName(name);
			return  list.getLength();
			}
			catch(Exception e)
			{
				System.out.println("exception: "+e.getMessage());
				return -1;
			}	
		}
}
