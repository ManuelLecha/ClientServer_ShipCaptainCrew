import Communications.LogWriter;
import org.junit.Test;
import utils.Commands;
import utils.Datagram;
import utils.ParamNames;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class LogWriterTest{

    private final String S = "S: ";

    @Test
    public void cash_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.CASH)
                    .addParam(ParamNames.COINS.name(),10)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();

            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "CASH 10", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }


    @Test
    public void loot_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.LOOT)
                    .addParam(ParamNames.COINS.name(),2)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "LOOT 2", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }


    @Test
    public void play_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.PLAY)
                    .addParam(ParamNames.PLAYER.name(),1)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "PLAY 1", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }


    }


    @Test
    public void dice_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.DICE);
            db.addParam(ParamNames.ID.name(),1);

            ArrayList<Integer> aInt = new ArrayList<>();

            aInt.add(1);
            aInt.add(2);
            aInt.add(3);
            aInt.add(4);
            aInt.add(5);

            db.addParam(ParamNames.DICES.name(), aInt);

            Datagram d = db.build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "DICE 1 1 2 3 4 5", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }


    @Test
    public void take_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.TAKE);
            db.addParam(ParamNames.ID.name(),1);

            ArrayList<Integer> aInt = new ArrayList<>();

            aInt.add(1);
            aInt.add(2);
            aInt.add(3);

            db.addParam(ParamNames.DICES.name(), aInt);

            Datagram d = db.build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "TAKE 1 3 1 2 3", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }


    @Test
    public void pass_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.PASS)
                    .addParam(ParamNames.ID.name(),1)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "PASS 1", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }



    @Test
    public void pnts_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.PNTS)
                    .addParam(ParamNames.ID.name(),1)
                    .addParam(ParamNames.POINTS.name(),7)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "PNTS 1 7", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }



    @Test
    public void wins_logger_test() throws IOException {

        File f = new File("test");

        try{
            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.WINS)
                    .addParam(ParamNames.WINNER.name(),1)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "WINS 1", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }



    @Test
    public void erro_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.ERRO)
                    .addParam(ParamNames.MESSAGE.name(),"2")
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "ERRO 2", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }


    @Test
    public void strt_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.STRT)
                    .addParam(ParamNames.ID.name(),1)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "STRT 1", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }

    @Test
    public void bett_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.BETT)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "BETT", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }

    @Test
    public void exit_logger_test() throws IOException {

        File f = new File("test");

        try{

            LogWriter lw = new LogWriter(f);

            Datagram d = new Datagram.Builder()
                    .withType(Commands.EXIT)
                    .build();

            lw.writeDatagram(d, 0);
            lw.closeFile();
            FileReader fr = new FileReader(f);
            BufferedReader bf = new BufferedReader(fr);
            assertEquals(S + "EXIT", bf.readLine());

        }catch(IOException e){
            System.out.println(e);
        }
    }

}
