package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 *  Handles the event when a line in the game needs to be cleared. Passes the list of blocks
 *  that need to be cleared.
 */
public interface LineClearedListener {
    /**
     * Handle a line cleared event
     * @param blocksToBeCleared the set of blocks that need to be cleared
     */
    void handle(HashSet<GameBlockCoordinate> blocksToBeCleared);
}