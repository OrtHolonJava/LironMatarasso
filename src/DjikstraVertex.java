import java.awt.Point;

public class DjikstraVertex implements Comparable<DjikstraVertex>, Cloneable
{
	private Point _prev, _loc;
	private boolean _isClear;
	private double _distance, _toEnd;
	private int _endX, _endY;

	public DjikstraVertex(int x, int y, boolean isClear, int endX, int endY)
	{
		_distance = 1000000;
		_loc = new Point(x, y);
		_prev = new Point(-1, -1);
		_isClear = isClear;
		_endX = endX;
		_endY = endY;
		_toEnd = Math.hypot(x - _endX, y - _endY);
	}

	public double getToEnd()
	{
		return _toEnd;
	}

	public void setToEnd(double toEnd)
	{
		_toEnd = toEnd;
	}

	public double getDistance()
	{
		return _distance;
	}

	public int getX()
	{
		return _loc.x;
	}

	public int getY()
	{
		return _loc.y;
	}

	public void setDistance(double distance)
	{
		_distance = distance;
	}

	public Point getPrev()
	{
		return _prev;
	}

	public void setPrev(Point prev)
	{
		_prev = prev;
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
		// TODO Auto-generated method stub
		return new DjikstraVertex(_loc.x, _loc.y, _isClear, _endX, _endY);
	}

	@Override
	public int compareTo(DjikstraVertex o)
	{
		// TODO Auto-generated method stub
		return (int) _distance - (int) o._distance;
	}

	public Point getLoc()
	{
		return _loc;
	}

	public void setLoc(Point loc)
	{
		_loc = loc;
	}

	public boolean isClear()
	{
		return _isClear;
	}

	public void setClear(boolean isClear)
	{
		_isClear = isClear;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return _loc.toString() + " " + _distance;
	}

}
