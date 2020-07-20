import Communications.ClientHandler1p;
import Communications.ClientHandler2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main class for server
 * @author Oriol-Manu
 */
public class Server {

    /**
     * Server method to initiate a connection socket to listen from the provided port. If the game
     * is for 1 player, the timeout is set to 60s, if it is for 2 players, the timeout
     * is set to 500ms
     * 
     * @param args -p for server's port (necessary) and -m for number of player (necessary)
     */
    public static void main(String[] args) {

        if (args != null && args.length > 3) {
            if (args[0].equals("-p")) {
                int port = Integer.parseInt(args[1]);
                if (args[2].equals("-m")) {
                    int option = Integer.parseInt(args[3]);
                    if (option == 1) {
                        try {
                            ServerSocket server = new ServerSocket(port);

                            while (true) {
                                Socket socket;
                                socket = server.accept();
                                socket.setSoTimeout(60*1000);
                                Thread client = new ClientHandler1p(socket, port);
                                client.start();
                            }
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                    else if (option == 2){
                        try {
                            ServerSocket server = new ServerSocket(port);

                            while (true) {
                                Socket socket1, socket2;
                                socket1 = server.accept();
                                socket1.setSoTimeout(500);
                                socket2 = server.accept();
                                socket2.setSoTimeout(500);
                                Thread game = new ClientHandler2p(socket1, socket2);
                                game.start();
                            }
                        } catch (IOException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
                else {
                    System.out.println("Number of players not provided");
                }
            }
            else {
                System.out.println("No port provided");
            }
        }
        else if (args != null && args[0].equals("-h")){
            System.out.println("Us: java Server -p <port> -m [1|2]");
        }
        else {
            System.out.println("Not enough arguments");
        }
    }
}