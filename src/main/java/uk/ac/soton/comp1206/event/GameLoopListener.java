package uk.ac.soton.comp1206.event;

/**
 * Used to handle what happens when the game timer is reset. Passes the amount of time left in the
 * new timer.
 */
public interface GameLoopListener {

    /**
     * Handles what happens once the game timer has been reset
     * @param timeLeft how long the new game timer is (in milliseconds)
     */
    public void handle(int timeLeft);
}
