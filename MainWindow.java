import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Holds the main GUI */
public class MainWindow extends JFrame {
	private static final int MAX_TOOLBAR_PADDING = 50;
	private static final int MIN_TOOLBAR_PADDING = 10;
	private JButton toolbar;
	private GridBagLayout gridbag;

	public MainWindow() {
		initializeFrame();
		setupLayout();
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Network Modeling");
		setSize(800, 500);
		setMinimumSize(new Dimension(100, 200));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//On window resize
		addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent ce) {
				MainWindow.this.fixToolbar();
			}

			public void componentHidden(ComponentEvent ce) {
				//do nothing
			}

			public void componentShown(ComponentEvent ce) {
				//do nothing
			}

			public void componentMoved(ComponentEvent ce) {
				//do nothing
			}
		});

		//Initialize local look and feel
		try {
			UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());
		} catch(
			UnsupportedLookAndFeelException |
			ClassNotFoundException |
			InstantiationException |
			IllegalAccessException ex
		) {
			System.out.println("Non fatal error: Unable to initialize local LAF");
		}
	}

	private void setupLayout() {
		//Initialize layout manager
		gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		setLayout(gridbag);

		//Toolbar
		c.fill = GridBagConstraints.BOTH; //fill up horizontal + vertical
		c.gridwidth = GridBagConstraints.REMAINDER; //end of row
		c.weightx = 1.0; //fill horizontal
		c.ipady = MAX_TOOLBAR_PADDING; //add vertical padding
		toolbar = new JButton("FIRST");
		gridbag.setConstraints(toolbar, c);
		add(toolbar);

		//Central frame with canvas
		c.ipady = 0; //remove vertical padding
		c.weighty = 1.0; //fill up excess space from toolbar
		DisplayPanel button2 = new DisplayPanel();
		gridbag.setConstraints(button2, c);
		add(button2);
	}

	/**
	 * If window is below a certain size, shrink toolbar to make
	 * components fit.
	 */
	public void fixToolbar() {
		GridBagConstraints c = gridbag.getConstraints(toolbar);
		if(getHeight() < 400 || getWidth() < 400) {
			c.ipady = MIN_TOOLBAR_PADDING;
		} else {
			c.ipady = MAX_TOOLBAR_PADDING;
		}
		gridbag.setConstraints(toolbar, c);
	}

	public static void main(String[] args) {
		new MainWindow();
	}
}