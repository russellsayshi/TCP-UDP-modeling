import java.awt.*;

class DrawableRectangle extends DrawableObject {
    private Computer computer;
    
    public DrawableRectangle(Rectangle self) {
        originalRect = self;
        rect = (Rectangle)self.clone();
        updateBoundingBox(null, 1.0);
    }
    
    public void setComputer(Computer computer) {
        this.computer = computer;
    }
    
    @Override
    public void updateBoundingBox(Graphics g, double zoom) {
        rect.setSize((int)(originalRect.width*zoom), (int)(originalRect.height*zoom));
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawRect((int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y - viewport.y) * zoom) + offsetY, (int)(originalRect.width*zoom), (int)(originalRect.height*zoom));
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