package utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class to store the parameters of a Datagram using HashMaps
 * @author Oriol-Manu
 */
public class Parameters {

    private HashMap<String, Integer> intParameter = null;
    private HashMap<String, String> stringParameter = null;
    private HashMap<String, ArrayList<Integer>> arrayParameter = null;

    /**
     * Empty constructor to avoid inizializing HashMaps unnecessarily
     */
    public Parameters() {
    }

    /**
     * Returns the integer HashMap and creates it if not inicialized
     * @return integer HashMap
     */
    public HashMap<String, Integer> getIntParameter() {
        if (intParameter == null) {
            intParameter = new HashMap<>();
        }
        return intParameter;
    }

    /**
     * Returns the string HashMap and creates it if not inicialized
     * @return string HashMap
     */
    public HashMap<String, String> getStringParameter() {
        if (stringParameter == null) {
            stringParameter = new HashMap<>();
        }
        return stringParameter;
    }

    /**
     * Returns the integer array HashMap and creates it if not inicialized
     * @return integer array HashMap
     */
    public HashMap<String, ArrayList<Integer>> getArrayParameter() {
        if (arrayParameter == null) {
            arrayParameter = new HashMap<>();
        }
        return arrayParameter;
    }

    /**
     * Checks if 2 parameters objects are equal by checking every HashMap
     * @param obj Parameter object to be checked
     * @return true if equal, else otherwise
     */
    @Override
    public boolean equals(Object obj) {
        Parameters p = (Parameters) obj;
        boolean eq = true;

        if(this.intParameter != null && p.intParameter!=null){
            eq = eq && this.intParameter.equals(p.intParameter);
        }

        if(this.stringParameter!= null && p.stringParameter!=null){
            eq = eq && this.stringParameter.equals(p.stringParameter);
        }

        if(this.arrayParameter!=null && p.arrayParameter!=null){
            eq = eq && (this.arrayParameter.equals(p.arrayParameter));
        }

        return eq;

    }
}
