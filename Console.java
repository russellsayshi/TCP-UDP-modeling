import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.function.*;
import javax.script.ScriptException;

class Console extends JTextPane implements MouseListener, MouseMotionListener {
    private Style errorStyle;
    private Style errorStyleRed;
    private Style errorStyleMono;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private JScrollPane parent;
    private StyledDocument doc;
    private Network net;
    private JFrame parentFrame;
    private boolean autoscroll = true;
    
    public Console(JFrame parentFrame, Network net) {
        this.parentFrame = parentFrame;
        this.net = net;
        setEditable(false);
        setContentType("text/html");
        doc = getStyledDocument();
        addStyledText("Initialized", null);
        setPreferredSize(new Dimension(150, 0));
        addMouseMotionListener(this);
        addMouseListener(this);
        initializeStyles();
    }
    
    private void initializeStyles() {
        errorStyle = doc.addStyle("errorStyle",
                                 StyleContext.
                                 getDefaultStyleContext().
                                 getStyle(StyleContext.DEFAULT_STYLE));
        errorStyleRed = doc.addStyle("errorStyleRed",
                                 errorStyle);
        StyleConstants.setForeground(errorStyleRed, Color.RED);
        errorStyleMono = doc.addStyle("errorStyleMono",
                                 errorStyle);
        StyleConstants.setFontFamily(errorStyleMono, "Monospaced");
    }
    
    public BiConsumer<String, String> getPrintCallback() {
        return ((ip, str) -> {
            if(str.charAt(str.length()-1) == '\n') {
                str = str.substring(0, str.length()-1);
            }
            displayMessageFromIP(ip, str);
        });
    }
    
    public Consumer<ScriptExceptionContainer> getErrorCallback() {
        return ((error) -> {
            displayScriptError(error);
        });
    }
    
    public void setScrollPane(JScrollPane parent) {
        this.parent = parent;
        JScrollBar jsb = parent.getVerticalScrollBar();
        jsb.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent ae) {
                if(!ae.getValueIsAdjusting() && autoscroll) {
                    jsb.setValue(jsb.getMaximum());
                }
            }
        });
    }
    
    public void displayMessageFromIP(String ip, String text) {
        addHTML("<br><b>" + ip + "</b>: " + text);
    }
    
    private void addHTML(String text) {
        HTMLDocument hdoc = (HTMLDocument)doc;
        try {
            hdoc.insertAfterEnd(hdoc.getCharacterElement(hdoc.getLength()), text);
        } catch(BadLocationException|IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void displayScriptError(ScriptExceptionContainer se) {
        errorStyle.addAttribute("ip", se.ip);
        errorStyle.addAttribute("exception", se.exception);
        errorStyleRed.addAttribute("ip", se.ip);
        errorStyleRed.addAttribute("exception", se.exception);
        errorStyleMono.addAttribute("ip", se.ip);
        errorStyleMono.addAttribute("exception", se.exception);
        
        addStyledText("\n", null);
        addStyledText("Error", errorStyleRed);
        addStyledText(" at ", errorStyle);
        addStyledText(se.ip, errorStyleMono);
    }
    
    private void addStyledText(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch(BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    public void setAutoscroll(boolean autoscroll) {
        this.autoscroll = autoscroll;
    }
    
    public boolean getAutoscroll() {
        return autoscroll;
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        Element el = doc.getCharacterElement(viewToModel(me.getPoint()));
        if(el == null) return;
        AttributeSet as = el.getAttributes();
        if(as.isDefined("ip")) {
            String ip = (String)as.getAttribute("ip");
            ScriptException se = (ScriptException)as.getAttribute("exception");
            Node node = net.getAtIP(ip);
            if(node == null) {
                Utility.displayError("Error", "Computer does not exist");
                return;
            }
            String errorString = "--ERROR--\n" +
                "Error at line number " + se.getLineNumber() +
                " and column number " + se.getColumnNumber() + 
                "\n" + se.getMessage();
            new ScriptDialog(parentFrame, str -> {
                if(str != null) {
                    node.setScript(str);
                }
            }, node.getScript(), errorString).setVisible(true);
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent me) {
        Element el = doc.getCharacterElement(viewToModel(me.getPoint()));
        if(el == null) return;
        AttributeSet as = el.getAttributes();
        if(as.isDefined("ip")) {
            setCursor(handCursor);
        } else {
            setCursor(defaultCursor);
        }
    }
    
    @Override
    public void mouseExited(MouseEvent me) {}
    @Override
    public void mouseEntered(MouseEvent me) {}
    @Override
    public void mouseReleased(MouseEvent me) {}
    @Override
    public void mousePressed(MouseEvent me) {}
    @Override
    public void mouseDragged(MouseEvent me) {}
}