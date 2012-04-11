package org.titanomachia.mclogcmdexec.command;

public class Card {
    private Suit suit;
    private CardType type;
    
    public Card(Suit suit, CardType type) {
        this.suit = suit;
        this.type = type;
    }

    public Suit getSuit() {
        return suit;
    }

    public CardType getType() {
        return type;
    }
}
