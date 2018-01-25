import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;

import images.Img;

public class BlockType {
	private Img _blockImage;
	private int _size, _id;
	private String _path;
	private static LinkedList<String> _imgPaths = new LinkedList<String>(
			Arrays.asList("images//SandBlock2.png", "images/StoneBlock2.png", "images//OneSW.png",
					"images//SandBackground.png", "images//StoneBackground.png"));
	private boolean setSandsFlag = false;

	public BlockType(int size, String path) {
		_size = size;
		_path = path;
		_blockImage = new Img(_path, 0, 0, _size, _size);
	}

	public BlockType(int size, int id) {
		_size = size;
		_path = _imgPaths.get(id - 1);
		_blockImage = new Img(_path, 0, 0, _size, _size);
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
