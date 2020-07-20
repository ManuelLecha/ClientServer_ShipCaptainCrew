package Communications;


import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import utils.Commands;
import utils.DataAssembler;
import utils.Datagram;
import java.util.Scanner;
import utils.ParamNames;

/**
 * Usual client playing on terminal
 * @author Oriol-Manu
 */
public class TerminalClient extends ClientManager{

    private final Scanner sc;
    
    /**
     * Constructor creating a Hash table with the private methods depending on the
     * received command send by the server
     * @param socket socket for communication to the server
     */
    public TerminalClient(Socket socket) {
        super(socket);
        actions.put(Commands.CASH, new CashMenu());
        actions.put(Commands.DICE, new DiceMenu());
        actions.put(Commands.PNTS, new PntsMenu());
        sc = new Scanner(System.in);
    }
    
    /**
     * After setting up a communcation channel with the server, the client has to
     * start the game by sending a STRT command with the id. This id is asked using terminal
     */
    @Override
    public void initGame() {
        System.out.println("Enter your integer id: ");
        
        boolean int_id = false;
        id = 0;
        while (!int_id) {
            try {
                id = nextInt();
                int_id = true;
            }
            catch (NumberFormatException ex) {
                System.out.println("Invalid, try again");
            }
            
        }
        
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
    * Modularize code and skip enter character when asking for integers
    */
    private int nextInt() throws NumberFormatException {
        String txt = sc.nextLine();
        return Integer.parseInt(txt);
    }

    /**
    * Handler if the server sends a CASH command. Basically, it checks if the server
    * can send this command and starts a new game if the client say so
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
            System.out.println("Do you want a play another game [Yes|No]");
            
            boolean valid = false;
            String txt = "No";
            while (!valid) {
                txt = sc.nextLine();
                if (txt.equals("No") || txt.equals("Yes")) {
                    valid = true;
                }
                else {
                    System.out.println("Invalid answer, try again.");
                }
            }
            
            ArrayList<Datagram> dg = new ArrayList<>();
            if (txt.equals("Yes")) {
                Datagram.Builder db = new Datagram.Builder();
                db.withType(Commands.BETT);
                dg.add(db.build());
                first_player = -1;
                num_rolls = 1;
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
    * can send this command and check who is playing. If the player is playing, then he
    * can decide which dices to take and when stop playing
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
                        System.out.print("You got: ");
                        for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                            System.out.print(d.getParams().getArrayParameter().get(ParamNames.DICES.name()).get(i) + " ");
                        }
                        
                        System.out.println("");
                        System.out.println("Do you want to pass? [Yes|No]");
                        boolean valid = false;
                        String txt = "No";
                        while (!valid) {
                            txt = sc.nextLine();
                            if (txt.equals("No") || txt.equals("Yes")) {
                                valid = true;
                            }
                            else {
                                System.out.println("Invalid answer, try again.");
                            }
                        }
                        
                        ArrayList<Datagram> dg = new ArrayList<>();
                        if (txt.equals("Yes")) {
                            Datagram.Builder db = new Datagram.Builder();
                            db.withType(Commands.PASS);
                            db.addParam(ParamNames.ID.name(), id);
                            dg.add(db.build());
                            gstate = ClientManager.GameState.PASSED;
                        }
                        else {
                            System.out.println("How many dices do you want to take? [0|1|2|3]");
                            valid = false;
                            int size = 0;
                            while (!valid) {
                                try {
                                    size = nextInt();
                                    if (size >= 0 && size < 4) {
                                        valid = true;
                                    }
                                    else {
                                        System.out.println("Invalid, try again");
                                    }
                                }
                                catch (NumberFormatException ex) {
                                    System.out.println("Invalid, try again");
                                }
                            }
                            
                            int pos;
                            ArrayList<Integer> arrInt = new ArrayList<>();
                            for (int i = 0; i < size; i++) {
                                System.out.println("Enter dice position");
                                valid = false;
                                while (!valid) {
                                    try {
                                        pos = nextInt();
                                        if (pos > 0 && pos < 6) {
                                            arrInt.add(pos);
                                            valid = true;
                                        }
                                        else {
                                            System.out.println("Invalid, try again");
                                        }
                                    }
                                    catch (NumberFormatException ex) {
                                        System.out.println("Invalid, try again");
                                    }
                                }
                            }
                            
                            Datagram.Builder db = new Datagram.Builder();
                            db.withType(Commands.TAKE);
                            db.addParam(ParamNames.ID.name(), id);
                            db.addParam(ParamNames.DICES.name(), arrInt);
                            dg.add(db.build());
                        }
                        num_rolls++;
                        return dg;
                    }
                    else {
                        System.out.print("You got: ");
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
            }
            return null;
        }
    }    
}
