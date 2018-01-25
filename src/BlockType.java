import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;

import images.Img;

public class BlockType {
	private Img _blockImage;
	private int _size, _id;
	private static LinkedList<String> _imgPaths = new LinkedList<String>(
			Arrays.asList("images//SandBlock2.png", "images/StoneBlock2.png", "images//OneSW.png",
					"images//SandBackground.png", "images//StoneBackground.png"));
	private boolean setSandsFlag = false;

	public BlockType(int size, int id) {
		if (!setSandsFlag) {
			setSands();
			setSandsFlag = true;
		}
		_size = size;
		_id = id;
		_blockImage = new Img(_imgPaths.get(id - 1), 0, 0, _size, _size);
	}

	public void setSands() {
		for (int i = 0; i < 48; i++) {
			_imgPaths.add(String.format("images//SandParts//image_part_%03d.png", (i + 1)));
		}
	}

	public void paintAt(Graphics g, int x, int y) {
		_blockImage.setImgCords(x * _size, y * _size);
		_blockImage.drawImg(g);
	}

	public static void paintSand(Graphics g, int x, int y, int index) {
		new Img(_imgPaths.get(index - 1 + 5), x * 40, y * 40, 40, 40).drawImg(g);
	}

	public Img getBlockImage() {
		return _blockImage;
	}

	public void setBlockImage(Img blockImage) {
		_blockImage = blockImage;
	}

	public int getSize() {
		return _size;
	}

	public void setSize(int size) {
		_size = size;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		_id = id;
	}

}
