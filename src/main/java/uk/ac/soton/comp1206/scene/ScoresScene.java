package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
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
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
import uk.ac.soton.comp1206.utility.Multimedia;

import java.io.*;
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
     * Decides whether to prompt the user at the beginning of the screen for their name. If user got a top 10
     * local high score, set to true, else false.
     */
    private boolean showInputUsernameScreen = false;

    /**
     * Holds a list of scores retrieved from the server
     */
    private SimpleListProperty<Pair<String, Integer>> remoteScores;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        //logger.info("Creating Scores Scene");
        Multimedia.switchBackgroundMusic("end.wav");
    }

    /**
     * Build the layout of the scene
     */
    @Override
    public void build() {
        //logger.info("Building " + this.getClass().getName());

        ArrayList<Pair<String, Integer>> localScoresArrayList = new ArrayList<>();
        ArrayList<Pair<String, Integer>> remoteScoresArrayList = new ArrayList<>();

        ObservableList<Pair<String, Integer>> observableLocalScores = FXCollections.observableArrayList(localScoresArrayList);
        ObservableList<Pair<String, Integer>> observableRemoteScores = FXCollections.observableArrayList(remoteScoresArrayList);

        localScores = new SimpleListProperty<>(observableLocalScores);
        remoteScores = new SimpleListProperty<>(observableRemoteScores);

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var contentVBox = new VBox();
        contentVBox.setAlignment(Pos.TOP_CENTER);
        contentVBox.setSpacing(20);
        contentVBox.setPadding(new Insets(10, 10, 10, 10));
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

        loadLocalScores();
        loadOnlineScores();

        int counter = 0;
        for (Pair<String, Integer> score : localScores) {
            if (score.getValue() < game.getUserScore().intValue()) {
                showInputUsernameScreen = true;
                break;
            }
            counter++;
        }

        if (showInputUsernameScreen) {
            contentVBox.getChildren().clear();
            TextField inputUsername = new TextField();
            inputUsername.setPromptText("Enter your name");
            Button submitButton = new Button("Submit");

            int finalCounter = counter;

            submitButton.setOnMouseClicked((event) -> {
                if (!inputUsername.getText().trim().equals("")) {
                    Multimedia.switchAudioFile("pling.wav");

                    // update localScores
                    localScores.add(finalCounter, new Pair(inputUsername.getText(), game.getUserScore().intValue()));

                    // update scores file
                    writeLocalScores();

                    // switch scenes
                    showInputUsernameScreen = false;
                    showScoresList(contentVBox, imageView, gameOverHeading);
                }
            });

            inputUsername.setOnAction((event) -> {
                if (!inputUsername.getText().trim().equals("")) {
                    Multimedia.switchAudioFile("pling.wav");
                    // update localScores
                    localScores.add(finalCounter, new Pair(inputUsername.getText(), game.getUserScore().intValue()));

                    // update scores file
                    writeLocalScores();

                    // switch scenes
                    showInputUsernameScreen = false;
                    showScoresList(contentVBox, imageView, gameOverHeading);
                }
            });

            contentVBox.getChildren().addAll(imageView, gameOverHeading, inputUsername, submitButton);
        } else {
            showScoresList(contentVBox, imageView, gameOverHeading);
        }

    }

    /**
     * Request online high scores from the server and store them in ScoresScene.remoteScores
     */
    private void loadOnlineScores() {
        gameWindow.getCommunicator().addListener((message) -> {
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
        gameWindow.getCommunicator().send("HISCORES");
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
    public void loadLocalScores() {
        logger.info("Loading scores from the local text file");
        try {
            // Get the directory path where the jar file is located or current directory if run from code
            String dirPath = ChallengeScene.getJarDirectory();
            String filePath = dirPath + File.separator + "scores.txt";

            File scoresFile = new File(filePath);
            try (BufferedReader reader = new BufferedReader(new FileReader(scoresFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    localScores.add(new Pair(line.split(":")[0], Integer.valueOf(line.split(":")[1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write updates scores to the local text file
     */
    public void writeLocalScores() {
        //logger.info("Writing updated scores to the local text file");
        try {
            // Get the directory path where the jar file is located or current directory if run from code
            String dirPath = ChallengeScene.getJarDirectory();
            String filePath = dirPath + File.separator + "scores.txt";

            File scoresFile = new File(filePath);

            StringBuilder scoresData = new StringBuilder();
            for (Pair<String, Integer> score : localScores.subList(0, 10)) {
                scoresData.append(score.getKey()).append(":").append(score.getValue()).append("\n");
            }

            try (FileWriter writer = new FileWriter(scoresFile, false)) {
                writer.write(String.valueOf(scoresData));
            }
        } catch (IOException e) {
            logger.error("Error writing scores: ", e);
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
        localScoresVBox.setAlignment(Pos.TOP_CENTER);
        VBox onlineScoresVBox = new VBox(onlineHighScoresHeading, onlineScoresList);
        onlineScoresVBox.setAlignment(Pos.TOP_CENTER);
        HBox localAndRemoteScores = new HBox(localScoresVBox, onlineScoresVBox);
        localAndRemoteScores.setAlignment(Pos.TOP_CENTER);

        contentVBox.getChildren().clear();
        contentVBox.getChildren().addAll(imageView, gameOverHeading, localAndRemoteScores);
        contentVBox.setAlignment(Pos.CENTER);
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
