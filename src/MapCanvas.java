import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

public class MapCanvas extends Canvas implements MyMouseListener
{
	private int _blockSize, _mapWidth, _mapHeight;
	private Img _backgroundImg;
	private BlockType _blocksTypes[];
	private BlockType _sandBlocks[], _stoneBlocks[], _seaweedBlock[];
	private String _mapFile, _effectsFile, _backgroundFile;
	private boolean _mouseDown;
	private Logic _logic;
	private Point2D.Double _mousePoint;
	public static ImageLoader _imageLoader;

	boolean drawDebug = false;

	public MapCanvas()
	{
		// setOpaque(false);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		addMouseMotionListener(getMouseMotionAdapter());
		addMouseListener(getMouseAdapter());
		// addMouseListener(a);
		_mapFile = "MapFiles//world_20180221203331.xml";
		_backgroundFile = "MapFiles//background_20180220162654.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_mapHeight = Map.getElementCountByName(_mapFile, "Line");
		_mapWidth = Map.getElementCountByName(_mapFile, "Area") / _mapHeight;
		_blockSize = 60;
		BlockType.setSize(_blockSize);
		_imageLoader = new ImageLoader();

		Player player = new Player(2000, 400, 8 * _blockSize / 10, 19 * _blockSize / 10, 8);
		Camera cam = new Camera(new Point(_blockSize, _blockSize), (int) screenSize.getWidth(), (int) screenSize.getHeight(), _mapWidth,
								_mapHeight);
		Map map = new Map(_mapHeight, _mapWidth, _mapFile, _effectsFile, _backgroundFile);
		_logic = new Logic(player, cam, map, new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5)));
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _mapWidth * _blockSize, _mapHeight * _blockSize);
		_blocksTypes = new BlockType[5];
		_mouseDown = false;
		_mousePoint = new Point2D.Double(0, 0);
		for (int i = 0; i < _blocksTypes.length; i++)
		{
			_blocksTypes[i] = new BlockType(i + 1);
		}
		_sandBlocks = setBlocks("images\\Blocks\\Sand\\");
		_stoneBlocks = setBlocks("images\\Blocks\\Stone\\");
		_seaweedBlock = setBlocks("images\\Blocks\\Seaweed\\");
	}

	public void doLogic()
	{
		_logic.doLogic();
	}

	public BlockType[] setBlocks(String path)
	{
		BlockType[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new BlockType[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				// System.out.println(path + child.getName());
				arr[counter++] = new BlockType(path + child.getName());
			}
		}
		return arr;
	}

	public void drawComponent()
	{
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null)
		{
			this.createBufferStrategy(4);
			return;
		}
		Graphics2D g2d = (Graphics2D) bs.getDrawGraphics();
		g2d.translate(-_logic.getCam().getCamPoint().x, -_logic.getCam().getCamPoint().y);

		// g.setColor(Color.cyan);
		// g2d.fillRect(_logic.getCam().getCamPoint().x,
		// _logic.getCam().getCamPoint().y, getWidth(), getHeight());

		_backgroundImg.drawImg(g2d);
		drawHMap(g2d, _logic.getMap().getHbackgrounds());
		// drawGayMap(g2d, _logic.getMap().getHbackgrounds());
		_logic.getPlayer().Paint(g2d, drawDebug);
		_logic.paintAICharacters(g2d, drawDebug);
		drawHMap(g2d, _logic.getMap().getHmap());
		// drawGayMap(g2d, _logic.getMap().getHmap());
		_logic.getPlayer().drawBars(g2d, _logic.getCam().getCamPoint());
		if (drawDebug)
			_logic.drawDebug(g2d);
		g2d.dispose();
		bs.show();
	}

	// @Override
	// protected void paintComponent(Graphics g)
	// {
	// Graphics2D g2d = (Graphics2D) g;
	// super.paintComponent(g2d);
	// // g.setColor(Color.cyan);
	// g.translate(-_logic.getCam().getCamPoint().x,
	// -_logic.getCam().getCamPoint().y);
	// // g.fillRect(g2d.getClipBounds().x, g2d.getClipBounds().y,
	// // g2d.getClipBounds().width, g2d.getClipBounds().height);
	// _backgroundImg.drawImg(g);
	// drawHMap(g2d, _logic.getMap().getHbackgrounds());
	// // drawGayMap(g, _logic.getMap().getHbackgrounds());
	// _logic.getPlayer().Paint(g2d, drawDebug);
	// _logic.paintAICharacters(g2d, drawDebug);
	// drawHMap(g2d, _logic.getMap().getHmap());
	// // drawGayMap(g2d, _logic.getMap().getHmap());
	// _logic.getPlayer().drawBars(g2d, _logic.getCam().getCamPoint());
	// if (drawDebug)
	// _logic.drawDebug(g2d);
	// }

	public void drawGayMap(Graphics2D g, HashMap<Point, BitMask> hmap)
	{
		g.setColor(Color.black);
		Iterator<java.util.Map.Entry<Point, BitMask>> iterator = hmap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<Point, BitMask> e = iterator.next();
			// for (Entry<Integer, BitMask> e : hmap.entrySet())
			// {
			int row = e.getKey().y, col = e.getKey().x;
			if (_logic.getCam().inScreen(new Area((new Rectangle(col * _blockSize, row * _blockSize, _blockSize, _blockSize)))))
			{
				if (e.getValue().getBlockID() != 0)
				{
					g.drawRect(col * _blockSize, row * _blockSize, _blockSize, _blockSize);
				}
			}
		}
	}

	public void drawHMap(Graphics2D g, HashMap<Point, BitMask> hmap)
	{

		Iterator<java.util.Map.Entry<Point, BitMask>> iterator = hmap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<Point, BitMask> e = iterator.next();
			// for (Entry<Integer, BitMask> e : hmap.entrySet())
			// {
			int row = e.getKey().y, col = e.getKey().x;
			if (_logic.getCam().inScreen(new Area((new Rectangle(col * _blockSize, row * _blockSize, _blockSize, _blockSize)))))
			{
				switch (e.getValue().getBlockID())
				{
					case 1:
					{
						_sandBlocks[e.getValue().getBitMask()].paintAt(g, col, row);
						break;
					}
					case 2:
					{
						_stoneBlocks[e.getValue().getBitMask()].paintAt(g, col, row);
						break;
					}
					case 3:
					{
						switch (e.getValue().getBitMask())
						{
							case 2:
							case 5:
							case 8:
							case 13:
							case 24:
							case 26:
							case 29:
							case 34:
							case 42:
							case 47:
							{
								_seaweedBlock[1].paintAt(g, col, row);
								break;
							}
							default:
							{
								_seaweedBlock[0].paintAt(g, col, row);
							}
						}
						break;
					}
					default:
					{
						_blocksTypes[e.getValue().getBlockID() - 1].paintAt(g, col, row);
					}
				}
			}
			// g.drawString( Integer.toString(e.getValue().getBitMask()),
			// (e.getKey() % _logic.getMap().getWidth()) * _blockSize,
			// (e.getKey() / _logic.getMap().getWidth()) * _blockSize +
			// _blockSize / 2);

		}
		// g.drawString(String.valueOf(i * _mapWidth + j), j *
		// _blockSize,
		// i *
		// _blockSize +
		// _blockSize / 2);
	}

	public int rowColToIndex(int row, int col, int width)
	{
		return col + row * width;
	}

	public void checkMouse()
	{
		_logic.getCam().getMousePoint().setLocation(_mousePoint);
		_logic.getPlayer().applyMouseBoost(_mouseDown);
	}

	public void printMat(int[][] mat, int size, int sizeW)
	{
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < sizeW; j++)
			{
				System.out.print(mat[i][j] + " ");
			}
			System.out.println();
		}
	}

	public void printPolygon(Polygon p)
	{
		for (int i = 0; i < p.npoints; i++)
		{
			System.out.println("x: " + p.xpoints[i] + " y: " + p.ypoints[i]);
		}
	}

	public MouseMotionAdapter getMouseMotionAdapter()
	{
		MouseMotionAdapter a = new MouseMotionAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				_mousePoint.setLocation(e.getPoint());
			}

			public void mouseMoved(MouseEvent e)
			{
				_mousePoint.setLocation(e.getPoint());
			}
		};
		return a;
	}

	public MouseAdapter getMouseAdapter()
	{
		MouseAdapter a = new MouseAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				_mousePoint.setLocation(e.getPoint());
			}

			public void mouseMoved(MouseEvent e)
			{
				_mousePoint.setLocation(e.getPoint());
			}

			public void mousePressed(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					_mouseDown = true;
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if (e.getButton() == MouseEvent.BUTTON1)
				{
					_mouseDown = false;
				}
			}
		};
		return a;
	}

	public void mouseDragged(MouseEvent e)
	{
		_mousePoint.setLocation(e.getPoint());
	}

	public void mouseMoved(MouseEvent e)
	{
		_mousePoint.setLocation(e.getPoint());
	}

	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			_mouseDown = true;
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			_mouseDown = false;
		}
	}

	public Logic getLogic()
	{
		return _logic;
	}

	public void setLogic(Logic logic)
	{
		_logic = logic;
	}
}
