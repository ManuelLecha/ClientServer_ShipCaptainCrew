package Communications;

import Model.GameManager;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import utils.ComUtils;
import utils.DataAssembler;
import utils.DataDisassembler;

/**
 * Class to handle every client extending from Thread. Every type of client handler
 * (depending on the type of game) have to extend this class
 * @author Oriol-Manu
 */
public abstract class ClientHandler extends Thread {

    /**
     * Maximum number of timeouts for 2-player games
     */
    public static final int MAX_TIMEOUT = 120;

    /**
     * Datagram assembler for the client's socket stream
     */
    protected DataAssembler assembler;

    /**
     * Datagram disassembler for the client's socket stream
     */
    protected DataDisassembler disassembler;

    /**
     * Controller of the game (logic)
     */
    protected GameManager manager;

    /**
     * Socket with the stream between client and server
     */
    protected Socket sck;

    /**
     * Class to write the log of every datagram received or sent
     */
    protected LogWriter file;

    /**
     * Constructor instatiating all common objects for every client handler
     * @param socket socket containing the stream
     */
    public ClientHandler(Socket socket) {
        try {
            sck = socket;
            ComUtils com = new ComUtils(socket);
            assembler = new DataAssembler(com);
            disassembler = new DataDisassembler(com);
            file = new LogWriter(new File("Server"+getName()+".log"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
