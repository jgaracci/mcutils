package org.rtd.command;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class D20 extends Command {
    private static final Log LOG = LogFactory.getLog( D20.class );

    @Override
    public void execute() {
        try {
            Integer value = new Random().nextInt(20) + 1;
            
            String output = getUser() + " rolled a " + value;
            
            Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"say " + output + "\" > console.pipe"}).waitFor();
        }
        catch ( InterruptedException e ) {
            LOG.error( "Failed to exec: ", e );
        }
        catch ( IOException e ) {
            LOG.error( "Failed to exec: ", e );
        }
    }
}