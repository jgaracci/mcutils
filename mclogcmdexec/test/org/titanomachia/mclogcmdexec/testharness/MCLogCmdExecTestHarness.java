package org.titanomachia.mclogcmdexec.testharness;

public class MCLogCmdExecTestHarness {
	public static void main(String[] args) {
		try {
			MCLogCmdExecTestHarnessController controller = new MCLogCmdExecTestHarnessController();
			controller.initialize();
			controller.showInDialog();
		}
		catch (Throwable t) {
			t.printStackTrace(System.err);
		}
	}
}
