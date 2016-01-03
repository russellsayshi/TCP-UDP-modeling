import java.awt.*;

public class DrawableText extends DrawableObject {
    private String text;
    private Font f;
    
    public DrawableText(String text, int x, int y, Graphics g) {
        this.text = text;
        rect.x = x;
        rect.y = y;
        recalculateBoundingBox(g);
    }
    
    @Override
    public void recalculateBoundingBox(Graphics g) {
        this.f = g.getFont();
        FontMetrics metrics = g.getFontMetrics(f);
        rect = new Rectangle(rect.x, rect.y, metrics.stringWidth(text), metrics.getHeight());
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawString(text, (int)(rect.x * zoom) + offsetX, (int)((rect.y + rect.height) * zoom) + offsetX);
    }
}