package LuckyThirteenGame.ScoreStrategy;
import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.CardHolderEntities.Players.Player;

public interface ScoringStrategy {
    int INIT_PLAYER_SCORE = 0;
    void getScoreForPlayer(Player player, PublicCards publics);
}
