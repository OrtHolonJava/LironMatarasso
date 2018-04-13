import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class AICharacter extends Character
{
	private Point2D.Double _target;
	private double _tempSpeedBoost;

	public AICharacter(double x, double y, int width, int height, double baseSpeed, BufferedImage[] frames)
	{
		super(x, y, width, height, baseSpeed, frames);
		Random r = new Random();
		_tempSpeedBoost = 0;
		_target = new Point2D.Double(-1, -1);
		setAngle(r.nextInt(360));
		updateFinalSpeed();
		setNewTarget();
	}

	public void chase(Player p)
	{
		setAngleAndTarget(new Point2D.Double(p.getLoc().x, p.getLoc().y));
	}

	public boolean nearTarget()
	{
		return getLoc().distance(_target) < getFinalSpeed();
	}

	public void basicAIMovement(Player p)
	{
		if (getLoc().distance(p.getLoc()) < BlockType.getSize() * 2.5)
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
		move(getAngle(), getFinalSpeed());
	}

	public void setNewTarget()
	{
		Random r = new Random();
		int a = r.nextInt(30) - 15;
		setAngleAndTarget((getAngle() + a - 90));
	}

	public void setAngleAndTarget(Point2D.Double target)
	{

		_target.setLocation(target);
		setAngle(Math.toDegrees(Math.atan2(_target.getY() - getY(), _target.getX() - getX())));
	}

	public void setAngleAndTarget(double a)
	{
		double radius = BlockType.getSize() * 2.5;
		setAngle(a);
		_target.setLocation(getX() + radius * Math.sin(Math.toRadians(getAngle())), getY() - radius * Math.cos(Math.toRadians(getAngle())));
	}

	@Override
	public void updateFinalSpeed()
	{
		setFinalSpeed((getBaseSpeed() + _tempSpeedBoost) * getSpeedSeaweedSlowdown());
	}

	@Override
	public synchronized void draw(Graphics2D g, boolean drawDebug)
	{
		super.draw(g, drawDebug);
		g.setColor(Color.orange);
		if (drawDebug)
		{
			g.drawRect((int) _target.getX(), (int) _target.getY(), 100, 100);
			g.drawString(String.valueOf(getAngle()), (int) _target.getX(), (int) _target.getY());
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
