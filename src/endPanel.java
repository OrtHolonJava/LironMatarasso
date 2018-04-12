import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class endPanel extends JPanel
{
	private Img _background;
	private ImageButton _exitToDesktopButton, _backToMenuButton;
	private final int BUTTON_WIDTH = 400, BUTTON_HEIGHT = 80;

	public endPanel(GameFrame frame)
	{
		setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		_background = new Img("images//deathScreen.jpg", 0, 0, screenSize.width, screenSize.height);
		setFocusable(true);
		grabFocus();
		setBackground(new Color(0, 0, 0, 0.5f));
		_backToMenuButton = new ImageButton("images//Buttons//exitToMenuButton.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
											4 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOMENU,
											frame);

		_exitToDesktopButton = new ImageButton(	"images//Buttons//exitToDesktopButton.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
												5 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.EXITTODESKTOP, frame);

		add(_backToMenuButton);
		add(_exitToDesktopButton);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paintComponent(g);
		_background.drawImg(g);
	}
}
