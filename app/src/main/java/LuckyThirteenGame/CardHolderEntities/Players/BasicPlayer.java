package LuckyThirteenGame.CardHolderEntities.Players;

import LuckyThirteenGame.CardHolderEntities.CardHolder;
import LuckyThirteenGame.Cards.CardDeck;
import ch.aplu.jcardgame.Card;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class BasicPlayer extends Player {
    public BasicPlayer(String autoMovements, String initialCards, CardDeck deck) {
        super(autoMovements, initialCards, deck,false);
    }

    @Override
    public Card removeCardByPlayer(CardHolder publicCards, int timeToWait) {
        //get the lowest card in the hand
        Card cardToRemove = getLowestCard(this.getPlayerHand().getCardList());
        delay(timeToWait);

        if (cardToRemove != null) {
            //remove it
            cardToRemove.removeFromHand(true);
        } else {
            //if we can't remove card due to error, just remove a random one
            cardToRemove = CardDeck.randomCard(getPlayerHand().getCardList());
            cardToRemove.removeFromHand(true);
            System.out.println("No lowest card found, had to remove a random card instead");
        }

        return cardToRemove;
    }
}
