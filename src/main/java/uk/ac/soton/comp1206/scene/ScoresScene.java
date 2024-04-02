package uk.ac.soton.comp1206.scene;

import java.io.*;
import java.util.ArrayList;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    // TODO this comment
    private Game game;

    // TODO this comment
    private SimpleListProperty<Pair<String, Integer>> localScores;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        logger.info("Creating Scores Scene");
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableScores = FXCollections.observableArrayList(scores);

        localScores = new SimpleListProperty<>(observableScores);

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        challengePane.getChildren().add(contentVBox);

        var imageFilePath = ScoresScene.class.getResource("/images/" + "TetrECS.png").toExternalForm();
        Image image = new Image(imageFilePath);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(root.heightProperty().divide(1.5));
        imageView.fitWidthProperty().bind(root.widthProperty().divide(1.5));

        // Game Over heading
        Label gameOverHeading = new Label("Game Over");
        gameOverHeading.getStyleClass().add("score");

        // High Scores heading
        Label highScoresHeading = new Label("High Scores");
        highScoresHeading.getStyleClass().add("heading");

        // Scores list
        loadScores();
        ScoresList scoresList = new ScoresList(localScores);
        scoresList.scoresProperty().bind(localScores);

        contentVBox.getChildren().addAll(imageView, gameOverHeading, highScoresHeading, scoresList);
    }

    // TODO this comment
    public void loadScores() {
        var inputStream = ScoresScene.class.getResourceAsStream("/scores.txt");

        if (inputStream == null) {
            throw new RuntimeException("File scores.txt not found in the resources directory");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                localScores.add(new Pair(line.split(":")[0], line.split(":")[1]));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
    // Add keyboard listener to the scene
    scene.addEventHandler(
        KeyEvent.KEY_PRESSED,
        event -> {
          switch (event.getCode()) {
            case ESCAPE:
              logger.info("Escape key pressed");
              gameWindow.startMenu();
              break;
            default:
              logger.info("Key pressed: {}", event.getText());
          }
        });
    }

}
