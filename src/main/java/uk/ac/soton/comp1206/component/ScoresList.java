package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

// TODO this comment
public class ScoresList extends VBox {
  private final SimpleListProperty<Pair<String, Integer>> scores = new SimpleListProperty<>();

    public ScoresList(ObservableList<Pair<String, Integer>> scores) {
        this.scores.set(scores);
        this.scores.addListener((ListChangeListener<Pair<String, Integer>>) change -> {
            updateDisplay();
        });
    }

    private void updateDisplay() {
        getChildren().clear();  // Clear existing score display
        for (Pair<String, Integer> score : scores.get()) {
            getChildren().add(new Label(score.toString()));
        }
    }

    public SimpleListProperty<Pair<String, Integer>> scoresProperty() {
        return this.scores;
    }

}