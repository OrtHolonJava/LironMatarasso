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
	private LinkedList<AICharacter> _aiCharacters;
	private Camera _cam;
	public static LinkedList<Integer> _passables;
	private BufferedImage[] _playerFrames, _simpleFishFrames;

	private final int PLAYER_WIDTH = 8 * BlockType.getSize() / 10, PLAYER_HEIGHT = 19 * BlockType.getSize() / 10,
			SIMPLE_FISH_WIDTH = PLAYER_WIDTH / 4, SIMPLE_FISH_HEIGHT = PLAYER_HEIGHT / 4;

	public Logic(Map map)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_playerFrames = setFrames("images//SharkFrames//", PLAYER_WIDTH, PLAYER_HEIGHT);
		_simpleFishFrames = setFrames("images//SharkFrames//", SIMPLE_FISH_WIDTH, SIMPLE_FISH_HEIGHT);
		_map = map;
		_player = new Player(33 * Block.getSize(), 7 * Block.getSize(), PLAYER_WIDTH, PLAYER_HEIGHT, Block.getSize() / 8, _playerFrames);
		_cam = new Camera(	new Point(BlockType.getSize(), BlockType.getSize()), (int) screenSize.getWidth(), (int) screenSize.getHeight(),
							_map.getWidth(), _map.getHeight());
		_cam.updateCamPoint(_player);
		_passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));
		_aiCharacters = new LinkedList<AICharacter>();
		addAICharacters(10);
		addFollowers(1);
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
		movementLogic();
		_player.updateStats();
		for (AICharacter c : _aiCharacters)
		{
			if (c instanceof Follower)
			{
				Follower f = (Follower) c;
				if (f.getLoc().distance(_player.getLoc()) < Block.getSize() * 10)
				{
					followPlayer(f);
				}
				if (f.getPath().isEmpty())
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
		Iterator<AICharacter> iterator = _aiCharacters.iterator();
		while (iterator.hasNext())
		{
			AICharacter c = iterator.next();
			if (_cam.inScreen(c.getHitbox()))
				c.Paint(g, drawDebug);
		}
	}

	public void checkEaten()
	{
		Iterator<AICharacter> iterator = _aiCharacters.iterator();
		while (iterator.hasNext())
		{
			AICharacter c = iterator.next();
			Area temp = (Area) _player.getMouthHitbox().clone();
			temp.intersect(c.getHitbox());
			if (!temp.isEmpty())
			{
				iterator.remove();
				_player.eaten(1);
			}
		}
	}

	public void addAICharacters(int count)
	{
		for (int i = 0; i < count; i++)
		{
			_aiCharacters.add(new AICharacter(16	* Block.getSize(), 7 * Block.getSize(), SIMPLE_FISH_WIDTH, SIMPLE_FISH_HEIGHT, 2,
												_simpleFishFrames));
		}
	}

	private void addFollowers(int count)
	{
		for (int i = 0; i < count; i++)
		{
			_aiCharacters.add(new Follower(16 * Block.getSize()	+ i * Block.getSize(), 5 * Block.getSize() + i * Block.getSize(),
											SIMPLE_FISH_WIDTH, SIMPLE_FISH_HEIGHT, 2, _simpleFishFrames));
		}
	}

	public void followPlayer(Follower c)
	{
		startDjikstra(c, _player.getLoc());
		// startDjikstraFromEnd(c, _player.getLoc());
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
		for (int curRow = (int) (c.getY() / BlockType.getSize()) - 1; curRow <= (c.getY() / BlockType.getSize()) + 1; curRow++)
		{
			for (int curCol = (int) (c.getX() / BlockType.getSize()) - 1; curCol <= (c.getX() / BlockType.getSize()) + 1; curCol++)
			{
				if (curRow >= 0	&& curCol >= 0 && curRow < _map.getHeight() && curCol < _map.getWidth()
					&& _map.getHmap()[curRow][curCol] != null)
				{
					Rectangle rect = _map.getHmap()[curRow][curCol].getRectangle();
					if (c.getHitbox().intersects(rect))
					{
						if (!_passables.contains(_map.getHmap()[curRow][curCol].getBitMask().getBlockID()))
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
							c.applySeaweedSlowdown(_map.getHmap()[curRow][curCol].getBitMask().getBlockID() == 3);
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

	public void startDjikstra(Follower c, Point2D.Double endLoc)
	{
		LinkedList<DjikstraVertex> Q = new LinkedList<DjikstraVertex>();
		DjikstraVertex[][] dMap = new DjikstraVertex[Map.getdMap().length][Map.getdMap()[0].length];
		for (int y = 0; y < Map.getdMap().length; y++)
		{
			for (int x = 0; x < Map.getdMap()[0].length; x++)
			{
				dMap[y][x] = new DjikstraVertex(x, y, Map.getdMap()[y][x].isClear());
				Q.add(dMap[y][x]);
			}
		}
		dMap[(int) c.getLoc().y / Block.getSize()][(int) c.getLoc().x / Block.getSize()].setDistance(0);

		while (!Q.isEmpty())
		{
			DjikstraVertex u = Collections.min(Q);
			if (u.getLoc().y == (int) endLoc.y / Block.getSize() && u.getLoc().x == (int) endLoc.x / Block.getSize())
				break;
			Q.remove(u);
			changeNeighbours(dMap, u);
		}
		c.getPath().clear();
		DjikstraVertex next = dMap[(int) endLoc.y / Block.getSize()][(int) endLoc.x / Block.getSize()];
		while (next.getPrev().x != -1 && next.getPrev().y != -1)
		{
			c.getPath().addFirst(new Point2D.Double(next.getLoc().x * Block.getSize()	+ Block.getSize() / 2,
													next.getLoc().y * Block.getSize() + Block.getSize() / 2));
			next = dMap[next.getPrev().y][next.getPrev().x];
		}
		c.getPath().addLast((Point2D.Double) endLoc.clone());
	}

	public void startDjikstraFromEnd(Follower c, Point2D.Double endLoc)
	{
		LinkedList<DjikstraVertex> Q = new LinkedList<DjikstraVertex>();
		LinkedList<Point2D.Double> addList = new LinkedList<Point2D.Double>();
		DjikstraVertex[][] dMap = new DjikstraVertex[Map.getdMap().length][Map.getdMap()[0].length];
		for (int y = 0; y < Map.getdMap().length; y++)
		{
			for (int x = 0; x < Map.getdMap()[0].length; x++)
			{
				dMap[y][x] = new DjikstraVertex(x, y, Map.getdMap()[y][x].isClear());
				Q.add(dMap[y][x]);
			}
		}
		Point2D.Double startLoc = (c.getPath().isEmpty()) ? c.getLoc() : c.getPath().getLast();
		dMap[(int) startLoc.y / Block.getSize()][(int) startLoc.x / Block.getSize()].setDistance(0);

		while (!Q.isEmpty())
		{
			DjikstraVertex u = Collections.min(Q);
			if (u.getLoc().y == (int) endLoc.y / Block.getSize() && u.getLoc().x == (int) endLoc.x / Block.getSize())
				break;
			Q.remove(u);
			changeNeighbours(dMap, u);
		}
		// c.getPath().clear();
		DjikstraVertex next = dMap[(int) endLoc.y / Block.getSize()][(int) endLoc.x / Block.getSize()];
		while (next.getPrev().x != -1 && next.getPrev().y != -1)
		{
			Point2D.Double temp = new Point2D.Double(next.getLoc().x * Block.getSize()	+ Block.getSize() / 2,
														next.getLoc().y * Block.getSize() + Block.getSize() / 2);
			if (c.getPath().contains(temp))
			{
				c.getPath().subList(findIndex(c.getPath(), temp), c.getPath().size()).clear();
			}
			else
			{
				addList.addFirst(temp);
			}
			next = dMap[next.getPrev().y][next.getPrev().x];
		}
		c.getPath().addAll(c.getPath().size(), addList);
	}

	public <T> int findIndex(LinkedList<T> l, T e)
	{
		for (int i = 0; i < l.size(); i++)
		{
			if (l.get(i).equals(e))
				return i;
		}
		return -1;
	}

	public void changeNeighbours(DjikstraVertex[][] dMap, DjikstraVertex u)
	{
		changeNeighbour(dMap, u.getX() - 1, u.getY() - 1, u, 1.5);
		changeNeighbour(dMap, u.getX() - 1, u.getY(), u, 1);
		changeNeighbour(dMap, u.getX() - 1, u.getY() + 1, u, 1.5);
		changeNeighbour(dMap, u.getX(), u.getY() - 1, u, 1);
		changeNeighbour(dMap, u.getX(), u.getY() + 1, u, 1);
		changeNeighbour(dMap, u.getX() + 1, u.getY() - 1, u, 1.5);
		changeNeighbour(dMap, u.getX() + 1, u.getY(), u, 1);
		changeNeighbour(dMap, u.getX() + 1, u.getY() + 1, u, 1.5);

	}

	public void changeNeighbour(DjikstraVertex[][] dMap, int x, int y, DjikstraVertex u, double add)
	{
		if (inBounds(x, y, dMap[0].length, dMap.length) && dMap[y][x].isClear())
		{
			double dis = u.getDistance() + add;
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
}
