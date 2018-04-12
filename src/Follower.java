import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

public class Follower extends AICharacter
{
	private LinkedList<Point2D.Double> _path;
	private Rectangle _searchRect;

	public Follower(double x, double y, int width, int height, double baseSpeed, BufferedImage[] frames)
	{
		super(x, y, width, height, baseSpeed, frames);
		_path = new LinkedList<Point2D.Double>();
		_searchRect = new Rectangle(0, 0, 0, 0);
	}

	public void followPath()
	{
		if (nearTarget())
		{
			if (_path.size() > 1)
			{
				_path.removeFirst();
			}
		}
		setAngleAndTarget(_path.getFirst());

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

	@Override
	public synchronized void draw(Graphics2D g, boolean drawDebug)
	{
		super.draw(g, drawDebug);
		if (drawDebug)
		{
			drawPath(g, _path);
			g.setColor(Color.YELLOW);
			drawSearchRect(g);
		}
	}

	public void drawPath(Graphics2D g, LinkedList<Point2D.Double> path)
	{
		g.setColor(Color.red);
		if (!path.isEmpty())
		{
			Point2D.Double start = path.getFirst();
			for (int i = 1; i < path.size(); i++)
			{
				Point2D.Double p = path.get(i);
				g.drawLine((int) start.x, (int) start.y, (int) p.x, (int) p.y);
				start = p;
			}
		}
	}

	public void drawSearchRect(Graphics2D g)
	{
		g.setColor(Color.yellow);
		g.drawRect(_searchRect.x	* Block.getSize(), _searchRect.y * Block.getSize(), _searchRect.width * Block.getSize(),
					_searchRect.height * Block.getSize());
	}

	public void setPath(LinkedList<Point2D.Double> path)
	{
		_path = path;
	}

	public LinkedList<Point2D.Double> getPath()
	{
		return _path;
	}

	public Rectangle getSearchRect()
	{
		return _searchRect;
	}

	public void setSearchRect(Rectangle searchRect)
	{
		_searchRect = searchRect;
	}
}
