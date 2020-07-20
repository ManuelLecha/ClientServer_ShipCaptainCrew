package utils;

import java.util.ArrayList;

/**
 * Unchangeable class containing the data of one datagram (a command sent or received)
 * @author Oriol-Manu
 */
public class Datagram {

    private final Parameters params;
    private final Commands type;

    /**
     * Constructor of the datagram
     * @param type command of the datagram
     * @param params parameters of the datagram
     */
    public Datagram(Commands type, Parameters params) {
        this.type = type;
        this.params = params;
    }

    /**
     * Returns the parameters of the datagram
     * @return parameters of the datagram
     */
    public Parameters getParams() {
        return params;
    }

    /**
     * Returns the type of the datagram
     * @return type of the datagram
     */
    public Commands getType() {
        return type;
    }

    /**
     * Checks if 2 datagrams are equals
     * @param obj datagram to be compared with
     * @return true if both are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Datagram d = (Datagram) obj;
        return (d.type.equals(this.type)) && (d.params.equals(this.params));
    }

    /**
     * Builder of a datagram, since a datagram is unchangeable
     */
    public static class Builder{

        private final  Parameters params;
        private Commands type;

        /**
         * Constructor inizializing parameters
         */
        public Builder(){
            this.params = new Parameters();
            this.type = null;
        }

        /**
         * Adds an integer parameter to the hash table of parameters
         * @param key key of the parameter in the hash table
         * @param value integer to be added
         * @return this builder instance
         */
        public Builder addParam(String key, Integer value) {
            this.params.getIntParameter().put(key,value);
            return this;
        }

        /**
         * Adds a string parameter to the hash table of parameters
         * @param key key of the parameter in the hash table
         * @param value string to be added
         * @return this builder instance
         */
        public Builder addParam(String key, String value){
            this.params.getStringParameter().put(key,value);
            return this;
        }

        /**
         * Adds an integer array parameter to the hash table of parameters
         * @param key key of the parameter in the hash table
         * @param value integer array to be added
         * @return this builder instance
         */
        public Builder addParam(String key, ArrayList<Integer> value){
            this.params.getArrayParameter().put(key,value);
            return this;
        }

        /**
         * Sets the command type of the datagram
         * @param type command type
         * @return this builder instance
         */
        public Builder withType(Commands type){
            this.type = type;
            return this;
        }

        /**
         * Builds the unchangeable datagram
         * @return the built datagrams
         */
        public Datagram build(){
            return new Datagram(type,params);
        }

    }
}
