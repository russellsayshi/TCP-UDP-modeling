import java.util.function.*;
import java.awt.*;

class CanvasAction {
    public interface ActionFunction {
        void handle(Point screen, Point actual, Graphics g);
    }
    private ActionFunction down;
    private ActionFunction drag;
    private ActionFunction release;
    private String name;
    
    public CanvasAction(String name, ActionFunction down, ActionFunction drag, ActionFunction release) {
        this.down = down;
        this.drag = drag;
        this.release = release;
        this.name = name;
    }
    
    public ActionFunction down() {
        return down;
    }
    
    public ActionFunction drag() {
        return drag;
    }
    
    public ActionFunction release() {
        return release;
    }
    
    public String getName() {
        return name;
    }
}
