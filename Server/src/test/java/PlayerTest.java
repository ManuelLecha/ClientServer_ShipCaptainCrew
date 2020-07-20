import Exceptions.BetException;
import Model.Dices;
import Model.Player;
import org.junit.Test;
import static org.junit.Assert.*;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class PlayerTest {

    @Test
    public void bett_with_no_cash_exception() {
        Player player = new Player(-10);
        try{
            player.bet(11);
            fail("Expected error: You have no cash");

        }catch(BetException e){
            assertThat(e.getMessage(),is("You have no cash"));
        }
    }

    @Test
    public void betting_correctly(){
        Player player = new Player(-10);
        try{
            player.bet(1);
            assertEquals(9,player.getGems());
        }catch(BetException e){
           e.printStackTrace();
        }
    }
}
