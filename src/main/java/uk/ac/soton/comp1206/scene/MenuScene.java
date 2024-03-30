package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        //Awful title
        var title = new Text("TetrECS");
        title.getStyleClass().add("title");
        mainPane.setTop(title);

        // Main menu buttons list
        var singlePlayerButton = new Button("Single Player");
        var multiPlayerButton = new Button("Multi Player");
        var instructionsButton = new Button("How to Play");
        var exitButton = new Button("Exit");

        //Bind the button actions to the necessary methods
        singlePlayerButton.setOnAction(this::startGame);
        // TODO: multiplayer button
        // TODO: instruction button
        exitButton.setOnAction((event) -> Platform.exit());

        VBox buttonsList = new VBox(singlePlayerButton, multiPlayerButton, instructionsButton, exitButton);
        buttonsList.setAlignment(Pos.CENTER);
        buttonsList.setSpacing(12);
        mainPane.setCenter(buttonsList);


        // If other background music is already playing
        if (Multimedia.getMusicPlayer() != null) {
            Multimedia.getMusicPlayer().stop();
            Multimedia.playBackgroundMusic("menu.mp3");
        } else {
            // No background music is playing (when game is first booted up)
            Multimedia.playBackgroundMusic("menu.mp3");
        }

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

}
