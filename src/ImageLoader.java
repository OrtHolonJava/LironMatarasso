import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

public class ImageLoader
{
	private BufferedImage[] _playerFrames;

	public ImageLoader()
	{
		_playerFrames = setFrames("images\\SharkFrames\\");
	}

	public BufferedImage[] setFrames(String path)
	{
		BufferedImage[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new BufferedImage[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				// System.out.println(path + child.getName());
				arr[counter++] = Img.toBufferedImage(new ImageIcon(this	.getClass().getClassLoader()
																		.getResource(path + child.getName())).getImage());
			}
		}
		return arr;
	}

	public BufferedImage[] getPlayerFrames()
	{
		return _playerFrames;
	}

	public void setPlayerFrames(BufferedImage[] playerFrames)
	{
		_playerFrames = playerFrames;
	}

}
