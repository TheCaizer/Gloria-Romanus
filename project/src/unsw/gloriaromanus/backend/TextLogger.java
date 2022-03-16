package unsw.gloriaromanus.backend;

/**
 * Observer interface to receive text information about events
 */
public interface TextLogger {
    /**
     * @param String containing event information
     */
    public void onEvent(String description);
}
