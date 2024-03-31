package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * TODO this comment
 * The LineClearedListener is used to handle the event when lines are cleared in the game.
 * It passes a Set of GameBlockCoordinates representing the blocks that were cleared.
 */
public interface LineClearedListener {
    /**
     * TODO this comment
     * Handle a line cleared event.
     * @param blocksToBeCleared the set of blocks that were cleared
     */
    void handle(HashSet<GameBlockCoordinate> blocksToBeCleared);
}