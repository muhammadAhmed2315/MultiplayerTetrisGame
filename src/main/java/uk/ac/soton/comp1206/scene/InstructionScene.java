package uk.ac.soton.comp1206.scene;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Instruction Scene");
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        challengePane.getChildren().add(contentVBox);

        // Heading label
        Label instructionsHeading = new Label("Instructions");
        instructionsHeading.getStyleClass().add("heading");

        // Text below heading
        Label instructionsDetails = new Label("TetrECS is a fast-paced gravity-fre block placement "
            + "game, where you must survive by clearing rows through careful placement of the "
            + "upcoming blocks before the time runs out. Lose all 3 lives and you're destroyed!");
        instructionsDetails.getStyleClass().add("instructions");
        instructionsDetails.setWrapText(true);
        instructionsDetails.setTextAlignment(TextAlignment.CENTER);

        // Instructions image
        var imageFilePath = InstructionScene.class.getResource("/images/" + "Instructions.png").toExternalForm();
        Image image = new Image(imageFilePath);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(root.heightProperty().divide(1.5));
        imageView.fitWidthProperty().bind(root.widthProperty().divide(1.5));

        // Game Pieces heading
        Label gamePiecesHeading = new Label("Game Pieces");
        gamePiecesHeading.getStyleClass().add("heading");

        // GridPane to store 15 pieces
        GridPane piecesGrid = new GridPane();
        piecesGrid.setAlignment(Pos.CENTER);
        piecesGrid.setGridLinesVisible(true);
        piecesGrid.setHgap(10); // Horizontal gap
        piecesGrid.setVgap(10); // Vertical gap

        int piecesCounter = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                PieceBoard myPiece = new PieceBoard(3, 3, 55, 55);
                myPiece.displayPiece(GamePiece.createPiece(piecesCounter));
                piecesGrid.add(myPiece, col, row);
                piecesCounter++;
            }
        }

        contentVBox.getChildren().addAll(
            instructionsHeading, instructionsDetails, imageView, gamePiecesHeading, piecesGrid
        );
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {

    }
}
