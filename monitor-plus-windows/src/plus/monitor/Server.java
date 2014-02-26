package plus.monitor;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

public class Server {
	public static void main(String args[]) throws IOException {
		String line = "a";
		ServerSocket servSock = null;
		Socket clientSock = null;
		InputStream is = null;
		OutputStream os = null;
		BufferedReader br = null;
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
				case 0:
					return;
				case 1:
					line = br.readLine();
					CommandLine.command(line);
					break;
				case 2:
					line = br.readLine();
					CommandLine.launch(line);
					break;
				case 3:
					BufferedImage screen = null;
					try {
						screen = Screenshot.take();
					} catch (HeadlessException e) {
					} catch (AWTException e) {
					}
					PrintWriter pw = new PrintWriter(os, true);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write(screen, "png", baos);
					baos.close();
					Integer contentLength = baos.size();
					pw.println(contentLength);
					byte[] b = baos.toByteArray();
					os.write(b);
					os.flush();
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

}
