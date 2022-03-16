package unsw.gloriaromanus.backend.infrastructure;

import java.io.Serializable;

/**
 * Abstract class to define the behaviour of a building. Stores basic 
 * building information. Part of building strategy pattern.
 */
public abstract class BuildingState implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int level;

    // Construction costs
    private int baseCost;
    private int baseConstrTime;

    /**
     * Default building state constructor.
     * @param level Building level
     * @param baseCost base gold cost of building
     * @param baseConstrTime base construction time of building
     */
    public BuildingState(int level, int baseCost, int baseConstrTime) {
        this.level = level;
        this.baseCost = baseCost;
        this.baseConstrTime = baseConstrTime;
    }

    /**
     * @return Building level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return base gold cost of building
     */
    public int getBaseCost() {
        return baseCost;
    }

    /**
     * @return base number of turns to construct.
     */
    public int getBaseConstrTime() {
        return baseConstrTime;
    }

}