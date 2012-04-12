package org.titanomachia.mclogcmdexec.command;

import java.io.Serializable;

public class Card implements Serializable {
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

    @Override
    public String toString() {
        return type.getDisplay() + " of " + suit.getDisplay();
    }
}
