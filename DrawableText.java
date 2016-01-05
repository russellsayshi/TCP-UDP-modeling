import java.awt.*;

class DrawableText extends DrawableObject {
    private String text;
    private Font f;
    
    public DrawableText(String text, int x, int y, Graphics g) {
        this.text = text;
        rect.x = x;
        rect.y = y;
        updateBoundingBox(g, 1.0);
        originalRect = rect;
    }
    
    @Override
    public void updateBoundingBox(Graphics g, double zoom) {
        this.f = g.getFont();
        FontMetrics metrics = g.getFontMetrics(f);
        rect.setSize(metrics.stringWidth(text), metrics.getHeight());
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawString(text, (int)((rect.x - viewport.x) * zoom) + offsetX, (int)((rect.y + rect.height - viewport.y) * zoom) + offsetY);
    }
}