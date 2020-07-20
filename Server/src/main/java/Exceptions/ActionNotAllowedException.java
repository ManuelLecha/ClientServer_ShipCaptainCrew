package Exceptions;

/**
 * Exception thrown if the command sent by the client is not possible according to the game state
 * @author Oriol-Manu
 */
public class ActionNotAllowedException extends Exception {
    
    /**
     * Constructor with the default message "Action not allowed error"
     */
    public ActionNotAllowedException(){
        super("Action not allowed error");
    }

    /**
     * Constructor sending the message provided
     * @param s message to be sent
     */
    public ActionNotAllowedException(String s){
        super(s);
    }
}
