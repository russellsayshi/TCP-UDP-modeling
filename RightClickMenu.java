import javax.swing.*;
import java.awt.event.*;

class RightClickMenu extends JPopupMenu implements ActionListener {
    private DrawableObject drawable = null;
    
    public RightClickMenu(DrawableObject drawable) {
        if(drawable != null) {
            this.drawable = drawable;
            if(drawable.getComputer() != null) {
                JMenuItem jmi = new JMenuItem("Change IP");
                jmi.addActionListener(this);
                jmi.setActionCommand("ip");
                add(jmi);
            } else {
                add((new JMenuItem("Nothing to see here.")));
            }
        } else {
            add((new JMenuItem("Nothing to see here.")));
        }
    }
    
    public void actionPerformed(ActionEvent ae) {
        if("ip".equals(ae.getActionCommand())) {
            drawable.getComputer().ipPrompt();
        }
    }
}