import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;

/**
 * MyPanel class for a basic menu style panel
 * 
 * @author liron
 *
 */
public abstract class MyPanel extends JPanel
{
	protected Img _background;
	protected Dimension _screenSize;

	/**
	 * Init a new MyPanel object with the following parameters:
	 * 
	 * @param backgroundPath
	 */
	public MyPanel(String backgroundPath)
	{
		setLayout(null);
		setFocusable(true);
		grabFocus();
		_screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(_screenSize);
		_background = new Img(backgroundPath, 0, 0, _screenSize.width, _screenSize.height);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		_background.drawImg(g);
	}
}
