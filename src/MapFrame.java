import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.JFrame;

public class MapFrame extends JFrame
{
	private MapPanel _mapPanel;
	private LinkedList<MyMouseListener> _mouseListeners;

	public MapFrame()
	{
		MouseAdapter mouseAdapter = getMouseAdapter();
		_mouseListeners = new LinkedList<MyMouseListener>();
		setLayout(new BorderLayout());
		_mapPanel = new MapPanel();
		addListener(_mapPanel);
		add(_mapPanel, BorderLayout.CENTER);
		addMouseMotionListener(mouseAdapter);
		addMouseListener(mouseAdapter);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}

	public void startGame()
	{
		_mapPanel.startGame();
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
