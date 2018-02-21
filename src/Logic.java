import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Logic
{
	private Map _map;
	private Player _player;
	private LinkedList<AICharacter> _aiCharacters;
	private Camera _cam;
	private LinkedList<Rectangle> _rects;
	private LinkedList<Integer> _passables;

	public Logic(Player player, Camera cam, Map map, LinkedList<Integer> passables)
	{
		_player = player;
		_cam = cam;
		_map = map;
		_passables = passables;
		_rects = new LinkedList<Rectangle>();
		_aiCharacters = new LinkedList<AICharacter>();
		_aiCharacters.add(new AICharacter(900, 450, _player.getWidth() / 4, _player.getHeight() / 4, 2, _player.getFramesPath()));
	}

	public void doLogic()
	{
		movementLogic();
		for (AICharacter c : _aiCharacters)
		{
			c.basicAIMovement();
			if (!checkCollision(c))
			{
				for (Rectangle r : _rects)
				{
					while (c.getHitbox().intersects(r))
					{
						c.move(	Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (c.getX()), (r.y + r.getHeight() / 2) - (c.getY()))),
								1);
					}
				}
				c.setNewTarget();
				checkCollision(c);
				// c.setNewTarget();
				// c.setNewTarget();
			}
			// System.out.println("colido");
			// for (Rectangle r : _rects)
			// {
			// while (c.getHitbox().intersects(r))
			// {
			// c.basicAIMovement();
			// }
			// }
			// checkCollision(c);
		}
	}

	public void movementLogic()
	{
		_cam.getFinalMousePoint().setLocation(_cam.getCamPoint().x + _cam.getMousePoint().x, _cam.getCamPoint().y + _cam.getMousePoint().y);
		if (_cam.getFinalMousePoint().distance(_player.getLoc()) > 2)
		{
			_player.setAngle(Math.toDegrees(-Math.atan2(_cam.getFinalMousePoint().x	- _player.getX(),
														_cam.getFinalMousePoint().y - _player.getY()))
								+ 180);
		}

		double disToSpeedRatio = (_cam.getFinalMousePoint().distance(_player.getX(), _player.getY()) / (5 * BlockType.getSize()));
		disToSpeedRatio = (disToSpeedRatio > 1) ? 1 : disToSpeedRatio;
		_player.setBaseSpeed(8 * disToSpeedRatio);
		_player.updateFinalSpeed();
		// _cam.moveCam(_player.getAngle(), _player.getFinalSpeed());
		_player.move(_player.getAngle(), _player.getFinalSpeed());
		_cam.updateCamPoint(_player);
		if (!checkCollision(_player))
		{
			for (Rectangle r : _rects)
			{
				while (_player.getHitbox().intersects(r))
				{
					// _cam.moveCam( Math.toDegrees(-Math.atan2((r.x +
					// r.getWidth() / 2) - (_player.getX()),
					// (r.y + r.getHeight() / 2) - (_player.getY()))),
					// 1);
					_player.move(	Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2)	- (_player.getX()),
															(r.y + r.getHeight() / 2) - (_player.getY()))),
									1);
					_cam.updateCamPoint(_player);

				}
			}
			checkCollision(_player);
		}
	}

	public boolean checkCollision(Character c)
	{
		c.applySeaweedSlowdown(false);
		_rects = new LinkedList<Rectangle>();
		boolean flag = true;
		c.getColiList().clear();
		for (int curRow = (int) (c.getY() / BlockType.getSize()) - 1; curRow <= (c.getY() / BlockType.getSize()) + 1; curRow++)
		{
			for (int curCol = (int) (c.getX() / BlockType.getSize()) - 1; curCol <= (c.getX() / BlockType.getSize()) + 1; curCol++)
			{
				if (curRow >= 0	&& curCol >= 0 && curRow < _map.getHeight() && curCol < _map.getWidth()
					&& _map.getHmap().containsKey(curCol + curRow * _map.getWidth()))
				{
					Rectangle rect = new Rectangle(curCol	* BlockType.getSize(), curRow * BlockType.getSize(), BlockType.getSize(),
													BlockType.getSize());
					if (c.getHitbox().intersects(rect))
					{
						if (!_passables.contains(_map.getHmap().get(curCol + curRow * _map.getWidth()).getBlockID()))
						{
							_rects.add(rect);
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
							c.applySeaweedSlowdown(_map.getHmap().get(curCol + curRow * _map.getWidth()).getBlockID() == 3);
					}
				}
			}
		}
		return flag;
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

	public LinkedList<Rectangle> getRects()
	{
		return _rects;
	}

	public void setRects(LinkedList<Rectangle> rects)
	{
		_rects = rects;
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
