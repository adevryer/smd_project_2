package LuckyThirteenGame.CardHolderEntities.Players;

import ch.aplu.jcardgame.Card;

public interface DiscardedCardListener {
    void onCardDisposed(Card card);
}
