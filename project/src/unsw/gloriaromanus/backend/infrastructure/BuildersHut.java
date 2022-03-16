package unsw.gloriaromanus.backend.infrastructure;
import java.io.Serializable;

import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.TurnListener;
import unsw.gloriaromanus.backend.exceptions.InfrastructureException;

/**
 * Class to construct/upgrade province infrastructure after a turn delay.
 * Ensures that only one building can be constructed per province.
 */
public class BuildersHut implements TurnListener, Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Turn turn;
    private int finishTurn = 0;   // Stores end turn of construction.
    
    private Building curr = null; // null indicates no building currently
                                  // undergoing construction.


    /**
     * @param turn the turn object to observe when neccessary.
     */
    public BuildersHut(Turn turn) {
        // Not attached to turn until construction starts
        this.turn = turn;
    }

    /**
     * Begins construction of a building/upgrade, but only if
     * no construction is already underway.
     * @param building the building to be upgraded
     * @param adjustedTime number of turns to upgrade after modifiers
     * @return true if no other building is currently being constructed and the
     *         building is not max level, otherwise false;
     */
    public boolean constructBuilding(Building curr, int adjustedTime) throws InfrastructureException {
        // if construction is already underway, or if the building
        // is already the last in its chain.
        if (this.curr != null) {
            throw new InfrastructureException("Another building is already being constructed here");
        }
        if (curr.isMaxLevel()) {
            throw new InfrastructureException(curr.getName() + " cannot be upgraded any further");
        }
        this.curr = curr;
        finishTurn = turn.getTurnNum() + adjustedTime;
        turn.attach(this); // set this observer to start observing the turn

        return true;
    }

    /**
     * Stops any current construction, detaching from turn.
     */
    public void cancel() {
        curr = null;
        turn.detach(this);
    }

    public Building getCurrentConstruction() {
        return curr;
    }

    public int getTimeLeft() {
        if (curr == null) return 0;
        return finishTurn - turn.getTurnNum();
    }

    @Override
    public boolean onTurnChange(Turn obj) {
        // If construction has finished
        if (obj.getTurnNum() >= finishTurn) {
            curr.upgrade();
            curr = null;
            return true;
        }
        return false;
    }
}