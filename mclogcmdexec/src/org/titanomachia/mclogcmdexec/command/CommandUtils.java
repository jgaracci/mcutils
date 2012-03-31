package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

public class CommandUtils {
	public static void writeToConsole(String message, String user) {
		try {
			Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"tell " + user + " " + message + "\" > console.pipe"}).waitFor();
		}
		catch ( InterruptedException e ) {
		    e.printStackTrace();
		}
		catch ( IOException e ) {
		    e.printStackTrace();
		}
	}
}
