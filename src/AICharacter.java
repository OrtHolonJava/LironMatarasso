import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * AICharacter class for computer controlled characters
 * 
 * @author liron
 */
public class AICharacter extends Character
{
	protected Point2D.Double _target;
	protected double _tempSpeedBoost;

	/**
	 * Init a new AICharacter object with the following parameters:
	 * 
	 * @param x
	 * @param y
	 * @param type
	 * @param angle
	 */
	public AICharacter(double x, double y, int type, double angle)
	{
		super(x, y, type);
		_tempSpeedBoost = 0;
		_target = new Point2D.Double(-1, -1);
		setCorrectedAngle(angle);
		updateFinalSpeed();
		setNewTarget();
	}

	/**
	 * 
	 * @return if the character distance from its target is less than a block
	 *         size
	 */
	public boolean nearTarget()
	{
		return _loc.distance(_target) < Block.getSize();
	}

	/**
	 * moves the character in a basic way: toward the target or away from the
	 * player
	 * 
	 * @param p
	 */
	public void basicAIMovement(Player p)
	{
		if (_loc.distance(p._loc) < BlockGraphics.getSize() * 2.5)
		{
			_tempSpeedBoost = 2;
			setAngleAndTarget(180 + (Math.toDegrees(Math.atan2(p.getY() - getY(), p.getX() - getX()))));
		}
		else
		{
			_tempSpeedBoost = 0;
		}
		updateFinalSpeed();
		if (nearTarget())
		{
			setNewTarget();
		}
		move(_angle, _finalSpeed);
	}

	/**
	 * setting a new target for the character in a range of 30 degrees from its
	 * current angle
	 */
	public void setNewTarget()
	{
		int range = 30;
		Random r = new Random();
		int a = r.nextInt(range) - range / 2;
		setAngleAndTarget((_angle + a - 90));
	}

	/**
	 * setting the target for the character and changing its angle so it will be
	 * toward it
	 * 
	 * @param target
	 */
	public void setAngleAndTarget(Point2D.Double target)
	{
		_target.setLocation(target);
		setCorrectedAngle(Math.toDegrees(Math.atan2(_target.getY() - getY(), _target.getX() - getX())));
	}

	/**
	 * setting the angle of the character and setting a target in the angle
	 * direction
	 * 
	 * @param a
	 */
	public void setAngleAndTarget(double a)
	{
		double radius = BlockGraphics.getSize() * 2.5;
		setCorrectedAngle(a);
		_target.setLocation(getX() + radius * Math.sin(Math.toRadians(_angle)), getY() - radius * Math.cos(Math.toRadians(_angle)));
	}

	/**
	 * updates the final speed of the character
	 */
	@Override
	public void updateFinalSpeed()
	{
		_finalSpeed = (_characterType.getBaseSpeed() + _tempSpeedBoost) * _speedSeaweedSlowdown;
	}

	/**
	 * draws the character
	 */
	@Override
	public synchronized void draw(Graphics2D g, boolean drawDebug)
	{
		super.draw(g, drawDebug);
		g.setColor(Color.orange);
		if (drawDebug)
		{
			g.drawRect((int) _target.getX(), (int) _target.getY(), 100, 100);
			g.drawString(String.valueOf(_angle), (int) _target.getX(), (int) _target.getY());
		}
	}

	public Point2D.Double getTarget()
	{
		return _target;
	}

	public void setTarget(Point2D.Double target)
	{
		_target = target;
	}
}
