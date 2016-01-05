import java.awt.*;

public class DrawableText extends DrawableObject {
    private String text;
    private Font f;
    private int absoluteX;
    private int absoluteY;
    private int strHeight;
    
    public DrawableText(String text, int x, int y, Graphics g) {
        this.text = text;
        absoluteX = x;
        absoluteY = y;
    }
    
    @Override
    public void updateBoundingBox(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        this.f = g.getFont();
        FontMetrics metrics = g.getFontMetrics(f);
        strHeight = metrics.getHeight();
        rect = new Rectangle((int)(absoluteX * zoom) + offsetX - viewport.x,
                             (int)(absoluteY * zoom) + offsetY - viewport.y,
                             (int)(metrics.stringWidth(text) * zoom),
                             (int)(strHeight * zoom));
    }
    
    @Override
    public void draw(Graphics g, Rectangle viewport, double zoom, int offsetX, int offsetY) {
        g.drawString(text, rect.x, rect.y + strHeight);
    }
}