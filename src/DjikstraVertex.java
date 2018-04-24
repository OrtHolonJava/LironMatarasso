import java.awt.Point;

/**
 * DjikstraVertex class for representing the vertexes in the Djikstra Graph
 * 
 * @author liron
 *
 */
public class DjikstraVertex implements Comparable<DjikstraVertex>, Cloneable
{
	private Point _prev, _current;
	private boolean _isClear;
	private double _distance, _toEnd;
	private int _endX, _endY;

	/**
	 * Init a new DjikstraVertex object with the following parameters:
	 * 
	 * @param x
	 * @param y
	 * @param isClear
	 * @param endX
	 * @param endY
	 */
	public DjikstraVertex(int x, int y, boolean isClear, int endX, int endY)
	{
		_distance = 1000000;
		_current = new Point(x, y);
		_prev = new Point(-1, -1);
		_isClear = isClear;
		_endX = endX;
		_endY = endY;
		_toEnd = Math.hypot(x - _endX, y - _endY);
	}

	public int getX()
	{
		return _current.x;
	}

	public int getY()
	{
		return _current.y;
	}

	public Point getPrev()
	{
		return _prev;
	}

	public void setPrev(Point prev)
	{
		_prev = prev;
	}

	public Point getCurrent()
	{
		return _current;
	}

	public void setCurrent(Point current)
	{
		_current = current;
	}

	public boolean isClear()
	{
		return _isClear;
	}

	public void setClear(boolean isClear)
	{
		_isClear = isClear;
	}

	public double getDistance()
	{
		return _distance;
	}

	public void setDistance(double distance)
	{
		_distance = distance;
	}

	public double getToEnd()
	{
		return _toEnd;
	}

	public void setToEnd(double toEnd)
	{
		_toEnd = toEnd;
	}

	public int getEndX()
	{
		return _endX;
	}

	public void setEndX(int endX)
	{
		_endX = endX;
	}

	public int getEndY()
	{
		return _endY;
	}

	public void setEndY(int endY)
	{
		_endY = endY;
	}

	@Override
	protected Object clone()
	{
		return new DjikstraVertex(_current.x, _current.y, _isClear, _endX, _endY);
	}

	@Override
	public int compareTo(DjikstraVertex o)
	{
		return (int) _distance - (int) o._distance;
	}

	@Override
	public String toString()
	{
		return _current.toString() + " " + _distance;
	}

}
