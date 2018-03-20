import java.awt.Point;

public class DjikstraVertex implements Comparable<DjikstraVertex>
{
	private Point _prev, _loc;
	private boolean _isClear;
	private double _distance;

	public DjikstraVertex(int x, int y, boolean isClear)
	{
		_distance = 1000000;
		_loc = new Point(x, y);
		_prev = new Point(-1, -1);
		_isClear = isClear;
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
