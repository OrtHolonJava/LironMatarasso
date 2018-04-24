import java.awt.Rectangle;

/**
 * Block class for holding the info of the blocks in the map
 * 
 * @author liron
 *
 */
public class Block
{
	private BlockRelativeInfo _blockRelativeInfo;
	private Rectangle _rectangle;

	private static int size;

	/**
	 * Init a new Block object with the following parameters:
	 * 
	 * @param blockRelativeInfo
	 * @param x
	 * @param y
	 */
	public Block(BlockRelativeInfo blockRelativeInfo, int x, int y)
	{
		_blockRelativeInfo = blockRelativeInfo;
		_rectangle = new Rectangle(x * size, y * size, size, size);
	}

	public static void setSize(int size)
	{
		Block.size = size;
	}

	public BlockRelativeInfo getBlockRelativeInfo()
	{
		return _blockRelativeInfo;
	}

	public void setBlockRelativeInfo(BlockRelativeInfo blockRelativeInfo)
	{
		_blockRelativeInfo = blockRelativeInfo;
	}

	public Rectangle getRectangle()
	{
		return _rectangle;
	}

	public void setRectangle(Rectangle rectangle)
	{
		_rectangle = rectangle;
	}

	public static int getSize()
	{
		return size;
	}

	@Override
	public String toString()
	{
		return _blockRelativeInfo.toString() + " " + _rectangle.toString();
	}

}
