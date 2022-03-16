package unsw.gloriaromanus.backend.infrastructure;

public class Port extends Building implements WealthGenerating {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "Port";
    private PortState state;
    private PortState nextState;
    private BuildingFactory factory;

    public Port(BuildingFactory factory) {
        this.factory = factory;
        state = factory.getUpgradedState((PortState) null);
        nextState = factory.getUpgradedState(state);
    }

    /**
     * @return The scalar increase to all CoastProvinces wealth
     */
    public int getCoastWealthBonus() {
        return state.getCoastWealthBonus();
    }

    /**
     * @return The scalar increase to all CoastProvinces growth.
     */
    public int getCoastGrowthBonus() {
        return state.getCoastGrowthBonus();
    }

    @Override
    public void upgrade() {
        if(isMaxLevel()) return;
        super.upgrade();
        state = nextState;
        nextState = factory.getUpgradedState(state);
    }

    @Override
    public int getLevel() {
        return state.getLevel();
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
    public BuildingState getNextState() {
        return nextState;
    }

    @Override
    public void destroy() {
        super.destroy();
        state = factory.getUpgradedState((PortState) null);
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
        s += "Coastal Wealth Bonus (multiplicative): " + getCoastWealthBonus() + "\n";
        s += "Coastal Growth Bonus (multiplicative): " + getCoastGrowthBonus() + "\n";
        return s; 
    }
}