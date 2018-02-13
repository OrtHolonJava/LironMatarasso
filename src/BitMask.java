import java.util.HashMap;

public class BitMask
{
	private int _blockID;
	private int _bitMask;

	public BitMask(int blockID, int bitMask)
	{
		_blockID = blockID;
		_bitMask = bitMask;
	}

	public static final HashMap<Integer, Integer> kek = setKek();

	public static HashMap<Integer, Integer> setKek()
	{
		HashMap<Integer, Integer> kek = new HashMap<Integer, Integer>();
		kek.put(2, 1);
		kek.put(8, 2);
		kek.put(10, 3);
		kek.put(11, 4);
		kek.put(16, 5);
		kek.put(18, 6);
		kek.put(22, 7);
		kek.put(24, 8);
		kek.put(26, 9);
		kek.put(27, 10);
		kek.put(30, 11);
		kek.put(31, 12);
		kek.put(64, 13);
		kek.put(66, 14);
		kek.put(72, 15);
		kek.put(74, 16);
		kek.put(75, 17);
		kek.put(80, 18);
		kek.put(82, 19);
		kek.put(86, 20);
		kek.put(88, 21);
		kek.put(90, 22);
		kek.put(91, 23);
		kek.put(94, 24);
		kek.put(95, 25);
		kek.put(104, 26);
		kek.put(106, 27);
		kek.put(107, 28);
		kek.put(120, 29);
		kek.put(122, 30);
		kek.put(123, 31);
		kek.put(126, 32);
		kek.put(127, 33);
		kek.put(208, 34);
		kek.put(210, 35);
		kek.put(214, 36);
		kek.put(216, 37);
		kek.put(218, 38);
		kek.put(219, 39);
		kek.put(222, 40);
		kek.put(223, 41);
		kek.put(248, 42);
		kek.put(250, 43);
		kek.put(251, 44);
		kek.put(254, 45);
		kek.put(255, 46);
		kek.put(0, 47);
		return kek;
	}

	public static int placeMeeting(HashMap<Integer, BitMask> map, int size, int col, int row, int curCol, int curRow, int tileVal)
	{
		return (map.containsKey(curRow * size + curCol) && map.get(curRow * size + curCol).getBlockID() == tileVal) ? 1 : 0;
	}

	public static int computeTile(HashMap<Integer, BitMask> map, int size, int y, int x, int tileVal)
	{
		int sum = 0;
		int north_tile = placeMeeting(map, size, x, y, x, y - 1, tileVal);
		int south_tile = placeMeeting(map, size, x, y, x, y + 1, tileVal);
		int west_tile = placeMeeting(map, size, x, y, x - 1, y, tileVal);
		int east_tile = placeMeeting(map, size, x, y, x + 1, y, tileVal);
		int north_west_tile = (west_tile == 1 && north_tile == 1 && placeMeeting(map, size, x, y, x - 1, y - 1, tileVal) == 1) ? 1 : 0;
		int north_east_tile = (north_tile == 1 && east_tile == 1 && placeMeeting(map, size, x, y, x + 1, y - 1, tileVal) == 1) ? 1 : 0;
		int south_west_tile = (south_tile == 1 && west_tile == 1 && placeMeeting(map, size, x, y, x - 1, y + 1, tileVal) == 1) ? 1 : 0;
		int south_east_tile = (south_tile == 1 && east_tile == 1 && placeMeeting(map, size, x, y, x + 1, y + 1, tileVal) == 1) ? 1 : 0;

		// 8 bit Bitmasking calculation using Directional check booleans values
		sum = north_west_tile + 2 * north_tile + 4 * north_east_tile + 8 * west_tile + 16 * east_tile + 32 * south_west_tile + 64 * south_tile + 128 * south_east_tile;

		if (kek.containsKey(sum))
			return kek.get(sum);
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

}
