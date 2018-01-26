import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;

import images.Img;

public class BlockType
{
	private Img _blockImage;
	private int _id;
	private String _path;
	private static LinkedList<String> _imgPaths = new LinkedList<String>(
			Arrays.asList("images//SandBlock2.png", "images/StoneBlock2.png", "images//OneSW.png",
					"images//SandBackground.png", "images//StoneBackground.png"));
	private static int size;

	public BlockType(String path)
	{
		_path = path;
		_blockImage = new Img(_path, 0, 0, size, size);
	}

	public BlockType(int id)
	{
		_path = _imgPaths.get(id - 1);
		_blockImage = new Img(_path, 0, 0, size, size);
	}

	public void paintAt(Graphics g, int x, int y)
	{
		_blockImage.setImgCords(x * size, y * size);
		_blockImage.drawImg(g);
	}

	public static void paintSand(Graphics g, int x, int y, int index)
	{
		new Img(_imgPaths.get(index - 1 + 5), x * size, y * size, size, size).drawImg(g);
	}

	public Img getBlockImage()
	{
		return _blockImage;
	}

	public void setBlockImage(Img blockImage)
	{
		_blockImage = blockImage;
	}

	public static int getSize()
	{
		return size;
	}

	public static void setSize(int s)
	{
		size = s;
	}

	public int getId()
	{
		return _id;
	}

	public void setId(int id)
	{
		_id = id;
	}

}
