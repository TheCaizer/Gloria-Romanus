package unsw.gloriaromanus.backend.units;

import org.json.JSONObject;

public class Infantry extends Unit {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public Infantry(String unitName, JSONObject unit) {
        super(unitName, unit);
    }
}