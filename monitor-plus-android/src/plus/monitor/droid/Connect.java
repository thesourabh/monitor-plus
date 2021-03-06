package plus.monitor.droid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Connect {
	private Socket clientSock;
	private PrintWriter pw;
	private BufferedReader br;

	Connect(String host, int port) {
		clientSock = new Socket();
		try {
			clientSock.connect(new InetSocketAddress(host, port), 8000);
			pw = new PrintWriter(clientSock.getOutputStream(), true);
			br = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
		} catch (Exception e) {
			return;
		}
	}
	public void sendCommand(int commandCode) {
		pw.println(commandCode);
	}
	public void println(String str) {
		pw.println(str);
	}
	public void println(int str) {
		pw.println("" + str);
	}
	public String readLine() throws IOException {
		return br.readLine();
	}
	public InputStream getInputStream() throws IOException {
		return clientSock.getInputStream();
	}
	public OutputStream getOutputStream() throws IOException {
		return clientSock.getOutputStream();
	}
	
	
	public void close() {
		if (pw != null)
			pw.close();
		try {
			if (clientSock != null)
				clientSock.close();
		} catch (IOException e) {
		} finally {
			pw = null;
			clientSock = null;
		}
	}

}
