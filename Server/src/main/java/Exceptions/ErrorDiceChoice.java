package Exceptions;

/**
 * Exception thrown when a player chooses some dices to take that are not takeable 
 * (because the dice value is not possible to take according to the current dice state)
 * @author Oriol-Manu
 */
public class ErrorDiceChoice extends Exception {

    /**
     * Exception with the default message "Incorrect dice choice"
     */
    public ErrorDiceChoice() {super("Incorrect dice choice");}
}
