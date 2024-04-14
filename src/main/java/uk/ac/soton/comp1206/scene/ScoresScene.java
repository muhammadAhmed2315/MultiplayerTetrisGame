package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class ScoresScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Contains the final game state
     */
    private Game game;

    /**
     * Contains scores loaded from text file
     */
    private SimpleListProperty<Pair<String, Integer>> localScores;

    /**
     * Decides whether to prompt the user at the beginning of the screen for their name.
     * Essentially, set to true if the user gets a top 10 high score when compared to the scores from the
     * text file.
     */
    private boolean showInputUsernameScreen = false;

    // TODO this comment
    private SimpleListProperty<Pair<String, Integer>> remoteScores;

    // TODO this comment
    private Communicator communicator;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        //logger.info("Creating Scores Scene");
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        //logger.info("Building " + this.getClass().getName());

        logger.info("Connecting to the server from {}", this.getClass().getName());
        communicator = new Communicator("ws://ofb-labs.soton.ac.uk:9700");

        ArrayList<Pair<String, Integer>> localScoresArrayList = new ArrayList<>();
        ArrayList<Pair<String, Integer>> remoteScoresArrayList = new ArrayList<>();


        ObservableList<Pair<String, Integer>> observableLocalScores = FXCollections.observableArrayList(localScoresArrayList);
        ObservableList<Pair<String, Integer>> observableRemoteScores = FXCollections.observableArrayList(remoteScoresArrayList);


        localScores = new SimpleListProperty<>(observableLocalScores);
        remoteScores = new SimpleListProperty<>(observableRemoteScores);

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        loadScores();
        loadOnlineScores();

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        challengePane.getChildren().add(contentVBox);

        var imageFilePath = ScoresScene.class.getResource("/images/" + "TetrECS.png").toExternalForm();
        Image image = new Image(imageFilePath);
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(root.heightProperty().divide(1.5));
        imageView.fitWidthProperty().bind(root.widthProperty().divide(1.5));

        // Game Over heading
        Label gameOverHeading = new Label("Game Over");
        gameOverHeading.getStyleClass().add("score");

        // if there are no high scores and user gets a score higher than 0
        int counter = 0;
        if (localScores.size() == 0 && game.getUserScore().intValue() > 0) {
            showInputUsernameScreen = true;
            counter = 0;
        } else if (localScores.size() < 10) {
            // if there are less than 10 high scores
            showInputUsernameScreen = true;
            counter = localScores.size();
        } else {
            for (Pair<String, Integer> score : localScores) {
                if (score.getValue() < game.getUserScore().intValue()) {
                    showInputUsernameScreen = true;
                    break;
                }
                counter++;
            }
        }

        if (showInputUsernameScreen) {
            contentVBox.getChildren().clear();
            TextField inputUsername = new TextField();
            inputUsername.setPromptText("Enter your name");

            Button myButton = new Button("Submit");
            int finalCounter = counter;
            myButton.setOnAction(event -> {
                // update localScores
                localScores.add(finalCounter, new Pair(inputUsername.getText(), game.getUserScore().intValue()));

                // update scores file
                writeScores();

                // switch scenes
                showInputUsernameScreen = false;
                showScoresList(contentVBox, imageView, gameOverHeading);
            });

            contentVBox.getChildren().addAll(imageView, gameOverHeading, inputUsername, myButton);
        } else {
            showScoresList(contentVBox, imageView, gameOverHeading);
        }

    }

    /**
     * Request online high scores from the server and store them in ScoresScene.remoteScores
     */
    private void loadOnlineScores() {
        communicator.addListener((message) -> {
            String messageWithoutPrefix = message.split(" ")[1];
            String[] scoresList = messageWithoutPrefix.split("\n");
            //logger.info("{}", Arrays.toString(scoresList));
            for (int i = 0; i < 10; i++) {
                //logger.info("{}", i);
                var scorePair = scoresList[i];
                //logger.info("Adding to remoteScores: {}:{}", scorePair.split(":")[0], scorePair.split(":")[1]);
                remoteScores.add(new Pair(scorePair.split(":")[0], Integer.valueOf(scorePair.split(":")[1])));

            }
        });
        communicator.send("HISCORES");
        // Send: HISCORES
        // Receive: HISCORES <Name>:<Score>\n<Name>:<Score>\n...
        // Description: Get the top high scores list
        // Send: HISCORES UNIQUE
        // Receive: HISCORES <Name>:<Score>\n<Name>:<Score>\n...
        // Description: Only include each unique player name for a more varied high score list
        // Send: HISCORES DEFAULT
        // Receive: HISCORES <Name>:<Score>\n<Name>:<Score>\n...
        // Description: Include a default high score list. Good for testing
        // Send: HISCORE <Name>:<Score>
        // Description: Submit a new high score. Do not cheat - we will know!
        // Receive: NEWSCORE <Name>:<Score>
    }

    /**
     * Loads scores from a local text file and stores them in the ScoresScene.localScores property
     */
    public void loadScores() {
        //logger.info("Loading scores from the local text file");
        var inputStream = ScoresScene.class.getResourceAsStream("/scores.txt");

        if (inputStream == null) {
            throw new RuntimeException("File scores.txt not found in the resources directory");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                localScores.add(new Pair(line.split(":")[0], Integer.valueOf(line.split(":")[1])));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows the ScoresList component at the bottom of the screen
     * @param contentVBox VBox to add components to
     * @param imageView image to show on top of the screen
     * @param gameOverHeading label that needs to be displayed
     */
    public void showScoresList(VBox contentVBox, ImageView imageView, Label gameOverHeading) {
        // Local high scores heading
        Label localHighScoresHeading = new Label("Local Scores");
        localHighScoresHeading.getStyleClass().add("heading");

        // Scores list
        ScoresList localScoresList = new ScoresList(localScores);
        localScoresList.scoresProperty().bind(localScores);

        // Online high scores heading
        Label onlineHighScoresHeading = new Label("Online Scores");
        onlineHighScoresHeading.getStyleClass().add("heading");

        // Scores list
        ScoresList onlineScoresList = new ScoresList(remoteScores);
        onlineScoresList.scoresProperty().bind(remoteScores);

        // HBox to hold both lists
        VBox localScoresVBox = new VBox(localHighScoresHeading, localScoresList);
        VBox onlineScoresVBox = new VBox(onlineHighScoresHeading, onlineScoresList);
        HBox localAndRemoteScores = new HBox(localScoresVBox, onlineScoresVBox);

        contentVBox.getChildren().clear();
        contentVBox.getChildren().addAll(imageView, gameOverHeading, localAndRemoteScores);
    }

    /**
     * Write updates scores to the local text file
     */
    public void writeScores() {
        //logger.info("Writing updated scores to the local text file");
        try {
            // Attempt to get the path to the scores file
            Path scoresFilePath = Paths.get(ScoresScene.class.getResource("/scores.txt").toURI());

            // Create the file if it doesn't exist
            if (!Files.exists(scoresFilePath)) {
                Files.write(scoresFilePath, getDefaultScores(), StandardOpenOption.CREATE_NEW);
            }

            // Format scores for writing
            StringBuilder scoresData = new StringBuilder();
            for (Pair<String, Integer> score : localScores.subList(0, Math.min(10, localScores.size()))) {
                scoresData.append(score.getKey()).append(":").append(score.getValue()).append("\n");
            }

            // Write scores to the file
            Files.writeString(scoresFilePath, scoresData.toString(), StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException | URISyntaxException e) {
            logger.error("Error writing scores: ", e);
        }
    }

    /**
     * Default scores list for if there is no score file
     * @return ArrayList of 10 default scores in the form "name:score"
     */
    private ArrayList<String> getDefaultScores() {
        ArrayList<String> defaultScores = new ArrayList<>();
        defaultScores.add("Oliver:2850");
        defaultScores.add("Logan:2250");
        defaultScores.add("Benjamin:2350");
        defaultScores.add("Lucas:2600");
        defaultScores.add("Elijah:1800");
        defaultScores.add("Evelyn:1550");
        defaultScores.add("Nora:1450");
        defaultScores.add("Harper:1150");
        defaultScores.add("Amelia:850");
        defaultScores.add("Avery:500");
        return defaultScores;
    }

    /**
     * Initialise this scene. Called after creation
     */
    @Override
    public void initialise() {
        // Add keyboard listener to the scene
        scene.addEventHandler(
            KeyEvent.KEY_PRESSED,
            event -> {
              switch (event.getCode()) {
                case ESCAPE:
                  gameWindow.startMenu();
                  break;
              }
            });
        }

}
