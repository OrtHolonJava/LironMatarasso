import java.awt.Rectangle;

public class Block
{
	private BlockType _blockType;
	private BitMask _bitMask;
	private Rectangle _rectangle;

	public Block(BlockType blockType, BitMask bitMask, Rectangle rectangle)
	{
		_blockType = blockType;
		_bitMask = bitMask;
		_rectangle = rectangle;
	}

	public BlockType getBlockType()
	{
		return _blockType;
	}

	public void setBlockType(BlockType blockType)
	{
		_blockType = blockType;
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
