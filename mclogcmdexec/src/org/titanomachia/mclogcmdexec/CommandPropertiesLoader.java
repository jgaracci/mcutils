package org.titanomachia.mclogcmdexec;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommandPropertiesLoader {
    private static final Log LOG = LogFactory.getLog( CommandPropertiesLoader.class );
    
    public Map<String, CommandMetaData> loadCommands() {
        Map<String, CommandMetaData> commandMetaDataByName = new HashMap<String, CommandMetaData>();
        
        // Read them from properties file
        Properties properties = new Properties();
        
        try {
            loadProperties(properties);
            
            for(Object key : properties.keySet()) {
                if (((String)key).contains(".")) {
                	continue;
                }
                String commandClassName = (String) properties.get( key );
                Set<String> authorizedUsers = new HashSet<String>();
                String authorizedUsersString = (String) properties.get(key + ".authorizedUsers");
                if (null != authorizedUsersString) {
                	for(StringTokenizer tokenizer = new StringTokenizer(authorizedUsersString, ","); tokenizer.hasMoreTokens();) {
                		String user = tokenizer.nextToken();
                		authorizedUsers.add( user );
                	}
                }
                try {
                    Class<?> commandClass = Class.forName( commandClassName );
                    CommandMetaData commandMetaData = new CommandMetaData( commandClass, authorizedUsers );
                    commandMetaDataByName.put( String.valueOf(key).toUpperCase(), commandMetaData );
                }
                catch ( ClassNotFoundException e ) {
                    LOG.error( "Unable to register command class " + commandClassName, e );
                }
            }
        }
        catch ( FileNotFoundException e ) {
            LOG.error( "Unable to load commands.properties", e );
        }
        catch ( IOException e ) {
            LOG.error( "Unable to load commands.properties", e );
        }
        
        System.out.println(commandMetaDataByName);
        
        return commandMetaDataByName;
    }

	void loadProperties(Properties properties) throws IOException, FileNotFoundException {
		properties.load( new FileReader("commands.properties") );
	}
}
