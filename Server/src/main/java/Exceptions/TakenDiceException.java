package Exceptions;

/**
 * Exception thrown when a player chooses some dices to take that are not takeable 
 * (because the dice is already taken)
 * @author Oriol-Manu
 */
public class TakenDiceException extends Exception {

    /**
     * Constructor with the default message "This dice cannot be taken"
     */
    public TakenDiceException() {
        super("This dice cannot be taken");
    }
}
