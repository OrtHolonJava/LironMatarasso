import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

/**
 * Follower class for the enemy characters that follow the player
 * 
 * @author liron
 *
 */
public class Follower extends AICharacter
{
	private LinkedList<Point2D.Double> _path;
	private Rectangle _searchRect;

	/**
	 * Init a new Follower object with the following parameters:
	 * 
	 * @param x
	 * @param y
	 * @param type
	 */
	public Follower(double x, double y, int type)
	{
		super(x, y, type, 0);
		_path = new LinkedList<Point2D.Double>();
		_searchRect = new Rectangle(0, 0, 0, 0);
	}

	/**
	 * changes the character current target according to its location and path
	 */
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
		move(_angle, _finalSpeed);
	}

	/**
	 * moves the character in a basic way when it didn't found a path to the
	 * player
	 */
	@Override
	public void basicAIMovement(Player p)
	{
		_path.clear();
		updateFinalSpeed();
		if (nearTarget())
		{
			super.setNewTarget();
		}
		move(_angle, _finalSpeed);
	}

	/**
	 * setting a new target for the character in a range of 90 degrees from its
	 * current angle
	 */
	@Override
	public void setNewTarget()
	{
		int range = 90;
		Random r = new Random();
		int a = r.nextInt(range) - range / 2;
		setAngleAndTarget((_angle + a - 90));
	}

	/**
	 * draws the character
	 */
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

	/**
	 * draws the character path to the player
	 * 
	 * @param g
	 * @param path
	 */
	public void drawPath(Graphics2D g, LinkedList<Point2D.Double> path)
	{
		g.setColor(Color.red);
		if (!path.isEmpty())
		{
			Point2D.Double start = path.getFirst();
			for (int i = 1; i < path.size(); i++)
			{
				Point2D.Double p = path.get(i);
				g.drawRect((int) start.x, (int) start.y, 10, 10);
				g.drawLine((int) start.x, (int) start.y, (int) p.x, (int) p.y);
				start = p;
			}
		}
	}

	/**
	 * draws the bounds of the area in which the character searches for the
	 * player
	 * 
	 * @param g
	 */
	public void drawSearchRect(Graphics2D g)
	{
		g.setColor(Color.yellow);
		g.drawRect(	_searchRect.x * Block.getSize(), _searchRect.y * Block.getSize(), _searchRect.width * Block.getSize(),
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
