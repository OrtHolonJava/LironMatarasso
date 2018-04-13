import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;

public class Logic
{
	private Map _map;
	private Player _player;
	private LinkedList<AICharacter> _aiCharacters, _basicCharacters;
	private LinkedList<Follower> _followers;
	private Camera _cam;
	private static LinkedList<Integer> _passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));;
	private BufferedImage[] _playerFrames, _simpleFishFrames, _followerFrames;

	private final int PLAYER_WIDTH = 8 * BlockType.getSize() / 10, PLAYER_HEIGHT = 19 * BlockType.getSize() / 10,
			SIMPLE_FISH_WIDTH = PLAYER_WIDTH / 4, SIMPLE_FISH_HEIGHT = PLAYER_HEIGHT / 4, FOLLOWER_WIDTH = 7 * PLAYER_WIDTH / 10,
			FOLLOWER_HEIGHT = PLAYER_HEIGHT;

	private int _increaseBorder = 5;
	private GameFrame _frame;

	public Logic(Map map, GameFrame frame)
	{
		_frame = frame;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_map = map;
		_playerFrames = setFrames("images//SharkFrames//", PLAYER_WIDTH, PLAYER_HEIGHT);
		_simpleFishFrames = setFrames("images//SharkFrames//", SIMPLE_FISH_WIDTH, SIMPLE_FISH_HEIGHT);
		_followerFrames = setFrames("images//SharkFrames//", FOLLOWER_WIDTH, FOLLOWER_HEIGHT);
		_player = new Player(	indexToMiddleBlock(3), indexToMiddleBlock(6), PLAYER_WIDTH, PLAYER_HEIGHT, 8.0 / 60 * Block.getSize(),
								_playerFrames);
		_cam = new Camera(	new Point(BlockType.getSize(), BlockType.getSize()), (int) screenSize.getWidth(), (int) screenSize.getHeight(),
							_map.getWidth(), _map.getHeight());
		_cam.updateCamPoint(_player);
		_aiCharacters = new LinkedList<AICharacter>();
		_basicCharacters = new LinkedList<AICharacter>();
		_followers = new LinkedList<Follower>();
		addAICharacters(1);
		addFollowers(0);
	}

	public BufferedImage[] setFrames(String path, int width, int height)
	{
		BufferedImage[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new BufferedImage[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				// System.out.println(path + child.getName());
				arr[counter] = Img.toBufferedImage(new ImageIcon(this	.getClass().getClassLoader()
																		.getResource(path + child.getName())).getImage());
				arr[counter++] = Img.resize(arr[counter - 1], width, height);
			}
		}
		return arr;
	}

	public void doLogic()
	{
		checkEaten();
		checkHurt();
		movementLogic();
		if (_player.getHealth() <= 0)
		{
			_frame.nibbaIsDead();
		}
		_player.updateStats();
		for (AICharacter c : _aiCharacters)
		{
			if (c instanceof Follower)
			{
				Follower f = (Follower) c;
				followCharacter(f, _player);
				if (f.getPath().size() <= 1)
				{
					f.basicAIMovement(_player);
				}
				else
				{
					f.followPath();
				}
			}
			else
			{
				c.basicAIMovement(_player);
			}
			if (collisionHandle(c))
				c.setNewTarget();
		}

	}

	public void paintAICharacters(Graphics2D g, boolean drawDebug)
	{
		for (int i = 0; i < _aiCharacters.size(); i++)
		{
			_aiCharacters.get(i).draw(g, drawDebug);
		}
	}

	public void checkEaten()
	{
		Iterator<AICharacter> iterator = _basicCharacters.iterator();
		while (iterator.hasNext())
		{
			AICharacter c = iterator.next();
			Area temp = (Area) _player.getMouthHitbox().clone();
			temp.intersect(c.getHitbox());
			if (!temp.isEmpty())
			{
				iterator.remove();
				_aiCharacters.remove(c);
				_player.eaten(1);
			}
		}
	}

	public void checkHurt()
	{
		Iterator<Follower> iterator = _followers.iterator();
		while (iterator.hasNext())
		{
			Follower f = iterator.next();
			Area temp = (Area) f.getMouthHitbox().clone();
			temp.intersect(_player.getHitbox());
			if (!temp.isEmpty())
			{
				_player.hurt(1);
			}
		}
	}

	public void addAICharacters(int count)
	{
		for (int i = 0; i < count; i++)
		{
			AICharacter b = new AICharacter(16	* Block.getSize(), 7 * Block.getSize(), SIMPLE_FISH_WIDTH, SIMPLE_FISH_HEIGHT,
											2.0 / 60 * Block.getSize(), _simpleFishFrames);
			_aiCharacters.add(b);
			_basicCharacters.add(b);

		}
	}

	private void addFollowers(int count)
	{
		for (int i = 0; i < count; i++)
		{
			Follower f = new Follower(	indexToMiddleBlock(48), indexToMiddleBlock(28), FOLLOWER_WIDTH, FOLLOWER_HEIGHT,
										6.0 / 60 * Block.getSize(), _followerFrames);
			_aiCharacters.add(f);
			_followers.add(f);
		}
	}

	public void followCharacter(Follower f, Character c)
	{
		Point2D.Double startLoc = new Point2D.Double(	indexToMiddleBlock((int) (f.getLoc().x / Block.getSize())),
														indexToMiddleBlock((int) (f.getLoc().y / Block.getSize())));
		Point2D.Double endLoc = new Point2D.Double(	indexToMiddleBlock((int) (c.getLoc().x / Block.getSize())),
													indexToMiddleBlock((int) (c.getLoc().y / Block.getSize())));

		int i = findIndex(f.getPath(), endLoc);
		if (i != -1)
		{
			f.getPath().subList(i, f.getPath().size()).clear();
			// System.out.println("lol cya");
		}
		else
		{
			LinkedList<Point2D.Double> add = startDjikstra(	f.getSearchRect(), startLoc, endLoc, _map.getWidth(), _map.getHeight(),
															_increaseBorder);
			add.removeFirst();
			f.getPath().clear();
			f.getPath().addAll(add);
		}
		// System.out.println("start: " + startLoc + " " + " end: " + endLoc);
		// System.out.println(f.getPath());
	}

	public boolean collisionHandle(Character c)
	{
		if (!checkCollision(c))
		{
			for (Rectangle r : c.getRects())
			{
				while (c.getHitbox().intersects(r))
				{
					c.move(Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (c.getX()), (r.y + r.getHeight() / 2) - (c.getY()))), 0.5);
				}
			}
			checkCollision(c);
			return true;
		}
		return false;
	}

	public void movementLogic()
	{
		_cam.getFinalMousePoint().setLocation(_cam.getCamPoint().x + _cam.getMousePoint().x, _cam.getCamPoint().y + _cam.getMousePoint().y);
		if (_cam.getFinalMousePoint().distance(_player.getLoc()) > 2)
		{
			_player.setAngle(Math.toDegrees(Math.atan2(_cam.getFinalMousePoint().y	- _player.getY(),
														_cam.getFinalMousePoint().x - _player.getX())));
		}

		_player.setDisToSpeedRatio((_cam.getFinalMousePoint().distance(_player.getX(), _player.getY()) / (5 * BlockType.getSize())));
		_player.updateFinalSpeed();
		_player.move(_player.getAngle(), _player.getFinalSpeed());
		collisionHandle(_player);
		_cam.updateCamPoint(_player);
	}

	public boolean checkCollision(Character c)
	{
		c.applySeaweedSlowdown(false);
		boolean flag = true;
		c.getColiList().clear();
		c.getRects().clear();
		for (int y = (int) (c.getY() / BlockType.getSize()) - 1; y <= (c.getY() / BlockType.getSize()) + 1; y++)
		{
			for (int x = (int) (c.getX() / BlockType.getSize()) - 1; x <= (c.getX() / BlockType.getSize()) + 1; x++)
			{
				if (y >= 0 && x >= 0 && y < _map.getHeight() && x < _map.getWidth() && _map.getHmap()[y][x] != null)
				{
					Rectangle rect = _map.getHmap()[y][x].getRectangle();
					if (c.getHitbox().intersects(rect))
					{
						if (!_passables.contains(_map.getHmap()[y][x].getBitMask().getBlockID()))
						{
							c.getRects().add(rect);
							flag = false;
							for (Point2D p : c.getPolyList())
							{
								if (rect.contains(p))
								{
									// System.out.println(p + " point at " +
									// rect.toString());
									c.getColiList().add(new Point2D.Double(p.getX(), p.getY()));
								}
							}
						}
						if (c.getSpeedSeaweedSlowdown() == 1)
							c.applySeaweedSlowdown(_map.getHmap()[y][x].getBitMask().getBlockID() == 3);
					}
				}
			}
		}
		return flag;
	}

	public void drawDebug(Graphics g)
	{
		g.setColor(Color.red);
		g.drawRect((int) _cam.getFinalMousePoint().x, (int) _cam.getFinalMousePoint().y, 100, 100);
		g.drawString(String.valueOf(_player.getAngle()), (int) _cam.getFinalMousePoint().x, (int) _cam.getFinalMousePoint().y);
		g.setColor(Color.green);
		g.drawRect(_cam.getCamPoint().x, _cam.getCamPoint().y, 100, 100);
		g.setColor(Color.cyan);
		g.drawRect((int) _player.getX(), (int) _player.getY(), 100, 100);
	}

	// public void drawSearchOrder(Graphics2D g)
	// {
	// for (int i = 0; i < searchOrder.size(); i++)
	// {
	// g.drawRect(searchOrder.get(i).getX() * Block.getSize(),
	// searchOrder.get(i).getY() * Block.getSize(), 5 * Block.getSize() / 6,
	// 5 * Block.getSize() / 6);
	// }
	// }

	public Map getMap()
	{
		return _map;
	}

	public void setMap(Map map)
	{
		_map = map;
	}

	public Player getPlayer()
	{
		return _player;
	}

	public void setPlayer(Player player)
	{
		_player = player;
	}

	public Camera getCam()
	{
		return _cam;
	}

	public void setCam(Camera cam)
	{
		_cam = cam;
	}

	public LinkedList<Integer> getPassables()
	{
		return _passables;
	}

	public void setPassables(LinkedList<Integer> passables)
	{
		_passables = passables;
	}

	public LinkedList<AICharacter> getAiCharacters()
	{
		return _aiCharacters;
	}

	public void setAiCharacters(LinkedList<AICharacter> aiCharacters)
	{
		_aiCharacters = aiCharacters;
	}

	public LinkedList<Point2D.Double> startDjikstra(Rectangle searchRect, Point2D.Double startLoc, Point2D.Double endLoc, int mapWidth,
													int mapHeight, int increaseBorder)
	{
		LinkedList<DjikstraVertex> Q = new LinkedList<DjikstraVertex>();
		LinkedList<Point2D.Double> path = new LinkedList<Point2D.Double>();
		searchRect.y = (int) ((startLoc.y < endLoc.y) ? startLoc.y / Block.getSize() : endLoc.y / Block.getSize());
		searchRect.x = (int) ((startLoc.x < endLoc.x) ? startLoc.x / Block.getSize() : endLoc.x / Block.getSize());
		searchRect.y -= increaseBorder;
		searchRect.x -= increaseBorder;
		searchRect.y = (searchRect.y < 0) ? 0 : searchRect.y;
		searchRect.x = (searchRect.x < 0) ? 0 : searchRect.x;
		searchRect.width = (int) ((startLoc.x > endLoc.x) ? startLoc.x - searchRect.x : endLoc.x - searchRect.x) / Block.getSize()	+ 1
							+ increaseBorder;

		searchRect.height = (int) ((startLoc.y > endLoc.y) ? startLoc.y - searchRect.y : endLoc.y - searchRect.y) / Block.getSize()	+ 1
							+ increaseBorder;
		searchRect.width = (searchRect.width + searchRect.x > mapWidth) ? mapWidth - searchRect.x : searchRect.width;
		searchRect.height = (searchRect.height + searchRect.y > mapHeight) ? mapHeight - searchRect.y : searchRect.height;
		DjikstraVertex[][] dMap = new DjikstraVertex[searchRect.height][searchRect.width];
		// + " h:" + height);

		for (int y = 0; y < searchRect.height; y++)
		{
			for (int x = 0; x < searchRect.width; x++)
			{
				// System.out.println((x + startX) + " " + (y + startY));
				dMap[y][x] = new DjikstraVertex(x	+ searchRect.x, y + searchRect.y,
												Map.getdMap()[y + searchRect.y][x + searchRect.x].isClear(),
												(int) endLoc.x / Block.getSize(), (int) endLoc.y / Block.getSize());
				Q.add(dMap[y][x]);
			}
		}

		dMap[(int) startLoc.y / Block.getSize() - searchRect.y][(int) startLoc.x / Block.getSize() - searchRect.x].setDistance(0);
		int count = 0, newStartCount = 0;
		// searchOrder.clear();
		while (!Q.isEmpty())
		{
			DjikstraVertex u = Collections.min(Q);
			searchOrder.add((DjikstraVertex) u.clone());
			if (u.getPrev().x == -1 && u.getPrev().y == -1)
			{
				newStartCount++;
			}
			if (newStartCount == 2
				|| (u.getLoc().x == (int) endLoc.x / Block.getSize() && u.getLoc().y == (int) endLoc.y / Block.getSize()))
			{
				break;
			}
			Q.remove(u);
			changeNeighbours(dMap, u, searchRect.x, searchRect.y);
			count++;
		}
		// System.out.println(count);
		DjikstraVertex next = dMap[(int) endLoc.y / Block.getSize() - searchRect.y][(int) endLoc.x / Block.getSize() - searchRect.x];
		while (next.getPrev().x != -1 && next.getPrev().y != -1)
		{
			path.addFirst(new Point2D.Double(indexToMiddleBlock(next.getLoc().x), indexToMiddleBlock(next.getLoc().y)));
			next = dMap[next.getPrev().y - searchRect.y][next.getPrev().x - searchRect.x];
		}
		path.addFirst((Point2D.Double) startLoc.clone());
		path.addLast((Point2D.Double) endLoc.clone());
		return path;
	}

	public static LinkedList<DjikstraVertex> searchOrder = new LinkedList<DjikstraVertex>();

	public static <T> int findIndex(LinkedList<T> l, T e)
	{
		for (int i = 0; i < l.size(); i++)
		{
			if (l.get(i).equals(e))
				return i;
		}
		return -1;
	}

	public static double indexToMiddleBlock(int i)
	{
		return i * Block.getSize() + Block.getSize() / 2;
	}

	public static void changeNeighbours(DjikstraVertex[][] dMap, DjikstraVertex u, int startX, int startY)
	{
		changeNeighbour(dMap, u.getX() - 1, u.getY() - 1, u, Math.sqrt(2), startX, startY);
		changeNeighbour(dMap, u.getX() - 1, u.getY() + 1, u, Math.sqrt(2), startX, startY);
		changeNeighbour(dMap, u.getX() + 1, u.getY() - 1, u, Math.sqrt(2), startX, startY);
		changeNeighbour(dMap, u.getX() + 1, u.getY() + 1, u, Math.sqrt(2), startX, startY);
		changeNeighbour(dMap, u.getX() - 1, u.getY(), u, 1, startX, startY);
		changeNeighbour(dMap, u.getX(), u.getY() - 1, u, 1, startX, startY);
		changeNeighbour(dMap, u.getX(), u.getY() + 1, u, 1, startX, startY);
		changeNeighbour(dMap, u.getX() + 1, u.getY(), u, 1, startX, startY);
	}

	public static void changeNeighbour(DjikstraVertex[][] dMap, int x, int y, DjikstraVertex u, double add, int startX, int startY)
	{
		x -= startX;
		y -= startY;
		if (inBounds(x, y, dMap[0].length, dMap.length) && dMap[y][x] != null && dMap[y][x].isClear())
		{
			double dis = u.getDistance()	+ Math.hypot((u.getLoc().x - dMap[y][x].getLoc().x), (u.getLoc().y - dMap[y][x].getLoc().y))
							+ dMap[y][x].getToEnd();
			// System.out.println(Math.hypot((u.getLoc().x - x), (u.getLoc().y -
			// y)) + " " + dMap[y][x].getToEnd());
			// double dis = u.getDistance() + add;
			if (dis < dMap[y][x].getDistance())
			{
				dMap[y][x].setDistance(dis);
				dMap[y][x].setPrev(u.getLoc());
			}
		}
	}

	public static boolean inBounds(int x, int y, int width, int height)
	{
		return x >= 0 && y >= 0 && y < height && x < width;
	}

	// public static void writeObjectToFile(Object o, String path)
	// {
	// try
	// {
	// // create a new file with an ObjectOutputStream
	// FileOutputStream out = new FileOutputStream(path);
	// ObjectOutputStream oout = new ObjectOutputStream(out);
	//
	// // write something in the file
	// oout.writeObject(o);
	// // close the stream
	// oout.close();
	// }
	// catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	//
	// public static Object readObjectFromFile(String path)
	// {
	// // create an ObjectInputStream for the file we created before
	// ObjectInputStream ois;
	// try
	// {
	// ois = new ObjectInputStream(new FileInputStream(path));
	// // read and print what we wrote before
	// return ois.readObject();
	// }
	// catch (Exception e)
	// {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// return null;
	// }
}
