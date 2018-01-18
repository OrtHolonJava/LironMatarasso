
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import images.Img;

public class Player {
	private int _height, _width;
	private Img _image, _mirrorImage;
	private double _angle, _speed;
	private Area _hitbox;
	private LinkedList<Point2D.Double> _polyList;
	private Point2D.Double _loc;

	public Player(String path, String mirrorPath, int x, int y, int width, int height, double speed) {
		_loc = new Point2D.Double(x, y);
		_angle = 0;
		_width = width;
		_height = height;
		_speed = speed;
		_image = new Img(path, x, y, width, height);
		_mirrorImage = new Img(mirrorPath, x, y, width, height);
		_polyList = new LinkedList<Point2D.Double>();

	}

	public void setHitbox() {
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(_angle), _loc.getX(), _loc.getY());
		Area a = new Area(
				new Rectangle((int) (_loc.getX() - _width / 2), (int) (_loc.getY() - _height / 2), _width, _height));
		_hitbox = a.createTransformedArea(af);
		_polyList = getPolygonPoints(toPolygon(_hitbox));
	}

	public LinkedList<Point2D.Double> getPolygonPoints(Polygon p) {
		LinkedList<Point2D.Double> list = new LinkedList<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++) {
			list.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		return list;
	}

	public Polygon toPolygon(Area a) {
		PathIterator iterator = a.getPathIterator(null);
		float[] floats = new float[6];
		Polygon polygon = new Polygon();
		while (!iterator.isDone()) {
			int type = iterator.currentSegment(floats);
			int x = (int) floats[0];
			int y = (int) floats[1];
			if (type != PathIterator.SEG_CLOSE) {
				if (polygon.npoints == 0 || (polygon.xpoints[0] != x || polygon.ypoints[0] != y)) {
					polygon.addPoint(x, y);
				}
			}
			iterator.next();
		}
		return polygon;
	}

	public void Paint(Graphics g) {

		BufferedImage use = _image.getbImage();
		Graphics2D g2d = (Graphics2D) g.create();
		use = (_angle < 180) ? _image.getbImage() : _mirrorImage.getbImage();
		Rectangle rect = new Rectangle((int) (_loc.getX() - use.getWidth() / 2),
				(int) (_loc.getY() - use.getHeight() / 2), use.getWidth(), use.getHeight());
		// g2d.setColor(Color.orange);
		// g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.rotate(Math.toRadians(_angle), _loc.getX(), _loc.getY());
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.drawImage(use, (int) _loc.getX() - use.getWidth() / 2, (int) _loc.getY() - use.getHeight() / 2, null);
		g2d.dispose();
	}

	public void setCords(int x, int y) {
		_loc.setLocation(x, y);
		_image.setImgCords(x, y);
		_mirrorImage.setImgCords(x, y);
	}

	public void setSize(int width, int height) {
		_width = width;
		_height = height;
		_image.setImgSize(width, height);
		_mirrorImage.setImgSize(width, height);

	}

	public Point2D.Double getLoc() {
		return _loc;
	}

	public void setLoc(Point2D.Double loc) {
		_loc = loc;
	}

	public double getX() {
		return _loc.getX();
	}

	public Area getHitbox() {
		return _hitbox;
	}

	public void setHitbox(Area hitbox) {
		_hitbox = hitbox;
	}

	public LinkedList<Point2D.Double> getPolyList() {
		return _polyList;
	}

	public void setPolyList(LinkedList<Point2D.Double> polyList) {
		_polyList = polyList;
	}

	public void setX(double x) {
		_loc.x = x;
	}

	public double getY() {
		return _loc.getY();
	}

	public void setY(double y) {
		_loc.y = y;
	}

	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		_width = width;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		_height = height;
	}

	public Img getImage() {
		return _image;
	}

	public void setImage(Img image) {
		_image = image;
	}

	public Img getMirrorImage() {
		return _mirrorImage;
	}

	public void setMirrorImage(Img mirrorImage) {
		_mirrorImage = mirrorImage;
	}

	public double getAngle() {
		return _angle;
	}

	public void setAngle(double angle) {
		_angle = angle;
	}

	public double getSpeed() {
		return _speed;
	}

	public void setSpeed(double speed) {
		_speed = speed;
	}

}
