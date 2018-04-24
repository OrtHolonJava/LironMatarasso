import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * ImageToggleButton for toggle buttons with image
 * 
 * @author liron
 */
public class ImageToggleButton extends JToggleButton
{

	public enum MyToggleType
	{
		GRAPHICS, DEBUG
	};

	private MyToggleType _type;

	/**
	 * Init a new ImageToggleButton object with the following parameters:
	 * 
	 * @param pathFalse
	 * @param pathTrue
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param type
	 * @param frame
	 */
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

	/**
	 * sets the image on the toggle button
	 * 
	 * @param path
	 * @param width
	 * @param height
	 */
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
