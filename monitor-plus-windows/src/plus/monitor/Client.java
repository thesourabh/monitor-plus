package plus.monitor;

/*
 * 
 * 
 * DEPRECATED: Use the android client instead 
 * 
 * 
 */


import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	public static void main(String args[]) throws IOException {
		Socket clientSock = null;
		try {
			clientSock = new Socket("Daenerys", 13579);
		} catch (UnknownHostException uhe) {
			System.out.println("Host unknown.");
			System.exit(1);
		} catch (IOException ie) {
			System.out
					.println("Could not connect to target computer.\nThe program may not be running on the target computer.");
			System.exit(1);
		}
		PrintWriter ow = new PrintWriter(clientSock.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		while (true) {
			System.out
					.print("1. Shutdown\n2. Enter terminal\n\n0. Exit\n\nEnter choice: ");
			switch (Integer.parseInt(br.readLine())) {
			case 1:
				break;
			case 2:
				System.out
						.print("Type \'exit\' to exit terminal at any time\n>>> ");
				while (!(line = br.readLine()).equals("exit")) {
					ow.println("cmd /c " + line);
					ow.flush();
					System.out.print(">>> ");
				}
				line = "";
				break;
			case 0:
				ow.println("clientserver: exit");
				ow.flush();
				ow.close();
				clientSock.close();
				System.exit(1);
				break;
			}
		}
	}
}
