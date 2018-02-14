import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.Timer;

import images.Img;

public class MapPanel extends JPanel
{
	private Img _backgroundImg;
	private BlockType _blocksTypes[];
	private BlockType _sandBlocks[], _stoneBlocks[], _seaweedBlock[];
	private String _mapFile, _effectsFile, _backgroundFile;
	private boolean _mouseDown;
	private Logic _logic;

	public MapPanel()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		MouseAdapter mouseAdapter = getMouseAdapter();
		_mapFile = "MapFiles//world2s_20180214134301.xml";
		_backgroundFile = "MapFiles//backgrounds_20180214141727.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		int mapHeight = Map.getElementCountByName(_mapFile, "Line");
		int mapWidth = Map.getElementCountByName(_mapFile, "Area") / mapHeight;
		BlockType.setSize(40);
		Player player = new Player(0, 0, 8 * BlockType.getSize() / 10, 19 * BlockType.getSize() / 10, 8);
		Camera cam = new Camera(0, -10 * BlockType.getSize(), new Point2D.Double(BlockType.getSize() * 3, BlockType.getSize()),
								(int) screenSize.getWidth(), (int) screenSize.getHeight(), mapWidth, mapHeight);
		Map map = new Map(mapHeight, mapWidth, _mapFile, _effectsFile, _backgroundFile);
		_logic = new Logic(player, cam, map, new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5)));
		_backgroundImg = new Img("images//Background.jpg", 0, 0, mapWidth * BlockType.getSize(), mapHeight * BlockType.getSize());
		_blocksTypes = new BlockType[5];
		_mouseDown = false;
		for (int i = 0; i < _blocksTypes.length; i++)
		{
			_blocksTypes[i] = new BlockType(i + 1);
		}
		_sandBlocks = setBlocks("images\\Blocks\\Sand\\");
		_stoneBlocks = setBlocks("images\\Blocks\\Stone\\");
		_seaweedBlock = setBlocks("images\\Blocks\\Seaweed\\");
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
	}

	public void startGame()
	{
		Timer t = new Timer(1000 / 60, getActionListener());
		t.start();
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

	boolean drawDebug = true;

	@Override
	protected void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_logic.getCam().getCamPoint().x, -_logic.getCam().getCamPoint().y);
		_backgroundImg.drawImg(g);
		drawHMap(g, _logic.getMap().getHbackgrounds());
		drawHMap(g, _logic.getMap().getHmap());
		_logic.getPlayer().Paint(g, drawDebug);
		drawBars(g);
		if (drawDebug)
			drawDebug(g);
	}

	public boolean InScreen(int row, int col)
	{
		return (col <= 1 + ((getWidth() + _logic.getCam().getCamPoint().getX()) / BlockType.getSize())
					&& col + 1 >= _logic.getCam().getCamPoint().getX() / BlockType.getSize()
				&& row <= 1 + ((getHeight() + _logic.getCam().getCamPoint().getY()) / BlockType.getSize())
				&& row + 1 >= _logic.getCam().getCamPoint().getY() / BlockType.getSize());
	}

	public void drawHMap(Graphics g, HashMap<Integer, BitMask> hmap)
	{
		for (Entry<Integer, BitMask> e : hmap.entrySet())
		{
			int row = e.getKey() / _logic.getMap().getWidth(), col = e.getKey() % _logic.getMap().getWidth();
			if (InScreen(row, col))
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
			g.drawString(	Integer.toString(e.getValue().getBitMask()), (e.getKey() % _logic.getMap().getWidth()) * BlockType.getSize(),
							(e.getKey() / _logic.getMap().getWidth()) * BlockType.getSize() + BlockType.getSize() / 2);

		}
		// g.drawString(String.valueOf(i * mapWidth + j), j *
		// BlockType.getSize(),
		// i *
		// BlockType.getSize() +
		// BlockType.getSize() / 2);
	}

	public int rowColToIndex(int row, int col, int width)
	{
		return col + row * width;
	}

	public void drawDebug(Graphics g)
	{
		g.setColor(Color.red);
		g.drawRect((int) _logic.getCam().getFinalMousePoint().x, (int) _logic.getCam().getFinalMousePoint().y, 100, 100);
		g.setColor(Color.yellow);
		g.drawRect((int) _logic.getCam().getCenterPoint().x, (int) _logic.getCam().getCenterPoint().y, 100, 100);
		g.setColor(Color.green);
		g.drawRect((int) _logic.getCam().getCamPoint().x, (int) _logic.getCam().getCamPoint().y, 100, 100);
		g.setColor(Color.cyan);
		g.drawRect((int) _logic.getPlayer().getX(), (int) _logic.getPlayer().getY(), 100, 100);
		g.setColor(Color.orange);
		for (Rectangle r : _logic.getRects())
		{
			g.fillRect(r.x, r.y, r.width, r.height);
		}
		g.setColor(new Color(128, 0, 128));
		for (Point2D p : _logic.getPlayer().getPolyList())
		{
			g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
		}
		g.setColor(Color.magenta);
		for (Point2D p : _logic.getColiList())
		{
			g.fillRect((int) p.getX(), (int) p.getY(), 1, 1);
		}
		g.drawOval((int) _logic.getPlayer().getX()	- BlockType.getSize() * 5 / 2,
					(int) _logic.getPlayer().getY() - BlockType.getSize() * 5 / 2, BlockType.getSize() * 5, BlockType.getSize() * 5);

	}

	public void drawBars(Graphics g)
	{
		g.setColor(Color.red);
		g.drawString("health: "	+ String.valueOf((int) _logic.getPlayer().getHealth()), (int) _logic.getCam().getCamPoint().x,
						(int) _logic.getCam().getCamPoint().y + 10);
		g.drawRect((int) _logic.getCam().getCamPoint().x + 70, (int) _logic.getCam().getCamPoint().y, 100, 10);
		g.fillRect((int) _logic.getCam().getCamPoint().x	+ 70, (int) _logic.getCam().getCamPoint().y, (int) _logic.getPlayer().getHealth(),
					10);
		g.setColor(Color.green);
		g.drawString("stamina: "	+ String.valueOf((int) _logic.getPlayer().getStamina()), (int) _logic.getCam().getCamPoint().x,
						(int) _logic.getCam().getCamPoint().y + 20);
		g.drawRect((int) _logic.getCam().getCamPoint().x + 70, (int) _logic.getCam().getCamPoint().y + 10, 100, 10);
		g.fillRect((int) _logic.getCam().getCamPoint().x	+ 70, (int) _logic.getCam().getCamPoint().y + 10,
					(int) _logic.getPlayer().getStamina(), 10);
		g.setColor(Color.yellow);
		g.drawString("hunger: "	+ String.valueOf((int) _logic.getPlayer().getHunger()), (int) _logic.getCam().getCamPoint().x,
						(int) _logic.getCam().getCamPoint().y + 30);
		g.drawRect((int) _logic.getCam().getCamPoint().x + 70, (int) _logic.getCam().getCamPoint().y + 20, 100, 10);
		g.fillRect((int) _logic.getCam().getCamPoint().x	+ 70, (int) _logic.getCam().getCamPoint().y + 20,
					(int) _logic.getPlayer().getHunger(), 10);
	}

	public MouseAdapter getMouseAdapter()
	{
		MouseAdapter m = new MouseAdapter()
		{
			public void mouseDragged(MouseEvent e)
			{
				_logic.getCam().getMousePoint().setLocation(e.getPoint());
			}

			public void mouseMoved(MouseEvent e)
			{
				_logic.getCam().getMousePoint().setLocation(e.getPoint());
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
		return m;
	}

	public ActionListener getActionListener()
	{
		ActionListener a = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				// long start = System.nanoTime(), end;
				if (getWidth() != 0 && getHeight() != 0)
				{
					checkMouse();
					_logic.movementLogic();
					repaint();
				}
				// end = System.nanoTime();
				// System.out.println((double) (end - start) / 1000000000);
			}

		};
		return a;
	}

	public void checkMouse()
	{
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

}
