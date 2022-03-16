package unsw.gloriaromanus.backend.infrastructure;

import java.io.Serializable;

public abstract class Building implements Serializable {

    private int totalCost = 0;
    private static final long serialVersionUID = 1L;

    
    public abstract BuildingState getNextState();
    public abstract String getName();
    public abstract int getLevel();


    public void destroy() {
        totalCost = 0;
    }

    public void upgrade() {
        totalCost += getUpgradeCost();
    }


    public int getRefundAmount() {
        return totalCost/2;
    }

    /**
     * 
     * @return true if the building is the last in its chain (i.e. cannot be 
     *         further upgraded), otherwise false.
     */
    public boolean isMaxLevel() {
        return getNextState() == null;
    }

    /**
     * @return The base number of turns to upgrade/construct this building,
     *         before any faction modifiers are applied.
     */
    public int getUpgradeTime() {
        if (isMaxLevel()) return 0;
        return getNextState().getBaseConstrTime();
    }
    /**
     * @return The base number of gold required to upgrade this building, 
     *         before any faction modifiers are applied.
     */
    public int getUpgradeCost() {
        if (isMaxLevel()) return 0;
        return getNextState().getBaseCost();
    }


    @Override
    public String toString() {
        String s = getName() + "\n";
        s += "Level: " + getLevel() + ((isMaxLevel()) ? "(MAX)\n": "\n");
        return s;
    }
}