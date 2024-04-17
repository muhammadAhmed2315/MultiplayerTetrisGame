package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlockCoordinate;

import java.util.HashSet;

/**
 *  Handles what happens when a line in the game needs to be cleared. Needs to be passed the list of blocks
 *  that need to be cleared.
 */
public interface LineClearedListener {
    /**
     * Handle a line cleared event
     * @param blocksToBeCleared the set of blocks that need to be cleared
     */
    void handle(HashSet<GameBlockCoordinate> blocksToBeCleared);
}