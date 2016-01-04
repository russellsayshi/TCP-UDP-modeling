import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.URL;

public class PanelToolBar extends JToolBar implements ActionListener, MouseListener {
	private int currentImageSize = 32;
    private MainWindow mw;
    private Point lastKnownMouseLoc = new Point(0, 0);

	public PanelToolBar(MainWindow mw) {
		super("Toolbar");
        this.mw = mw;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        addMouseListener(this);
		addComponents();
	}
	private void addComponents() {
		add(makeIconButton("cursor", "select", "Selection tool", "Select"));
		add(makeIconButton("text", "text", "Draw text on the canvas", "Text"));
		add(makeIconButton("computer", "computer", "Create a new computer", "Add computer"));

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
		//button.addActionListener(this);

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
        } else if("computer".equals(this)) {
            new ComputerTooltipFrame(lastKnownMouseLoc.x, lastKnownMouseLoc.y, null);
        }
        mw.notifyDisplayPanelToUpdate();
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
    
    @Override
    public void mouseClicked(MouseEvent me) {
        Point locationOnScreen = getLocationOnScreen();
        locationOnScreen.translate(me.getX(), me.getY());
        lastKnownMouseLoc = locationOnScreen;
    }
    @Override
    public void mouseExited(MouseEvent me) {}
    @Override
    public void mouseEntered(MouseEvent me) {}
    @Override
    public void mouseReleased(MouseEvent me) {}
    @Override
    public void mousePressed(MouseEvent me) {}
}