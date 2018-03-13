import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.JPanel;

public class MapPanel extends JPanel implements MyMouseListener
{
	private int _blockSize, _mapWidth, _mapHeight;
	private Img _backgroundImg;
	private BlockType _blocksTypes[];
	private BlockType _sandBlocks[], _stoneBlocks[], _seaweedBlock[];
	private boolean _mouseDown;
	private Logic _logic;
	private Point2D.Double _mousePoint;

	private boolean _drawDebug, _fastGraphics;
	private String _mapFile, _effectsFile, _backgroundFile;

	public MapPanel(boolean drawDebug, boolean fastGraphics)
	{
		setOpaque(false);
		_drawDebug = drawDebug;
		_fastGraphics = fastGraphics;
		_mapFile = "MapFiles//world_20180221203331.xml";
		_backgroundFile = "MapFiles//background_20180220162654.xml";
		_effectsFile = "MapFiles//effects_20180103202456.xml";
		_mapHeight = Map.getElementCountByName(_mapFile, "Line");
		_mapWidth = Map.getElementCountByName(_mapFile, "Area") / _mapHeight;
		_blockSize = 60;
		BlockType.setSize(_blockSize);
		Block.setSize(_blockSize);
		_logic = new Logic(new Map(_mapHeight, _mapWidth, _mapFile, _effectsFile, _backgroundFile));
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
	protected synchronized void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		// super.paintComponent(g2d);
		g2d.translate(-_logic.getCam().getCamPoint().x, -_logic.getCam().getCamPoint().y);
		if (_fastGraphics)
		{
			g2d.setColor(Color.blue);
			g.fillRect(g2d.getClipBounds().x, g2d.getClipBounds().y, g2d.getClipBounds().width, g2d.getClipBounds().height);
			drawGayMap(g2d, _logic.getMap().getHbackgrounds());
		}
		else
		{
			_backgroundImg.drawImg(g2d);
			drawHMap(g2d, _logic.getMap().getHbackgrounds());

		}
		_logic.getPlayer().Paint(g2d, _drawDebug);
		_logic.paintAICharacters(g2d, _drawDebug);
		if (_fastGraphics)
			drawGayMap(g2d, _logic.getMap().getHmap());
		else
			drawHMap(g2d, _logic.getMap().getHmap());
		_logic.getPlayer().drawBars(g2d);
		if (_drawDebug)
			_logic.drawDebug(g2d);
		g2d.dispose();
		g.dispose();
	}

	public void drawGayMap(Graphics2D g, Block[][] hmap)
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
						switch (hmap[y][x].getBitMask().getBlockID())
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
						if (hmap[y][x].getBitMask().getBlockID() != 3)
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
						switch (hmap[y][x].getBitMask().getBlockID())
						{
							case 1:
							{
								_sandBlocks[hmap[y][x].getBitMask().getBitMask()].paintAt(g, x, y);
								break;
							}
							case 2:
							{
								_stoneBlocks[hmap[y][x].getBitMask().getBitMask()].paintAt(g, x, y);
								break;
							}
							case 3:
							{
								switch (hmap[y][x].getBitMask().getBitMask())
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
										_seaweedBlock[1].paintAt(g, x, y);
										break;
									}
									default:
									{
										_seaweedBlock[0].paintAt(g, x, y);
									}
								}
								break;
							}
							default:
							{
								_blocksTypes[hmap[y][x].getBitMask().getBlockID() - 1].paintAt(g, x, y);
							}
						}
					}
				}
				// g.drawString( Integer.toString(e.getValue().getBitMask()),
				// (e.getKey() % _logic.getMap().getWidth()) * _blockSize,
				// (e.getKey() / _logic.getMap().getWidth()) * _blockSize +
				// _blockSize / 2);

				// g.drawString(String.valueOf(i * _mapWidth + j), j *
				// _blockSize,
				// i *
				// _blockSize +
				// _blockSize / 2);
			}
		}
	}

	public int rowColToIndex(int row, int col, int width)
	{
		return col + row * width;
	}

	public void doLogic()
	{
		checkMouse();
		_logic.doLogic();
	}

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

	public Logic getLogic()
	{
		return _logic;
	}

	public void setLogic(Logic logic)
	{
		_logic = logic;
	}
}