package Communications;

/**
 * Exception thrown when the action send by the server is not allowed by game logic
 * @author Oriol-Manu
 */
public class ActionNotAllowedException extends Exception {
    
    /**
     * Constructor throwing always the message "Action not allowed error"
     */
    public ActionNotAllowedException(){
        super("Action not allowed error");
    }

    /**
     * Constructor throwing the given message
     * @param s message to be shown
     */
    public ActionNotAllowedException(String s){
        super(s);
    }
}
