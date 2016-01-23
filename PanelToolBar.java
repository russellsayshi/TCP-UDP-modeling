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
        add(makeIconButton("script", "script", "Scripting tool", "Script"));

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
                            DrawablePath drawpath = new DrawablePath(gp);
                            if(dp.errorIfObjectOffscreen(drawpath)) return;
                            Computer comp = Computer.computerFactory(drawpath, dp,
                                mw.getConsole().getPrintCallback(),
                                mw.getConsole().getErrorCallback());
                            if(comp != null) {
                                drawpath.setComputer(comp);
                                dp.addDrawableObject(drawpath);
                            } else {
                                dp.updateAll();
                            }
                        }
                    }));
                    break;
                    case RECTANGLE:
                    dp.setAction(new CanvasAction("Rectangle", (screen, actual, g) -> {
                        //Mouse pressed
                        pointsScreen.add(screen);
                        pointsActual.add(actual);
                        counter.val = 0;
                    }, (screen, actual, g) -> {
                        Graphics2D g2d = (Graphics2D)g;
                        Point p1 = pointsScreen.get(0);
                        if(counter.val == 1) {
                            Point p2 = pointsScreen.get(1);
                            g2d.setColor(Color.WHITE);
                            g2d.draw(Utility.normalizeRectangle(p1.x, p1.y, p2.x-p1.x, p2.y-p1.y));
                            pointsScreen.remove(1);
                            pointsActual.remove(1);
                        }
                        g2d.setColor(Color.BLACK);
                        Rectangle rect = Utility.normalizeRectangle(p1.x, p1.y, screen.x-p1.x, screen.y-p1.y);
                        g2d.draw(rect);
                        pointsScreen.add(screen);
                        pointsActual.add(actual);
                        counter.val = 1;
                    }, (screen, actual, g) -> {
                        if(pointsActual.size() > 0) {
                            Point p1 = pointsActual.get(0);
                            Rectangle rect = Utility.normalizeRectangle(p1.x, p1.y, actual.x-p1.x, actual.y-p1.y);
                            if(rect.width < 5 || rect.height < 5) {
                                Utility.displayError("Error", "Please draw a bigger rectangle");
                                dp.updateAll();
                                return;
                            }
                            DrawableRectangle drawrect = new DrawableRectangle(rect);
                            if(dp.errorIfObjectOffscreen(drawrect)) return;
                            Computer comp = Computer.computerFactory(drawrect, dp,
                                mw.getConsole().getPrintCallback(),
                                mw.getConsole().getErrorCallback());
                            if(comp != null) {
                                drawrect.setComputer(comp);
                                dp.addDrawableObject(drawrect);
                            } else {
                                dp.updateAll();
                            }
                        }
                    }));
                    break;
                    case OVAL:
                    Utility.displayError("Error", "Not supported yet.");
                    break;
                    case IMAGE:
                    Utility.displayError("Error", "Not supported yet.");
                    break;
                }
            });
        } else if("script".equals(s)) {
            mw.getDisplayPanel().setAction(new CanvasAction("Script", null, null, (screen, actual, g) -> {
                DrawableObject drawable = mw.getDisplayPanel().getObjectAtScreenLocation(screen.x, screen.y);
                if(drawable == null) {
                    Utility.displayError("Error", "The background is not scriptable.");
                } else {
                    Computer comp = drawable.getComputer();
                    if(comp != null) {
                        new ScriptDialog(mw, str -> {
                            if(str != null) {
                                comp.getNode().setScript(str);
                            }
                        }, comp.getNode().getScript(), null).setVisible(true);
                    }
                }
            }));
        } else if("select".equals(s)) {
            //ArrayList<DrawableObject> intersectingO
            mw.getDisplayPanel().setAction(new CanvasAction("Select", (screen, actual, g) -> {
                
            }, (screen, actual, g) -> {
                
            }, (screen, actual, g) -> {
                
            }));
        } else {
            System.out.println("Unknown action: " + s);
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