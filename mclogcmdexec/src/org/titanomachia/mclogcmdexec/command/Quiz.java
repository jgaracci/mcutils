package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.titanomachia.mclogcmdexec.ApplicationContext;


public class Quiz extends Command {
    @Override
    public void execute() {
    	try {
	    	int argIndex = 0, nextIndex = getArgs().indexOf(" ");
	    	if (nextIndex < 0) {
	    		writeToConsole("Invalid Quiz args");
	    		showUsage();
	    		return;
	    	}
	    	String type = getArgs().substring(argIndex, nextIndex);
	    	
	    	argIndex = nextIndex;
	    	nextIndex = getArgs().indexOf(" ", argIndex + 1);
	    	if (nextIndex < 0) {
	    		writeToConsole("Invalid Quiz args");
	    		showUsage();
	    		return;
	    	}
	    	String subType = getArgs().substring(argIndex + 1, nextIndex);
	    	
	    	Integer level = Integer.valueOf(getArgs().substring(nextIndex + 1));
	    	
	    	if (level < 1 || level > 4) {
	    		writeToConsole("Level must be 1-4");
	    		return;
	    	}
	    	
    		Problem problem = ApplicationContext.getValue("quiz.problem." + getUser());
    		
    		if (null == problem) {
    			problem = ProblemTypeFactory.createFactoryFor( type ).createProblemFor( subType, level );
        		ApplicationContext.setValue("quiz.problem." + getUser(), problem);
    		}

            writeToConsole("What is " + problem.toDisplayString() + " ?");
    	}
    	catch (Exception e) {
	        e.printStackTrace();
    	}
    }
    
    private void showUsage() {
    	writeToConsole("Usage: Quiz {type {subtype}} {level}");
		writeToConsole("supported quizes - Math {+, -, *, /} {1-4}");
		writeToConsole("e.g. quiz math + 1");
    }

	private void writeToConsole(String message) {
		try {
			Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"tell " + getUser() + " " + message + "\" > console.pipe"}).waitFor();
		}
		catch ( InterruptedException e ) {
		    e.printStackTrace();
		}
		catch ( IOException e ) {
		    e.printStackTrace();
		}
	}
}
