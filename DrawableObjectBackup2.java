import java.awt.*;

public abstract class DrawableObject {
    protected Rectangle rect; //bounding box
    
    public DrawableObject() {
        rect = new Rectangle(0, 0, 10, 10);
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect, double zoom) {
        return true;
        //return rect.intersects((otherRect.x - rect.x) / zoom, (otherRect.y - rect.y) / zoom, otherRect.width, otherRect.height);
    }
    
    public abstract void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY);
    public abstract void updateBoundingBox(Graphics g);
    
    public void drawBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        if(rect.x > 300) {
            System.out.println("ME: " + rect);
            System.out.println("VIEWPORT: " + viewport);
        }
        g.drawRect((int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y - viewport.y) * zoom) + offsetY, (int)(rect.width * zoom), (int)(rect.height * zoom));
    }
}