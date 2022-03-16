package test;

//import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.CoastProvince;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.Treasury;
import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.exceptions.InfrastructureException;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;


public class WealthTest {
    
    @Test 
    public void TreasuryTest(){
        Treasury test = new Treasury(100);
        assertEquals(test.getBalance(), 100);
    }

    @Test
    public void TestNegativeTreasury(){
        Treasury test = new Treasury(-10);
        assertEquals(test.getBalance(),0);
    }

    @Test 
    public void TestAdd(){
        Treasury test = new Treasury(10);
        assertEquals(test.getBalance(),10);
        test.add(100);
        assertEquals(test.getBalance(),110);
    }

    @Test
    public void TestRemove(){
        Treasury test = new Treasury(10);
        assertEquals(test.getBalance(),10);
        test.remove(9);
        assertEquals(test.getBalance(),1);
    }

    @Test
    public void TestInitialTax() throws IOException{
        Turn t = new Turn();
        BuildingFactory b = new BuildingFactory();
        Province p = new Province("I", t, b);
        double rate = p.getTaxRate();
        String buff = p.getTaxBuff();
        assertEquals(rate, 0.10, 0.01);
        assertEquals(buff, "Low Tax");
    }

    @Test
    public void TestTaxChange() throws IOException{
        Turn t = new Turn();
        BuildingFactory b = new BuildingFactory();
        Province p = new Province("I", t, b);
        
        assertEquals(p.getTaxRate(), 0.10, 0.01);
        assertEquals(p.getTaxBuff(), "Low Tax");

        p.setTaxRate("Normal Tax");
        assertEquals(p.getTaxRate(), 0.15, 0.01);
        assertEquals(p.getTaxBuff(), "Normal Tax");

        p.setTaxRate("High Tax");
        assertEquals(p.getTaxRate(), 0.20, 0.01);
        assertEquals(p.getTaxBuff(), "High Tax");

        p.setTaxRate("Very High Tax");
        assertEquals(p.getTaxRate(), 0.25, 0.01);
        assertEquals(p.getTaxBuff(), "Very High Tax");

        p.setTaxRate("Low Tax");
        assertEquals(p.getTaxRate(), 0.10, 0.01);
        assertEquals(p.getTaxBuff(), "Low Tax");
    }

    @Test
    public void TestGetProvince()throws IOException{
        Turn t = new Turn();
        BuildingFactory b = new BuildingFactory();
        Province p = new Province("I", t, b);
        int wealth = p.getProvinceWealth();
        assertEquals(wealth, 0);

        int growth = p.getProvinceGrowth();
        assertEquals(growth, 10);

        p.setTaxRate("Normal Tax");
        growth = p.getProvinceGrowth();
        assertEquals(growth, 0);

        p.setTaxRate("High Tax");
        growth = p.getProvinceGrowth();
        assertEquals(growth, -10);

        p.setTaxRate("Very High Tax");
        growth = p.getProvinceGrowth();
        assertEquals(growth, -30);
    }

    @Test
    public void testTaxProvince() throws IOException, ConditionsParseException{
        var winCond = VictoryFactory.getVictoryCondition();
        UnitFactory u = new UnitFactory();
        Faction f = new Faction("Rome", 100, u, winCond);
        Turn t = new Turn();
        BuildingFactory b = new BuildingFactory();
        Province p = new Province("I", t, b);
        f.addProvince(p);
        assertEquals(f.getTreasury().getBalance(), 100);
        // first endTurn increases wealth AFTER taxation
        f.endTurn();
        // So need to endTurn twice before treasury increases if no buildings
        // are built.
        f.endTurn();
        assertEquals(f.getTreasury().getBalance(), 101);
    }

    @Test 
    public void testGetFactionWealth() throws IOException, ConditionsParseException{
        var winCond = VictoryFactory.getVictoryCondition();
        UnitFactory u = new UnitFactory();
        Faction f = new Faction("Rome", 100, u, winCond);
        Turn t = new Turn();
        BuildingFactory b = new BuildingFactory();
        Province p = new Province("I", t, b);
        assertEquals(p.getProvinceWealth(), 0);
        Province o = new Province("II", t, b);
        assertEquals(o.getProvinceWealth(), 0);
        Province q = new Province("III", t, b);
        assertEquals(q.getProvinceWealth(), 0);
        f.addProvince(p);
        f.addProvince(o);
        f.addProvince(q);

        p.setProvinceWealth(100);
        o.setProvinceWealth(20);
        q.setProvinceWealth(3);
        int total = f.getFactionWealth();
        assertEquals(total, 123);
    }

    @Test
    public void testCoastWealth()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory();
        CoastProvince p = new CoastProvince("I", t, factory);
        f.addProvince(p);
        p.setFaction(f);

        p.getMarket().upgrade();
        p.getMine().upgrade();
        p.getPort().upgrade();

        assertEquals(p.getCoastWealthBonus(), 5);
        assertEquals(p.getCoastGrowthBonus(), 5);
        assertEquals(p.getProvinceGrowth(), 35);
        assertEquals(p.getProvinceWealth(), 85);
    }

    @Test
    public void testUpgradeBuilding()throws IOException, ConditionsParseException, InfrastructureException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 10, u, winCond);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory();
        Province p = new Province("I", t, factory);
        f.addProvince(p);
        p.setFaction(f);

        //False since market cost 60 and mine cost 50 so not enough money
        boolean a = p.upgradeBuilding("Mine");
        f.getTreasury().add(1000);
        boolean b = p.upgradeBuilding("Market");
        assertFalse(a);
        assertTrue(b);
    }

    @Test
    public void testGetterCost()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 10000, u, winCond);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory();
        Province p = new Province("I", t, factory);
        f.addProvince(p);
        p.setFaction(f);
        p.getMarket().upgrade();
        p.getMine().upgrade();
        int a = p.getUpgradeCost("Mine");
        int b = p.getUpgradeTime("Market");
        assertEquals(b, 5);
        assertEquals(a, 88);
    }
}