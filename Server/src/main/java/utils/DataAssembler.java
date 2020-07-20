package utils;

import utils.Exceptions.DatagramSyntacticException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to build command Datagrams from data read
 * @author Oriol-Manu
 */
public class DataAssembler {

    private final HashMap<Commands, AssemblerCommand> assemblers;
    private final ComUtils com;

    /**
     * Header for length variable error message
     */
    public final static int ERROR_HEADER = 2;

    /**
     * Maximum number of dices
     */
    public static final int NUM_DICES = 5;

    /**
     * Maximum number of rolls
     */
    public static final int MAX_ROLLS = 3;

    /**
     * Constructor building the hash table for the selector pattern depending on the command
     * @param com communication class for receiving and seding information
     */
    public DataAssembler (ComUtils com) {
        this.com = com;
        this.assemblers = new HashMap<>();
        assemblers.put(Commands.CASH,new CashAssembler());
        assemblers.put(Commands.LOOT, new LootAssembler());
        assemblers.put(Commands.PLAY, new PlayAssembler());
        assemblers.put(Commands.DICE, new DiceAssembler());
        assemblers.put(Commands.TAKE,new TakeAssembler());
        assemblers.put(Commands.PASS, new PassAssembler());
        assemblers.put(Commands.PNTS, new PntsAssembler());
        assemblers.put(Commands.WINS, new WinsAssembler());
        assemblers.put(Commands.ERRO, new ErroAssembler());
        assemblers.put(Commands.STRT, new StrtAssembler());
        assemblers.put(Commands.BETT, new BettAssembler());
        assemblers.put(Commands.EXIT, new ExitAssembler());
    }

    /**
     * Waits for a command to be received and builds a package with selector pattern
     * @return a datagram with the information received
     * @throws IOException if the stream is not available
     * @throws DatagramSyntacticException if the command received is not well built
     */
    public Datagram assembleDatagram() throws IOException, DatagramSyntacticException {

        String cmd = com.read_string();
        Commands command;
        
        try{
            command = Commands.valueOf(cmd);
        }catch(Exception e){
            throw new DatagramSyntacticException("The command does not exist");
        }

        Datagram datagram = assemblers.get(command).assemble();
        return datagram;

    }
    
    /**
     * Skips the space between parameters in commands
     * @throws DatagramSyntacticException if the command is not well built
     * @throws IOException if the stream is not available
     */
    private void skipSpace() throws DatagramSyntacticException, IOException {
        Character sp = com.read_char();
        Character aux = ' ';

        if(!sp.equals(aux)){
            throw new DatagramSyntacticException("There is not an space between command parameters");
        }
    }

    /**
     * Interface to be implemented for every command assembler method
     */
    private interface AssemblerCommand {
        
        /**
         * Assembles a datagram
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        public Datagram assemble() throws IOException, DatagramSyntacticException;
    }

    private class CashAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with CASH command and coins parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.CASH);
            int gems = com.read_int32();

            if(gems < 0){
                throw new DatagramSyntacticException("Gems cannot be negative");
            }

            db.addParam(ParamNames.COINS.name(), gems);

            return db.build();
        }
    }

    private class LootAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with LOOT command and coins parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.LOOT);

            int loot = com.read_int32();

            if(loot < 2){
                throw new DatagramSyntacticException("Loot cannot be less than two");
            }

            db.addParam(ParamNames.COINS.name(), loot);

            return db.build();
        }
    }

    private class PlayAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with PLAY command and player parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db= new Datagram.Builder();
            db.withType(Commands.PLAY);

            int player =  Integer.parseInt(String.valueOf(com.read_char()));

            if(player < 0 || player > 1){
                throw new DatagramSyntacticException("This is a two player game");
            }

            db.addParam(ParamNames.PLAYER.name(),player);

            return db.build();
        }
    }

    private class DiceAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with DICE command and id and dices parameters
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.DICE);
            db.addParam(ParamNames.ID.name(), com.read_int32());

            ArrayList<Integer> aInt = new ArrayList<>();
            for (int i = 0; i < DataAssembler.NUM_DICES; i++) {
                skipSpace();
                Integer dice = Integer.parseInt(String.valueOf(com.read_char()));
                if(1<=dice && dice<=6){
                    aInt.add(dice);
                }else{
                    throw new DatagramSyntacticException("Dices must be between 1-6");
                }
            }

            db.addParam(ParamNames.DICES.name(), aInt);
            return db.build();
        }
    }

    private class TakeAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with TAKE command and id and dices parameters
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.TAKE);
            db.addParam(ParamNames.ID.name(), com.read_int32());

            ArrayList<Integer> aInt = new ArrayList<>();
            skipSpace();

            int size = com.read_int8();

            if(size < 0 || size > 3){
                throw new DatagramSyntacticException("User can take between 0 and 3 dices");
            }

            for (int i = 0; i < size; i++) {
                skipSpace();
                int dicePos = com.read_int8();
                if(dicePos < 1 || dicePos > 5){
                    throw new DatagramSyntacticException("Dice positions must be between 1 and 5");
                }
                aInt.add(dicePos);
            }

            db.addParam(ParamNames.DICES.name(), aInt);

            return db.build();
        }
    }

    private class PassAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with PASS command and id parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram d = new Datagram.Builder()
                    .withType(Commands.PASS)
                    .addParam(ParamNames.ID.name(), com.read_int32()).build();

            return d;
        }
    }

    private class PntsAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with PNTS command and id and points parameters
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder()
                    .withType(Commands.PNTS)
                    .addParam(ParamNames.ID.name(), com.read_int32());

            skipSpace();
            int pnts = com.read_int8();

            if(!(pnts == 0 || ((pnts >= 2) && (pnts <= 12)))){
                throw new DatagramSyntacticException("Points must be 0 or grater that one or less that 13");
            }

            db.addParam(ParamNames.POINTS.name(), pnts);

            return db.build();
        }
    }

    private class WinsAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with WINS command and winner parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram.Builder db = new Datagram.Builder();
            db.withType(Commands.WINS);

            int winner = Integer.parseInt(String.valueOf(com.read_char()));

            if(winner < 0 || winner > 2){
                throw new DatagramSyntacticException("Winner not defined");
            }

            db.addParam(ParamNames.WINNER.name(), winner);

            return db.build();
        }
    }

    private class ErroAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with ERRO command and message parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram d = new Datagram.Builder()
                    .withType(Commands.ERRO)
                    .addParam(ParamNames.MESSAGE.name(), com.read_string_variable(ERROR_HEADER))
                    .build();

            return d;
        }
    }

    private class StrtAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with STRT command and id parameter
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException, DatagramSyntacticException {
            skipSpace();
            Datagram d = new Datagram.Builder()
                    .withType(Commands.STRT)
                    .addParam(ParamNames.ID.name(), com.read_int32())
                    .build();

            return d;
        }
    }

    private class BettAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with BETT command
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException {
            
            Datagram d = new Datagram.Builder()
                    .withType(Commands.BETT)
                    .build();

            return d;
        }
    }

    private class ExitAssembler implements AssemblerCommand{
        
        /**
         * Assembles a datagram with EXIT command
         * @return the datagram built
         * @throws IOException if the stream is not available
         * @throws DatagramSyntacticException if the command is not well built
         */
        @Override
        public Datagram assemble() throws IOException {

            Datagram d = new Datagram.Builder()
                    .withType(Commands.EXIT)
                    .build();

            return d;
        }
    }
}