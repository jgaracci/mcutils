package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Whisper extends Command {
    private static final Log LOG = LogFactory.getLog( Move.class );

    @Override
    public void execute() {
        try {
        	String target = getArgs().substring(0, getArgs().indexOf(" "));
        	String message = getArgs().substring(getArgs().indexOf(" "));
        	
        	String targetUser = ApplicationContext.getValue( "alias." + target.toUpperCase() );
        	if (null == targetUser) {
        		targetUser = target;
        	}
        	
            Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"tell " + targetUser + " [" + getUser() + "]" + message + "\" > console.pipe"}).waitFor();
        }
        catch ( InterruptedException e ) {
            LOG.error( "Failed to exec: ", e );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to exec: ", e );
        }
    }
}
