package org.titanomachia.mclogcmdexec.command;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Prizes extends Command {

	@Override
	public void execute() {
		Integer points = ApplicationContext.getValue("quiz.points." + getUser());
		
		if (null == points || 0 == points) {
			CommandUtils.writeToConsole("You have no points", getUser());
			return;
		}
		else {
			CommandUtils.writeToConsole("You have " + points + " points", getUser());
		}
	}
}
