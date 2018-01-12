import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.JPanel;
import javax.swing.Timer;
import images.Img;
import map.Map;

public class MapPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
	private int _size, _sizeW, _blockSize, _mapPixelWidth, _mapPixelHeight;
	private double _sharkOffsetX, _sharkOffsetY;
	private Map _map;
	private Img _backgroundImg, _sandBlock, _stoneBlock, _seaweedBlock, _sandBackground, _stoneBackground;
	private Player _shark;
	private String _mapFile, _effectsFile;
	private Point2D.Double _mousePoint, _finalMousePoint, _centerPoint, _camPoint;
	private LinkedList<Integer> _passables;
	private LinkedList<Point2D.Double> _coliList;

	public MapPanel() {
		_mapFile = "MapFiles//‏‏world2b_20180112180443.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_size = Map.getElementCountByName(_mapFile, "Line");
		_sizeW = Map.getElementCountByName(_mapFile, "Area") / _size;
		_blockSize = 40;
		_mapPixelWidth = _sizeW * _blockSize;
		_mapPixelHeight = _size * _blockSize;
		_mousePoint = new Point2D.Double(0, 0);
		_centerPoint = new Point2D.Double(0, 0);
		_camPoint = new Point2D.Double(_blockSize * 3, _blockSize);
		_sharkOffsetX = 0;
		_sharkOffsetY = -10 * _blockSize;
		_finalMousePoint = new Point2D.Double(0, 0);
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _sizeW * _blockSize, _size * _blockSize);
		_sandBlock = new Img("images//SandBlock2.png", 0, 0, _blockSize, _blockSize);
		_stoneBlock = new Img("images//‏‏StoneBlock2.png", 0, 0, _blockSize, _blockSize);
		_seaweedBlock = new Img("images//OneSW.png", 0, 0, _blockSize, _blockSize);
		_sandBackground = new Img("images//SandBackground.png", 0, 0, _blockSize, _blockSize);
		_stoneBackground = new Img("images//StoneBackground.png", 0, 0, _blockSize, _blockSize);
		_shark = new Player("images//shark1.png", "images//shark1rev.png", 0, 0, _blockSize / 2, _blockSize, 4);
		_map = new Map(_size, _sizeW, _mapFile, _effectsFile);
		_passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));
		_rects = new LinkedList<Rectangle>();
		_coliList = new LinkedList<Point2D.Double>();
		addMouseMotionListener(this);
		addMouseListener(this);
		Timer t = new Timer(10, this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (getWidth() != 0 && getHeight() != 0) {
			double tempa = _shark.getAngle();
			_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
			_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
			_shark.setAngle(
					Math.toDegrees(-Math.atan2(_finalMousePoint.x - _shark.getX(), _finalMousePoint.y - _shark.getY()))
							+ 180);
			move(_shark.getAngle(), _shark.getSpeed());
			int count = 0;
			while (!test()) {
				// for (Point2D p : _coliList) {
				for (Rectangle r : _rects) {
					while (_shark.getHitbox().intersects(r)) {
						move(Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (_shark.getX()),
								(r.y + r.getHeight() / 2) - (_shark.getY()))), 1);
					}
				}
				count++;
				if (count == 10) {
					_shark.setAngle(tempa);
					count = 0;
				}
				// }
			}
			repaint();
		}
	}

	public void move(double angle, double speed) {
		if (_sharkOffsetX == 0) {
			_camPoint.x += speed * Math.sin(Math.toRadians(angle));
			if (_camPoint.x < 0 || _camPoint.x > _mapPixelWidth - getWidth()) {
				_camPoint.x = (_camPoint.x < 0) ? 0 : _mapPixelWidth - getWidth();
				_sharkOffsetX += speed * Math.sin(Math.toRadians(angle));
			}
		} else {
			int preSignX = (int) Math.signum(_sharkOffsetX);
			_sharkOffsetX += speed * Math.sin(Math.toRadians(angle));
			if (preSignX != (int) Math.signum(_sharkOffsetX)) {
				System.out.println("cammmmm: " + _camPoint);
				_camPoint.x += _sharkOffsetX;
				_sharkOffsetX = 0;
				System.out.println("restarted x");
				System.out.println("post cammmmm: " + _camPoint);

			}
		}

		if (_sharkOffsetY == 0) {
			_camPoint.y -= speed * Math.cos(Math.toRadians(angle));
			if (_camPoint.y < _blockSize || _camPoint.y > _mapPixelHeight - getHeight()) {
				_camPoint.y = (_camPoint.y < _blockSize) ? _blockSize : _mapPixelHeight - getHeight();
				_sharkOffsetY -= speed * Math.cos(Math.toRadians(angle));
			}
		} else {
			int preSignY = (int) Math.signum(_sharkOffsetY);
			_sharkOffsetY -= speed * Math.cos(Math.toRadians(angle));
			if (preSignY != (int) Math.signum(_sharkOffsetY)) {
				_camPoint.y += _sharkOffsetY;
				_sharkOffsetY = 0;
				System.out.println("restarted y");
			}
		}
		// System.out.println("offx: " + _sharkOffsetX + " offy: " + _sharkOffsetY);
		_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
		_shark.setCords((int) (_centerPoint.x + _sharkOffsetX), (int) (_centerPoint.y + _sharkOffsetY));
		_shark.setHitbox();
	}

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_camPoint.x, -_camPoint.y);
		drawMap(g);
		_shark.Paint(g);
		drawDebug(g);
	}

	public void drawMap(Graphics g) {
		_backgroundImg.drawImg(g);
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _sizeW; j++) {
				switch (_map.getMap()[i][j]) {
				case 1: {
					_sandBlock.setImgCords(j * _blockSize, i * _blockSize);
					_sandBlock.drawImg(g);
					break;
				}
				case 2: {
					_stoneBlock.setImgCords(j * _blockSize, i * _blockSize);
					_stoneBlock.drawImg(g);
					break;
				}
				case 3: {
					_seaweedBlock.setImgCords(j * _blockSize, i * _blockSize);
					_seaweedBlock.drawImg(g);
					break;
				}
				case 4: {
					_sandBackground.setImgCords(j * _blockSize, i * _blockSize);
					_sandBackground.drawImg(g);
					break;
				}
				case 5: {
					_stoneBackground.setImgCords(j * _blockSize, i * _blockSize);
					_stoneBackground.drawImg(g);
					break;
				}
				}
				// g.drawString(String.valueOf(i * _sizeW + j), j * _blockSize, i * _blockSize +
				// _blockSize / 2);
			}
		}
	}

	private LinkedList<Rectangle> _rects;

	public boolean test() {
		_shark.setCords((int) (_centerPoint.x + _sharkOffsetX), (int) (_centerPoint.y + _sharkOffsetY));
		_shark.setHitbox();
		_rects = new LinkedList<Rectangle>();
		boolean flag = true;
		_coliList = new LinkedList<Point2D.Double>();
		// System.out.println("_finalSharkPoint);
		for (int i = (int) (_shark.getY() / _blockSize) - 2; i <= (_shark.getY() / _blockSize) + 2; i++) {
			for (int j = (int) (_shark.getX() / _blockSize) - 2; j <= (_shark.getX() / _blockSize) + 2; j++) {
				if (i >= 0 && j >= 0 && i < _size && j < _sizeW) {
					if (!_passables.contains(_map.getMap()[i][j])) {
						Rectangle rect = new Rectangle(j * _blockSize, i * _blockSize, _blockSize, _blockSize);
						if (_shark.getHitbox().intersects(rect)) {
							_rects.add(rect);
							flag = false;
							for (Point2D p : _shark.getPolyList())
								if (rect.contains(p)) {
									// System.out.println(p + " point at " + rect.toString());
									_coliList.add(new Point2D.Double(p.getX(), p.getY()));
								}
						}
					}
				}
			}
		}
		return flag;
	}

	public void printPolygon(Polygon p) {
		for (int i = 0; i < p.npoints; i++) {
			System.out.println("x: " + p.xpoints[i] + " y: " + p.ypoints[i]);
		}
	}

	public void drawDebug(Graphics g) {
		g.setColor(Color.red);
		g.drawRect((int) _finalMousePoint.x, (int) _finalMousePoint.y, 100, 100);
		g.setColor(Color.yellow);
		g.drawRect((int) _centerPoint.x, (int) _centerPoint.y, 100, 100);
		g.setColor(Color.green);
		g.drawRect((int) _camPoint.x, (int) _camPoint.y, 100, 100);
		g.setColor(Color.cyan);
		g.drawRect((int) _shark.getX(), (int) _shark.getY(), 100, 100);
		g.setColor(Color.orange);
		for (Rectangle r : _rects) {
			g.fillRect(r.x, r.y, r.width, r.height);
		}
		g.setColor(new Color(128, 0, 128));
		for (Point2D p : _shark.getPolyList()) {
			g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
		}
		g.setColor(Color.magenta);
		for (Point2D p : _coliList) {
			g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
		}
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
			_shark.setSpeed(_shark.getSpeed() + 5);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1)
			_shark.setSpeed(_shark.getSpeed() - 5);
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
