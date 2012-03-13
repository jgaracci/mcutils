package org.titanomachia.mclogcmdexec.command;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Alias extends Command {
    @Override
    public void execute() {
    	ApplicationContext.setValue( "alias." + getArgs().toUpperCase(), getUser() );
    }
}
