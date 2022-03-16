package unsw.gloriaromanus;

/**
 * Interface to store the configurations of different
 * gloria Romanus scenes.
 */
public interface Screen {
    /**
     * 
     * Start this Screen
     */
    public void start();

    /**
     * 
     * @return the Title of the scene
     */
    public String getTitle();

    /**
     * Method to terminate this Screen, disposing of 
     * any neccessary resources.
     */
    public default void terminate(){};

}
