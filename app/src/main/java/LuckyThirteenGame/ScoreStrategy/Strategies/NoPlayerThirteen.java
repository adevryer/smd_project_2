package LuckyThirteenGame.ScoreStrategy.Strategies;

import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.Cards.Rank;
import LuckyThirteenGame.Cards.Suit;
import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.ScoreStrategy.ScoringStrategy;
import ch.aplu.jcardgame.Card;

import java.util.List;

public class NoPlayerThirteen implements ScoringStrategy {
    @Override
    public void getScoreForPlayer(Player player, PublicCards publics) {
        //get the score for the player's private cards
        List<Card> privateCards = player.getPlayerHand().getCardList();
        int score = getScorePrivateCard(privateCards.get(0)) + getScorePrivateCard(privateCards.get(1));
        player.setPlayerScore(score);
    }

    private int getScorePrivateCard(Card card) {
        Rank rank = (Rank) card.getRank();
        Suit suit = (Suit) card.getSuit();

        return rank.getScoreCardValue() * suit.getMultiplicationFactor();
    }
}
