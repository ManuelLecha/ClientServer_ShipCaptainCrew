package utils;
import utils.Exceptions.NextStateNotDefinedException;

/**
 * States that the dices of a player can be
 * @author Oriol-Manu
 */
public enum DicesState {

    /**
     * No dice taken, next dice to be taken has to be a 6
     */
    NONE("NONE",0,6),

    /**
     * Ship dice taken, next dice to be taken has to be a 5
     */
    SHIP("SHIP", 6, 5),

    /**
     * Captain dice taken, next dice to be taken has to be a 4
     */
    CAPTAIN("CAPTAIN", 5, 4),

    /**
     * All dice that can be taken have already been taken
     */
    CREW("CREW", 4,0);

        private final String s;
        private final int value;
        private final int nextValue;

        DicesState(String s, int value, int nextValue) {
            this.s = s;
            this.value = value;
            this.nextValue = nextValue;

        }

    /**
     * Returns a string with the state
     * @return name of the state
     */
    public String getName(){
            return this.s;
        }

    /**
     * Returns the numerical value related to the state (dice value that has already been rolled
     * to reach the current state)
     * @return integer already rolled
     */
    public int getValue(){
            return this.value;
        }

    /**
     * Returns the numerical value related to the next state (dice value that has 
     * to be rolled to reach the next state)
     * @return integer to be rolled
     */
    public int nextValue(){
            return this.nextValue;
        }

    /**
     * Returns the nexts state
     * @return next state
     * @throws NextStateNotDefinedException if last state has been reached
     */
    public DicesState nextState() throws NextStateNotDefinedException{
            switch (this){
                case NONE:
                    return SHIP;
                case SHIP:
                    return CAPTAIN;
                case CAPTAIN:
                    return CREW;
                default:
                    throw new NextStateNotDefinedException();
            }
        }

}
