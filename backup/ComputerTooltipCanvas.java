import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.util.function.*;
import java.net.URL;

class ComputerTooltipCanvas extends JPanel implements ActionListener {
    Consumer<ComputerTooltipFrame.Result> callback;
    public ComputerTooltipCanvas(Consumer<ComputerTooltipFrame.Result> callback) {
        this.callback = callback;
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createHorizontalGlue());
        add(makeIconButton("freehand", "freehand", "Freehand drawing", "Freehand"));
        add(makeIconButton("rectangle", "rectangle", "Rectangle", "Rectangle"));
        add(makeIconButton("oval", "oval", "Oval", "Oval"));
        add(makeIconButton("computer_outline32", "computer", "Computer image", "Image"));
        add(Box.createHorizontalGlue());
    }
    private JButton makeIconButton(String name,
				   String actionCommand,
				   String toolTipText,
				   String altText) {
		//Find image
		String loc = "resources/" + name + ".png";
		URL imageURL = PanelToolBar.class.getResource(loc);

		//Create and setup button
		JButton button = new JButton();
		button.setActionCommand(actionCommand);
		button.setToolTipText(toolTipText);
		button.addActionListener(this);
        button.setBorderPainted(false); 
        button.setContentAreaFilled(false); 
        button.setFocusPainted(false); 
        button.setOpaque(false);
        button.setMargin(new Insets(10,5,0,5));

		if(imageURL != null) { //image found
			button.setIcon(new ImageIcon(imageURL, altText));
		} else {
			button.setText(altText);
			System.err.println("Unable to find resource: " + loc);
		}
		return button;
	}
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        Color c1 = new Color(235,235,235);
        Color c2 = new Color(210, 210, 210);
        GradientPaint gpaint = new GradientPaint(0, 10, c1, 0, ComputerTooltipFrame.HEIGHT, c2);
        g2d.setPaint(gpaint);
        GeneralPath gp = new GeneralPath();
        int wid = ComputerTooltipFrame.WIDTH-1;
        int hei = ComputerTooltipFrame.HEIGHT-1;
        gp.moveTo(wid/2, 0);
        gp.lineTo(wid/2+10, 10);
        gp.lineTo(wid-10, 10);
        gp.curveTo(wid-10, 10,
                   wid, 10,
                   wid, 20);
        gp.lineTo(wid, hei-10);
        gp.curveTo(wid, hei-10,
                   wid, hei,
                   wid-10, hei);
        gp.lineTo(10, hei);
        gp.curveTo(10, hei,
                   0, hei,
                   0, hei-10);
        gp.lineTo(0, 20);
        gp.curveTo(0, 20,
                   0, 10,
                   10, 10);
        gp.lineTo(wid/2-10, 10);
        gp.lineTo(wid/2, 0);
        g2d.fill(gp);
        g2d.setColor(new Color(105, 105, 105));
        g2d.draw(gp);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        String s = ae.getActionCommand();
        if("freehand".equals(s)) {
            callback.accept(ComputerTooltipFrame.Result.FREEHAND);
        } else if("rectangle".equals(s)) {
            callback.accept(ComputerTooltipFrame.Result.RECTANGLE);
        } else if("oval".equals(s)) {
            callback.accept(ComputerTooltipFrame.Result.OVAL);
        } else if("computer".equals(s)) {
            callback.accept(ComputerTooltipFrame.Result.IMAGE);
        }
    }
}