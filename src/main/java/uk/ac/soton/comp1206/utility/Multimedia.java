package uk.ac.soton.comp1206.utility;

import java.util.HashMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

public class Multimedia {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Handles background music
     */
    private static MediaPlayer musicPlayer;

    /**
     * Handles sound effects
     */
    private static MediaPlayer audioPlayer;

    /**
     * Dictionary containing list of all sound effects and whether the game should try to play
     * them again or not. In the form (FileName, Boolean), with Boolean = false meaning that the
     * game shouldn't try to play the sound effect again.
     */
    private static HashMap<String, Boolean> audioFiles = new HashMap<>();

    /**
     * Tries to play a sound effect. If sound effect cannot be played, stops the game from trying
     * to play the sound effect again using the audioFiles dictionary.
     * @param fileName file name of sound effect to be played
     */
    public static void playAudioFile(String fileName) {
        logger.info("Attempting to play sound effect {}", fileName);
        if (audioFiles.get(fileName) || !audioFiles.containsKey(fileName)) {
            try {
                var filePath = Multimedia.class.getResource("/" + fileName).toExternalForm();
                Media sound = new Media(filePath);
                audioPlayer = new MediaPlayer(sound);
                audioPlayer.play();
                logger.info("Sound effect {} successfully played", fileName);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Unable to play sound: " + fileName);
                logger.error("Disabling sound");
                // disable any other attempts to play that sound
                audioFiles.put(fileName, false);
            }
        }
    }

    /**
     * Plays a background music file in an infinite loop
     * @param fileName file name of background music to be played
     */
    public static void playBackgroundMusic(String fileName) {
        logger.info("Attempting to play background music {}", fileName);
        try {
            var filePath = Multimedia.class.getResource("/music/" + fileName).toExternalForm();
            Media sound = new Media(filePath);
            musicPlayer = new MediaPlayer(sound);
            musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            musicPlayer.play();
            logger.info("Background music {} successfully played", fileName);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Unable to play sound: " + fileName);
        }
    }

    /**
     * Returns the MediaPlayer responsible for playing background music
     * @return background music MediaPlayer
     */
    public static MediaPlayer getMusicPlayer() {
        return musicPlayer;
    }

    /**
     * Returns the MediaPlayer responsible for playing sound effects
     * @return sound effects MediaPlayer
     */
    public static MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }
}
