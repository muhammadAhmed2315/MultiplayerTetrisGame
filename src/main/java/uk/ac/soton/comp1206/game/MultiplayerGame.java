package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class MultiplayerGame extends Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    private Random random = new Random();

    /**
     * Listener for handling when the next piece
     */
    private NextPieceListener nextPieceListener;

    /**
     * Listener for handling when a line needs to be cleared
     */
    private LineClearedListener lineClearedListener;

    /**
     * Listener for handling the game timer being reset
     */
    private GameLoopListener gameLoopListener;

    /**
     * User score
     */
    private final SimpleIntegerProperty userScore = new SimpleIntegerProperty(0);

    /**
     * Score multiplier
     */
    private final SimpleIntegerProperty scoreMultiplier = new SimpleIntegerProperty(1);

    /**
     * What level the user is on
     */
    private final SimpleIntegerProperty gameLevel = new SimpleIntegerProperty(0);

    /**
     * How many lives the user has left
     */
    private final SimpleIntegerProperty livesRemaining = new SimpleIntegerProperty(3);

    /**
     * Game timer which counts down how long the user has left to play a piece
     */
    private ScheduledExecutorService gameTimer;

    // TODO this comment
    private Queue<Integer> nextPiecesQueue = new LinkedList<>();

    /**
     * The grid model linked to the game
     */
    private GamePiece currentPiece;
    private GamePiece nextPiece;

    // TODO this comment
    private Communicator communicator;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows, Communicator communicator) {
        super(cols, rows);

        this.communicator = communicator;
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting multiplayer game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising multiplayer game");

        AtomicInteger piecesReceived = new AtomicInteger(0);

        communicator.addListener((message) -> {
            if (message.startsWith("PIECE ")) {
                Platform.runLater(() -> {
                    nextPiecesQueue.offer(Integer.valueOf(message.substring(6)));
                    if (piecesReceived.incrementAndGet() == 5) {
                        Platform.runLater(() -> {
                            nextPiece = spawnPiece();
                            nextPiece();
                        });
                    }
                });
            }
        });

        for (int i = 0; i < 5; i++) {
            communicator.send("PIECE");
        }

        gameTimer = Executors.newSingleThreadScheduledExecutor();
        gameTimer.scheduleAtFixedRate(this::gameLoop, getTimerDelay(), getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener.handle(getTimerDelay());
    }

    /**
     * Method that is repeatedly called by the game loop. Handles the logic for what happens when
     * the user doesn't play a piece within the given time.
     */
    private void gameLoop() {
        logger.info("Multiplayer game loop triggered");
        if (livesRemaining.get() > 0) {
            Platform.runLater(() -> {
                livesRemaining.set(livesRemaining.get() - 1);
                communicator.send("LIVES " + livesRemaining.get());
                Multimedia.switchAudioFile("lifelose.wav");
                //logger.info("Lives remaining: {}", livesRemaining.get());
                nextPiece();
                scoreMultiplier.set(1);
            });
        } else {
            Platform.runLater(() -> {
                livesRemaining.set(livesRemaining.get() - 1);
                communicator.send("DIE");
            });
            //logger.info("Game over");
            Multimedia.switchAudioFile("explode.wav");
            gameTimer.shutdown();
        }
    }

    /**
     * Shuts down the game timer
     */
    public void gameTimerShutdown() {
        gameTimer.shutdown();
    }

    /**
     * Calculates how long the user has to play a piece depending on the level
     * @return how long the user has to play a piece
     */
    private int getTimerDelay() {
        return 12000 - (500 * gameLevel.intValue()); // TODO 12000
    }

    /**
     * Handles line clearing logic
     */
    public void afterPiece() {
        // loop through rows
        HashSet<GameBlockCoordinate> blocksToBeCleared = new HashSet<>();
        int lineCounter = 0;

        // looking for horizontal lines
        for (int i = 0; i < rows; i++) {
            int rowSum = 0;
            for (int j = 0; j < cols; j++) {
                if (grid.get(i, j) > 0) {
                    rowSum++;
                }
            }
            // if a line is found
            if (rowSum >= rows) {
                for (int temp = 0; temp < rows; temp++) {
                    blocksToBeCleared.add(new GameBlockCoordinate(i, temp));
                }
                lineCounter++;
            }
        }

        // looking for vertical lines
        for (int i = 0; i < rows; i++) {
            int colSum = 0;
            for (int j = 0; j < cols; j++) {
                if (grid.get(j, i) > 0) {
                    colSum++;
                }
            }
            // if a line is found
            if (colSum >= cols) {
                for (int temp = 0; temp < cols; temp++) {
                    blocksToBeCleared.add(new GameBlockCoordinate(temp, i));
                }
                lineCounter++;
            }
        }
        //logger.info("Line clearing function: found {} lines containing {} blocks", lineCounter, blocksToBeCleared.size());
        // Clear the lines
        if (!blocksToBeCleared.isEmpty()) {
            lineCleared(blocksToBeCleared);
            for (GameBlockCoordinate x : blocksToBeCleared) {
                grid.set(x.getX(), x.getY(), 0);
            }
            // Find value to update the score by and increase the score
            int oldGameLevel = gameLevel.intValue();
            int incScoreBy = calculateScore(lineCounter, blocksToBeCleared.size());
            userScore.set(userScore.get() + incScoreBy);
            //logger.info("Increasing score by {}, new score = {}", incScoreBy, userScore);
            scoreMultiplier.set(scoreMultiplier.get() + 1);
            gameLevel.set(userScore.get() / 1000);

            // If levelled up, play the level up sound
            if (gameLevel.intValue() != oldGameLevel) {
                Multimedia.switchAudioFile("level.wav");
            }

            // Resets the timer to 0 and starts it again if the user correctly places a piece
            // Timer is set to a new timer delay (in case the timer delay has changed)
            gameTimer.shutdownNow();
            gameTimer = Executors.newSingleThreadScheduledExecutor();
            gameTimer.scheduleAtFixedRate(this::gameLoop, getTimerDelay(), getTimerDelay(), TimeUnit.MILLISECONDS);
            if (gameLoopListener != null) {
                gameLoopListener.handle(getTimerDelay());
            }
        } else {
            scoreMultiplier.set(1);
        }
    }

    /**
     * Rotates the current piece 90 degrees clockwise
     */
    public void rotateCurrentPiece() {
        Multimedia.switchAudioFile("rotate.wav");
        currentPiece.rotate();
        nextPieceListener.nextPiece(currentPiece, nextPiece);
    }

    /**
     * Calculates how much the score should increase by given the number of blocks and lines cleared
     * @param linesCleared number of lines cleared
     * @param blocksCleared number of blocks cleared
     * @return value to increment current score by (or points gained by last play)
     */
    public int calculateScore(int linesCleared, int blocksCleared) {
        return linesCleared * blocksCleared * 10 * scoreMultiplier.getValue();
    }

    /**
     * Updates currentPiece to a new randomly generated piece
     * @return updated currentPiece
     */
    public void nextPiece() {
        currentPiece = nextPiece;
        //logger.info("The next piece is: {}", currentPiece);

        nextPiece = spawnPiece();
        //logger.info("The following piece is: {}", nextPiece);

        if (nextPieceListener != null) {
            //logger.info("Passing new piece to the nextPieceListener");
            nextPieceListener.nextPiece(currentPiece, nextPiece);
        }
    }

    /**
     * Requests a randomly-generated piece from the server and returns the piece at the front of nextPiecesQueue
     * @return randomly-generated piece
     */
    public GamePiece spawnPiece() {
        communicator.send("PIECE");
        GamePiece temp = GamePiece.createPiece(nextPiecesQueue.poll());
        return temp;
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        // Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        if (grid.canPlayPiece(currentPiece, x, y)) {
            // Can play the piece
            Multimedia.switchAudioFile("place.wav");

            // Resets the timer to 0 and starts it again if the user correctly places a piece
            // Timer is set to a new timer delay (in case the timer delay has changed)
            gameTimer.shutdownNow();
            gameTimer = Executors.newSingleThreadScheduledExecutor();
            gameTimer.scheduleAtFixedRate(this::gameLoop, getTimerDelay(), getTimerDelay(), TimeUnit.MILLISECONDS);
            if (gameLoopListener != null) {
                gameLoopListener.handle(getTimerDelay());
            }

            grid.playPiece(currentPiece, x, y);
            nextPiece();
            afterPiece();

            communicator.send("BOARD " + grid.getFlattenedGrid());
            communicator.send("SCORE " + userScore.intValue());
        } else {
            // Can't play the piece
            Multimedia.switchAudioFile("fail.wav");
        }
    }

    /**
     * Swaps the current piece with the next piece
     */
    public void swapCurrentPiece() {
        //logger.info("Swapping current piece with the upcoming piece");
        var temp = nextPiece;
        nextPiece = currentPiece;
        currentPiece = temp;
        Multimedia.switchAudioFile("rotate.wav");
        nextPieceListener.nextPiece(currentPiece, nextPiece);
    }

    // Sets nextPieceListener
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    // Sets lineClearedListener
    public void setOnLineClear(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }

    // Calls event handling code for when a line needs to be cleared
    private void lineCleared(HashSet<GameBlockCoordinate> blocksToBeCleared) {
        if(lineClearedListener != null) {
            lineClearedListener.handle(blocksToBeCleared);
        }
    }

    public void setGameLoopListener(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get the user's current score
     * @return user's current score
     */
    public SimpleIntegerProperty getUserScore() {
        return userScore;
    }

    /**
     * Get the current game level
     * @return current game level
     */
    public SimpleIntegerProperty getGameLevel() {
        return gameLevel;
    }

    /**
     * Get the current score multiplier
     * @return current score multiplier
     */
    public SimpleIntegerProperty getScoreMultiplier() {
        return scoreMultiplier;
    }

    /**
     * Get number of lives user has remaining
     * @return number of lives user has remaining
     */
    public SimpleIntegerProperty getLivesRemaining() {
        return livesRemaining;
    }

}