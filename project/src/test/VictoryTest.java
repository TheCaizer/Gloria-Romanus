package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.AndCondition;
import unsw.gloriaromanus.backend.victoryConditions.OrCondition;
import unsw.gloriaromanus.backend.victoryConditions.TreasuryCondition;
import unsw.gloriaromanus.backend.victoryConditions.VictoryCondition;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;
import unsw.gloriaromanus.backend.victoryConditions.WealthCondition;

public class VictoryTest {

    @Test
    public void testShowVictory()throws IOException, ConditionsParseException {
        var winCond = VictoryFactory.getVictoryCondition();
        UnitFactory u = new UnitFactory();
        Faction f = new Faction("Rome", 100, u, winCond);

        System.out.println(winCond.getString(f, 0));

        //testing default cond
        String result = "AND: false\n    TREASURY: false\n    OR: false\n        CONQUEST: false\n        WEALTH: false";
        assertEquals(result, winCond.getString(f, 0));
    }

    @Test
    public void testOrCheck()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        

        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 1, u, winCond);

        ArrayList<VictoryCondition> conds = new ArrayList<VictoryCondition> ();
        conds.add(new WealthCondition());
        conds.add(new TreasuryCondition());
        VictoryCondition o = new OrCondition(conds);

        assertFalse(o.check(f));

        f.getTreasury().add(100000);
        assertTrue(o.check(f));
    }

    @Test 
    public void testAndCheck()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        Turn t = new Turn();
        BuildingFactory factory = new BuildingFactory();
        Province province = new Province("I", t, factory);
        province.setFaction(f);


        ArrayList<VictoryCondition> conds = new ArrayList<VictoryCondition> ();
        conds.add(new WealthCondition());
        conds.add(new TreasuryCondition());
        VictoryCondition a = new AndCondition(conds);

        assertFalse(a.check(f));
        
        f.getTreasury().add(100000);
        assertFalse(a.check(f));
        f.getProvince("I").setProvinceWealth(400000);
        assertTrue(a.check(f));
        // checking condition can be unset
        f.getProvince("I").setProvinceWealth(0);
        assertFalse(a.check(f));
        f.getProvince("I").setProvinceWealth(400000);
        assertTrue(a.check(f));
        // checking condition can be disabled
        a.disable();
        assertFalse(a.check(f));
    }

    @Test
    public void testGenConditions()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);

        // Seeding specific random so I can check cond.
        JSONObject json = VictoryFactory.generateRandomConditions(new Random(4));
        System.out.println(json.toString(4));
        //Check that it can be correctly converted into a Composite
        var c = VictoryFactory.getVictoryCondition(json);
        System.out.println(c.getString(f, 0));
        assertFalse(c.check(f));
        f.getTreasury().add(100000);
        assertTrue(c.check(f));
        
    }

    @Test
    //Since when making a faction there is no province
    //So technlically a defeat
    public void testDefeat()throws IOException, ConditionsParseException{
        UnitFactory u = new UnitFactory();
        var winCond = VictoryFactory.getVictoryCondition();
        Faction f = new Faction("Rome", 100, u, winCond);
        assertTrue(f.checkDefeat());
    }
}
