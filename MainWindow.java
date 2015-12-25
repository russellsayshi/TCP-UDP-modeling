import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/* Holds the main GUI */
public class MainWindow extends JFrame {
	private PanelToolBar toolbar;
	protected final int canvasWidth;
	protected final int canvasHeight;

	public MainWindow(int w, int h) {
		canvasWidth = w;
		canvasHeight = h;
		initializeFrame();
		setupLayout();
		setVisible(true);
	}

	private void initializeFrame() {
		setTitle("Network Modeling");
		setSize(800, 500);
		setMinimumSize(new Dimension(150, 200));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
		setLayout(new BorderLayout());

		//Toolbar
		toolbar = new PanelToolBar();
		add(toolbar, BorderLayout.PAGE_START);

		//Central frame with canvas
		DisplayPanel button2 = new DisplayPanel(canvasWidth, canvasHeight);
		add(button2, BorderLayout.CENTER);
	}

	public static void main(String[] args) {
		new MainWindow(800, 600);
	}
}