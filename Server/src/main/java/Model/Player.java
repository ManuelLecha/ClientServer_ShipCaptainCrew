package Model;

import Exceptions.BetException;

/**
 * Class with player features
 * @author Oriol-Manu
 */
public class Player {

    /**
     * Initial number of gems
     */
    public static final int INITIAL_GEMS = 10;

    /**
     * Bet for every game
     */
    public static final int INITIAL_BET = 1;
    private int gems;
    private final int id;
    private Dices dices;
    private boolean connected;

    /**
     * Constructor for a player
     * @param id player's ID
     */
    public Player(int id) {
        this.gems = INITIAL_GEMS;
        this.id = id;
        this.dices = new Dices();
        connected = false;
    }

    /**
     * @return number of gems
     */
    public int getGems() {
        return gems;
    }

    /**
     * @param gems number of gems bet
     * @throws BetException if player doesn't have enough cash (gems)
     */
    public void bet(int gems) throws BetException {
        if (this.gems - gems < 0) {
            throw new BetException();
        }
        this.gems = this.gems - gems;
    }
    
    /**
     * @param gems gems added to player's cash
     */
    public void winGems (int gems) {
        this.gems = this.gems + gems;
    }

    /**
     * @return player's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Inizialize new dices for the player
     */
    public void giveNewDices(){
        this.dices = new Dices();
    }

    /**
     * @return dice array
     */
    public Dices getDices(){
        return this.dices;
    }

    /**
     * Disconnect the player (a player cannot be playing in several games at a time)
     */
    public void disconnect() {
        connected = false;
    }
    
    /**
     * Connect a player
     */
    public void connect() {
        connected = true;
    }
    
    /**
     * @return true if is connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }
}
