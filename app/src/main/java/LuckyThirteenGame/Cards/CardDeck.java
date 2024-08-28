package LuckyThirteenGame.Cards;//maybe put the overall card deck as a separate class?
//he will probs not be happy if we don't...

import ch.aplu.jcardgame.Card;
import ch.aplu.jcardgame.Deck;
import ch.aplu.jcardgame.Hand;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CardDeck {
    public static final int SEED = 30008;
    public static final Random RANDOM = new Random(SEED);
    private final Deck deck = new Deck(Suit.values(), Rank.values(), "cover");
    private Hand pack = deck.toHand(false);

    public Card getCardFromList(List<Card> cards, String cardName) {
        Rank cardRank = getRankFromString(cardName);
        Suit cardSuit = getSuitFromString(cardName);
        for (Card card: cards) {
            if (card.getSuit() == cardSuit
                    && card.getRank() == cardRank) {
                return card;
            }
        }

        return null;
    }

    private Suit getSuitFromString(String cardName) {
        //get the suit from a specified string
        String suitString = cardName.substring(cardName.length() - 1);

        for (Suit suit : Suit.values()) {
            if (suit.getSuitShortHand().equals(suitString)) {
                return suit;
            }
        }
        return Suit.CLUBS;
    }

    private Rank getRankFromString(String cardName) {
        //get the rank of a card from the specified string
        String rankString = cardName.substring(0, cardName.length() - 1);
        int rankValue = Integer.parseInt(rankString);

        for (Rank rank : Rank.values()) {
            if (rank.getRankCardValue() == rankValue) {
                return rank;
            }
        }
        return Rank.ACE;
    }

    public Hand getPack() {
        return pack;
    }

    public Deck getDeck() {
        return deck;
    }

    public static Card randomCard(ArrayList<Card> list) {
        //get a random card from the card pack
        int x = RANDOM.nextInt(list.size());
        return list.get(x);
    }
}
