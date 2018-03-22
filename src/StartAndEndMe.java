import java.awt.geom.Point2D;
import java.io.Serializable;

public class StartAndEndMe implements Serializable, Comparable<StartAndEndMe>, Cloneable
{
	private Point2D.Double _start, _end;
	private static final long serialVersionUID = 6934798852710196981L;

	public StartAndEndMe(double startX, double startY, double endX, double endY)
	{
		_start = new Point2D.Double(startX, startY);
		_end = new Point2D.Double(endX, endY);
	}

	public Point2D.Double getStart()
	{
		return _start;
	}

	public Point2D.Double getEnd()
	{
		return _end;
	}

	public void setStart(Point2D.Double start)
	{
		_start = start;
	}

	public void setEnd(Point2D.Double end)
	{
		_end = end;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null	&& obj instanceof StartAndEndMe && ((StartAndEndMe) obj)._end.equals(_end)
				&& ((StartAndEndMe) obj)._start.equals(_start);
	}

	@Override
	public int compareTo(StartAndEndMe o)
	{
		// TODO Auto-generated method stub
		return (equals(o)) ? 0 : 1;
	}

	@Override
	public int hashCode()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString()
	{
		return "start: " + _start.toString() + " end: " + _end.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
		return new StartAndEndMe(_start.x, _start.y, _end.x, _end.y);
	}

}
