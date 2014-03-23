package plus.monitor;

import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server {
	public static void main(String args[]) throws IOException {

		final int CMD_ACTIVITY = 1, LAUNCH_APP = 2, SCREENSHOT_CONTROLS = 3, PPT_CONTROLS = 4, MEDIA_CONTROLS = 5, WEBCAM_SNAP = 6, WEBCAM_CLOSE = 66;
		String line = "a";
		ServerSocket servSock = null;
		Socket clientSock = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
		BufferedImage screen = null;
		while (true) {
			try {
				servSock = new ServerSocket(13579);
				servSock.setReuseAddress(true);
				clientSock = servSock.accept();
				is = clientSock.getInputStream();
				os = clientSock.getOutputStream();
				br = new BufferedReader(new InputStreamReader(is));
				int ch = Integer.parseInt(br.readLine());
				switch (ch) {
				case -1:
					return;
				case 0:
					break;
				case CMD_ACTIVITY:
					line = br.readLine();
					String cmdOutput = CommandLine.command(line);
					PrintWriter pw = new PrintWriter(os, true);
					System.out.println("aaaaaa" + cmdOutput + "bbbbbb" + cmdOutput.length());
					pw.println(cmdOutput);
					pw.close();
					break;
				case LAUNCH_APP:
					line = br.readLine();
					CommandLine.launch(line);
					break;
				case PPT_CONTROLS:
					int pptCommand = Integer.parseInt(br.readLine());
					switch (pptCommand) {
					case 1:
						PressKeys.type("p");
						break;
					case 2:
						PressKeys.type("n");
						break;
					case 3:
						PressKeys.function(5);
						break;
					case 0:
						PressKeys.function(0);
						break;
					}
					break;
				case MEDIA_CONTROLS:
					int mediaCommand = Integer.parseInt(br.readLine());
					switch (mediaCommand) {
					case 5:
						PressKeys.type(" ");
						break;
					case 0:
						PressKeys.type(".");
						break;
					case 15:
						PressKeys.alt(1);
						break;
					default:
						PressKeys.arrow(mediaCommand);
						break;
					}
					break;
				case WEBCAM_SNAP:
					screen = null;
					try {
						screen = Camera.pic();
					} catch (HeadlessException e) {
					}
					sendPicture(os, screen);
					break;
				case WEBCAM_CLOSE:
					Camera.close();
					break;
				case SCREENSHOT_CONTROLS:
					screen = null;
					line = br.readLine();
					int width = Integer.parseInt(line);
					int height = Integer.parseInt(br.readLine());
					try {
						screen = Screenshot.take(width, height);
					} catch (Exception e) {}
					sendPicture(os, screen);
					break;
				}
			} finally {
				br.close();
				is.close();
				os.close();
				clientSock.close();
				servSock.close();
			}
		}
	}

	private static void sendPicture(OutputStream os, BufferedImage screen) throws IOException {
		PrintWriter pw = new PrintWriter(os, true);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(screen, "png", baos);
		baos.close();
		Integer contentLength = baos.size();
		pw.println(contentLength);
		byte[] b = baos.toByteArray();
		os.write(b);
		os.flush();

	}

}
