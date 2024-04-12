package uk.ac.soton.comp1206.scene;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.converter.NumberStringConverter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;
    protected GameBoard board;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        // Bar at the top of the screen showing the score on the left and the lives remaining
        // on the right
        // HBox containing score and lives
        Label scoreHeading = new Label("Score");
        Label actualScore = new Label("0");
        scoreHeading.getStyleClass().add("heading");
        actualScore.getStyleClass().add("score");
        VBox scoreVBox = new VBox(scoreHeading, actualScore);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label livesHeading = new Label("Lives");
        Label actualLives = new Label("3");
        livesHeading.getStyleClass().add("heading");
        actualLives.getStyleClass().add("lives");
        VBox livesVBox = new VBox(livesHeading, actualLives);

        HBox topBar = new HBox(scoreVBox, spacer, livesVBox);
        topBar.setAlignment(Pos.CENTER);

        // Bar on the right hand side, showing the high score, next piece, and the level
        Label multiplierHeading = new Label("Multiplier");
        Label actualMultiplier = new Label("1");
        multiplierHeading.getStyleClass().add("heading");
        actualMultiplier.getStyleClass().add("level");
        VBox multiplierVBox = new VBox(multiplierHeading, actualMultiplier);

        Label highScoreHeading = new Label("High Score");
        Label actualHighScore = new Label(Integer.toString(getHighScore()));
        highScoreHeading.getStyleClass().add("heading");
        actualHighScore.getStyleClass().add("level");
        VBox highScoreVBox = new VBox(highScoreHeading, actualHighScore);

        game.getUserScore().addListener((ObservableValue, oldValue, newValue) -> {
            if (game.getUserScore().intValue() > Integer.valueOf(actualHighScore.getText())) {
                actualHighScore.setText(Integer.toString(game.getUserScore().intValue()));
            }
        });

        PieceBoard currentPieceBoard = new PieceBoard(3, 3, 132, 132, true);
        PieceBoard nextPieceBoard = new PieceBoard(3, 3, 80, 80, false);

        game.setNextPieceListener(((currentGamePiece, nextGamePiece) -> {
            currentPieceBoard.displayPiece(currentGamePiece);
            nextPieceBoard.displayPiece(nextGamePiece);
        }));

        game.setOnLineClear(board::fadeOut);

        Label levelHeading = new Label("Level");
        Label actualLevel = new Label("0");
        levelHeading.getStyleClass().add("heading");
        actualLevel.getStyleClass().add("level");
        VBox levelVBox = new VBox(levelHeading, actualLevel);

        VBox rightBar = new VBox(multiplierVBox, highScoreVBox, currentPieceBoard, nextPieceBoard, levelVBox);
        rightBar.setAlignment(Pos.CENTER);

        Bindings.bindBidirectional(actualScore.textProperty(), game.getUserScore(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLevel.textProperty(), game.getGameLevel(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLives.textProperty(), game.getLivesRemaining(), new NumberStringConverter());
        Bindings.bindBidirectional(actualMultiplier.textProperty(), game.getScoreMultiplier(), new NumberStringConverter());

        actualLives.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("-1")) {
                gameWindow.startScores(game);
                endGame();
            }
        });

        // Timer bar at the bottom of the screen
        Rectangle rectangle = new Rectangle(gameWindow.getWidth(), (double) gameWindow.getHeight() / 30);
        rectangle.setFill(Color.GREEN);
        game.setGameLoopListener((event) -> {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                new KeyFrame(Duration.ZERO,
                    new KeyValue(rectangle.widthProperty(), gameWindow.getWidth()),
                    new KeyValue(rectangle.fillProperty(), Color.GREEN)
                ),
                new KeyFrame(
                    Duration.millis(event / 2), new KeyValue(rectangle.widthProperty(), gameWindow.getWidth() / 2),
                    new KeyValue(rectangle.fillProperty(), Color.YELLOW)
                ),
                new KeyFrame(
                    Duration.millis(event), new KeyValue(rectangle.widthProperty(), 0),
                    new KeyValue(rectangle.fillProperty(), Color.RED)
                )
            );
            timeline.setCycleCount(Integer.MAX_VALUE);
            timeline.play();
        });
        Region bottomSpacer = new Region();
        bottomSpacer.setPrefHeight(5); // Set the desired padding height
        VBox timerVBox = new VBox(rectangle, bottomSpacer);

        topBar.setSpacing(10); // Set spacing between the subcomponents
        mainPane.setTop(topBar);
        mainPane.setRight(rightBar);
        mainPane.setBottom(timerVBox);

        // Set what function is executed when a block, the main GameBoard, or the two PieceBoards
        // are clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClick(this::GameBoardClicked);
        currentPieceBoard.setOnRightClick(this::GameBoardClicked);
        nextPieceBoard.setOnRightClick(this::nextPieceGameBoardClicked);

        // If other background music is playing (e.g., menu background music)
        if (Multimedia.getMusicPlayer() != null) {
            Multimedia.getMusicPlayer().stop();
            // Play intro game music, and then the actual game music
            Multimedia.playBackgroundMusic("game_start.wav");
            Multimedia.getMusicPlayer().setOnEndOfMedia(() -> {
                Multimedia.getMusicPlayer().stop();
                Multimedia.playBackgroundMusic("game.wav");
            });
        }

    }

    private int getHighScore() {
        // Get top high score
        var inputStream = ScoresScene.class.getResourceAsStream("/scores.txt");

        if (inputStream == null) {
            throw new RuntimeException("File scores.txt not found in the resources directory");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = br.readLine();
            if (line != null) {
                return Integer.valueOf(line.split(":")[1]);
            } else {
                return 0;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Handles what happens if the next piece GameBoard is left-clicked
     */
    private void nextPieceGameBoardClicked() {
        game.swapCurrentPiece();
    }

    /**
     * Handles what happens if the main GameBoard is right-clicked
     */
    private void GameBoardClicked() {
        game.rotateCurrentPiece();
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Set up the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Cleanup code before the game is ended
     */
    public void endGame() {
        logger.info("Ending the game");
        game.gameTimerShutdown();
        game = null;
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        // Add keyboard listener to the scene
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    endGame();
                    gameWindow.startMenu();
                    break;
                case UP:
                case W:
                    board.moveKeyboardAimUp();
                    break;
                case DOWN:
                case S:
                    board.moveKeyboardAimDown();
                    break;
                case RIGHT:
                case D:
                    board.moveKeyboardAimRight();
                    break;
                case LEFT:
                case A:
                    board.moveKeyboardAimLeft();
                    break;
                case E:
                case C:
                case CLOSE_BRACKET:
                    game.rotateCurrentPiece();
                    break;
                case Q:
                case Z:
                case OPEN_BRACKET:
                    game.rotateCurrentPiece();
                    game.rotateCurrentPiece();
                    game.rotateCurrentPiece();
                    break;
                case ENTER:
                case X:
                    board.placeKeyboardAim();
                    break;
                case SPACE:
                case R:
                    game.swapCurrentPiece();
                    break;
            }
        });
    }

}
