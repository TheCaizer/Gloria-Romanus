package unsw.gloriaromanus.backend.infrastructure;

public class RoadState extends BuildingState {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String type = "None";
    private int provinceMovementCost;

    /**
     * Constructor used by JSON factory
     * @param level Building level/ Number in building chain.
     * @param type Road type: "None", "Dirt", "Paved"
     * @param baseWealthCost  Base gold construction cost
     * @param baseTurnCost    Base number of turns to construct.
     * @param provinceMovementCost
     */
    public RoadState(int level, String type, int baseWealthCost, 
                     int baseTurnCost, int provinceMovementCost) {
        super(level, baseWealthCost, baseTurnCost);
        this.type = type;
        this.provinceMovementCost = provinceMovementCost;
    }

    /**
     * @return the cost for a unit to move from this province
     */
    public int getMovementCost() {
        return provinceMovementCost;
    }

    /**
     * @return the type of road
     */
    public String getType() {
        return type;
    }
}