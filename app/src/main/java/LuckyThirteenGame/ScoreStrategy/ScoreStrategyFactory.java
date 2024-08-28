package LuckyThirteenGame.ScoreStrategy;

import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.ScoreStrategy.Strategies.NoPlayerThirteen;
import LuckyThirteenGame.ScoreStrategy.Strategies.OnePlayerThirteen;
import LuckyThirteenGame.ScoreStrategy.Strategies.SeveralPlayerThirteen;

import java.util.List;

public class ScoreStrategyFactory {
    private static ScoreStrategyFactory instance = null;
    private ThirteenSumChecker thirteenChecker = ThirteenSumChecker.getInstance();

    private ScoreStrategyFactory() {}

    //make as a singleton class
    public static ScoreStrategyFactory getInstance(){
        if (instance == null){
            instance = new ScoreStrategyFactory();
        }

        return instance;
    }

    public ScoringStrategy returnStrategy(List<Player> playerList, PublicCards publicCards) {
        int numThirteen = 0;

        //check all players to see if they are at 13
        for (Player player : playerList) {
            thirteenChecker.isThirteen(player, publicCards);

            if (player.isThirteenPlayer()) {
                numThirteen += 1;
            }
        }

        //based on number of players at 13, return the appropriate scoring strategy
        return switch (numThirteen) {
            case 0 -> new NoPlayerThirteen();
            case 1 -> new OnePlayerThirteen();
            default -> new SeveralPlayerThirteen();
        };
    }
}
