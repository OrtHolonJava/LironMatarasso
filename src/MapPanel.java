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
import java.util.HashMap;
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
		setKek();
		_mapFile = "MapFiles//‏‏world2s_20180125170030.xml";
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
		_blocksTypes = new BlockType[48 + 5];
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
		Timer t = new Timer(1000 / 120, this);
		t.start();
	}

	public void movementLogic() {
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		if (_finalMousePoint.distance(_shark.getLoc()) > 2)
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
			checkCollision();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// long start = System.nanoTime(), end;
		if (getWidth() != 0 && getHeight() != 0) {
			checkMouse();
			movementLogic();
			repaint();
		}
		// end = System.nanoTime();
		// System.out.println((double) (end - start) / 1000000000);
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
			if (_camPoint.y < _blockSize || _camPoint.y > _mapPixelHeight - getHeight() - _blockSize) {
				_camPoint.y = (_camPoint.y < _blockSize) ? _blockSize : _mapPixelHeight - getHeight() - _blockSize;
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
		_camPoint.y = Math.round(_camPoint.y);
		_camPoint.x = Math.round(_camPoint.x);
		_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
		_finalMousePoint.setLocation(_camPoint.x + _mousePoint.x, _camPoint.y + _mousePoint.y);
		_shark.setCords((int) (_centerPoint.x + _sharkOffsetX), (int) (_centerPoint.y + _sharkOffsetY));
		_shark.setHitbox();
	}

	boolean drawDebug = true;

	@Override
	protected void paintComponent(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_camPoint.x, -_camPoint.y);
		_backgroundImg.drawImg(g);
		drawHMap(g);
		_shark.Paint(g, drawDebug);
		drawBars(g);
		if (drawDebug)
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

	public boolean InScreen(int row, int col) {
		System.out.println();
		return (col <= 1 + ((getWidth() + _camPoint.getX()) / _blockSize) && col + 1 >= _camPoint.getX() / _blockSize
				&& row <= 1 + ((getHeight() + _camPoint.getY()) / _blockSize)
				&& row + 1 >= _camPoint.getY() / _blockSize);
	}

	public void drawHMap(Graphics g) {
		for (Entry<Integer, Integer> e : _map.getHmap().entrySet()) {
			if (InScreen(e.getKey() / _sizeW, e.getKey() % _sizeW)) {
				_blocksTypes[e.getValue() - 1
						+ ((e.getValue() == 1) ? 5 + computeTile(e.getKey() / _sizeW, e.getKey() % _sizeW, e.getValue())
								: 0)].paintAt(g, e.getKey() % _sizeW, e.getKey() / _sizeW);
			}
			// g.drawString(Integer.toString(computeTile(e.getKey() / _sizeW, e.getKey() %
			// _sizeW, e.getValue())),
			// (e.getKey() % _sizeW) * _blockSize, (e.getKey() / _sizeW) * _blockSize +
			// _blockSize / 2);

		}
		// g.drawString(String.valueOf(i * _sizeW + j), j * _blockSize, i * _blockSize +
		// _blockSize / 2);
	}

	public int rowColToIndex(int row, int col, int width) {
		return col + row * width;
	}

	public static HashMap<Integer, Integer> kek = new HashMap<Integer, Integer>();

	public void setKek() {
		kek.put(2, 1);
		kek.put(8, 2);
		kek.put(10, 3);
		kek.put(11, 4);
		kek.put(16, 5);
		kek.put(18, 6);
		kek.put(22, 7);
		kek.put(24, 8);
		kek.put(26, 9);
		kek.put(27, 10);
		kek.put(30, 11);
		kek.put(31, 12);
		kek.put(64, 13);
		kek.put(66, 14);
		kek.put(72, 15);
		kek.put(74, 16);
		kek.put(75, 17);
		kek.put(80, 18);
		kek.put(82, 19);
		kek.put(86, 20);
		kek.put(88, 21);
		kek.put(90, 22);
		kek.put(91, 23);
		kek.put(94, 24);
		kek.put(95, 25);
		kek.put(104, 26);
		kek.put(106, 27);
		kek.put(107, 28);
		kek.put(120, 29);
		kek.put(122, 30);
		kek.put(123, 31);
		kek.put(126, 32);
		kek.put(127, 33);
		kek.put(208, 34);
		kek.put(210, 35);
		kek.put(214, 36);
		kek.put(216, 37);
		kek.put(218, 38);
		kek.put(219, 39);
		kek.put(222, 40);
		kek.put(223, 41);
		kek.put(248, 42);
		kek.put(250, 43);
		kek.put(251, 44);
		kek.put(254, 45);
		kek.put(255, 46);
		kek.put(0, 47);
	}

	public int placeMeeting(int col, int row, int curCol, int curRow, int tileVal) {
		if (_map.getHmap().containsKey(curRow * _sizeW + curCol)
				&& _map.getHmap().get(curRow * _sizeW + curCol).intValue() == tileVal) {
			return 1;
		}
		return 0;
	}

	public int computeTile(int y, int x, int tileVal) {
		int sum = 0;
		int north_tile = placeMeeting(x, y, x, y - 1, tileVal);
		int south_tile = placeMeeting(x, y, x, y + 1, tileVal);
		int west_tile = placeMeeting(x, y, x - 1, y, tileVal);
		int east_tile = placeMeeting(x, y, x + 1, y, tileVal);
		int north_west_tile = (placeMeeting(x, y, x - 1, y - 1, tileVal) == 1 && west_tile == 1 && north_tile == 1) ? 1
				: 0;
		int north_east_tile = (placeMeeting(x, y, x + 1, y - 1, tileVal) == 1 && north_tile == 1 && east_tile == 1) ? 1
				: 0;
		int south_west_tile = (placeMeeting(x, y, x - 1, y + 1, tileVal) == 1 && south_tile == 1 && west_tile == 1) ? 1
				: 0;
		int south_east_tile = (placeMeeting(x, y, x + 1, y + 1, tileVal) == 1 && south_tile == 1 && east_tile == 1) ? 1
				: 0;

		// 8 bit Bitmasking calculation using Directional check booleans values
		sum = north_west_tile + 2 * north_tile + 4 * north_east_tile + 8 * west_tile + 16 * east_tile
				+ 32 * south_west_tile + 64 * south_tile + 128 * south_east_tile;

		if (kek.containsKey(sum))
			return kek.get(sum);
		return sum;
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
		g.drawOval((int) _shark.getX() - _blockSize * 5 / 2, (int) _shark.getY() - _blockSize * 5 / 2, _blockSize * 5,
				_blockSize * 5);

	}

	public void drawBars(Graphics g) {
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
