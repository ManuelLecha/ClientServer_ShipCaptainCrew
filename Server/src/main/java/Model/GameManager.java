package Model;

import Exceptions.ActionNotAllowedException;
import java.util.ArrayList;
import utils.Commands;
import utils.Datagram;

import java.util.HashMap;
import utils.ParamNames;

/**
 * Class containing the game logic using selector pattern
 * @author Oriol-Manu
 */
public abstract class GameManager {

    /**
     * HashMap for the selector patters given the command
     */
    protected final HashMap<Commands,GameActionCommand> actions;

    /**
     * State of the game
     */
    protected GameState state;

    /**
     * Loot of the current game
     */
    protected int loot;

    /**
     * Integer for the first player
     */
    protected int first_player;

    /**
     * Main player
     */
    protected Player player1;

    /**
     * Constructor building the hashmap
     */
    public GameManager() {
        this.actions = new HashMap<>();
        this.actions.put(Commands.ERRO, new ErroAction());
        this.state = GameState.WAITING;
        this.loot = 0;
    }

    /**
     * @return current state of the game
     */
    public GameState getState(){
        return this.state;
    }
    
    /**
     * @return main player of the game
     */
    public Player getPlayer(){
        return this.player1;
    }

    /**
     * Interface to be implemented for every command action method
     */
    private interface GameActionCommand{
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    /**
     * Executes the corresponding game action depending on the state and the datagram object received
     * @param datagram The datagram object obtained from assembling the socket protocol command received
     * @return An array list of datagram responses to a certain action
     * @throws ActionNotAllowedException If the datagram does is not executable at the current state
     */
    public ArrayList<Datagram> executeAction(Datagram datagram) throws ActionNotAllowedException {
        return actions.get(datagram.getType()).execute(datagram);
    }

    protected abstract class StrtAction  implements GameActionCommand{

        /**
         * Creates an array of datagram as a response of a STRT command received
         * @param d last datagram received
         * @return array of datagram to be sended to clients
         * @throws ActionNotAllowedException if this command is not possible according to the current state
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    protected abstract class BettAction  implements GameActionCommand{

        /**
         * Creates an array of datagram as a response of a BETT command received
         * @param d last datagram received
         * @return array of datagram to be sended to clients
         * @throws ActionNotAllowedException if this command is not possible according to the current state
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }

    protected abstract class TakeAction  implements GameActionCommand{

        /**
         * Creates an array of datagram as a response of a TAKE command received
         * @param d last datagram received
         * @return array of datagram to be sended to clients
         * @throws ActionNotAllowedException if this command is not possible according to the current state
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }
    
    protected abstract class PassAction  implements GameActionCommand{

        /**
         * Creates an array of datagram as a response of a PASS command received
         * @param d last datagram received
         * @return array of datagram to be sended to clients
         * @throws ActionNotAllowedException if this command is not possible according to the current state
         */
        @Override
        public abstract ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException;
    }
    
    private class ErroAction implements GameActionCommand {
        
        /**
         * Prints the error and returns an emtpy list
         * @param d last datagram received
         * @return array of datagram to be sended to clients
         * @throws ActionNotAllowedException if this command is not possible according to the current state
         */
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            System.out.println(d.getParams().getStringParameter().get(ParamNames.MESSAGE.name()));
            return new ArrayList<>();
        }
    }
}
