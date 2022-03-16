package unsw.gloriaromanus.backend.victoryConditions;

import java.util.List;

import unsw.gloriaromanus.backend.Faction;

public class AndCondition implements VictoryCondition{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name = "AND";
    private List<VictoryCondition> conditions;
    private boolean isDisabled = false;

    public AndCondition(List<VictoryCondition> conditions) {
        this.conditions = conditions;
    }

    @Override
    public void disable() {
        isDisabled = true;
        // For good measure
        conditions.forEach((x) -> x.disable());
    }

    @Override
    public boolean check(Faction faction) {
        if (isDisabled) return false;
        boolean result = true;
        for(VictoryCondition i : conditions) {
            result = result && i.check(faction);
        }
        return result;
    }

    @Override
    public String getString(Faction faction, int indent) {
        if (isDisabled) return "Disabled";
        String s = " ".repeat(indent) + name + ": " + check(faction);
        for (VictoryCondition condition : conditions) {
            s += "\n" + condition.getString(faction, indent + 4);
        }
        return s;
    }
}