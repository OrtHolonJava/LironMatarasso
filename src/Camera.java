import java.awt.geom.Point2D;

public class Camera
{
	private double _playerOffsetX, _playerOffsetY;
	private Point2D.Double _mousePoint, _finalMousePoint, _centerPoint, _camPoint;
	private int _panelWidth, _panelHeight, _mapPixelWidth, _mapPixelHeight;

	public Camera(	double playerOffsetX, double playerOffsetY, Point2D.Double camPoint, int panelWidth, int panelHeight, int mapWidth,
					int mapHeight)
	{
		_playerOffsetX = playerOffsetX;
		_playerOffsetY = playerOffsetY;
		_mousePoint = new Point2D.Double(0, 0);
		_centerPoint = new Point2D.Double(0, 0);
		_finalMousePoint = new Point2D.Double(0, 0);
		_camPoint = camPoint;
		_panelWidth = panelWidth;
		_panelHeight = panelHeight;
		_mapPixelWidth = mapWidth * BlockType.getSize();
		_mapPixelHeight = mapHeight * BlockType.getSize();

	}

	public void move(double angle, double speed, Player player)
	{
		if (_playerOffsetX == 0)
		{
			_camPoint.x += speed * Math.sin(Math.toRadians(angle));
			if (_camPoint.x < 0 || _camPoint.x > _mapPixelWidth - _panelWidth)
			{
				_camPoint.x = (_camPoint.x < 0) ? 0 : _mapPixelWidth - _panelWidth;
				_playerOffsetX += speed * Math.sin(Math.toRadians(angle));
			}
		}
		else
		{
			int preSignX = (int) Math.signum(_playerOffsetX);
			_playerOffsetX += speed * Math.sin(Math.toRadians(angle));
			if (preSignX != (int) Math.signum(_playerOffsetX))
			{
				_camPoint.x += _playerOffsetX;
				_playerOffsetX = 0;
			}
		}

		if (_playerOffsetY == 0)
		{
			_camPoint.y -= speed * Math.cos(Math.toRadians(angle));
			if (_camPoint.y < BlockType.getSize() || _camPoint.y > _mapPixelHeight - _panelHeight - BlockType.getSize())
			{
				_camPoint.y = (_camPoint.y < BlockType.getSize())	? BlockType.getSize()
																	: _mapPixelHeight - _panelHeight - BlockType.getSize();
				_playerOffsetY -= speed * Math.cos(Math.toRadians(angle));
			}
		}
		else
		{
			int preSignY = (int) Math.signum(_playerOffsetY);
			_playerOffsetY -= speed * Math.cos(Math.toRadians(angle));
			if (preSignY != (int) Math.signum(_playerOffsetY))
			{
				_camPoint.y += _playerOffsetY;
				_playerOffsetY = 0;
			}
		}
		_camPoint.y = Math.round(_camPoint.y);
		_camPoint.x = Math.round(_camPoint.x);
		_centerPoint.setLocation(_camPoint.x + _panelWidth / 2, _camPoint.y + _panelHeight / 2);
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		player.setCords((int) (_centerPoint.x + _playerOffsetX), (int) (_centerPoint.y + _playerOffsetY));
		player.setHitbox();
	}

	public double getPlayerOffsetX()
	{
		return _playerOffsetX;
	}

	public void setPlayerOffsetX(double playerOffsetX)
	{
		_playerOffsetX = playerOffsetX;
	}

	public double getPlayerOffsetY()
	{
		return _playerOffsetY;
	}

	public void setPlayerOffsetY(double playerOffsetY)
	{
		_playerOffsetY = playerOffsetY;
	}

	public Point2D.Double getMousePoint()
	{
		return _mousePoint;
	}

	public void setMousePoint(Point2D.Double mousePoint)
	{
		_mousePoint = mousePoint;
	}

	public Point2D.Double getFinalMousePoint()
	{
		return _finalMousePoint;
	}

	public void setFinalMousePoint(Point2D.Double finalMousePoint)
	{
		_finalMousePoint = finalMousePoint;
	}

	public Point2D.Double getCenterPoint()
	{
		return _centerPoint;
	}

	public void setCenterPoint(Point2D.Double centerPoint)
	{
		_centerPoint = centerPoint;
	}

	public Point2D.Double getCamPoint()
	{
		return _camPoint;
	}

	public void setCamPoint(Point2D.Double camPoint)
	{
		_camPoint = camPoint;
	}

	public int getPanelWidth()
	{
		return _panelWidth;
	}

	public void setPanelWidth(int panelWidth)
	{
		_panelWidth = panelWidth;
	}

	public int getPanelHeight()
	{
		return _panelHeight;
	}

	public void setPanelHeight(int panelHeight)
	{
		_panelHeight = panelHeight;
	}

	public int getMapPixelWidth()
	{
		return _mapPixelWidth;
	}

	public void setMapPixelWidth(int mapPixelWidth)
	{
		_mapPixelWidth = mapPixelWidth;
	}

	public int getMapPixelHeight()
	{
		return _mapPixelHeight;
	}

	public void setMapPixelHeight(int mapPixelHeight)
	{
		_mapPixelHeight = mapPixelHeight;
	}

}
