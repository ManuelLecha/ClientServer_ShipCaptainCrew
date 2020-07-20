package Model;

/**
 * Possible states the game can be
 * @author Oriol-Manu
 */
public enum GameState {

    /**
     * When the game has not started yet
     */
    WAITING,

    /**
     * Both player have sent a STRT command
     */
    STARTED,

    /**
     * Both players have sent a BETT command
     */
    BET,

    /**
     * Second player is playing (just used in 2 player games)
     */
    PLAYER1
}
