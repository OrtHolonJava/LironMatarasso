import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame
{
	private MapPanel _mapPanel;
	private LinkedList<MyMouseListener> _mouseListeners;
	private GameLoop _gameLoop;
	private MenuPanel _menuPanel;
	private OptionsPanel _optionsPanel;

	private boolean _drawDebug, _fastGraphics;

	public GameFrame()
	{
		_drawDebug = false;
		_fastGraphics = false;
		MouseAdapter mouseAdapter = getMouseAdapter();
		_mouseListeners = new LinkedList<MyMouseListener>();
		setLayout(new BorderLayout());
		_menuPanel = new MenuPanel(this);
		add(_menuPanel, BorderLayout.CENTER);
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}

	public boolean panel = true;

	public void startGame(JPanel panelToClose)
	{
		remove(panelToClose);
		_mapPanel = new MapPanel(_drawDebug, _fastGraphics);
		add(_mapPanel, BorderLayout.CENTER);
		addListener(_mapPanel);
		revalidate();
		repaint();
		_gameLoop = new GameLoop(_mapPanel);
		_gameLoop.startGame();
	}

	public void close()
	{
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public void addListener(MyMouseListener e)
	{
		_mouseListeners.add(e);
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
			case EXIT:
			{
				close();
				break;
			}

			case PLAYOPTIONS:
			{
				startGame(_optionsPanel);
				break;
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
		remove(_menuPanel);
		_optionsPanel = new OptionsPanel(this, _drawDebug, _fastGraphics);
		add(_optionsPanel, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

}
