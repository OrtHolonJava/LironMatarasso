import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;

public class Player extends Character
{
	private double _speedMouseBoost, _disToSpeedRatio, _stamina, _hunger, _health;
	private boolean _isCooldown;
	private Area _mouthHitbox;

	public Player(double x, double y, int width, int height, double baseSpeed, BufferedImage[] frames)
	{
		super(x, y, width, height, baseSpeed, frames);
		_stamina = 100;
		_disToSpeedRatio = 1;
		_hunger = 100;
		_health = 100;
		_isCooldown = false;
		// Timer t = new Timer(1000, this);
		// t.start();
	}

	public double getHunger()
	{
		return _hunger;
	}

	public void drawBars(Graphics g)
	{
		g.setColor(Color.red);
		g.drawString("health: " + String.valueOf((int) _health), g.getClipBounds().x, g.getClipBounds().y + 10);
		g.drawRect(g.getClipBounds().x + 70, g.getClipBounds().y, 100, 10);
		g.fillRect(g.getClipBounds().x + 70, g.getClipBounds().y, (int) _health, 10);
		g.setColor(Color.green);
		g.drawString("stamina: " + String.valueOf((int) _stamina), g.getClipBounds().x, g.getClipBounds().y + 20);
		g.drawRect(g.getClipBounds().x + 70, g.getClipBounds().y + 10, 100, 10);
		g.fillRect(g.getClipBounds().x + 70, g.getClipBounds().y + 10, (int) _stamina, 10);
		g.setColor(Color.yellow);
		g.drawString("hunger: " + String.valueOf((int) _hunger), g.getClipBounds().x, g.getClipBounds().y + 30);
		g.drawRect(g.getClipBounds().x + 70, g.getClipBounds().y + 20, 100, 10);
		g.fillRect(g.getClipBounds().x + 70, g.getClipBounds().y + 20, (int) _hunger, 10);
	}

	public void setHunger(double hunger)
	{
		_hunger = hunger;
	}

	public boolean isCooldown()
	{
		return _isCooldown;
	}

	@Override
	public void setHitbox()
	{
		super.setHitbox();
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(getAngle()), getX(), getY());
		Area a = new Area(new Rectangle((int) (getX() - getWidth() / 2), (int) (getY() - getHeight() / 2), getWidth(), getHeight() / 4));
		_mouthHitbox = a.createTransformedArea(af);
	}

	@Override
	public synchronized void Paint(Graphics2D g, boolean isDebug)
	{
		// TODO Auto-generated method stub
		super.Paint(g, isDebug);
		if (isDebug)
		{
			g.setColor(Color.magenta);
			g.draw(_mouthHitbox);
		}
	}

	public Area getMouthHitbox()
	{
		return _mouthHitbox;
	}

	public void setMouthHitbox(Area mouthHitbox)
	{
		_mouthHitbox = mouthHitbox;
	}

	public void setCooldown(boolean isCooldown)
	{
		_isCooldown = isCooldown;
	}

	@Override
	public void updateFinalSpeed()
	{
		setFinalSpeed((getBaseSpeed() * _disToSpeedRatio + _speedMouseBoost) * getSpeedSeaweedSlowdown());
	}

	public void applyMouseBoost(boolean mouseDown)
	{
		if (mouseDown && _stamina > 0 && !_isCooldown)
		{
			_speedMouseBoost = 10;
			_stamina -= 2;
		}
		else
		{
			_speedMouseBoost = 0;
			if (_stamina < 100)
			{
				_stamina += _hunger / 100 + 1;
			}
		}
		if (_stamina <= 0)
		{
			_stamina = 0;
			_isCooldown = true;
		}
		if (_stamina >= 100)
		{
			_stamina = 100;
			_isCooldown = false;
		}
		updateFinalSpeed();
	}

	public void updateHealth()
	{
		if (_hunger == 0 && _health > 0)
		{
			_health -= 1 / 60.0;
		}
	}

	public void updateHunger()
	{
		if (_hunger > 0)
		{
			_hunger -= 1 / 60.0;
		}
		_hunger = (_hunger < 0) ? 0 : _hunger;
	}

	public void updateStats()
	{
		updateHunger();
		updateHealth();
	}

	public double getSpeedMouseBoost()
	{
		return _speedMouseBoost;
	}

	public void setSpeedMouseBoost(double speedMouseBoost)
	{
		_speedMouseBoost = speedMouseBoost;
	}

	public double getHealth()
	{
		return _health;
	}

	public void setHealth(double health)
	{
		_health = health;
	}

	public double getStamina()
	{
		return _stamina;
	}

	public void setStamina(double stamina)
	{
		_stamina = stamina;
	}

	public void setDisToSpeedRatio(double disToSpeedRatio)
	{
		_disToSpeedRatio = (disToSpeedRatio > 1) ? 1 : disToSpeedRatio;
	}

	public double getDisToSpeedRatio()
	{
		return _disToSpeedRatio;
	}

	public void eaten(int hungerPoints)
	{
		// TODO Auto-generated method stub
		_hunger += hungerPoints;
		_hunger = (_hunger > 101) ? 101 : _hunger;
	}

}
