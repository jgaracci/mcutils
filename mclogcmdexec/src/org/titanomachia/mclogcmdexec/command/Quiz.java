package org.titanomachia.mclogcmdexec.command;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Quiz extends Command {
    @Override
    public void execute() {
    	try {
	    	int argIndex = 0, nextIndex = getArgs().indexOf(" ");
	    	if (nextIndex < 0) {
	    		CommandUtils.writeToConsole("Invalid Quiz args", getUser());
	    		showUsage();
	    		return;
	    	}
	    	String type = getArgs().substring(argIndex, nextIndex);
	    	
	    	argIndex = nextIndex;
	    	nextIndex = getArgs().indexOf(" ", argIndex + 1);
	    	if (nextIndex < 0) {
	    		CommandUtils.writeToConsole("Invalid Quiz args", getUser());
	    		showUsage();
	    		return;
	    	}
	    	String subType = getArgs().substring(argIndex + 1, nextIndex);
	    	
	    	Integer level = Integer.valueOf(getArgs().substring(nextIndex + 1));
	    	
	    	if (level < 1 || level > 3) {
	    		CommandUtils.writeToConsole("Level must be 1-3", getUser());
	    		return;
	    	}
	    	
    		Problem<?> problem = ApplicationContext.getValue("quiz.problem." + getUser());
    		
    		if (null == problem) {
    			problem = ProblemTypeFactory.createFactoryFor( type ).createProblemFor( subType, level );
        		ApplicationContext.setValue("quiz.problem." + getUser(), problem);
    		}

    		CommandUtils.writeToConsole("What is " + problem.toDisplayString() + " ?", getUser());
    	}
    	catch (Exception e) {
	        e.printStackTrace();
    	}
    }
    
    private void showUsage() {
    	CommandUtils.writeToConsole("Usage: Quiz {type {subtype}} {level}", getUser());
    	CommandUtils.writeToConsole("supported quizes - Math {+, -, *, /} {1-3}", getUser());
    	CommandUtils.writeToConsole("e.g. quiz math + 1", getUser());
    }
}
