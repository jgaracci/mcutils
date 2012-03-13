package org.rtd.command;

import org.rtd.ApplicationContext;

public class Alias extends Command {
    @Override
    public void execute() {
    	ApplicationContext.setValue( "alias." + getArgs().toUpperCase(), getUser() );
    }
}
