import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Logic class that stores and computes all the data in the game
 * 
 * @author liron
 *
 */
public class Logic
{
	private Map _map;
	private Player _player;

	private LinkedList<AICharacter> _aiCharacters, _basicCharacters, _tempAdd;
	private LinkedList<Follower> _followers;
	private Camera _cam;

	private GameFrame _frame;

	private int _increaseBorder = 5;

	private LocalDateTime _startTime, _endTime;

	/**
	 * Init a new Logic object with the following parameters:
	 * 
	 * @param map
	 * @param frame
	 */
	public Logic(Map map, GameFrame frame)
	{
		Random r = new Random();
		_frame = frame;
		_map = map;
		_player = new Player(indexToMiddleBlock(4), indexToMiddleBlock(11));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_cam = new Camera(	new Point(BlockGraphics.getSize(), BlockGraphics.getSize()), (int) screenSize.getWidth(),
							(int) screenSize.getHeight(), _map.getWidth(), _map.getHeight());
		_cam.updateCamPoint(_player);
		_aiCharacters = new LinkedList<AICharacter>();
		_basicCharacters = new LinkedList<AICharacter>();
		_tempAdd = new LinkedList<AICharacter>();
		_followers = new LinkedList<Follower>();
		// addAIToCord(20, 7, 20, 5, r.nextInt(360));
		addFollowers(1, 3, 6);
		for (int i = 0; i < 20; i++)
		{
			spawnShoal();
		}
		addTempFish();
		_startTime = LocalDateTime.now();
	}

	/**
	 * adds fish from the temp list to the real list to prevent changing the
	 * list while iterating over it
	 */
	public void addTempFish()
	{
		_aiCharacters.addAll(_tempAdd);
		_basicCharacters.addAll(_tempAdd);
		_tempAdd.clear();
	}

	/**
	 * computes everything in a "tick" of the game: moves characters, checks
	 * collisions, updates stats, adds new fishes, check if the player died and
	 * more
	 */
	public void doLogic()
	{
		addTempFish();
		checkEaten();
		checkHurt();
		movementLogic();
		if (_player.getHealth() <= 0)
		{
			_endTime = LocalDateTime.now();
			Duration duration = Duration.between(_startTime, _endTime);
			_frame.playerIsDead(_player.getAmountEaten(), String.format("%02d:%02d:%02d", duration.toHoursPart(), duration.toMinutesPart(),
																		duration.toSecondsPart()));
			while (true)
			{
				Thread.yield();
			}
		}
		_player.updateStats();
		for (int i = 0; i < _aiCharacters.size(); i++)
		{
			AICharacter c = _aiCharacters.get(i);
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
			{
				c.setNewTarget();
			}
		}
	}

	/**
	 * runs over the computer characters and draws them
	 * 
	 * @param g
	 * @param drawDebug
	 */
	public void drawAICharacters(Graphics2D g, boolean drawDebug)
	{
		for (int i = 0; i < _aiCharacters.size(); i++)
		{
			_aiCharacters.get(i).draw(g, drawDebug);
		}
	}

	/**
	 * checks if the player intersects small fishes, and eats them if so
	 */
	public void checkEaten()
	{
		Iterator<AICharacter> iterator = _basicCharacters.iterator();
		while (iterator.hasNext())
		{
			AICharacter c = iterator.next();
			Area temp = (Area) _player._mouthHitbox.clone();
			temp.intersect(c._hitbox);
			if (!temp.isEmpty())
			{
				iterator.remove();
				_aiCharacters.remove(c);
				_player.eaten(1);
			}
		}
	}

	/**
	 * checks if an enemy fish intersects the player, and hurts the player if so
	 */
	public void checkHurt()
	{
		for (int i = 0; i < _followers.size(); i++)
		{
			Follower f = _followers.get(i);
			Area temp = (Area) f._mouthHitbox.clone();
			temp.intersect(_player._hitbox);
			if (!temp.isEmpty())
			{
				_player.hurt(1);
			}
		}
	}

	/**
	 * spawns a shoal of fishes
	 */
	public void spawnShoal()
	{
		Random r = new Random();
		int type = 1 + r.nextInt(CharacterType.SMALL_FISH_COUNT);
		int size = 5 + r.nextInt(15);
		Point spawnPoint = getValidPoint();
		double angle = r.nextInt(360);
		for (int i = 0; i < size; i++)
		{
			var c = new AICharacter(spawnPoint.x * Block.getSize(), spawnPoint.y * Block.getSize(), type, angle);
			_tempAdd.add(c);
		}
	}

	/**
	 * 
	 * @return a coordinate on the map that is empty and not inside the screen
	 */
	private Point getValidPoint()
	{
		Random r = new Random();
		Point p = _map.getClearBlock().get(r.nextInt(_map.getClearBlock().size()));
		while (_cam.getScreenRectangle().contains(p.x * Block.getSize(), p.y * Block.getSize()))
		{
			p = _map.getClearBlock().get(r.nextInt(_map.getClearBlock().size()));
		}
		return p;
	}

	/**
	 * adds basic fish to the given coordinate
	 * 
	 * @param x
	 * @param y
	 * @param count
	 * @param type
	 * @param angle
	 */
	public void addAIToCord(int x, int y, int count, int type, double angle)
	{
		for (int i = 0; i < count; i++)
		{
			AICharacter b = new AICharacter(indexToMiddleBlock(x), indexToMiddleBlock(y), type, angle);
			_aiCharacters.add(b);
			_basicCharacters.add(b);
		}
	}

	/**
	 * adds followers to the given coordinate
	 * 
	 * @param count
	 * @param x
	 * @param y
	 */
	private void addFollowers(int count, int x, int y)
	{
		for (int i = 0; i < count; i++)
		{
			Follower f = new Follower(indexToMiddleBlock(x), indexToMiddleBlock(y), 10);
			_aiCharacters.add(f);
			_followers.add(f);
		}
	}

	/**
	 * handles the following of the player by followers, checks if the player
	 * coordinates are already in the follower path to prevent recalculating the
	 * path, and calculating it otherwise
	 * 
	 * @param f
	 * @param c
	 */
	public void followCharacter(Follower f, Character c)
	{
		Point2D.Double startLoc = new Point2D.Double(	indexToMiddleBlock((int) (f._loc.x / Block.getSize())),
														indexToMiddleBlock((int) (f._loc.y / Block.getSize())));
		Point2D.Double endLoc = new Point2D.Double(	indexToMiddleBlock((int) (c._loc.x / Block.getSize())),
													indexToMiddleBlock((int) (c._loc.y / Block.getSize())));

		int i = findIndex(f.getPath(), endLoc);
		if (i != -1)
		{
			f.getPath().subList(i, f.getPath().size()).clear();
		}
		else
		{
			LinkedList<Point2D.Double> add = startDjikstra(f.getSearchRect(), startLoc, endLoc, _increaseBorder);
			add.removeFirst();
			if (add.size() > 1)
			{
				f.getPath().clear();
				f.getPath().addAll(add);
			}
		}
	}

	/**
	 * handles the collision of a character with a block
	 * 
	 * @param c
	 * @return
	 */
	public boolean collisionHandle(Character c)
	{
		if (!checkCollision(c))
		{
			for (Rectangle r : c._rects)
			{
				while (c._hitbox.intersects(r))
				{
					c.move(Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (c.getX()), (r.y + r.getHeight() / 2) - (c.getY()))), 1);
				}
			}
			checkCollision(c);
			return true;
		}
		return false;
	}

	/**
	 * moves the player and the cam according to the mouse cursor location
	 */
	public void movementLogic()
	{
		_cam.getFinalMousePoint().setLocation(_cam.getCamPoint().x + _cam.getMousePoint().x, _cam.getCamPoint().y + _cam.getMousePoint().y);
		if (_cam.getFinalMousePoint().distance(_player._loc) > 2)
		{
			_player.setCorrectedAngle(Math.toDegrees(Math.atan2(_cam.getFinalMousePoint().y - _player.getY(),
																_cam.getFinalMousePoint().x - _player.getX())));
		}

		_player.setDisToSpeedRatio((_cam.getFinalMousePoint().distance(_player.getX(), _player.getY()) / (5 * BlockGraphics.getSize())));
		_player.updateFinalSpeed();
		_player.move(_player._angle, _player._finalSpeed);
		collisionHandle(_player);
		_cam.updateCamPoint(_player);
	}

	/**
	 * checks if a character collides with any block
	 * 
	 * @param c
	 * @return
	 */
	public boolean checkCollision(Character c)
	{
		c.applySeaweedSlowdown(false);
		boolean flag = true;
		c._coliList.clear();
		c._rects.clear();
		for (int y = (int) (c.getY() / BlockGraphics.getSize()) - 1; y <= (c.getY() / BlockGraphics.getSize()) + 1; y++)
		{
			for (int x = (int) (c.getX() / BlockGraphics.getSize()) - 1; x <= (c.getX() / BlockGraphics.getSize()) + 1; x++)
			{
				if (y >= 0 && x >= 0 && y < _map.getHeight() && x < _map.getWidth() && _map.getHmap()[y][x] != null)
				{
					Rectangle rect = _map.getHmap()[y][x].getRectangle();
					if (c._hitbox.intersects(rect))
					{
						if (!GamePanel.getPassables().contains(_map.getHmap()[y][x].getBlockRelativeInfo().getBlockID()))
						{
							c._rects.add(rect);
							flag = false;
							for (Point2D p : c._polyList)
							{
								if (rect.contains(p))
								{
									c._coliList.add(new Point2D.Double(p.getX(), p.getY()));
								}
							}
						}
						if (c._speedSeaweedSlowdown == 1)
						{
							c.applySeaweedSlowdown(_map.getHmap()[y][x].getBlockRelativeInfo().getBlockID() == 3);
						}
					}
				}
			}
		}
		return flag;
	}

	/**
	 * draws debug info
	 * 
	 * @param g
	 */
	public void drawDebug(Graphics g)
	{
		g.setColor(Color.red);
		g.drawRect((int) _cam.getFinalMousePoint().x, (int) _cam.getFinalMousePoint().y, 100, 100);
		g.drawString(String.valueOf(_player._angle), (int) _cam.getFinalMousePoint().x, (int) _cam.getFinalMousePoint().y);
		g.setColor(Color.green);
		g.drawRect(_cam.getCamPoint().x, _cam.getCamPoint().y, 100, 100);
		g.setColor(Color.cyan);
		g.drawRect((int) _player.getX(), (int) _player.getY(), 100, 100);
	}

	/**
	 * gets the following parameters:
	 * 
	 * @param searchRect
	 * @param startLoc
	 * @param endLoc
	 * @param increaseBorder
	 * @return a path between the start and end points that goes only inside the
	 *         calculated search rectangle of the character
	 */
	public LinkedList<Point2D.Double> startDjikstra(Rectangle searchRect, Point2D.Double startLoc, Point2D.Double endLoc,
													int increaseBorder)
	{
		LinkedList<DjikstraVertex> vertexQueue = new LinkedList<DjikstraVertex>();
		LinkedList<Point2D.Double> path = new LinkedList<Point2D.Double>();
		searchRect.x = (int) Math.min(startLoc.x, endLoc.x) / Block.getSize();
		searchRect.y = (int) Math.min(startLoc.y, endLoc.y) / Block.getSize();
		searchRect.x -= increaseBorder;
		searchRect.y -= increaseBorder;
		searchRect.x = Math.max(searchRect.x, 0);
		searchRect.y = Math.max(searchRect.y, 0);
		searchRect.width = (int) (Math.abs(endLoc.x - startLoc.x) / Block.getSize()) + 1 + 2 * increaseBorder;
		searchRect.height = (int) (Math.abs(endLoc.y - startLoc.y) / Block.getSize()) + 1 + 2 * increaseBorder;
		searchRect.width = (searchRect.width + searchRect.x > _map.getWidth()) ? _map.getWidth() - searchRect.x : searchRect.width;
		searchRect.height = (searchRect.height + searchRect.y > _map.getHeight()) ? _map.getHeight() - searchRect.y : searchRect.height;
		DjikstraVertex[][] dMap = new DjikstraVertex[searchRect.height][searchRect.width];

		for (int y = 0; y < searchRect.height; y++)
		{
			for (int x = 0; x < searchRect.width; x++)
			{
				dMap[y][x] = new DjikstraVertex(x + searchRect.x, y + searchRect.y,
												Map.getdMap()[y + searchRect.y][x + searchRect.x].isClear(),
												(int) endLoc.x / Block.getSize(), (int) endLoc.y / Block.getSize());
				vertexQueue.add(dMap[y][x]);
			}
		}

		dMap[(int) startLoc.y / Block.getSize() - searchRect.y][(int) startLoc.x / Block.getSize() - searchRect.x].setDistance(0);
		int newStartCount = 0;
		while (!vertexQueue.isEmpty())
		{
			DjikstraVertex u = Collections.min(vertexQueue);
			if (u.getPrev().x == -1 && u.getPrev().y == -1)
			{
				newStartCount++;
			}
			if (newStartCount == 2
					|| (u.getCurrent().x == (int) endLoc.x / Block.getSize() && u.getCurrent().y == (int) endLoc.y / Block.getSize()))
			{
				break;
			}
			vertexQueue.remove(u);
			changeNeighbors(dMap, u, searchRect.x, searchRect.y);
		}
		DjikstraVertex next = dMap[(int) endLoc.y / Block.getSize() - searchRect.y][(int) endLoc.x / Block.getSize() - searchRect.x];
		while (next.getPrev().x != -1 && next.getPrev().y != -1)
		{
			path.addFirst(new Point2D.Double(indexToMiddleBlock(next.getCurrent().x), indexToMiddleBlock(next.getCurrent().y)));
			next = dMap[next.getPrev().y - searchRect.y][next.getPrev().x - searchRect.x];
		}
		path.addFirst((Point2D.Double) startLoc.clone());
		path.addLast((Point2D.Double) endLoc.clone());
		return path;
	}

	/**
	 * 
	 * @param l
	 * @param e
	 * @return the index of element e in the list l, returns -1 if e isn't in l
	 */
	public static <T> int findIndex(LinkedList<T> l, T e)
	{
		for (int i = 0; i < l.size(); i++)
		{
			if (l.get(i).equals(e))
				return i;
		}
		return -1;
	}

	/**
	 * 
	 * @param i
	 * @return gets an x or y of a block and returns the x or y of the pixel in
	 *         the center of the block
	 */
	public static double indexToMiddleBlock(int i)
	{
		return i * Block.getSize() + Block.getSize() / 2;
	}

	/**
	 * changes the neighbors of a given vertex in the graph relative diagonal
	 * vertexes have a distance of sqrt(2) and non diagonal a distance of 1
	 * 
	 * @param dMap
	 * @param u
	 * @param startX
	 * @param startY
	 */
	public static void changeNeighbors(DjikstraVertex[][] dMap, DjikstraVertex u, int startX, int startY)
	{
		changeNeighbor(dMap, u.getX() - 1, u.getY() - 1, u, Math.sqrt(2), startX, startY);
		changeNeighbor(dMap, u.getX() - 1, u.getY() + 1, u, Math.sqrt(2), startX, startY);
		changeNeighbor(dMap, u.getX() + 1, u.getY() - 1, u, Math.sqrt(2), startX, startY);
		changeNeighbor(dMap, u.getX() + 1, u.getY() + 1, u, Math.sqrt(2), startX, startY);
		changeNeighbor(dMap, u.getX() - 1, u.getY(), u, 1, startX, startY);
		changeNeighbor(dMap, u.getX(), u.getY() - 1, u, 1, startX, startY);
		changeNeighbor(dMap, u.getX(), u.getY() + 1, u, 1, startX, startY);
		changeNeighbor(dMap, u.getX() + 1, u.getY(), u, 1, startX, startY);
	}

	/**
	 * changes the distance in the given vertex if the given distance is shorter
	 * than the current
	 * 
	 * @param dMap
	 * @param x
	 * @param y
	 * @param u
	 * @param add
	 * @param startX
	 * @param startY
	 */
	public static void changeNeighbor(DjikstraVertex[][] dMap, int x, int y, DjikstraVertex u, double add, int startX, int startY)
	{
		x -= startX;
		y -= startY;
		if (inBounds(x, y, dMap[0].length, dMap.length) && dMap[y][x] != null && dMap[y][x].isClear())
		{
			double dis = u.getDistance()
					+ Math.hypot((u.getCurrent().x - dMap[y][x].getCurrent().x), (u.getCurrent().y - dMap[y][x].getCurrent().y))
					+ dMap[y][x].getToEnd();
			if (dis < dMap[y][x].getDistance())
			{
				dMap[y][x].setDistance(dis);
				dMap[y][x].setPrev(u.getCurrent());
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return if the coordinates are inside a rectangle that starts at (0,0)
	 *         with the given width and height
	 */
	public static boolean inBounds(int x, int y, int width, int height)
	{
		return x >= 0 && y >= 0 && y < height && x < width;
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

	public LinkedList<AICharacter> getAiCharacters()
	{
		return _aiCharacters;
	}

	public void setAiCharacters(LinkedList<AICharacter> aiCharacters)
	{
		_aiCharacters = aiCharacters;
	}

	public LinkedList<AICharacter> getBasicCharacters()
	{
		return _basicCharacters;
	}

	public void setBasicCharacters(LinkedList<AICharacter> basicCharacters)
	{
		_basicCharacters = basicCharacters;
	}

	public LinkedList<AICharacter> getTempAdd()
	{
		return _tempAdd;
	}

	public void setTempAdd(LinkedList<AICharacter> tempAdd)
	{
		_tempAdd = tempAdd;
	}

	public LinkedList<Follower> getFollowers()
	{
		return _followers;
	}

	public void setFollowers(LinkedList<Follower> followers)
	{
		_followers = followers;
	}

	public Camera getCam()
	{
		return _cam;
	}

	public void setCam(Camera cam)
	{
		_cam = cam;
	}

	public GameFrame getFrame()
	{
		return _frame;
	}

	public void setFrame(GameFrame frame)
	{
		_frame = frame;
	}

	public int getIncreaseBorder()
	{
		return _increaseBorder;
	}

	public void setIncreaseBorder(int increaseBorder)
	{
		_increaseBorder = increaseBorder;
	}
}
