import java.awt.*;
import java.awt.geom.*;

class DrawablePath extends DrawableObject {
    private GeneralPath gp;
    private GeneralPath oldGP;
    private int xloc;
    private int yloc;
    private int translateX;
    private int translateY;
    private AffineTransform storedTransform;
    private Computer computer;
    
    public DrawablePath(GeneralPath gp) {
        this.oldGP = gp;
        rect = oldGP.getBounds();
        originalRect = (Rectangle)rect.clone();
        xloc = rect.x;
        yloc = rect.y;
        generatePath();
    }
    
    public void setComputer(Computer computer) {
        this.computer = computer;
    }
    
    private void generatePath() {
        AffineTransform t = new AffineTransform();
        t.translate(-xloc, -yloc);
        Shape s = oldGP.createTransformedShape(t);
        this.gp = new GeneralPath(s);
    }
    
    @Override
    public void updateBoundingBox(Graphics g, double zoom) {
        rect.setSize((int)(originalRect.width * zoom), (int)(originalRect.height * zoom));
    }
    
    @Override
    public void changePosition(int dx, int dy, double zoom, Graphics g) {
        xloc += dx;
        yloc += dy;
        translateX += dx;
        translateY += dy;
        originalRect.x = xloc;
        originalRect.y = yloc;
        generatePath();
        updateBoundingBox(g, zoom);
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        AffineTransform t = new AffineTransform();
        t.translate((int)((xloc - viewport.x + translateX) * zoom) + offsetX, (int)((yloc - viewport.y + translateY) * zoom) + offsetY);
        t.scale(zoom, zoom);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setTransform(t);
        g2d.draw(gp);
    }
    
    @Override
    public void drawBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        Graphics2D g2d = (Graphics2D)g;
        Rectangle r = new Rectangle(-translateX, -translateY, rect.width, rect.height);
        g2d.draw(r);
    }
    
    public void transformGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        storedTransform = g2d.getTransform();
    }
    
    public void resetGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setTransform(storedTransform);
    }
    
    @Override
    public boolean isComputer() {
        return computer != null;
    }
    
    @Override
    public Computer getComputer() {
        return computer;
    }
}