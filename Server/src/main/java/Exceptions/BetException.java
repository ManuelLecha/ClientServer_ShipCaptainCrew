package Exceptions;

/**
 * Exception thrown when a player is trying to bet but have no cash
 * @author Oriol-Manu
 */
public class BetException extends Exception {

    /**
     * Constructor with default message "You have no cash"
     */
    public BetException(){
        super("You have no cash");
    }
}
