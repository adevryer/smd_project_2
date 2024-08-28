package LuckyThirteenGame.ScoreStrategy.Strategies;

import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.ScoreStrategy.ScoringStrategy;

public class OnePlayerThirteen implements ScoringStrategy {
    public static final int WINNER_SCORE = 100, LOSER_SCORE = 0;

    @Override
    public void getScoreForPlayer(Player player, PublicCards publics) {
        //assign the winner 100 and losers 0
        if (player.isThirteenPlayer()) {
            player.setPlayerScore(WINNER_SCORE);
        } else {
            player.setPlayerScore(LOSER_SCORE);
        }
    }
}
