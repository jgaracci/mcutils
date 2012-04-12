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
        
        String args = getArgs();
        if (null == args) {
            args = "";
        }
        
        int houseHandSum = sumHand(houseHand);
        
        if ("HIT".equals( args.toUpperCase()) || "STAND".equals(  args.toUpperCase() )) {
            if ("HIT".equals( args.toUpperCase())) {
                currentHand.add( drawCard() );
            }
            
            int currentHandValue = sumHand(currentHand);
            
            if (currentHandValue > 21) {
                // Bust
                displayMessage( "** BUST **" );
                
                // Current hand is done
                playerHand.remove( currentHand );
                
                // Clean up if there are no more player hands
                if (playerHand.size() == 0) {
                    clearHand();
                }
            }
            else {
                // Update the house hand
                if ("STAND".equals( args.toUpperCase())) {
                    while (houseHandSum < 16) {
                        houseHand.add( drawCard() );
                        houseHandSum = sumHand(houseHand);
                    }
                }
                else {
                    if (houseHandSum < 16) {
                        houseHand.add( drawCard() );
                        houseHandSum = sumHand(houseHand);
                    }
                }
                
                if (houseHandSum > 21) {
                    displayMessage( "House busts, you win");
                    
                    // Current hand is done
                    playerHand.remove( currentHand );
                    
                    // Clean up if there are no more player hands
                    if (playerHand.size() == 0) {
                        clearHand();
                    }
                }

                if ("STAND".equals( args.toUpperCase())) {
                    // Compare with House
                    if (currentHandValue > houseHandSum) {
                        showPlayerHand(currentHand);
                        showHouseHand(true);
                        
                        displayMessage( "You win!" );
                        
                        // Current hand is done
                        playerHand.remove( currentHand );
                        
                        // Clean up if there are no more player hands
                        if (playerHand.size() == 0) {
                            clearHand();
                        }
                    }
                    else if (currentHandValue == houseHandSum) {
                        showPlayerHand(currentHand);
                        showHouseHand(true);
                        
                        displayMessage( "You pushed" );
                        
                        // Current hand is done
                        playerHand.remove( currentHand );
                        
                        // Clean up if there are no more player hands
                        if (playerHand.size() == 0) {
                            clearHand();
                        }
                    }
                }
                else {
                    showPlayerHand(currentHand);
                    showHouseHand(false);
                    saveHands();
                }
            }
        }
        else {
            if (currentHand.size() == 21) {
                showPlayerHand(currentHand);
                showHouseHand(true);
                
                // Blackjack
                displayMessage( "** BLACKJACK **" );
                
                // Compare with House
                if (houseHandSum < 21) {
                    displayMessage( "You win!" );
                }
                else {
                    displayMessage( "You pushed" );
                }
                
                // Current hand is done
                playerHand.remove( currentHand );
                
                // Clean up if there are no more player hands
                if (playerHand.size() == 0) {
                    clearHand();
                }
            }
            else {
                showPlayerHand(currentHand);
                showHouseHand(false);
                saveHands();
            }
        }
        
        if (deck.size() < 20) {
            deck = createDeck();
        }
        
        ApplicationContext.setValue( "blackjack.deck." + getUser(), deck );
    }

    private void saveHands() {
        ApplicationContext.setValue( "blackjack.hand." + getUser(), playerHand );
        ApplicationContext.setValue( "blackjack.hand.house." + getUser(), houseHand );
    }

    public void clearHand() {
        ApplicationContext.clearValue( "blackjack.hand." + getUser() );
        ApplicationContext.clearValue( "blackjack.hand.house." + getUser() );
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
                if (hand.indexOf( card ) == hand.size() - 1 && card.getType() == CardType.Ace ) {
                    if (null != value && value + 11 == 21) {
                        value = 21;
                        break;
                    }
                }
                value = (null == value ? 0 : value) + (card.getType().getValue() > 9 ? 10 : card.getType().getValue());
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
        displayMessage( hand );
    }

    public void displayMessage( String hand ) {
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
        displayMessage( hand );
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
        return "Play a game of Blackjack";
    }
    
    public static void main( String[] args ) {
        try {
            ApplicationContext.setFilePath( "blackjack.context" );
            ApplicationContext.load();
            
            Blackjack bj = new Blackjack() {
                @Override
                public void displayMessage( String hand ) {
                    System.out.println(hand);
                }
            };
            bj.execute();
            ApplicationContext.save();
            
            bj = new Blackjack() {
                @Override
                public void displayMessage( String hand ) {
                    System.out.println(hand);
                }
            };
            bj.setArgs( "HIT" );
            bj.execute();
            ApplicationContext.save();
        }
        catch ( Throwable t ) {
            t.printStackTrace( System.err );
        }
    }
}
