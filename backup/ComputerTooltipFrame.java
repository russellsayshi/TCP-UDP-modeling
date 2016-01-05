import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.function.*;

class ComputerTooltipFrame extends JFrame implements WindowListener {
    private Consumer<Result> callback;
    public final static int WIDTH = 200;
    public final static int HEIGHT = 50;
    
    public enum Result {
        FREEHAND,
        RECTANGLE,
        OVAL,
        IMAGE
    }
    
    public ComputerTooltipFrame(int x, int y, Consumer<Result> callback) {
        this.callback = callback;
        setLocation(x - WIDTH/2, y);
        setSize(WIDTH, HEIGHT);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255, 0));
        addWindowListener(this);
        setupCanvas();
        setVisible(true);
    }
    
    public void setupCanvas() {
        add(new ComputerTooltipCanvas(r -> {
            dispose();
            callback.accept(r);
        }), BorderLayout.CENTER);
    }
    
    @Override
    public void windowDeactivated(WindowEvent we) {
        dispose();
    }
    @Override
    public void windowActivated(WindowEvent we) {}
    @Override
    public void windowIconified(WindowEvent we) {}
    @Override
    public void windowDeiconified(WindowEvent we) {}
    @Override
    public void windowClosed(WindowEvent we) {}
    @Override
    public void windowClosing(WindowEvent we) {}
    @Override
    public void windowOpened(WindowEvent we) {}
}