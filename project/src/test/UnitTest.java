package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.units.Unit;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;

public class UnitTest{
    String unitPath = "src/configs/units.json";
    String buildingPath = "src/configs/buildings.json";

    @Test
    public void testCreateUnit()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory(unitPath);
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        assertEquals(f.getTreasury().getBalance(), 100);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory(buildingPath);
        Province p = new Province("I", t, factory);

        Unit legionaire = u.getUnit("legionaire");
        f.addProvince(p);
        p.setFaction(f);
        p.hireUnits(f.getTreasury(), legionaire);

        assertEquals(f.getTreasury().getBalance(), 90);

        Unit chariot = u.getUnit("chariot");
        boolean chariotB = p.hireUnits(f.getTreasury(), chariot);
        assertTrue(chariotB);

        Unit peasant = u.getUnit("peasant");
        boolean peasantB = p.hireUnits(f.getTreasury(), peasant);

        assertFalse(peasantB);
        f.getTreasury().add(100);
        f.trainUnit(p, "peasant");
    }
    @Test
    public void testEmptyTreasury()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory(unitPath);
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        assertEquals(f.getTreasury().getBalance(), 100);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory(buildingPath);
        Province p = new Province("I", t, factory);

        Unit legionaire = u.getUnit("legionaire");
        f.addProvince(p);
        p.setFaction(f);

        f.getTreasury().remove(99);
        assertFalse(p.hireUnits(f.getTreasury(), legionaire));

    }

    @Test
    public void testUnitFactory()throws IOException{
        UnitFactory u = new UnitFactory(unitPath);
        String content = Files.readString(Paths.get(unitPath));
        JSONObject objects = new JSONObject(content);
        ArrayList<Unit> units = new ArrayList<Unit>();
        JSONArray key = objects.names();
        for (int i = 0; i < key.length (); ++i) {
            String keys = key.getString(i);
            Unit temp = u.getUnit(keys);
            units.add(temp);
        };
        assertEquals(units.size(), key.length());
    }
    
    @Test
    public void testNull()throws IOException{
        UnitFactory u = new UnitFactory(unitPath);
        Unit nuller = u.getUnit(null);
        assertTrue(nuller == null);
    }

    @Test
    public void testSimpleMethod()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory(unitPath);
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        assertEquals(f.getTreasury().getBalance(), 100);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory(buildingPath);
        Province p = new Province("I", t, factory);

        Unit legionaire = u.getUnit("legionaire");
        f.addProvince(p);
        p.setFaction(f);

        assertEquals(legionaire.getMovement(), 10);
        legionaire.deductMovement(4);
        assertEquals(legionaire.getMovement(), 6);

        assertEquals(legionaire.getNumTroops(), 10);
        legionaire.killTroops(3);
        assertEquals(legionaire.getNumTroops(), 7);

        legionaire.resetUnit();
        assertEquals(legionaire.getMovement(), 10);
    }

    @Test 
    public void testUnitHut()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory(unitPath);
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        assertEquals(f.getTreasury().getBalance(), 100);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory(buildingPath);
        Province p = new Province("I", t, factory);
        f.addProvince(p);
        p.setFaction(f);

        Unit legion = u.getUnit("legionaire");
        Unit chariot = u.getUnit("chariot");
        p.hireUnits(f.getTreasury(), chariot);
        p.hireUnits(f.getTreasury(), legion);

        t.setTurn(3);
        assertEquals(p.getArmy().size(), 2);
    }
}

