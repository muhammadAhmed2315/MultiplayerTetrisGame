package uk.ac.soton.comp1206.scene;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

public class LobbyScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Lobby Scene");
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // Basic UI setup
        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var contentVBox = new VBox();
        contentVBox.setPadding(new Insets(10, 10, 10, 10));
        contentVBox.setAlignment(Pos.TOP_CENTER);
        challengePane.getChildren().add(contentVBox);

        // HBox for Multiplayer heading at the top of the screen
        Label multiplayerHeading = new Label("Multiplayer");
        multiplayerHeading.getStyleClass().add("title");
        HBox headingHBox = new HBox();
        headingHBox.setAlignment(Pos.CENTER);
        headingHBox.getChildren().add(multiplayerHeading);

        // Left hand side of the screen containing the current channels and the option to create a new channel
        Label currentGamesHeading = new Label("Current Games");
        currentGamesHeading.getStyleClass().add("heading");

        Label hostNewGameHeading = new Label("Host New Game");
        hostNewGameHeading.getStyleClass().add("heading");

        TextField newChannelNameTextField = new TextField();
        newChannelNameTextField.setVisible(false);
        newChannelNameTextField.setOnAction((event) -> {
                gameWindow.getCommunicator().send("CREATE " + newChannelNameTextField.getText());
                newChannelNameTextField.clear();
                newChannelNameTextField.setVisible(false);
        });

        hostNewGameHeading.setOnMouseClicked((event) -> {
            Multimedia.switchAudioFile("rotate.wav");
            newChannelNameTextField.setVisible(true);
        });

        VBox channelsList = new VBox();
        ScrollPane channelScroller = new ScrollPane();
        VBox.setVgrow(channelScroller, Priority.ALWAYS);
        channelScroller.setContent(channelsList);

        // Set the background of the ScrollPane to be transparent
        channelScroller.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        // Making the viewport transparent
        channelScroller.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            channelScroller.lookup(".viewport").setStyle("-fx-background-color: transparent;");
        });

        VBox leftSectionVBox = new VBox(currentGamesHeading, hostNewGameHeading, newChannelNameTextField, channelScroller);

        // Right hand side of the screen containing the channel name heading and the chat box
        Label chatboxHeading = new Label();
        chatboxHeading.getStyleClass().add("heading");

        HBox usersList = new HBox();

        Label chatBoxInfoText = new Label("Welcome to the lobby \n Type /nick NewName to change your name");
        chatBoxInfoText.getStyleClass().add("messages");

        Button startGameButton = new Button("Start game");
        startGameButton.setVisible(false);
        Button leaveGameButton = new Button("Leave game");
        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        VBox messagesList = new VBox();
        ScrollPane messagesScroller = new ScrollPane();
        VBox.setVgrow(messagesScroller, Priority.ALWAYS);
        messagesScroller.setContent(messagesList);

        // Set the background of the ScrollPane to be transparent
        messagesScroller.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        // Making the viewport transparent
        messagesScroller.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            messagesScroller.lookup(".viewport").setStyle("-fx-background-color: transparent;");
        });

        TextField messageInputField = new TextField();
        messageInputField.setPromptText("Send a message");
        messageInputField.setOnAction((event) -> {
            if (messageInputField.getText().startsWith("/nick ")) {
                gameWindow.getCommunicator().send("NICK " + messageInputField.getText().split(" ")[1]);
            } else {
                gameWindow.getCommunicator().send("MSG " + messageInputField.getText());
            }
            messageInputField.clear();
        });

        HBox chatBoxButtons = new HBox(startGameButton, space, leaveGameButton);

        VBox chatBox = new VBox(usersList, chatBoxInfoText, messagesScroller, messageInputField, chatBoxButtons);
        chatBox.setPrefWidth(480);
        chatBox.setPrefHeight(425);
        chatBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-border-color: white; -fx-border-width: 2;");
        chatBox.setSpacing(10);
        chatBox.setPadding(new Insets(10, 10, 10, 10));

        VBox rightSectionVBox = new VBox(chatboxHeading, chatBox);
        rightSectionVBox.setVisible(false);
        rightSectionVBox.setSpacing(10);

        // Horizontal region to separate the channels list and the chatbox
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox mainContentHBox = new HBox(leftSectionVBox, spacer, rightSectionVBox);
        contentVBox.getChildren().addAll(headingHBox, mainContentHBox);

        leaveGameButton.setOnMouseClicked((event) -> {
            gameWindow.getCommunicator().send("PART");
            startGameButton.setVisible(false);
        });

        startGameButton.setOnMouseClicked((event) -> {
            gameWindow.getCommunicator().send("START");
        });

        // Create a Timer to request current channels from the server every 5 seconds
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                gameWindow.getCommunicator().send("LIST");
            }
        }, 0, 5000); // 0 initial delay, 5000 ms period (5 seconds)

        // Listener for communications
        gameWindow.getCommunicator().addListener((message) -> {
            if (message.startsWith("CHANNELS ")) {
                // Updates channels list
                message = message.split(" ")[1];
                String[] channels = message.split("\n");
                Platform.runLater(() -> {
                    channelsList.getChildren().clear();
                    for (String channel : channels) {
                        Label channelNameHeading = new Label(channel);
                        channelNameHeading.getStyleClass().add("channelItem");
                        channelNameHeading.setOnMouseClicked((event) -> {
                            gameWindow.getCommunicator().send("JOIN " + channel);
                        });
                        channelsList.getChildren().add(channelNameHeading);
                    }
                });
            } else if (message.startsWith("JOIN ")) {
                Multimedia.switchAudioFile("message.wav");
                // Makes the chat box visible once the user has joined a lobby
                String finalMessage = message.split(" ")[1];
                Platform.runLater(() -> {
                    gameWindow.getCommunicator().send("LIST");
                    chatboxHeading.setText(finalMessage);
                    rightSectionVBox.setVisible(true);
                });
            } else if (message.startsWith("USERS ")) {
                // Updates user list at the top of the chat box
                message = message.split(" ")[1];
                String[] users = message.split("\n");
                Platform.runLater(() -> {
                    usersList.getChildren().clear();
                    for (String user : users) {
                        Label userLabel = new Label(user);
                        userLabel.getStyleClass().add("channelItem");
                        usersList.getChildren().add(userLabel);
                    }
                });
            } else if (message.equals("PARTED")) {
                // If user leaves a lobby
                gameWindow.getCommunicator().send("LIST");
                rightSectionVBox.setVisible(false);
                Platform.runLater(() -> {
                    messagesList.getChildren().clear();
                    rightSectionVBox.setVisible(false);
                });
            } else if (message.equals("HOST")) {
                // If user becomes the host of a lobby he is currently in
                startGameButton.setVisible(true);
            } else if (message.startsWith("MSG ")) {
                // Change messageLabel to the following form: <HH:MM> playerName:message
                Multimedia.switchAudioFile("message.wav");
                message = message.substring(4);
                String playerName = message.split(":")[0];
                String playerMessage = message.split(":")[1];
                Platform.runLater(() -> {
                    LocalTime now = LocalTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                    String formattedTime = now.format(formatter);
                    formattedTime = "[" + formattedTime + "]";

                    Label messageLabel = new Label(formattedTime + " <" + playerName + "> " + playerMessage);
                    messageLabel.getStyleClass().add("messages");
                    messagesList.getChildren().add(messageLabel);
                });
            } else if (message.equals("START")) {
                Platform.runLater(() -> {
                    gameWindow.startMultiplayerGame();
                    timer.cancel();
                });
            } else if (message.startsWith("ERROR ")) {
                message = message.substring(6);
                String finalMessage1 = message;
                Platform.runLater(() -> {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Error");
                    alert.setContentText(finalMessage1);

                    // Display the alert and wait for a response
                    alert.showAndWait();
                });
            }
        });
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        // Add keyboard listener to the scene
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    gameWindow.getCommunicator().send("PART");
                    gameWindow.startMenu();
                    break;
            }
        });
    }
}
