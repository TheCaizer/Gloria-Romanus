package unsw.gloriaromanus.backend.infrastructure;

public class Market extends Building implements WealthGenerating {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "Market";
    private MarketState state;
    private MarketState nextState; 
    private BuildingFactory factory;


    /**
     * @param factory building factory contain building config info.
     */
    public Market(BuildingFactory factory) {
        this.factory = factory;
        state = factory.getUpgradedState((MarketState) null);
        nextState = factory.getUpgradedState(state);
    }

    /**
     * @return The multaplicitive construction cost reduction applied to 
     *         all provinces of the same faction.
     */
    public int getConstrCostRed() {
        return state.getConstrWealthCostRed();
    }

    @Override
    public int getWealthBonus() {
        return state.getWealthBonus();
    }

    @Override
    public int getGrowthBonus() {
        return state.getGrowthBonus();
    }

    @Override
    public void upgrade() {
        if(isMaxLevel()) return;
        super.upgrade();
        state = nextState;
        nextState = factory.getUpgradedState(state);
    }

    @Override
    public BuildingState getNextState() {
        return nextState;
    }

    @Override
    public int getLevel() {
        return state.getLevel();
    }

    @Override
    public void destroy() {
        super.destroy();
        state = factory.getUpgradedState((MarketState) null);
        nextState = factory.getUpgradedState(state);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String s = super.toString();
        s += getWealthInfo();
        s += "Construction Cost Reduction: " + getConstrCostRed() + "\n";

        return s; 
    }

}