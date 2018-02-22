import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MapPanel extends JPanel implements Runnable, MyMouseListener
{
	private int _blockSize, _mapWidth, _mapHeight;
	private Img _backgroundImg;
	private BlockType _blocksTypes[];
	private BlockType _sandBlocks[], _stoneBlocks[], _seaweedBlock[];
	private String _mapFile, _effectsFile, _backgroundFile;
	private boolean _mouseDown;
	private Logic _logic;
	private Point2D.Double _mousePoint;
	private Thread _gameThread;
	public static ImageLoader _imageLoader;

	private final double _ups = 60.0, _timeBetweenUpdates = 1000000000 / _ups, _targetFPS = 60,
			_timeBetweenRenders = 1000000000 / _targetFPS;
	private final int _maxUpdatesBeforeRender = 5;

	boolean drawDebug = false;

	public MapPanel()
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_mapFile = "MapFiles//world_20180221203331.xml";
		_backgroundFile = "MapFiles//background_20180220162654.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_mapHeight = Map.getElementCountByName(_mapFile, "Line");
		_mapWidth = Map.getElementCountByName(_mapFile, "Area") / _mapHeight;
		_blockSize = 60;
		BlockType.setSize(_blockSize);
		_imageLoader = new ImageLoader();

		Player player = new Player(900, 400, 8 * _blockSize / 10, 19 * _blockSize / 10, 8);
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

	@Override
	protected void paintComponent(Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1;
		super.paintComponent(g);
		g.translate(-_logic.getCam().getCamPoint().x, -_logic.getCam().getCamPoint().y);
		_backgroundImg.drawImg(g);
		drawHMap(g, _logic.getMap().getHbackgrounds());
		_logic.getPlayer().Paint(g, drawDebug);
		_logic.paintAICharacters(g, drawDebug);
		drawHMap(g, _logic.getMap().getHmap());
		_logic.getPlayer().drawBars(g, _logic.getCam().getCamPoint());
		if (drawDebug)
			_logic.drawDebug(g);
	}

	public void drawHMap(Graphics g, HashMap<Integer, BitMask> hmap)
	{

		Iterator<java.util.Map.Entry<Integer, BitMask>> iterator = hmap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<Integer, BitMask> e = iterator.next();
			// for (Entry<Integer, BitMask> e : hmap.entrySet())
			// {
			int row = e.getKey() / _logic.getMap().getWidth(), col = e.getKey() % _logic.getMap().getWidth();
			if (_logic.getCam().inScreen(new Area(new Rectangle(col * _blockSize, row * _blockSize, _blockSize, _blockSize))))
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

	public void startGame()
	{
		_gameThread = new Thread(this);
		_gameThread.start();
	}

	@Override
	public void run()
	{
		double lastUpdateTime = System.nanoTime(); // Store the time of the last
													// update call.
		double lastRenderTime = System.nanoTime(); // Store the time of the last
													// render call.
		double now;
		int updateCount;

		/**
		 * FPS Calculation Variables
		 */
		int lastSecondTime = (int) (lastUpdateTime / 1000000000);

		while (true)
		{
			now = System.nanoTime();
			updateCount = 0;

			/**
			 * Doing as many game updates as we currently need to.
			 */
			while (now - lastUpdateTime > _timeBetweenUpdates && updateCount < _maxUpdatesBeforeRender)
			{
				this.tick();
				lastUpdateTime += _timeBetweenUpdates;
				updateCount++;
			}

			// If for some reason an update takes forever, we don't want to do
			// an insane number of catchups.
			// If you were doing some sort of game that needed to keep EXACT
			// time, you would get rid of this.
			if (now - lastUpdateTime > _timeBetweenUpdates)
			{
				lastUpdateTime = now - _timeBetweenUpdates;
			}

			/**
			 * Render the current (updated) state of the game.
			 */
			this.render();
			lastRenderTime = now;

			// Update the frames we got.
			int thisSecond = (int) (lastUpdateTime / 1000000000);
			if (thisSecond > lastSecondTime)
			{
				lastSecondTime = thisSecond;
			}

			/**
			 * The timing mechanism. The thread sleeps within this while loop
			 * until enough time has passed and another update or render call is
			 * required.
			 */
			while (now - lastRenderTime < _timeBetweenRenders && now - lastUpdateTime < _timeBetweenUpdates)
			{
				Thread.yield(); // Yield until it has been at least the target
								// time between renders. This saves the CPU from
								// hogging.

				/**
				 * Preventing over-consumption of the system's CPU power.
				 */
				try
				{
					Thread.sleep(1);
				}
				catch (Exception e)
				{
				}

				now = System.nanoTime();
			}
		}
	}

	private void render()
	{
		SwingUtilities.invokeLater(() -> repaint());
	}

	private void tick()
	{
		SwingUtilities.invokeLater(() -> checkMouse());
		SwingUtilities.invokeLater(() -> _logic.doLogic());
		// System.out.println(_logic.getCam().getCamPoint());
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

}
