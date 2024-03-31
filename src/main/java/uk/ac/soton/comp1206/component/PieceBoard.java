package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Specialised GameBoard used for displaying a single game piece, and provides additional
 * functionality for controlling the hover effect on the displayed piece.
 */
public class PieceBoard extends GameBoard {
    /**
     * Decides whether the hover effect should be enabled or not in the middle of the PieceBoard
     */
    private boolean hover;

    public PieceBoard(int cols, int rows, double width, double height, boolean hover) {
        super(cols, rows, width, height);
        this.hover = hover;
    }

    /**
     * Displays a single piece on the PieceBoard
     * @param nextPiece piece to be displayed
     */
    public void displayPiece(GamePiece nextPiece) {
        this.grid.clear();
        this.grid.playPiece(nextPiece, 1, 1);
        if (hover) {
            getBlock(1, 1).onHover();
        }
    }
}
