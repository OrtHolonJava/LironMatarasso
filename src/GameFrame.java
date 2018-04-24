import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * GameFrame class for the window of the game
 * 
 * @author liron
 *
 */
public class GameFrame extends JFrame
{
	private GamePanel _gamePanel;
	private LinkedList<MyKeyListener> _keyListeners;
	private LinkedList<MyMouseListener> _mouseListeners;
	private GameLoop _gameLoop;
	private MenuPanel _menuPanel;
	private OptionsPanel _optionsPanel;
	private InstructionsPanel _instructionsPanel;
	private PausePanel _pausePanel;
	private EndPanel _endPanel;
	private JLayeredPane _layerdPane;

	private boolean _drawDebug, _fastGraphics, _isPaused, _inGame;

	/**
	 * Init a new GameFrame object
	 */
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

	/**
	 * starts the game and closes the given panel
	 * 
	 * @param panelToClose
	 */
	public void startGame(JPanel panelToClose)
	{
		_isPaused = false;
		_inGame = true;
		_layerdPane.remove(panelToClose);
		_gamePanel = new GamePanel(_drawDebug, _fastGraphics, this);
		_layerdPane.add(_gamePanel, 1);
		addMyMouseListenerToList(_gamePanel);
		addMyKeyListenerToList(_gamePanel);
		revalidate();
		repaint();
		_gameLoop = new GameLoop(_gamePanel);
		_gameLoop.startGame();
	}

	/**
	 * shuts down the game
	 */
	public void close()
	{
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	/**
	 * adds a listener to the mouse events
	 * 
	 * @param e
	 */
	public void addMyMouseListenerToList(MyMouseListener e)
	{
		_mouseListeners.add(e);
	}

	/**
	 * adds a listener to the keyboard events
	 * 
	 * @param e
	 */
	public void addMyKeyListenerToList(MyKeyListener e)
	{
		_keyListeners.add(e);
	}

	/**
	 * toggle the pause of the game
	 */
	public void togglePaused()
	{
		_isPaused = !_isPaused;
		_gameLoop.setPaused(_isPaused);
		if (_isPaused)
		{
			openPausePanel();
		}
		else
		{
			closePausePanel();
		}
	}

	/**
	 * 
	 * @return a key adapter
	 */
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

	/**
	 * 
	 * @return a mouse adapter
	 */
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

	/**
	 * Handles an event called from a button according to the type
	 * 
	 * @param type
	 */
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
				break;
			}
			case INSTRUCTIONS:
			{
				openInstructions();
				break;
			}
			case NOTHING:
			{
				break;
			}
			default:
			{
				break;
			}
		}
	}

	/**
	 * Handles an event called from a toggle button according to the type
	 * 
	 * @param type
	 * @param selected
	 */
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

	/**
	 * opens the instructions panel
	 */
	private void openInstructions()
	{
		_layerdPane.remove(_menuPanel);
		_instructionsPanel = new InstructionsPanel(this);
		_layerdPane.add(_instructionsPanel, 1);
		revalidate();
		repaint();
	}

	/**
	 * opens the options panel
	 */
	private void openOptions()
	{
		_layerdPane.remove(_menuPanel);
		_optionsPanel = new OptionsPanel(this, _drawDebug, _fastGraphics);
		_layerdPane.add(_optionsPanel, 1);
		revalidate();
		repaint();
	}

	/**
	 * opens the pause panel
	 */
	private void openPausePanel()
	{
		_pausePanel = new PausePanel(this);
		_layerdPane.add(_pausePanel, 2);
	}

	/**
	 * closes the pause panel
	 */
	private void closePausePanel()
	{
		_layerdPane.remove(_pausePanel);
	}

	/**
	 * returns to the menu
	 */
	private void backToMenu()
	{
		_layerdPane.removeAll();
		_menuPanel = new MenuPanel(this);
		_layerdPane.add(_menuPanel, 1);
		_inGame = false;
		revalidate();
		repaint();
	}

	/**
	 * stops the game and opens the end game panel if the player died
	 * 
	 * @param amountEaten
	 * @param timeSurvived
	 */
	public void playerIsDead(int amountEaten, String timeSurvived)
	{
		_inGame = false;
		_gameLoop.setPaused(true);
		_endPanel = new EndPanel(this, amountEaten, timeSurvived);
		_layerdPane.add(_endPanel, 2);
	}
}
