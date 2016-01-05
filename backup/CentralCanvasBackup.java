import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.swing.*;

public class CentralCanvas extends JPanel implements MouseListener, MouseMotionListener {
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
    private Font drawingFont;

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
	}
    
    public void regenerateImage(boolean zoomChanged) {
        updateScrollbars();
        redrawImage(zoomChanged);
    }
    
    boolean done = false;
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
        
        //KILL ME
        if(!done) {
            objects.add(new DrawableText("Hey", 0, 0, g));
            objects.add(new DrawableText("N33to", 100, 100, g));
            objects.add(new DrawableText("N33to", 700, 500, g));
            done = true;
        }
        //END KILL ME
        
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
        g.setFont(drawingFont);
        if(zoomChanged) {
            drawingFont = g.getFont().deriveFont((float)(12 * zoom));
        }
        g.drawRect((int)((-dp.getHorizontalScrollBar().getValue()) * zoom + offsetX),
                   (int)((-dp.getVerticalScrollBar().getValue()) * zoom + offsetY),
                   (int)(newImageWid)-1,
                   (int)(newImageHei)-1);
        Rectangle viewport = new Rectangle(dp.getHorizontalScrollBar().getValue(),
                                           dp.getVerticalScrollBar().getValue(),
                                           dp.getHorizontalScrollBar().getVisibleAmount(),
                                           dp.getVerticalScrollBar().getVisibleAmount());
        System.out.println(viewport);
        for(DrawableObject object : objects) {
            if(object.intersectsWith(viewport, g, zoom, offsetX, offsetY)) {
                object.draw(g, viewport, zoom, offsetX, offsetY);
                object.drawBoundingBox(g);
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
            handleImageNull();
            return;
        }
		g.drawImage(img, 0, 0, null);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
        if(img == null) {
            handleImageNull();
            return;
        }
		int x = e.getX();
		int y = e.getY();
        
        int xDisplay = (int)((x - offsetX)/zoom);
        int yDisplay = (int)((y - offsetY)/zoom);
        
        repaint();
	}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if(SwingUtilities.isRightMouseButton(e)) {
            int toMoveX = lastMouseX - e.getX();
            int toMoveY = lastMouseY - e.getY();
            dp.getHorizontalScrollBar().setValue(dp.getHorizontalScrollBar().getValue() + toMoveX);
            dp.getVerticalScrollBar().setValue(dp.getVerticalScrollBar().getValue() + toMoveY);
            regenerateImage(false);
        }
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }
    
	@Override
	public void mousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }
    
    //For debug purposes
    /*int repaintCount = 0;
    @Override
    public void repaint() {
        super.repaint();
        System.out.println("Repainting: " + (++repaintCount));
    }*/
    
    public void handleImageNull() {
        JOptionPane.showMessageDialog(this,
                                      "An error occured. A BufferedImage was null when it should not have been.",
                                      "Image error",
                                      JOptionPane.ERROR_MESSAGE);
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
        dp.getHorizontalScrollBar().setValue(dp.getHorizontalScrollBar().getValue() + (int)(((x - getWidth()/2)/zoom * (-scrollAmount) / (zoom))));
        dp.getVerticalScrollBar().setValue(dp.getVerticalScrollBar().getValue() + (int)(((y - getHeight()/2)/zoom * (-scrollAmount) / (zoom))));
    }

	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {}
}