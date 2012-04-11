package org.titanomachia.mclogcmdexec.command;

public enum Suit {
    Spades("Spades"),
    Hearts("Hearts"),
    Clubs("Clubs"),
    Diamonds("Diamonds");
    
    private String display;

    private Suit(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
