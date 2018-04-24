/**
 * MenuPanel class for the menu panel
 * 
 * @author liron
 *
 */
public class MenuPanel extends MyPanel
{
	private ImageButton _playButton, _instructionsButton, _exitToDesktopButton, _optionsButton;
	private final int BUTTON_WIDTH = 800, BUTTON_HEIGHT = 60;

	/**
	 * Init a new MenuPanel object with the following parameters:
	 * 
	 * @param frame
	 */
	public MenuPanel(GameFrame frame)
	{
		super("images//sharkPicture0.png");
		_playButton = new ImageButton(	"images//Buttons//playButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
										2 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.PLAY, frame);
		_instructionsButton = new ImageButton(	"images//Buttons//instructionsButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
												3 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.INSTRUCTIONS, frame);
		_optionsButton = new ImageButton(	"images//Buttons//optionsButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
											4 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.OPTIONS,
											frame);
		_exitToDesktopButton = new ImageButton(	"images//Buttons//exitButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
												5 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT,
												ImageButton.MyButtonType.EXITTODESKTOP, frame);
		add(_playButton);
		add(_instructionsButton);
		add(_optionsButton);
		add(_exitToDesktopButton);
	}
}
