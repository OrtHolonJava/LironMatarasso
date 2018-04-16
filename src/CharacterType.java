import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;

public abstract class CharacterType
{
	private int _width, _height, _type;
	private double _baseSpeed;
	private BufferedImage[] _frames;

	private final int PLAYER_WIDTH = 8 * BlockType.getSize() / 10, PLAYER_HEIGHT = 19 * BlockType.getSize() / 10,
			GRAY_FISH_WIDTH = PLAYER_WIDTH / 4, GRAY_FISH_HEIGHT = PLAYER_HEIGHT / 4, SWORD_FISH_WIDTH = 7 * PLAYER_WIDTH / 10,
			SWORD_FISH_HEIGHT = PLAYER_HEIGHT, YELLOW_FISH_WIDTH = 2 * GRAY_FISH_WIDTH, YELLOW_FISH_HEIGHT = YELLOW_FISH_WIDTH;

	protected CharacterType(int type)
	{
		_type = type;
		switch (_type)
		{
			case 0: // Player shark
			{
				_width = PLAYER_WIDTH;
				_height = PLAYER_HEIGHT;
				_frames = setFrames("images//Frames//Shark//", _width, _height);
				_baseSpeed = 8.0 / 60 * Block.getSize();
				break;
			}
			case 1: // Gray fish
			{
				_width = GRAY_FISH_WIDTH;
				_height = GRAY_FISH_HEIGHT;
				_frames = setFrames("images//Frames//GrayFish//", _width, _height);
				_baseSpeed = 2.0 / 60 * Block.getSize();
				break;
			}
			case 2: // Yellow fish
			{
				_width = YELLOW_FISH_WIDTH;
				_height = YELLOW_FISH_HEIGHT;
				_frames = setFrames("images//Frames//YellowFish//", _width, _height);
				_baseSpeed = 2.0 / 60 * Block.getSize();
				break;
			}
			case 3: // Pink fish
			{
				_width = GRAY_FISH_WIDTH;
				_height = GRAY_FISH_HEIGHT;
				_frames = setFrames("images//Frames//PinkFish//", _width, _height);
				_baseSpeed = 2.0 / 60 * Block.getSize();
				break;
			}
			case 10: // Sword Fish
			{
				_width = SWORD_FISH_WIDTH;
				_height = SWORD_FISH_HEIGHT;
				_frames = setFrames("images//Frames//SwordFish//", _width, _height);
				_baseSpeed = 7.0 / 60 * Block.getSize();
				break;
			}
		}
	}

	public BufferedImage[] setFrames(String path, int width, int height)
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
				arr[counter] = Img.toBufferedImage(new ImageIcon(this	.getClass().getClassLoader()
																		.getResource(path + child.getName())).getImage());
				arr[counter++] = Img.resize(arr[counter - 1], width, height);
			}
		}
		return arr;
	}

	public int getWidth()
	{
		return _width;
	}

	public void setWidth(int width)
	{
		_width = width;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setHeight(int height)
	{
		_height = height;
	}

	public double getBaseSpeed()
	{
		return _baseSpeed;
	}

	public void setBaseSpeed(double baseSpeed)
	{
		_baseSpeed = baseSpeed;
	}

	public BufferedImage[] getFrames()
	{
		return _frames;
	}

	public void setFrames(BufferedImage[] frames)
	{
		_frames = frames;
	}
}
