import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;

public class Logic
{
	private Map _map;
	private Player _player;
	private Camera _cam;
	private LinkedList<Rectangle> _rects;
	private LinkedList<Point2D.Double> _coliList;
	private LinkedList<Integer> _passables;

	public Logic(Player player, Camera cam, Map map, LinkedList<Integer> passables)
	{
		_player = player;
		_cam = cam;
		_map = map;
		_passables = passables;
		_rects = new LinkedList<Rectangle>();
		_coliList = new LinkedList<Point2D.Double>();
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
		_cam.move(_player.getAngle(), _player.getFinalSpeed(), _player);
		if (!checkCollision())
		{
			for (Rectangle r : _rects)
			{
				while (_player.getHitbox().intersects(r))
				{
					_cam.move(	Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2)	- (_player.getX()),
															(r.y + r.getHeight() / 2) - (_player.getY()))),
								1, _player);
				}
			}
			checkCollision();
		}
	}

	public boolean checkCollision()
	{
		_player.setCords(	(int) (_cam.getCenterPoint().x + _cam.getPlayerOffsetX()),
							(int) (_cam.getCenterPoint().y + _cam.getPlayerOffsetY()));
		_player.setHitbox();
		_player.applySeaweedSlowdown(false);
		_rects = new LinkedList<Rectangle>();
		boolean flag = true;
		_coliList = new LinkedList<Point2D.Double>();
		for (int i = (int) (_player.getY() / BlockType.getSize()) - 2; i <= (_player.getY() / BlockType.getSize()) + 2; i++)
		{
			for (int j = (int) (_player.getX() / BlockType.getSize()) - 2; j <= (_player.getX() / BlockType.getSize()) + 2; j++)
			{
				if (i >= 0 && j >= 0 && i < _map.getHeight() && j < _map.getWidth() && _map.getHmap().containsKey(j + i * _map.getWidth()))
				{
					Rectangle rect = new Rectangle(j	* BlockType.getSize(), i * BlockType.getSize(), BlockType.getSize(),
													BlockType.getSize());
					if (_player.getHitbox().intersects(rect))
					{
						if (!_passables.contains(_map.getHmap().get(j + i * _map.getWidth()).getBlockID()))
						{
							_rects.add(rect);
							flag = false;
							for (Point2D p : _player.getPolyList())
							{
								if (rect.contains(p))
								{
									// System.out.println(p + " point at " +
									// rect.toString());
									_coliList.add(new Point2D.Double(p.getX(), p.getY()));
								}
							}
						}
						_player.applySeaweedSlowdown(_map.getHmap().get(j + i * _map.getWidth()).getBlockID() == 3);
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

	public LinkedList<Point2D.Double> getColiList()
	{
		return _coliList;
	}

	public void setColiList(LinkedList<Point2D.Double> coliList)
	{
		_coliList = coliList;
	}

	public LinkedList<Integer> getPassables()
	{
		return _passables;
	}

	public void setPassables(LinkedList<Integer> passables)
	{
		_passables = passables;
	}
}
