package LuckyThirteenGame.CardHolderEntities;

import LuckyThirteenGame.Cards.CardDeck;

public class PublicCards extends CardHolder {
    //all public cards methods/attributes are contained in the CardHolder superclass
    public PublicCards(String initialCards, CardDeck deck) {
        super(initialCards, deck, false);
    }
}
