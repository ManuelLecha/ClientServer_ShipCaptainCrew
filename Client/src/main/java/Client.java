import Communications.AutomaticClient;
import Communications.ClientManager;
import Communications.TerminalClient;
import java.io.IOException;
import java.net.Socket;

/**
 * Main class for Client
 * @author Manu-Oriol
 */
public class Client {

    /**
     * Client method to initiate a socket to the specified server with 60 seconds of timeout
     * 
     * @param args -s for server's IP (necessary), -p for server's port (necessary)
     *              and -i for automatic or terminal client (terminal predetermined)
     */
    public static void main(String[] args) {
                
        if (args != null && args.length > 3) {
            if (args[0].equals("-s")) {
                String address = args[1];
                if (args[2].equals("-p")) {
                    int port = Integer.parseInt(args[3]);
                    int terminal = 0;
                    if (args.length > 5 && args[4].equals("-i")) {
                        terminal = Integer.parseInt(args[5]);
                    }
                    boolean exit = false;

                    try {
                        Socket socket = new Socket(address, port);
                        socket.setSoTimeout(60*1000);
                        ClientManager ter;
                        if (terminal == 0) {
                            ter = new TerminalClient(socket);
                        }
                        else {
                            ter = new AutomaticClient(socket);
                        }
                        
                        ter.initGame();
                        while (!exit && socket.isConnected()) {
                            exit = ter.menu();
                        }
                        socket.close();
                    }
                    catch (IOException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                else {
                    System.out.println("No port provided");
                }
            }
            else {
                System.out.println("No address provided");
            }
        }
        else if (args != null && args[0].equals("-h")){
            System.out.println("Us: java Client -s <maquina_servidora> -p <port> [-i 0|1|2]");
        }
        else {
            System.out.println("Not enough arguments");
        }
    }
}