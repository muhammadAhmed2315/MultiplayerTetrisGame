package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
        logger.info("Creating ChallengeScene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        // Basic UI setup
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();

        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        // Bar the top of the screen showing a HBox containing the score, title, and lives remaining
        Label scoreHeading = new Label("Score");
        Label actualScore = new Label("0");
        scoreHeading.getStyleClass().add("heading");
        actualScore.getStyleClass().add("score");
        VBox scoreVBox = new VBox(scoreHeading, actualScore);
        scoreVBox.setAlignment(Pos.CENTER);

        Pane spacerOne = new Pane();
        HBox.setHgrow(spacerOne, Priority.ALWAYS);

        Label sceneTitleLabel = new Label("Challenge Mode");
        sceneTitleLabel.getStyleClass().add("title");

        Pane spacerTwo = new Pane();
        HBox.setHgrow(spacerTwo, Priority.ALWAYS);

        Label livesHeading = new Label("Lives");
        Label actualLives = new Label("3");
        livesHeading.getStyleClass().add("heading");
        actualLives.getStyleClass().add("lives");
        VBox livesVBox = new VBox(livesHeading, actualLives);
        livesVBox.setAlignment(Pos.CENTER);

        HBox topBar = new HBox(scoreVBox, spacerOne, sceneTitleLabel, spacerTwo, livesVBox);
        topBar.setAlignment(Pos.CENTER);
        mainPane.setTop(topBar);

        /*
          Bar on the right hand side, containing a VBox showing the local high score, level, current
          piece, and next piece
         */
        Label highScoreHeading = new Label("High Score");
        Label actualHighScore = new Label(Integer.toString(getHighScore()));
        highScoreHeading.getStyleClass().add("heading");
        actualHighScore.getStyleClass().add("hiscore");
        VBox highScoreVBox = new VBox(highScoreHeading, actualHighScore);
        highScoreVBox.setAlignment(Pos.CENTER);

        Label levelHeading = new Label("Level");
        Label actualLevel = new Label("0");
        levelHeading.getStyleClass().add("heading");
        actualLevel.getStyleClass().add("level");
        VBox levelVBox = new VBox(levelHeading, actualLevel);
        levelVBox.setAlignment(Pos.CENTER);

        Label incomingLabel = new Label("Incoming");
        incomingLabel.getStyleClass().add("heading");

        /*
          Update the local high score text to the user's current score if the user's current
          score is higher
         */
        game.getUserScore().addListener((ObservableValue, oldValue, newValue) -> {
            if (game.getUserScore().intValue() > Integer.valueOf(actualHighScore.getText())) {
                actualHighScore.setText(Integer.toString(game.getUserScore().intValue()));
            }
        });

        PieceBoard currentPieceBoard = new PieceBoard(3, 3, 132, 132, true);
        PieceBoard nextPieceBoard = new PieceBoard(3, 3, 80, 80, false);

        /*
          Update the currentPieceBoard and nextPieceBoard displays when necessary
         */
        game.setNextPieceListener(((currentGamePiece, nextGamePiece) -> {
            currentPieceBoard.displayPiece(currentGamePiece);
            nextPieceBoard.displayPiece(nextGamePiece);
        }));

        game.setOnLineClear(board::fadeOut);

        VBox rightBar = new VBox(highScoreVBox, levelVBox, incomingLabel, currentPieceBoard, nextPieceBoard);
        rightBar.setSpacing(10);
        rightBar.setAlignment(Pos.CENTER);

        Bindings.bindBidirectional(actualScore.textProperty(), game.getUserScore(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLevel.textProperty(), game.getGameLevel(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLives.textProperty(), game.getLivesRemaining(), new NumberStringConverter());

        /*
          Switch to local high scores scene if the game ends
         */
        actualLives.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("-1")) {
                gameWindow.startLocalScores(game);
                endGame();
            }
        });

        mainPane.setRight(rightBar);

        // Timer bar at the bottom of the screen
        Rectangle rectangle = new Rectangle(gameWindow.getWidth() - 20, (double) gameWindow.getHeight() / 30);
        rectangle.setFill(Color.GREEN);
        game.setGameLoopListener((event) -> {
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(rectangle.widthProperty(), gameWindow.getWidth() - 20),
                            new KeyValue(rectangle.fillProperty(), Color.GREEN)
                    ),
                    new KeyFrame(
                            Duration.millis(event / 2),
                            new KeyValue(rectangle.widthProperty(), (gameWindow.getWidth() - 20) / 2),
                            new KeyValue(rectangle.fillProperty(), Color.YELLOW)
                    ),
                    new KeyFrame(
                            Duration.millis(event),
                            new KeyValue(rectangle.widthProperty(), 0),
                            new KeyValue(rectangle.fillProperty(), Color.RED)
                    )
            );
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        });

        VBox timerVBox = new VBox(rectangle);

        mainPane.setBottom(timerVBox);

        /*
          Set what functions are executed when a block, the main GameBoard, or the two PieceBoards
          are clicked
         */
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


    /**
     * Returns the path of the directory containing the jar file
     * @return Path of the directory containing the jar file
     */
    public static String getJarDirectory() {
        try {
            // Get the path of the directory containing the jar file
            return new File(ChallengeScene.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        } catch (Exception e) {
            // Fallback to current working directory if the above fails
            return ".";
        }
    }

    /**
     * Gets the local highest score from the scores.txt file. If the file's contents don't match
     * the required format, then it wipes the file and fills it with default scores. If the file
     * doesn't exist, it creates a scores.txt file and fills it with default scores.
     * a scores.txt file and fills it with default scores.
     * @return Highest score from scores.txt file
     */
    private int getHighScore() {
        int highScoreToReturn = Integer.valueOf(getDefaultScores().get(0).split(":")[1]);
        try {
            // Get the directory path where the jar file is located or current directory if run from code
            String dirPath = getJarDirectory();
            String filePath = dirPath + File.separator + "scores.txt";

            // Check if the file exists, and create it if it doesn't
            File scoresFile = new File(filePath);
            if (!scoresFile.exists()) {
                fillLocalFileWithDefaultScores(scoresFile);
                return highScoreToReturn;
            } else {
                // If file exists, read the first line from the file to get the high score
                System.out.println("File already exists at: " + filePath);
                BufferedReader reader = new BufferedReader(new FileReader(scoresFile));
                String firstLine = reader.readLine();

                // The format each line of the file should be in
                Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{1,16}:\\d+$");

                // If first line isn't empty and matches the pattern
                if (firstLine != null && pattern.matcher(firstLine).matches() && firstLine.endsWith("0")) {
                        highScoreToReturn = Integer.valueOf(firstLine.split(":")[1]);
                } else {
                    fillLocalFileWithDefaultScores(scoresFile);
                    return highScoreToReturn;
                }

                String line;
                while ((line = reader.readLine()) != null) {
                    // If a line doesn't match the required format
                    if (!pattern.matcher(line).matches() || !line.endsWith("0")) {
                        fillLocalFileWithDefaultScores(scoresFile);
                        return highScoreToReturn;
                    }
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return highScoreToReturn;
    }

    /**
     * Creates a new scores.txt file and fills it with default scores
     * @param fileToBeCreated File to be created in a directory
     */
    private void fillLocalFileWithDefaultScores(File fileToBeCreated) throws IOException {
        logger.info("FILLING LOCAL FILE WITH DEFAULT SCORES");
        fileToBeCreated.createNewFile();

        // Fill file with default scores
        FileWriter writer = new FileWriter(fileToBeCreated, false);
        ArrayList<String> defaultScores = getDefaultScores();
        for (String score : defaultScores) {
            writer.write(score + "\n");
        }
        writer.close();
    }

    /**
     * Default scores list for if there is no score file
     * @return ArrayList of 10 default scores in the form "name:score"
     */
    private static ArrayList<String> getDefaultScores() {
        ArrayList<String> defaultScores = new ArrayList<>();
        defaultScores.add("John:10000");
        defaultScores.add("John:9000");
        defaultScores.add("John:8000");
        defaultScores.add("John:7000");
        defaultScores.add("John:6000");
        defaultScores.add("John:5000");
        defaultScores.add("John:4000");
        defaultScores.add("John:3000");
        defaultScores.add("John:2000");
        defaultScores.add("John:1000");
        return defaultScores;
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
        logger.info("Initialising ChallengeScene");
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
