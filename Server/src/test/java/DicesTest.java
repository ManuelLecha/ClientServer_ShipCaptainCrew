import Exceptions.RollLimitOverpassedException;
import Model.Dice;
import Model.Dices;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DicesTest {


    @Test
    public void roll_limit_exception_test() {
       Dices dices = new Dices();
       try{
           dices.rollDices();
           dices.rollDices();
           dices.rollDices();
           dices.rollDices();
           fail("Expected error: No more rolls allowed");

       }catch(RollLimitOverpassedException e){
           assertThat(e.getMessage(),is("No more rolls allowed"));
       }
    }







}
