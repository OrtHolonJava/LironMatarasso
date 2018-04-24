import java.awt.event.MouseEvent;

/**
 * MyMouseListener interface for passing mouse events
 * 
 * @author liron
 *
 */
public interface MyMouseListener
{
	void mouseDragged(MouseEvent e);

	void mouseMoved(MouseEvent e);

	void mousePressed(MouseEvent e);

	void mouseReleased(MouseEvent e);
}
