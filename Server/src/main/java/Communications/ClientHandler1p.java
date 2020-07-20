package Communications;

import Exceptions.ActionNotAllowedException;
import Model.GameManager1p;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import utils.Commands;
import utils.Datagram;
import utils.Exceptions.DatagramSyntacticException;
import utils.ParamNames;

/**
 * Handler for clients playing versus the server (have a different game logic
 * from 2 player games)
 * @author Oriol-Manu
 */
public class ClientHandler1p extends ClientHandler {
    
    /**
     * Constructor inheriting from ClientHandler adding the port (needed since the server's ID
     * is its port)
     * @param socket socket for the player
     * @param port server's port
     */
    public ClientHandler1p (Socket socket, int port) {
        super(socket);
        manager = new GameManager1p(port);
    }
    
    /**
     * Main method for any Thread class. It reads the first datagram from the client
     * (that initiates the game) and starts a while-loop ending when the received
     * command is of type EXIT or ERRO and the socket is connected. Inside the loop
     * it sends a list of datagrams obtained from executing the last received datagram and
     * waits for a new datagram. Also, every datagram is written in the log.
     * The catch clauses are in case the connection is no loger available or there is any kind
     * of error with the received datagram or the game logic (in this case, it sends an
     * error datagram).
     * Finally, it closes the stream and the log file and disconnect the player
     */
    @Override
    public void run() {
        try {
            Datagram in = assembler.assembleDatagram();
            file.writeDatagram(in, 1);
            ArrayList<Datagram> out;

            while (in.getType() != Commands.EXIT && sck.isConnected() && in.getType() != Commands.ERRO) {
                out = manager.executeAction(in);
                for (int i = 0; i < out.size(); i++) {
                    disassembler.disassembleDatagram(out.get(i));
                    file.writeDatagram(out.get(i), 0);
                }
                in = assembler.assembleDatagram();
                file.writeDatagram(in, 1);
                if (in.getType() == Commands.ERRO) {
                    manager.executeAction(in);
                }
            }            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        } catch (DatagramSyntacticException | ActionNotAllowedException e) {
            Datagram d = new Datagram.Builder()
                    .withType(Commands.ERRO)
                    .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + e.getMessage())
                    .build();
            try {
                disassembler.disassembleDatagram(d);
                file.writeDatagram(d, 0);
            } catch (IOException ex) {
                System.out.println("Socket not recognized");
            }
        }
        finally {
            try {
                sck.close();
                file.closeFile();
                if (manager.getPlayer() != null) {
                    synchronized(this) {
                        manager.getPlayer().disconnect();
                    }
                }
           } catch (IOException ex1) {
               System.out.println("Socket not recognized");
           }
        }
    }
}
