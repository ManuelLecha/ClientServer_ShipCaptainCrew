package utils;

/**
 * Possible commands that can be sent between client and server
 * @author Oriol-Manu
 */
public enum Commands {

    /**
     * Cash command
     */
    CASH("CASH"),

    /**
     * Loot command
     */
    LOOT("LOOT"),

    /**
     * Play command
     */
    PLAY("PLAY"),

    /**
     * Dice command
     */
    DICE("DICE"),

    /**
     * Take command
     */
    TAKE("TAKE"),

    /**
     * Pass command
     */
    PASS("PASS"),

    /**
     * Points command
     */
    PNTS("PNTS"),

    /**
     * Win command
     */
    WINS("WINS"),

    /**
     * Error command
     */
    ERRO("ERRO"),

    /**
     * Start command
     */
    STRT("STRT"),

    /**
     * Bet command
     */
    BETT("BETT"),

    /**
     * Exit command
     */
    EXIT("EXIT");


    private final String command;

    Commands(String command) {
        this.command = command;
    }

    /**
     * Returns the command in string type
     * @return string with the command
     */
    public String getKey(){
        return this.command;
    }

}
