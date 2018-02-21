import java.io.File;

public class ImageLoader
{
	private Img[] _playerFrames;

	public ImageLoader()
	{
		_playerFrames = setFrames("images\\SharkFrames\\");
	}

	public Img[] setFrames(String path)
	{
		Img[] arr = null;
		File dir = new File(System.getProperty("user.dir") + "\\bin\\" + path);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null)
		{
			arr = new Img[directoryListing.length];
			int counter = 0;
			for (File child : directoryListing)
			{
				// System.out.println(path + child.getName());
				arr[counter++] = new Img(path + child.getName(), 0, 0, 100, 100);
			}
		}
		return arr;
	}

	public Img[] getPlayerFrames()
	{
		return _playerFrames;
	}

	public void setPlayerFrames(Img[] playerFrames)
	{
		_playerFrames = playerFrames;
	}

}
