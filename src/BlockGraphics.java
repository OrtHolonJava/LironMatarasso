import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * BlockGraphics class for drawing various blocks
 * 
 * @author liron
 *
 */
public class BlockGraphics
{
	private Img _blockImage;
	private int _id;
	private String _path;
	private static LinkedList<String> _imgPaths = new LinkedList<String>(Arrays.asList(	"images//SandBackground.png",
																						"images//StoneBackground.png"));
	private static int size;

	/**
	 * Init a new BlockGraphics object with the following parameters:
	 * 
	 * @param path
	 */
	public BlockGraphics(String path)
	{
		_path = path;
		_blockImage = new Img(_path, 0, 0, size, size);
	}

	/**
	 * Init a new BlockGraphics object with the following parameters:
	 * 
	 * @param id
	 */
	public BlockGraphics(int id)
	{
		_path = _imgPaths.get(id);
		_blockImage = new Img(_path, 0, 0, size, size);
	}

	/**
	 * draws the block at the given coordinates
	 * 
	 * @param g
	 * @param x
	 * @param y
	 */
	public void drawtAt(Graphics g, int x, int y)
	{
		_blockImage.setImgCords(x * size, y * size);
		_blockImage.drawImg(g);
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
