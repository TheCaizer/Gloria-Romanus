package unsw.gloriaromanus.backend.victoryConditions;

import unsw.gloriaromanus.backend.Faction;

public class TreasuryCondition implements VictoryCondition {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "TREASURY";
    private int balanceReq = 100000;
    private boolean isDisabled = false;

    /**
     * Default constructor
     */
    public TreasuryCondition(){
        this(100000);
    }

    /**
     * Constructor with specified win condition
     * @param balanceReq
     */
    public TreasuryCondition(int balanceReq) {
        if (balanceReq > 0) {
            this.balanceReq = balanceReq;
        }
    }

    @Override
    public void disable() {
        isDisabled = true;
    }

    @Override
    public boolean check(Faction faction){
        if (isDisabled) return false;
        return faction.getBalance() >= balanceReq;
    }

    @Override
    public String getString(Faction faction, int indent) {
        if (isDisabled) return "Disabled";
        return " ".repeat(indent) + name + ": " + faction.getBalance() + "/" + balanceReq;
    }

}
