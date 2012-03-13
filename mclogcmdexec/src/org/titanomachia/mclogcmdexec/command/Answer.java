package org.titanomachia.mclogcmdexec.command;

import java.io.IOException;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Answer extends Command {

	@Override
	public void execute() {
		Problem problem = ApplicationContext.getValue("quiz.problem." + getUser());
		
		if (null == problem) {
			return;
		}
		
		String answer = getArgs().trim();
		
		if ("".equals(answer)) {
			return;
		}
		
		boolean correct = problem.isCorrectAnswer(answer);
		
		String response = correct ? "Correct!" : ("Wrong... " + problem.getAnswer());

		Integer streak = ApplicationContext.getValue("quiz.streak." + getUser());
		if (correct) {
			if (null != streak) {
				streak = streak + 1;
				response += " That's " + streak + " in a row!";
			}
			else {
				streak = 1;
			}
			ApplicationContext.setValue("quiz.streak." + getUser(), streak);
		}
		else {
			if (null != streak && streak > 0) {
				response += ". You had a streak of " + streak + " going.";
			}
			ApplicationContext.setValue("quiz.streak." + getUser(), 0);
		}
		
		ApplicationContext.clearValue("quiz.problem." + getUser());

        writeToConsole(response, getUser());
	}

	private void writeToConsole(String message, String user) {
		try {
			Runtime.getRuntime().exec(new String[] {"/bin/bash", "-c", "echo \"tell " + user + " " + message + "\" > console.pipe"}).waitFor();
		}
		catch ( InterruptedException e ) {
		    e.printStackTrace();
		}
		catch ( IOException e ) {
		    e.printStackTrace();
		}
	}
}
