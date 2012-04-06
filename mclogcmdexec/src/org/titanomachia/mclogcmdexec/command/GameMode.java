package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GameMode extends Command {
    private static final Log LOG = LogFactory.getLog( Move.class );

    @Override
    public void execute() {
        try {
            Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"gamemode " + getUser() + " " + getArgs() + "\" > console.pipe"}).waitFor();
        }
        catch ( InterruptedException e ) {
            LOG.error( "Failed to exec: ", e );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to exec: ", e );
        }
    }

	@Override
	public String getDescription() {
		return "changes the gamemode between 0 - Survival and 1 - Creative";
	}
}
