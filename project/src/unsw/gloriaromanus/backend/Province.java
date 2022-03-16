package unsw.gloriaromanus.backend;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import unsw.gloriaromanus.backend.exceptions.DefeatException;
import unsw.gloriaromanus.backend.exceptions.InfrastructureException;
import unsw.gloriaromanus.backend.exceptions.InvasionException;
import unsw.gloriaromanus.backend.exceptions.MovementException;
import unsw.gloriaromanus.backend.infrastructure.*;
import unsw.gloriaromanus.backend.units.Unit;
import unsw.gloriaromanus.backend.units.UnitHut;

public class Province implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private int wealth = 10;
    private transient  ObjectProperty<Faction> factionProperty;
    //private Faction faction = null;
    //private ArrayList<Unit> units;
    private transient ListProperty<Unit> unitsProperty;
    private ArrayList<Province> adjacent;
    private BuildersHut hut; // Upgrades buildings in this province

    private double taxRate; //taxRate of province
    private String taxBuff; //taxbuff depending on the taxRate
    private UnitHut[] unitHuts; // array of unit constructors with fixed size
    private HashMap <String, Building> buildings;
    private boolean hasBeenInvaded = false; //whether this province was conquered on the current turn

    public Province(String name, Turn turn, BuildingFactory buildingFactory) {
        factionProperty = new SimpleObjectProperty<Faction>(null);
        unitsProperty = new SimpleListProperty<Unit>(FXCollections.observableArrayList());

        this.name = name;
        buildings = new HashMap<String, Building>();
        buildings.put("Market", new Market(buildingFactory));
        buildings.put("Road", new Road(buildingFactory));
        buildings.put("Mine", new Mine(buildingFactory));

        taxRate = 0.10;
        taxBuff = "Low Tax";

        //units = new ArrayList<Unit>();


        adjacent = new ArrayList<Province>();
        hut = new BuildersHut(turn);
        unitHuts = new UnitHut[]{new UnitHut(turn, unitsProperty), 
                                 new UnitHut(turn, unitsProperty)};
    }

    // public void bindFaction(ObjectProperty<Faction> factionProperty) {
    //     if (this.factionProperty == null) this.factionProperty = new SimpleObjectProperty<Faction>(this.faction);
    //     factionProperty.bind(this.factionProperty);
    // }

    public void addFactionListener(ChangeListener<Faction> listener) {
        factionProperty.addListener(listener);
    }


    /**
     * Move a group of units from this province to another, adjusting every units
     * movement points.
     * @param group a group of 1 or more units.
     * @param dest The destination province
     * @return true if units were moved, otherwise false;
     * @throws Exception if no valid path exists between this province and the destination.
     */
    public boolean moveUnits(List<Unit> group, Province dest) throws MovementException {
        if (group.size() == 0 || dest == this) return true; 
        int minMovement = group.get(0).getMovement();
        for (Unit unit : group) {
            if (unit.getMovement() < minMovement)
                minMovement = unit.getMovement();
        }

        int distance = getShortestDistTo(dest);
        if (distance <= minMovement) {
            group.forEach((x) -> x.deductMovement(distance));
            dest.getArmy().addAll(group);
            unitsProperty.removeAll(group);
            return true;
        }
        throw new MovementException("Some Selected units dont have enough movement points");
    }

    /**
     * Finds the distance from this province to the destination province.
     * @param dest the destination province.
     * @return the number of movement points required to move a unit to this province.
     * @throws Exception If no valid path exists between this province and the destination.
     */
    public int getShortestDistTo(Province dest) throws MovementException {
        if (getFaction() != dest.getFaction()) {
            throw new MovementException("You can only move troops to your own provinces");
        }
        if (hasBeenInvaded() || dest.hasBeenInvaded()) {
            throw new MovementException("You can't move troops to or from a province you've conquered this turn");
        }

        HashMap<Province, Integer> dist = new HashMap<Province, Integer>();
        HashSet<Province> visited = new HashSet<Province>();

        Comparator<Province> byDist = (Province o1, Province o2)
                -> dist.get(o1) - dist.get(o2);

        PriorityQueue<Province> vertices = new PriorityQueue<Province>(byDist);

        dist.put(this, 0);
        vertices.add(this);
        Province currV = null;
        while (!vertices.isEmpty() && dest != (currV = vertices.poll())) {
            for (Province w : currV.getAdjacent()) {
                if (!visited.contains(w) && w.getFaction() == getFaction()) {
                    int newDist = dist.get(currV) + currV.getMovementCost();
                    if (!dist.containsKey(w) || newDist < dist.get(w)) {
                        vertices.remove(w);
                        dist.put(w, newDist);
                        vertices.add(w);
                    }
                }
            }
            visited.add(currV);
        }
        // vertex was never reached - seperated by enemy provinces
        if (currV != dest) {
            throw new MovementException("There is no path between these provinces");
        }
        return dist.get(dest);
    }

    /**
     * Sets this provinces faction, adding itself to the factions factionMap.
     * Removes itself from previous faction. Cancels any construction/
     * unit training. removes all defending units
     * @param faction the faction to set
     */
    public void setFaction(Faction faction) {
        if (getFaction() != null) {
            // Post Invasion
            getFaction().removeProvince(this);
            hasBeenInvaded = true;
            hut.cancel();
            for(UnitHut unitHut : unitHuts) {
                unitHut.cancel();
            }
            unitsProperty.clear();
        }
        faction.addProvince(this);
        factionProperty.set(faction);
    }

    /**
     * @return the faction
     */
    public Faction getFaction() {
        return factionProperty.get();
    }

    


    /**
     * adds a province to to this provinces adjacency list.
     * Only used for gameData Initialisation.
     * @param province an adjacent province.
     */
    public void addAdjacent(Province province) {
        adjacent.add(province);
    }

    /**
     * @return a list of adjacent provinces.
     */
    private ArrayList<Province> getAdjacent() {
        return adjacent;
    }

    /**
     * @return the province name
     */
    public String getName() {
        return name;
    }

    /**
     * (Sum of variable wealth + base wealth from buildings)
     * @return The total wealth of this province.
     */
    public int getProvinceWealth() {
        int result = wealth; 
        for (Building building : buildings.values()) {
            if (building instanceof WealthGenerating) {
                result += ((WealthGenerating) building).getWealthBonus();
            }
        }
        return result;
    }

    public int getProvinceGrowth() {
        int result = 0;
        int bonus = 0;
        for (Building building : buildings.values()) {
            if (building instanceof WealthGenerating) {
                result += ((WealthGenerating) building).getGrowthBonus();
            }
        }
        if(this.taxBuff.equals("Low Tax")){
            bonus = 10;
        }
        else if(this.taxBuff.equals("High Tax")){
            bonus = -10;
        }
        else if(this.taxBuff.equals("Very High Tax")){
            bonus = -30;
        }
        return result + bonus;
    }

    /**
     * Upgrades the building in this province correspoinding to the building key
     * if the faction can afford the cost and no other construction is taking
     * place.
     * @param buildingKey the building key string
     * @return true if construction commences.
     */
    public boolean upgradeBuilding(String buildingKey) throws InfrastructureException  {
        Building building = buildings.get(buildingKey);
        if (building != null) return upgradeBuilding(getTreasury(), building);
        return false;
    }

    /**
     * @param buildingKey the building key string
     * @return The cost of construction/upgrading the building.
     */
    public int getUpgradeCost(String buildingKey) {
        Building building = buildings.get(buildingKey);
        if (building == null) return 0;
        return getUpgradeCost(building);
    }

    /**
     * @param buildingKey the building key string
     * @return The number of turns to construct/upgrade the building.
     */
    public int getUpgradeTime(String buildingKey) {
        Building building = buildings.get(buildingKey);
        if (building == null) return 0;
        return getUpgradeTime(building);
    }

    /**
     * starts recruiting the troop if possible and remove cost from treasury
     * @param treasury treasury to charge for construction
     * @param unit the unit it wants to make
     * @return false if the treasury does not contain enough gold, or if 
     *         there are no free slots. otherwise true.
     */
    public boolean hireUnits(Treasury treasury, Unit unit){
        int cost = getFaction().adjustUnitCost(unit.getCost());
        // Check faction has enough wealth
        if (treasury.getBalance() < cost) return false;

        for (UnitHut unitHut : unitHuts) {
            if(unitHut.constructUnit(unit)) {
                treasury.remove(cost);
                return true;
            }
        }
        // All unitHuts are full
        return false;
    }


    // Invasion methods


    /**
     * Initiate a battle between this province and the target province.
     * @param province the province to attack
     * @param logger optional observer object to log battle progress
     * @throws Exception if the target province cannot be attack from this province
     */
    public void invadeProvince(List<Unit>attackingArmy, Province province, boolean raidMode,TextLogger logger) throws InvasionException, DefeatException {
        if (!adjacent.contains(province)) {
            throw new InvasionException("Can only attack adjacent provinces");
        } 
        if (province.getFaction() == getFaction()) {
            throw new InvasionException("Can only invade enemy provinces");
        }
        if (hasBeenInvaded()) {
            throw new InvasionException("Cannot invade from a province conquered in the current turn");
        }
        //Checking that Some of the units can attack
        int numEffective = 0;
        for (Unit u : attackingArmy) {
            if (!u.hasAttacked()) numEffective++;
        }
        if (numEffective < 1) {
            throw new InvasionException("You havent selected any valid units");
        }


        BattleResolver resolver = new BattleResolver(this, attackingArmy, province);
        if (logger != null) resolver.attachObserver(logger);
        if (raidMode) resolver.initiateRaid();
        else resolver.initiateBattle();
    }

    /**
     * @return the moral penalty due to tax rates.
     */
    public int getMoralePenalty() {
        return taxBuff.equals("Very High Tax") ? 1 : 0;
    }

    /**
     * @return a list of all soldiers located at this province.
     */
    public ListProperty<Unit> getArmy() {
        return unitsProperty;
    }
    
    /**
     * @return true if this province has been invaded in the factions current turn.
     */
    public boolean hasBeenInvaded() {
        return hasBeenInvaded;
    }

    /**
     * method to reset state of province at the end of the factions turn. 
     * Adjusts provinces base wealth.
     * resets the state of all units in the province.
     */
    public void onTurnEnd() {
        hasBeenInvaded = false;
        for (Unit unit : unitsProperty) {
            unit.resetUnit();
        }
        wealth += getProvinceGrowth();
        if (wealth < 0) wealth = 0; // lower bound on variable wealth of 0
    }


    // Helper methods

    /**
     * starts infrastructure construction if possible, and removes 
     * construction cost from treasury.
     * @param treasury treasury to charge for construction
     * @param building building to construct/upgrade
     * @return false if the treasury does not contain enough gold, or if 
     *         a building is already undergoing construction.
     *         otherwise true.
     */
    private boolean upgradeBuilding(Treasury treasury, Building building) throws InfrastructureException {
        int cost = getUpgradeCost(building);
        int turns = getUpgradeTime(building);
        if (treasury.getBalance() >= cost 
         && hut.constructBuilding(building, turns)) {
             treasury.remove(cost);
             return true;
        }
        throw new InfrastructureException("Not enough gold to construct " + building.getName());
    }

    public double getTaxRate(){
        return this.taxRate;
    }

    public String getTaxBuff(){
        return this.taxBuff;
    }

    public void setTaxRate(String taxRate) {
        switch(taxRate){
            case "Low Tax":
                this.taxRate = 0.10;
                this.taxBuff = taxRate;
                break;
            case "Normal Tax":
                this.taxRate = 0.15;
                this.taxBuff = taxRate;
                break;
            case "High Tax":
                this.taxRate = 0.20;
                this.taxBuff = taxRate;
                break;
            case "Very High Tax":
                this.taxRate = 0.25;
                this.taxBuff = taxRate;
                break;
        }
    }

    public boolean cancelUnitHire(Unit u, Treasury t) {
        for(UnitHut p: unitHuts){
            if(p.getUnit() == u){
                p.cancel();
                int refund = ((int)((double) getFaction().adjustUnitCost(u.getCost()) * 0.9));
                t.add(refund);
                return true;
            }
        }
        return false;
    }


    private int getUpgradeCost(Building building) {
        return getFaction().adjustConstrCost(building.getUpgradeCost());
    }

    /**
     * @param building the building
     * @return The number of turns to construct/upgrade the building.
     */
    private int getUpgradeTime(Building building) {
        return getFaction().adjustConstrTime(building.getUpgradeTime());
    }

    /**
     * @return the market
     */
    public Market getMarket() {
        //return market;
        return ((Market) buildings.get("Market"));
    }

    /*
    *   @return the unit hut list
    */
    public UnitHut[] getUnitHut(){
        return this.unitHuts;
    }

    /**
     * @return the mine
     */
    public Mine getMine() {
        // return mine;
        return ((Mine) buildings.get("Mine"));
    }

    /**
     * @return the road
     */
    private Road getRoad() {
        // return road;
        return ((Road) buildings.get("Road"));
    }

    public HashMap<String, Building> getBuildings() {
        return buildings;
    }

    public Building getBuildings(String buildingKey) {
        return buildings.get(buildingKey);
    }

    /**
     * Refunds a construction, returning some of the cost.
     * Less cost is returned the longer the construction has gone on for.
     * @return
     */
    public int refundConstruction() {
        Building curr = hut.getCurrentConstruction();
        if (curr == null) return 0;
        int reduction = getUpgradeTime(curr) - hut.getTimeLeft() + 2;
        hut.cancel();
        getTreasury().add(getUpgradeCost(curr)/reduction);
        return getUpgradeCost(curr)  / reduction;
    }


    public int getBuildingRefundAmound(String buildingKey) {
        return getBuildingRefundAmound(getBuildings(buildingKey));
    }

    public int getBuildingRefundAmound(Building building) {
        return getFaction().adjustConstrCost(building.getRefundAmount()) - 1;
    }


    public int destroyBuilding(String buildingKey) {
        return destroyBuilding(getBuildings(buildingKey));
    }


    public int destroyBuilding(Building building) {
        int refund = getBuildingRefundAmound(building);

        getTreasury().add(refund);
        destroyBuildingNoRefund(building);
        return refund;
    }


    public void destroyBuildingNoRefund(Building building) {
        building.destroy();
        if (hut.getCurrentConstruction() == building) hut.cancel();
    }

    //Forwarded methods


    /**
     * @return the cost for a unit to move from this province
     */
    public int getMovementCost() {
        return getRoad().getMovementCost();
    }

    /**
     * @return The multaplicitive construction cost reduction applied to 
     *         all provinces of the same faction.
     */
    public int getConstrCostRed() {
        return getMarket().getConstrCostRed();
    }

    /**
     * @return the scalar construction time reduction applied to all provinces
     *         of the same faction.
     */
    public int getConstrTimeRed() {
        return getMine().getConstrTurnCostReduction();
    }

    /**
     * @return The multaplicitive training cost reduction applied to all provinces
     *         of the same faction. 
     */
    public int getUnitCostRed() {
        return getMine().getSoldierWealthCostReduction();
    }


    public Treasury getTreasury() {
        if (getFaction() == null) return null;
        return getFaction().getTreasury();
    }

    /*
    * mainly for testing getProvinceWealth and getFactionWealth
    *@param the set
    */
    public void setProvinceWealth(int i){
        this.wealth = i;
    }

    public String getCurrentConstructionInfo() {
        Building current = hut.getCurrentConstruction();
        if (current == null) return "";
 
        return current.getName() + ": Lvl " + (current.getLevel() + 1) 
            + " (" + hut.getTimeLeft() + " Turns)";
    }


    /**
     * Custom serialization function for this class. Used by 
     * ObjectOutputStream.writeObject().
     * @param out ObjectStream to write to.
     * @throws IOException if I/O errors occur while writing to the underlying OutputStream
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Taking faction out of property and serializing
        out.writeObject(factionProperty.get());
        out.writeObject(new ArrayList<Unit>(unitsProperty));
    }

    /**
     * Custom deserialization function for this class. Used by 
     * ObjectInputStream.readObject().
     * @param in ObjectStream to read from
     * @throws IOException  if the class of a serialized object could not be found.
     * @throws ClassNotFoundException if an I/O error occurs.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // reading json from stream
        factionProperty = new SimpleObjectProperty<Faction>();
        factionProperty.set((Faction) in.readObject());
        unitsProperty = new SimpleListProperty<Unit>(FXCollections.observableArrayList());
        unitsProperty.addAll((ArrayList<Unit>) in.readObject());
        //unitsProperty.addAll((Unit[]) in.readObject());
        for (UnitHut uhut : unitHuts) {
            uhut.setProvinceArmy(unitsProperty);
        }
    }

}
