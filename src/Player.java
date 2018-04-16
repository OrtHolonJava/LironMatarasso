import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Player extends Character
{
	private double _speedMouseBoost, _disToSpeedRatio, _stamina, _hunger, _health;
	private int _rectSize;
	private boolean _isCooldown;
	private Font _font;

	public Player(double x, double y)
	{
		super(x, y, 0);
		_font = new Font("TimesRoman", Font.PLAIN, Block.getSize() / 4);
		_rectSize = Block.getSize() * 5;
		_stamina = 100;
		_hunger = 100;
		_health = 100;
		_disToSpeedRatio = 1;
		_isCooldown = false;
		// Timer t = new Timer(1000, this);
		// t.start();
	}

	public double getHunger()
	{
		return _hunger;
	}

	public void textWithOutline(Graphics g, String text, int x, int y, Color inColor, Color outColor)
	{
		g.setColor(outColor);
		g.drawString(text, x - 1, y);
		g.drawString(text, x + 1, y);
		g.drawString(text, x, y - 1);
		g.drawString(text, x, y + 1);
		g.setColor(inColor);
		g.drawString(text, x, y);
	}

	public void drawBars(Graphics g)
	{
		g.setColor(Color.red);
		g.drawRect(g.getClipBounds().x, g.getClipBounds().y, _rectSize, _font.getSize());
		g.fillRect(g.getClipBounds().x, g.getClipBounds().y, (int) (_health / 100 * _rectSize), _font.getSize());
		g.setColor(Color.green);
		g.drawRect(g.getClipBounds().x, g.getClipBounds().y + _font.getSize(), _rectSize, _font.getSize());
		g.fillRect(g.getClipBounds().x, g.getClipBounds().y + _font.getSize(), (int) (_stamina / 100 * _rectSize), _font.getSize());
		g.setColor(Color.yellow);
		g.drawRect(g.getClipBounds().x, g.getClipBounds().y + 2 * _font.getSize(), _rectSize, _font.getSize());
		g.fillRect(g.getClipBounds().x, g.getClipBounds().y + 2 * _font.getSize(), (int) (_hunger / 100 * _rectSize), _font.getSize());

		g.setFont(_font);
		// g.setColor(Color.white);
		textWithOutline(g, "health: " + String.valueOf((int) _health), g.getClipBounds().x + 1, g.getClipBounds().y + _font.getSize() - 3,
						Color.white, Color.black);
		textWithOutline(g, "stamina: " + String.valueOf((int) _stamina), g.getClipBounds().x + 1,
						g.getClipBounds().y + 2 * _font.getSize() - 3, Color.white, Color.black);
		textWithOutline(g, "hunger: " + String.valueOf((int) _hunger), g.getClipBounds().x + 1,
						g.getClipBounds().y + 3 * _font.getSize() - 3, Color.white, Color.black);

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
		else
		{
			if (_health < 100)
			{
				_health += _hunger / 1000;
			}
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
		_hunger += hungerPoints;
		_hunger = (_hunger > 101) ? 101 : _hunger;
	}

	public void hurt(int hitPoints)
	{
		_health -= hitPoints;
		_health = (_health < 0) ? 0 : _health;
	}

}
