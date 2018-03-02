import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class MenuPanel extends JPanel
{
	private ImageButton _playButton, _exitButton;
	private GameFrame _frame;
	private Img _background;
	private final int BUTTON_WIDTH = 642, BUTTON_HEIGHT = 65;

	public MenuPanel(GameFrame frame)
	{
		_frame = frame;
		setLayout(null);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		_playButton = new ImageButton(	"images//Buttons//playButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2,
										3 * screenSize.height / 8, BUTTON_WIDTH, BUTTON_HEIGHT);
		_background = new Img("images//menuBackground.jpg", 0, 0, screenSize.width, screenSize.height);
		_playButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				_frame.startGame();
			}
		});
		_exitButton = new ImageButton(	"images//Buttons//exitButtonM.png", screenSize.width / 2 - BUTTON_WIDTH / 2, screenSize.height / 2,
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

	@Override
	protected void paintComponent(Graphics g)
	{
		// TODO Auto-generated method stub
		super.paintComponent(g);
		_background.drawImg(g);
	}
}
