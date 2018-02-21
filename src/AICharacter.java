import java.awt.geom.Point2D;
import java.util.Random;

public class AICharacter extends Character
{
	private Point2D.Double _target;

	public AICharacter(double x, double y, int width, int height, double baseSpeed, String framesPath)
	{
		super(x, y, width, height, baseSpeed, framesPath);
		_target = new Point2D.Double(-1, -1);
		updateFinalSpeed();
		setNewTarget();
	}


	public void basicAIMovement()
	{
		updateFinalSpeed();
		// System.out.println("cur: " + getLoc());
		// System.out.println("target: " + _target);
		if (getLoc().distance(_target) < getFinalSpeed())
		{
			setNewTarget();
		}
		move(getAngle(), getFinalSpeed());
	}

	public void setNewTarget()
	{
		Random r = new Random();
		int radius = 100;
		int a = r.nextInt(90) - 45;
		setAngle(Math.abs((getAngle() + a) % 360));
		_target.setLocation(getLoc().x	+ radius * Math.sin(Math.toRadians(getAngle())),
							getLoc().y - radius * Math.cos(Math.toRadians(getAngle())));
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
