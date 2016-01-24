import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.function.*;
import java.net.URL;
import java.util.ArrayList;

class PanelToolBar extends JToolBar implements ActionListener {
	private int currentImageSize = 3;
    private MainWindow mw;
    private JButton computerButton;
    private JButton scriptButton;
    private ComputerTooltipFrame ctf;
    private String lastActionCommand = "select";

	public PanelToolBar(MainWindow mw) {
		super("Toolbar");
        this.mw = mw;
		//setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		addComponents();
	}
	private void addComponents() {
		add(makeIconButton("move", "select", "Movement tool", "Move"));
		add(makeIconButton("text", "text", "Draw text on the canvas", "Text"));
        add((computerButton = makeIconButton("monitor", "computer", "Create a new computer", "Computer")));
        add((scriptButton = makeIconButton("script", "script", "Scripting tool", "Script")));

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
        URL imageURL = null;
        String loc = null;
        if(currentImageSize != -1) {
            //Find image
            loc = "resources/openiconic/png/" + name
                + (currentImageSize == 1 ? "" :  "-" + currentImageSize + "x")
                + ".png";
            imageURL = PanelToolBar.class.getResource(loc);
        }

		//Create and setup button
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);

		if(imageURL != null) { //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {
			button.setText(altText);
            if(currentImageSize != -1) {
                System.err.println("Unable to find resource: " + loc);
            }
		}
		return button;
	}
	public void actionPerformed(ActionEvent ae) {
		String s = ae.getActionCommand();
		setAction(s);
	}
    public void resetAction() {
        setAction(lastActionCommand);
    }
    private void setAction(String s) {
        if("size".equals(s)) {
			setMaxDesiredBound(currentImageSize == 1 ? 2 :
                              (currentImageSize == 2 ? 3 :
                              (currentImageSize == 3 ? 4 :
                              (currentImageSize == 4 ? 6 :
                              (currentImageSize == 6 ? 8 :
                              (currentImageSize == 8 ? -1 : 1))))));
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
                            }
                            dp.updateAll();
                        }
                    }));
                    break;
                    case OVAL:
                    //There's repetition because the alternatives are much slower
                    dp.setAction(new CanvasAction("Oval", (screen, actual, g) -> {
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
                            Rectangle rect = Utility.normalizeRectangle(p1.x, p1.y, p2.x-p1.x, p2.y-p1.y);
                            g2d.drawOval(rect.x,
                                        rect.y,
                                        rect.width,
                                        rect.height);
                            pointsScreen.remove(1);
                            pointsActual.remove(1);
                        }
                        g2d.setColor(Color.BLACK);
                        Rectangle rect = Utility.normalizeRectangle(p1.x, p1.y, screen.x-p1.x, screen.y-p1.y);
                        g2d.drawOval(rect.x,
                                    rect.y,
                                    rect.width,
                                    rect.height);
                        pointsScreen.add(screen);
                        pointsActual.add(actual);
                        counter.val = 1;
                    }, (screen, actual, g) -> {
                        if(pointsActual.size() > 0) {
                            Point p1 = pointsActual.get(0);
                            Rectangle rect = Utility.normalizeRectangle(p1.x, p1.y, actual.x-p1.x, actual.y-p1.y);
                            if(rect.width < 5 || rect.height < 5) {
                                Utility.displayError("Error", "Please draw a bigger ellipse");
                                dp.updateAll();
                                return;
                            }
                            DrawableEllipse drawrect = new DrawableEllipse(rect);
                            if(dp.errorIfObjectOffscreen(drawrect)) return;
                            Computer comp = Computer.computerFactory(drawrect, dp,
                                mw.getConsole().getPrintCallback(),
                                mw.getConsole().getErrorCallback());
                            if(comp != null) {
                                drawrect.setComputer(comp);
                                dp.addDrawableObject(drawrect);
                            }
                            dp.updateAll();
                        }
                    }));
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
            setSelectAction((action) -> {
                mw.getDisplayPanel().setAction(action);
            });
        } else {
            System.out.println("Unknown action: " + s);
        }
    }
    public void initializeSelectAction() {
        setSelectAction((action) -> {
            mw.getDisplayPanel().getCanvas().setDefaultAction(action);
        });
    }
    public void setSelectAction(Consumer<CanvasAction> actionSetter) {
            Container<Point> trulyOriginalActual = new Container<>(); //original location
            Container<Point> originalActual = new Container<>(); //buffer location, gets updated
            Container<DrawableObject> drawable = new Container<>(); //object we're moving
            actionSetter.accept(new CanvasAction("Select", (screen, actual, g) -> {
                originalActual.set(actual);
                trulyOriginalActual.set(actual);
                drawable.set(mw.getDisplayPanel().getObjectAtScreenLocation(screen.x, screen.y));
            }, (screen, actual, g) -> {
                if(drawable.get() != null) {
                    int dx = actual.x - originalActual.get().x;
                    int dy = actual.y - originalActual.get().y;
                    mw.getDisplayPanel().getCanvas().drawObjectWithColor(drawable.get(), Color.WHITE, g);
                    drawable.get().changePosition(dx, dy, mw.getDisplayPanel().getZoom(), g);
                    mw.getDisplayPanel().getCanvas().drawObjectWithColor(drawable.get(), Color.BLACK, g);
                    originalActual.set(actual);
                }
            }, (screen, actual, g) -> {
                if(drawable.get() != null) {
                    if(mw.getDisplayPanel().errorIfObjectOffscreen(drawable.get())) {
                        //Revert position if offscreen
                        drawable.get().changePosition(trulyOriginalActual.get().x - actual.x,
                                                      trulyOriginalActual.get().y - actual.y,
                                                      mw.getDisplayPanel().getZoom(),
                                                      g);
                    }
                    mw.getDisplayPanel().updateAll();
                }
            }));
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