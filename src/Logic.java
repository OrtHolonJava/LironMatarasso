import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;

public class Logic
{
	private Map _map;
	private Player _player;
	private LinkedList<AICharacter> _aiCharacters;
	private Camera _cam;
	private LinkedList<Integer> _passables;

	public Logic(Player player, Camera cam, Map map, LinkedList<Integer> passables)
	{
		_player = player;
		_cam = cam;
		_cam.updateCamPoint(_player);
		_map = map;
		_passables = passables;
		_aiCharacters = new LinkedList<AICharacter>();
		addAICharacters(100);
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
	public void addAICharacters(int count)
	{
		for (int i = 0; i < count; i++)
		{
			_aiCharacters.add(new AICharacter(900, 450, _player.getWidth() / 4, _player.getHeight() / 4, 2, _player.getFramesPath()));
		}
	}

	public boolean collisionHandle(Character c)
	{
		if (!checkCollision(c))
		{
			for (Rectangle r : c.getRects())
			{
				while (c.getHitbox().intersects(r))
				{
					c.move(Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (c.getX()), (r.y + r.getHeight() / 2) - (c.getY()))), 2);
				}
			}
			checkCollision(c);
			return true;
		}
		return false;
	}

	public void doLogic()
	{
		movementLogic();
		_player.updateStats();
		for (AICharacter c : _aiCharacters)
		{
			c.basicAIMovement(_player);
			if (collisionHandle(c))
				c.setNewTarget();
		}
	}

	public void movementLogic()
	{
		_cam.getFinalMousePoint().setLocation(_cam.getCamPoint().x + _cam.getMousePoint().x, _cam.getCamPoint().y + _cam.getMousePoint().y);
		if (_cam.getFinalMousePoint().distance(_player.getLoc()) > 2)
		{
			_player.setAngle(Math.toDegrees(Math.atan2(_cam.getFinalMousePoint().y	- _player.getY(),
														_cam.getFinalMousePoint().x - _player.getX()))
								+ 90);
		}

		double disToSpeedRatio = (_cam.getFinalMousePoint().distance(_player.getX(), _player.getY()) / (5 * BlockType.getSize()));
		disToSpeedRatio = (disToSpeedRatio > 1) ? 1 : disToSpeedRatio;
		_player.setBaseSpeed(8 * disToSpeedRatio);
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
				Point temp = new Point(curCol, curRow);
				if (curRow >= 0 && curCol >= 0 && curRow < _map.getHeight() && curCol < _map.getWidth() && _map.getHmap().containsKey(temp))
				{
					Rectangle rect = new Rectangle(curCol	* BlockType.getSize(), curRow * BlockType.getSize(), BlockType.getSize(),
													BlockType.getSize());
					if (c.getHitbox().intersects(rect))
					{
						if (!_passables.contains(_map.getHmap().get(temp).getBlockID()))
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
							c.applySeaweedSlowdown(_map.getHmap().get(temp).getBlockID() == 3);
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
		g.drawRect((int) _cam.getCamPoint().x, (int) _cam.getCamPoint().y, 100, 100);
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

}
