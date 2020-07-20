package Communications;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import utils.ComUtils;
import utils.Commands;
import utils.DataAssembler;
import utils.DataDisassembler;
import utils.Datagram;
import utils.Exceptions.DatagramSyntacticException;
import utils.ParamNames;

/**
 * Manager of client. Every kind of client must extend this class
 * @author Oriol-Manu
 */
public abstract class ClientManager {
    
    /**
     * Reader of datagrams coming from the server
     */
    protected DataAssembler assembler;

    /**
     * Sender of datagrams to the server
     */
    protected DataDisassembler disassembler;

    /**
     * hash table to select which execute action to do depending on the received datagram
     */
    protected final HashMap<Commands, MenuCommand> actions;

    /**
     * Stores who is the first player (send by the server)
     */
    protected int first_player;

    /**
     * stores the id of the client
     */
    protected int id;

    /**
     * stores the number of rolls performed
     */
    protected int num_rolls;

    /**
     * stores the state of game to check if the server can send an specific command
     */
    protected GameState gstate;
    
    /**
     * States the game can be
     */
    protected enum GameState {

        /**
         * Waiting to start a new game. Default state
         */
        WAITING,

        /**
         * STRT command has been sent. Client inizialized to the server
         */
        STARTED,

        /**
         * BETT command has been send. Client willing to play another game
         */
        PLAYING,

        /**
         * Client is playing.
         */
        DICE,

        /**
         * Client has passed and is waiting to check who is the winner.
         */
        PASSED
    }
    
    /**
     * Constructor creating a Hash table with the private methods depending on the
     * received command send by the server. Also, the channels for communication with
     * the server are inizialized
     * @param socket socket for communication to the server
     */
    public ClientManager(Socket socket) {
        this.actions = new HashMap<>();
        actions.put(Commands.LOOT, new LootMenu());
        actions.put(Commands.PLAY, new PlayMenu());
        actions.put(Commands.TAKE, new TakeMenu());
        actions.put(Commands.PASS, new PassMenu());
        actions.put(Commands.WINS, new WinsMenu());
        actions.put(Commands.ERRO, new ErroMenu());
        
        try {
            ComUtils com = new ComUtils(socket);
            assembler =  new DataAssembler(com);
            disassembler =  new DataDisassembler(com);
            gstate = ClientManager.GameState.WAITING;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Method to send the first command by the client
     */
    public abstract void initGame();
    
    /**
     * Main method. This method is called every iteration for the main loop, and waits
     * for information to receive and sends the answer. If there is some error, it sends
     * an EXIT command.
     * @return true if an EXIT command is sent and false otherwise
     */
    public boolean menu() {
        try {
            Datagram aux = assembler.assembleDatagram();
            ArrayList<Datagram> arrD = actions.get(aux.getType()).execute(aux);
            
            boolean exit = false;
            if (arrD != null) {
                for (Datagram d_aux : arrD) {
                    disassembler.disassembleDatagram(d_aux);
                    exit = (d_aux.getType() == Commands.EXIT);
                }
            }
            return exit;
        } catch (IOException ex1) {
            System.out.println(ex1.getMessage());
            return true;
        } catch (DatagramSyntacticException | ActionNotAllowedException ex2) {
            try {
                Datagram d = new Datagram.Builder()
                    .withType(Commands.ERRO)
                    .addParam(ParamNames.MESSAGE.name(), "Protocol error:" + ex2.getMessage())
                    .build();
                disassembler.disassembleDatagram(d);
                
                d = new Datagram.Builder()
                    .withType(Commands.EXIT)
                    .build();
                disassembler.disassembleDatagram(d);
                return true;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return true;
            }
        }
    }
    
    /**
    * Interface for handlers depending on the received datagram.
    */
    private interface MenuCommand{
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    /**
     * Handler for CASH command to be implemented
     */
    protected abstract class CashMenu implements MenuCommand {

        /**
         * Handler method to be implemented
         * @param d received datagram
         * @return list of datagrams containg the answer of the client
         * @throws ActionNotAllowedException if the action is not allowed
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    /**
    * Handler if the server sends a LOOT command. Basically, it checks if the server
    * can send this command and prints the loot of the game
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class LootMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.PLAYING) {
                throw new ActionNotAllowedException("Loot command not expected here");
            }
            System.out.println("The current loot of the game is " + d.getParams().getIntParameter().get(ParamNames.COINS.name()));
            return null;
        }
    }

    /**
    * Handler if the server sends a PLAY command. Basically, it checks if the server
    * can send this command and prints the loot of the game and sets the first player
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class PlayMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.PLAYING) {
                throw new ActionNotAllowedException("Play command not expected here");
            }
            System.out.print("The first player is ");
            if (d.getParams().getIntParameter().get(ParamNames.PLAYER.name()) == 0) {
                System.out.println("you");
                first_player = 0;
            }
            else {
                System.out.println("other player");
                first_player = 1;
            }
            gstate = GameState.DICE;
            return null;
        }
    }

    /**
     * Handler for DICE command to be implemented
     */
    protected abstract class DiceMenu implements MenuCommand {

        /**
         * Handler method to be implemented
         * @param d received datagram
         * @return list of datagrams containg the answer of the client
         * @throws ActionNotAllowedException if the action is not allowed
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    /**
    * Handler if the server sends a TAKE command. It does nothing since this command
    * indicates that the other player is playing
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class TakeMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.DICE && gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Dice command not expected here");
            }
            return null;
        }
    }

    /**
    * Handler if the server sends a PASS command. It does nothing since this command
    * indicates that the other player is playing
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class PassMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.DICE && gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Dice command not expected here");
            }
            return null;
        }
    }

    /**
     * Handler for PNTS command to be implemented
     */
    protected abstract class PntsMenu implements MenuCommand {

        /**
         * Handler method to be implemented
         * @param d received datagram
         * @return list of datagrams containg the answer of the client
         * @throws ActionNotAllowedException if the action is not allowed
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    /**
    * Handler if the server sends a WIN command. It prints the winner
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class WinsMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Dice command not expected here");
            }
            switch (d.getParams().getIntParameter().get(ParamNames.WINNER.name())) {
                case 0:
                    System.out.println("You have won");
                    break;
                case 1:
                    System.out.println("Server has won");
                    break;
                default:
                    System.out.println("you have tied");
                    break;
            }
            return null;
        }
    }

    /**
    * Handler if the server sends a ERRO command. It prints the error and sends an
    * EXIT command.
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class ErroMenu implements MenuCommand {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            System.out.println(d.getParams().getStringParameter().get(ParamNames.MESSAGE.name()));
            Datagram data = new Datagram.Builder()
                .withType(Commands.EXIT)
                .build();
            ArrayList<Datagram> arrD = new ArrayList<>();
            arrD.add(data);
            return arrD;
        }
    }   
}