import javax.swing.JFrame;

public class MapFrame extends JFrame {
	private MapPanel _mapPanel;

	public MapFrame() {
		_mapPanel = new MapPanel();
		add(_mapPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(2000,1000);
		setVisible(true);
	}
}
