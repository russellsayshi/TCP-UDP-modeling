import java.awt.*;

public class DrawableObject {
    private Rectangle rect;
    private ObjectType t;
    
    public enum ObjectType {
        TEXT,
        NODE
    }
    
    public DrawableObject(int x, int y, int width, int height, ObjectType t) {
        this.t = TEXT;
        rect = new Rectangle(x, y, width, height);
    }
    
    public Rectangle getRectangle() {
        return rect;
    }
    
    public boolean intersectsWith(Rectangle otherRect) {
        return rect.intersects(otherRect);
    }
}