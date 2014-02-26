package plus.monitor;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Screenshot {
	public static BufferedImage take() throws HeadlessException, AWTException {
		BufferedImage screen = new Robot().createScreenCapture(new Rectangle(
				Toolkit.getDefaultToolkit().getScreenSize()));
		return screen;
	}


public static class ImagePanel extends JFrame {
	private static final long serialVersionUID = 1L;

	public ImagePanel(BufferedImage bi){
		this.setSize(bi.getWidth(), bi.getHeight());
		JLabel jLabel = new JLabel(new ImageIcon(bi));
		JPanel jPanel = new JPanel();
		jPanel.add(jLabel);
		this.add(jPanel);
	}
}


	public static void main(String args[]) throws HeadlessException, AWTException {
		BufferedImage screen = Screenshot.take();
		new ImagePanel(screen).setVisible(true);		
		
	}
}