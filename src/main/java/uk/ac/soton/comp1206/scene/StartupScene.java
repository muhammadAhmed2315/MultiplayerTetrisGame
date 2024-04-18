package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

/**
 * Startup animation screen. Once the animation is finished, automatically switches to the main menu.
 */
public class StartupScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new startup animation scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public StartupScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Contains the actual animation
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        // Basic UI setup
        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        root.getChildren().add(menuPane);

        VBox mainPane = new VBox();
        menuPane.getChildren().add(mainPane);

        // Intro screen animation
        var startupImageFilePath = MenuScene.class.getResource("/images/" + "ECSGames.png").toExternalForm();
        Image startupImage = new Image(startupImageFilePath);
        ImageView startupImageView = new ImageView(startupImage);
        startupImageView.setPreserveRatio(true);
        startupImageView.setFitHeight(gameWindow.getHeight() / 5);
        startupImageView.setFitHeight(gameWindow.getWidth() / 5);

        mainPane.getChildren().add(startupImageView);
        mainPane.setAlignment(Pos.CENTER);

        Multimedia.playAudioFile("intro.mp3");

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(4), startupImageView);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();

        fadeTransition.setOnFinished((event) -> {
            gameWindow.startMenu();
        });

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {

    }

}
