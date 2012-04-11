package org.titanomachia.mclogcmdexec.command;

public enum CardType {
    Ace("Ace", 1),
    King("King", 13),
    Queen("Queen", 12),
    Jack("Jack", 11),
    Ten("10", 10),
    Nine("9", 9),
    Eight("8", 8),
    Seven("7", 7),
    Six("6", 6),
    Five("5", 5),
    Four("4", 4),
    Three("3", 3),
    Two("2", 2);
    
    private String display;
    private int value;

    private CardType(String display, int value) {
        this.display = display;
        this.value = value;
    }

    public String getDisplay() {
        return display;
    }

    public int getValue() {
        return value;
    }
}
