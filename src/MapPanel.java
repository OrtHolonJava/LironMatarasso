import java.awt.Graphics;

import javax.swing.JPanel;

import images.Img;

import map.Map;

public class MapPanel extends JPanel {
	private String _mapFile;
	private int _size;
	private int _sizeW;
	private int _blockSize;
	private Img _backgroundImg;
	private Map _map;
	private Img _sandBlock;
	private Img _stoneBlock;
	private Img _seaweedBlock;
	private Img _sandBackground;
	private Img _stoneBackground;

	public MapPanel() {
		_mapFile = "MapFiles//GetPicture_20171206112109.xml";
		_size = Map.getElementCountByName(_mapFile,"Line");
		_sizeW = Map.getElementCountByName(_mapFile,"Area")/_size;
		_blockSize = 7;
		_backgroundImg = new Img("images//Background.jpg", 0, 0, _sizeW * _blockSize, _size * _blockSize);
		_sandBlock = new Img("images//SandBlock2.png", 0, 0, _blockSize, _blockSize);
		_stoneBlock = new Img("images//‏‏StoneBlock2.png", 0, 0, _blockSize, _blockSize);
		_seaweedBlock= new Img("images//OneSW.png", 0, 0, _blockSize, _blockSize);
		_sandBackground= new Img("images//SandBackground.png", 0, 0, _blockSize, _blockSize);
		_stoneBackground= new Img("images//StoneBackground.png", 0, 0, _blockSize, _blockSize);
		_map = new Map(_size, _sizeW, _mapFile);

	}

	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		_backgroundImg.drawImg(g);
		for (int i = 0; i < _size; i++) {
			for (int j = 0; j < _sizeW; j++) {
				switch (_map.get_map()[i][j]) {
				case 1: {
					_sandBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_sandBlock.drawImg(g);
					break;
				}
				case 2: {
					_stoneBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_stoneBlock.drawImg(g);
					break;
				}
				case 3: {
					_seaweedBlock.setImgCords((j * _blockSize), (i) * _blockSize);
					_seaweedBlock.drawImg(g);
					break;
				}
				case 4: {
					_sandBackground.setImgCords((j * _blockSize), (i) * _blockSize);
					_sandBackground.drawImg(g);
					break;
				}
				case 5: {
					_stoneBackground.setImgCords((j * _blockSize), (i) * _blockSize);
					_stoneBackground.drawImg(g);
					break;
				}
				}
			}
		}
	}
}
