package uk.ac.soton.comp1206.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
            Color.TRANSPARENT,
            Color.DEEPPINK,
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.YELLOWGREEN,
            Color.LIME,
            Color.GREEN,
            Color.DARKGREEN,
            Color.DARKTURQUOISE,
            Color.DEEPSKYBLUE,
            Color.AQUA,
            Color.AQUAMARINE,
            Color.BLUE,
            Color.MEDIUMPURPLE,
            Color.PURPLE
    };

    private final GameBoard gameBoard;

    private final double width;
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[value.get()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);

        // Semi-transparent grey background
        Color backgroundColor = Color.rgb(128, 128, 128, 0.3);
        gc.setFill(backgroundColor);
        gc.fillRoundRect(0, 0, width, height, 10, 10);

        // Neon sign-like outline
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(2);
        gc.setEffect(new Glow(0.8));
        gc.strokeRoundRect(1, 1, width - 2, height - 2, 8, 8);
        gc.setEffect(null);

        // Inner neon sign-like outline
        gc.setStroke(Color.rgb(0, 0, 0));
        gc.setLineWidth(1);
        gc.setEffect(new Glow(0.6));
        gc.strokeRoundRect(4, 4, width - 8, height - 8, 6, 6);
        gc.setEffect(null);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        // Clear
        gc.clearRect(0, 0, width, height);

        // Neon glow effect
        Color glowColor = ((Color) colour).deriveColor(0, 1, 1, 0.8);
        gc.setEffect(new GaussianBlur(5));
        gc.setFill(glowColor);
        gc.fillRoundRect(2, 2, width - 4, height - 4, 15, 15);
        gc.setEffect(null);

        // Gradient fill
        Color lightColor = ((Color) colour).interpolate(Color.WHITE, 0.3);
        Stop[] stops = new Stop[] {
            new Stop(0, lightColor),
            new Stop(1, (Color) colour)
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, height, true, CycleMethod.NO_CYCLE, stops);
        gc.setFill(gradient);
        gc.fillRoundRect(4, 4, width - 8, height - 8, 10, 10);

        // Reflection effect
        gc.setFill(Color.color(1, 1, 1, 0.2));
        gc.fillOval(width * 0.2, height * 0.2, width * 0.6, height * 0.2);

        // Border
        gc.setStroke(glowColor);
        gc.setLineWidth(2);
        gc.strokeRoundRect(4, 4, width - 8, height - 8, 10, 10);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    @Override
    public String toString() {
        return "GameBlock{" +
            "x=" + x +
            ", y=" + y +
            ", value=" + value.get() +
            '}';
    }
}
