package LuckyThirteenGame.ScoreStrategy.Strategies;

import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.Cards.*;
import LuckyThirteenGame.ScoreStrategy.ScoringStrategy;
import LuckyThirteenGame.ScoreStrategy.ThirteenSumChecker;
import ch.aplu.jcardgame.Card;

import java.util.List;

public class SeveralPlayerThirteen implements ScoringStrategy {
    private final ThirteenSumChecker sumChecker = ThirteenSumChecker.getInstance();

    @Override
    public void getScoreForPlayer(Player player, PublicCards publics) {
        int currentHighestScore = INIT_PLAYER_SCORE;
        List<Card> privateCards = player.getPlayerHand().getCardList();
        List<Card> publicCards = publics.getPlayerHand().getCardList();

        //find all possible scores the players can receive for card combinations and assign the highest one
        //check both private cards
        if (sumChecker.isThirteenCards(privateCards.get(0), privateCards.get(1))) {
            int score = getScorePrivateCard(privateCards.get(0)) + getScorePrivateCard(privateCards.get(1));
            if (score > currentHighestScore) {
                currentHighestScore = score;
            }
        }

        //check one private and one public card
        for (Card publicCard : publicCards) {
            for (Card privateCard : privateCards) {
                if (sumChecker.isThirteenCards(publicCard, privateCard)) {
                    int score = getScorePublicCard(publicCard) + getScorePrivateCard(privateCard);

                    if (score > currentHighestScore) {
                        currentHighestScore = score;
                    }
                }
            }
        }

        //check all four cards
        if (sumChecker.isThirteenAllCards(privateCards, publicCards)) {
            int score = getScorePrivateCard(privateCards.get(0)) + getScorePrivateCard(privateCards.get(1)) +
                    getScorePublicCard(publicCards.get(0)) + getScorePublicCard(publicCards.get(1));

            if (score > currentHighestScore) {
                currentHighestScore = score;
            }
        }

        player.setPlayerScore(currentHighestScore);
    }

    private int getScorePrivateCard(Card card) {
        //get the score for the specified private card
        Rank rank = (Rank) card.getRank();
        Suit suit = (Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }

    private int getScorePublicCard(Card card) {
        //get the score for the specified public card
        Rank rank = (Rank) card.getRank();
        return rank.getScoreCardValue() * Suit.PUBLIC_CARD_MULTIPLICATION_FACTOR;
    }
}
