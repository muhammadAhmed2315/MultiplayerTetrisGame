package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Handles receiving notifications about the next game piece. Notifies classes when the next
 * game piece is available, along with the current game piece.
 */
public interface NextPieceListener {

    /**
     * Called when the next game piece is available.
     * @param currentGamePiece The current game piece being played.
     * @param nextGamePiece    The next game piece that will be played.
     */
    public void nextPiece(GamePiece currentGamePiece, GamePiece nextGamePiece);
}
