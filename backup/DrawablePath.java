import java.awt.*;
import java.awt.geom.GeneralPath;

class DrawablePath extends DrawableObject {
    private GeneralPath gp;
    private int xloc;
    private int yloc;
    private AffineTransform storedTransform;
    
    public DrawableText(GeneralPath gp, Graphics g) {
        AffineTransform t = new AffineTransform();
        rect = gp.getBounds();
        xloc = rect.x;
        yloc = rect.y;
        t.translate(-xloc, -yloc);
        Shape s = gp.createTransformedShape(t);
        gp = new GeneralPath(s);
    }
    
    @Override
    public void updateBoundingBox(Graphics g) {
        this.f = g.getFont();
        FontMetrics metrics = g.getFontMetrics(f);
        rect = new Rectangle(rect.x, rect.y, metrics.stringWidth(text), metrics.getHeight());
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
    
    public void transformGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        storedTransform = g2d.getTransform();
    }
    
    public void resetGraphics(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setTransform(storedTransform);
    }
}