package plus.monitor;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class CommandLine {
	public static void exec(List<String> args) throws IOException {
		ProcessBuilder pb = new ProcessBuilder(args);
		pb.redirectErrorStream(true);
		Process p = null;
		try {
			p = pb.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return;
		}
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		while ((line = br.readLine()) != null)
			System.out.println(line);
	}

	public static void launch(String command) throws IOException {
		command = "rundll32 shell32.dll,ShellExec_RunDLL " + command;
		exec(Arrays.asList(command.split(" ")));
	}

	public static void command(String command) throws IOException {
		command = "cmd /c " + command;
		exec(Arrays.asList(command.split(" ")));
	}

	public static void main(String args[]) throws IOException {
		// CommandLine.exec(Arrays.asList("cmd","/c","dir"));
		CommandLine.launch("notepad");
		CommandLine.command("cmd /c adb");
		/*
		 * String text = "Hello.\nYou are in danger."; PressKeys.type(text,
		 * 100);
		 */
	}
}
