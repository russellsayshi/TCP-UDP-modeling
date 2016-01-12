import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.function.*;
import java.awt.event.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

class ScriptDialog extends JDialog implements ActionListener {
    public static final String TITLE = "Script node";
    private static String helpStr;
    private JSplitPane jsp;
    private Consumer<String> runCallback;
    private RSyntaxTextArea textArea;
    
    public ScriptDialog(JFrame parent, Consumer<String> runCallback, String oldScript, String additionalInfo) {
        super(parent, TITLE, ModalityType.DOCUMENT_MODAL);
        this.runCallback = runCallback;
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(100, 145));
        
        textArea = new RSyntaxTextArea(20, 80);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        textArea.setCodeFoldingEnabled(true);
        textArea.setText(oldScript);
        RTextScrollPane sp = new RTextScrollPane(textArea);
        
        JTextPane help = new JTextPane();
        help.setEditable(false);
        help.setContentType("text/html");
        if(helpStr == null) {
            Scanner scan = new Scanner(
                ScriptDialog.class.getResourceAsStream("resources/scripthelp.html"), "UTF-8")
                .useDelimiter("\\A");
            helpStr = scan.next();
            scan.close();
        }
        JScrollPane helpScroll = new JScrollPane(help);
        
        JTextArea additionalPane = null;
        JSplitPane pane2 = null;
        JScrollPane additionalScroll = null;
        if(additionalInfo == null) {
            jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, helpScroll);
        } else {
            additionalPane = new JTextArea();
            additionalPane.setEditable(false);
            additionalPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
            additionalScroll = new JScrollPane(additionalPane);
            pane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, additionalScroll, sp);
            jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pane2, helpScroll);
        }
        jsp.getRightComponent().setMinimumSize(new Dimension(0, 0));
        jsp.setOneTouchExpandable(false);
        jsp.setResizeWeight(1.0);
        add(jsp, BorderLayout.CENTER);
        
        JPanel buttons = new JPanel();
        buttons.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.weighty = 0.0;
        buttons.add(makeButton("Run"), c);
        c.gridy = 1;
        c.weighty = 1.0;
        buttons.add(Box.createVerticalGlue(), c);
        c.weighty = 0.0;
        c.gridy = 2;
        buttons.add(makeButton("Info"), c);
        c.gridy = 3;
        buttons.add(makeButton("Help"), c);
        c.gridy = 4;
        buttons.add(makeButton("Apply"), c);
        add(buttons, BorderLayout.EAST);
        
        pack();
        help.setText(helpStr);
        if(additionalPane != null && pane2 != null && additionalScroll != null) {
            additionalPane.setText(additionalInfo);
            pane2.setDividerLocation(0.3);
            final JScrollPane innerScrollPane = additionalScroll;
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = innerScrollPane.getHorizontalScrollBar();
                sb.setValue(sb.getMinimum());
            });
        }
        setLocationRelativeTo(parent);
        jsp.setDividerLocation(jsp.getMaximumDividerLocation());
    }
    
    private JButton makeButton(String title) {
        JButton btn = new JButton(title);
        btn.setActionCommand(title.toLowerCase());
        btn.addActionListener(this);
        return btn;
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if("help".equals(ae.getActionCommand())) {
            if(Math.abs(jsp.getDividerLocation() - jsp.getMaximumDividerLocation()) < 2) {
                //Show help
                if(jsp.getWidth() < 800) {
                    jsp.setDividerLocation(0.5);
                } else {
                    jsp.setDividerLocation(jsp.getMaximumDividerLocation() - 400);
                }
                JScrollBar horizontal = ((JScrollPane)jsp.getRightComponent()).getHorizontalScrollBar();
                JScrollBar vertical = ((JScrollPane)jsp.getRightComponent()).getVerticalScrollBar();
                horizontal.setValue(horizontal.getMinimum());
                vertical.setValue(vertical.getMinimum());
            } else {
                //Hide help
                jsp.setDividerLocation(jsp.getMaximumDividerLocation());
            }
        } else if("info".equals(ae.getActionCommand())) {
            Scanner scan = new Scanner(
                ScriptDialog.class.getResourceAsStream("RSyntaxTextArea/license.txt"), "UTF-8")
                .useDelimiter("\\A");
            String license = scan.next();
            scan.close();
            Utility.displayMessage("License notice", "The syntax highligher/editor is RSyntaxTextArea, and was not created by the author of this\nprogram, Russell Coleman. It is licensed under a modified BSD license, shown below.\n\n--BEGIN LICENSE--\n" + license + "\n--END LICENSE--");
        } else if("run".equals(ae.getActionCommand())) {
            if(runCallback != null) {
                runCallback.accept(textArea.getText());
            }
        } else if("apply".equals(ae.getActionCommand())) {
            if(runCallback != null) {
                runCallback.accept(textArea.getText());
            }
            dispose();
        }
    }
    
    public static void main(String[] args) {
        (new ScriptDialog(null, null, "", null)).setVisible(true);
    }
}