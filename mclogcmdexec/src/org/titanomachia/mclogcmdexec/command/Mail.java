package org.titanomachia.mclogcmdexec.command;

public class Mail extends Command {
    @Override
    public void execute() {
    	int argIndex = 0, nextIndex = getArgs().indexOf(" ");
    	
    	// mail list [n[,m]]
    	// mail read [n]
    	// mail new
    	// mail send
    	// mail delete [n[,m]]
    	
    	String command = getArgs().substring(argIndex);
    	String indexArgs = null;
    	if (nextIndex >= 0) {
    		command = getArgs().substring(argIndex, nextIndex);
    		indexArgs = getArgs().substring(nextIndex + 1);
    	}
    	
    	if ("list".equals(command)) {
    		// List message subjects
    		if (null != indexArgs) {
    			// List a range (up to 10)
    		}
    		else {
    			// List the first 10
    		}
    	}
    	else if ("read".equals(command)) {
    		// Read a message
    		if (null != indexArgs) {
    			// Read a specific message
    		}
    		else {
    			// Read the first message
    		}
    	}
    	else if ("new".equals(command)) {
    		// Create a new message
    	}
    	else if ("send".equals(command)) {
    		// Send any complete messages in your outbox
    	}
    	else if ("delete".equals(command)) {
    		// Delete messages
    		if (null != indexArgs) {
    			// Delete a range of messages
    		}
    		else {
    			// Delete the first message
    		}
    	}
    }

	@Override
	public String getDescription() {
		return "manages user mail";
	}
}
