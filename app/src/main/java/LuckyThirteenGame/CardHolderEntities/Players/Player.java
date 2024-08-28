package LuckyThirteenGame.CardHolderEntities.Players;

import LuckyThirteenGame.CardHolderEntities.CardHolder;
import LuckyThirteenGame.Cards.CardDeck;
import LuckyThirteenGame.Cards.Rank;
import LuckyThirteenGame.Cards.Suit;
import ch.aplu.jcardgame.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ch.aplu.jgamegrid.GameGrid.delay;

public abstract class Player extends CardHolder {
    private List<String> playerAutoMovements = null;
    public static final int INIT_CARD_SCORE = 1000;

    private int playerScore = 0;
    private boolean isThirteen = false;

    public Player(String autoMovements, String initialCards, CardDeck deck, boolean isHuman) {
        super(initialCards, deck, isHuman);

        //get the player auto moves if some specified
        if (autoMovements != null && !autoMovements.isEmpty()) {
            playerAutoMovements = Arrays.asList(autoMovements.split(","));
        }
    }

    public abstract Card removeCardByPlayer(CardHolder publicCards, int timeToWait);

    public void addCard(int index) {
        if (playerAutoMovements == null || index >= playerAutoMovements.size()) {
            //if we have no movements specified, add a random card
            addRandomCardToHand();
        } else {
            //get the card that we are assigned to add next
            String[] cardStrings = playerAutoMovements.get(index).split("-");
            String cardDealtString = cardStrings[0];

            //find which card this is
            Card dealt = getDeck().getCardFromList(getDeck().getPack().getCardList(), cardDealtString);

            //put this card into our hand and remove it from the overall deck
            if (dealt != null) {
                dealt.removeFromHand(false);
                getPlayerHand().insert(dealt, true);
            } else {
                addRandomCardToHand();
                System.out.println("cannot draw card: " + cardDealtString);
            }
        }
    }

    public Card removeCard(int index, CardHolder publicCards, int timeToWait) {
        Card removed = null;

        if (playerAutoMovements == null || index >= playerAutoMovements.size()) {
            //remove a card specified by the player strategy
            //overridden by each player subclass
            setCardForRemovalNull();
            removed = removeCardByPlayer(publicCards, timeToWait);

        } else {
            //get the card that we are assigned to remove next
            String[] cardStrings = playerAutoMovements.get(index).split("-");
            String cardDealtString = cardStrings[1];
            cardDealtString = cardDealtString.strip();

            //find which card this is
            removed = getDeck().getCardFromList(getPlayerHand().getCardList(), cardDealtString);
            delay(timeToWait);

            //put this card into our hand and remove it from the overall deck
            if (removed != null) {
                removed.removeFromHand(true);
            } else {
                removed = CardDeck.randomCard(getPlayerHand().getCardList());
                removed.removeFromHand(true);
            }
        }

        return removed;
    }

    protected Card getLowestCard(ArrayList<Card> privateCards) {
        int lowestScore = INIT_CARD_SCORE;
        Card lowestCard = null;

        //find the lowest scoring card in the player hand
        for (Card card : privateCards) {
            Rank rank = (Rank) card.getRank();
            Suit suit = (Suit) card.getSuit();
            int score = rank.getScoreCardValue() * suit.getMultiplicationFactor();

            if (score < lowestScore) {
                lowestScore = score;
                lowestCard = card;
            }
        }

        return lowestCard;
    }

    public boolean isThirteenPlayer() {
        return isThirteen;
    }

    public void setThirteenPlayer(boolean isThirteen) {
        this.isThirteen = isThirteen;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }
}
