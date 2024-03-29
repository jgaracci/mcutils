package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WhiteList extends Command {
    private static final Log LOG = LogFactory.getLog( WhiteList.class );
    
    @Override
    public void execute() {
        try {
            Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"whitelist " + getArgs() + "\" > console.pipe"}).waitFor();
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
		return "change the whilelist, add/remove a player, e.g. /xwhitelist add jimbob";
	}
}
