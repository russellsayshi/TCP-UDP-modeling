import java.awt.*;

abstract class DrawableObject {
    protected Rectangle rect; //bounding box
    
    public DrawableObject() {
        rect = new Rectangle(0, 0, 10, 10);
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect, double zoom) {
        return otherRect.intersects(rect);
    }
    
    public abstract void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY);
    public abstract void updateBoundingBox(Graphics g);
    
    int r = (new java.util.Random()).nextInt();
    public void drawBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawRect((int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y - viewport.y) * zoom) + offsetY, (int)(rect.width * zoom), (int)(rect.height * zoom));
    }
}