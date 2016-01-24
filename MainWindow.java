import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.UIManager.*;

/* Holds the main GUI */
class MainWindow extends JFrame {
	private PanelToolBar toolbar;
    private DisplayPanel dp;
    private Console console;
    private JScrollPane consoleScroll;
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

/*        try {
    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        System.out.println(info.getName());
        if ("Metal".equals(info.getName())) {
            //UIManager.setLookAndFeel(info.getClassName());
            break;
        }
    }
} catch (Exception e) {
    // If Nimbus is not available, you can set the GUI to another look and feel.
}*/
        //TODO: ADD LOOK AND FEEL PICKER
        
		/*//Initialize local look and feel
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
		}*/
	}

	private void setupLayout() {
		//Initialize layout manager
		setLayout(new BorderLayout());

		//Toolbar
		toolbar = new PanelToolBar(this);
		add(toolbar, BorderLayout.PAGE_START);

		//Central frame with canvas
		dp = new DisplayPanel(canvasWidth, canvasHeight);
        toolbar.initializeSelectAction();
        
        console = new Console(this, dp.getNetwork());
        consoleScroll = new JScrollPane(console);
        console.setScrollPane(consoleScroll);
        
        JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consoleScroll, dp);
        jsp.setOneTouchExpandable(false);
        jsp.getLeftComponent().setMinimumSize(new Dimension(0, 0));
		add(jsp, BorderLayout.CENTER);
	}
    
    protected DisplayPanel getDisplayPanel() {
        return dp;
    }
    
    public Console getConsole() {
        return console;
    }

	public static void main(String[] args) {
		new MainWindow(800, 600);
	}
    
    public void notifyDisplayPanelToUpdate() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                dp.updateAll();
            }
        });
    }
}