import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * Player class for the character that the player controls
 * 
 * @author liron
 *
 */
public class Player extends Character
{
	private double _speedMouseBoost, _disToSpeedRatio, _stamina, _hunger, _health;
	private int _rectSize, _amountEaten;
	private boolean _isCooldown;
	private Font _font;

	/**
	 * Init a new Player object with the following parameters:
	 * 
	 * @param x
	 * @param y
	 */
	public Player(double x, double y)
	{
		super(x, y, 0);
		_font = new Font("TimesRoman", Font.PLAIN, Block.getSize() / 4);
		_rectSize = Block.getSize() * 3;
		_stamina = 100;
		_hunger = 100;
		_health = 100;
		_disToSpeedRatio = 1;
		_amountEaten = 0;
		_isCooldown = false;
	}

	/**
	 * draws a text with an outline
	 * 
	 * @param g
	 * @param text
	 * @param x
	 * @param y
	 * @param inColor
	 * @param outColor
	 */
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

	/**
	 * draws the bars of the player
	 * 
	 * @param g
	 */
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
		textWithOutline(g, "health: " + String.valueOf((int) _health), g.getClipBounds().x + 1, g.getClipBounds().y + _font.getSize() - 3,
						Color.white, Color.black);
		textWithOutline(g, "stamina: " + String.valueOf((int) _stamina), g.getClipBounds().x + 1,
						g.getClipBounds().y + 2 * _font.getSize() - 3, Color.white, Color.black);
		textWithOutline(g, "hunger: " + String.valueOf((int) _hunger), g.getClipBounds().x + 1,
						g.getClipBounds().y + 3 * _font.getSize() - 3, Color.white, Color.black);
	}

	/**
	 * updates the final speed of the player
	 */
	@Override
	public void updateFinalSpeed()
	{
		_finalSpeed = (_characterType.getBaseSpeed() * _disToSpeedRatio + _speedMouseBoost) * _speedSeaweedSlowdown;
	}

	/**
	 * changes the speed of the player according to the state of the left mouse
	 * button
	 * 
	 * @param mouseDown
	 */
	public void applyMouseBoost(boolean mouseDown)
	{
		if (mouseDown && _stamina > 0 && !_isCooldown)
		{
			_speedMouseBoost = Block.getSize() / 8;
			_stamina -= 2;
		}
		else
		{
			_speedMouseBoost = 0;
			if (_stamina < 100)
			{
				_stamina += _hunger / 100;
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

	/**
	 * changes the health of the player
	 */
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

	/**
	 * changes the hunger of the player
	 */
	public void updateHunger()
	{
		if (_hunger > 0)
		{
			_hunger -= 1 / 60.0;
		}
		_hunger = Math.max(_hunger, 0);
	}

	/**
	 * updates both health and hunger
	 */
	public void updateStats()
	{
		updateHunger();
		updateHealth();
	}

	/**
	 * changes the speed of the player according to the distance of the mouse
	 * from the player
	 * 
	 * @param disToSpeedRatio
	 */
	public void setDisToSpeedRatio(double disToSpeedRatio)
	{
		_disToSpeedRatio = Math.min(1, disToSpeedRatio);
	}

	/**
	 * changes the hunger of the player if he ate a fish
	 * 
	 * @param hungerPoints
	 */
	public void eaten(int hungerPoints)
	{
		_amountEaten++;
		_hunger += hungerPoints;
		_hunger = Math.min(_hunger, 101);
	}

	/**
	 * changes the health of the player if he was hit by an enemy
	 * 
	 * @param hitPoints
	 */
	public void hurt(int hitPoints)
	{
		_health -= hitPoints;
		_health = Math.max(_health, 0);
	}

	public double getSpeedMouseBoost()
	{
		return _speedMouseBoost;
	}

	public void setSpeedMouseBoost(double speedMouseBoost)
	{
		_speedMouseBoost = speedMouseBoost;
	}

	public double getStamina()
	{
		return _stamina;
	}

	public void setStamina(double stamina)
	{
		_stamina = stamina;
	}

	public double getHunger()
	{
		return _hunger;
	}

	public void setHunger(double hunger)
	{
		_hunger = hunger;
	}

	public double getHealth()
	{
		return _health;
	}

	public void setHealth(double health)
	{
		_health = health;
	}

	public int getRectSize()
	{
		return _rectSize;
	}

	public void setRectSize(int rectSize)
	{
		_rectSize = rectSize;
	}

	public int getAmountEaten()
	{
		return _amountEaten;
	}

	public void setAmountEaten(int amountEaten)
	{
		_amountEaten = amountEaten;
	}

	public boolean isCooldown()
	{
		return _isCooldown;
	}

	public void setCooldown(boolean isCooldown)
	{
		_isCooldown = isCooldown;
	}

	public Font getFont()
	{
		return _font;
	}

	public void setFont(Font font)
	{
		_font = font;
	}

	public double getDisToSpeedRatio()
	{
		return _disToSpeedRatio;
	}
}
