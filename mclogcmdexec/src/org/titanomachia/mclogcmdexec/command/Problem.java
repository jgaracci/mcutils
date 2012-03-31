package org.titanomachia.mclogcmdexec.command;

import java.io.Serializable;
import java.util.List;

public interface Problem<T extends Problem<?>> extends Serializable {
	String toDisplayString();
	boolean isCorrectAnswer(String answer);
	String getAnswer();
	Integer getLevel();
	Integer calculatePoints(List<T> streak);
}
