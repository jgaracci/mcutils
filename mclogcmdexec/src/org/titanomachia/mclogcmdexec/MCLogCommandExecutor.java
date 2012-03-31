package org.titanomachia.mclogcmdexec;
import java.io.File;
import java.util.Map;

import org.jgdk.util.DataFeedReader;
import org.jgdk.util.TailFileReader;
import org.titanomachia.mclogcmdexec.command.Command;

public class MCLogCommandExecutor {
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
                return;
            }
            
            ApplicationContext.setFilePath("commands.context");
            
            ApplicationContext.load();
            
            Map<String, CommandMetaData> COMMANDS = new CommandPropertiesLoader().loadCommands();
            
            final CommandFactory factory = new CommandFactory(COMMANDS);
            
            DataFeedReader reader = new DataFeedReader( new TailFileReader(file) ) {
                @Override
                protected void processData( String line ) {
                    Command command = factory.getCommand( line );
                    if (null != command) {
                    	try {
                    		command.execute();
                    	}
                    	finally {
                    		ApplicationContext.save();
                    	}
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