package sharkdir;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.Timer;
import javax.swing.JPanel;

public class Tar1 extends JPanel implements ActionListener {

	private int _dotX;
	private int _dotY;
	private Point mousePoint;
	private Img _shark;
	private BufferedImage img;

	public Tar1() {
		_dotX = 0;
		_dotY = 0;
		_shark = new Img("shark1.png", 0, 0, 66, 122);
		img = Img.toBufferedImage(_shark.getImage());
		Timer t = new Timer(10, this);
		t.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		mousePoint = MouseInfo.getPointerInfo().getLocation();
		// TODO Auto-generated method stub
		if (_dotX < mousePoint.getX())
			_dotX++;
		else if (_dotX > mousePoint.getX())
			_dotX--;

		if (_dotY < mousePoint.getY())
			_dotY++;
		else if (_dotY > mousePoint.getY())
			_dotY--;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();

		double rotation = 0f;

		int width = getWidth() - 1;
		int height = getHeight() - 1;

		if (mousePoint != null) {

			int x = width / 2;
			int y = height / 2;

			int deltaX = mousePoint.x - x;
			int deltaY = mousePoint.y - y;

			rotation = -Math.atan2(deltaX, deltaY);

			rotation = Math.toDegrees(rotation) + 180;

		}

		int x = (width - img.getWidth()) / 2;
		int y = (height - img.getHeight()) / 2;

		g2d.rotate(Math.toRadians(rotation), width / 2, height / 2);
		g2d.drawImage(img, x, y, this);
		x = width / 2;
		y = height / 2;
		g2d.dispose();
	}
}
