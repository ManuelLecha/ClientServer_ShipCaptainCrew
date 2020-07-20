package Model;

import utils.DicesState;
import Exceptions.ErrorDiceChoice;
import utils.Exceptions.NextStateNotDefinedException;
import Exceptions.RollLimitOverpassedException;
import Exceptions.TakenDiceException;
import java.util.ArrayList;
import utils.Commands;
import utils.DataAssembler;
import utils.Datagram;
import utils.ParamNames;

/**
 * Artificial Intelligence for the server when playing as a player
 * @author Oriol-Manu
 */


public class ServerIA {
    
    /**
     * Rolls the dices every time is allowed (this is a usual server's action)
     * @param server player representing the server
     * @param points number of points the other player has scored, 0 if server is
     *               the first player
     * @return array of Datagrams with all commands for player playing as a player and
     *         server's actions interspersed
     */
    public static ArrayList<Datagram> play(Player server, int points) {
        ArrayList<Datagram> dg = new ArrayList<>();
        Datagram.Builder db;
        
        try {
            boolean pass = false;
            while(server.getDices().canRoll() && !pass) {
                db = new Datagram.Builder();
                db.withType(Commands.DICE);
                db.addParam(ParamNames.ID.name(), server.getId());
                server.getDices().rollDices();
                db.addParam(ParamNames.DICES.name(), server.getDices().getIntDices());
                dg.add(db.build());
                
                pass = takeDices(server, dg, points);
            }
            
        } catch (RollLimitOverpassedException ex) {
        }
        finally {
            db = new Datagram.Builder();
            db.withType(Commands.PNTS);
            db.addParam(ParamNames.ID.name(), server.getId());
            db.addParam(ParamNames.POINTS.name(), server.getDices().getScore());
            dg.add(db.build());
        }
        return dg;
    }
    
    /**
     * Server playing as a player intelligence. Algorythm: takes all dices he can
     * (depending on the state and values rolled) and calculates the score. If the
     * score is bigger than the opponents's score, then pass (if server is second player);
     * else pass when score bigger than 7 (if server is first player)
     * @param player server as a player
     * @param dg array where the datagrams are added
     * @param points points scored by the opponent, 0 if server is first player
     * @return true if server wants to pass, else otherwise
     */
    private static boolean takeDices(Player player, ArrayList<Datagram> dg, int points) {
        DicesState state = player.getDices().getState();
        
        ArrayList<Integer> arrDices = new ArrayList<>();
        if (state != DicesState.CREW) {
            boolean take = true;
            while (take) {
                take = false;
                for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                    if (player.getDices().getDice(i).getValue() == state.nextValue()) {
                        take = true;
                        arrDices.add(i+1);
                        try {
                            state = state.nextState();
                        } catch (NextStateNotDefinedException ex) {
                            break;
                        }
                    }
                }
            }
            try {
                player.getDices().takeDices(arrDices, arrDices.size());
            } catch (NextStateNotDefinedException | TakenDiceException | ErrorDiceChoice ex) {
                System.out.println("Server as a player has done something bad");
            }
        }
        if (!player.getDices().canRoll()) {
            return false;
        }
        else if ((points == 0 && player.getDices().getScore() > 7) ||
                (points > 0 && player.getDices().getScore() > points)) {
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.PASS);
            db.addParam(ParamNames.ID.name(), player.getId());
            dg.add(db.build());
            return true;
        }
        else {
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.TAKE);
            db.addParam(ParamNames.ID.name(), player.getId());
            db.addParam(ParamNames.DICES.name(), arrDices);
            dg.add(db.build());
            return false;
        }
    }
}