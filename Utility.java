import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.function.*;

public class Utility {
    public static void loadOneTimeMousePressedListener(JComponent component, Consumer<MouseEvent> consumer) {
        component.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("DEBUG");
                component.removeMouseListener(this);
                consumer.accept(e);
            }
        });
        System.out.println("Registered");
    }
}