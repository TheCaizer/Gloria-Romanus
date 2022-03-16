package unsw.gloriaromanus.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import unsw.gloriaromanus.backend.exceptions.DefeatException;
import unsw.gloriaromanus.backend.exceptions.InvasionException;
import unsw.gloriaromanus.backend.infrastructure.Building;
import unsw.gloriaromanus.backend.units.Unit;

// basic battle resolver, does not handle special abilities
public class BattleResolver {
    private List<Unit> attackingArmy;
    private List<Unit> defendingArmy;
    private List<TextLogger> observers;
    private Province defendingProvince;
    private Province attackingProvince;
    private Faction defendingFaction;
    private Random random;
    private int numEngagements = 0; // counts number of engagements in the current battle.

    /**
     * battle resolver seeded with a specific random generator 
     * @param attackingProvince the invading province
     * @param defendingProvince the defending province
     * @param seeded the seeded random generator.
     */
    public BattleResolver(Province attackingProvince, List<Unit> attackingArmy, Province defendingProvince, Random seeded) {
        this.defendingProvince = defendingProvince;
        this.attackingProvince = attackingProvince;
        random = seeded;
        observers = new ArrayList<TextLogger>();
        defendingArmy = defendingProvince.getArmy();
        // All defending units can participate.
        defendingArmy.forEach((x) -> x.setHasAttacked(false));
        //attackingArmy = attackingProvince.getArmy();
        // copying into temporary list
        this.attackingArmy = new ArrayList<Unit>();
        this.attackingArmy.addAll(attackingArmy);
        this.defendingFaction = defendingProvince.getFaction();
    }

    /**
     * Unseeded battleResolver
     * @param attackingProvince the invading province
     * @param defendingProvince the defending province
     */
    public BattleResolver(Province attackingProvince, List<Unit> attackingArmy, Province defendingProvince) {
        this(attackingProvince, attackingArmy, defendingProvince, new Random(System.currentTimeMillis()));
    }

    /**
     * Attaches a text logger to this battle resolver, to collect battle/engagement
     * results.
     * @param observer
     */
    public void attachObserver(TextLogger observer) {
        observers.add(observer);
    }

    /**
     * Notify all observers, passing a text description of the battle results.
     * @param description String description of event.
     */
    private void log(String description) {
        for (TextLogger obs : observers) {
            obs.onEvent(description);
        }
    }

    public void initiateBattle() throws DefeatException {
        log("Starting Battle between " + attackingProvince.getName() + " and " + defendingProvince.getName());
        numEngagements = 0;
        while (numEngagements < 200 && !isdefeated(attackingArmy) && !isdefeated(defendingArmy)) {
            initiateSkirmish();
        }

        if (numEngagements >= 200 || isdefeated(attackingArmy)) {
            log(defendingProvince.getName() + " has won the battle.");
            // Draw/Defenders won: all surviving units return home
            attackingArmy.forEach((x) -> {x.setBroken(false); x.setHasAttacked(true);});
            defendingArmy.forEach((x) -> x.setBroken(false));
        } else {
            log(attackingProvince.getName() + " has won the battle.");
            // Attackers won: Defending province is captured.
            defendingProvince.setFaction(attackingProvince.getFaction());

            var i = attackingArmy.iterator();
            while (i.hasNext()) {
                Unit unit = i.next();
                // only move troops who participated in this battle.
                if (unit.hasAttacked() == false) {
                    defendingArmy.add(unit);
                    attackingProvince.getArmy().remove(unit);
                    unit.setHasAttacked(true);
                    i.remove();
                }
            }
        }
        if (defendingFaction.checkDefeat()){
            var e = new DefeatException(defendingFaction.getName() + " have lost the game!");
            e.setFaction(defendingFaction);
            throw e;
        }
    }

    /**
     * alternate option to battle, allows attacking army to attempt to 
     * destroy enemy units and infrastructure, but the attacking army cannot
     * conquer the defending province. 
     * Raids continue until half of defending troops are destroyed, or the attacking
     * army is defeated
     */
    public void initiateRaid() throws InvasionException {
        if (getEffectiveArmy(defendingArmy).isEmpty()) {
            throw new InvasionException("You can't raid a province with no defending troops! \n Try invading instead");
        }
        log("Starting Raid by " + attackingProvince.getName() + " against " + defendingProvince.getName());
        numEngagements = 0;
        int initialDefendingSize = getEffectiveArmy(defendingArmy).size();
        while (numEngagements < 200 && !isdefeated(attackingArmy) && !isRaided(defendingArmy, initialDefendingSize)) {
            initiateSkirmish();
        }


        if (numEngagements >= 200 || isdefeated(attackingArmy)) {
            log("Raid failed");
            // Draw/Defenders won: all surviving units return home
        } else {
            log(defendingProvince.getName() + " Was Successfully raided");
            // Attackers won: Defending province has a chance to take infrastructure damage
            List<Building> buildings = new ArrayList<Building>( defendingProvince.getBuildings().values());
            int damageChance = random.nextInt(25);
            if (damageChance < buildings.size() && buildings.get(damageChance).getLevel() != 0) {
                // Building is destroyed
                log(buildings.get(damageChance).getName() + " was Destroyed!");
                defendingProvince.destroyBuildingNoRefund(buildings.get(damageChance));
            } else {
                log("No infrastructure was successfully destroyed");
            }
        }
        attackingArmy.forEach((x) -> {x.setBroken(false); x.setHasAttacked(true);});
        defendingArmy.forEach((x) -> x.setBroken(false));
    }


    /**
     * Selects a (uniform) random unit from each army and runs engagements
     * between them until either a unit is destroyed or flees/routes from the battle, 
     * or the number of engagements exceeds 200.
     */
    public void initiateSkirmish() {
        Unit attacking = getRandomUnit(attackingArmy);
        Unit defending = getRandomUnit(defendingArmy);
        log("Skirmish initiated: " + attacking.getType() + " vs " + defending.getType());
        int rangedChance = getRangedChance(attacking, defending);

        while (numEngagements < 200) {
            boolean alive = runEngagement(attacking, defending, rangedChance);

            // return if either unit is dead or has successfully routed.
            if (!alive || haveUnitsRouted(attacking, defending)) return;
            numEngagements++;
        }
    }

    /**
     * Checks if either unit is able to successfully flee the battle. 
     * Non broken units do not attempt to flee. if both units are broken
     * then fleeing is guarrenteed.
     * @param u1 a unit
     * @param u2 a unit
     * @return true if either/both units have fled, otherwise false.
     */
    private boolean haveUnitsRouted(Unit u1, Unit u2) {
        if (u1.isBroken() && u2.isBroken()) {
            log("Both units successfully routed.");
            return true;
        }
        if (u1.isBroken() || u2.isBroken()) {
            int routeChance = getRouteChance(u1, u2);
            if (random.nextInt(100) < routeChance) {
                log((u1.isBroken() ? u1.getType() : u2.getType()) 
                    + " successfully routed(" + routeChance + "% chance)");
                return true;
            }
        }
        return false;

    }

    /**
     * Calculates the chance to route, assuming one of the two units has broken.
     * According to the following formula:
     * routeChance = 50% + 10% x (speed of routing unit - speed of pursuing unit)
     * @param u1 a unit
     * @param u2 a unit
     * @return the chance to route, between 10% and 100%
     */
    public int getRouteChance(Unit u1, Unit u2) {
        int routeChance = 50; // 50 %
        // 10 * (speed of routing unit - speed of pursuing unit)
        int routingMod = u1.getSpeed() - u2.getSpeed();
        routingMod *= (u1.isBroken()) ? 10 : -10;
        return applyBounds(routeChance + routingMod, 10, 100);
    }

    /**
     * Runs an engagement and checks if either unit has been destroyed, 
     * removing them from their respective army. If a unit is not destroyed,
     *  then it checked to see if it breaks.
     * Both units are always checked, allowing both to to be destroyed, both to 
     * break, or for one to break and one to be destroyed.  
     * @param attacking the attacking unit to check
     * @param defending the defending unit to check
     * @param casualtyRatio the ratio of casualties attacking:defending.
     * @return true if both units are alive at the end, otherwise false.
     */
    private boolean runEngagement(Unit attacking, Unit defending, int rangedChance) {
        double casualtyRatio = dealCasualties(attacking, defending, rangedChance);
        boolean result = true;
        // Check if attacking unit is alive
        if (attacking.getNumTroops() == 0) {
            log("Attacking " + attacking.getType() + " has been destroyed.");
            attackingArmy.remove(attacking);
            attackingProvince.getArmy().remove(attacking);
            result = false;
        } else {
            // check if unit has broken, only if it is still alive.
            checkUnitBreak(attacking, casualtyRatio, attackingProvince.getMoralePenalty());
        }

        log(defending.getNumTroops()+ "");

        // Check if defending unit is alive
        if (defending.getNumTroops() == 0) {
            log("Defending " + defending.getType() + " has been destroyed.");
            defendingArmy.remove(defending);
            result = false;
        } else {
            // Defending unit's ratio is the inverse of the attacking.
            checkUnitBreak(defending, 1/casualtyRatio, defendingProvince.getMoralePenalty());
        }
        return result;
    }


    /**
     * Calculates the chance for a ranged engagement. If an engagement is not ranged
     * then it is a melee engagmenet. Ranged chance calculated according to the 
     * formulae: 
     * 100% if both units are missle.
     * 0% if neither unit is missle.
     * Otherwise:
     * 50% + 10% x (speed of missile unit - speed of melee unit)
     * bounded between 5% and 95%.
     * @param u1 a unit
     * @param u2 a unit
     * @return the chance for a ranged engagement, out of 100.
     */
    public int getRangedChance(Unit u1, Unit u2) {
        int baseChance = u1.getBaseRangedChance() + u2.getBaseRangedChance();
        if (u1.getRange().equals(u2.getRange())) {
            return baseChance; // 0% or 100%
        }
        // 10% x (speed of missile unit - speed of melee unit)
        int modifier = 10 * (u1.getRangedModifier() + u2.getRangedModifier());
        return applyBounds(baseChance + modifier, 5, 95);
    }

    /**
     * Deals appropriate casualties to 2 units, returning their causualty ratio.
     * Casualty ratio is calculated according to the formula:
     * (u1 casualties/u1 initial size)/(u2 casualties/u2 initial size).
     * This can result in a casualty ratio of 
     * inf, if u2 casualties = 0
     * NaN, if u1 casualties = 0 and u2 casualties = 0
     * (This is dealt with by rounding to integer and setting lower and upper bounds
     * when using this ratio)
     * @param u1 a unit
     * @param u2 a unit
     * @return the casualty ratio of the two units.
     */
    private double dealCasualties(Unit u1, Unit u2, int rangedChance) {
        int u1InitialSize = u1.getNumTroops();
        int u2InitialSize = u2.getNumTroops();
        int u1Casualties;
        int u2Casualties;
        if (random.nextInt(100) < rangedChance) {
            // Ranged engagement
            log("Engagment" + numEngagements + ": Ranged (" + rangedChance + "% chance)");
            u1Casualties = calculateRangedCasualties(u2, u1);
            u2Casualties = calculateRangedCasualties(u1, u2);
        } else {
            // Melee engagement
            log("Engagment " + numEngagements + ": Melee  (" + (100 - rangedChance) + "% chance)");
            u1Casualties = calculateMeleeCasualties(u2, u1);
            u2Casualties = calculateMeleeCasualties(u1, u2);
        }
        log(u1.getType()+" Casualties: "+ u1Casualties + "/" +  u1InitialSize);
        log(u2.getType()+" Casualties: "+ u2Casualties + "/" +  u2InitialSize);

        u1.killTroops(u1Casualties);
        u2.killTroops(u2Casualties);
        // (u1 casualties/u1 initial size)/(u2 casualties/u2 initial size).
        return (((double) u1Casualties)/u1InitialSize)/ (((double) u2Casualties)/u2InitialSize);
    }

    /**
     * Calulates casualties dealt to enemy unit by attacker unit in a ranged 
     * engagement, based on the formula.
     * (initial enemy size x 10%) x (Attacker missle damage/(enemy armor + enemy shield)) x (N+1)
     * where N is a standard normally distributed variable.
     * Melee units deal no damage in ranged encounters.
     * @param attacker the unit dealing damage
     * @param enemy the unit receiving casualties
     * @return the number of casualties dealt to enemy unit by attacker unit.
     */
    private int calculateRangedCasualties(Unit attacker, Unit enemy) {
        // (initial enemy size x 10%)
        double result = enemy.getNumTroops()/10.0;
        // x (Attacker missle damage/(enemy armor + enemy shield))
        result *= ((double) attacker.getMissleAttack())/(enemy.getArmour() + enemy.getShieldDefense());
        //  x (N+1)
        result *= random.nextGaussian() + 1;

        return applyBounds((int) Math.round(result), 0, enemy.getNumTroops());
    }

    /**
     * Calulates casualties dealt to enemy unit by attacker unit in a melee
     * engagement, based on the formula.
     * (initial enemy size x 10%) x (Attacker melee damage/(enemy armor + enemy shield + enemy defense)) x (N+1)
     * where N is a standard normally distributed variable.
     * Attacker melee damage is melee attack damage + charge value.
     * @param attacker the unit dealing damage
     * @param enemy the unit receiving casualties
     * @return the number of casualties dealt to enemy unit by attacker unit.
     */
    private int calculateMeleeCasualties(Unit attacker, Unit enemy) {
        // (initial enemy size x 10%)
        double result = enemy.getNumTroops()/10.0;
        // x (Attacker melee damage/(enemy armor + enemy shield + enemy defense))
        result *= ((double) attacker.getAttack())/(enemy.getArmour() + enemy.getShieldDefense() + enemy.getDefenseSkill());
        // x (N+1)
        result *= random.nextGaussian() + 1;

        return applyBounds((int) Math.round(result), 0, enemy.getNumTroops());
    }

    /**
     * Checks if a unit breaks after an engagement, using the formula:
     * (100% - (morale x 10%)) + (casualtyRatio x 10%).
     * @param unit a unit
     * @param casualtyRatio the ratio of causualties of this unit to the enemey 
     *                      unit in the previous engagement
     * @param provinceMoralePenalty the reduction to morality due to tax rate.
     */
    private void checkUnitBreak(Unit unit, double casualtyRatio, int provinceMoralePenalty) {
        if (unit.isBroken()) return;
        // (100% - (morale x 10%)) 
        int breakChance = 100 - ((unit.getMorale() - provinceMoralePenalty) * 10); 
        // + (casualtyRatio x 10%)
        breakChance += Math.round(casualtyRatio * 10);
        breakChance = applyBounds(breakChance, 5, 100);
        if (random.nextInt(100) < breakChance) {
            unit.setBroken(true);
            log(unit.getType() + " has broken (" + breakChance + "% chance)");
        }
    }
    

    /**
    * @param result a calculation to set a max and min for
     * @param lowerBound the minimum value
     * @param upperBound the maximum value
     * @return a result between the bounds
     */
    private int applyBounds(int result, int lowerBound, int upperBound) {
        if (result < lowerBound) return lowerBound;
        if (result > upperBound) return upperBound;
        return result;
    }

    /**
     * 
     * @param army a province army.
     * @return a uniform randomly selected unit from the army, which is not
     * broken, and has not previously attacked.
     */
    private Unit getRandomUnit(List<Unit> army) {
        var effectiveArmy = getEffectiveArmy(army);
        if (effectiveArmy.size() == 0) return null;
        int index = random.nextInt(effectiveArmy.size());
        return effectiveArmy.get(index);
    }

    /**
     * @param army defending or attacking army
     * @return true if the army has no effective units left
     */
    private boolean isdefeated(List<Unit> army) {
        return getEffectiveArmy(army).isEmpty();
    }


    /**
     * @param army defending army
     * @return true if the army is less than or equal to half of its initial size
     */
    private boolean isRaided(List<Unit> army, int initialSize) {
        double targetSize = initialSize/2.0;
        return getEffectiveArmy(army).size() <= targetSize;
    }

    /**
     * returns a new list contain only enemies who have not routed, and are
     * participating in this battle.
     * @param army the total army of a province
     * @return the effective army of a province
     */
    private List<Unit> getEffectiveArmy(List<Unit> army) {
        ArrayList<Unit> effectiveArmy = new ArrayList<Unit>();
        for(Unit unit : army) {
            if (!unit.isBroken() && !unit.hasAttacked()) {
                effectiveArmy.add(unit);
            }
        }
        return effectiveArmy;
    }
}
