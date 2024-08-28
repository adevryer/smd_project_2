package LuckyThirteenGame.ScoreStrategy;

import LuckyThirteenGame.Cards.Rank;
import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.CardHolderEntities.PublicCards;
import ch.aplu.jcardgame.Card;

import java.util.List;

public class ThirteenSumChecker {
    public static final int THIRTEEN_GOAL = 13;
    private static ThirteenSumChecker sumChecker = null;

    private ThirteenSumChecker() {}

    //make as a singleton class
    public static ThirteenSumChecker getInstance() {
        if (sumChecker == null) {
            sumChecker = new ThirteenSumChecker();
        }

        return sumChecker;
    }

    private boolean isThirteenFromPossibleValues(int[] possibleValues1, int[] possibleValues2) {
        //check if we can reach 13 from all sum values
        for (int value1 : possibleValues1) {
            for (int value2 : possibleValues2) {
                if (value1 + value2 == THIRTEEN_GOAL) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isThirteenCards(Card card1, Card card2) {
        //check if two specified cards are thirteen
        Rank rank1 = (Rank) card1.getRank();
        Rank rank2 = (Rank) card2.getRank();
        return isThirteenFromPossibleValues(rank1.getPossibleSumValues(), rank2.getPossibleSumValues());
    }

    private boolean isThirteenMixedCards(List<Card> privateCards, List<Card> publicCards) {
        //check a mix of public and private cards to see if they get 13
        for (Card privateCard : privateCards) {
            for (Card publicCard : publicCards) {
                if (isThirteenCards(privateCard, publicCard)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isThirteenAllCards(List<Card> privateCards, List<Card> publicCards) {
        //check if all two public and two private cards reach 13
        int[] sumPossiblePrivateValue1 = ((Rank) privateCards.get(0).getRank()).getPossibleSumValues();
        int[] sumPossiblePrivateValue2 = ((Rank) privateCards.get(1).getRank()).getPossibleSumValues();
        int[] sumPossiblePublicValue1 = ((Rank) publicCards.get(0).getRank()).getPossibleSumValues();
        int[] sumPossiblePublicValue2 = ((Rank) publicCards.get(1).getRank()).getPossibleSumValues();

        for (int possiblePrivateVal1: sumPossiblePrivateValue1) {
            for (int possiblePrivateVal2: sumPossiblePrivateValue2) {
                for (int possiblePublicVal1: sumPossiblePublicValue1) {
                    for (int possiblePublicVal2: sumPossiblePublicValue2) {
                        int sum = possiblePrivateVal1+possiblePrivateVal2+possiblePublicVal1+possiblePublicVal2;
                        if (sum == THIRTEEN_GOAL) {
                            return true;
                        }

                    }
                }
            }
        }
        return false;
    }

    public void isThirteen(Player player, PublicCards publics) {
        //set the boolean value isThirteen in player to be true if the player is thirteen
        List<Card> privateCards = player.getPlayerHand().getCardList();
        List<Card> publicCards = publics.getPlayerHand().getCardList();

        boolean playerResult = verifyThirteen(privateCards, publicCards);
        player.setThirteenPlayer(playerResult);
    }

    public boolean verifyThirteen(List<Card> privateCards, List<Card> publicCards) {
        //check if 13 can be reached using any possible combination
        boolean isThirteenPrivate = isThirteenCards(privateCards.get(0), privateCards.get(1));
        boolean isThirteenMixed = isThirteenMixedCards(privateCards, publicCards);
        boolean isThirteenAll= isThirteenAllCards(privateCards, publicCards);

        return isThirteenMixed || isThirteenPrivate || isThirteenAll;
    }

}
