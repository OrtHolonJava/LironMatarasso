import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class OptionsPanel extends JPanel
{
	private Img _background;
	private ImageToggleButton _graphicsToggle;
	private ImageButton _playButton;
	private final int BUTTON_WIDTH = 600, BUTTON_HEIGHT = 80;

	public OptionsPanel(GameFrame frame, boolean drawDebug, boolean fastGraphics)
	{
		setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_background = new Img("images//menuBackground.jpg", 0, 0, screenSize.width, screenSize.height);
		_graphicsToggle = new ImageToggleButton("images//Buttons//graphicsButtonFancy.png", "images//Buttons//graphicsButtonFast.png",
												screenSize.width / 2 - BUTTON_WIDTH / 2, 2 * screenSize.height / 8, BUTTON_WIDTH,
												BUTTON_HEIGHT, frame, ImageToggleButton.MyToggleType.GRAPHICS);
		_graphicsToggle.setSelected(fastGraphics);

		_playButton =
					new ImageButton("images//Buttons//playButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2, 7 * screenSize.height / 8,
									BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.PLAYOPTIONS, frame);

		add(_graphicsToggle);
		add(_playButton);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paintComponent(g);
		_background.drawImg(g);
	}
}
