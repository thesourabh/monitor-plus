package plus.monitor;

import java.awt.image.BufferedImage;

import com.github.sarxos.webcam.Webcam;


public class Camera {
	
	static Webcam webcam;
	public static BufferedImage pic() {
		webcam = Webcam.getDefault();
		webcam.open();
		return webcam.getImage();
	}
	
	public static void close() {
		webcam.close();
	}

}
