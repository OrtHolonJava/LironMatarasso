import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton
{

	public enum MyButtonType
	{
		PLAY, OPTIONS, EXIT, PLAYOPTIONS
	};

	private MyButtonType _type;

	public ImageButton(String path, int x, int y, int width, int height, MyButtonType type, GameFrame frame)
	{
		_type = type;
		ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource(path));
		Image newimg = icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		setMargin(new Insets(0, 0, 0, 0));
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setBorder(null);
		setIcon(icon);
		setBounds(x, y, width, height);
		addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.buttonPressed(_type);
			}
		});
	}

	public MyButtonType getType()
	{
		return _type;
	}

	public void setType(MyButtonType type)
	{
		_type = type;
	}
}
