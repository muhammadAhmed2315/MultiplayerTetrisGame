package uk.ac.soton.comp1206.event;

/**
 * The Communications Listener is used for handling messages received by the communicator.
 */
public interface CommunicationsListener {

    /**
     * Handles an incoming message received by the Communicator
     * @param communication the message that was received
     */
    public void receiveCommunication(String communication);
}
