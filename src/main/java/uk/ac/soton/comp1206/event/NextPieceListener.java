package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * TODO update this comment its not correct
 * The Block Clicked listener is used to handle the event when a block in a GameBoard is clicked. It
 * passes the GameBlock that was clicked in the message
 */
public interface NextPieceListener {

    /**
     * TODO update this comment its not correct
     * @param currentGamePiece
     * @param nextGamePiece
     */
    public void nextPiece(GamePiece currentGamePiece, GamePiece nextGamePiece);
}
