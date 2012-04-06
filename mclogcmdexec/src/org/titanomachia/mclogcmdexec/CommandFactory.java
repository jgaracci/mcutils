package org.titanomachia.mclogcmdexec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.titanomachia.mclogcmdexec.command.Command;

public class CommandFactory {
    private static Pattern CONSOLE_COMMAND_PATTERN = Pattern.compile( "^.+[]] ([A-Za-z_0-9]*) issued server command: ([^ ]*)[ ]{0,1}(([^ ]*[ ]{0,1})*)" );
    
    private Map<String, CommandMetaData> metaDataByName;
    
    public CommandFactory(Map<String,CommandMetaData> commands) {
        this.metaDataByName = commands;
    }   
    
    public Command getCommand(String line) {
        Command command = null;
        
        Matcher matcher = CONSOLE_COMMAND_PATTERN.matcher( line );
        if (matcher.matches()) {
            String user = matcher.group( 1 );
            String commandName = matcher.group( 2 ).toUpperCase();
            String args = matcher.group( 3 );
            
            command = createCommand(commandName, user);
            if (null != command) {
                command.setUser(user);
                command.setArgs(args);
            }
        }
        
        return command;
    }
    
    public Command createCommand( String commandName, String userName ) {
        Command command = null;
        CommandMetaData commandMetaData = metaDataByName.get(commandName);
        if (null != commandMetaData && ( commandMetaData.getAuthorizedUsers().isEmpty() || commandMetaData.getAuthorizedUsers().contains(userName) ) ) {
            try {
                command = (Command)commandMetaData.getCommandClass().newInstance();
            }
            catch ( InstantiationException e ) {
                e.printStackTrace();
            }
            catch ( IllegalAccessException e ) {
                e.printStackTrace();
            }
        }
        return command;
    }
    
    public List<String> printHelp() {
    	List<String> help = new ArrayList<String>();
    	
    	for(String name : metaDataByName.keySet()) {
    		Command command = createCommand(name, null);
    		help.add(StringUtils.rightPad(name, 8) + "-  " + command.getDescription());
    	}
    	
    	return help;
    }
}