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
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.Timer;
import images.Img;
import map.Map;

public class MapPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
	private int _size, _sizeW, _blockSize, _mapPixelWidth, _mapPixelHeight;
	private double _sharkOffsetX, _sharkOffsetY;
	private Map _map;
	private Img _backgroundImg;
	private BlockType _blocksTypes[];
	private Player _shark;
	private String _mapFile, _effectsFile;
	private Point2D.Double _mousePoint, _finalMousePoint, _centerPoint, _camPoint;
	private LinkedList<Integer> _passables;
	private LinkedList<Point2D.Double> _coliList;
	private boolean _mouseDown;

	public MapPanel() {
		_mapFile = "MapFiles//‏‏‏‏‏‏‏‏‏‏‏‏world2s_20180124222818.xml";
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
		_blocksTypes = new BlockType[5];
		_mouseDown = false;
		for (int i = 0; i < _blocksTypes.length; i++) {
			_blocksTypes[i] = new BlockType(_blockSize, i + 1);
		}
		_shark = new Player("images//shark1.png", "images//shark1rev.png", 0, 0, 8 * _blockSize / 10,
				19 * _blockSize / 10, 8);
		_map = new Map(_size, _sizeW, _mapFile, _effectsFile);
		_passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));
		_rects = new LinkedList<Rectangle>();
		_coliList = new LinkedList<Point2D.Double>();
		addMouseMotionListener(this);
		addMouseListener(this);
		Timer t = new Timer(1000 / 60, this);
		t.start();
	}

	public void movementLogic() {
		// double tempa = _shark.getAngle();
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_shark.setAngle(
				Math.toDegrees(-Math.atan2(_finalMousePoint.x - _shark.getX(), _finalMousePoint.y - _shark.getY()))
						+ 180);

		double disToSpeedRatio = (_finalMousePoint.distance(_shark.getX(), _shark.getY()) / (5 * _blockSize));
		disToSpeedRatio = (disToSpeedRatio > 1) ? 1 : disToSpeedRatio;
		_shark.setBaseSpeed(8 * disToSpeedRatio);

		move(_shark.getAngle(), _shark.getFinalSpeed());
		if (!checkCollision()) {
			for (Rectangle r : _rects) {
				while (_shark.getHitbox().intersects(r)) {
					move(Math.toDegrees(-Math.atan2((r.x + r.getWidth() / 2) - (_shark.getX()),
							(r.y + r.getHeight() / 2) - (_shark.getY()))), 1);
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		long start = System.nanoTime(), end;
		if (getWidth() != 0 && getHeight() != 0) {
			checkMouse();
			movementLogic();
			// _shark.updateHunger();
			repaint();
		}
		end = System.nanoTime();
		// System.out.println("counter: " + _counter);
		System.out.println((double) (end - start) / 1000000000);
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
				_camPoint.x += _sharkOffsetX;
				_sharkOffsetX = 0;
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
			}
		}
		_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_shark.setCords((int) (_centerPoint.x + _sharkOffsetX), (int) (_centerPoint.y + _sharkOffsetY));
		_shark.setHitbox();
	}

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_camPoint.x, -_camPoint.y);
		_backgroundImg.drawImg(g);
		drawHMap(g);
		_shark.Paint(g);
		drawDebug(g);
	}

	public void drawMap(Graphics g) {
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _sizeW; j++) {
				if (_map.getMap()[i][j] != 0) {
					_blocksTypes[_map.getMap()[i][j] - 1].paintAt(g, j, i);
				}
				// g.drawString(String.valueOf(i * _sizeW + j), j * _blockSize, i * _blockSize +
				// _blockSize / 2);
			}
		}
	}

	public void drawHMap(Graphics g) {
		for (Entry<Integer, Integer> e : _map.getHmap().entrySet()) {
			_blocksTypes[e.getValue() - 1].paintAt(g, e.getKey() % _sizeW, e.getKey() / _sizeW);
			// g.drawString(String.valueOf(i * _sizeW + j), j * _blockSize, i * _blockSize +
			// _blockSize / 2);
		}
	}

	private LinkedList<Rectangle> _rects;

	public boolean checkCollision() {
		_shark.setCords((int) (_centerPoint.x + _sharkOffsetX), (int) (_centerPoint.y + _sharkOffsetY));
		_shark.setHitbox();
		_rects = new LinkedList<Rectangle>();
		boolean flag = true;
		_coliList = new LinkedList<Point2D.Double>();
		// System.out.println("_finalSharkPoint);
		for (int i = (int) (_shark.getY() / _blockSize) - 2; i <= (_shark.getY() / _blockSize) + 2; i++) {
			for (int j = (int) (_shark.getX() / _blockSize) - 2; j <= (_shark.getX() / _blockSize) + 2; j++) {
				if (i >= 0 && j >= 0 && i < _size && j < _sizeW) {
					if (_map.getHmap().containsKey(j + i * _sizeW))
						if (!_passables.contains(_map.getHmap().get(j + i * _sizeW))) {
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
		g.setColor(Color.red);
		g.drawString("health: " + String.valueOf(_shark.getHealth()), (int) _camPoint.x, (int) _camPoint.y + 10);
		g.drawRect((int) _camPoint.x + 70, (int) _camPoint.y, 100, 10);
		g.fillRect((int) _camPoint.x + 70, (int) _camPoint.y, _shark.getHealth(), 10);
		g.setColor(Color.green);
		g.drawString("stamina: " + String.valueOf(_shark.getStamina()), (int) _camPoint.x, (int) _camPoint.y + 20);
		g.drawRect((int) _camPoint.x + 70, (int) _camPoint.y + 10, 100, 10);
		g.fillRect((int) _camPoint.x + 70, (int) _camPoint.y + 10, _shark.getStamina(), 10);
		g.setColor(Color.yellow);
		g.drawString("hunger: " + String.valueOf(_shark.getHunger()), (int) _camPoint.x, (int) _camPoint.y + 30);
		g.drawRect((int) _camPoint.x + 70, (int) _camPoint.y + 20, 100, 10);
		g.fillRect((int) _camPoint.x + 70, (int) _camPoint.y + 20, _shark.getHunger(), 10);
		/*
		 * g.drawOval((int) _shark.getX() - getHeight() / 2, (int) _shark.getY() -
		 * getHeight() / 2, getHeight(), getHeight());
		 */
	}

	public void checkMouse() {
		_shark.applyMouseBoost(_mouseDown);
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
		if (e.getButton() == MouseEvent.BUTTON1) {
			_mouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			_mouseDown = false;
		}
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
