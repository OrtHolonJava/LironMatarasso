import java.awt.Graphics;

/**
 * InstructionsPanel class for the instruction panel
 * 
 * @author liron
 */
public class InstructionsPanel extends MyPanel
{
	private Img _info;
	private ImageButton _backToMenuButton;

	private final int BUTTON_WIDTH = 600, BUTTON_HEIGHT = 80, EDGE = 80;

	/**
	 * Init a new InstructionsPanel object with the following parameters:
	 * 
	 * @param frame
	 */
	public InstructionsPanel(GameFrame frame)
	{
		super("images//sharkPicture1.jpg");
		_info = new Img("images//instructions0.png", EDGE, EDGE, _screenSize.width - 2 * EDGE, _screenSize.height - 2 * EDGE);
		_backToMenuButton = new ImageButton("images//Buttons//exitToMenuButton.png", _screenSize.width / 2 - BUTTON_WIDTH / 2,
											7 * _screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT, ImageButton.MyButtonType.BACKTOMENU,
											frame);
		add(_backToMenuButton);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		_info.drawImg(g);
	}
}
