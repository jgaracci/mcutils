package org.rtd;

import java.util.HashSet;
import java.util.Set;

public class CommandMetaData {
	private Class<?> commandClass;
	private Set<String> authorizedUsers;

	public CommandMetaData(Class<?> commandClass, Set<String> authorizedUsers) {
		this.commandClass = commandClass;
		this.authorizedUsers = new HashSet<String>(authorizedUsers);
	}

	public Class<?> getCommandClass() {
		return commandClass;
	}

	public Set<String> getAuthorizedUsers() {
		return authorizedUsers;
	}

	@Override
	public String toString() {
		return String.valueOf(commandClass) + " : " + authorizedUsers;
	}
	
	
}
