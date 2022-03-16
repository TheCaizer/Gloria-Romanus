package unsw.gloriaromanus.backend.infrastructure;

public class PortState extends BuildingState {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int wealthBonus;
    private int growthBonus;
    private int coastWealthBonus;
    private int coastGrowthBonus;

    /**
     * Constructor used by BuildingFactory
     * @param level Building level/ Number in building chain.
     * @param baseCost Base gold construction cost.
     * @param baseConstrTime Base number of turns to construct.
     * @param wealthBonus The scalar increase to province wealth
     * @param growthBonus The scalar increase to province growth.
     * @param coastWealthBonus The scalar increase to all CoastProvinces wealth
     * @param coastGrowthBonus The scalar increase to all CoastProvinces growth.
     */
    public PortState(int level, int baseCost, int baseConstrTime, 
                     int wealthBonus, int growthBonus, int coastWealthBonus, 
                     int coastGrowthBonus) {
        super(level, baseCost, baseConstrTime);
        this.wealthBonus = wealthBonus;
        this.growthBonus = growthBonus;
        this.coastWealthBonus = coastWealthBonus;
        this.coastGrowthBonus = coastGrowthBonus;
    }

    /**
     * @return The scalar increase to province wealth
     */
    public int getWealthBonus() {
        return wealthBonus;
    }

    /**
     * @return The scalar increase to province growth.
     */
    public int getGrowthBonus() {
        return growthBonus;
    }

    /**
     * @return The scalar increase to all CoastProvinces wealth
     */
    public int getCoastWealthBonus() {
        return coastWealthBonus;
    }

    /**
     * @return The scalar increase to all CoastProvinces growth.
     */
    public int getCoastGrowthBonus() {
        return coastGrowthBonus;
    }

}