import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;

public class Follower extends AICharacter
{
	private LinkedList<Point2D.Double> _path;

	public Follower(double x, double y, int width, int height, double baseSpeed, BufferedImage[] frames)
	{
		super(x, y, width, height, baseSpeed, frames);
		_path = new LinkedList<Point2D.Double>();
	}

	public void followPath()
	{
		setAngleAndTarget(_path.getFirst());
		if (nearTarget())
		{
			if (_path.size() > 1)
			{
				_path.removeFirst();
			}
		}
		move(getAngle(), getFinalSpeed());
	}

	@Override
	public void basicAIMovement(Player p)
	{
		_path.clear();
		updateFinalSpeed();
		if (nearTarget())
		{
			setNewTarget();
		}
		move(getAngle(), getFinalSpeed());
	}

	public void drawPath(Graphics2D g)
	{
		Iterator<Point2D.Double> iterator = _path.iterator();
		g.setColor(Color.red);
		if (!_path.isEmpty())
		{
			Point2D.Double start = getLoc();
			g.drawLine((int) start.x, (int) start.y, (int) getLoc().x, (int) getLoc().y);
			while (iterator.hasNext())
			{
				Point2D.Double p = iterator.next();
				g.drawLine((int) start.x, (int) start.y, (int) p.x, (int) p.y);
				start = p;
			}
		}
	}

	@Override
	public synchronized void Paint(Graphics2D g, boolean isDebug)
	{
		// TODO Auto-generated method stub
		super.Paint(g, isDebug);
		// if (isDebug)
		// {
		drawPath(g);
		g.setColor(Color.orange);
		g.drawRect((int) getTarget().getX(), (int) getTarget().getY(), 100, 100);
		g.drawString(String.valueOf(getAngle()), (int) getTarget().getX(), (int) getTarget().getY());

		// }

	}

	public void setPath(LinkedList<Point2D.Double> path)
	{
		_path = path;
	}

	public LinkedList<Point2D.Double> getPath()
	{
		return _path;
	}

}
