package org.titanomachia.mclogcmdexec.command;

public abstract class Command {
    private String user;
    private String args;

    public String getUser() {
        return user;
    }

    public void setUser( String user ) {
        this.user = user;
    }
    
    public abstract String getDescription();
    
    public String getArgs() {
        return args;
    }

    public void setArgs( String args ) {
        this.args = args;
    }
    
    public abstract void execute();
}