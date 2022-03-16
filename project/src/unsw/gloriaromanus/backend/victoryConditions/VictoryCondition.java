package unsw.gloriaromanus.backend.victoryConditions;

import java.io.Serializable;

import unsw.gloriaromanus.backend.Faction;

public interface VictoryCondition extends Serializable {

    /**
     * @return string representing this condition and factions progress
     */
    public String getString(Faction faction, int indent);

    /**
     * disables the Victory condition. This can be used after a game victory
     * to allow players to continue playing without interuption.
     */
    public void disable();

    /**
     * @param faction a faction to check
     * @return true if the faction satisfies this condition.
     *         false if the faction has not satified this condition, or if
     *         this condition is disabled.
     */
    public boolean check(Faction faction);
}