import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Container for bottom part of layout, with canvas, scrollbars, etc. */
public class DisplayPanel extends JPanel {
    private CentralCanvas center;
	JScrollBar rightScroller;
	JScrollBar bottomScroller;
    //To prevent excess repaints
    private boolean ignoreNextVerticalScroll = false;
    private boolean ignoreNextHorizontalScroll = false;

	public DisplayPanel(int canvWid, int canvHei) {
        addComponents(canvWid, canvHei);
        initializeListeners();
	}
    
    public void initializeListeners() {
        //Initialize listeners
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateAll();
            }
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
            @Override
            public void componentMoved(ComponentEvent e) {}
        });
        
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mwe) {
                if(mwe.isControlDown()) {
                    ignoreNextVerticalScroll();
                    ignoreNextHorizontalScroll();
                    center.correctZoomPosition(mwe.getWheelRotation(), mwe.getX(), mwe.getY());
                    center.modifyZoom(mwe.getWheelRotation());
                } else if(mwe.isShiftDown()) {
                    center.horizontalScrollChanged(mwe.getUnitsToScroll());
                } else {
                    center.verticalScrollChanged(mwe.getUnitsToScroll());
                }
            }
        });
        
        rightScroller.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(ignoreNextVerticalScroll) {
                    ignoreNextVerticalScroll = false;
                    return;
                }
                center.redrawImage(false);
                center.repaint();
            }
        });
        
        bottomScroller.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                if(ignoreNextHorizontalScroll) {
                    ignoreNextHorizontalScroll = false;
                    return;
                }
                center.redrawImage(false);
                center.repaint();
            }
        });
    }
    
    public void addComponents(int canvWid, int canvHei) {
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
		center = new CentralCanvas(canvWid, canvHei, this);
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
    
    public void updateAll() {
        center.regenerateImage(true);
        center.repaint();
    }
    
    public JScrollBar getHorizontalScrollBar() {
        return bottomScroller;
    }
    
    public JScrollBar getVerticalScrollBar() {
        return rightScroller;
    }
    
    public void updateVerticalScrollWithDelta(int delta) {
        int currentVal = rightScroller.getValue();
        currentVal += delta;
        if(currentVal < 0) {
            rightScroller.setValue(0);
        } else if(currentVal >= center.getSpaceHeight()) {
            rightScroller.setValue(center.getSpaceHeight() - 1);
        } else {
            rightScroller.setValue(currentVal);
        }
    }
    
    public void updateHorizontalScrollWithDelta(int delta) {
        int currentVal = bottomScroller.getValue();
        currentVal += delta;
        if(currentVal < 0) {
            bottomScroller.setValue(0);
        } else if(currentVal >= center.getSpaceWidth()) {
            bottomScroller.setValue(center.getSpaceWidth() - 1);
        } else {
            bottomScroller.setValue(currentVal);
        }
    }
    
    public void ignoreNextHorizontalScroll() {
        ignoreNextHorizontalScroll = true;
    }
    
    public void ignoreNextVerticalScroll() {
        ignoreNextVerticalScroll = true;
    }
}