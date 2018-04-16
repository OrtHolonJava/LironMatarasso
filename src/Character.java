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

public abstract class Character extends CharacterType
{
	private double _frameCount;
	private double _angle, _finalSpeed, _speedSeaweedSlowdown, _currentFrame;
	private Area _hitbox;
	private LinkedList<Point2D.Double> _polyList, _coliList;
	private Point2D.Double _loc;
	private LinkedList<Rectangle> _rects;
	private Area _mouthHitbox;

	public Character(double x, double y, int type)
	{
		super(type);
		_loc = new Point2D.Double(x, y);
		_angle = 0;
		_currentFrame = 0;
		_frameCount = getFrames().length;
		_speedSeaweedSlowdown = 1;
		_polyList = new LinkedList<Point2D.Double>();
		_coliList = new LinkedList<Point2D.Double>();
		_rects = new LinkedList<Rectangle>();
		setHitbox();
	}

	public int frameNumToIndex(int frameNum)
	{
		if (frameNum < 10)
			return frameNum;
		return frameNum % 10 + ((int) _frameCount / 2);
	}

	public void draw(Graphics2D g, boolean drawDebug)
	{
		BufferedImage use = getFrames()[frameNumToIndex(10 * (int) (_angle / 180)
														+ (int) (_currentFrame += 0.5 * (_finalSpeed
																							/ ((getBaseSpeed() != 0) ? getBaseSpeed() : 1)))
															% (int) (_frameCount / 2))];
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
			// g.drawOval( (int) (getX() - BlockType.getSize() * 5 / 2), (int)
			// (getY() - BlockType.getSize() * 5 / 2), BlockType.getSize() * 5,
			// BlockType.getSize() * 5);
			g.setColor(Color.magenta);
			g.draw(_mouthHitbox);
			g.drawString(String.valueOf(_angle), (int) getX(), (int) getY());
		}
		g2d.drawImage(use, (int) (getX() - use.getWidth() / 2), (int) (getY() - use.getHeight() / 2), null);
		g2d.dispose();
	}

	public void move(double angle, double speed)
	{
		getLoc().x += speed * Math.sin(Math.toRadians(angle));
		getLoc().y -= speed * Math.cos(Math.toRadians(angle));
		updateCharacter(getLoc().x, getLoc().y);
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

	public void updateCharacter(double x, double y)
	{
		setCords(x, y);
		setHitbox();
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
				// System.out.println(path + child.getName());
				arr[counter++] = new Img(path + child.getName(), 0, 0, getWidth(), getHeight());
			}
		}
		return arr;
	}

	public void setHitbox()
	{
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(_angle), getX(), getY());
		Area a = new Area(new Rectangle((int) (getX() - getWidth() / 2), (int) (getY() - getHeight() / 2), getWidth(), getHeight()));
		_hitbox = a.createTransformedArea(af);
		_polyList = getPolygonPoints(toPolygon(_hitbox));
		Area mh = new Area(new Rectangle((int) (getX() - getWidth() / 2), (int) (getY() - getHeight() / 2), getWidth(), getHeight() / 4));
		_mouthHitbox = mh.createTransformedArea(af);
	}

	public static LinkedList<Point2D.Double> getPolygonPoints(Polygon p)
	{
		LinkedList<Point2D.Double> list = new LinkedList<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++)
		{
			list.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		return list;
	}

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

	public static void printPolygon(Polygon p)
	{
		for (int i = 0; i < p.npoints; i++)
		{
			System.out.println("x: " + p.xpoints[i] + " y: " + p.ypoints[i]);
		}
	}

	public void updateFinalSpeed()
	{
		_finalSpeed = getBaseSpeed() * _speedSeaweedSlowdown;
	}

	public void setCords(double x, double y)
	{
		_loc.setLocation(x, y);
	}

	public void setSize(int width, int height)
	{
		setWidth(width);
		setHeight(height);
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

	public LinkedList<Point2D.Double> getColiList()
	{
		return _coliList;
	}

	public void setColiList(LinkedList<Point2D.Double> coliList)
	{
		_coliList = coliList;
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

	public double getCurrentFrame()
	{
		return _currentFrame;
	}

	public void setCurrentFrame(double currentFrame)
	{
		_currentFrame = currentFrame;
	}

	public double getAngle()
	{
		return _angle;
	}

	public double getCorrectedAngle(double angle)
	{
		return (((angle < 0) ? 360 + angle : angle) + 90) % 360;
	}

	public void setAngle(double angle)
	{
		_angle = angle;
	}

	public void setCorrectedAngle(double angle)
	{
		_angle = Math.floor(getCorrectedAngle(angle));
	}

	public double getFrameCount()
	{
		return _frameCount;
	}

	public void setFrameCount(double frameCount)
	{
		_frameCount = frameCount;
	}

	public double getFinalSpeed()
	{
		return _finalSpeed;
	}

	public void setFinalSpeed(double finalSpeed)
	{
		_finalSpeed = finalSpeed;
	}

	public double getSpeedSeaweedSlowdown()
	{
		return _speedSeaweedSlowdown;
	}

	public void setSpeedSeaweedSlowdown(double speedSeaweedSlowdown)
	{
		_speedSeaweedSlowdown = speedSeaweedSlowdown;
	}

	public LinkedList<Rectangle> getRects()
	{
		return _rects;
	}

	public void setRects(LinkedList<Rectangle> rects)
	{
		_rects = rects;
	}

	public Area getMouthHitbox()
	{
		return _mouthHitbox;
	}

	public void setMouthHitbox(Area mouthHitbox)
	{
		_mouthHitbox = mouthHitbox;
	}

}
