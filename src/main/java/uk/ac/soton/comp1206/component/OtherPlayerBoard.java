package uk.ac.soton.comp1206.component;

/**
 * Used to show another player's board in the multiplayer game.
 */
public class OtherPlayerBoard extends GameBoard {
    /**
     * Create a new GameBoard with its own internal grid, specifying the number of columns and rows,
     * along with the visual width and height.
     *
     * @param cols   number of columns for internal grid
     * @param rows   number of rows for internal grid
     * @param width  the visual width
     * @param height the visual height
     */
    public OtherPlayerBoard(int cols, int rows, double width, double height) {
        super(cols, rows, width, height);
    }

    /**
     * Updates the grid of this board based on a string representation of the flattened grid.
     * The string should contain space separated integers, with each integer representing the value
     * of the GameBlock at that location.
     * @param flattenedGrid A string representing the flattened grid data
     */
    public void updateBoard(String flattenedGrid) {
        grid.clear();

        String[] flattenedGridArray = flattenedGrid.split(" ");

        /**
         * Loops over each element of the grid, and then sets its value to the corresponding value
         * in the flattenedGrid String representation
         */
        for (int columnIndex = 0; columnIndex < getRows(); columnIndex++) {
            for (int rowIndex = 0; rowIndex < getCols(); rowIndex++) {
                grid.set(columnIndex, rowIndex, Integer.valueOf(flattenedGridArray[(columnIndex * getCols()) + rowIndex]));
            }
        }

    }
}
