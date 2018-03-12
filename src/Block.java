import java.awt.Rectangle;

public class Block
{
	private BitMask _bitMask;
	private Rectangle _rectangle;

	private static int size;

	public Block(BitMask bitMask, int x, int y)
	{
		_bitMask = bitMask;
		_rectangle = new Rectangle(x * size, y * size, size, size);
	}

	public static int getSize()
	{
		return size;
	}

	public static void setSize(int size)
	{
		Block.size = size;
	}

	public BitMask getBitMask()
	{
		return _bitMask;
	}

	public void setBitMask(BitMask bitMask)
	{
		_bitMask = bitMask;
	}

	public Rectangle getRectangle()
	{
		return _rectangle;
	}

	public void setRectangle(Rectangle rectangle)
	{
		_rectangle = rectangle;
	}

}
