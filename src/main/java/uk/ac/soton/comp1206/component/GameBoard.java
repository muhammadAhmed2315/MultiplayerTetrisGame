package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;

import java.util.HashSet;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of its own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    private final int cols;

    /**
     * Number of rows in the board
     */
    private final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    private final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    private final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * Decides whether the block outline hovering effect should be enabled for this GameBoard
     * or not [TRUE = hovering effects, FALSE = no hovering effects]
     */
    private final boolean hoverEnabled;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    private BlockClickedListener blockClickedListener;

    /**
     * The listener to call when the user wants to rotate a block
     */
    private RightClickedListener rightClickedListener;

    /**
     * Coordinate for where the user is aiming using the keyboard
     */
    private GameBlockCoordinate keyboardAim;

    /**
     * Coordinate for where the user is aiming using the mouse
     */
    private GameBlockCoordinate mouseAim;

    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;
        // This constructor for GameBoard is used for the main GameBoard, which we want hovering
        // effects for
        hoverEnabled = true;
        keyboardAim = new GameBlockCoordinate(0, 0);
        mouseAim = new GameBlockCoordinate(0, 0);

        //Build the GameBoard
        build();

        // Add a mouse click handler to the main GameBoard to trigger the
        // GameBoardRightClicked method
        this.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                this.GameBoardRightClicked();
            }
        });
    }

    /**
     * Create a new GameBoard with its own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);
        // This constructor for GameBoard is used by PieceBoard, which we don't want hovering
        // effects for
        hoverEnabled = false;

        //Build the GameBoard
        build();

        // Add a mouse click handler to the primary GameBoard that handles what happens when the
        // primary GameBoard is left-clicked
        this.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.GameBoardRightClicked();
            }
        });
    }

    /**
     * Get a specific block from the GameBoard, specified by its row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        logger.info("Building grid: {} x {}",cols,rows);

        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                var temp_block = createBlock(x,y);
                if (hoverEnabled) {
                    // Add a mouse handler for once the block is hovered
                    temp_block.setOnMouseEntered((event) -> {
                        blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
                        temp_block.onHover();
                        mouseAim = new GameBlockCoordinate(temp_block.getX(), temp_block.getY());
                    });
                    // Add a mouse handler for once the hovered block is no longer hovered
                    temp_block.setOnMouseExited((event) -> {
                        blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
                        temp_block.offHover();
                    });
                }
            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     * @param x column
     * @param y row
     * @return Newly created GameBlock
     */
    protected GameBlock createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        // Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked((event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                blockClicked(block);
            }
        });

        return block;
    }

    // Calls the fading animation on each block that is passed to it
    public void fadeOut(HashSet<GameBlockCoordinate> coordinates) {
        for (GameBlockCoordinate coordinate : coordinates) {
            getBlock(coordinate.getX(), coordinate.getY()).fadeOut();
        }
    }

    /**
     * Add a listener to the GameBoard that handles what happens when a block is clicked
     * @param listener listener to add to the GameBoard
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Add a listener to the GameBoard that handles what happens when a GameBoard is clicked
     * @param rightClickedListener listener to add to the GameBoard
     */
    public void setOnRightClick(RightClickedListener rightClickedListener) {
        this.rightClickedListener = rightClickedListener;
    }

    /**
     * Triggered when the main GameBoard is right-clicked. Call the attached listener.
     */
    private void GameBoardRightClicked() {
        logger.info("Main GameBoard right-clicked");

        if (rightClickedListener != null) {
            rightClickedListener.handle();
        }
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param block block clicked on
     */
    private void blockClicked(GameBlock block) {
        logger.info("Block clicked: {}", block);

        if(blockClickedListener != null) {
            blockClickedListener.blockClicked(block);
        }
    }

    public void placeKeyboardAim() {
        blockClicked(blocks[keyboardAim.getX()][keyboardAim.getY()]);
    }

    public void moveKeyboardAimUp() {
        if (keyboardAim.getY() != 0) {
            blocks[mouseAim.getX()][mouseAim.getY()].offHover();
            blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
            keyboardAim = keyboardAim.subtract(0, 1);
            blocks[keyboardAim.getX()][keyboardAim.getY()].onHover();
        }
    }

    public void moveKeyboardAimDown() {
        if (keyboardAim.getY() != 4) {
            blocks[mouseAim.getX()][mouseAim.getY()].offHover();
            blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
            keyboardAim = keyboardAim.add(0, 1);
            blocks[keyboardAim.getX()][keyboardAim.getY()].onHover();
        }
    }

    public void moveKeyboardAimRight() {
        if (keyboardAim.getX() != 4) {
            blocks[mouseAim.getX()][mouseAim.getY()].offHover();
            blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
            keyboardAim = keyboardAim.add(1, 0);
            blocks[keyboardAim.getX()][keyboardAim.getY()].onHover();
        }
    }

    public void moveKeyboardAimLeft() {
        if (keyboardAim.getX() != 0) {
            blocks[mouseAim.getX()][mouseAim.getY()].offHover();
            blocks[keyboardAim.getX()][keyboardAim.getY()].offHover();
            keyboardAim = keyboardAim.subtract(1, 0);
            blocks[keyboardAim.getX()][keyboardAim.getY()].onHover();
        }
    }

}
