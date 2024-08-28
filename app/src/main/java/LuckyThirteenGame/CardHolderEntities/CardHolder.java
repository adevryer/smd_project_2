package LuckyThirteenGame.CardHolderEntities;

import LuckyThirteenGame.Cards.CardDeck;
import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.CardAdapter;
import ch.aplu.jcardgame.CardListener;
import ch.aplu.jcardgame.Hand;

public abstract class CardHolder {
    private static final int NUM_STARTING_CARDS = 2;
    private static final int CARD_LEN = 2;
    private final CardDeck deck;
    private Hand playerHand;
    private CardListener cardListener = null;
    private Card cardForRemoval = null;

    public CardHolder(String initialCards, CardDeck deck, boolean isHuman) {
        this.deck = deck;
        playerHand = new Hand(deck.getDeck());
        addInitialCards(initialCards);

        if (isHuman) {
            //add a card listener if the player is human to register clicks
            cardListener = new CardAdapter() {
                public void leftDoubleClicked(Card card) {
                    cardForRemoval = card;
                    playerHand.setTouchEnabled(false);
                }
            };

            playerHand.addCardListener(cardListener);
        }
    }

    public void addInitialCards(String initialCards) {
        //add their pre-defined initial cards from the properties file if they have any
        if (initialCards != null && !initialCards.isEmpty()) {
            String[] initial = initialCards.split(",");
            for (String initialCard : initial) {
                if (initialCard.length() < CARD_LEN) {
                    continue;
                }

                //deal their pre-set initial cards
                Card card = deck.getCardFromList(deck.getPack().getCardList(), initialCard);
                if (card != null) {
                    card.removeFromHand(false);
                    playerHand.insert(card, false);
                }
            }
        }
    }

    public void assignRandomInitialCards() {
        //give random cards to the player if they don't already have two cards
        while (playerHand.getNumberOfCards() != NUM_STARTING_CARDS) {
            addRandomCardToHand();
        }

        playerHand.sort(Hand.SortType.SUITPRIORITY, false);
    }

    protected void addRandomCardToHand() {
        //get a random card from the card pack and insert into hand
        if (deck.getPack().isEmpty()) return;
        Card dealt = CardDeck.randomCard(deck.getPack().getCardList());
        dealt.removeFromHand(false);
        playerHand.insert(dealt, true);
    }

    public Hand getPlayerHand() {
        return playerHand;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public CardListener getCardListener() {
        return cardListener;
    }

    public Card getCardForRemoval() {
        return cardForRemoval;
    }

    public void setCardForRemovalNull() {
        this.cardForRemoval = null;
    }
}
