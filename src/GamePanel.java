import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.JPanel;

/**
 * GamePanel class for the panel that shows the game
 * 
 * @author liron
 */
public class GamePanel extends JPanel implements MyMouseListener, MyKeyListener
{
	private int _blockSize, _mapWidth, _mapHeight;
	private Img _backgroundImg;
	private BlockGraphics _blocksTypes[];
	private BlockGraphics _sandBlocks[], _stoneBlocks[], _seaweedBlock[];
	private boolean _mouseDown;
	private Logic _logic;
	private Point2D.Double _mousePoint;

	public static LinkedList<Integer> _passables = new LinkedList<Integer>(Arrays.asList(0, 3, 4, 5));

	private boolean _drawDebug, _fastGraphics;
	private String _mapFile, _backgroundFile;

	/**
	 * Init a new GamePanel object with the following parameters:
	 * 
	 * @param drawDebug
	 * @param fastGraphics
	 * @param frame
	 */
	public GamePanel(boolean drawDebug, boolean fastGraphics, GameFrame frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		setOpaque(false);
		_drawDebug = drawDebug;
		_fastGraphics = fastGraphics;
		_mapFile = "MapFiles//world_20180221203331.xml";
		_backgroundFile = "MapFiles//background_20180220162654.xml";
		_mapHeight = Map.getElementCountByName(_mapFile, "Line");
		_mapWidth = Map.getElementCountByName(_mapFile, "Area") / _mapHeight;
		_blockSize = 80;
		BlockGraphics.setSize(_blockSize);
		Block.setSize(_blockSize);
		_logic = new Logic(new Map(_mapHeight, _mapWidth, _mapFile, _backgroundFile), frame);
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _mapWidth * _blockSize, _mapHeight * _blockSize);
		_blocksTypes = new BlockGraphics[2];
		_mouseDown = false;
		_mousePoint = new Point2D.Double(0, 0);
		for (int i = 0; i < _blocksTypes.length; i++)
		{
			_blocksTypes[i] = new BlockGraphics(i);
		}
		_sandBlocks = setBlocks("images\\Blocks\\Sand\\");
		_stoneBlocks = setBlocks("images\\Blocks\\Stone\\");
		_seaweedBlock = setBlocks("images\\Blocks\\Seaweed\\");
	}

	/**
	 * gets a directory path and returns all the pictures inside the directory
	 * in an array
	 * 
	 * @param path
	 * @return the blockgraphics in the path in array
	 */
	public BlockGraphics[] setBlocks(String path)
	{
		BlockGraphics[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new BlockGraphics[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				arr[counter++] = new BlockGraphics(path + child.getName());
			}
		}
		return arr;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(-_logic.getCam().getCamPoint().x, -_logic.getCam().getCamPoint().y);
		if (_fastGraphics)
		{
			g2d.setColor(Color.blue);
			g2d.fill(g2d.getClipBounds());
			drawFastMap(g2d, _logic.getMap().getHbackgrounds());
		}
		else
		{
			_backgroundImg.drawImg(g2d);
			drawHMap(g2d, _logic.getMap().getHbackgrounds());
		}

		_logic.getPlayer().draw(g2d, _drawDebug);

		if (_fastGraphics)
		{
			drawFastMap(g2d, _logic.getMap().getHmap());
		}
		else
		{
			drawHMap(g2d, _logic.getMap().getHmap());
		}
		if (_drawDebug)
		{
			_logic.drawDebug(g2d);
		}
		_logic.drawAICharacters(g2d, _drawDebug);
		_logic.getPlayer().drawBars(g2d);
		g2d.dispose();
		g.dispose();
		Toolkit.getDefaultToolkit().sync();
	}

	/**
	 * draws the map in fast graphics
	 * 
	 * @param g
	 * @param hmap
	 */
	public void drawFastMap(Graphics2D g, Block[][] hmap)
	{
		g.setColor(Color.black);
		for (int y = 0; y < hmap.length; y++)
		{
			for (int x = 0; x < hmap[y].length; x++)
			{
				if (hmap[y][x] != null)
				{
					if (g.getClipBounds().intersects(hmap[y][x].getRectangle()))
					{
						switch (hmap[y][x].getBlockRelativeInfo().getBlockID())
						{
							case 1:
							{
								g.setColor(Color.yellow);
								break;
							}
							case 2:
							{
								g.setColor(Color.gray);
								break;
							}
							case 3:
							{
								g.setColor(Color.green);
								break;
							}
							case 4:
							{
								g.setColor(new Color(204, 182, 102));
								break;
							}
							case 5:
							{
								g.setColor(Color.darkGray);
								break;
							}

						}
						if (hmap[y][x].getBlockRelativeInfo().getBlockID() != 3)
						{
							g.fill(hmap[y][x].getRectangle());
						}
						else
						{
							g.fillRect(x * _blockSize + 1 * _blockSize / 7, y * _blockSize, _blockSize / 7, _blockSize);
							g.fillRect(x * _blockSize + 3 * _blockSize / 7, y * _blockSize, _blockSize / 7, _blockSize);
							g.fillRect(x * _blockSize + 5 * _blockSize / 7, y * _blockSize, _blockSize / 7, _blockSize);
						}
					}
				}
			}
		}
	}

	/**
	 * draws the map in regular graphics
	 * 
	 * @param g
	 * @param hmap
	 */
	public void drawHMap(Graphics2D g, Block[][] hmap)
	{
		for (int y = 0; y < hmap.length; y++)
		{
			for (int x = 0; x < hmap[y].length; x++)
			{
				if (hmap[y][x] != null)
				{
					if (g.getClipBounds().intersects(hmap[y][x].getRectangle()))
					{
						switch (hmap[y][x].getBlockRelativeInfo().getBlockID())
						{
							case 1:
							{
								_sandBlocks[hmap[y][x].getBlockRelativeInfo().getBitMask()].drawtAt(g, x, y);
								break;
							}
							case 2:
							{
								_stoneBlocks[hmap[y][x].getBlockRelativeInfo().getBitMask()].drawtAt(g, x, y);
								break;
							}
							case 3:
							{
								switch (hmap[y][x].getBlockRelativeInfo().getBitMask())
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
										_seaweedBlock[1].drawtAt(g, x, y);
										break;
									}
									default:
									{
										_seaweedBlock[0].drawtAt(g, x, y);
									}
								}
								break;
							}
							default:
							{
								_blocksTypes[hmap[y][x].getBlockRelativeInfo().getBlockID() - 4].drawtAt(g, x, y);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * gets the following parameters:
	 * 
	 * @param row
	 * @param col
	 * @param width
	 * @return the conversion of the row and col of an element in a matrix to
	 *         its index in the matrix
	 */
	public int rowColToIndex(int row, int col, int width)
	{
		return col + row * width;
	}

	/**
	 * gets the mouse input and computes everything
	 */
	public void doLogic()
	{
		checkMouse();
		_logic.doLogic();
	}

	/**
	 * checks the location of the mouse on the screen and if the left button is
	 * pressed
	 */
	public void checkMouse()
	{
		_logic.getCam().getMousePoint().setLocation(_mousePoint);
		_logic.getPlayer().applyMouseBoost(_mouseDown);
	}

	@Override
	public void mouseDragged(MouseEvent e)
	{
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mouseMoved(MouseEvent e)
	{
		_mousePoint.setLocation(e.getPoint());
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			_mouseDown = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			_mouseDown = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		switch (e.getKeyCode())
		{
			case KeyEvent.VK_ESCAPE:
			{
				break;
			}

			case KeyEvent.VK_D:
			{
				_drawDebug = !_drawDebug;
				break;
			}

			case KeyEvent.VK_G:
			{
				_fastGraphics = !_fastGraphics;
				break;
			}

			case KeyEvent.VK_ADD:
			{
				_logic.spawnShoal();
				break;
			}

		}

	}

	@Override
	public void keyReleased(KeyEvent e)
	{

	}

	public Logic getLogic()
	{
		return _logic;
	}

	public void setLogic(Logic logic)
	{
		_logic = logic;
	}

	public static LinkedList<Integer> getPassables()
	{
		return _passables;
	}

	public static void setPassables(LinkedList<Integer> passables)
	{
		_passables = passables;
	}
}