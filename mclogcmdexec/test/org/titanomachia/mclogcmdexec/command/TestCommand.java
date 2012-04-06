package org.titanomachia.mclogcmdexec.command;

import org.titanomachia.mclogcmdexec.command.Command;


public class TestCommand extends Command {
	@Override
	public void execute() {
		System.out.println("Test");
	}

	@Override
	public String getDescription() {
		return "test";
	}
}
