import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class ImageToggleButton extends JToggleButton
{

	public enum MyToggleType
	{
		GRAPHICS, DEBUG
	};

	private MyToggleType _type;

	public ImageToggleButton(String pathFalse, String pathTrue, int x, int y, int width, int height, MyToggleType type, GameFrame frame)
	{
		_type = type;
		setIcon(pathFalse, width, height);
		setBounds(x, y, width, height);

		addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (!isSelected())
				{
					setIcon(pathFalse, width, height);
				}
				else
				{
					setIcon(pathTrue, width, height);
				}
				frame.buttonToggled(_type, isSelected());
			}
		});

	}

	public void setIcon(String path, int width, int height)
	{
		ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource(path));
		Image newimg = icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newimg);
		setMargin(new Insets(0, 0, 0, 0));
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);
		setBorder(null);
		setIcon(icon);
	}

}
