package LuckyThirteenGame.CardHolderEntities.Players;

import LuckyThirteenGame.CardHolderEntities.CardHolder;
import LuckyThirteenGame.Cards.CardDeck;
import LuckyThirteenGame.Cards.Rank;
import LuckyThirteenGame.ScoreStrategy.ThirteenSumChecker;
import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static ch.aplu.jgamegrid.GameGrid.delay;

@SuppressWarnings("unchecked")
public class CleverPlayer extends Player implements DiscardedCardListener {
    public static final int TARGET_VALUE = 13, VIABILITY_THRESHOLD = 3;
    private HashMap<Integer, Integer> disposedValues = new HashMap<>();
    private ThirteenSumChecker sumChecker = ThirteenSumChecker.getInstance();

    public CleverPlayer(String autoMovements, String initialCards, CardDeck deck) {
        super(autoMovements, initialCards, deck, false);

        for (int i = 0; i <= TARGET_VALUE; i++) {
            disposedValues.put(i, 0);
        }
    }

    @Override
    public Card removeCardByPlayer(CardHolder publicCards, int timeToDelay) {
        //get a list of player private and public cards
        ArrayList<Card> playerCardList = this.getPlayerHand().getCardList();
        ArrayList<Card> publicCardList = publicCards.getPlayerHand().getCardList();
        ArrayList<Card> tempCards = (ArrayList<Card>) playerCardList.clone();

        //assign random card to remove in case of error
        Card cardToRemove = CardDeck.randomCard(getPlayerHand().getCardList());

        //check all cards to see if we can achieve thirteen with current card set
        HashSet<Card> cardsInThirteen = findThirteenSolutions(playerCardList, publicCardList);


        switch (cardsInThirteen.size()) {
            case 0:
                //check if we might be able to achieve thirteen with the current cards
                ArrayList<Card> unusableCards = unusableCards(playerCardList);

                if (unusableCards.isEmpty()) {
                    //all cards are fine, remove the lowest one
                    cardToRemove = getLowestCard(playerCardList);
                } else {
                    //some cards are more unhelpful than others, remove the lowest of these
                    cardToRemove = getLowestCard(unusableCards);
                }

                break;

            case 1:
                //remove the lowest card out of the two
                Card thirteenCard = (Card) cardsInThirteen.toArray()[0];
                tempCards.remove(thirteenCard);
                cardToRemove = getLowestCard(tempCards);
                break;

            case 2:
                //remove the card which does not add to the thirteen solutions
                for (Card card : playerCardList) {
                    if (!cardsInThirteen.contains(card)) {
                        cardToRemove = card;
                    }
                }

                break;

            case 3:
                //remove the lowest card in the hand
                Card lowest = getLowestCard(playerCardList);

                //check if this card will impact the ability to score thirteen beforehand
                if (canRemoveCard(lowest, playerCardList, publicCardList) != null) {
                    cardToRemove = lowest;
                } else {
                    //try the other cards if we can't remove the lowest one
                    tempCards.remove(lowest);
                    Card secondLowest = getLowestCard(tempCards);

                    if (canRemoveCard(secondLowest, playerCardList, publicCardList) != null) {
                        cardToRemove = secondLowest;
                    } else {
                        tempCards.remove(secondLowest);
                        cardToRemove = tempCards.get(0);
                    }
                }

                break;
        }


        //remove from hand and return
        delay(timeToDelay);
        cardToRemove.removeFromHand(true);
        return cardToRemove;
    }

    @Override
    public void onCardDisposed(Card card) {
        int[] rankValues = ((Rank) card.getRank()).getPossibleSumValues();

        //update the hashmap for all the possible sum values after card has been removed
        for (int sumValue : rankValues) {
            int current = disposedValues.get(sumValue);
            disposedValues.put(sumValue, current + 1);
        }
    }

    public ArrayList<Card> unusableCards(ArrayList<Card> privateCardList) {
        ArrayList<Card> unusableCards = new ArrayList<>();

        //check all of our cards
        for (Card card : privateCardList) {
            boolean solutionPossible = false;
            int[] rankValues = ((Rank) card.getRank()).getPossibleSumValues();

            //check all possible sum values of this card
            for (int sumValue : rankValues) {
                int difference = TARGET_VALUE - sumValue;
                int withdrawnCardCount = disposedValues.get(difference);

                //if we possible have a card to combine with this card to make thirteen, set as a possible solution
                if (withdrawnCardCount < VIABILITY_THRESHOLD) {
                    solutionPossible = true;
                    break;
                }
            }

            //no solution possible, mark to be potentially removed
            if (!solutionPossible) {
                unusableCards.add(card);
            }
        }

        return unusableCards;
    }

    public Card canRemoveCard(Card candidate, ArrayList<Card> playerCardList, ArrayList<Card> publicCardList) {
        Card cardToRemove = null;
        ArrayList<Card> tempCards = (ArrayList<Card>) playerCardList.clone();
        tempCards.remove(candidate);

        //check if we can still achieve thirteen without this card
        if (sumChecker.verifyThirteen(tempCards, publicCardList)) {
            cardToRemove = candidate;
        }

        return cardToRemove;
    }

    private HashSet<Card> findThirteenSolutions(ArrayList<Card> playerCardList, ArrayList<Card> publicCardList) {
        HashSet<Card> cardsInThirteen = new HashSet<>();

        //check all possible 13 sum combinations to see which cards contribute to 13 sum

        for (Card privateCard : playerCardList) {
            //one private card and one player card
            for (Card publicCard : publicCardList) {
                if (sumChecker.isThirteenCards(privateCard, publicCard)) {
                    cardsInThirteen.add(privateCard);
                }
            }

            ArrayList<Card> tempCards = (ArrayList<Card>) playerCardList.clone();
            tempCards.remove(privateCard);

            //two private cards and two public cards
            if (sumChecker.isThirteenAllCards(tempCards, publicCardList)) {
                cardsInThirteen.addAll(tempCards);
            }

            //both private cards
            for (Card otherCard : tempCards) {
                if (sumChecker.isThirteenCards(privateCard, otherCard)) {
                    cardsInThirteen.add(privateCard);
                    cardsInThirteen.add(otherCard);
                }
            }
        }

        return cardsInThirteen;
    }
}
