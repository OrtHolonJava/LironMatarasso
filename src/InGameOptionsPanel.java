import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class InGameOptionsPanel extends JPanel
{

	private ImageButton _exitToDesktopButton, _backToGameButton, _backToMenuButton;
	private final int BUTTON_WIDTH = 400, BUTTON_HEIGHT = 80;

	public InGameOptionsPanel(GameFrame frame)
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screenSize);
		setFocusable(true);
		setLayout(null);
		grabFocus();
		setBackground(new Color(0, 0, 0, 0.5f));
		_backToGameButton = new ImageButton("images//Buttons//resumeButton.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
											2 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOGAME,
											frame);

		_backToMenuButton = new ImageButton("images//Buttons//exitToMenuButton.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
											4 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOMENU,
											frame);

		_exitToDesktopButton = new ImageButton(	"images//Buttons//exitToDesktopButton.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
												5 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.EXITTODESKTOP, frame);

		add(_backToGameButton);
		add(_backToMenuButton);
		add(_exitToDesktopButton);

	}
}
