package Model;

import Exceptions.TakenDiceException;

import java.util.Random;

/**
 * Class representing a dice
 * @author Oriol-Manu
 */
public class Dice {
    private boolean taken;
    private int value;

    /**
     * Constructor initializing the value of the dice to 0
     */
    public Dice() {
        this.taken = false;
        this.value = 0;
    }

    /**
     * @return Boolean object. True if this dice is already taken, false otherwise.
     */
    public boolean isTaken() {
        return this.taken;
    }

    /**
     * Take the dice
     * @throws TakenDiceException if the dice is already taken
     */
    public void take() throws TakenDiceException {
        if (!this.taken) {
            this.taken = true;
        }
        else {
            throw new TakenDiceException();
        }
    }

    /**
     * @return value of the dice
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Rolls the dice (gives a new value).
     * @return the new value of the dice
     */
    public int rollDice() {
        if (!this.taken) {
            Random r = new Random();
            this.value = r.nextInt(6) + 1;
        }
        return this.value;
    }
}
