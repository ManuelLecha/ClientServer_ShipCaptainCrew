package Communications;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import utils.Commands;
import utils.Datagram;
import utils.ParamNames;

/**
 * Class to write commands as they were sent but using text
 * @author Oriol-Manu
 */
public class LogWriter {

    private final HashMap<Commands, WriterCommand> writers;
    private final FileWriter fw;

    /**
     * Constructor iniziating a HashMap with the selector pattern by command
     * @param fp file to be written to
     * @throws IOException if the stream with the file is not available
     */
    public LogWriter (File fp) throws IOException {
        fp.createNewFile();
        fw = new FileWriter(fp);
        
        this.writers = new HashMap<>();
        writers.put(Commands.CASH,new CashWriter());
        writers.put(Commands.LOOT, new LootWriter());
        writers.put(Commands.PLAY, new PlayWriter());
        writers.put(Commands.DICE, new DiceWriter());
        writers.put(Commands.TAKE,new TakeWriter());
        writers.put(Commands.PASS, new PassWriter());
        writers.put(Commands.PNTS, new PntsWriter());
        writers.put(Commands.WINS, new WinsWriter());
        writers.put(Commands.ERRO, new ErroWriter());
        writers.put(Commands.STRT, new StrtWriter());
        writers.put(Commands.BETT, new BettWriter());
        writers.put(Commands.EXIT, new ExitWriter());
    }

    /**
     * Writes the given datagram to the log
     * @param d datagram to be written
     * @param player user that has sent the datagram. 0 for server, 1 for client in
     *          1 player games, 2 for first client in 2 player games and
     *          3 for second client in 2 player 
     * @throws IOException if the stream with the file is not available
     */
    public void writeDatagram(Datagram d, int player) throws IOException {
        switch (player) {
            case 0:
                fw.write("S: ");
                break;
            case 1:
                fw.write("C: ");
                break;
            case 2:
                fw.write("C1: ");
                break;
            case 3:
                fw.write("C2: ");
                break;
            default:
                break;
        }
        
        fw.write(d.getType().getKey());
        writers.get(d.getType()).write(d);
        fw.write('\n');
    }
    
    /**
     * closes the file
     * @throws IOException if the stream with the file is not available
     */
    public void closeFile() throws IOException {
        fw.close();
    }
    
    /**
     * Interface to be implemented for every command writer method
     */
    private interface WriterCommand {
        
        /**
         * Writes a datagram
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        public void write(Datagram d) throws IOException;
    }

    private class CashWriter implements WriterCommand{
        
        /**
         * Writes a datagram with CASH command and coins parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.COINS.name()));
        }
    }

    private class LootWriter implements WriterCommand{
        
        /**
         * Writes a datagram with LOOT command and coins parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.COINS.name()));
        }
    }

    private class PlayWriter implements WriterCommand{
        
        /**
         * Writes a datagram with PLAY command and player parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.PLAYER.name()));
        }
    }

    private class DiceWriter implements WriterCommand{
        
        /**
         * Writes a datagram with DICE command and id and dices parameters
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {            
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.ID.name()));
            for (Integer i: d.getParams().getArrayParameter().get(ParamNames.DICES.name())) {
                fw.write(" " + i);
            }
        }
    }

    private class TakeWriter implements WriterCommand{
        
        /**
         * Writes a datagram with TAKE command and id and dices parameters
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.ID.name()));
            fw.write(" " + d.getParams().getArrayParameter().get(ParamNames.DICES.name()).size());
            for (Integer i: d.getParams().getArrayParameter().get(ParamNames.DICES.name())) {
                fw.write(" " + i);
            }
        }
    }

    private class PassWriter implements WriterCommand{
        
        /**
         * Writes a datagram with PASS command and id parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.ID.name()));
        }
    }

    private class PntsWriter implements WriterCommand{
        
        /**
         * Writes a datagram with PNTS command and id and points parameters
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.ID.name()));
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.POINTS.name()));
        }
    }

    private class WinsWriter implements WriterCommand{
        
        /**
         * Writes a datagram with WINS command and winner parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.WINNER.name()));
        }
    }

    private class ErroWriter implements WriterCommand{
        
        /**
         * Writes a datagram with ERRO command and message parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getStringParameter().get(ParamNames.MESSAGE.name()));
        }
    }

    private class StrtWriter implements WriterCommand{
        
        /**
         * Writes a datagram with STRT command and id parameter
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
            fw.write(" " + d.getParams().getIntParameter().get(ParamNames.ID.name()));
        }
    }

    private class BettWriter implements WriterCommand{
        
        /**
         * Writes a datagram with BETT command
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
        }
    }

    private class ExitWriter implements WriterCommand{
        
        /**
         * Writes a datagram with EXIT command
         * @param d datagram to be written
         * @throws IOException if the stream with the file is not available
         */
        @Override
        public void write(Datagram d) throws IOException {
        }
    }
}
