package Communications;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import utils.Commands;
import utils.DataAssembler;
import utils.Datagram;
import utils.DicesState;
import utils.Exceptions.NextStateNotDefinedException;
import utils.ParamNames;

/**
 * Artificial Intelligence that plays on behalf of a client
 * @author Oriol-Manu
 */
public class AutomaticClient extends ClientManager{

    /**
     * Maximum natural ID the automatic player can take
     */
    public static int MAX_ID = 100;

    /**
     * Maximum number f games the automatic player can play consecutively
     */
    public static int MAX_GAMES = 50;
    private int games;
    private int points;
    private DicesState state;
    
    /**
     * Constructor creating a Hash table with the private methods depending on the
     * received command send by the server
     * @param socket socket for communication to the server
     */
    public AutomaticClient(Socket socket) {
        super(socket);
        actions.put(Commands.CASH, new CashMenu());
        actions.put(Commands.DICE, new DiceMenu());
        actions.put(Commands.PNTS, new PntsMenu());
        games = 0;
    }
    
    /**
     * After setting up a communcation channel with the server, the client has to
     * start the game by sending a STRT command with the id
     */
    @Override
    public void initGame() {
        Random r = new Random();
        id = r.nextInt(MAX_ID) + 1;
        Datagram d = new Datagram.Builder()
                    .withType(Commands.STRT)
                    .addParam(ParamNames.ID.name(), id)
                    .build();
        
        try {
            disassembler.disassembleDatagram(d);
            gstate = ClientManager.GameState.STARTED;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
    * Handler if the server sends a CASH command. Basically, it checks if the server
    * can send this command and starts a new game if it has enough gems and the maximum
    * number of gems has not been reached
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class CashMenu extends ClientManager.CashMenu {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.STARTED && gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Cash command not expected here");
            }
            System.out.println("Your current cash is " + d.getParams().getIntParameter().get(ParamNames.COINS.name()));            
            ArrayList<Datagram> dg = new ArrayList<>();
            if (games < MAX_GAMES && d.getParams().getIntParameter().get(ParamNames.COINS.name()) > 0) {
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.BETT);
                dg.add(db.build());
                first_player = -1;
                num_rolls = 1;
                games++;
                points = -1;
                state = DicesState.NONE;
            }
            else {
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.EXIT);
                dg.add(db.build());
            }
            gstate = ClientManager.GameState.PLAYING;
            return dg;
        }
        
    }

    /**
    * Handler if the server sends a DICE command. Basically, it checks if the server
    * can send this command and check who is playing. If the player is playing, then
    * the program decides wether to take or pass using the following flow: if it can
    * take some dices, it does this until obtaining all Ship, Captain and Crew; if it
    * can pass before doing so, it passes if is the first and have a score better or equal
    * than 7 or if it is the second and has a score better than the first one
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class DiceMenu extends ClientManager.DiceMenu {
        @Override
        public ArrayList<Datagram> execute(Datagram d) throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.DICE && gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Dice command not expected here");
            }
            switch (first_player) {
                case 0:
                    if (gstate != ClientManager.GameState.DICE) {
                        throw new ActionNotAllowedException("Dice command not expected here");
                    }
                    if (num_rolls < DataAssembler.MAX_ROLLS) {
                        boolean take = true;
                        ArrayList<Integer> dPos = new ArrayList<>();
                        while (take) {
                            take = false;
                            for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                                if (d.getParams().getArrayParameter().get(ParamNames.DICES.name()).get(i) == state.nextValue()) {
                                    take = true;
                                    dPos.add(i+1);
                                    try {
                                        state = state.nextState();
                                    } catch (NextStateNotDefinedException ex) {
                                        break;
                                    }
                                }
                            }
                        }
                        
                        ArrayList<Datagram> dg = new ArrayList<>();
                        if (state == DicesState.CREW) {
                            int pnts = -(DicesState.CAPTAIN.getValue() + DicesState.CREW.getValue() + DicesState.SHIP.getValue());
                            pnts += d.getParams().getArrayParameter().get(ParamNames.DICES.name()).stream().map((dice) -> dice).reduce(pnts, Integer::sum);
                            if ((points != -1 && pnts > points) || (points == -1 && pnts >= 7)) {
                                Datagram.Builder db = new Datagram.Builder();
                                db.withType(Commands.PASS);
                                db.addParam(ParamNames.ID.name(), id);
                                dg.add(db.build());
                                gstate = ClientManager.GameState.PASSED;
                            } 
                            else{
                                Datagram.Builder db = new Datagram.Builder();
                                db.withType(Commands.TAKE);
                                db.addParam(ParamNames.ID.name(), id);
                                db.addParam(ParamNames.DICES.name(), dPos);
                                dg.add(db.build());
                            }
                        }
                        else {
                            Datagram.Builder db = new Datagram.Builder();
                            db.withType(Commands.TAKE);
                            db.addParam(ParamNames.ID.name(), id);
                            db.addParam(ParamNames.DICES.name(), dPos);
                            dg.add(db.build());
                        }
                        num_rolls++;
                        return dg;
                    }
                    else {
                        System.out.print("Final dices: ");
                        for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                            System.out.print(d.getParams().getArrayParameter().get(ParamNames.DICES.name()).get(i) + " ");
                        }
                        System.out.println("");
                        gstate = ClientManager.GameState.PASSED;
                        return null;
                    }
                case 1:
                    return null;
                default:
                    ArrayList<Datagram> dg = new ArrayList<>();
                    Datagram.Builder db = new Datagram.Builder();
                    db.withType(Commands.ERRO);
                    db.addParam(ParamNames.MESSAGE.name(), "Dice command not possible");
                    dg.add(db.build());
                    return dg;
            }
        }
    }
    
    /**
    * Handler if the server sends a PNTS command. Basically, it checks if the server
    * can send this command and prints the punctuation and change the playing player
    * @param d received datagram
    * @return list of datagrams containg the answer of the client
    * @throws ActionNotAllowedException if the action is not allowed
    */
    private class PntsMenu extends ClientManager.PntsMenu {
        @Override
        public ArrayList<Datagram> execute(Datagram d)  throws ActionNotAllowedException {
            if (gstate != ClientManager.GameState.DICE && gstate != ClientManager.GameState.PASSED) {
                throw new ActionNotAllowedException("Pnts command not expected here");
            }
            if(d.getParams().getIntParameter().get(ParamNames.ID.name()) == id) {
                System.out.println("Your punctuation is " + d.getParams().getIntParameter().get(ParamNames.POINTS.name()));
                first_player = 1;
            }
            else {
                System.out.println("Other player's punctuation is " + d.getParams().getIntParameter().get(ParamNames.POINTS.name()));
                first_player = 0;
                points = d.getParams().getIntParameter().get(ParamNames.POINTS.name());
            }
            return null;
        }
    }
}
