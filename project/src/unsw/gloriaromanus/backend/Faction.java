package unsw.gloriaromanus.backend;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import javafx.beans.property.IntegerProperty;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.VictoryCondition;

public class Faction implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private HashMap<String, Province> factionMap;
    private String name;
    private Treasury treasury;
    private UnitFactory factory;
    private VictoryCondition winCondition;
    
    public Faction(String name, int initialBalance, UnitFactory factory, VictoryCondition winCondition) {
        this.name = name;
        factionMap = new HashMap <String, Province>();
        treasury = new Treasury(initialBalance);
        this.factory = factory;
        this.winCondition = winCondition;
    }


    /*
    * @return true if all conditon has been achived
    * @return false if not
    */
    public boolean checkVictory(){
        return winCondition.check(this);
    }

    /**
     * Disables the win condition for all players.
     */
    public void disableVictory() {
        winCondition.disable();
    }

    /*
    * @return true if there are no province in your faction
    * @return false if you still have provinces
    */
    public boolean checkDefeat(){
        if(factionMap.size() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Inserts a province into this faction's graph.
     * @param province A province
     */
    public void addProvince(Province province) {
        factionMap.put(province.getName(), province);
    }

    /**
     * Removes a province from this faction's graph.
     * @param province A province
     */
    public void removeProvince(Province province) {
        factionMap.remove(province.getName());
    }

    public Province getProvince(String name) {
        return factionMap.get(name);
    }

    public Collection<Province> getProvinces() {
        return factionMap.values();
    }

    public boolean trainUnit(Province province, String unitName) {
        return province.hireUnits(treasury, factory.getUnit(unitName));
    }

    public int getTrainingCost(String unitName) {
        int baseCost = factory.getUnit(unitName).getCost();
        return adjustUnitCost(baseCost);
    }

    public int getTrainingTime(String unitName) {
        return factory.getUnit(unitName).getTurnToProduce();
    }

    public int adjustConstrCost(int baseCost) {
        for (Province province : factionMap.values()) {
            double reduct = 100 - province.getConstrCostRed();
            reduct = reduct / 100;
            baseCost *= reduct;
        }
        int result = (int) Math.round(baseCost);
        
        if (result < 1) return 1; // lower bound of 1
        return result;
    }

    public int adjustConstrTime(int baseTime){
        for (Province province : factionMap.values()) {
            baseTime -= province.getConstrTimeRed();
        }
        if (baseTime < 1) return 1; // lower bound of 1
        return baseTime;
    }

    public int adjustUnitCost(int baseCost) {
        for (Province province : factionMap.values()) {
            double reduct = 100 - province.getUnitCostRed();
            reduct = reduct / 100;
            baseCost *= reduct;
        }
        int result = (int) Math.round(baseCost);
        if (result < 1) return 1; // lower bound of 1
        return result;
    }

    public int getCoastWealthBonus() {
        int result = 0;
        for (Province province : factionMap.values()) {
            if (province instanceof CoastProvince) {
                result += ((CoastProvince) province).getCoastWealthBonus();
            }
        }
        return result;
    }

    public int getCoastGrowthBonus() {
        int result = 0;
        for (Province province : factionMap.values()) {
            if (province instanceof CoastProvince) {
                result += ((CoastProvince) province).getCoastGrowthBonus();
            }
        }
        return result;
    }

    /**
     * Function to trigger end of turn actions for a faction.
     * Taxes all provinces, adjust province wealth, resets province invasion state
     * and checks victory conditions.
     * @return True if this faction satisfies all win conditions.
     */
    public boolean endTurn() {
        taxProvinces();
        for (Province province: factionMap.values()) {
            province.onTurnEnd();
        }
        return checkVictory();
    }

    /**
     * @return The number of provinces owned by this faction.
     */
    public int getFactionSize() {
        return factionMap.size();
    }

    /*
    * At the start of turn goes to each province and gets the treasury welath 
    * equal to province tax * province wealth
    */
    public  void taxProvinces() {
        for(Province p: factionMap.values()) {
            int tax = (int) Math.round(p.getTaxRate() * p.getProvinceWealth());
            treasury.add(tax); 
        }
    }
    /*
    * gets the sum of all the wealth in the faction not wealth growth
    * @return the sum of all proivnce wealth
    */
    public int getFactionWealth(){
        int result = 0;
        for(Province p: factionMap.values()){
            result += p.getProvinceWealth();
        }
        return result;
    }

    /*
    * @return the faction map
    */
    public HashMap <String, Province> getFactionMap(){
        return this.factionMap;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /*
    * @return the treasury
    */
    public Treasury getTreasury(){
        return this.treasury;
    }

    /**
     * @return treasury balance
     */
    public int getBalance() {
        return treasury.getBalance();
    }

        /**
     * @return treasury balance property
     */
    public IntegerProperty getBalanceProperty() {
        return treasury.getBalanceProperty();
    }
    
    /*
    * @return unitFactory
    */
    public UnitFactory getUnitFactory(){
        return this.factory;
    }

    public String getVictoryString() {
        return winCondition.getString(this, 0);
    }

    
}
