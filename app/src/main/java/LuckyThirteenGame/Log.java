package LuckyThirteenGame;

import LuckyThirteenGame.CardHolderEntities.Players.Player;
import LuckyThirteenGame.Cards.*;
import ch.aplu.jcardgame.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Log {
    private static Log instance = null;
    private StringBuilder logResult = new StringBuilder();

    private Log() {}

    //set log as a singleton class
    public static Log getInstance() {
        if (instance == null) {
            instance = new Log();
        }
        return instance;
    }

    public void addCardPlayedToLog(int playerNumber, Player player) {
        //add information about a card being played to the log
        ArrayList<Card> cards = player.getPlayerHand().getCardList();
        int listSize = cards.size();

        if (listSize < 2) {
            return;
        }

        logResult.append("P" + playerNumber + "-");

        for (int i = 0; i < listSize; i++) {
            Rank cardRank = (Rank) cards.get(i).getRank();
            Suit cardSuit = (Suit) cards.get(i).getSuit();
            logResult.append(cardRank.getRankCardLog() + cardSuit.getSuitShortHand());
            if (i < cards.size() - 1) {
                logResult.append("-");
            }
        }
        logResult.append(",");
    }

    public void addRoundInfoToLog(int roundNumber) {
        //add the round info to the log when we start a new round
        logResult.append("Round" + roundNumber + ":");
    }

    public void addEndOfRoundToLog(List<Player> players) {
        //add the end of the round to the log with the current player scores
        logResult.append("Score:");
        for (Player player : players) {
            logResult.append(player.getPlayerScore() + ",");
        }
        logResult.append("\n");
    }

    public void addEndOfGameToLog(List<Integer> winners, List<Player> players) {
        //add the end of the game to the log string
        logResult.append("EndGame:");
        for (Player player : players) {
            logResult.append(player.getPlayerScore() + ",");
        }
        logResult.append("\n");
        logResult.append("Winners:" + winners.stream().map(String::valueOf).collect(Collectors.joining(", ")));
    }

    public String getLogResult() {
        //return the log result as a string
        //remove the old string to prevent issues with singleton pattern during testing
        String stringToReturn = logResult.toString();
        logResult = new StringBuilder();
        return stringToReturn;
    }
}
