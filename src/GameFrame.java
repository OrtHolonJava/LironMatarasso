import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.LinkedList;

import javax.swing.JFrame;

public class GameFrame extends JFrame
{
	private MapPanel _mapPanel;
	private LinkedList<MyMouseListener> _mouseListeners;
	private GameLoop _gameLoop;
	private MenuPanel _menuPanel;

	private GayLoop _gayLoop;
	private MapCanvas _mapCanvas;

	public GameFrame()
	{
		MouseAdapter mouseAdapter = getMouseAdapter();
		_mouseListeners = new LinkedList<MyMouseListener>();
		setLayout(new BorderLayout());
		_mapPanel = new MapPanel();
		_mapCanvas = new MapCanvas();
		_menuPanel = new MenuPanel(this);
		_gameLoop = new GameLoop(_mapPanel);
		_gayLoop = new GayLoop(_mapCanvas);
		addListener(_mapPanel);
		addListener(_mapCanvas);
		add(_menuPanel, BorderLayout.CENTER);
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}

	public boolean panel = true;

	public void startGame()
	{
		remove(_menuPanel);
		if (panel)
			add(_mapPanel, BorderLayout.CENTER);
		else add(_mapCanvas, BorderLayout.CENTER);
		revalidate();
		repaint();
		if (panel)
			_gameLoop.startGame();
		else _gayLoop.startGame();
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
			public void mouseDragged(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mouseDragged(e);
				}
			}

			public void mouseMoved(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mouseMoved(e);
				}
			}

			public void mousePressed(MouseEvent e)
			{
				for (MyMouseListener l : _mouseListeners)
				{
					l.mousePressed(e);
				}
			}

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
}
