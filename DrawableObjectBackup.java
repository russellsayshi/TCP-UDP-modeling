import java.awt.*;

public abstract class DrawableObject {
    protected Rectangle rect; //bounding box
    
    public DrawableObject() {
        rect = new Rectangle(0, 0, 10, 10);
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect, Graphics g, double zoom, int offsetX, int offsetY) {
        updateBoundingBox(g, otherRect, zoom, offsetX, offsetY);
        return rect.intersects(otherRect);
    }
    
    public abstract void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY);
    public abstract void updateBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY);
    
    public void drawAndUpdateBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        updateBoundingBox(g, viewport, zoom, offsetX, offsetY);
        draw(g, viewport, zoom, offsetX, offsetY);
    }
    
    public void drawBoundingBox(Graphics g) {
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }
}