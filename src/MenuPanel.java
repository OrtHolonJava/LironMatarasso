import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class MenuPanel extends JPanel
{
	private ImageButton _playButton, _exitToDesktopButton, _optionsButton;
	private Img _background;
	private final int BUTTON_WIDTH = 800, BUTTON_HEIGHT = 60;

	public MenuPanel(GameFrame frame)
	{
		setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		_background = new Img("images//menuBackground.jpg", 0, 0, screenSize.width, screenSize.height);
		_playButton = new ImageButton(	"images//Buttons//playButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
										3 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.PLAY, frame);
		_optionsButton = new ImageButton(	"images//Buttons//optionsButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
											4 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.OPTIONS,
											frame);
		_exitToDesktopButton = new ImageButton(	"images//Buttons//exitButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
												5 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.EXITTODESKTOP, frame);
		add(_playButton);
		add(_optionsButton);
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
