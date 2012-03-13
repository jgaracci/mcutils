package org.rtd;
import java.io.File;
import java.util.Map;

import org.rtd.command.Command;
import org.rtd.util.DataFeedReader;
import org.rtd.util.TailFileReader;

public class RTD {
    public static void main( String[] args ) {
        try {
            if ( args.length != 1 ) {
                System.err.println("Invalid args (" + args.length + "): " + args[0]);
                System.err.println("Usage RTD <logfile>");
                System.exit(1);
            }
            
            File file = new File(args[0]);
            
            if (!file.exists()) {
                System.err.println("File \"" + file + "\" does not exist");
            }
            
            Map<String, CommandMetaData> COMMANDS = new CommandPropertiesLoader().loadCommands();
            
            final CommandFactory factory = new CommandFactory(COMMANDS);
            
            DataFeedReader reader = new DataFeedReader( new TailFileReader(file) ) {
                @Override
                protected void processData( String line ) {
                    Command command = factory.getCommand( line );
                    if (null != command) {
                        command.execute();
                    }
                }
            };
            reader.setIsTailing(true);
            new Thread(reader, "Reader").start();
        }
        catch ( Throwable t ) {
            t.printStackTrace( System.err );
        }
    }


}