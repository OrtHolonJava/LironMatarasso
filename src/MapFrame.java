import java.awt.BorderLayout;

import javax.swing.JFrame;

public class MapFrame extends JFrame {
	private MapPanel _mapPanel;

	public MapFrame() {
		setLayout(new BorderLayout());
		_mapPanel = new MapPanel();
		add(_mapPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setVisible(true);
	}
}
