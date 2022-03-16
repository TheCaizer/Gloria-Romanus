package unsw.gloriaromanus.backend.units;

import java.io.Serializable;
import java.util.List;

import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.TurnListener;

public class UnitHut implements TurnListener, Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Turn turn;
    private int finishTurn = 0;
    private Unit curr = null;
    transient private List<Unit> provinceArmy; // reference to province list of units
                                     // to allow insertion.

    public UnitHut(Turn turn, List<Unit> provinceArmy) {
        this.turn = turn;
        this.provinceArmy = provinceArmy;
    }

    public boolean constructUnit(Unit curr) {
        if(this.curr != null){
            return false;
        }
        this.curr = curr;
        finishTurn = turn.getTurnNum() + curr.getTurnToProduce();
        turn.attach(this);

        return true;
    }

    /**
     * Stops any current training, detaching from turn.
     */
    public void cancel() {
        curr = null;
        turn.detach(this);
    }

    public void setProvinceArmy(List<Unit> provinceArmy) {
        this.provinceArmy = provinceArmy;
    }

    public int getTurnFinish(){
        return this.finishTurn;
    }

    public Unit getUnit(){
        return this.curr;
    }

    @Override
    public boolean onTurnChange(Turn obj){
        if (obj.getTurnNum() >= finishTurn){
            provinceArmy.add(curr); // finish unit training;
            curr = null;
            return true;
        }
        return false;
    }
}
