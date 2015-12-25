import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Holds the main GUI */
public class MainWindow extends JFrame {
	public MainWindow() {
		initializeFrame();
		setupLayout();
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Network Modeling");
		setSize(800, 500);
		setMinimumSize(new Dimension(100, 150));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void setupLayout() {
		//Initialize layout manager
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		//Toolbar
		c.fill = GridBagConstraints.BOTH; //fill up horizontal + vertical
		c.gridwidth = GridBagConstraints.REMAINDER; //end of row
		c.weightx = 1.0; //fill horizontal
		c.ipady = 50; //add vertical padding
		JButton button = new JButton("FIRST");
		gridbag.setConstraints(button, c);
		add(button);

		//Central frame with canvas
		c.ipady = 0; //remove vertical padding
		c.weighty = 1.0; //fill up excess space from toolbar
		JButton button2 = new JButton("Test2");
		gridbag.setConstraints(button2, c);
		add(button2);
	}

	public static void main(String[] args) {
		new MainWindow();
	}
}