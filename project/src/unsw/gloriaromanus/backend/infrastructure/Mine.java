package unsw.gloriaromanus.backend.infrastructure;

public class Mine extends Building implements WealthGenerating{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "Mine";
    private MineState state;
    private MineState nextState;
    private BuildingFactory factory;

    public Mine(BuildingFactory factory) {
        this.factory = factory;
        state = factory.getUpgradedState((MineState) null);
        nextState = factory.getUpgradedState(state);
    }

    public int getSoldierWealthCostReduction() {
        return state.getSoldierWealthCostReduction();
    }

    public int getConstrTurnCostReduction() {
        return state.getConstrTurnCostReduction();
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
    public int getLevel() {
        return state.getLevel();
    }

    @Override
    public BuildingState getNextState() {
        return nextState;
    }

    @Override
    public void destroy() {
        super.destroy();
        state = factory.getUpgradedState((MineState) null);
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
        s += "Training Cost Reduction: " + getSoldierWealthCostReduction() + "\n";
        s += "Training Time Reduction: " + getConstrTurnCostReduction() + "\n";
        return s; 
    }
}