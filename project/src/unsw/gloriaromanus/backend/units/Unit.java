package unsw.gloriaromanus.backend.units;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Represents a basic unit of soldiers
 * 
 * incomplete - should have heavy infantry, skirmishers, spearmen, lancers, heavy cavalry, elephants, chariots, archers, slingers, horse-archers, onagers, ballista, etc...
 * higher classes include ranged infantry, cavalry, infantry, artillery
 * 
 * current version represents a heavy infantry unit (almost no range, decent armour and morale)
 */
public abstract class Unit implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private int numTroops; // the number of troops in this unit (should reduce based on depletion)
    private String range; // range of the unit
    private String type; // the type of unit infantry, cavlary, artillery, spearmen(same as infantry
                         // without shield charge)
    private int armour; // armour defense
    private int morale; // resistance to fleeing
    private int speed; // ability to disengage from disadvantageous battle
    private int attack; // can be either missile or melee attack to simplify. Could improve
                        // implementation by differentiating!
    private int defenseSkill; // skill to defend in battle. Does not protect from arrows!
    private int shieldDefense; // a shield
    private String[] specialAbility; // the list of innate buffs they get due to their type, range and unit name
    private int turnToProduce; // how long they take to produce
    private int charge; // the charge num for mainly cavlary units
    private int numEngagement = 0; // number of engagement per battle for shield charge (melee infantry)
    private int cost; // cost of recruiting the unit
    private int maxMovement;    // maximum movement points
    private int movementPoints; // current movement points
    private boolean isBroken = false;
    private boolean hasAttacked = false;

    public Unit(String unitName, JSONObject unit) {
        this.name = unitName;
        this.numTroops = unit.getInt("numTroops");
        this.range = unit.getString("range");
        this.type = unit.getString("type");
        this.armour = unit.getInt("armour");
        this.morale = unit.getInt("morale");
        this.speed = unit.getInt("speed");
        this.attack = unit.getInt("attack");
        this.defenseSkill = unit.getInt("defenseSkills");
        this.shieldDefense = unit.getInt("shieldDefense");
        this.turnToProduce = unit.getInt("turnToProduce");
        this.charge = unit.getInt("charge");
        this.cost = unit.getInt("cost");

        JSONArray tempSpecialAbility = unit.getJSONArray("specialAbility");
        specialAbility = new String[tempSpecialAbility.length()];
        for(int i = 0; i < tempSpecialAbility.length();i++){
            specialAbility[i] = tempSpecialAbility.getString(i);
        }
        
        if(this.type.equals("infantry") || this.type.equals("spearmen")){
            this.maxMovement = 10;
        }
        else if(this.type.equals("cavlary")){
            this.maxMovement = 15;
        }
        else if(this.type.equals("artillery")){
            this.maxMovement = 4;
        }
        movementPoints = maxMovement;
    }

    /**
     * Resets the unit's movement points
     */
    public void resetUnit() {
        movementPoints = maxMovement;
        hasAttacked = false;
        isBroken = false;
    }

    public int getNumTroops() {
        return this.numTroops;
    }

    public void killTroops(int numCasualties) {
        this.numTroops -= numCasualties;
    }

    public String getRange() {
        return this.range;
    }

    public String getType() {
        return this.type;
    }

    public int getArmour() {
        return this.armour;
    }

    public String getName(){
        return this.name;
    }

    public int getMorale() {
        return this.morale;
    }

    public int getSpeed() {
        return this.speed;
    }

    public int getAttack() {
        return (isBroken) ? 0 : this.attack; // cant deal damage if routing
    }

    public int getMeleeAttack() {
        return getAttack() + getCharge();
    }

    /**
     * @return The missle attack damage for this unit. (melee units deal 0
     *         missle attack damage)
     */
    public int getMissleAttack() {
        return getRange().equals("melee") ? 0 : getAttack();
    }

    /**
     * @return the base chance for this unit to be in a ranged encounter.
     */
    public int getBaseRangedChance() {
        return (getRange().equals("missle")) ? 50: 0;
    }

    /**
     * @return the modifying chance for this unit to be in a ranged encounter,
     *         depending on the units speed.
     */
    public int getRangedModifier() {
        return (getRange().equals("missle")) ? speed: -1*speed;
    }

    public int getDefenseSkill() {
        return this.defenseSkill;
    }

    public int getShieldDefense() {
        return this.shieldDefense;
    }

    public String[] getSpecialAbility() {
        return this.specialAbility;
    }

    public int getTurnToProduce() {
        return this.turnToProduce;
    }

    public int getCharge() {
        return (isBroken) ? 0 : this.charge;
    }

    public int getNumEngagement() {
        return this.numEngagement;
    }

    public void setNumEngagement(int numEngagement) {
        this.numEngagement = numEngagement;
    }

    public int getCost() {
        return this.cost;
    }

    public int getMovement() {
        if (hasAttacked()) return 0; // units used to invade cant be moved.
        return movementPoints;
    }

    public void deductMovement(int numPoints) {
        this.movementPoints -= numPoints;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean isBroken) {
        this.isBroken = isBroken;
    }

    public boolean hasAttacked() {
        return hasAttacked;
    }

    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }
    
    @Override
    public String toString() {
        String s = getName()+ " (" + getRange() + "): N=" + getNumTroops();
        if (!hasAttacked) {
            s += ", MP=" + getMovement();
        } else {
            s += " (has attacked)";
        }
        return s;
    }
}
