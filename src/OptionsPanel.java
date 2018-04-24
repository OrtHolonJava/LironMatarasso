/**
 * InstructionsPanel class for the instruction panel
 * 
 * @author liron
 */
public class OptionsPanel extends MyPanel
{
	private ImageToggleButton _graphicsToggle, _debugToggle;
	private ImageButton _backToMenu;
	private final int BUTTON_WIDTH = 600, BUTTON_HEIGHT = 80;

	/**
	 * Init a new OptionsPanel object with the following parameters:
	 * 
	 * @param frame
	 * @param drawDebug
	 * @param fastGraphics
	 */
	public OptionsPanel(GameFrame frame, boolean drawDebug, boolean fastGraphics)
	{
		super("images//sharkPicture2.jpg");
		_graphicsToggle = new ImageToggleButton("images//Buttons//graphicsButtonFancy.png", "images//Buttons//graphicsButtonFast.png",
												_screenSize.width / 2 - BUTTON_WIDTH / 2, 2 * _screenSize.height / 8, BUTTON_WIDTH,
												BUTTON_HEIGHT, ImageToggleButton.MyToggleType.GRAPHICS, frame);

		_debugToggle = new ImageToggleButton(	"images//Buttons//debugButtonOff.png", "images//Buttons//debugButtonOn.png",
												_screenSize.width / 2 - BUTTON_WIDTH / 2, 3 * _screenSize.height / 8, BUTTON_WIDTH,
												BUTTON_HEIGHT, ImageToggleButton.MyToggleType.DEBUG, frame);
		_backToMenu =
					new ImageButton("images//Buttons//exitToMenuButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
									7 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOMENU, frame);
		add(_graphicsToggle);
		if (fastGraphics)
			_graphicsToggle.doClick();
		add(_debugToggle);
		if (drawDebug)
			_debugToggle.doClick();
		add(_backToMenu);
	}
}
