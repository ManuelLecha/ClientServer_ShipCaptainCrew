package utils.Exceptions;

/**
 * Exception thrown if last dice state is reached but asked for the next one
 * @author Oriol-Manu
 */
public class NextStateNotDefinedException extends Exception {

    /**
     * Constructor with text "State not defined"
     */
    public NextStateNotDefinedException() {
        super("State not defined");
    }
}
