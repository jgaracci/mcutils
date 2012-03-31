package org.titanomachia.mclogcmdexec.command;

public interface ProblemFactory<T extends Problem<?>> {
	T createProblem(Integer level);
}
