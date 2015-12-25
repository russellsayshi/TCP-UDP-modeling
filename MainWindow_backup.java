import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Holds the main GUI */
public class MainWindow extends JFrame {
	private static final int TOOLBAR_MAX_SIZE = 50;
	private static final int TOOLBAR_MIN_SIZE = 10;
	private PanelToolBar toolbar;

	public MainWindow() {
		super(new BorderLayout());
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
		//Toolbar
		toolbar = new PanelToolBar();
		toolbar.setMaxDesiredBound(TOOLBAR_MAX_SIZE);
		add(toolbar, BorderLayout.PAGE_START);

		//Central frame with canvas
		DisplayPanel button2 = new DisplayPanel();
		add(button2, BorderLayout.CENTER);
	}

	/**
	 * If window is below a certain size, shrink toolbar to make
	 * components fit.
	 */
	public void fixToolbar() {
		if(getHeight() < 400 || getWidth() < 400) {
			toolbar.setMaxDesiredBound(TOOLBAR_MIN_SIZE);
		} else {
			toolbar.setMaxDesiredBound(TOOLBAR_MAX_SIZE);
		}
	}

	public static void main(String[] args) {
		new MainWindow();
	}
}