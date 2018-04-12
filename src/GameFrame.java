import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class GameFrame extends JFrame
{
	private MapPanel _mapPanel;
	private LinkedList<MyKeyListener> _keyListeners;
	private LinkedList<MyMouseListener> _mouseListeners;
	private GameLoop _gameLoop;
	private MenuPanel _menuPanel;
	private OptionsPanel _optionsPanel;
	private InGameOptionsPanel _inGameOptionsPanel;
	private endPanel _endPanel;
	private JLayeredPane _layerdPane;

	private boolean _drawDebug, _fastGraphics, _isPaused, _inGame;

	public GameFrame()
	{
		_drawDebug = false;
		_fastGraphics = false;
		_isPaused = false;
		_inGame = false;
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		_layerdPane = getLayeredPane();
		MouseAdapter mouseAdapter = getMouseAdapter();
		KeyAdapter keyAdapter = getKeyAdapter();
		_mouseListeners = new LinkedList<MyMouseListener>();
		_keyListeners = new LinkedList<MyKeyListener>();
		_menuPanel = new MenuPanel(this);
		_layerdPane.add(_menuPanel, 1);
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		addKeyListener(keyAdapter);
		setFocusable(true);
		requestFocus();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}

	public void startGame(JPanel panelToClose)
	{
		_isPaused = false;
		_inGame = true;
		_layerdPane.remove(panelToClose);
		_mapPanel = new MapPanel(_drawDebug, _fastGraphics, this);
		_layerdPane.add(_mapPanel, 1);
		addMyMouseListenerToList(_mapPanel);
		addMyKeyListenerToList(_mapPanel);
		revalidate();
		repaint();
		_gameLoop = new GameLoop(_mapPanel);
		_gameLoop.startGame();
	}

	public void close()
	{
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public void addMyMouseListenerToList(MyMouseListener e)
	{
		_mouseListeners.add(e);
	}

	public void addMyKeyListenerToList(MyKeyListener e)
	{
		_keyListeners.add(e);
	}

	public void togglePaused()
	{
		_isPaused = !_isPaused;
		_gameLoop.setPaused(_isPaused);
		if (_isPaused)
		{
			openInGameOptions();
		}
		else
		{
			closeInGameOptions();
		}
	}

	public KeyAdapter getKeyAdapter()
	{
		KeyAdapter k = new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE && _inGame)
				{
					togglePaused();
				}

				for (MyKeyListener l : _keyListeners)
				{
					l.keyPressed(e);
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				for (MyKeyListener l : _keyListeners)
				{
					l.keyReleased(e);
				}
			}
		};
		return k;
	}

	public MouseAdapter getMouseAdapter()
	{
		MouseAdapter m = new MouseAdapter()
		{
			@Override
			public void mouseDragged(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mouseDragged(e);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mouseMoved(e);
				}
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mousePressed(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mouseReleased(e);
				}
			}
		};
		return m;
	}

	public void buttonPressed(ImageButton.MyButtonType type)
	{
		switch (type)
		{
			case PLAY:
			{
				startGame(_menuPanel);
				break;
			}
			case OPTIONS:
			{
				openOptions();
				break;
			}
			case EXITTODESKTOP:
			{
				close();
				break;
			}

			case PLAYOPTIONS:
			{
				startGame(_optionsPanel);
				break;
			}

			case BACKTOGAME:
			{
				togglePaused();
				break;
			}

			case BACKTOMENU:
			{
				backToMenu();
			}
		}
	}

	public void buttonToggled(ImageToggleButton.MyToggleType type, boolean selected)
	{
		switch (type)
		{
			case GRAPHICS:
			{
				_fastGraphics = selected;
				break;
			}
			case DEBUG:
			{
				_drawDebug = selected;
				break;
			}
		}
	}

	private void openOptions()
	{
		_layerdPane.remove(_menuPanel);
		_optionsPanel = new OptionsPanel(this, _drawDebug, _fastGraphics);
		_layerdPane.add(_optionsPanel, 1);
		revalidate();
		repaint();
	}

	private void openInGameOptions()
	{
		_inGameOptionsPanel = new InGameOptionsPanel(this);
		_layerdPane.add(_inGameOptionsPanel, 2);
	}

	private void closeInGameOptions()
	{
		_layerdPane.remove(_inGameOptionsPanel);
	}

	private void backToMenu()
	{
		_layerdPane.removeAll();
		_menuPanel = new MenuPanel(this);
		_layerdPane.add(_menuPanel, 1);
		_inGame = false;
		revalidate();
		repaint();
	}

	public void nibbaIsDead()
	{
		_inGame = false;
		_gameLoop.setPaused(true);
		_endPanel = new endPanel(this);
		_layerdPane.add(_endPanel, 2);
	}
}
