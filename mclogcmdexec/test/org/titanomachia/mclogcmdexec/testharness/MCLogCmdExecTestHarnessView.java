package org.titanomachia.mclogcmdexec.testharness;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

public class MCLogCmdExecTestHarnessView {
	private JPanel panel;

	public void initialize() {
		initComponents();
		layoutComponents();
	}

	private void initComponents() {
		panel = new JPanel();
	}

	private void layoutComponents() {
		panel.setLayout(new BorderLayout());
	}

	public void setModel(MCLogCmdExecTestHarnessModel model) {
	}

	public Component getComponent() {
		return panel;
	}
}
