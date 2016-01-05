import java.awt.*;
import java.awt.geom.*;

class DrawablePath extends DrawableObject {
    private GeneralPath gp;
    private int xloc;
    private int yloc;
    private AffineTransform storedTransform;
    private Computer computer;
    
    public DrawablePath(GeneralPath gp) {
        this.computer = computer;
        AffineTransform t = new AffineTransform();
        rect = gp.getBounds();
        originalRect = rect;
        xloc = rect.x;
        yloc = rect.y;
        t.translate(-xloc, -yloc);
        Shape s = gp.createTransformedShape(t);
        this.gp = new GeneralPath(s);
    }
    
    public void setComputer(Computer computer) {
        this.computer = computer;
    }
    
    @Override
    public void updateBoundingBox(Graphics g, double zoom) {
        rect.setSize((int)(originalRect.width * zoom), (int)(originalRect.height * zoom));
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        AffineTransform t = new AffineTransform();
        t.translate((int)((xloc - viewport.x) * zoom) + offsetX, (int)((yloc - viewport.y) * zoom) + offsetY);
        t.scale(zoom, zoom);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setTransform(t);
        g2d.draw(gp);
        //g.drawString(text, (int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y + rect.height - viewport.y) * zoom) + offsetY);
    }
    
    @Override
    public void drawBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        Graphics2D g2d = (Graphics2D)g;
        Rectangle r = new Rectangle(0, 0, rect.width, rect.height);
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