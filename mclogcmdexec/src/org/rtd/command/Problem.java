package org.rtd.command;

public interface Problem {
	String toDisplayString();
	boolean isCorrectAnswer(String answer);
	String getAnswer();
}
