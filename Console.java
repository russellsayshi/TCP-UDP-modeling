import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

class Console extends JTextPane implements MouseListener, MouseMotionListener {
    private Style errorStyle;
    private Style errorStyleRed;
    private Style errorStyleMono;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    private JScrollPane parent;
    private StyledDocument doc;
    
    public Console() {
        this.parent = parent;
        setEditable(false);
        setContentType("text/html");
        doc = getStyledDocument();
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
        addStyledText("Initialized", null);
        addHTML("<br><b>NEAT</b>");
        setPreferredSize(new Dimension(150, 0));
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    public void setScrollPane(JScrollPane parent) {
        this.parent = parent;
    }
    
    private void scrollToBottom() {
        if(parent == null) return;
        JScrollBar vertical = parent.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    private void scrollToBottomLater() {
        SwingUtilities.invokeLater(()->scrollToBottom());
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
        scrollToBottomLater();
    }
    
    private void addStyledText(String text, Style style) {
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch(BadLocationException ble) {
            ble.printStackTrace();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        Element el = doc.getCharacterElement(viewToModel(me.getPoint()));
        if(el == null) return;
        AttributeSet as = el.getAttributes();
        if(as.isDefined("ip")) {
            String action = as.getAttribute("ip").toString();
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
    public void mouseEntered(MouseEvent me) {
        ScriptExceptionContainer se = new ScriptExceptionContainer();
        se.ip = "10.10.10.45";
        se.exception = new javax.script.ScriptException("neat");
        displayScriptError(se);}
    @Override
    public void mouseReleased(MouseEvent me) {}
    @Override
    public void mousePressed(MouseEvent me) {}
    @Override
    public void mouseDragged(MouseEvent me) {}
}