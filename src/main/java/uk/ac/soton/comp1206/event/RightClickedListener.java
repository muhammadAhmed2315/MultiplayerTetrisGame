package uk.ac.soton.comp1206.event;

/**
 * Handles what happens when a GameBoard or PieceBoard is mouse clicked.
 */
public interface RightClickedListener {

    /**
     * Called when a GameBoard or PieceBoard is mouse clicked.
     * Implementing classes should provide the necessary logic to handle the mouse click event.
     */
    void handle();
}
