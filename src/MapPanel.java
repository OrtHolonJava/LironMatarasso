import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.Timer;
import images.Img;
import map.Map;

public class MapPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
	private int _size, _sizeW, _blockSize, _mapPixelWidth, _mapPixelHeight;
	private double _sharkOffsetX, _sharkOffsetY, _speed, _angle;
	private Map _map;
	private Img _backgroundImg, _sandBlock, _stoneBlock, _seaweedBlock, _sandBackground, _stoneBackground, _shark,
			_sharkRev;
	private String _mapFile, _effectsFile;
	private BufferedImage _bImgShark, _bImgSharkRev;
	private Point2D.Double _mousePoint, _finalSharkPoint, _finalMousePoint, _centerPoint, _camPoint;
	private LinkedList<Integer> _passables;
	private Area _sharkhb;

	public MapPanel() {
		_mapFile = "MapFiles//pic2_20171206105928.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_size = Map.getElementCountByName(_mapFile, "Line");
		_sizeW = Map.getElementCountByName(_mapFile, "Area") / _size;
		_blockSize = 40;
		_mapPixelWidth = _sizeW * _blockSize;
		_mapPixelHeight = _size * _blockSize;
		_speed = 1;
		_angle = 0;
		_mousePoint = new Point2D.Double(0, 0);
		_centerPoint = new Point2D.Double(0, 0);
		_camPoint = new Point2D.Double(_blockSize * 3, 0);
		_sharkOffsetX = 0;
		_sharkOffsetY = -0.5*_blockSize;
		_finalMousePoint = new Point2D.Double(0, 0);
		_finalSharkPoint = new Point2D.Double(0, 0);
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _sizeW * _blockSize, _size * _blockSize);
		_sandBlock = new Img("images//SandBlock2.png", 0, 0, _blockSize, _blockSize);
		_stoneBlock = new Img("images//‏‏StoneBlock2.png", 0, 0, _blockSize, _blockSize);
		_seaweedBlock = new Img("images//OneSW.png", 0, 0, _blockSize, _blockSize);
		_sandBackground = new Img("images//SandBackground.png", 0, 0, _blockSize, _blockSize);
		_stoneBackground = new Img("images//StoneBackground.png", 0, 0, _blockSize, _blockSize);
		_shark = new Img("images//shark1.png", 0, 0, _blockSize / 2, _blockSize);
		_sharkRev = new Img("images//shark1rev.png", 0, 0, _blockSize / 2, _blockSize);
		_map = new Map(_size, _sizeW, _mapFile, _effectsFile);
		_passables = new LinkedList<Integer>(Arrays.asList(0, 4, 5));
		addMouseMotionListener(this);
		addMouseListener(this);
		Timer t = new Timer(10, this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (test())
			move();
		_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_finalSharkPoint.setLocation(_centerPoint.x + _sharkOffsetX, _centerPoint.y + _sharkOffsetY);
		_angle = Math.toDegrees(
				-Math.atan2(_finalMousePoint.x - _finalSharkPoint.x, _finalMousePoint.y - _finalSharkPoint.y)) + 180;
		repaint();
	}

	public void move() {
		if (_sharkOffsetY == 0) {
			_camPoint.y -= _speed * Math.cos(Math.toRadians(_angle));
			if (_camPoint.y < 0 || _camPoint.y > _mapPixelHeight - getHeight()) {
				_camPoint.y = (_camPoint.y < 0) ? 0 : _mapPixelHeight - getHeight();
				_sharkOffsetY -= _speed * Math.cos(Math.toRadians(_angle));
			}
		} else {
			int preSignY = (int) Math.signum(_sharkOffsetY);
			_sharkOffsetY -= _speed * Math.cos(Math.toRadians(_angle));
			if (preSignY != (int) Math.signum(_sharkOffsetY)) {
				_camPoint.y -= _sharkOffsetY;
				_sharkOffsetY = 0;
			}
		}
		if (_sharkOffsetX == 0) {
			_camPoint.x += _speed * Math.sin(Math.toRadians(_angle));
			if (_camPoint.x < 0 || _camPoint.x > _mapPixelWidth - getWidth()) {
				_camPoint.x = (_camPoint.x < 0) ? 0 : _mapPixelWidth - getWidth();
				_sharkOffsetX += _speed * Math.sin(Math.toRadians(_angle));
			}
		} else {
			int preSignX = (int) Math.signum(_sharkOffsetX);
			_sharkOffsetX += _speed * Math.sin(Math.toRadians(_angle));
			if (preSignX != (int) Math.signum(_sharkOffsetX)) {
				_camPoint.x -= _sharkOffsetX;
				_sharkOffsetX = 0;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_camPoint.x, -_camPoint.y);
		drawMap(g);
		rotateShark2(g);
		drawDebug(g);
	}

	public void drawMap(Graphics g) {
		_backgroundImg.drawImg(g);
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _sizeW; j++) {
				switch (_map.getMap()[i][j]) {
				case 1: {
					_sandBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_sandBlock.drawImg(g);
					break;
				}
				case 2: {
					_stoneBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_stoneBlock.drawImg(g);
					break;
				}
				case 3: {
					_seaweedBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_seaweedBlock.drawImg(g);
					break;
				}
				case 4: {
					_sandBackground.setImgCords((j * _blockSize), (i) * _blockSize);
					_sandBackground.drawImg(g);
					break;
				}
				case 5: {
					_stoneBackground.setImgCords((j * _blockSize), (i) * _blockSize);
					_stoneBackground.drawImg(g);
					break;
				}
				}
			}
		}
	}

	public boolean test() {
		// System.out.println(at.getBounds().toString());
		// g.setColor(Color.magenta);
		// g.drawRect(nr.x, nr.y, nr.width, nr.height);
		setSharkHitBox();
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _sizeW; j++) {
				if (!_passables.contains(_map.getMap()[i][j])) {
					Rectangle rect = new Rectangle(j * _blockSize, i * _blockSize, _blockSize, _blockSize);
					if (_sharkhb.intersects(rect)) {
						Polygon poly = toPolygon(_sharkhb);
						LinkedList<Point2D.Double> list = getPolygonPoints(poly);
						for (Point2D p : list)
							if (rect.contains(p)) {
								// System.out.println(p + " point at " + rect.toString());
								return false;
							}
					}
				}
			}
		}
		return true;
	}

	public LinkedList<Point2D.Double> getPolygonPoints(Polygon p) {
		LinkedList<Point2D.Double> list = new LinkedList<Point2D.Double>();
		for (int i = 0; i < p.npoints; i++) {
			list.add(new Point2D.Double(p.xpoints[i], p.ypoints[i]));
		}
		return list;
	}

	public void setSharkHitBox() {
		AffineTransform af = new AffineTransform();
		af.rotate(Math.toRadians(_angle), _finalSharkPoint.x, _finalSharkPoint.y);
		Area a = new Area(new Rectangle((int) (_finalSharkPoint.x - _shark.getWidth() / 2),
				(int) (_finalSharkPoint.y - _shark.getHeight() / 2), _shark.getWidth(), _shark.getHeight()));
		_sharkhb = a.createTransformedArea(af);
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

	public void printPolygon(Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			System.out.println("x: " + p.xpoints[i] + " y: " + p.ypoints[i]);
		}
	}

	public void rotateShark2(Graphics g) {
		_bImgShark = Img.toBufferedImage(_shark.getImage());
		_bImgShark = Img.resize(_bImgShark, _shark.getWidth(), _shark.getHeight());
		_bImgSharkRev = Img.toBufferedImage(_sharkRev.getImage());
		_bImgSharkRev = Img.resize(_bImgSharkRev, _shark.getWidth(), _shark.getHeight());

		BufferedImage use = _bImgShark;
		Graphics2D g2d = (Graphics2D) g.create();
		use = (_angle < 180) ? _bImgShark : _bImgSharkRev;
		Rectangle rect = new Rectangle((int) (_finalSharkPoint.x - use.getWidth() / 2),
				(int) (_finalSharkPoint.y - use.getHeight() / 2), use.getWidth(), use.getHeight());
		g2d.setColor(Color.orange);
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.rotate(Math.toRadians(_angle), _finalSharkPoint.x, _finalSharkPoint.y);
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.drawImage(use, (int) _finalSharkPoint.x - use.getWidth() / 2,
				(int) _finalSharkPoint.y - use.getHeight() / 2, this);
		g2d.dispose();
	}

	public void drawDebug(Graphics g) {
		g.setColor(Color.red);
		g.drawRect((int) _finalMousePoint.x, (int) _finalMousePoint.y, 100, 100);
		g.setColor(Color.yellow);
		g.drawRect((int) _centerPoint.x, (int) _centerPoint.y, 100, 100);
		g.setColor(Color.green);
		g.drawRect((int) _camPoint.x, (int) _camPoint.y, 100, 100);
		g.setColor(Color.cyan);
		g.drawRect((int) _finalSharkPoint.x, (int) _finalSharkPoint.y, 100, 100);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			_speed += 5;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			_speed -= 5;
	}

	public void printMat(int[][] mat, int size, int sizeW) {
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < sizeW; j++) {
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}
}
