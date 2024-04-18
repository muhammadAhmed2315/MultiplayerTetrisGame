package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;

/**
 * Specialised component for showing the list of high scores at the end of a game.
 */
public class ScoresList extends VBox {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * List of scores to be displayed
     */
    private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

    public ScoresList(ObservableList<Pair<String, Integer>> scores) {
        this.scores.set(scores);
        this.scores.addListener((ListChangeListener<Pair<String, Integer>>) change -> {
            Platform.runLater(() -> {
                updateDisplay();
            });
        });
        setAlignment(Pos.CENTER);
    }

    /**
     * Updates the score list component if the scores list changes
     */
    private void updateDisplay() {
        String[] colours = {"Fuchsia", "Red", "DarkOrange", "Yellow", "YellowGreen", "LimeGreen", "MediumSpringGreen", "SkyBlue", "DeepSkyBlue", "DodgerBlue"};

        getChildren().clear();  // Clear existing score display
        var temp = scores.get().subList(0, Math.min(10, scores.size()));
        int index = 0;

        for (Pair<String, Integer> score : temp) {
            var tempLabel = new Label(score.toString());
            tempLabel.getStyleClass().add("scorelist");
            tempLabel.setStyle("-fx-text-fill: " + colours[index] + ";");
            tempLabel.setOpacity(0);  // Set the initial opacity to 0 (invisible)
            getChildren().add(tempLabel);

            // Create a fade transition for the label
            FadeTransition fade = new FadeTransition(Duration.seconds(0.3), tempLabel);
            fade.setFromValue(0);  // Start from invisible
            fade.setToValue(1);  // Fade to fully visible
            fade.setDelay(Duration.seconds(0.3 * index));  // Delay based on the label index

            fade.play();  // Start the animation
            index++;
        }
    }

    /**
     * Returns the scores list
     * @return scores list
     */
    public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
        return this.scores;
    }

}