import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PanelToolBar extends JToolBar {
	public PanelToolBar() {
		super(HORIZONTAL);
		add(new JButton("WHEE"));
		add(new JButton("2"));
	}
}