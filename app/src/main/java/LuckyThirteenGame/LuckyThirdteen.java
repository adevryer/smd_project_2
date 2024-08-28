package LuckyThirteenGame;// LuckyThirteen.java

import LuckyThirteenGame.CardHolderEntities.Players.*;
import LuckyThirteenGame.Cards.*;
import LuckyThirteenGame.CardHolderEntities.*;
import LuckyThirteenGame.ScoreStrategy.*;
import ch.aplu.jcardgame.*;
import ch.aplu.jgamegrid.*;

import java.awt.*;
import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("serial")
public class LuckyThirdteen extends CardGame {
    //define all the constants for the class
    private static final String VERSION = "1.0";
    public static final int NBPLAYERS = 4;
    public static final int NBROUNDS = 4;
    private static final int HANDWIDTH = 400;
    private static final int TRICKWIDTH = 40;
    public static final Font BIG_FONT = new Font("Arial", Font.BOLD, 36);
    private static final Location TRICK_LOCATION = new Location(350, 350);
    private static final Location TEXT_LOCATION = new Location(350, 450);

    //define the hand and score locations for each of the players
    private static final Location[] HAND_LOCATIONS = {
            new Location(350, 625),
            new Location(75, 350),
            new Location(350, 75),
            new Location(625, 350)
    };
    private static final Location[] SCORE_LOCATIONS = {
            new Location(575, 675),
            new Location(25, 575),
            new Location(575, 25),
            // new Location(650, 575)
            new Location(575, 575)
    };

    private Properties properties;
    private final Log gameLog = Log.getInstance();
    private ArrayList<Player> players = new ArrayList<>();
    private PublicCards publicCards;
    private CardDeck cardDeck = new CardDeck();
    private ArrayList<DiscardedCardListener> discardedListeners = new ArrayList<>();

    private Actor[] scoreActors = {null, null, null, null};
    private int thinkingTime = 2000;
    private int delayTime = 600;

    private void playGame() {
        //loop through all four rounds
        for (int roundNumber = 1; roundNumber <= NBROUNDS; roundNumber++) {
            gameLog.addRoundInfoToLog(roundNumber);

            for (int playerNumber = 0; playerNumber < NBPLAYERS; playerNumber++) {
                //set the status text for each of the players
                if (players.get(playerNumber) instanceof HumanPlayer) {
                    setStatusText("Players. Player " + playerNumber + " is playing. Please double click on a card to discard.");
                } else {
                    setStatusText("Players. Player " + playerNumber + " is thinking...");
                }

                //add a card to the player's hand
                players.get(playerNumber).addCard(roundNumber - 1);

                Card removed;

                //remove a card from the player's hand
                if (players.get(playerNumber) instanceof HumanPlayer) {
                    removed = players.get(playerNumber).removeCard(roundNumber - 1, publicCards, delayTime);
                } else {
                    removed = players.get(playerNumber).removeCard(roundNumber - 1, publicCards, thinkingTime);
                }

                //let the discarded card listeners know that a card was discarded
                if (removed != null) {
                    publishDiscardedEvent(removed);
                }

                gameLog.addCardPlayedToLog(playerNumber, players.get(playerNumber));
                delay(delayTime);
            }

            gameLog.addEndOfRoundToLog(players);
        }
    }

    private void initiatePublicDeck() {
        //deal the public cards
        String publicCardsToDeal = properties.getProperty("shared.initialcards");
        publicCards = new PublicCards(publicCardsToDeal, cardDeck);
    }

    private void initiatePlayers() {
        //create all four players
        for (int i = 0; i < NBPLAYERS; i++) {
            //create the player of the right type with the specified starting cards and playing sequence
            String playerToCreate = properties.getProperty("players." + i);
            String playerInitialCards = properties.getProperty("players." + i + ".initialcards");
            String playerCardsPlayed = properties.getProperty("players." + i + ".cardsPlayed");

            switch (playerToCreate) {
                case "human":
                    players.add(new HumanPlayer(playerCardsPlayed, playerInitialCards, cardDeck));
                    break;
                case "random":
                    players.add(new RandomPlayer(playerCardsPlayed, playerInitialCards, cardDeck));
                    break;
                case "basic":
                    players.add(new BasicPlayer(playerCardsPlayed, playerInitialCards, cardDeck));
                    break;
                case "clever":
                    //create clever player and register as discarded card listener
                    Player playerToAdd = new CleverPlayer(playerCardsPlayed, playerInitialCards, cardDeck);
                    players.add(playerToAdd);
                    addDiscardedListener((DiscardedCardListener) playerToAdd);
                    break;
            }
        }

        //if the player or public deck had no cards pre-assigned in the properties, assign some random cards now
        publicCards.assignRandomInitialCards();

        for (Player player : players) {
            player.assignRandomInitialCards();
        }
    }

    private void addDiscardedListener(DiscardedCardListener listener) {
        discardedListeners.add(listener);
    }

    private void publishDiscardedEvent(Card disposedCard) {
        for (DiscardedCardListener listener : discardedListeners) {
            listener.onCardDisposed(disposedCard);
        }
    }

    private void drawHandsOnScreen() {
        //draw the public card hand on the screen
        publicCards.getPlayerHand().setView(this, new RowLayout(TRICK_LOCATION,
                (publicCards.getPlayerHand().getNumberOfCards() + 2) * TRICKWIDTH));
        publicCards.getPlayerHand().draw();

        //draw all the player hands on the screen
        for (int i = 0; i < NBPLAYERS; i++) {
            Player currentPlayer = players.get(i);
            RowLayout[] layouts = new RowLayout[NBPLAYERS];

            layouts[i] = new RowLayout(HAND_LOCATIONS[i], HANDWIDTH);
            layouts[i].setRotationAngle(90 * i);

            currentPlayer.getPlayerHand().setView(this, layouts[i]);
            currentPlayer.getPlayerHand().setTargetArea(new TargetArea(TRICK_LOCATION));
            currentPlayer.getPlayerHand().draw();
        }
    }

    private void drawScoresOnScreen() {
        //draw each of the player's scores on the screen
        for (int playerNum = 0; playerNum < NBPLAYERS; playerNum++) {
            //if there is already a score on the screen, remove it and update it with a new one
            if (scoreActors[playerNum] != null) {
                removeActor(scoreActors[playerNum]);
            }

            String text = "P" + playerNum + "[" + players.get(playerNum).getPlayerScore() + "]";
            scoreActors[playerNum] = new TextActor(text, Color.WHITE, bgColor, BIG_FONT);
            addActor(scoreActors[playerNum], SCORE_LOCATIONS[playerNum]);
        }
    }

    public void findWinners() {
        int maxScore = 0;
        ArrayList<Integer> winners = new ArrayList<>();
        String winText;

        //loop through all of the players and find those with the highest score
        for (int i = 0; i < NBPLAYERS; i++) {
            if (players.get(i).getPlayerScore() > maxScore) {
                maxScore = players.get(i).getPlayerScore();
                winners.clear();
                winners.add(i);
            } else if (players.get(i).getPlayerScore() == maxScore) {
                winners.add(i);
            }
        }

        //print the winning message to the screen
        if (winners.size() == 1) {
            winText = "Game over. Winner is player: " + winners.get(0);
        } else {
            winText = "Game Over. Drawn winners are players: " +
                    winners.stream().map(String::valueOf).collect(Collectors.joining(", "));
        }

        //draw the game end animation image and update log and screen
        addActor(new Actor("sprites/gameover.gif"), TEXT_LOCATION);
        setStatusText(winText);
        refresh();
        gameLog.addEndOfGameToLog(winners, players);
    }

    public String runApp() {
        setTitle("LuckyThirteen (V" + VERSION + ") Constructed for UofM SWEN30006 with JGameGrid (www.aplu.ch)");
        setStatusText("Initializing...");

        //BEFORE GAME: initiate players and public deck and draw these on screen
        initiatePublicDeck();
        initiatePlayers();
        drawHandsOnScreen();
        drawScoresOnScreen();

        playGame();

        //AFTER GAME: find the correct scoring strategy to use
        ScoreStrategyFactory strategyFactory = ScoreStrategyFactory.getInstance();
        ScoringStrategy scoreStrategy = strategyFactory.returnStrategy(players, publicCards);

        //score each player using this scoring strategy
        for (int i = 0; i < NBPLAYERS; i++) {
            scoreStrategy.getScoreForPlayer(players.get(i), publicCards);
        }

        //show the scores and declare the winners of the game
        drawScoresOnScreen();
        findWinners();
        return gameLog.getLogResult();
    }

    public LuckyThirdteen(Properties properties) {
        super(700, 700, 30);
        this.properties = properties;
        thinkingTime = Integer.parseInt(properties.getProperty("thinkingTime", "200"));
        delayTime = Integer.parseInt(properties.getProperty("delayTime", "50"));
    }
}
