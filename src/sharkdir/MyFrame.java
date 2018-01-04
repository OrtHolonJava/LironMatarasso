package sharkdir;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MyFrame extends JFrame {

	public MyFrame(JPanel p, String name, int x, int y) {
		super(name);
		add(p);
		setLocation(x, y);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 900);
		setVisible(true);
	}
}
