package org.titanomachia.mclogcmdexec.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.titanomachia.mclogcmdexec.ApplicationContext;

public class Blackjack extends Command {
    private List<Card> deck;
    private List<List<Card>> playerHand;
    private List<Card> houseHand;

    @Override
    public void execute() {
        deck = ApplicationContext.getValue( "blackjack.deck." + getUser() );
        if (null == deck) {
            deck = createDeck();
        }

        playerHand = ApplicationContext.getValue( "blackjack.hand." + getUser() );
        if (null == playerHand) {
            playerHand = new ArrayList<List<Card>>();
            List<Card> firstHand = new ArrayList<Card>();
            firstHand.add(drawCard());
            firstHand.add(drawCard());
            playerHand.add(firstHand);
        }
        houseHand = ApplicationContext.getValue( "blackjack.hand.house." + getUser() );
        if (null == houseHand) {
            houseHand = new ArrayList<Card>();
            houseHand.add(drawCard());
            houseHand.add(drawCard());
        }
        
        List<Card> currentHand = playerHand.get(0);
        
        showPlayerHand(currentHand);
        showHouseHand(false);
        
        if ("HIT".equals( getArgs().toUpperCase())) {
            currentHand.add( drawCard() );
        }
        
        int currentHandValue = sumHand(currentHand);
        
        if (currentHandValue == 21) {
            // Win
        }
        else if (currentHandValue > 21) {
            // Bust
            CommandUtils.writeToConsole( "** BUST **", getUser());
            
            // Currenty hand is done
            playerHand.remove( currentHand );
            
            // Clean up if there are no more player hands
            if (playerHand.size() == 0) {
                ApplicationContext.clearValue( "blackjack.hand." + getUser() );
                ApplicationContext.clearValue( "blackjack.hand.house." + getUser() );
            }
        }
        
        if (deck.size() < 20) {
            deck = createDeck();
        }
        
        ApplicationContext.setValue( "blackjack.deck." + getUser(), deck );
    }
    
    private int sumHand(List<Card> hand) {
        Integer value = null;
        // Look for blackjack
        if (hand.size() == 2) {
            if (hand.get( 0 ).getType() == CardType.Ace && hand.get( 1 ).getType().getValue() > 9) {
                value = 21;
            }
            else if (hand.get( 1 ).getType() == CardType.Ace && hand.get( 0 ).getType().getValue() > 9) {
                value = 21;
            }
        }
        
        if (null == value) {
            // Sort cards so Ace is last
            Collections.sort( hand, new Comparator<Card>() {
                @Override
                public int compare( Card o1, Card o2 ) {
                    return o2.getType().getValue() - o1.getType().getValue();
                }
            });
            
            // Look for 21
            for(Card card : hand) {
                // If this is the last card and an Ace
                if (hand.indexOf( card ) == hand.size() - 1) {
                    if (value + 11 == 21) {
                    }
                }
                value += card.getType().getValue();
                
            }
        }
        return value;
    }

    private void showHouseHand( boolean reveal ) {
        String hand = "House Cards: ";
        for (Card card : houseHand) {
            if (houseHand.indexOf( card ) == 0) {
                hand += "******";
            }
            else {
                hand += card.toString();
            }
            if (houseHand.indexOf( card ) < houseHand.size() - 1) {
                hand += ", ";
            }
        }
        CommandUtils.writeToConsole( hand, getUser() );
    }

    private void showPlayerHand(List<Card> currentHand) {
        String hand = "Cards: ";
        for (Card card : currentHand) {
            hand += card.toString();
            if (currentHand.indexOf( card ) < currentHand.size() - 1) {
                hand += ", ";
            }
        }
        CommandUtils.writeToConsole( hand, getUser() );
    }

    private Card drawCard() {
        return deck.remove( new Random().nextInt( deck.size() ) );
    }

    private List<Card> createDeck() {
        List<Card> deck = new ArrayList<Card>();
        for ( Suit suit : Suit.values() ) {
            for ( CardType type : CardType.values() ) {
                deck.add(new Card(suit, type));
            }
        }
        Collections.shuffle( deck );
        return deck;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
