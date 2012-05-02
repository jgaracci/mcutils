package org.titanomachia.mclogcmdexec.testharness;

import javax.swing.JFrame;

public class MCLogCmdExecTestHarnessController {

	private MCLogCmdExecTestHarnessModel model;
	private MCLogCmdExecTestHarnessView view;

	public void initialize() {
		model = new MCLogCmdExecTestHarnessModel();
		model.initialize();
		
		view = new MCLogCmdExecTestHarnessView();
		view.initialize();
		
		view.setModel( model );
		
		// Add listeners
	}

	public void showInDialog() {
		JFrame frame = new JFrame("MCLogCmdExec Test Harness");
		frame.getContentPane().add(view.getComponent());
		frame.setSize(240, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
