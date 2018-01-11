package images;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class PlayerImg extends Img {
	private Image _mirrorImage;
	private BufferedImage _bImgShark;
	private BufferedImage _bImgSharkRev;

	public PlayerImg(String path, String mirrorPath, int x, int y, int width, int height) {
		super(path, x, y, width, height);
		_mirrorImage = new ImageIcon(this.getClass().getClassLoader().getResource(mirrorPath)).getImage();
	}

	public void rotateAndPaint(double angle, Graphics g) {

		_bImgShark = Img.toBufferedImage(_image);
		_bImgShark = Img.resize(_bImgShark, _width, _height);
		_bImgSharkRev = Img.toBufferedImage(_mirrorImage);
		_bImgSharkRev = Img.resize(_bImgSharkRev, _width, _height);
		BufferedImage use = _bImgShark;
		Graphics2D g2d = (Graphics2D) g.create();
		use = (angle < 180) ? _bImgShark : _bImgSharkRev;
		Rectangle rect = new Rectangle((int) (x - use.getWidth() / 2), (int) (y - use.getHeight() / 2), use.getWidth(),
				use.getHeight());
		// g2d.setColor(Color.orange);
		// g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.rotate(Math.toRadians(angle), x, y);
		g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
		g2d.drawImage(use, (int) x - use.getWidth() / 2, (int) y - use.getHeight() / 2, null);
		g2d.dispose();
	}
}
