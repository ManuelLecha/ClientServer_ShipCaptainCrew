package Exceptions;

/**
 * Exception thrown when a player tries to roll dices when the number of rolls
 * performed are more or equal than the number of rolls possible in a game
 * @author Oriol-Manu
 */
public class RollLimitOverpassedException extends Exception {

    /**
     * Constructor with the default message "No more rolls allowed"
     */
    public RollLimitOverpassedException() {
        super("No more rolls allowed");
    }
}
