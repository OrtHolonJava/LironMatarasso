import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTextPane;

/**
 * EndPanel class for the panel that appears when the player loses
 * 
 * @author liron
 *
 */
public class EndPanel extends MyPanel
{
	private ImageButton _exitToDesktopButton, _backToMenuButton;
	private final int BUTTON_WIDTH = 400, BUTTON_HEIGHT = 80;

	private JTextPane _textArea;

	/**
	 * Init a new EndPanel object with the following parameters:
	 * 
	 * @param frame
	 * @param amountEaten
	 * @param timeSurvived
	 */
	public EndPanel(GameFrame frame, int amountEaten, String timeSurvived)
	{
		super("images//deepOcean.jpg");
		_textArea = new JTextPane();
		_textArea.setForeground(Color.red);
		_textArea.setFont(new Font("TimesRoman", Font.PLAIN, BUTTON_WIDTH / 16));
		_textArea.setText("Amount of fish eaten: " + amountEaten + "\nTime survived: " + timeSurvived);
		_textArea.setOpaque(false);
		_textArea.setBackground(new Color(0, 0, 0, 0));
		_textArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		_textArea.setEditable(false);
		_textArea.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		_textArea.setLocation(_screenSize.width / 2 - BUTTON_WIDTH / 2, 3 * _screenSize.height / 8);

		_backToMenuButton = new ImageButton("images//Buttons//exitToMenuButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
											4 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOMENU,
											frame);

		_exitToDesktopButton = new ImageButton(	"images//Buttons//exitToDesktopButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
												5 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.EXITTODESKTOP, frame);
		add(_textArea);
		add(_backToMenuButton);
		add(_exitToDesktopButton);
	}
}
