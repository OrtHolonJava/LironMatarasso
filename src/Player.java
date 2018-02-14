
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

import javax.swing.Timer;

import images.Img;

public class Player implements ActionListener
{
	private int _height, _width;
	private double _frameCount;
	private Img _frames[];
	private double _angle, _baseSpeed, _speedMouseBoost, _speedSeaweedSlowdown, _finalSpeed, _stamina, _hunger, _health;
	private Area _hitbox;
	private LinkedList<Point2D.Double> _polyList;
	private Point2D.Double _loc;
	private boolean _isCooldown;

	public Player(int x, int y, int width, int height, double speed)
	{
		_frameCount = 0;
		_loc = new Point2D.Double(x, y);
		_angle = 0;
		_width = width;
		_height = height;
		_baseSpeed = speed;
		_frames = setFrames("images\\SharkFrames\\");
		_polyList = new LinkedList<Point2D.Double>();
		_stamina = 100;
		_hunger = 100;
		_health = 100;
		_isCooldown = false;
		Timer t = new Timer(1000, this);
		t.start();
	}

	public Img[] setFrames(String path)
	{
		Img[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new Img[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				System.out.println(path + child.getName());
				arr[counter++] = new Img(path + child.getName(), 0, 0, _width, _height);
			}
		}
		return arr;
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

	public void setHitbox()
	{
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(_angle), _loc.getX(), _loc.getY());
		Area a = new Area(new Rectangle((int) (_loc.getX() - _width / 2), (int) (_loc.getY() - _height / 2), _width, _height));
		_hitbox = a.createTransformedArea(af);
		_polyList = getPolygonPoints(toPolygon(_hitbox));
	}

	public LinkedList<Point2D.Double> getPolygonPoints(Polygon p)
	{
		LinkedList<Point2D.Double> list = new LinkedList<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++)
		{
			list.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		return list;
	}

	public Polygon toPolygon(Area a)
	{
		PathIterator iterator = a.getPathIterator(null);
		float[] floats = new float[6];
		Polygon polygon = new Polygon();
		while (!iterator.isDone())
		{
			int type = iterator.currentSegment(floats);
			int x = (int) floats[0];
			int y = (int) floats[1];
			if (type != PathIterator.SEG_CLOSE)
			{
				if (polygon.npoints == 0 || (polygon.xpoints[0] != x || polygon.ypoints[0] != y))
				{
					polygon.addPoint(x, y);
				}
			}
			iterator.next();
		}
		return polygon;
	}

	public void applySeaweedSlowdown(boolean touching)
	{
		if (touching)
		{
			_speedSeaweedSlowdown = 0.5;
		}
		else
		{
			_speedSeaweedSlowdown = 1;
		}
		updateFinalSpeed();
	}

	public void updateFinalSpeed()
	{
		_finalSpeed = (_baseSpeed + _speedMouseBoost) * _speedSeaweedSlowdown;
		System.out.println(_finalSpeed);
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

	public void Paint(Graphics g, boolean isDebug)
	{
		_frames[(10 * (int) (_angle / 180)) + (int) (_frameCount) % 10].setImgCords((int) _loc.getX(), (int) _loc.getY());
		_frames[10 * (int) (_angle / 180) + (int) (_frameCount) % 10].setImgSize(_width, _height);
		BufferedImage use = _frames[10 * (int) (_angle / 180)
									+ (int) (_frameCount += 0.5 * (_finalSpeed / ((_baseSpeed != 0) ? _baseSpeed : 1))) % 10].getbImage();
		Graphics2D g2d = (Graphics2D) g.create();
		// use = (_angle < 180) ? _image.getbImage() : _mirrorImage.getbImage();
		Rectangle rect = new Rectangle(	(int) (_loc.getX() - use.getWidth() / 2), (int) (_loc.getY() - use.getHeight() / 2), use.getWidth(),
										use.getHeight());
		// g2d.setColor(Color.orange);
		// g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.rotate(Math.toRadians(_angle), _loc.getX(), _loc.getY());
		if (isDebug)
		{
			g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
		g2d.drawImage(use, (int) _loc.getX() - use.getWidth() / 2, (int) _loc.getY() - use.getHeight() / 2, null);
		g2d.dispose();
	}

	public void setCords(int x, int y)
	{
		_loc.setLocation(x, y);
		// _image.setImgCords(x, y);
		// _mirrorImage.setImgCords(x, y);
	}

	public void setSize(int width, int height)
	{
		_width = width;
		_height = height;
		// _image.setImgSize(width, height);
		// _mirrorImage.setImgSize(width, height);

	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// TODO Auto-generated method stub
		updateHunger();
		updateHealth();
	}

	public Point2D.Double getLoc()
	{
		return _loc;
	}

	public void setLoc(Point2D.Double loc)
	{
		_loc = loc;
	}

	public double getX()
	{
		return _loc.getX();
	}

	public Area getHitbox()
	{
		return _hitbox;
	}

	public void setHitbox(Area hitbox)
	{
		_hitbox = hitbox;
	}

	public LinkedList<Point2D.Double> getPolyList()
	{
		return _polyList;
	}

	public void setPolyList(LinkedList<Point2D.Double> polyList)
	{
		_polyList = polyList;
	}

	public void setX(double x)
	{
		_loc.x = x;
	}

	public double getY()
	{
		return _loc.getY();
	}

	public void setY(double y)
	{
		_loc.y = y;
	}

	public int getWidth()
	{
		return _width;
	}

	public void setWidth(int width)
	{
		_width = width;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setHeight(int height)
	{
		_height = height;
	}

	public double getAngle()
	{
		return _angle;
	}

	public void setAngle(double angle)
	{
		_angle = angle;
	}

	public double getStamina()
	{
		return _stamina;
	}

	public void setStamina(double stamina)
	{
		_stamina = stamina;
	}

	public double getBaseSpeed()
	{
		return _baseSpeed;
	}

	public void setBaseSpeed(double baseSpeed)
	{
		_baseSpeed = baseSpeed;
	}

	public double getSpeedMouseBoost()
	{
		return _speedMouseBoost;
	}

	public void setSpeedMouseBoost(double speedMouseBoost)
	{
		_speedMouseBoost = speedMouseBoost;
	}

	public double getFinalSpeed()
	{
		updateFinalSpeed();
		// _finalSpeed = Math.max(_baseSpeed + _speedMouseBoost, 0);
		return _finalSpeed;
	}

	public void setFinalSpeed(double finalSpeed)
	{
		_finalSpeed = finalSpeed;
	}

	public double getHealth()
	{
		return _health;
	}

	public void setHealth(double health)
	{
		_health = health;
	}

	public double getFrameCount()
	{
		return _frameCount;
	}

	public void setFrameCount(double frameCount)
	{
		_frameCount = frameCount;
	}

	public Img[] getFrames()
	{
		return _frames;
	}

	public void setFrames(Img[] frames)
	{
		_frames = frames;
	}

	public double getSpeedSeaweedSlowdown()
	{
		return _speedSeaweedSlowdown;
	}

	public void setSpeedSeaweedSlowdown(double speedSeaweedSlowdown)
	{
		_speedSeaweedSlowdown = speedSeaweedSlowdown;
	}

}
