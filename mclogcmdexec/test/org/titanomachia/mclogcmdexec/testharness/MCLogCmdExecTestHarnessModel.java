package org.titanomachia.mclogcmdexec.testharness;

import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class MCLogCmdExecTestHarnessModel {
	private Document userNameModel;
	private Document commandModel;
	private Document outputModel;
	
	public void initialize() {
		userNameModel = new PlainDocument();
		commandModel = new PlainDocument();
		outputModel = new PlainDocument();
	}

	public Document getUserNameModel() {
		return userNameModel;
	}

	public Document getCommandModel() {
		return commandModel;
	}

	public Document getOutputModel() {
		return outputModel;
	}
}
