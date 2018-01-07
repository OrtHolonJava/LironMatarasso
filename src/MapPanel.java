import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.peer.MouseInfoPeer;

import javax.swing.JPanel;
import javax.swing.Timer;

import images.Img;

import map.Map;

public class MapPanel extends JPanel implements ActionListener, MouseMotionListener, MouseListener {
	private String _mapFile;
	private String _effectsFile;
	private int _size;
	private int _sizeW;
	private int _blockSize;
	private double _speed;
	private int _mapPixelWidth;
	private int _mapPixelHeight;
	private double _sharkOffsetX;
	private double _sharkOffsetY;
	private Img _backgroundImg;
	private Map _map;
	private Img _sandBlock;
	private Img _stoneBlock;
	private Img _seaweedBlock;
	private Img _sandBackground;
	private Img _stoneBackground;
	private Img _shark;
	private Img _krahs;
	private BufferedImage _bImgShark;
	private BufferedImage _bImgKrahs;
	private Point2D.Double _mousePoint;
	private Point2D.Double _finalSharkPoint;
	private Point2D.Double _finalMousePoint;
	private Point2D.Double _centerPoint;
	private Point2D.Double _camPoint;

	public MapPanel() {
		_mapFile = "MapFiles//pic2_20171206105928.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_size = Map.getElementCountByName(_mapFile, "Line");
		_sizeW = Map.getElementCountByName(_mapFile, "Area") / _size;
		_blockSize = 40;
		_mapPixelWidth = _sizeW * _blockSize;
		_mapPixelHeight = _size * _blockSize;
		_speed = 4;
		_sharkOffsetX = 0;
		_sharkOffsetY = 0;
		_mousePoint = new Point2D.Double(0, 0);
		_centerPoint = new Point2D.Double(0, 0);
		_camPoint = new Point2D.Double(0, 0);
		_finalMousePoint = new Point2D.Double(0, 0);
		_finalSharkPoint = new Point2D.Double(0, 0);
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _sizeW * _blockSize, _size * _blockSize);
		_sandBlock = new Img("images//SandBlock2.png", 0, 0, _blockSize, _blockSize);
		_stoneBlock = new Img("images//‏‏StoneBlock2.png", 0, 0, _blockSize, _blockSize);
		_seaweedBlock = new Img("images//OneSW.png", 0, 0, _blockSize, _blockSize);
		_sandBackground = new Img("images//SandBackground.png", 0, 0, _blockSize, _blockSize);
		_stoneBackground = new Img("images//StoneBackground.png", 0, 0, _blockSize, _blockSize);
		_shark = new Img("images//shark1.png", 0, 0, 33, 61);
		_krahs = new Img("images//1krahs.png", 0, 0, 33, 61);
		_map = new Map(_size, _sizeW, _mapFile, _effectsFile);
		addMouseMotionListener(this);
		addMouseListener(this);
		Timer t = new Timer(10, this);
		t.start();
	}

	protected void rotateShark2(Graphics g) {
		_bImgShark = Img.toBufferedImage(_shark.getImage());
		_bImgKrahs = Img.toBufferedImage(_krahs.getImage());
		BufferedImage use = _bImgShark;
		Graphics2D g2d = (Graphics2D) g.create();
		double rotation = 0f;

		if (_finalMousePoint != null) {

			double x = _finalSharkPoint.x;
			double y = _finalSharkPoint.y;
			double deltaX = _finalMousePoint.x - x;
			double deltaY = _finalMousePoint.y - y;
			rotation = Math.toDegrees(-Math.atan2(deltaX, deltaY)) + 180;
			use = (rotation < 180) ? _bImgShark : _bImgKrahs;
		}
		double x = _finalSharkPoint.x - use.getWidth() / 2;
		double y = _finalSharkPoint.y - use.getWidth() / 2;
		g2d.rotate(Math.toRadians(rotation), _finalSharkPoint.x, _finalSharkPoint.y);
		g2d.drawImage(use, (int) x, (int) y, this);
		g2d.dispose();
	}

	@Override
	protected void paintComponent(Graphics g1) {
		// TODO Auto-generated method stub
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_camPoint.x, -_camPoint.y);
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
		rotateShark2(g);
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
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		// System.out.println("w: " + getWidth() + " h: " + getHeight());
		// System.out.println("tot: w:" + _sizeW * _blockSize + " h: " + _size *
		// _blockSize);
		_finalMousePoint.setLocation(_camPoint);
		_finalMousePoint.x += _mousePoint.x;
		_finalMousePoint.y += _mousePoint.y;
		_centerPoint.setLocation(_camPoint.x + getWidth() / 2, _camPoint.y + getHeight() / 2);
		_finalSharkPoint.setLocation(_centerPoint.x + _sharkOffsetX, _centerPoint.y + _sharkOffsetY);
		double x = _finalSharkPoint.x;
		double y = _finalSharkPoint.y;
		double deltaX = _finalMousePoint.x - x;
		double deltaY = _finalMousePoint.y - y;
		double rotation = -Math.atan2(deltaX, deltaY);
		rotation = Math.toDegrees(rotation) + 180;
		if (_sharkOffsetY == 0) {
			_camPoint.y -= _speed * Math.cos(Math.toRadians(-rotation));
			if (_camPoint.y < 0) {
				_camPoint.y = 0;
				_sharkOffsetY -= _speed * Math.cos(Math.toRadians(-rotation));
			}
			if (_camPoint.y > _mapPixelHeight - getHeight()) {
				_camPoint.y = _mapPixelHeight - getHeight();
				_sharkOffsetY -= _speed * Math.cos(Math.toRadians(-rotation));
			}
		} else {
			int preSignY = (int) Math.signum(_sharkOffsetY);
			_sharkOffsetY -= _speed * Math.cos(Math.toRadians(-rotation));
			if (preSignY != (int) Math.signum(_sharkOffsetY)) {
				_camPoint.y -= _sharkOffsetY;
				_sharkOffsetY = 0;
			}
		}

		if (_sharkOffsetX == 0) {
			_camPoint.x -= _speed * Math.sin(Math.toRadians(-rotation));
			if (_camPoint.x < 0) {
				_camPoint.x = 0;
				_sharkOffsetX -= _speed * Math.sin(Math.toRadians(-rotation));

			}
			if (_camPoint.x > _mapPixelWidth - getWidth()) {
				_camPoint.x = _mapPixelWidth - getWidth();
				_sharkOffsetX -= _speed * Math.sin(Math.toRadians(-rotation));
			}
		} else {
			int preSignX = (int) Math.signum(_sharkOffsetX);
			_sharkOffsetX -= _speed * Math.sin(Math.toRadians(-rotation));
			if (preSignX != (int) Math.signum(_sharkOffsetX)) {
				_camPoint.x -= _sharkOffsetX;
				_sharkOffsetX = 0;
			}
		}

		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		_speed += 5;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
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
