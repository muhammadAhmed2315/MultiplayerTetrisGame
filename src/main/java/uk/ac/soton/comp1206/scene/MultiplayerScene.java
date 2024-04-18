package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
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
import uk.ac.soton.comp1206.component.OtherPlayerBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The Multi Player challenge scene. Holds the UI for the multiplayer challenge mode in the game.
 */
public class MultiplayerScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * The game logic and state manager for the multiplayer mode.
     */
    protected MultiplayerGame game;

    /**
     * The visual representation of the user's game grid.
     */
    protected GameBoard board;

    /**
     * Map containing player names as the keys, and the current state of their boards (in
     * flattened string form)
     */
    private Map<String, String> playerBoards = new HashMap<>();

    /**
     * Represents which player's board is being shown right now
     */
    private int playerIndex = 0;

    /**
     * List containing all of the players playing the game
     */
    List<String> playerBoardsKeys;

    /**
     * Current player's nickname
     */
    private String playerNickname;

    /**
     * Map containing player names as the keys, and their current scores
     */
    private Map<String, Integer> playerScoresHashMap = new HashMap<>();

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

        AtomicInteger counter = new AtomicInteger();

        gameWindow.getCommunicator().addListener((message) -> {
            logger.info(message);
            if (message.startsWith("USERS ")) {
                // Remove "USERS " part from the message, leaving <User>\n<User>\n<User>...
                message = message.substring(6);
                String emptyFlattenedGrid = "";

                // Flattened grid representation with every block holding a value of 0
                for (int i = 0; i < game.getCols(); i++) {
                    for (int j = 0; j < game.getRows(); j++) {
                        emptyFlattenedGrid += "0 ";
                    }
                }
                emptyFlattenedGrid = emptyFlattenedGrid.trim();

                // If more than one user in the game
                if (message.contains("\n")) {
                    String[] usersArray = message.split("\n");

                    // Assign each user an empty grid
                    for (String user : usersArray) {
                        playerBoards.put(user, emptyFlattenedGrid);
                        playerScoresHashMap.put(user, 0);
                    }

                    // Create list of users
                    playerBoardsKeys = new ArrayList<>(playerBoards.keySet());
                    counter.getAndIncrement();
                } else {
                    // Assign just one player (the current user) an empty grid
                    playerBoards.put(message, emptyFlattenedGrid);

                    // Create list of users
                    playerScoresHashMap.put(message, 0);
                    playerBoardsKeys = new ArrayList<>(playerBoards.keySet());
                    counter.getAndIncrement();
                }
            } else if (message.startsWith("NICK ")) {
                // Set the playerNickname field to the player's nickname
                playerNickname = message.substring(5);
                counter.getAndIncrement();
            }
            if (counter.get() == 2) {
                gameWindow.getCommunicator().clearListeners();
            }
        });

        gameWindow.getCommunicator().send("USERS");
        gameWindow.getCommunicator().send("NICK");

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

        board = new GameBoard(game.getGrid(), gameWindow.getWidth()/2, gameWindow.getWidth()/2);
        VBox boardAndChatVBox = new VBox(board);
        mainPane.setCenter(boardAndChatVBox);

        /*
          Bar at the top of the screen containing a VBox, which contains the player score, title,
          and lives
         */
        Label scoreHeading = new Label(playerNickname);
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

        /*
          Bar on the right hand side containing a VBox that contains: a representation of another
          player's board (user can switch which player's board is being shown), player scoreboard,
          current piece, and next piece
         */
        Label otherPlayerBoardHeading;
        OtherPlayerBoard otherPlayerBoard;
        VBox rightBar = new VBox();

        otherPlayerBoardHeading = new Label(playerBoardsKeys.get(playerIndex));
        otherPlayerBoardHeading.getStyleClass().add("heading");

        otherPlayerBoard = new OtherPlayerBoard(5, 5, 100, 100);
        otherPlayerBoard.updateBoard(playerBoards.get(playerBoardsKeys.get(playerIndex)));

        otherPlayerBoard.setOnMouseClicked((event) -> {
            // Skip over the user's own board by incrementing playerIndex
            if (playerBoardsKeys.get(playerIndex).equals(playerNickname)) {
                playerIndex = (playerIndex + 1) % playerBoardsKeys.size();
            }

            // Change heading to player whose board is being shown
            otherPlayerBoardHeading.setText(playerBoardsKeys.get(playerIndex));

            // Update the board to the player it belongs to
            otherPlayerBoard.updateBoard(playerBoards.get(playerBoardsKeys.get(playerIndex)));

            // Increment the playerIndex
            playerIndex = (playerIndex + 1) % playerBoardsKeys.size();
        });

        rightBar.getChildren().addAll(otherPlayerBoardHeading, otherPlayerBoard);

        // Don't show otherPlayerBoard if user is playing alone
        if (playerBoards.size() == 1) {
            otherPlayerBoardHeading.setVisible(false);
            otherPlayerBoard.setVisible(false);
        }

        Label versusLabel = new Label("Versus");
        versusLabel.getStyleClass().add("heading");

        Label playerScoresFormatLabel = new Label("<Score>:<Lives>");
        playerScoresFormatLabel.getStyleClass().add("channelItem");

        VBox playerScoresVBox = new VBox();
        playerScoresVBox.setAlignment(Pos.CENTER);

        Label incomingLabel = new Label("Incoming");
        incomingLabel.getStyleClass().add("heading");

        PieceBoard currentPieceBoard = new PieceBoard(3, 3, 100, 100, true);
        PieceBoard nextPieceBoard = new PieceBoard(3, 3, 60, 60, false);

        game.setNextPieceListener(((currentGamePiece, nextGamePiece) -> {
            currentPieceBoard.displayPiece(currentGamePiece);
            nextPieceBoard.displayPiece(nextGamePiece);
        }));

        game.setOnLineClear(board::fadeOut);

        rightBar.getChildren().addAll(versusLabel, playerScoresFormatLabel, playerScoresVBox,
                                        incomingLabel, currentPieceBoard, nextPieceBoard);

        rightBar.setSpacing(5);

        rightBar.setAlignment(Pos.CENTER);

        Bindings.bindBidirectional(actualScore.textProperty(), game.getUserScore(), new NumberStringConverter());
        Bindings.bindBidirectional(actualLives.textProperty(), game.getLivesRemaining(), new NumberStringConverter());

        actualLives.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue.equals("-1")) {
                gameWindow.startMultiplayerScores(playerScoresHashMap);
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
        tempVBox.setSpacing(5);
        tempVBox.setAlignment(Pos.CENTER);

        boardAndChatVBox.getChildren().add(tempVBox);
        boardAndChatVBox.setAlignment(Pos.CENTER);

        VBox timerVBox = new VBox(rectangle);
        timerVBox.setSpacing(5);

        mainPane.setTop(topBar);
        mainPane.setRight(rightBar);
        mainPane.setBottom(timerVBox);

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

                        if (!score.endsWith("DEAD")) {
                            int tempScore = Integer.valueOf(score.split(":")[1]);
                            String tempPlayerName = score.split(":")[0];

                            if (tempScore > playerScoresHashMap.get(tempPlayerName)) {
                                playerScoresHashMap.put(tempPlayerName, tempScore);
                            }
                        }

                    }
                });
            } else if (message.startsWith("MSG ")) {
                Multimedia.switchAudioFile("message.wav");
                String messageSenderName = message.split(":")[0];
                String messageText = message.split(":")[1];
                Platform.runLater(() -> {
                    messageInputVisible.set(false);
                    inGameChatLabel.setText("<" + messageSenderName + "> " + messageText);
                });
            } else if (message.startsWith("BOARD ")) {
                    message = message.substring(6);
                    String playerWhoseBoardItIs = message.split(":")[0];
                    String actualBoard = message.split(":")[1];
                    playerBoards.put(playerWhoseBoardItIs, actualBoard);

                    // If the current board is the same as playerWhoseBoardItIs, then update the board
                    if (playerWhoseBoardItIs.equals(otherPlayerBoardHeading.getText())) {
                        Platform.runLater(() -> {
                            otherPlayerBoard.updateBoard(playerBoards.get(playerWhoseBoardItIs));
                        });
                    }
            }
        });

        // If user is only player in the game, then NICK command is not sent by the server, and the scoreHeading
        // label will remain empty. Moreover, the SCORES command will also not be sent by the server until the
        // user plays a piece, and the playerScoresVBox will remain empty.
        // Therefore, these commands are sent to ensure that a NICK response and a SCORES response is returned by
        // the server to make sure that those UI components aren't empty at the start of the game.
        gameWindow.getCommunicator().send("SCORES");

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
        logger.info("Ending the game");
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
