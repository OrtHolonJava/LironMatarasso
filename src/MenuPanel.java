import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class MenuPanel extends JPanel
{
	private ImageButton _playButton, _exitButton;
	private GameFrame _frame;
	private final int BUTTON_WIDTH = 400, BUTTON_HEIGHT = BUTTON_WIDTH / 2;

	public MenuPanel(GameFrame frame)
	{
		_frame = frame;
		setLayout(null);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_playButton = new ImageButton(	"images//Buttons//playbutton.png", screenSize.width / 2 - BUTTON_WIDTH / 2, screenSize.height / 4,
										BUTTON_WIDTH, BUTTON_HEIGHT);
		_playButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_frame.startGame();
			}
		});
		_exitButton = new ImageButton(	"images//Buttons//quitbutton.png", screenSize.width / 2 - BUTTON_WIDTH / 2, screenSize.height / 2,
										BUTTON_WIDTH, BUTTON_HEIGHT);
		_exitButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_frame.close();
			}
		});
		add(_playButton);
		add(_exitButton);

	}
}
