package uk.ac.soton.comp1206.event;

/**
 * Used to handle the event when the game timer is reset. Passes the amount of time left in the
 * new timer.
 */
public interface GameLoopListener {

    /**
     * Handle the event that the game timer has been reset
     * @param timeLeft how long the new game timer is (in milliseconds)
     */
    public void handle(int timeLeft);
}
