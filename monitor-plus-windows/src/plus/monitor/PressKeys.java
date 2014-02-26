package plus.monitor;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;

public class PressKeys {
	private static int delay = 0;
	public static void type(String text) {
		int keyChar;
		try {
			Robot robot = new Robot();
			for (int i = 0; i < text.length(); i++) {
				if (Character.isUpperCase(text.charAt(i)))
					robot.keyPress(KeyEvent.VK_SHIFT);
				keyChar = KeyEvent.getExtendedKeyCodeForChar(text.charAt(i));
				robot.keyPress(keyChar);
				robot.keyRelease(keyChar);
				if (Character.isUpperCase(text.charAt(i)))
					robot.keyRelease(KeyEvent.VK_SHIFT);
				if(delay > 0)
					Thread.sleep(delay);
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void type(String text, int delayMilli) {
		delay = delayMilli;
		type(text);
	}

}
