import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

class CentralCanvas extends JPanel implements MouseListener, MouseMotionListener {
	private final int width;
	private final int height;
    private static final double ZOOM_FACTOR = 0.1; //up to 1 decimal place
    private static final double MAXIMUM_ZOOM = 5.0;
    private static final double MINIMUM_ZOOM = 0.3;
    private double zoom = 1;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int offsetX;
    private int offsetY;
    private DisplayPanel dp;
	private BufferedImage img;
    private ArrayList<DrawableObject> objects = new ArrayList<>();
    private ArrayList<DrawablePath> freehandObjects = new ArrayList<>();
    private Font drawingFont;
    private CanvasAction action = new CanvasAction("", null, null, null);
    private CanvasAction defaultAction = action;

	public CentralCanvas(int width, int height, DisplayPanel dp) {
        this.dp = dp;
		this.width = width;
		this.height = height;
		addMouseListener(this);
		addMouseMotionListener(this);
        addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                CentralCanvas.this.regenerateImage(false);
            }
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
            @Override
            public void componentMoved(ComponentEvent e) {}
        });
        setMinimumSize(new Dimension(1, 1));
	}
    
    public boolean errorIfObjectOffscreen(DrawableObject obj) {
        Rectangle whole = new Rectangle(0, 0, width, height);
        if(!whole.contains(obj.getOriginalRectangle())) {
            Utility.displayError("Error", "That object does not fit within the bounds of the canvas.");
            redrawImage(false);
            return true;
        }
        return false;
    }
    
    public void setDefaultAction(CanvasAction action) {
        defaultAction = action;
    }

    public void addDrawableObject(DrawableObject obj) {
        if(errorIfObjectOffscreen(obj)) {
            return;
        }
        if(obj instanceof DrawablePath) {
            freehandObjects.add((DrawablePath)obj);
        } else {
            objects.add(obj);
        }
    }
    
    public double getZoom() {
        return zoom;
    }
    
    public void drawObjectWithColor(DrawableObject drawable, Color c, Graphics g) {
        Rectangle viewport = new Rectangle(dp.getHorizontalScrollBar().getValue(),
                                           dp.getVerticalScrollBar().getValue(),
                                           dp.getHorizontalScrollBar().getVisibleAmount(),
                                           dp.getVerticalScrollBar().getVisibleAmount());
        Color color = g.getColor();
        g.setColor(c);
        drawable.draw(g, viewport, zoom, offsetX, offsetY);
        g.setColor(color);
    }
    
    public void regenerateImage(boolean zoomChanged) {
        updateScrollbars();
        redrawImage(zoomChanged);
    }
    
    public void redrawImage(boolean zoomChanged) {
        int paneWid = getWidth();
        int paneHei = getHeight();
        img = new BufferedImage(paneWid, paneHei, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, paneWid, paneHei);
        g.setColor(Color.BLACK);
        
        if(drawingFont == null) {
            drawingFont = new Font("Serif", Font.PLAIN, 12);
        }
        
        int newImageWid = (int)(width * zoom);
        int newImageHei = (int)(height * zoom);
        offsetX = 0;
        if(newImageWid < paneWid) {
            offsetX = (paneWid-newImageWid)/2;
        }
        offsetY = 0;
        if(newImageHei < paneHei) {
            offsetY = (paneHei - newImageHei)/2;
        }
        if(zoomChanged) {
            drawingFont = g.getFont().deriveFont((float)(12 * zoom));
        }
        g.setFont(drawingFont);
        g.drawRect((int)((-dp.getHorizontalScrollBar().getValue()) * zoom + offsetX),
                   (int)((-dp.getVerticalScrollBar().getValue()) * zoom + offsetY),
                   (int)(newImageWid)-1,
                   (int)(newImageHei)-1);
        Rectangle viewport = new Rectangle(dp.getHorizontalScrollBar().getValue(),
                                           dp.getVerticalScrollBar().getValue(),
                                           dp.getHorizontalScrollBar().getVisibleAmount(),
                                           dp.getVerticalScrollBar().getVisibleAmount());
        //System.out.println(viewport);
        if(freehandObjects.size() > 0) {
            freehandObjects.get(0).transformGraphics(g);
            for(DrawablePath object : freehandObjects) {
                if(object.intersectsWith(viewport, zoom)) {
                    object.draw(g, viewport, zoom, offsetX, offsetY);
                    //object.drawBoundingBox(g, viewport, zoom, offsetX, offsetY);
                }
            }
            freehandObjects.get(0).resetGraphics(g);
        }
        for(DrawableObject object : objects) {
            if(object.intersectsWith(viewport, zoom)) {
                object.draw(g, viewport, zoom, offsetX, offsetY);
                //object.drawBoundingBox(g, viewport, zoom, offsetX, offsetY);
            }
        }
        g.dispose();
    }
    
    private void updateScrollbars() {
        int paneWid = getWidth();
        int paneHei = getHeight();
        dp.getHorizontalScrollBar().setMaximum(width);
        int visHor = (int)(paneWid / zoom);
        if(visHor >= width) {
            dp.getHorizontalScrollBar().setValue(0);
        }
        int visibleToBeHorizontal = Math.min(width, visHor);
        if(visibleToBeHorizontal + dp.getHorizontalScrollBar().getValue() >= width) {
            dp.getHorizontalScrollBar().setValue(width - visibleToBeHorizontal);
        }
        dp.getHorizontalScrollBar().setVisibleAmount(visibleToBeHorizontal);
        dp.getVerticalScrollBar().setMaximum(height);
        int visVer = (int)(paneHei / zoom);
        if(visVer >= height) {
            dp.getVerticalScrollBar().setValue(0);
        }
        int visibleToBeVertical = Math.min(height, visVer);
        if(visibleToBeVertical + dp.getVerticalScrollBar().getValue() >= height) {
            dp.getVerticalScrollBar().setValue(height - visibleToBeVertical);
        }
        dp.getVerticalScrollBar().setVisibleAmount(visibleToBeVertical);
        repaint();
    }

	@Override
	public void paintComponent(Graphics g) {
        if(img == null) {
            regenerateImage(true);
            if(img == null) {
                handleImageNull();
                return;
            }
        }
		g.drawImage(img, 0, 0, null);
	}
    
    public void setAction(CanvasAction action) {
        this.action = action;
    }
    
    private Point calculateTrueLocation(int x, int y) {
        int xDisplay = (int)((x - offsetX)/zoom + dp.getHorizontalScrollBar().getValue());
        int yDisplay = (int)((y - offsetY)/zoom + dp.getVerticalScrollBar().getValue());
        return new Point(xDisplay, yDisplay);
    }
    
    private double toMoveXBuffer = 0.0;
    private double toMoveYBuffer = 0.0;
    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            double toMoveX = lastMouseX - e.getX();
            double toMoveY = lastMouseY - e.getY();
            toMoveXBuffer += toMoveX/zoom;
            toMoveYBuffer += toMoveY/zoom;
            if(Math.abs(toMoveXBuffer) >= 1.0) {
                int buf = (int)toMoveXBuffer;
                dp.getHorizontalScrollBar().setValue(dp.getHorizontalScrollBar().getValue() + buf);
                toMoveXBuffer -= buf;
            }
            if(Math.abs(toMoveYBuffer) >= 1.0) {
                int buf = (int)toMoveYBuffer;
                dp.getVerticalScrollBar().setValue(dp.getVerticalScrollBar().getValue() + buf);
                toMoveYBuffer -= buf;
            }
            regenerateImage(false);
        } else if(SwingUtilities.isLeftMouseButton(e)) {
            if(action.drag() != null) {
                Graphics g = img.getGraphics();
                action.drag().handle(new Point(e.getX(), e.getY()), calculateTrueLocation(e.getX(), e.getY()), g);
                g.dispose();
                repaint();
            }
        }
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }
    
	@Override
	public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
        
        if(SwingUtilities.isLeftMouseButton(e)) {
            if(action.down() != null) {
                Graphics g = img.getGraphics();
                action.down().handle(new Point(e.getX(), e.getY()), calculateTrueLocation(e.getX(), e.getY()), g);
                g.dispose();
                repaint();
            }
        }
    }
    
	@Override
	public void mouseReleased(MouseEvent e) {
        if(SwingUtilities.isLeftMouseButton(e)) {
            if(action.release() != null) {
                Graphics g = img.getGraphics();
                action.release().handle(new Point(e.getX(), e.getY()), calculateTrueLocation(e.getX(), e.getY()), g);
                g.dispose();
                repaint();
                action = defaultAction;
            }
        }
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e) && !e.isConsumed()) {
            RightClickMenu rcm = new RightClickMenu(getObjectAtScreenLocation(e.getX(), e.getY()));
            rcm.show(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    //For debug purposes
    /*int repaintCount = 0;
    @Override
    public void repaint() {
        super.repaint();
        System.out.println("Repainting: " + (++repaintCount));
    }*/
    
    public void handleImageNull() {
        Utility.displayError("Image error", "An error occured. A BufferedImage was null when it should not have been.");
    }
    
    public void verticalScrollChanged(final int delta) {
        dp.updateVerticalScrollWithDelta(delta);
    }
    
    public void horizontalScrollChanged(final int delta) {
        dp.updateHorizontalScrollWithDelta(delta);
    }
    
    public int getSpaceWidth() {
        return width;
    }
    
    public int getSpaceHeight() {
        return height;
    }
    
    public void modifyZoom(int scrollAmount) {
        zoom -= scrollAmount * ZOOM_FACTOR;
        zoom = Math.round(zoom * 10)/10.0;
        zoom = Math.max(MINIMUM_ZOOM, Math.min(zoom, MAXIMUM_ZOOM));
        regenerateImage(true);
    }
    
    public void correctZoomPosition(int scrollAmount, int x, int y) {
        dp.getHorizontalScrollBar().setValue(dp.getHorizontalScrollBar().getValue() + (int)(((x - getWidth()/2) * (-scrollAmount)/2/zoom)));
        dp.getVerticalScrollBar().setValue(dp.getVerticalScrollBar().getValue() + (int)(((y - getHeight()/2) * (-scrollAmount)/2/zoom)));
    }
    
    public DrawableObject getObjectAtScreenLocation(int x, int y) {
        Point truePoint = calculateTrueLocation(x, y);
        for(int i = objects.size()-1; i >= 0; i--) {
            if(objects.get(i).getOriginalRectangle().contains(truePoint)) {
                return objects.get(i);
            }
        }
        for(int i = freehandObjects.size()-1; i >= 0; i--) {
            if(freehandObjects.get(i).getOriginalRectangle().contains(truePoint)) {
                return freehandObjects.get(i);
            }
        }
        return null;
    }

	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}