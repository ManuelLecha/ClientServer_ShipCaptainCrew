import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import utils.*;
import utils.Exceptions.DatagramSyntacticException;

import java.io.*;
import java.util.ArrayList;

public class AssemblerDisassemblerTest {

    /**
     * Test the assemble and disassemble process for a start protocol datagram
     */
    @Test
    public void strt_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.STRT)
                    .addParam(ParamNames.ID.name(),1)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }


    /**
     * Test the assemble and disassemble process for a cash protocol datagram
     */
    @Test
    public void cash_assemble_disassemble_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.CASH)
                    .addParam(ParamNames.COINS.name(),10)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when cash value is not positive
     */
    @Test
    public void cash_positive_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.CASH)
                    .addParam(ParamNames.COINS.name(),-10)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: Gems cannot be positive");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Gems cannot be negative"));
        }
    }


    /**
     * Test the assemble and disassemble process for a loot protocol datagram
     */
    @Test
    public void loot_assemble_disassemble_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.LOOT)
                    .addParam(ParamNames.COINS.name(),2)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when loot value is less than two
     */
    @Test
    public void loot_greater_or_equal_to_two_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.LOOT)
                    .addParam(ParamNames.COINS.name(),1)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: Loot cannot be less than two");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Loot cannot be less than two"));
        }
    }

    /**
     * Test the assemble and disassemble process for a play protocol datagram
     */
    @Test
    public void play_assemble_disassemble_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PLAY)
                    .addParam(ParamNames.PLAYER.name(),1)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when the number of players id grater than two
     */
    @Test
    public void play_less_than_two_players_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PLAY)
                    .addParam(ParamNames.PLAYER.name(),4)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();

            fail("Expected error: This is a two player game");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("This is a two player game"));
        }
    }
    /*
    @Test
    public void play_more_than_cero_players_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PLAY)
                    .addParam(ParamNames.PLAYER.name(),1)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();

            fail("Expected error: This is a two player game");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("This is a two player game"));
        }
    }*/

    /**
     * Test the assemble and disassemble process for a dice protocol datagram
     */
    @Test
    public void dice_assemble_disassemble_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            ArrayList<Integer> dices = new ArrayList<>();
            for(int i = 0; i<5; i++){
                dices.add(1);
            }

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.DICE)
                    .addParam(ParamNames.ID.name(),1)
                    .addParam(ParamNames.DICES.name(),dices)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when there is at least one dice value received with a not defined value
     */
    @Test
    public void dice_correct_value_test() {

        File file = new File("test");

        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            ArrayList<Integer> dices = new ArrayList<>();
            for(int i = 0; i<5; i++){
                dices.add(7);
            }

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.DICE)
                    .addParam(ParamNames.ID.name(),1)
                    .addParam(ParamNames.DICES.name(),dices)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: Dices must be between 1-6");


        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Dices must be between 1-6"));
        }
    }

    /**
     * Test the assemble and disassemble process for a take protocol datagram
     */
    @Test
    public void take_assemble_disassemble_test() {

        File file = new File("test");

        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            ArrayList<Integer> takeDices = new ArrayList<>();
            takeDices.add(1);
            takeDices.add(2);
            takeDices.add(3);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.TAKE)
                    .addParam(ParamNames.ID.name(),2)
                    .addParam(ParamNames.DICES.name(),takeDices)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Tests if the assemble datagram creation process throws an exception when the number of dices taken received is greater than three
     */
    @Test
    public void take_correct_number_of_dices_test() {

        File file = new File("test");

        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            ArrayList<Integer> takeDices = new ArrayList<>();
            takeDices.add(1);
            takeDices.add(2);
            takeDices.add(3);
            takeDices.add(4);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.TAKE)
                    .addParam(ParamNames.ID.name(),2)
                    .addParam(ParamNames.DICES.name(),takeDices)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: User can take between 0 and 3 dices");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("User can take between 0 and 3 dices"));
        }

    }

    /**
     * Tests if the assemble datagram creation process throws an exception when a dice position received is no congruous with the game
     */
    @Test
    public void take_correct_position_dice_test() {

        File file = new File("test");

        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            ArrayList<Integer> takeDices = new ArrayList<>();
            takeDices.add(1);
            takeDices.add(2);
            takeDices.add(7);


            Datagram d1 = new Datagram.Builder().
                    withType(Commands.TAKE)
                    .addParam(ParamNames.ID.name(),2)
                    .addParam(ParamNames.DICES.name(),takeDices)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: Dice positions must be between 1 and 5");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Dice positions must be between 1 and 5"));
        }

    }

    /**
     * Test the assemble and disassemble process for a pass protocol datagram
     */
    @Test
    public void pass_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PASS)
                    .addParam(ParamNames.ID.name(),1)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test the assemble and disassemble process for a points protocol datagram
     */
    @Test
    public void pnts_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PNTS)
                    .addParam(ParamNames.ID.name(),1)
                    .addParam(ParamNames.POINTS.name(),10)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when the sum of points received is greater than twelve
     */
    @Test
    public void pnts_correct_value_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.PNTS)
                    .addParam(ParamNames.ID.name(),1)
                    .addParam(ParamNames.POINTS.name(),15)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Expected error: Points must be 0 or grater that one or less that 13");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Points must be 0 or grater that one or less that 13"));
        }
    }

    /**
     * Test the assemble and disassemble process for a wins protocol datagram
     */
    @Test
    public void wins_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.WINS)
                    .addParam(ParamNames.WINNER.name(),0)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Tests if the assemble datagram creation process throws an exception when the winner number is not consistent with the protocol
     */
    @Test
    public void wins_correct_value_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.WINS)
                    .addParam(ParamNames.WINNER.name(),5)
                    .build();

            dd.disassembleDatagram(d1);
            da.assembleDatagram();
            fail("Winner not defined");

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            assertThat(e.getMessage(),is("Winner not defined"));
        }
    }

    /**
     * Test the assemble and disassemble process for a error protocol datagram
     */
    @Test
    public void erro_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.ERRO)
                    .addParam(ParamNames.MESSAGE.name(),"2Nope")
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test the assemble and disassemble process for a bett protocol datagram
     */
    @Test
    public void bett_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.BETT)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Test the assemble and disassemble process for a exit protocol datagram
     */
    @Test
    public void exit_assemble_disassemble_test() {
        File file = new File("test");
        try {

            file.createNewFile();

            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            DataAssembler da = new DataAssembler(comUtils);
            DataDisassembler dd = new DataDisassembler(comUtils);

            Datagram d1 = new Datagram.Builder().
                    withType(Commands.EXIT)
                    .build();

            dd.disassembleDatagram(d1);
            Datagram d2 = da.assembleDatagram();

            assertEquals(d1,d2);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (DatagramSyntacticException e) {
            System.out.println(e.getMessage());
        }
    }





}
