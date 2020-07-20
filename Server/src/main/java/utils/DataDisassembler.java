package utils;

import java.io.IOException;
import java.util.HashMap;

/**
 * Class to send commands from Datagram structure
 * @author Oriol-Manu
 */
public class DataDisassembler {
    private final HashMap<Commands, DisassemblerCommand> disassemblers;
    private final ComUtils com;

    /**
     * Constructor building the hash table for the selector pattern depending on the command
     * @param com communication class for receiving and seding information
     */
    public DataDisassembler (ComUtils com) {
        this.com = com;
        this.disassemblers = new HashMap<>();
        disassemblers.put(Commands.CASH,new CashDisassembler());
        disassemblers.put(Commands.LOOT, new LootDisassembler());
        disassemblers.put(Commands.PLAY, new PlayDisassembler());
        disassemblers.put(Commands.DICE, new DiceDisassembler());
        disassemblers.put(Commands.TAKE,new TakeDisassembler());
        disassemblers.put(Commands.PASS, new PassDisassembler());
        disassemblers.put(Commands.PNTS, new PntsDisassembler());
        disassemblers.put(Commands.WINS, new WinsDisassembler());
        disassemblers.put(Commands.ERRO, new ErroDisassembler());
        disassemblers.put(Commands.STRT, new StrtDisassembler());
        disassemblers.put(Commands.BETT, new BettDisassembler());
        disassemblers.put(Commands.EXIT, new ExitDisassembler());
    }

    /**
     * Sends the given datagram to the stream in the appropiate form
     * @param d datagram to be sent
     * @throws IOException if the stream is not available
     */
    public void disassembleDatagram(Datagram d) throws IOException{
        disassemblers.get(d.getType()).disassemble(d);
    }

    /**
     * Interface to be implemented for every command disassembler method
     */
    private interface DisassemblerCommand {
        
        /**
         * Disassembles a datagram
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        public void disassemble(Datagram d) throws IOException;
    }

    private class CashDisassembler implements  DisassemblerCommand{
        
        /**
         * Disassembles a datagram with CASH command and coins parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.COINS.name()));
        }
    }

    private class LootDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with LOOT command and coins parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.COINS.name()));
        }
    }

    private class PlayDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with PLAY command and playerparameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_char(Character.forDigit(d.getParams().getIntParameter().get(ParamNames.PLAYER.name()), 10));
        }
    }

    private class DiceDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with DICE command and id and dices parameters
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.ID.name()));
            for(int i = 0; i < DataAssembler.NUM_DICES; i++){
                com.write_char(' ');
                com.write_char(Character.forDigit(d.getParams().getArrayParameter().get(ParamNames.DICES.name()).get(i), 10));
            }
        }
    }

    private class TakeDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with TAKE command and id and dices parameters
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.ID.name()));
            com.write_char(' ');
            com.write_int8(d.getParams().getArrayParameter().get(ParamNames.DICES.name()).size());
            for(int i = 0; i < d.getParams().getArrayParameter().get(ParamNames.DICES.name()).size(); i++){
                com.write_char(' ');
                com.write_int8(d.getParams().getArrayParameter().get(ParamNames.DICES.name()).get(i));
            }
        }
    }

    private class PassDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with PASS command and id parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.ID.name()));
        }
    }

    private class PntsDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with PNTS command and id parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.ID.name()));
            com.write_char(' ');
            com.write_int8(d.getParams().getIntParameter().get(ParamNames.POINTS.name()));
        }
    }

    private class WinsDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with WINS command and winner parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_char(Character.forDigit(d.getParams().getIntParameter().get(ParamNames.WINNER.name()), 10));
        }
    }

    private class ErroDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with ERRO command and message parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_string_variable(2, d.getParams().getStringParameter().get(ParamNames.MESSAGE.name()));
        }
    }

    private class StrtDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with STRT command and id parameter
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
            com.write_char(' ');
            com.write_int32(d.getParams().getIntParameter().get(ParamNames.ID.name()));
        }
    }

    private class BettDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with BETT command
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
        }
    }

    private class ExitDisassembler implements DisassemblerCommand{
        
        /**
         * Disassembles a datagram with EXIT command
         * @param d datagram to be sent
         * @throws IOException if the stream is not available
         */
        @Override
        public void disassemble(Datagram d) throws IOException {
            com.write_string(d.getType().getKey());
        }
    }

}
