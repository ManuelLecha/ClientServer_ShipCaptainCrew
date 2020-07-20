package utils;

/**
 * All possible keys that a hashmap of parameters can take
 * @author Oriol-Manu
 */
public enum ParamNames {

    /**
     * When the related value is a number of coins
     */
    COINS,

    /**
     * When the related value is an integer representing a player
     */
    PLAYER,

    /**
     * When the related value is an integer id
     */
    ID,

    /**
     * When the related value is a list of dice values (integer)
     */
    DICES,

    /**
     * When the related value is a number of points
     */
    POINTS,

    /**
     * When the related value is an intger representing the winner
     */
    WINNER,

    /**
     * When the related value is a string message
     */
    MESSAGE,

    /**
     * When the related value is the reciever of the datagram (not a parameter, is for 2 player games)
     */
    RECEIVER;


}
