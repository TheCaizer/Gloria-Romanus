package unsw.gloriaromanus.backend.exceptions;

import java.lang.Exception;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;

/**
 * Custom exception class for passing information from the backend
 */
public abstract class RomanusException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Province province = null;
    private Faction faction = null;

    public RomanusException(String message) {
        super(message);
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(Faction faction) {
        this.faction = faction;
    }
    
}