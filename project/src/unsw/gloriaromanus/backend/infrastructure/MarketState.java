package unsw.gloriaromanus.backend.infrastructure;

public class MarketState extends BuildingState {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    // Bonuses once constructed
    private int wealthBonus;
    private int growthBonus;
    private int constrWealthCostRed;

    /**
     * Constructor used by BuildingFactory
     * @param level Building level/ Number in building chain.
     * @param baseCost Base gold construction cost.
     * @param baseConstrTime Base number of turns to construct.
     * @param wealthBonus The scalar increase to province wealth
     * @param growthBonus The scalar increase to province growth.
     * @param constrWealthCostRed multaplicitive construction cost reduction 
     */
    public MarketState(int level, int baseCost, int baseConstrTime, 
                       int wealthBonus, int growthBonus, int constrWealthCostRed) {
        super(level, baseCost, baseConstrTime);
        this.wealthBonus = wealthBonus;
        this.growthBonus = growthBonus;
        this.constrWealthCostRed = constrWealthCostRed;
    }

    /**
     * @return The scalar increase to province wealth.
     */
    public int getWealthBonus() {
        return wealthBonus;
    }

    /**
     * @return The scalar increase to province wealth growth.
     */
    public int getGrowthBonus() {
        return growthBonus;
    }

    /**
     * @return The multaplicitive construction cost reduction applied to 
     *         all provinces of the same faction.
     */
    public int getConstrWealthCostRed() {
        return constrWealthCostRed;
    }

}