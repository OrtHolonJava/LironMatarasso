import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;

/**
 * Character class for all the characters in the game (player and computer
 * controlled)
 * 
 * @author liron
 *
 */
public abstract class Character
{
	protected CharacterType _characterType;
	protected double _frameCount;
	protected double _angle, _finalSpeed, _speedSeaweedSlowdown, _currentFrame;
	protected Area _hitbox;
	protected LinkedList<Point2D.Double> _polyList, _coliList;
	protected Point2D.Double _loc;
	protected LinkedList<Rectangle> _rects;
	protected Area _mouthHitbox;

	/**
	 * Init a new Character object with the following parameters:
	 * 
	 * @param x
	 * @param y
	 * @param type
	 */
	public Character(double x, double y, int type)
	{
		_characterType = new CharacterType(type);
		_loc = new Point2D.Double(x, y);
		_angle = 0;
		_currentFrame = 0;
		_frameCount = _characterType.getFrames().length;
		_speedSeaweedSlowdown = 1;
		_polyList = new LinkedList<Point2D.Double>();
		_coliList = new LinkedList<Point2D.Double>();
		_rects = new LinkedList<Rectangle>();
		setHitbox();
	}

	/**
	 * gets the number of a frame and returns its index in the frames array
	 * 
	 * @param frameNum
	 * @return
	 */
	public int frameNumToIndex(int frameNum)
	{
		if (frameNum < 10)
			return frameNum;
		return frameNum % 10 + ((int) _frameCount / 2);
	}

	/**
	 * draws the character
	 * 
	 * @param g
	 * @param drawDebug
	 */
	public void draw(Graphics2D g, boolean drawDebug)
	{
		int i = frameNumToIndex(10 * (int) (_angle / 180)
				+ (int) (_currentFrame += 0.25 * (_finalSpeed / ((_characterType.getBaseSpeed() != 0) ? _characterType.getBaseSpeed() : 1)))
						% (int) (_frameCount / 2));
		BufferedImage use = _characterType.getFrames()[i];
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.rotate(Math.toRadians(_angle), getX(), getY());
		if (drawDebug)
		{
			g.setColor(Color.black);
			g.draw(_hitbox);
			g.setColor(Color.orange);
			for (Rectangle r : _rects)
			{
				g.fillRect(r.x, r.y, r.width, r.height);
			}
			g.setColor(new Color(128, 0, 128));
			for (Point2D p : _polyList)
			{
				g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
			}
			g.setColor(Color.magenta);
			for (Point2D p : _coliList)
			{
				g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
			}
			g.setColor(Color.magenta);
			g.draw(_mouthHitbox);
			g.drawString(String.valueOf(_angle), (int) getX(), (int) getY());
		}
		g2d.drawImage(use, (int) (getX() - use.getWidth() / 2), (int) (getY() - use.getHeight() / 2), null);
		g2d.dispose();
	}

	/**
	 * moves the character according to the angle and speed
	 * 
	 * @param angle
	 * @param speed
	 */
	public void move(double angle, double speed)
	{
		_loc.x += speed * Math.sin(Math.toRadians(angle));
		_loc.y -= speed * Math.cos(Math.toRadians(angle));
		updateCharacter(_loc.x, _loc.y);
	}

	/**
	 * slows the character if it is touching seaweed
	 * 
	 * @param touching
	 */
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

	/**
	 * updates a character coordinates and its hitbox
	 * 
	 * @param x
	 * @param y
	 */
	public void updateCharacter(double x, double y)
	{
		setCords(x, y);
		setHitbox();
	}

	/**
	 * gets a directory path and returns all the pictures inside the directory
	 * in an array
	 * 
	 * @param path
	 * @return the frames in the path in array
	 */
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
				arr[counter++] = new Img(path + child.getName(), 0, 0, _characterType.getWidth(), _characterType.getHeight());
			}
		}
		return arr;
	}

	/**
	 * setting the hitbox of the character
	 */
	public void setHitbox()
	{
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(_angle), getX(), getY());
		Area a = new Area(new Rectangle((int) (getX() - _characterType.getWidth() / 2), (int) (getY() - _characterType.getHeight() / 2),
										_characterType.getWidth(), _characterType.getHeight()));
		_hitbox = a.createTransformedArea(af);
		_polyList = getPolygonPoints(toPolygon(_hitbox));
		Area mh = new Area(new Rectangle(	(int) (getX() - _characterType.getWidth() / 2), (int) (getY() - _characterType.getHeight() / 2),
											_characterType.getWidth(), _characterType.getHeight() / 4));
		_mouthHitbox = mh.createTransformedArea(af);
	}

	/**
	 * gets a polygon and returns the points that define the polygon
	 * 
	 * @param p
	 * @return a linked list of the points in the edges of the polygon
	 */
	public static LinkedList<Point2D.Double> getPolygonPoints(Polygon p)
	{
		LinkedList<Point2D.Double> list = new LinkedList<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++)
		{
			list.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		return list;
	}

	/**
	 * gets an area and converts it into a polygon
	 * 
	 * @param a
	 * @return a polygon that covers the area
	 */
	public static Polygon toPolygon(Area a)
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

	/**
	 * prints the polygon points
	 * 
	 * @param p
	 */
	public static void printPolygon(Polygon p)
	{
		for (int i = 0; i < p.npoints; i++)
		{
			System.out.println("x: " + p.xpoints[i] + " y: " + p.ypoints[i]);
		}
	}

	/**
	 * updates the final speed of the character
	 */
	public void updateFinalSpeed()
	{
		_finalSpeed = _characterType.getBaseSpeed() * _speedSeaweedSlowdown;
	}

	/**
	 * sets the coordinates of the character
	 * 
	 * @param x
	 * @param y
	 */
	public void setCords(double x, double y)
	{
		_loc.setLocation(x, y);
	}

	/**
	 * sets the size of the character
	 * 
	 * @param width
	 * @param height
	 */
	public void setSize(int width, int height)
	{
		_characterType.setWidth(width);
		_characterType.setHeight(height);
	}

	/**
	 * gets an angle and returns an angle that is accuratly represented in the
	 * java swing panel
	 * 
	 * @param angle
	 * @return
	 */
	public double getCorrectedAngle(double angle)
	{
		return Math.floor((((angle < 0) ? 360 + angle : angle) + 90) % 360);
	}

	/**
	 * sets the angle of the character to the given angle after correction
	 * 
	 * @param angle
	 */
	public void setCorrectedAngle(double angle)
	{
		_angle = getCorrectedAngle(angle);
	}

	public double getX()
	{
		return _loc.getX();
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
}
