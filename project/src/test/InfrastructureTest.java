package test;

//import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.exceptions.InfrastructureException;
import unsw.gloriaromanus.backend.infrastructure.BuildersHut;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.infrastructure.Market;
import unsw.gloriaromanus.backend.infrastructure.Mine;
import unsw.gloriaromanus.backend.infrastructure.Port;
import unsw.gloriaromanus.backend.infrastructure.Road;


public class InfrastructureTest {
    // This string matches the buildings.json config file
    // Basic building construction
    @Test
    public void marketTest() throws IOException {
        JSONObject json =  new JSONObject( Files.readString(Paths.get("src/configs/buildings.json")));
        BuildingFactory factory = new BuildingFactory(json);
        JSONArray marketJson = json.getJSONArray("Market");

        // Building an empty (level 0) market
        // Provides no stat bonuses, only upgrade time and cost;
        Market market = new Market(factory);

        assertBuildingLevel(market, 0, marketJson);

        // Constructing basic market
        market.upgrade();

        assertBuildingLevel(market, 1, marketJson);

        // // Testing each upgrade
        for (int i = market.getLevel() + 1; !market.isMaxLevel(); i++) {
            market.upgrade();
            assertBuildingLevel(market, i, marketJson);
        }

        int maxLevel = market.getLevel();
        market.upgrade();
        // checking that Market cannot be upgraded beyond max level.
        assertBuildingLevel(market, maxLevel, marketJson);
    }

    @Test
    public void mineTest() throws IOException {
        JSONObject json =  new JSONObject( Files.readString(Paths.get("src/configs/buildings.json")));
        BuildingFactory factory = new BuildingFactory(json);
        JSONArray mineJson = json.getJSONArray("Mine");

        Mine mine = new Mine(factory);
        assertBuildingLevel(mine, 0, mineJson);
        mine.upgrade();
        assertBuildingLevel(mine, 1, mineJson);

        // Testing each upgrade
        for (int i = mine.getLevel() + 1; !mine.isMaxLevel(); i++) {
            mine.upgrade();
            assertBuildingLevel(mine, i, mineJson);
        }

        int maxLevel = mine.getLevel();
        mine.upgrade();
        // checking max level
        assertBuildingLevel(mine, maxLevel, mineJson);
    }

    @Test
    public void roadTest() throws IOException {
        JSONObject json =  new JSONObject( Files.readString(Paths.get("src/configs/buildings.json")));
        BuildingFactory factory = new BuildingFactory(json);
        JSONArray roadJson = json.getJSONArray("Road");

        Road road = new Road(factory);
        assertBuildingLevel(road, 0, roadJson);
        road.upgrade();
        assertBuildingLevel(road, 1, roadJson);
        road.upgrade();
        assertBuildingLevel(road, 2, roadJson);
        assertTrue(road.isMaxLevel());
        // Checking can't be upgraded further
        road.upgrade();
        assertBuildingLevel(road, 2, roadJson);
        assertTrue(road.isMaxLevel());
    }

    @Test
    public void portTest() throws IOException {
        JSONObject json =  new JSONObject( Files.readString(Paths.get("src/configs/buildings.json")));
        BuildingFactory factory = new BuildingFactory(json);
        JSONArray portJson = json.getJSONArray("Port");

        Port port = new Port(factory);
        assertBuildingLevel(port, 0, portJson);
        port.upgrade();
        assertBuildingLevel(port, 1, portJson);
        
        // Testing each upgrade
        for (int i = port.getLevel() + 1; !port.isMaxLevel(); i++) {
            port.upgrade();
            assertBuildingLevel(port, i, portJson);
        }

        int maxLevel = port.getLevel();
        port.upgrade();
        // checking max level
        assertBuildingLevel(port, maxLevel, portJson);
    }

    @Test
    // Testing delayed construction
    public void buildersHutTest() throws IOException, InfrastructureException {
        JSONObject json =  new JSONObject( Files.readString(Paths.get("src/configs/buildings.json")));
        BuildingFactory factory = new BuildingFactory(json);
        JSONArray marketJson = json.getJSONArray("Market");
        Turn turn = new Turn();

        BuildersHut hut = new BuildersHut(turn);
        Market market = new Market(factory);
        int upgradeTime = market.getUpgradeTime();

        // Successfully start construction
        assertTrue(hut.constructBuilding(market, turn.getTurnNum() + upgradeTime));
        assertBuildingLevel(market, 0, marketJson);

        // Progress in time - building not yet constructed
        turn.setTurn(turn.getTurnNum() + market.getUpgradeTime() -1);
        assertBuildingLevel(market, 0, marketJson);

        Mine mine = new Mine(factory);

        // Unsuccessfully start new construction: already one taking place
        assertFalse(hut.constructBuilding(mine, 1));

        turn.increment();
        // Building should now be constructed
        assertBuildingLevel(market, 1, marketJson);

        // Can now start a new construction
        assertTrue(hut.constructBuilding(mine, 1));

        // cancelling construction;
        hut.cancel();
        
        // Upgrade mine to max level
        while (!mine.isMaxLevel()) mine.upgrade();

        // Cant upgrade max level building
        assertFalse(hut.constructBuilding(mine, 1));
        assertTrue(hut.constructBuilding(new Port(factory), 1));

    }


    //helper functions
    /**
     * Checks that a building is of a certain level, as defined by a json array.
     */
    private void assertBuildingLevel(Market market, int level, JSONArray marketJson) {
        assertEquals(market.getLevel(), marketJson.getJSONObject(level).getInt("level"));
        assertEquals(market.getWealthBonus(), marketJson.getJSONObject(level).getInt("wealthBonus"));
        assertEquals(market.getGrowthBonus(), marketJson.getJSONObject(level).getInt("growthBonus"));
        assertEquals(market.getConstrCostRed(), marketJson.getJSONObject(level).getInt("factionConstrWealthCostReduction"));

        if (!market.isMaxLevel()) {
            assertEquals(market.getUpgradeCost(), marketJson.getJSONObject(level + 1).getInt("baseWealthCost"));
            assertEquals(market.getUpgradeTime(), marketJson.getJSONObject(level + 1).getInt("baseTurnCost"));
        } else {
            assertEquals(market.getUpgradeCost(), 0);
            assertEquals(market.getUpgradeTime(), 0);
        }
    }

    private void assertBuildingLevel(Mine mine, int level, JSONArray mineJson) {
        assertEquals(mine.getLevel(), mineJson.getJSONObject(level).getInt("level"));
        assertEquals(mine.getWealthBonus(), mineJson.getJSONObject(level).getInt("wealthBonus"));
        assertEquals(mine.getGrowthBonus(), mineJson.getJSONObject(level).getInt("growthBonus"));
        assertEquals(mine.getSoldierWealthCostReduction(), mineJson.getJSONObject(level).getInt("factionSoldierWealthCostReduction"));
        assertEquals(mine.getConstrTurnCostReduction(), mineJson.getJSONObject(level).getInt("factionConstrTurnCostReduction"));
        if (!mine.isMaxLevel()) {
            assertEquals(mine.getUpgradeCost(), mineJson.getJSONObject(level + 1).getInt("baseWealthCost"));
            assertEquals(mine.getUpgradeTime(), mineJson.getJSONObject(level + 1).getInt("baseTurnCost"));
        } else {
            assertEquals(mine.getUpgradeCost(), 0);
            assertEquals(mine.getUpgradeTime(), 0);
        }
    }


    private void assertBuildingLevel(Road road, int level, JSONArray json) {
        assertEquals(road.getLevel(), json.getJSONObject(level).getInt("level"));
        assertEquals(road.getType(), json.getJSONObject(level).getString("type"));
        assertEquals(road.getMovementCost(), json.getJSONObject(level).getInt("provinceMovementCost"));
        
        if (!road.isMaxLevel()) {
            assertEquals(road.getUpgradeCost(), json.getJSONObject(level + 1).getInt("baseWealthCost"));
            assertEquals(road.getUpgradeTime(), json.getJSONObject(level + 1).getInt("baseTurnCost"));
        } else {
            assertEquals(road.getUpgradeCost(), 0);
            assertEquals(road.getUpgradeTime(), 0);
        }
    }

    private void assertBuildingLevel(Port port, int level, JSONArray json) {
        assertEquals(port.getLevel(), json.getJSONObject(level).getInt("level"));
        assertEquals(port.getWealthBonus(), json.getJSONObject(level).getInt("wealthBonus"));
        assertEquals(port.getGrowthBonus(), json.getJSONObject(level).getInt("growthBonus"));
        assertEquals(port.getCoastWealthBonus(), json.getJSONObject(level).getInt("coastWealthBonus"));
        assertEquals(port.getCoastGrowthBonus(), json.getJSONObject(level).getInt("coastGrowthBonus"));

        if (!port.isMaxLevel()) {
            assertEquals(port.getUpgradeCost(), json.getJSONObject(level + 1).getInt("baseWealthCost"));
            assertEquals(port.getUpgradeTime(), json.getJSONObject(level + 1).getInt("baseTurnCost"));
        } else {
            assertEquals(port.getUpgradeCost(), 0);
            assertEquals(port.getUpgradeTime(), 0);
        }
    }
}