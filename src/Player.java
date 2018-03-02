import java.awt.Color;
import java.awt.Graphics;

public class Player extends Character
{
	private double _speedMouseBoost, _stamina, _hunger, _health;
	private boolean _isCooldown;

	public Player(double x, double y, int width, int height, double baseSpeed)
	{
		super(x, y, width, height, baseSpeed, "player");
		_stamina = 100;
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
		g.drawString("health: " + String.valueOf((int) _health), (int)  g.getClipBounds().x, (int)  g.getClipBounds().y + 10);
		g.drawRect((int) g.getClipBounds().x + 70, (int)  g.getClipBounds().y, 100, 10);
		g.fillRect((int)  g.getClipBounds().x + 70, (int)  g.getClipBounds().y, (int) _health, 10);
		g.setColor(Color.green);
		g.drawString("stamina: " + String.valueOf((int) _stamina), (int)  g.getClipBounds().x, (int)  g.getClipBounds().y + 20);
		g.drawRect((int)  g.getClipBounds().x + 70, (int)  g.getClipBounds().y + 10, 100, 10);
		g.fillRect((int)  g.getClipBounds().x + 70, (int)  g.getClipBounds().y + 10, (int) _stamina, 10);
		g.setColor(Color.yellow);
		g.drawString("hunger: " + String.valueOf((int) _hunger), (int)  g.getClipBounds().x, (int)  g.getClipBounds().y + 30);
		g.drawRect((int)  g.getClipBounds().x + 70, (int)  g.getClipBounds().y + 20, 100, 10);
		g.fillRect((int)  g.getClipBounds().x + 70, (int)  g.getClipBounds().y + 20, (int) _hunger, 10);
	}

	public void setHunger(double hunger)
	{
		_hunger = hunger;
	}

	public boolean isCooldown()
	{
		return _isCooldown;
	}

	public void setCooldown(boolean isCooldown)
	{
		_isCooldown = isCooldown;
	}

	public void updateFinalSpeed()
	{
		setFinalSpeed((getBaseSpeed() + _speedMouseBoost) * getSpeedSeaweedSlowdown());
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
			_health--;
		}
	}

	public void updateHunger()
	{
		if (_hunger > 0)
		{
			_hunger--;
		}
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

}
