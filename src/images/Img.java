package images;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

/**
 * Img class for saving an image and its info
 * 
 * @author PC05
 *
 */
public class Img {
	private Image _image;
	private BufferedImage _bImage;
	private int _x, _y, _width, _height;

	/**
	 * Init a new Img object with the following parameters:
	 * 
	 * @param path
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public Img(String path, int x, int y, int width, int height) {
		try {
			_image = new ImageIcon(this.getClass().getClassLoader().getResource(path)).getImage();
			setImgCords(x, y);
			setImgSize(width, height);
			_bImage = toBufferedImage(_image);
			_bImage = resize(_bImage, width, height);
		} catch (NullPointerException e) {
			System.out.println("image: " + path + " doesnt exist");
		}
	}

	/**
	 * draw the image
	 * 
	 * @param g
	 */
	public void drawImg(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(_image, _x, _y, _width, _height, null);
	}

	/**
	 * set the image coordinations in the GUI
	 * 
	 * @param x
	 * @param y
	 */
	public void setImgCords(int x, int y) {
		this._x = x;
		this._y = y;
	}

	/**
	 * set the image size
	 * 
	 * @param width
	 * @param _height
	 */
	public void setImgSize(int width, int height) {
		_width = width;
		_height = height;
	}

	/**
	 * Creates an empty image with transparency
	 * 
	 * @param width
	 *            The width of required image
	 * @param height
	 *            The height of required image
	 * @return The created image
	 */
	public static Image getEmptyImage(int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return toImage(img);
	}

	public BufferedImage getbImage() {
		return _bImage;
	}

	public void setbImage(BufferedImage bImage) {
		_bImage = bImage;
	}

	/**
	 * Converts a given Image into a BufferedImage
	 * 
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}
		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		// Return the buffered image
		return bimage;
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	/**
	 * Converts a given BufferedImage into an Image
	 * 
	 * @param bimage
	 *            The BufferedImage to be converted
	 * @return The converted Image
	 */
	public static Image toImage(BufferedImage bimage) {
		// Casting is enough to convert from BufferedImage to Image
		Image img = (Image) bimage;
		return img;
	}

	public Image getImage() {
		return _image;
	}

	public void setImage(Image image) {
		_image = image;
	}

	public int getX() {
		return _x;
	}

	public void setX(int x) {
		this._x = x;
	}

	public int getY() {
		return _y;
	}

	public void setY(int y) {
		this._y = y;
	}

	public int getWidth() {
		return _width;
	}

	public void setWidth(int width) {
		_width = width;
	}

	public int getHeight() {
		return _height;
	}

	public void setHeight(int height) {
		_height = height;
	}
}
