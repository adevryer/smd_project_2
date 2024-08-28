package LuckyThirteenGame.CardHolderEntities.Players;

import LuckyThirteenGame.CardHolderEntities.CardHolder;
import LuckyThirteenGame.Cards.CardDeck;
import ch.aplu.jcardgame.Card;

import static ch.aplu.jgamegrid.GameGrid.delay;


public class RandomPlayer extends Player {
    public RandomPlayer(String autoMovements, String initialCards, CardDeck deck) {
        super(autoMovements, initialCards, deck, false);
    }

    @Override
    public Card removeCardByPlayer(CardHolder publicCards, int timeToDelay) {
        //random player removes a random card
        Card randomToRemove = CardDeck.randomCard(getPlayerHand().getCardList());
        delay(timeToDelay);
        randomToRemove.removeFromHand(true);
        return randomToRemove;
    }
}
