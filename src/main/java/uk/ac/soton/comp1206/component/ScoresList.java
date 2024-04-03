package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * Specialised component for showing the list of high scores at the end of a game.
 */
public class ScoresList extends VBox {
  private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

    public ScoresList(ObservableList<Pair<String, Integer>> scores) {
        this.scores.set(scores);
        this.scores.addListener((ListChangeListener<Pair<String, Integer>>) change -> {
            updateDisplay();
        });
    }

    /**
     * Updates the score list component if the scores list changes
     */
    private void updateDisplay() {
        getChildren().clear();  // Clear existing score display
        var temp = scores.get().subList(0, Math.min(10, scores.size()));
        for (Pair<String, Integer> score : temp) {
            var tempLabel = new Label(score.toString());
            tempLabel.getStyleClass().add("scorelist");
            getChildren().add(tempLabel);
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