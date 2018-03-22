import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class Main
{
	public static LinkedList<Integer> _passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));;

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		new GameFrame();

	}

	public void test()
	{
		Block.setSize(60);
		String _mapFile = "MapFiles//world_20180221203331.xml";
		String _backgroundFile = "MapFiles//background_20180220162654.xml";
		String _effectsFile = "MapFiles//effects_20180103202456.xml";
		mapHeight = Map.getElementCountByName(_mapFile, "Line");
		mapWidth = Map.getElementCountByName(_mapFile, "Area") / mapHeight;

		Map map = new Map(mapHeight, mapWidth, _mapFile, _effectsFile, _backgroundFile);
		computeAllPaths(map.getHmap());
	}

	private static HashMap<StartAndEndMe, LinkedList<Point2D.Double>> _allPaths = new HashMap<StartAndEndMe, LinkedList<Point2D.Double>>();
	private static Rectangle searchRect = new Rectangle(0, 0, 0, 0);
	private static int mapWidth, mapHeight;

	public static void getShortcut(StartAndEndMe SAEM)
	{
		for (LinkedList<Point2D.Double> p : _allPaths.values())
		{
			int si = Logic.findIndex(p, SAEM.getStart());
			System.out.println(si);
			if (si != -1)
			{
				System.out.println("found start");
				int ei = Logic.findIndex(p, SAEM.getEnd());
				if (ei != -1)
				{
					System.out.println("found end");
					return;
				}
			}
		}
	}

	public static void computeAllPaths(Block[][] map)
	{
		for (int startIndex = 0; startIndex < mapWidth * mapHeight; startIndex++)
		{
			int startY = startIndex / mapWidth;
			int startX = startIndex % mapWidth;
			if (map[startY][startX] == null || _passables.contains(map[startY][startX].getBitMask().getBlockID()))
			{
				for (int endIndex = mapWidth * mapHeight - 1; endIndex >= startIndex; endIndex--)
				{
					int endY = endIndex / mapWidth;
					int endX = endIndex % mapWidth;
					if (map[endY][endX] == null || _passables.contains(map[endY][endX].getBitMask().getBlockID()))
					{
						StartAndEndMe SAEM = new StartAndEndMe(startX * Block.getSize()	+ Block.getSize() / 2,
																startY * Block.getSize() + Block.getSize() / 2,
																endX * Block.getSize() + Block.getSize() / 2,
																endY * Block.getSize() + Block.getSize() / 2);
						System.out.println(SAEM);
						getShortcut(SAEM);
						_allPaths.put(	SAEM,
										Logic.startDjikstra(searchRect, SAEM.getStart(), SAEM.getEnd(), map[0].length, map.length, 100000));
						System.out.println(_allPaths.get(SAEM));
					}
				}
			}
		}
	}
}