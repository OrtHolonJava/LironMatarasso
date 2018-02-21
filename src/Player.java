import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Player extends Character implements ActionListener
{
	private double _speedMouseBoost, _stamina, _hunger, _health;
	private boolean _isCooldown;

	public Player(int x, int y, int width, int height, double baseSpeed)
	{
		super(x, y, width, height, baseSpeed, "player");
		_stamina = 100;
		_hunger = 100;
		_health = 100;
		_isCooldown = false;
		Timer t = new Timer(1000, this);
		t.start();
	}

	public double getHunger()
	{
		return _hunger;
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

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
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
