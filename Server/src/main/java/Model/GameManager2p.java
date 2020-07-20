package Model;

import Exceptions.ActionNotAllowedException;
import Exceptions.BetException;
import Exceptions.ErrorDiceChoice;
import Exceptions.RollLimitOverpassedException;
import Exceptions.TakenDiceException;
import java.util.ArrayList;
import java.util.Random;
import utils.Commands;
import utils.Datagram;
import utils.Exceptions.NextStateNotDefinedException;
import utils.ParamNames;

/**
 * Game logic for 2 player games
 * @author Oriol-Menu
 */
public class GameManager2p extends GameManager {
    
    private Player player2;
    private int playing;
    
    /**
     * Constructor building the HashMap
     */
    public GameManager2p() {
        super();
        actions.put(Commands.TAKE,new TakeAction());
        actions.put(Commands.PASS, new PassAction());
        actions.put(Commands.STRT, new StrtAction());
        actions.put(Commands.BETT, new BettAction());
        first_player = -1;
        player2 = null;
    }
    
    /**
     * @return Secondary player
     */
    public Player getPlayer2() {
        return player2;
    }



    /**
     * Executes the corresponding game action depending on the state, the datagram object received and the player who has send the order
     * @param datagram The datagram object obtained from assembling the socket protocol command received
     * @param player The player identifier
     * @return An array list of datagram responses to a certain action
     * @throws ActionNotAllowedException If the datagram does is not executable at the current state
     */
    public ArrayList<Datagram> executeAction(Datagram datagram, int player) throws ActionNotAllowedException {
        playing = player;
        return super.executeAction(datagram);
    }

    /**
     * Creates a player with the ID given in the Datagram and waits to call this method
     * twice (both players need to send this command) and then send a personalized CASH datagram
     * for every player
     */
    private class StrtAction extends GameManager.StrtAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.WAITING) {
                throw new ActionNotAllowedException("The game is already started");
            }
            
            int id = d.getParams().getIntParameter().get(ParamNames.ID.name());
            ArrayList<Datagram> dg = new ArrayList<>();

            PlayersList pList = PlayersList.getInstance();
            
            if (first_player == -1) {
                synchronized(this) {
                    player1 = pList.getPlayer(id);
                    if (player1.isConnected()) {
                        throw new ActionNotAllowedException("This id is already playing");
                    }
                    player1.connect();
                }
                
                player1.giveNewDices();    
                first_player = 0;
            }
            else {
                synchronized(this) {
                    player2 = pList.getPlayer(id);
                    if (player2.isConnected()) {
                        throw new ActionNotAllowedException("This id is already playing");
                    }
                    player2.connect();
                }
                
                player2.giveNewDices();
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.CASH);
                db.addParam(ParamNames.COINS.name(), player1.getGems());
                db.addParam(ParamNames.RECEIVER.name(), 0);
                dg.add(db.build());
                
                db = new Datagram.Builder();
                db.withType(Commands.CASH);
                db.addParam(ParamNames.COINS.name(), player2.getGems());
                db.addParam(ParamNames.RECEIVER.name(), 1);
                dg.add(db.build());
                state = GameState.STARTED;
                first_player = -1;
            }
            return dg;
        }
    }

    /**
     * Places a bet for the sender player and waits for both player to send this command.
     * Then, it sends a LOOT datagram for both players, a personalized PLAY datagram for each
     * player and a DICE datagram for both (but dices for the first player)
     */
    private class BettAction extends GameManager.BettAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.STARTED) {
                throw new ActionNotAllowedException("Betting not allowed here");
            }
            try {
                ArrayList<Datagram> dg = new ArrayList<>();
                if (first_player == -1) {
                    player1.bet(Player.INITIAL_BET);
                    loot += Player.INITIAL_BET;
                    first_player = 0;
                }
                else {
                    player2.bet(Player.INITIAL_BET);
                    loot += Player.INITIAL_BET;
                       
                    Datagram.Builder db = new Datagram.Builder();
                    db.withType(Commands.LOOT);
                    db.addParam(ParamNames.COINS.name(), loot);
                    db.addParam(ParamNames.RECEIVER.name(), 2);
                    dg.add(db.build());

                    Random r = new Random();
                    first_player = r.nextInt(2);
                    if (first_player == 0) {
                        db = new Datagram.Builder();
                        db.withType(Commands.PLAY);
                        db.addParam(ParamNames.PLAYER.name(), 0);
                        db.addParam(ParamNames.RECEIVER.name(), 0);
                        dg.add(db.build());
                        
                        db = new Datagram.Builder();
                        db.withType(Commands.PLAY);
                        db.addParam(ParamNames.PLAYER.name(), 1);
                        db.addParam(ParamNames.RECEIVER.name(), 1);
                        dg.add(db.build());

                        db = new Datagram.Builder();
                        db.withType(Commands.DICE);
                        db.addParam(ParamNames.ID.name(), player1.getId());
                        player1.getDices().rollDices();
                        db.addParam(ParamNames.DICES.name(), player1.getDices().getIntDices());
                        db.addParam(ParamNames.RECEIVER.name(), 2);
                        dg.add(db.build());
                    }
                    else {
                        db = new Datagram.Builder();
                        db.withType(Commands.PLAY);
                        db.addParam(ParamNames.PLAYER.name(), 1);
                        db.addParam(ParamNames.RECEIVER.name(), 0);
                        dg.add(db.build());
                        
                        db = new Datagram.Builder();
                        db.withType(Commands.PLAY);
                        db.addParam(ParamNames.PLAYER.name(), 0);
                        db.addParam(ParamNames.RECEIVER.name(), 1);
                        dg.add(db.build());

                        db = new Datagram.Builder();
                        db.withType(Commands.DICE);
                        db.addParam(ParamNames.ID.name(), player2.getId());
                        player2.getDices().rollDices();
                        db.addParam(ParamNames.DICES.name(), player2.getDices().getIntDices());
                        db.addParam(ParamNames.RECEIVER.name(), 2);
                        dg.add(db.build());
                    }
                    state = GameState.BET;
                }
                return dg;
            } catch (BetException | RollLimitOverpassedException ex) {
                throw new ActionNotAllowedException(ex.getMessage());
            }
        }
    }
    
    /**
     * Take the given dices for the player playing and sends a DICE datagram (if the game is finished,
     * it also sends all finishing datagrams)
     */
    private class TakeAction extends GameManager.TakeAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.BET && state != GameState.PLAYER1) {
                throw new ActionNotAllowedException("Taking not allowed here");
            }
            if (first_player == playing) {
                Player aux, aux2;
                if (playing == 0) {
                    aux = player1;
                    aux2 = player2;
                }
                else {
                    aux = player2;
                    aux2 = player1;
                }
                try {
                    
                    aux.getDices().takeDices(d.getParams().getArrayParameter().get(ParamNames.DICES.name()), d.getParams().getArrayParameter().get(ParamNames.DICES.name()).size());

                    ArrayList<Datagram> dg = new ArrayList<>();
                    Datagram.Builder db = new Datagram.Builder();
                    db.withType(Commands.DICE);
                    db.addParam(ParamNames.ID.name(), aux.getId());
                    aux.getDices().rollDices();
                    db.addParam(ParamNames.DICES.name(), aux.getDices().getIntDices());
                    db.addParam(ParamNames.RECEIVER.name(), 2);
                    dg.add(db.build());
                    
                    if (!aux.getDices().canRoll()) {
                        if (playing == 0) {
                            first_player = 1;
                        }
                        else {
                            first_player = 0;
                        }
                        if (state == GameState.PLAYER1) {
                            dg.addAll(finishGame());
                        }
                        else {
                            db = new Datagram.Builder();
                            db.withType(Commands.PNTS);
                            db.addParam(ParamNames.ID.name(), aux.getId());
                            db.addParam(ParamNames.POINTS.name(), aux.getDices().getScore());
                            db.addParam(ParamNames.RECEIVER.name(), 2);
                            dg.add(db.build());
                            
                            db = new Datagram.Builder();
                            db.withType(Commands.DICE);
                            db.addParam(ParamNames.ID.name(), aux2.getId());
                            aux2.getDices().rollDices();
                            db.addParam(ParamNames.DICES.name(), aux2.getDices().getIntDices());
                            db.addParam(ParamNames.RECEIVER.name(), 2);
                            dg.add(db.build());
                            state = GameState.PLAYER1;
                        }
                    }

                    return dg;
                } catch (NextStateNotDefinedException | TakenDiceException | ErrorDiceChoice | RollLimitOverpassedException ex) {
                    throw new ActionNotAllowedException(ex.getMessage());
                }
            }
            else {
                throw new ActionNotAllowedException("You cannot play right now");
            }
        }
    }
    
    /**
     * Changes the playing player and finishes the game if applicable
     */
    private class PassAction extends GameManager.PassAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.BET && state != GameState.PLAYER1) {
                throw new ActionNotAllowedException("Taking not allowed here");
            }
            if (first_player == playing) {
                Player aux, aux2;
                if (playing == 0) {
                    aux = player1;
                    aux2 = player2;
                }
                else {
                    aux = player2;
                    aux2 = player1;
                }
                if (aux.getDices().getScore() < 2) {
                    throw new ActionNotAllowedException("Pass not allowed here");
                }
                else {
                    if (state == GameState.BET) {
                        try {
                            ArrayList<Datagram> dg = new ArrayList<>();
                            Datagram.Builder db = new Datagram.Builder();
                            db.withType(Commands.PNTS);
                            db.addParam(ParamNames.ID.name(), aux.getId());
                            db.addParam(ParamNames.POINTS.name(), aux.getDices().getScore());
                            db.addParam(ParamNames.RECEIVER.name(), 2);
                            dg.add(db.build());
                            
                            db.withType(Commands.DICE);
                            db.addParam(ParamNames.ID.name(), aux2.getId());
                            aux.getDices().rollDices();
                            db.addParam(ParamNames.DICES.name(), aux2.getDices().getIntDices());
                            db.addParam(ParamNames.RECEIVER.name(), 2);
                            dg.add(db.build());
                            
                            state = GameState.PLAYER1;
                            return dg;
                        } catch (RollLimitOverpassedException ex) {
                            throw new ActionNotAllowedException(ex.getMessage());
                        }
                    }
                    else {
                        return finishGame();
                    }
                }
            }
            else {
                throw new ActionNotAllowedException("You cannot play right now");
            }
        }
    }
    
    /**
     * Sends the last PNTS datagram and WINS and CASH datagrams (different for each player)
     * @return array of Datagrams to be sent
     */
    private ArrayList<Datagram> finishGame() {
        ArrayList<Datagram> dg = new ArrayList<>();
        
        Player aux;
        if (playing == 0) {
            aux = player1;
        }
        else {
            aux = player2;
        }
        Datagram.Builder db = new Datagram.Builder();
        db.withType(Commands.PNTS);
        db.addParam(ParamNames.ID.name(), aux.getId());
        db.addParam(ParamNames.POINTS.name(), aux.getDices().getScore());
        db.addParam(ParamNames.RECEIVER.name(), 2);
        dg.add(db.build());
        
        if (player1.getDices().getScore() > player2.getDices().getScore()) {
            db = new Datagram.Builder();
            db.withType(Commands.WINS);
            db.addParam(ParamNames.WINNER.name(), 0);
            db.addParam(ParamNames.RECEIVER.name(), 0);
            dg.add(db.build());
            
            db = new Datagram.Builder();
            db.withType(Commands.WINS);
            db.addParam(ParamNames.WINNER.name(), 1);
            db.addParam(ParamNames.RECEIVER.name(), 1);
            dg.add(db.build());
            
            player1.winGems(loot);
            loot = 0;
        }
        else if (player1.getDices().getScore() < player2.getDices().getScore()) {
            db = new Datagram.Builder();
            db.withType(Commands.WINS);
            db.addParam(ParamNames.WINNER.name(), 1);
            db.addParam(ParamNames.RECEIVER.name(), 0);
            dg.add(db.build());
            
            db = new Datagram.Builder();
            db.withType(Commands.WINS);
            db.addParam(ParamNames.WINNER.name(), 0);
            db.addParam(ParamNames.RECEIVER.name(), 1);
            dg.add(db.build());
            
            player2.winGems(loot);
            loot = 0;
        }
        else {
            db = new Datagram.Builder();
            db.withType(Commands.WINS);
            db.addParam(ParamNames.WINNER.name(), 2);
            db.addParam(ParamNames.RECEIVER.name(), 2);
            dg.add(db.build());
        }
        player2.giveNewDices();
        player1.giveNewDices();
        
        db = new Datagram.Builder();
        db.withType(Commands.CASH);
        db.addParam(ParamNames.COINS.name(), player1.getGems());
        db.addParam(ParamNames.RECEIVER.name(), 0);
        dg.add(db.build());

        db = new Datagram.Builder();
        db.withType(Commands.CASH);
        db.addParam(ParamNames.COINS.name(), player2.getGems());
        db.addParam(ParamNames.RECEIVER.name(), 1);
        dg.add(db.build());
        
        this.state = GameState.STARTED;
        first_player = -1;
        
        return dg;
    }
}
