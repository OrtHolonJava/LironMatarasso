import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

public class Camera
{
	private Point2D.Double _mousePoint, _finalMousePoint;
	private Point _camPoint;
	private int _panelWidth, _panelHeight, _mapPixelWidth, _mapPixelHeight;
	private Rectangle _screenRect;

	public Camera(Point camPoint, int panelWidth, int panelHeight, int mapWidth, int mapHeight)
	{
		_mousePoint = new Point2D.Double(0, 0);
		_finalMousePoint = new Point2D.Double(0, 0);
		_screenRect = new Rectangle(0, 0, panelWidth, panelHeight);
		_camPoint = camPoint;
		_panelWidth = panelWidth;
		_panelHeight = panelHeight;
		_mapPixelWidth = mapWidth * BlockType.getSize();
		_mapPixelHeight = mapHeight * BlockType.getSize();
	}

	public void updateCamPoint(Character c)
	{
		_camPoint.setLocation(c.getX() - _panelWidth / 2, c.getY() - _panelHeight / 2);
		if (_camPoint.x < BlockType.getSize() || _camPoint.x > _mapPixelWidth - _panelWidth - BlockType.getSize())
		{
			_camPoint.x = (_camPoint.x < BlockType.getSize()) ? BlockType.getSize() : _mapPixelWidth - _panelWidth - BlockType.getSize();
		}
		if (_camPoint.y < BlockType.getSize() || _camPoint.y > _mapPixelHeight - _panelHeight - BlockType.getSize())
		{
			_camPoint.y = (_camPoint.y < BlockType.getSize()) ? BlockType.getSize() : _mapPixelHeight - _panelHeight - BlockType.getSize();
		}
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_screenRect.setLocation(_camPoint);
	}

	public boolean inScreen(Area a)
	{
		return a.intersects(_screenRect);
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

	public Point getCamPoint()
	{
		return _camPoint;
	}

	public void setCamPoint(Point camPoint)
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

	public Rectangle getScreenRectangle()
	{
		// TODO Auto-generated method stub
		return _screenRect;
	}
}
