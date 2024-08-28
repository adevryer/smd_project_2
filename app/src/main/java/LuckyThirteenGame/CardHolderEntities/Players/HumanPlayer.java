package LuckyThirteenGame.CardHolderEntities.Players;

import LuckyThirteenGame.CardHolderEntities.CardHolder;
import LuckyThirteenGame.Cards.*;
import ch.aplu.jcardgame.Card;

import static ch.aplu.jgamegrid.GameGrid.delay;

public class HumanPlayer extends Player {
    public HumanPlayer(String autoMovements, String initialCards, CardDeck deck) {
        super(autoMovements, initialCards, deck, true);
    }

    @Override
    public Card removeCardByPlayer(CardHolder publicCards, int timeToDelay) {
        if (getCardListener() != null) {
            //human player clicks a card which will be removed
            getPlayerHand().setTouchEnabled(true);
            while (getCardForRemoval() == null) {
                delay(timeToDelay);
            }
            getCardForRemoval().removeFromHand(true);
            return getCardForRemoval();
        }

        return null;
    }
}
