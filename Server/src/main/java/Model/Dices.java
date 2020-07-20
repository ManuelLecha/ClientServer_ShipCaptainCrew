package Model;

import utils.DicesState;
import Exceptions.ErrorDiceChoice;
import utils.Exceptions.NextStateNotDefinedException;
import Exceptions.RollLimitOverpassedException;
import Exceptions.TakenDiceException;

import java.util.ArrayList;
import utils.DataAssembler;

/**
 * Array of 5 dices
 * @author Oriol-Manu
 */
public class Dices{

    private final ArrayList<Dice> dices;
    private int num_rolls;
    private DicesState state;

    /**
     * Constructor inizialting the 5 dices and state
     */
    public Dices() {
        this.dices = new ArrayList<>();
        for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
            this.dices.add(new Dice());
        }
        this.num_rolls = 0;
        this.state = DicesState.NONE;
    }

    /**
     * @return array of dices
     */
    public ArrayList<Dice> getDices() {
        return dices;
    }
    
    /**
     * @return array of dice's values
     */
    public ArrayList<Integer> getIntDices() {
        ArrayList<Integer> arrDices = new ArrayList<>();
        this.dices.forEach((dice) -> {
            arrDices.add(dice.getValue());
        });
        return arrDices;
    }

    /**
     * Rolls the five dices
     * @throws RollLimitOverpassedException if the dices have been rolled already three times
     */
    public void rollDices() throws RollLimitOverpassedException {

        if(num_rolls < DataAssembler.MAX_ROLLS){
            for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                this.dices.get(i).rollDice();
            }
        }else{
            throw new RollLimitOverpassedException();
        }

        this.num_rolls = this.num_rolls + 1;
    }

    /**
     * @param dice integer position in the array list
     * @return dice corresponding to the provided position
     */
    public Dice getDice (int dice){
        return dices.get(dice);
    }

    /**
     * @return int corresponding to the number of dices
     */
    public int size(){
        return this.dices.size();
    }

    /**
     * Method to take the specified dices
     * @param dicePosition array list with the positions of the dices to take
     * @param size number of dices to take
     * @throws NextStateNotDefinedException if there is no defined next state of the dices (i.e we are already in crew state at the game)
     * @throws TakenDiceException if the dice is already taken
     * @throws ErrorDiceChoice if the specified dice can not be chosen
     */
    public void takeDices(ArrayList<Integer> dicePosition, int size) throws NextStateNotDefinedException, TakenDiceException, ErrorDiceChoice {

        int it = 0;
        int taken = 0;

        while(it < size && taken < size) {
            for(int i = 0; i<size; i++){
                if (this.dices.get(dicePosition.get(i)-1).getValue() == state.nextValue()) {
                    this.dices.get(dicePosition.get(i)-1).take();
                    this.state = this.state.nextState();
                    taken++;
                }
            }
            it++;
        }

        if (taken < size){
            throw new ErrorDiceChoice();
        }

    }

    /**
     * @return int corresponding to the score (0 if the state is not crew and sum of
     * remaining dices)
     */
    public int getScore(){
        int score = 0;
        if (state != DicesState.CREW) {
            ArrayList<Integer> arrDices = new ArrayList<>();
            DicesState state_aux = state;
            boolean take = true;
            while (take) {
                take = false;
                for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                    if (this.dices.get(i).getValue() == state_aux.nextValue()) {
                        arrDices.add(i+1);
                        take = true;
                        try {
                            state_aux = state_aux.nextState();
                        } catch (NextStateNotDefinedException ex) {
                            break;
                        }
                    }
                }
            }
            try {
                takeDices(arrDices, arrDices.size());
            } catch (NextStateNotDefinedException | TakenDiceException | ErrorDiceChoice ex) {
            }
        }
        if(this.state == DicesState.CREW){
            for(int i = 0; i < DataAssembler.NUM_DICES; i++){
                if(!dices.get(i).isTaken()){
                    score = score + dices.get(i).getValue();
                }
            }
        }
        return score;
    }

    /**
     * @return Boolean object. True if we can roll again
     */
    public boolean canRoll(){
        return num_rolls < DataAssembler.MAX_ROLLS;
    }

    /**
     * @return state of the dices
     */
    public DicesState getState(){
        return this.state;
    }

}
