package Model;

import Exceptions.ActionNotAllowedException;
import Exceptions.BetException;
import Exceptions.ErrorDiceChoice;
import utils.Exceptions.NextStateNotDefinedException;
import Exceptions.RollLimitOverpassedException;
import Exceptions.TakenDiceException;
import java.util.ArrayList;
import java.util.Random;
import utils.Commands;
import utils.Datagram;
import utils.ParamNames;

/**
 * Game logic for 1 player games
 * @author Oriol-Manu
 */
public class GameManager1p extends GameManager {

    private final Player server;
    
    /**
     * Constructor building the HashMap
     * @param port server's port used as server's ID
     */
    public GameManager1p(int port) {
        super();
        actions.put(Commands.TAKE,new TakeAction());
        actions.put(Commands.PASS, new PassAction());
        actions.put(Commands.STRT, new StrtAction());
        actions.put(Commands.BETT, new BettAction());
        this.server = new Player(port);
    }

    /**
     * Creates a player with the ID given in the Datagram and send a CASH datagram
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
            synchronized(this) {
                player1 = pList.getPlayer(id);
                if (player1.isConnected()) {
                    Datagram.Builder db = new Datagram.Builder();
                    db.withType(Commands.ERRO);
                    db.addParam(ParamNames.MESSAGE.name(), "This id is already playing");
                    dg.add(db.build());
                    return dg;
                }
                player1.connect();
            }
            player1.giveNewDices();
            
            
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.CASH);
            db.addParam(ParamNames.COINS.name(), player1.getGems());
            dg.add(db.build());
            state = GameState.STARTED;
            
            return dg;
        }
    }

    /**
     * Places a bet and sends a LOOT, PLAY and DICE datagram (and all server's actions
     * if the server is first)
     */
    private class BettAction extends GameManager.BettAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.STARTED) {
                throw new ActionNotAllowedException("Betting not allowed here");
            }
            try {
                player1.bet(Player.INITIAL_BET);
                loot += Player.INITIAL_BET*2;
                
                ArrayList<Datagram> dg = new ArrayList<>();
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.LOOT);
                db.addParam(ParamNames.COINS.name(), loot);
                dg.add(db.build());
                
                Random r = new Random();
                first_player = r.nextInt(2);
                db = new Datagram.Builder();
                db.withType(Commands.PLAY);
                db.addParam(ParamNames.PLAYER.name(), first_player);
                dg.add(db.build());
                
                if (first_player == 1) {
                    dg.addAll(ServerIA.play(server, 0));
                }
                db = new Datagram.Builder();
                db.withType(Commands.DICE);
                db.addParam(ParamNames.ID.name(), player1.getId());
                player1.getDices().rollDices();
                db.addParam(ParamNames.DICES.name(), player1.getDices().getIntDices());
                dg.add(db.build());
                
                state = GameState.BET;
                return dg;
            } catch (BetException | RollLimitOverpassedException ex) {
                throw new ActionNotAllowedException(ex.getMessage());
            }
             
        }
    }
    
    /**
     * Take the given dices in the datagram and sends a DICE datagram (if the game is finished,
     * it also sends all finishing datagrams)
     */
    private class TakeAction extends GameManager.TakeAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (state != GameState.BET) {
                throw new ActionNotAllowedException("Taking not allowed here");
            }
            try {
                player1.getDices().takeDices(d.getParams().getArrayParameter().get(ParamNames.DICES.name()), d.getParams().getArrayParameter().get(ParamNames.DICES.name()).size());
                
                ArrayList<Datagram> dg = new ArrayList<>();
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.DICE);
                db.addParam(ParamNames.ID.name(), player1.getId());
                player1.getDices().rollDices();
                db.addParam(ParamNames.DICES.name(), player1.getDices().getIntDices());
                dg.add(db.build());
                
                if (!player1.getDices().canRoll()) {
                    dg.addAll(finishGame());
                }
                
                return dg;
            } catch (NextStateNotDefinedException | TakenDiceException | ErrorDiceChoice | RollLimitOverpassedException ex) {
                throw new ActionNotAllowedException(ex.getMessage());
            }
        }
    }
    
    /**
     * Finishes the game
     */
    private class PassAction extends GameManager.PassAction {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (player1.getDices().getScore() < 2) {
                throw new ActionNotAllowedException("Pass not allowed here");
            }
            return finishGame();
        }
    }
    
    /**
     * Finishes the game by sending the last player's points and all server's IA actions
     * if it has not played yet. Then, it also sends a WINS and a CASH datagram.
     * @return array of datagrams to be sent
     */
    private ArrayList<Datagram> finishGame() {
        ArrayList<Datagram> dg = new ArrayList<>();
        
        Datagram.Builder db = new Datagram.Builder();
        db.withType(Commands.PNTS);
        db.addParam(ParamNames.ID.name(), player1.getId());
        db.addParam(ParamNames.POINTS.name(), player1.getDices().getScore());
        dg.add(db.build());
        
        if (first_player == 0) {
            dg.addAll(ServerIA.play(server, player1.getDices().getScore()));
        }
        
        int winner;
        if (player1.getDices().getScore() > server.getDices().getScore()) {
            winner = 0;
            player1.winGems(loot);
            loot = 0;
        }
        else if (player1.getDices().getScore() < server.getDices().getScore()) {
            winner = 1;
            loot = 0;
        }
        else {
            winner = 2;
        }
        server.giveNewDices();
        player1.giveNewDices();
        
        db = new Datagram.Builder();
        db.withType(Commands.WINS);
        db.addParam(ParamNames.WINNER.name(), winner);
        dg.add(db.build());
        
        db = new Datagram.Builder();
        db.withType(Commands.CASH);
        db.addParam(ParamNames.COINS.name(), player1.getGems());
        dg.add(db.build());
        
        this.state = GameState.STARTED;
        
        return dg;
    }
}
