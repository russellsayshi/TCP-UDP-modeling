import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class CentralCanvas extends JPanel implements MouseListener {
	private final int width;
	private final int height;
	BufferedImage img = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);

	public CentralCanvas(int width, int height) {
		this.width = width;
		this.height = height;
		addMouseListener(this);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		Graphics g = img.getGraphics();
		g.fillOval(x, y, 3, 3);
		g.dispose();
	}
		
}