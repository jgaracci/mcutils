package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Move extends Command {
    private static final Log LOG = LogFactory.getLog( Move.class );

    @Override
    public void execute() {
        try {
            Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"tp " + getArgs() + "\" > console.pipe"}).waitFor();
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
		return "moves {source} to {target} location, e.g. /move jimbob billyjoe";
	}
    
}
