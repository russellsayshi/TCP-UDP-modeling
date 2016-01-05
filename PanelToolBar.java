import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;
import java.util.ArrayList;

class PanelToolBar extends JToolBar implements ActionListener {
	private int currentImageSize = 32;
    private MainWindow mw;
    private JButton computerButton;
    private ComputerTooltipFrame ctf;

	public PanelToolBar(MainWindow mw) {
		super("Toolbar");
        this.mw = mw;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		addComponents();
	}
	private void addComponents() {
		add(makeIconButton("cursor", "select", "Selection tool", "Select"));
		add(makeIconButton("text", "text", "Draw text on the canvas", "Text"));
        computerButton = makeIconButton("computer", "computer", "Create a new computer", "Add computer");
		add(computerButton);

		add(Box.createHorizontalGlue());

		JButton jb = new JButton("Change size");
		jb.setActionCommand("size");
		jb.setToolTipText("Changes size of icons");
		jb.addActionListener(this);
		add(jb);
	}
	private JButton makeIconButton(String name,
				   String actionCommand,
				   String toolTipText,
				   String altText) {
		//Find image
		String loc = "resources/" + name + "_outline" + currentImageSize + ".png";
		URL imageURL = PanelToolBar.class.getResource(loc);

		//Create and setup button
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if(imageURL != null) { //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {
			button.setText(altText);
			System.err.println("Unable to find resource: " + loc);
		}
		return button;
	}
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		if("size".equals(s)) {
			setMaxDesiredBound(currentImageSize == 32 ? 64 : 32);
            mw.notifyDisplayPanelToUpdate();
        } else if("computer".equals(s)) {
            Point p = computerButton.getLocationOnScreen();
            Dimension d = computerButton.getSize();
            p.translate(d.width/2, d.height);
            if(ctf != null) {
                ctf.dispose();
            }
            ctf = new ComputerTooltipFrame(p.x, p.y, r -> {
                DisplayPanel dp = mw.getDisplayPanel();
                final ArrayList<Point> pointsActual = new ArrayList<>();
                final ArrayList<Point> pointsScreen = new ArrayList<>();
                final MutableInteger counter = new MutableInteger();
                switch(r) {
                    case FREEHAND:
                    dp.setAction(new CanvasAction("Freehand", null, (screen, actual, g) -> {
                        //Mouse dragged
                        pointsScreen.add(screen);
                        pointsActual.add(actual);
                        counter.val++;
                        if(counter.val > 1) {
                            Point p1 = pointsScreen.get(counter.val-2);
                            Point p2 = pointsScreen.get(counter.val-1);
                            g.setColor(Color.BLACK);
                            g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                    }, (screen, actual, g) -> {
                        if(pointsActual.size() > 1) {
                            GeneralPath gp = new GeneralPath();
                            Point point = pointsActual.get(0);
                            gp.moveTo(point.x, point.y);
                            for(int i = 1; i < pointsActual.size(); i++) {
                                point = pointsActual.get(i);
                                gp.lineTo(point.x, point.y);
                            }
                            dp.addDrawableObject(new DrawablePath(gp));
                        }
                    }));
                    break;
                    case RECTANGLE:
                    break;
                    case OVAL:
                    break;
                    case IMAGE:
                    break;
                }
            });
        }
	}
	private void setMaxDesiredBound(int i) {
		if(currentImageSize != i) {
			currentImageSize = i;
			removeAll();
			addComponents();
			revalidate();
			repaint();
		}
	}
}