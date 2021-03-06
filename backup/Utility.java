import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.function.*;
import java.util.regex.*;

class Utility {
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    
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
    public static boolean verifyIP(final String ip) {
        return ip != null && IP_PATTERN.matcher(ip).matches();
    }
    
    public static void main(String[] args) {
        //Test "verifyIP" method
        if(args.length == 2 && args[0].equals("ip")) {
            System.out.println(verifyIP(args[1]));
        }
    }
}