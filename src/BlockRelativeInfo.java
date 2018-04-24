import java.util.HashMap;

/**
 * BlockRelativeInfo class for storing block info and its relation with the
 * blocks surrounding it
 * 
 * @author liron
 *
 */
public class BlockRelativeInfo
{
	private int _blockID;
	private int _bitMask;
	public static HashMap<Integer, Integer> keyMap = setKeyMap();

	/**
	 * Init a new BlockRelativeInfo object with the following parameters:
	 * 
	 * @param blockID
	 * @param bitMask
	 */
	public BlockRelativeInfo(int blockID, int bitMask)
	{
		_blockID = blockID;
		_bitMask = bitMask;
	}

	/**
	 * 
	 * @return a dictionary with various block relation situations that are
	 *         drawn as other situations
	 */
	public static HashMap<Integer, Integer> setKeyMap()
	{
		HashMap<Integer, Integer> keyMap = new HashMap<Integer, Integer>();
		keyMap.put(2, 1);
		keyMap.put(8, 2);
		keyMap.put(10, 3);
		keyMap.put(11, 4);
		keyMap.put(16, 5);
		keyMap.put(18, 6);
		keyMap.put(22, 7);
		keyMap.put(24, 8);
		keyMap.put(26, 9);
		keyMap.put(27, 10);
		keyMap.put(30, 11);
		keyMap.put(31, 12);
		keyMap.put(64, 13);
		keyMap.put(66, 14);
		keyMap.put(72, 15);
		keyMap.put(74, 16);
		keyMap.put(75, 17);
		keyMap.put(80, 18);
		keyMap.put(82, 19);
		keyMap.put(86, 20);
		keyMap.put(88, 21);
		keyMap.put(90, 22);
		keyMap.put(91, 23);
		keyMap.put(94, 24);
		keyMap.put(95, 25);
		keyMap.put(104, 26);
		keyMap.put(106, 27);
		keyMap.put(107, 28);
		keyMap.put(120, 29);
		keyMap.put(122, 30);
		keyMap.put(123, 31);
		keyMap.put(126, 32);
		keyMap.put(127, 33);
		keyMap.put(208, 34);
		keyMap.put(210, 35);
		keyMap.put(214, 36);
		keyMap.put(216, 37);
		keyMap.put(218, 38);
		keyMap.put(219, 39);
		keyMap.put(222, 40);
		keyMap.put(223, 41);
		keyMap.put(248, 42);
		keyMap.put(250, 43);
		keyMap.put(251, 44);
		keyMap.put(254, 45);
		keyMap.put(255, 46);
		keyMap.put(0, 47);
		return keyMap;
	}

	/**
	 * checks if the coordinates are inside the map,there is a block in the map
	 * and the block id is equal to the given id
	 * 
	 * @param map
	 * @param size
	 * @param x
	 * @param y
	 * @param blockID
	 * @return
	 */
	public static int placeMeeting(Block[][] map, int size, int x, int y, int blockID)
	{
		return (Logic.inBounds(x, y, map[0].length, map.length) && map[y][x] != null
				&& map[y][x].getBlockRelativeInfo()._blockID == blockID) ? 1 : 0;
	}

	/**
	 * computes the block bitmask value in relation to the surrounding blocks
	 * 
	 * @param map
	 * @param size
	 * @param y
	 * @param x
	 * @param blockID
	 * @return
	 */
	public static int computeBlock(Block[][] map, int size, int y, int x, int blockID)
	{
		int sum = 0;
		int north_tile = placeMeeting(map, size, x, y - 1, blockID);
		int south_tile = placeMeeting(map, size, x, y + 1, blockID);
		int west_tile = placeMeeting(map, size, x - 1, y, blockID);
		int east_tile = placeMeeting(map, size, x + 1, y, blockID);
		int north_west_tile = (west_tile == 1 && north_tile == 1 && placeMeeting(map, size, x - 1, y - 1, blockID) == 1) ? 1 : 0;
		int north_east_tile = (north_tile == 1 && east_tile == 1 && placeMeeting(map, size, x + 1, y - 1, blockID) == 1) ? 1 : 0;
		int south_west_tile = (south_tile == 1 && west_tile == 1 && placeMeeting(map, size, x - 1, y + 1, blockID) == 1) ? 1 : 0;
		int south_east_tile = (south_tile == 1 && east_tile == 1 && placeMeeting(map, size, x + 1, y + 1, blockID) == 1) ? 1 : 0;

		// 8 bit Bitmasking calculation using Directional check booleans values
		sum = north_west_tile + (north_tile << 1) + (north_east_tile << 2) + (west_tile << 3) + (east_tile << 4) + (south_west_tile << 5)
				+ (south_tile << 6) + (south_east_tile << 7);

		if (keyMap.containsKey(sum))
			return keyMap.get(sum);
		return sum;
	}

	public int getBlockID()
	{
		return _blockID;
	}

	public void setBlockID(int blockID)
	{
		_blockID = blockID;
	}

	public int getBitMask()
	{
		return _bitMask;
	}

	public void setBitMask(int bitMask)
	{
		_bitMask = bitMask;
	}

	@Override
	public String toString()
	{
		return _blockID + " " + _bitMask;
	}

}
