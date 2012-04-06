package org.titanomachia.mclogcmdexec.command;

import java.util.List;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Help extends Command {
    @Override
    public void execute() {
    	List<String> help = ApplicationContext.getFactory().printHelp(getUser());
    	
    	for (String string : help) {
    		CommandUtils.writeToConsole(string, getUser());
    	}
    }

	@Override
	public String getDescription() {
		return "displays the list of available commands";
	}
}
