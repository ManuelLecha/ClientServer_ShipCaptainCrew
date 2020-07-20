package Communications;

import Exceptions.ActionNotAllowedException;
import Model.GameManager2p;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import utils.ComUtils;
import utils.Commands;
import utils.DataAssembler;
import utils.DataDisassembler;
import utils.Datagram;
import utils.Exceptions.DatagramSyntacticException;
import utils.ParamNames;

/**
 * Handler for clients playing versus other players (have a different game logic
 * from 1 player games that share 1 client handler instance)
 * @author Oriol-Manu
 */
public class ClientHandler2p extends ClientHandler {

    private DataAssembler assembler2;
    private DataDisassembler disassembler2;
    private Socket sck2;
    
    /**
     * Constructor inheriting from ClientHandler and adding a new socket for the second player
     * @param socket1 socket for the first player
     * @param socket2 socket for the second player
     */
    public ClientHandler2p (Socket socket1, Socket socket2) {
        super(socket1);
        try {
            sck2 = socket2;
            ComUtils com2 = new ComUtils(socket2);
            assembler2 = new DataAssembler(com2);
            disassembler2 = new DataDisassembler(com2);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        manager = new GameManager2p();
    }
    
    /**
     * Main method for any Thread class. Since the timeout is shared for 2 players,
     * we need to implement a timeout by counting the number of read timeouts for both
     * sockets at one. It reads the first datagram from both clients (that initiates the game) 
     * and starts a while-loop ending when any received command is of type EXIT or ERRO, 
     * both sockets are connected and shared timeout is not reached. Inside the loop,
     * it sends a list of datagrams obtained from executing the last received datagram 
     * from any client, taking into account that it may send different packages to every
     * client, and waits for a new datagram from any client. Also, every datagram is written in the log.
     * Also, it doesn't read any datagram if there is an untreated datagram provided
     * from any client and send an error datagram if one client leaf or make an error.
     * The catch clauses are in case the connection is no loger available or there is any kind
     * of error with the received datagram or the game logic (in this case, it sends an
     * error datagram to both clients).
     * Finally, it closes the streams and the log file and disconnect the player
     */
    @Override
    public void run() {
        try {
            int status = 0;
            int timeout = 0;
            Datagram in1 = null, in2 = null;
            while (status < 2 && timeout < MAX_TIMEOUT*2) {
                try {
                    try {
                        in1 = assembler.assembleDatagram();
                        status++;
                    }
                    catch (SocketTimeoutException ex){
                        timeout++;
                    }
                    try {
                        in2 = assembler2.assembleDatagram();
                        status++;
                    }
                    catch (SocketTimeoutException ex){
                        timeout++;
                    }
                }
                catch (DatagramSyntacticException e) {
                    Datagram d = new Datagram.Builder()
                            .withType(Commands.ERRO)
                            .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + e.getMessage())
                            .build();
                    disassembler.disassembleDatagram(d);
                    disassembler2.disassembleDatagram(d);
                    file.writeDatagram(d, 0);
                }
            }
            
            if (timeout < MAX_TIMEOUT*2) {
                file.writeDatagram(in1, 2);
                file.writeDatagram(in2, 3);
                ArrayList<Datagram> out;
                timeout = 0;
                Datagram error;
                
                while (((in1 != null && in1.getType() != Commands.EXIT) || (in2 != null && in2.getType() != Commands.EXIT)) &&
                        ((in1 != null && in1.getType() != Commands.ERRO) || (in2 != null && in2.getType() != Commands.ERRO)) &&
                        sck.isConnected() && sck2.isConnected() && timeout < MAX_TIMEOUT) {
                    try {
                        if (in1 != null) {
                            out = ((GameManager2p)manager).executeAction(in1, 0);
                            in1 = null;
                        }
                        else {
                            out = ((GameManager2p)manager).executeAction(in2, 1);
                            in2 = null;
                        }
                        for (int i = 0; i < out.size(); i++) {
                            switch (out.get(i).getParams().getIntParameter().get(ParamNames.RECEIVER.name())) {
                                case 0:
                                    disassembler.disassembleDatagram(out.get(i));
                                    break;
                                case 1:
                                    disassembler2.disassembleDatagram(out.get(i));
                                    break;
                                default:
                                    disassembler.disassembleDatagram(out.get(i));
                                    disassembler2.disassembleDatagram(out.get(i));
                                    break;
                            }
                            file.writeDatagram(out.get(i), 0);
                        }
                        if (in1 == null && in2 == null) {
                            timeout = 0;
                            boolean started = false;
                            while (!started && timeout < MAX_TIMEOUT) {
                                try {
                                    in1 = assembler.assembleDatagram();
                                    started = true;
                                    file.writeDatagram(in1, 2);
                                }
                                catch (SocketTimeoutException ex){
                                    timeout++;
                                }
                                try {
                                    if (!started) {
                                        in2 = assembler2.assembleDatagram();
                                        started = true;
                                        file.writeDatagram(in2, 3);
                                    }
                                }
                                catch (SocketTimeoutException ex){
                                    timeout++;
                                }
                            }
                        }
                        if (in1 != null && in1.getType() == Commands.ERRO) {
                            manager.executeAction(in1);
                        }
                        if (in2 != null && in2.getType() == Commands.ERRO) {
                            manager.executeAction(in2);
                        }
                    }
                    catch (DatagramSyntacticException | ActionNotAllowedException e) {
                        Datagram d = new Datagram.Builder()
                                .withType(Commands.ERRO)
                                .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + e.getMessage())
                                .build();
                        disassembler.disassembleDatagram(d);
                        disassembler2.disassembleDatagram(d);
                        file.writeDatagram(d, 0);
                    }
                }
                if (timeout >= MAX_TIMEOUT) {
                    Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "Timeout problem")
                        .build();
                disassembler.disassembleDatagram(d);
                disassembler2.disassembleDatagram(d);
                file.writeDatagram(d, 0);
                }
                else if (in1 != null && in1.getType() == Commands.EXIT) {
                    Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "The opponent has disconnected")
                        .build();
                    disassembler2.disassembleDatagram(d);
                    file.writeDatagram(d, 0);
                }
                else if (in2 != null && in2.getType() == Commands.EXIT) {
                    Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "The opponent has disconnected")
                        .build();
                    disassembler.disassembleDatagram(d);
                    file.writeDatagram(d, 0);
                }
                else if (in1 != null && in1.getType() == Commands.ERRO) {
                    Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "The opponent has disconnected")
                        .build();
                    disassembler2.disassembleDatagram(d);
                    file.writeDatagram(d, 0);
                }
                else if (in2 != null && in2.getType() == Commands.ERRO) {
                    Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "The opponent has disconnected")
                        .build();
                    disassembler.disassembleDatagram(d);
                    file.writeDatagram(d, 0);
                }
            }
            else {
                Datagram d = new Datagram.Builder()
                        .withType(Commands.ERRO)
                        .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + "Timeout problem")
                        .build();
                disassembler.disassembleDatagram(d);
                disassembler2.disassembleDatagram(d);
                file.writeDatagram(d, 0);
            }
        }
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally {
            try {
                sck.close();
                sck2.close();
                file.closeFile();
                if (manager.getPlayer() != null) {
                    synchronized(this) {
                        manager.getPlayer().disconnect();
                    }
                }
                if (((GameManager2p)manager).getPlayer2() != null) {
                    synchronized(this) {
                        ((GameManager2p)manager).getPlayer2().disconnect();
                    }
                }
           } catch (IOException ex1) {
               System.out.println("Socket not recognized");
           }
        }
    }
}
