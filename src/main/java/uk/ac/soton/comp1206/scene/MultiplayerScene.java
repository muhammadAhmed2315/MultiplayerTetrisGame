package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The Multi Player challenge scene. Holds the UI for the multi player challenge mode in the game.
 */
public class MultiplayerScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected MultiplayerGame game;
    protected GameBoard board;

    /**
     * Determines whether the message input text field should be visible or not
     */
    private SimpleBooleanProperty messageInputVisible = new SimpleBooleanProperty(false);

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating MultiplayerScene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        logger.info("*******************************************************************");

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(), gameWindow.getWidth()/2, gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        // Bar at the top of the screen showing the score on the left and the lives remaining
        // on the right
        // HBox containing score and lives
        gameWindow.getCommunicator().send("NICK");

        Label scoreHeading = new Label();
        Label actualScore = new Label("0");
        scoreHeading.getStyleClass().add("heading");
        actualScore.getStyleClass().add("score");
        VBox scoreVBox = new VBox(scoreHeading, actualScore);

        Pane spacerOne = new Pane();
        HBox.setHgrow(spacerOne, Priority.ALWAYS);

        Label sceneTitleLabel = new Label("Multiplayer Match");
        sceneTitleLabel.getStyleClass().add("title");

        Pane spacerTwo = new Pane();
        HBox.setHgrow(spacerTwo, Priority.ALWAYS);

        Label livesHeading = new Label("Lives");
        Label actualLives = new Label("3");
        livesHeading.getStyleClass().add("heading");
        actualLives.getStyleClass().add("lives");
        VBox livesVBox = new VBox(livesHeading, actualLives);

        HBox topBar = new HBox(scoreVBox, spacerOne, sceneTitleLabel, spacerTwo, livesVBox);
        topBar.setAlignment(Pos.CENTER);

        // Bar on the right hand side, showing the high score, next piece, and the level
        Label versusLabel = new Label("Versus");
        versusLabel.getStyleClass().add("heading");

        Label playerScoresFormatLabel = new Label("Score:Lives");
        playerScoresFormatLabel.getStyleClass().add("heading");

        VBox playerScoresVBox = new VBox();
        playerScoresVBox.setAlignment(Pos.CENTER);

        Label incomingLabel = new Label("Incoming");
        incomingLabel.getStyleClass().add("heading");

        PieceBoard currentPieceBoard = new PieceBoard(3, 3, 132, 132, true);
        PieceBoard nextPieceBoard = new PieceBoard(3, 3, 80, 80, false);

        game.setNextPieceListener(((currentGamePiece, nextGamePiece) -> {
            currentPieceBoard.displayPiece(currentGamePiece);
            nextPieceBoard.displayPiece(nextGamePiece);
        }));

        game.setOnLineClear(board::fadeOut);

        VBox rightBar = new VBox(versusLabel, playerScoresFormatLabel, playerScoresVBox, incomingLabel, currentPieceBoard, nextPieceBoard);
        rightBar.setSpacing(10);
        rightBar.setAlignment(Pos.CENTER);

        Bindings.bindBidirectional(actualScore.textProperty(), game.getUserScore(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLives.textProperty(), game.getLivesRemaining(), new NumberStringConverter());

        actualLives.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("-1")) {
                gameWindow.startScores(game);
                endGame();
            }
        });

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

        Label inGameChatLabel = new Label("In-Game Chat: Press T to send a chat message");
        inGameChatLabel.getStyleClass().add("messages");

        TextField messageInput = new TextField();
        // set pref width and height
        messageInput.visibleProperty().bind(messageInputVisible);
        messageInput.setOnAction((event) -> {
            gameWindow.getCommunicator().send("MSG " + messageInput.getText());
            Platform.runLater(() -> {
                messageInput.clear();
            });
        });

        VBox chatVBox = new VBox(inGameChatLabel, messageInput);

        chatVBox.setAlignment(Pos.CENTER);

        VBox tempVBox = new VBox(inGameChatLabel, chatVBox);
        tempVBox.setSpacing(10);
        tempVBox.setAlignment(Pos.CENTER);

        VBox timerVBox = new VBox(tempVBox, rectangle);
        timerVBox.setSpacing(10);

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

        // Handle receiving scores from the server
        gameWindow.getCommunicator().addListener((message) -> {
            if (message.startsWith("SCORES ")) {
                // Update scoreboard
                message = message.substring(7);
                String[] scores = message.split("\n");
                Platform.runLater(() -> {
                    String[] colours = {"Fuchsia", "Red", "DarkOrange", "Yellow", "YellowGreen", "LimeGreen", "MediumSpringGreen", "SkyBlue", "DeepSkyBlue", "DodgerBlue"};
                    playerScoresVBox.getChildren().clear();
                    int index = 0;
                    for (String score : scores) {
                        Label playerScore = new Label(score);
                        playerScore.getStyleClass().add("channelItem");
                        playerScore.setStyle("-fx-text-fill: " + colours[index] + ";");
                        playerScoresVBox.getChildren().add(playerScore);

                        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), playerScore);
                        fade.setFromValue(0);
                        fade.setToValue(1);

                        fade.play();
                        index++;
                    }
                });
            } else if (message.startsWith("NICK ")) {
                // Change heading above score
                scoreHeading.setText(message.substring(5));
            } else if (message.startsWith("MSG ")) {
                Multimedia.switchAudioFile("message.wav");
                String messageSenderName = message.split(":")[0];
                String messageText = message.split(":")[1];
                Platform.runLater(() -> {
                    messageInputVisible.set(false);
                    inGameChatLabel.setText("<" + messageSenderName + "> " + messageText);
                });
            }
        });

        // If user is only player in the game, then NICK command is not sent by the server, and the scoreHeading
        // label will remain empty. Moreover, the SCORES command will also not be sent by the server until the
        // user plays a piece, and the playerScoresVBox will remain empty.
        // Therefore, these commands are sent to ensure that a NICK response and a SCORES response is returned by
        // the server to make sure that those UI components aren't empty at the start of the game.
        gameWindow.getCommunicator().send("NICK");
        gameWindow.getCommunicator().send("SCORES");
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
        //Start new game
        game = new MultiplayerGame(5, 5, gameWindow.getCommunicator());
    }

    /**
     * Cleanup code before the game is ended
     */
    public void endGame() {
        //logger.info("Ending the game");
        gameWindow.getCommunicator().send("DIE");
        game.gameTimerShutdown();
        game = null;
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising MultiplayerScene");
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
                case T:
                messageInputVisible.set(true);
            }
        });
    }

}
