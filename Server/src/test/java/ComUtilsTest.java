import org.junit.Test;
import static org.junit.Assert.*;
import utils.ComUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ComUtilsTest {

    @Test
    public void int_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_int32(2);
            int readedInt = comUtils.read_int32();

            assertEquals(2, readedInt);
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void string_test(){
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_string("Manu");
            String readedStr = comUtils.read_string();

            assertEquals("Manu", readedStr);
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void string_variable_test(){
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_string_variable(4, "Hello world");
            String readedStr = comUtils.read_string_variable(4);

            assertEquals("Hello world", readedStr);
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void char_test(){
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_char('a');
            char readedChr = comUtils.read_char();

            assertEquals('a', readedChr);
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void byte_test() {
        File file = new File("test");
        try {
            file.createNewFile();
            ComUtils comUtils = new ComUtils(new FileInputStream(file), new FileOutputStream(file));
            comUtils.write_int8(2);
            int readedInt = comUtils.read_int8();

            assertEquals(2, readedInt);
            file.delete();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
