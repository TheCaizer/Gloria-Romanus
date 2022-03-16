package unsw.gloriaromanus.backend.infrastructure;

public class MineState extends BuildingState{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int wealthBonus;
    private int growthBonus;
    private int SoldierWealthCostReduction;
    private int ConstrTurnCostReduction;  

    /**
     * Constructor used by BuildingFactory
     * @param level Building level/ Number in building chain
     * @param baseCost Base gold construction cost
     * @param baseConstrTime Base number of turns to construct.
     * @param wealthBonus The scalar increase to province wealth
     * @param growthBonus The scalar increase to province growth.
     * @param soldierWealthCostReduction Multaplicitive unit cost reduction 
     * @param constrTurnCostReduction Scalar reduction to construction time.
     */
    public MineState(int level, int baseCost, int baseConstrTime, int wealthBonus, 
                     int growthBonus, int soldierWealthCostReduction, 
                     int constrTurnCostReduction) {
        super(level, baseCost, baseConstrTime);
        this.wealthBonus = wealthBonus;
        this.growthBonus = growthBonus;
        SoldierWealthCostReduction = soldierWealthCostReduction;
        ConstrTurnCostReduction = constrTurnCostReduction;
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


    public int getSoldierWealthCostReduction() {
        return SoldierWealthCostReduction;
    }


    public int getConstrTurnCostReduction() {
        return ConstrTurnCostReduction;
    }

}