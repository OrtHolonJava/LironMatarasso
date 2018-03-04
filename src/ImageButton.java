import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ImageButton extends JButton
{
	public ImageButton(String path, int x, int y, int width, int height)
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
		setBounds(x, y, width, height);
	}
}
