package org.titanomachia.mclogcmdexec.command;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Whisper extends Command {
    @Override
    public void execute() {
    	String target = getArgs().substring(0, getArgs().indexOf(" "));
    	String message = getArgs().substring(getArgs().indexOf(" "));
    	
    	String targetUser = ApplicationContext.getValue( "alias." + target.toUpperCase() );
    	if (null == targetUser) {
    		targetUser = target;
    	}
    	
    	CommandUtils.writeToConsole("[" + getUser() + "] " + message, targetUser);
    }
}
