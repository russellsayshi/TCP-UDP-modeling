import java.awt.*;

public abstract class DrawableObject {
    protected Rectangle rect; //bounding box
    
    public DrawableObject() {
        rect = new Rectangle(0, 0, 10, 10);
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect) {
        return rect.intersects(otherRect);
    }
    
    public abstract void draw(Graphics g, double zoom, int offsetX, int offsetY);
    public abstract void recalculateBoundingBox(Graphics g);
    
    public void drawBoundingBox(Graphics g, double zoom, int offsetX, int offsetY) {
        g.drawRect(rect.x, rect.y, rect.width, rect.height);
    }
}