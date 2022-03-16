package unsw.gloriaromanus.backend.victoryConditions;

import unsw.gloriaromanus.backend.Faction;

public class WealthCondition implements VictoryCondition {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "WEALTH";
    private int wealthReq = 400000;
    private boolean isDisabled = false;

    /**
     * Default constructor
     */
    public WealthCondition() {
        this(400000);
    }

    /**
     * Constructor with specified win condition
     * @param wealthReq
     */
    public WealthCondition(int wealthReq) {
        if (wealthReq > 0) {
            this.wealthReq = wealthReq;
        }

    }

    @Override
    public void disable() {
        isDisabled = true;
    }

    @Override
    public boolean check(Faction faction) {
        if (isDisabled) return false;
         return faction.getFactionWealth() >= wealthReq;
    }

    @Override
    public String getString(Faction faction, int indent) {
        if (isDisabled) return "Disabled";
        return " ".repeat(indent) + name + ": " +  + faction.getFactionWealth() + "/" +wealthReq ;
    }

}
