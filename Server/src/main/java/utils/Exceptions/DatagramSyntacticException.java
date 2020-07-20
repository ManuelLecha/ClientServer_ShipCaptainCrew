package utils.Exceptions;

/**
 * Exception thrown if a command is syntactically build badly
 * @author Oriol-Manu
 */
public class DatagramSyntacticException extends Exception {

    /**
     * Constructor with message "Datagram syntactic error"
     */
    public DatagramSyntacticException(){
        super("Datagram syntactic error");
    }

    /**
     * Constructor with message provided
     * @param s message to be thrown
     */
    public DatagramSyntacticException(String s){
        super(s);
    }
}
