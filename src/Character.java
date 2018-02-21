import java.awt.Color;
import java.awt.Graphics;
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

public abstract class Character
{
	private int _height, _width;
	private double _frameCount;
	private Img _frames[];
	private double _angle, _baseSpeed, _finalSpeed, _speedSeaweedSlowdown;
	private Area _hitbox;
	private LinkedList<Point2D.Double> _polyList, _coliList;
	private Point2D.Double _loc;
	private String _framesPath;
	private LinkedList<Rectangle> _rects;

	public Character(double x, double y, int width, int height, double baseSpeed, String framesPath)
	{
		_loc = new Point2D.Double(x, y);
		_frameCount = 0;
		_angle = 0;
		_width = width;
		_height = height;
		_baseSpeed = baseSpeed;
		_framesPath = framesPath;
		_speedSeaweedSlowdown = 1;
		chooseFrames(framesPath);
		_polyList = new LinkedList<Point2D.Double>();
		_coliList = new LinkedList<Point2D.Double>();
		_rects = new LinkedList<Rectangle>();
		setHitbox();
	}

	public void chooseFrames(String framesPath)
	{
		switch (framesPath)
		{
			case "player":
				_frames = MapPanel._imageLoader.getPlayerFrames();
				break;

			default:
				break;
		}
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
				arr[counter++] = new Img(path + child.getName(), 0, 0, _width, _height);
			}
		}
		return arr;
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

	public void Paint(Graphics g, boolean isDebug)
	{
		BufferedImage use = _frames[10 * (int) (_angle / 180)
									+ (int) (_frameCount += 0.5 * (_finalSpeed / ((_baseSpeed != 0) ? _baseSpeed : 1)))
										% (_frames.length / 2)].getbImage();
		use = Img.resize(use, _width, _height);
		Graphics2D g2d = (Graphics2D) g.create();
		// use = (_angle < 180) ? _image.getbImage() : _mirrorImage.getbImage();
		Rectangle rect = new Rectangle(	(int) (_loc.getX() - use.getWidth() / 2), (int) (_loc.getY() - use.getHeight() / 2), use.getWidth(),
										use.getHeight());
		// g2d.setColor(Color.orange);
		// g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.rotate(Math.toRadians(_angle), _loc.getX(), _loc.getY());
		if (isDebug)
		{
			g2d.setColor(Color.black);
			g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
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
		}
		g2d.drawImage(use, (int) _loc.getX() - use.getWidth() / 2, (int) _loc.getY() - use.getHeight() / 2, null);
		g2d.dispose();
	}

	public void updateFinalSpeed()
	{
		_finalSpeed = _baseSpeed * _speedSeaweedSlowdown;
	}

	public void setCords(double x, double y)
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

	public double getBaseSpeed()
	{
		return _baseSpeed;
	}

	public void setBaseSpeed(double baseSpeed)
	{
		_baseSpeed = baseSpeed;
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

	public String getFramesPath()
	{
		return _framesPath;
	}

	public void setFramesPath(String framesPath)
	{
		_framesPath = framesPath;
	}

	public LinkedList<Rectangle> getRects()
	{
		return _rects;
	}

	public void setRects(LinkedList<Rectangle> rects)
	{
		_rects = rects;
	}

}
