import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * Camera class for following the player arround the map
 * 
 * @author liron
 */
public class Camera
{
	private Point2D.Double _mousePoint, _finalMousePoint;
	private Point _camPoint;
	private int _panelWidth, _panelHeight, _mapPixelWidth, _mapPixelHeight;
	private Rectangle _screenRect;

	/**
	 * Init a new Camera object with the following parameters:
	 * 
	 * @param camPoint
	 * @param panelWidth
	 * @param panelHeight
	 * @param mapWidth
	 * @param mapHeight
	 */
	public Camera(Point camPoint, int panelWidth, int panelHeight, int mapWidth, int mapHeight)
	{
		_mousePoint = new Point2D.Double(0, 0);
		_finalMousePoint = new Point2D.Double(0, 0);
		_screenRect = new Rectangle(0, 0, panelWidth, panelHeight);
		_camPoint = camPoint;
		_panelWidth = panelWidth;
		_panelHeight = panelHeight;
		_mapPixelWidth = mapWidth * BlockGraphics.getSize();
		_mapPixelHeight = mapHeight * BlockGraphics.getSize();
	}

	/**
	 * updating the camera position according to the given character, tries to
	 * put the character in the center of the screen
	 * 
	 * @param c
	 */
	public void updateCamPoint(Character c)
	{
		_camPoint.setLocation(c.getX() - _panelWidth / 2, c.getY() - _panelHeight / 2);
		if (_camPoint.x < BlockGraphics.getSize() || _camPoint.x > _mapPixelWidth - _panelWidth - BlockGraphics.getSize())
		{
			_camPoint.x = (_camPoint.x < BlockGraphics.getSize())	? BlockGraphics.getSize()
																	: _mapPixelWidth - _panelWidth - BlockGraphics.getSize();
		}
		if (_camPoint.y < BlockGraphics.getSize() || _camPoint.y > _mapPixelHeight - _panelHeight - BlockGraphics.getSize())
		{
			_camPoint.y = (_camPoint.y < BlockGraphics.getSize())	? BlockGraphics.getSize()
																	: _mapPixelHeight - _panelHeight - BlockGraphics.getSize();
		}
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_screenRect.setLocation(_camPoint);
	}

	/**
	 * checks if a certain area is inside the screen
	 * 
	 * @param a
	 * @return
	 */
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
		return _screenRect;
	}
}
