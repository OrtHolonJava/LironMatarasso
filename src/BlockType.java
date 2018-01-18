import java.awt.Graphics;

import images.Img;

public class BlockType {
	private Img _blockImage;
	private int _size, _id;
	private static String _imgPaths[] = { "images//SandBlock2.png", "images//þþStoneBlock2.png", "images//OneSW.png",
			"images//SandBackground.png", "images//StoneBackground.png" };

	public BlockType(int size, int id) {
		_size = size;
		_id = id;
		_blockImage = new Img(_imgPaths[id - 1], 0, 0, _size, _size);
	}

	public void paintAt(Graphics g, int x, int y) {
		_blockImage.setImgCords(x * _size, y * _size);
		_blockImage.drawImg(g);
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
