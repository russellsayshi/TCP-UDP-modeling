import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.function.*;

public class ComputerTooltipFrame extends JFrame {
    private Consumer<Result> callback;
    
    public enum Result {
        DRAWABLE,
        RECTANGLE,
        TRIANGLE,
        CIRCLE,
        IMAGE
    }
    public ComputerTooltipFrame(int x, int y, Consumer<Result> callback) {
        this.callback = callback;
        setLocation(x, y);
        setSize(200, 100);
        setUndecorated(true);
        setVisible(true);
        System.out.println("HEY SHITLORD");
    }
}