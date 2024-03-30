package uk.ac.soton.comp1206.component;

import uk.ac.soton.comp1206.game.GamePiece;

public class PieceBoard extends GameBoard {

    public PieceBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    public void displayPiece(GamePiece nextPiece) {
        this.grid.clear();
        this.grid.playPiece(nextPiece, 1, 1);
    }
}
