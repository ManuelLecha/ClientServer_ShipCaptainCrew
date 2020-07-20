package utils;

import java.io.*;
import java.net.Socket;

/**
 * Class to send and receive information from the provided stream
 * @author Oriol-PC
 */
public class ComUtils {
    private final int STRSIZE = 4;

    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    /**
     * Constructor with the stream
     * @param inputStream
     * @param outputStream
     * @throws IOException if the stream cannot be created
     */
    public ComUtils(InputStream inputStream, OutputStream outputStream) throws IOException {
        dataInputStream = new DataInputStream(inputStream);
        dataOutputStream = new DataOutputStream(outputStream);
    }

    /**
     * Constructor with the socket as a stream
     * @param socket
     * @throws IOException if the connection cannot be created
     */
    public ComUtils(Socket socket) throws IOException {
        dataInputStream = new DataInputStream(socket.getInputStream());
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
    }

    /**
     * Reads the next 4 bytes and returns it as an int
     * @return read int
     * @throws IOException if the connection cannot be created
     */
    public int read_int32() throws IOException {
        byte bytes[] = read_bytes(4);

        return bytesToInt32(bytes,Endianness.BIG_ENNDIAN);
    }

    /**
     * Writes a provided int to the stream
     * @param number int to be written
     * @throws IOException if the connection cannot be created
     */
    public void write_int32(int number) throws IOException {
        byte bytes[] = int32ToBytes(number, Endianness.BIG_ENNDIAN);

        dataOutputStream.write(bytes, 0, 4);
    }
    
    /**
     * Reads the next byte and returns it as an int
     * @return read int
     * @throws IOException if the connection cannot be created
     */
    public int read_int8() throws IOException {
        byte bytes[] = read_bytes(1);

        return bytesToInt8(bytes);
    }
    
    /**
     * Writes the first byte of the provided int to the stream 
     * @param number int to be written
     * @throws IOException if the connection cannot be created
     */
    public void write_int8(int number) throws IOException {
        byte bytes[] = int8ToBytes(number);

        dataOutputStream.write(bytes, 0, 1);
    }

    /**
     * Reads a string of maximum length of 40 characters
     * @return read string
     * @throws IOException if the connection cannot be created
     */
    public String read_string() throws IOException {
        String result;
        byte[] bStr = new byte[STRSIZE];
        char[] cStr = new char[STRSIZE];

        bStr = read_bytes(STRSIZE);

        for(int i = 0; i < STRSIZE;i++)
            cStr[i]= (char) bStr[i];

        result = String.valueOf(cStr);

        return result.trim();
    }

    /**
     * Writes a string of maximum length of 40 characters to the stream
     * @param str string to be written
     * @throws IOException if the connection cannot be created
     */
    public void write_string(String str) throws IOException {
        int numBytes, lenStr;
        byte bStr[] = new byte[STRSIZE];

        lenStr = str.length();

        if (lenStr > STRSIZE)
            numBytes = STRSIZE;
        else
            numBytes = lenStr;

        for(int i = 0; i < numBytes; i++)
            bStr[i] = (byte) str.charAt(i);

        for(int i = numBytes; i < STRSIZE; i++)
            bStr[i] = (byte) ' ';

        dataOutputStream.write(bStr, 0,STRSIZE);
    }

    /**
     * Reads a character
     * @return read character
     * @throws IOException if the connection cannot be created
     */
    public char read_char() throws IOException {
        char result = (char) read_bytes(1)[0];
        return result;
    }

    /**
     * Writes a character to the stream
     * @param chr character to be written
     * @throws IOException if the connection cannot be created
     */
    public void write_char(char chr) throws IOException {
        byte bChr[] = new byte[1];
        bChr[0] = (byte) chr;
        dataOutputStream.write(bChr, 0, 1);
    }

    private byte[] int32ToBytes(int number, Endianness endianness) {
        byte[] bytes = new byte[4];

        if(Endianness.BIG_ENNDIAN == endianness) {
            bytes[0] = (byte)((number >> 24) & 0xFF);
            bytes[1] = (byte)((number >> 16) & 0xFF);
            bytes[2] = (byte)((number >> 8) & 0xFF);
            bytes[3] = (byte)(number & 0xFF);
        }
        else {
            bytes[0] = (byte)(number & 0xFF);
            bytes[1] = (byte)((number >> 8) & 0xFF);
            bytes[2] = (byte)((number >> 16) & 0xFF);
            bytes[3] = (byte)((number >> 24) & 0xFF);
        }
        return bytes;
    }

    /* Passar de bytes a enters */
    private int bytesToInt32(byte bytes[], Endianness endianness) {
        int number;

        if(Endianness.BIG_ENNDIAN == endianness) {
            number=((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) |
                    ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        }
        else {
            number=(bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                    ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
        }
        return number;
    }
    
    private byte[] int8ToBytes(int number) {
        byte[] bytes = new byte[1];

        bytes[0] = (byte)(number & 0xFF);
        return bytes;
    }
    
    /* Passar d'un byte a enters */
    private int bytesToInt8(byte bytes[]) {
        int number = (bytes[0] & 0xFF);
        
        return number;
    }
    
    //llegir bytes.
    private byte[] read_bytes(int numBytes) throws IOException {
        int len = 0;
        byte bStr[] = new byte[numBytes];
        int bytesread = 0;
        do {
            bytesread = dataInputStream.read(bStr, len, numBytes-len);
            if (bytesread == -1)
                throw new IOException("Broken Pipe");
            len += bytesread;
        } while (len < numBytes);
        return bStr;
    }

    /* Llegir un string  mida variable size = nombre de bytes especifica la longitud*/

    /**
     * Reads a variable length string
     * @param size length of the string
     * @return read string
     * @throws IOException if the connection cannot be created
     */
    public  String read_string_variable(int size) throws IOException {
        byte bHeader[] = new byte[size];
        char cHeader[] = new char[size];
        int numBytes = 0;

        // Llegim els bytes que indiquen la mida de l'string
        bHeader = read_bytes(size);
        // La mida de l'string ve en format text, per tant creem un string i el parsejem
        for(int i=0;i<size;i++){
            cHeader[i]=(char)bHeader[i]; }
        numBytes=Integer.parseInt(new String(cHeader));

        // Llegim l'string
        byte bStr[]=new byte[numBytes];
        char cStr[]=new char[numBytes];
        bStr = read_bytes(numBytes);
        for(int i=0;i<numBytes;i++)
            cStr[i]=(char)bStr[i];
        return String.valueOf(cStr);
    }

    /* Escriure un string mida variable, size = nombre de bytes especifica la longitud  */
    /* String str = string a escriure.*/

    /**
     * Writes a variable length string to the stream
     * @param size lenght of the string
     * @param str string to be written
     * @throws IOException if the connection cannot be created
     */

    public void write_string_variable(int size,String str) throws IOException {

        // Creem una seqüència amb la mida
        byte bHeader[]=new byte[size];
        String strHeader;
        int numBytes=0;

        // Creem la capçalera amb el nombre de bytes que codifiquen la mida
        numBytes=str.length();

        strHeader=String.valueOf(numBytes);
        int len;
        if ((len=strHeader.length()) < size)
            for (int i =len; i< size;i++){
                strHeader= "0"+strHeader;}
        for(int i=0;i<size;i++)
            bHeader[i]=(byte)strHeader.charAt(i);
        // Enviem la capçalera
        dataOutputStream.write(bHeader, 0, size);
        // Enviem l'string writeBytes de DataOutputStrem no envia el byte més alt dels chars.
        dataOutputStream.writeBytes(str);
    }

    /**
     * Types of endiannes
     */
    public enum Endianness {

        /**
         * Big endian
         */
        BIG_ENNDIAN,

        /**
         * Little endian
         */
        LITTLE_ENDIAN
    }
}


