import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.*;

public class Utility {
    private static final Pattern IP_PATTERN = Pattern.compile(
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static byte[] nextIP = {Byte.MIN_VALUE+10, Byte.MIN_VALUE+10, Byte.MIN_VALUE, Byte.MIN_VALUE};
    
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
                component.removeMouseListener(this);
                consumer.accept(e);
            }
        });
    }
    public static boolean verifyIP(final String ip) {
        return ip != null && IP_PATTERN.matcher(ip).matches();
    }
    
    public static void main(String[] args) {
        //Test "verifyIP" method
        if(args.length == 2 && args[0].equals("ip")) {
            System.out.println(verifyIP(args[1]));
        } else {
            //Strictly debug REPL
            System.out.println("exit to exit, run to run what you input so far.");
            javax.script.ScriptEngineManager manager = new javax.script.ScriptEngineManager();
            javax.script.ScriptEngine engine = manager.getEngineByName("nashorn");
            engine.put("Utility", Utility.class);
            Scanner s = new Scanner(System.in);
            StringBuilder sb = new StringBuilder();
            while(s.hasNextLine()) {
                String str = s.nextLine();
                if(str.equals("exit")) {
                    s.close();
                    System.exit(0);
                } else if(str.equals("run")) {
                    try {
                        System.out.println("Output: " + engine.eval(sb.toString()));
                    } catch(javax.script.ScriptException se) {
                        System.out.println(se);
                    }
                    sb = new StringBuilder();
                } else {
                    sb.append(str);
                }
            }
        }
    }
    
    public static Rectangle normalizeRectangle(int x, int y, int width, int height) {
        if(width < 0) {
            x += width;
            width = -width;
        }
        if(height < 0) {
            y += height;
            height = -height;
        }
        return new Rectangle(x, y, width, height);
    }
    
    public static void displayError(String title, String message) {
        JOptionPane.showMessageDialog(null,
                                      message,
                                      title,
                                      JOptionPane.ERROR_MESSAGE);
    }
    
    public static void displayErrorMonospace(String title, String message) {
        displayError(title, "<code>" + message + "</code>");
    }
    
    public static void displayMessage(String title, String message) {
        JOptionPane.showMessageDialog(null,
                                      message,
                                      title,
                                      JOptionPane.PLAIN_MESSAGE);
    }
    
    public static String getInput(String title, String message) {
        return JOptionPane.showInputDialog(null,
                                      message,
                                      title,
                                      JOptionPane.PLAIN_MESSAGE);
    }
    
    public static String getNextIP() {
        StringBuilder ipbuild = new StringBuilder();
        for(int i = 0; i < nextIP.length; i++) {
            ipbuild.append(String.valueOf(((int)nextIP[i]) - Byte.MIN_VALUE));
            if(i != nextIP.length - 1) {
                ipbuild.append(".");
            }
        }
        incrementNextIPHelper(nextIP.length-1);
        return ipbuild.toString();
    }
    
    private static void incrementNextIPHelper(int index) {
        nextIP[index]++;
        if(nextIP[index] == Byte.MAX_VALUE) {
            if(index != 0) {
                incrementNextIPHelper(index - 1);
            }
            nextIP[index] = 0;
        }
    }
}