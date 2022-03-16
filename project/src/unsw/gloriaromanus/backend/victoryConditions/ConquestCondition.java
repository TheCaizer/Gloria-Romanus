package unsw.gloriaromanus.backend.victoryConditions;

import unsw.gloriaromanus.backend.Faction;

public class ConquestCondition implements VictoryCondition {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "CONQUEST";
    private int numProvinces = 53;
    private boolean isDisabled = false;

    /**
     * Default constructor
     */
    public ConquestCondition() {
        this(53);
    }



    /**
     * Constructor with specified win condition
     * @param numProvinces
     */
    public ConquestCondition(int numProvinces) {
        if (numProvinces > 0 && numProvinces < 53) {
            this.numProvinces = numProvinces;
        }
    }

    @Override
    public void disable() {
        isDisabled = true;
    }

    @Override
    public boolean check(Faction faction){
        if (isDisabled) return false;
        return faction.getFactionSize() >= numProvinces;
    }

    @Override
    public String getString(Faction faction, int indent) {
        if (isDisabled) return "Disabled";
        return " ".repeat(indent) + name + ": " + faction.getFactionSize() + "/" + numProvinces;
    }

}
