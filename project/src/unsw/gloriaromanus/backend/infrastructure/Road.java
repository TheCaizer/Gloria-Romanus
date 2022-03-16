package unsw.gloriaromanus.backend.infrastructure;

public class Road extends Building{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "Road";
    private RoadState state;
    private RoadState nextState;
    private BuildingFactory factory;

    public Road(BuildingFactory factory) {
        // initialise with empty roadState
        this.factory = factory;
        state = factory.getUpgradedState((RoadState) null);
        nextState = factory.getUpgradedState(state);
    }

    /**
     * @return the cost for a unit to move from this province
     */
    public int getMovementCost() {
        return state.getMovementCost();
    }

    /**
     * @return the type of road
     */
    public String getType() {
        return state.getType();
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
        state = factory.getUpgradedState((RoadState) null);
        nextState = factory.getUpgradedState(state);
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        String s = super.toString();
        s += "Movement Cost: " + getMovementCost() + "\n";
        return s; 
    }
}