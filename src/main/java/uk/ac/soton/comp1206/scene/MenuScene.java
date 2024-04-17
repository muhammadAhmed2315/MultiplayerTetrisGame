package uk.ac.soton.comp1206.scene;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        //logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        //logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        VBox mainPane = new VBox();
        menuPane.getChildren().add(mainPane);

        // Title image
        var titleImageFilePath = InstructionsScene.class.getResource("/images/" + "TetrECS.png").toExternalForm();
        Image titleImage = new Image(titleImageFilePath);
        ImageView titleImageView = new ImageView(titleImage);
        titleImageView.setPreserveRatio(true);
        titleImageView.fitHeightProperty().bind(root.heightProperty().divide(1.5));
        titleImageView.fitWidthProperty().bind(root.widthProperty().divide(1.5));

        // Animation for the title image
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), titleImageView);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.2);  // Scale up to 120%
        scaleTransition.setToY(1.2);  // Scale up to 120%
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);  // Automatically reverse the animation

        // Start the animation
        scaleTransition.play();

        // Vertical gap between the title image and the buttons menu
        Region spacer = new Region();
        spacer.setPrefHeight(100);

        // Main menu buttons list
        Label singlePlayerLabel = new Label("Single Player");
        Label multiplayerLabel = new Label("Multi Player");
        Label instructionsLabel = new Label("How to Play");
        Label exitLabel = new Label("Exit");

        // Buttons styling
        singlePlayerLabel.getStyleClass().add("menuItem");
        multiplayerLabel.getStyleClass().add("menuItem");
        instructionsLabel.getStyleClass().add("menuItem");
        exitLabel.getStyleClass().add("menuItem");

        //Bind the button actions to the necessary methods
        singlePlayerLabel.setOnMouseClicked(this::startGame);
        multiplayerLabel.setOnMouseClicked(this::startLobbyScene);
        instructionsLabel.setOnMouseClicked(this::startInstructionScene);
        exitLabel.setOnMouseClicked((event) -> {
            gameWindow.getCommunicator().send("QUIT");
            Platform.exit();
        });

        VBox buttonsList = new VBox(singlePlayerLabel, multiplayerLabel, instructionsLabel, exitLabel);
        buttonsList.setAlignment(Pos.CENTER);
        buttonsList.setSpacing(12);
        mainPane.getChildren().addAll(titleImageView, spacer, buttonsList);
        mainPane.setAlignment(Pos.CENTER);

        Multimedia.switchBackgroundMusic("menu.mp3");

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        // Add keyboard listener to the scene
        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case ESCAPE:
                    gameWindow.startMenu();
                    break;
            }
        });
    }

    /**
     * Switch to the single player challenge scene when triggered by a mouse event.
     * @param event The MouseEvent that triggers this method, not used directly in the method but required for
     *      *              the event-driven interaction.
     */
    private void startGame(MouseEvent event) {
        Multimedia.switchAudioFile("rotate.wav");
        gameWindow.startChallenge();
    }

    /**
     * Switches to the instruction scene when triggered by a mouse event.
     * @param event The MouseEvent that triggers this method, not used directly in the method but required for
     *              the event-driven interaction.
     *
     */
    private void startInstructionScene(MouseEvent event) {
        Multimedia.switchAudioFile("rotate.wav");
        gameWindow.startInstructions();
    }

    /**
     * Switch to the multiplayer lobby scene when triggered by a mouse event.
     * @param event The MouseEvent that triggers this method, not used directly in the method but required for
     *              the event-driven interaction.
     */
    private void startLobbyScene(MouseEvent event) {
        Multimedia.switchAudioFile("rotate.wav");
        gameWindow.startLobby();
    }

}
