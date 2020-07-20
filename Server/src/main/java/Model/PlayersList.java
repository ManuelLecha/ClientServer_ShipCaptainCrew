package Model;

import java.util.ArrayList;

/**
 * List of all player that have been inizialized any time while server has been active
 * @author Oriol-Manu
 */
public class PlayersList {
    private static PlayersList instance = null;
    private final ArrayList<Player> players;
    
    private PlayersList() {
        players = new ArrayList<>();
    }
    
    /**
     * @return the unique instance of a PlayerList by singleton
     */
    public static PlayersList getInstance() {
        if (instance == null) {
            instance = new PlayersList();
        }
        return instance;
    }
    
    /**
     * Searchs if there is a player with the provided ID and returns or creates it
     * @param id integer ID to search
     * @return if the player has been found, returns the player, creates a new player otherwise
     */
    public Player getPlayer (int id) {
        for(Player player: this.players) {
            if (player.getId() == id) {
                return player;
            }
        }
        synchronized(this) {
            Player player = new Player(id);
            players.add(player);
            return player;
        }
    }
}
