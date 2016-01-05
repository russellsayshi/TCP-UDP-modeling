import java.awt.*;

abstract class DrawableObject {
    protected Rectangle rect; //bounding box
    protected Rectangle originalRect;
    
    public DrawableObject() {
        rect = new Rectangle(0, 0, 10, 10);
        originalRect = rect;
    }
    
    public Rectangle getOriginalRectangle() {
        return originalRect;
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect, double zoom) {
        return otherRect.intersects(rect);
    }
    
    public abstract void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY);
    public abstract void updateBoundingBox(Graphics g, double zoom);
    public boolean isComputer() {
        return false;
    }
    public Computer getComputer() {
        return null;
    }
    
    public void drawBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawRect((int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y - viewport.y) * zoom) + offsetY, (int)(rect.width * zoom), (int)(rect.height * zoom));
    }
}