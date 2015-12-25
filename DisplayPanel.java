import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Container for bottom part of layout, with canvas, scrollbars, etc. */
public class DisplayPanel extends JPanel {
	JScrollBar rightScroller;
	JScrollBar bottomScroller;

	public DisplayPanel(int canvWid, int canvHei) {
		//Initialize layout manager
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		//Add central frame
		c.fill = GridBagConstraints.BOTH; //fill horizontal + vertical
		c.weightx = 1.0; //large x weight
		c.weighty = 1.0; //large y weight
		c.gridx = 0; //left
		c.gridy = 0; //top
		CentralCanvas center = new CentralCanvas(canvWid, canvHei);
		add(center, c);

		//Add right scrollbar
		c.weightx = 0.0; //small x weight
		c.weighty = 1.0; //large y weight
		c.gridx = 1; //right
		c.gridy = 0; //top
		rightScroller = new JScrollBar(
			JScrollBar.VERTICAL,
			0,
			100,
			0,
			200
		);
		add(rightScroller, c);

		//Add bottom scrollbar
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.gridx = 0; //left
		c.gridy = 1; //bottom
		bottomScroller = new JScrollBar(
			JScrollBar.HORIZONTAL,
			0,
			100,
			0,
			200
		);
		add(bottomScroller, c);

		/*//Add bottom right button
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 1; //right
		c.gridy = 1; //bottom
		JButton button4 = new JButton("!");
		add(button4, c);*/
	}
}