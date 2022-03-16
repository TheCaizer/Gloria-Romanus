package test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Random;

import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.BattleResolver;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.TextLogger;
import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.units.Unit;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;


public class BattleResolverTest {
    // @Test
    // public void basicInvasionTest() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province otherDefending = new Province("other", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);

    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(rome);
    //     otherDefending.setFaction(gaul);
    //     // not adjacent
    //     assertThrows(Exception.class, () -> attackingProvince.invadeProvince(defendingProvince,logger));
    //     attackingProvince.addAdjacent(defendingProvince);
    //     // Same faction
    //     assertThrows(Exception.class, () -> attackingProvince.invadeProvince(defendingProvince,logger));

    //     defendingProvince.setFaction(gaul);
    //     assertDoesNotThrow(() -> attackingProvince.invadeProvince(defendingProvince,logger));
    //     // Invasion unsuccessful -> no army (defenders win)
    //     assertEquals(defendingProvince.getFaction(), gaul);

    //     //adding 1 unit to attacking army, should now win with no casualties.
    //     Unit legionaire = unitFactory.getUnit("legionaire");
    //     int numTroops = legionaire.getNumTroops();
    //     attackingProvince.getArmy().add(legionaire);
    //     assertDoesNotThrow(() -> attackingProvince.invadeProvince(defendingProvince, logger));
    //     assertEquals(defendingProvince.getFaction(), rome);
    //     assertEquals(numTroops, legionaire.getNumTroops());

    //     //check unit has now been moved to invaded province;
    //     assertFalse(attackingProvince.getArmy().contains(legionaire));
    //     assertTrue(defendingProvince.getArmy().contains(legionaire));
    //     // Check province ownership has changed
    //     assertTrue(rome.getProvinces().contains(defendingProvince));
    //     assertFalse(gaul.getProvinces().contains(defendingProvince));
        
    //     // Check cant invade from recently conquered province.
    //     assertThrows(Exception.class, () -> defendingProvince.invadeProvince(otherDefending, logger));
        
    //     assertThrows(Exception.class, () -> defendingProvince.moveUnits(defendingProvince.getArmy(), attackingProvince));
    // }

    // /**
    //  * NOTE
    //  * The unit stats need balancing. Currently it very unlikely for units to be destroyed,
    //  * due to very low damage values, and very high defense/sheild values. This means
    //  * that engagements usually end via routing.
    //  */

    // @Test
    // public void meleeVsMeleeTest() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     Unit inf = unitFactory.getUnit("melee infantry");
    //     Unit peasant = unitFactory.getUnit("peasant");


    //     // adding troups to both armies.
    //     for (int i = 0; i < 4; i ++) {
    //         attackingProvince.getArmy().add(inf);
    //         defendingProvince.getArmy().add(peasant);
    //     }
    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));

    //     //Running componants of battle

    //     bR.attachObserver(logger);
    //     // 0% chance of ranged, as both are melee units
    //     assertEquals(0,  bR.getRangedChance(inf, peasant));

    //     // one of the units breaks.
    //     inf.setBroken(true);
        
    //     int routeChance = 50 + (10 * (inf.getSpeed() - peasant.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(inf, peasant));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(peasant, inf));

    //     inf.resetUnit();
    //     // check when other unit is broken.
    //     peasant.setBroken(true);
    //     routeChance = 50 + (10 * (peasant.getSpeed() - inf.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(inf, peasant));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(peasant, inf));
    //     peasant.resetUnit();
    //     // run battle
    //     bR.initiateBattle();
    // }

    // @Test
    // public void rangedVsRangedTest() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     Unit pult = unitFactory.getUnit("catapult");
    //     Unit javelin = unitFactory.getUnit("javelin-skirmisher");


    //     // adding troups to both armies.
    //     for (int i = 0; i < 4; i ++) {
    //         attackingProvince.getArmy().add(pult);
    //         defendingProvince.getArmy().add(javelin);
    //     }
    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));
    //     //Running componants of battle

    //     bR.attachObserver(logger);
    //     //100% chance of ranged, as both are melee units ranged
    //     assertEquals(100,  bR.getRangedChance(pult, javelin));

    //     // one of the units breaks.
    //     pult.setBroken(true);
        
    //     int routeChance = 50 + (10 * (pult.getSpeed() - javelin.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(pult, javelin));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(javelin, pult));

    //     pult.resetUnit();
    //     // check when other unit is broken.
    //     javelin.setBroken(true);
    //     routeChance = 50 + (10 * (javelin.getSpeed() - pult.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(pult, javelin));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(javelin, pult));
    //     javelin.resetUnit();
    //     // run battle
    //     bR.initiateBattle();
    // }

    // @Test
    // public void rangedVsMeleeTest() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     Unit elephant = unitFactory.getUnit("elephant");
    //     Unit javelin = unitFactory.getUnit("javelin-skirmisher");


    //     attackingProvince.getArmy().add(elephant);
    //     defendingProvince.getArmy().add(javelin);

    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));
    //     //Running componants of battle

    //     bR.attachObserver(logger);
    //     //variable chance of ranged.
    //     int rangeChance = 50 + (10 * (javelin.getSpeed() - elephant.getSpeed()));
    //     if (rangeChance > 95) rangeChance = 95;
    //     if (rangeChance < 5) rangeChance = 5;
    //     assertEquals(rangeChance, bR.getRangedChance(elephant, javelin));
    //     //symmetry 
    //     assertEquals(rangeChance, bR.getRangedChance(javelin, elephant));

    //     // one of the units breaks.
    //     elephant.setBroken(true);
        
    //     int routeChance = 50 + (10 * (elephant.getSpeed() - javelin.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(elephant, javelin));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(javelin, elephant));

    //     elephant.resetUnit();
    //     // check when other unit is broken.
    //     javelin.setBroken(true);
    //     routeChance = 50 + (10 * (javelin.getSpeed() - elephant.getSpeed()));
    //     if (routeChance > 100) routeChance = 100;
    //     if (routeChance < 10) routeChance = 10;

    //     assertEquals(routeChance, bR.getRouteChance(elephant, javelin));
    //     //check symmetry of calculation
    //     assertEquals(routeChance, bR.getRouteChance(javelin, elephant));
    //     javelin.resetUnit();
    //     // run battle
    //     bR.initiateBattle();
    // }



    // @Test
    // public void basicInvasionTest2() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     // adding troups to both armies.
    //     for (int i = 0; i < 4; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("melee infantry"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("peasant"));
    //     }
    //     for (int i = 0; i < 4; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("elephant"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("horse archer"));
    //     }
    //     for (int i = 0; i < 4; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("catapult"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("pikemen"));
    //     }
    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));

    //     bR.attachObserver(logger);
    //     bR.initiateBattle();
    // }

    // @Test
    // public void invasionTimeOut() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     // adding troups to both armies.
    //     for (int i = 0; i < 20; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("melee infantry"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("peasant"));
    //     }
    //     for (int i = 0; i < 20; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("elephant"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("horse archer"));
    //     }
    //     for (int i = 0; i < 20; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("catapult"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("pikemen"));
    //     }
    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));

    //     bR.attachObserver(logger);
    //     // should exceed 200 engagements (defending wins)
    //     bR.initiateBattle();
    //     assertEquals(defendingProvince.getFaction(), rome);
    // }

    // @Test
    // public void attackersWin() throws IOException{
    //     UnitFactory unitFactory = new UnitFactory("src/configs/units.json");
    //     var winCond = VictoryFactory.getVictoryCondition();
    //     Faction rome = new Faction("Rome", 1000, unitFactory, winCond);
    //     Faction gaul = new Faction("Gaul", 1000, unitFactory, winCond);
    //     Turn turn = new Turn();
    //     BuildingFactory factory = new BuildingFactory("src/configs/buildings.json");

    //     Province defendingProvince = new Province("defending", turn, factory);
    //     Province attackingProvince = new Province("attacking", turn, factory);
    //     TextLogger logger = (x) -> System.out.println(x);

    //     defendingProvince.setFaction(rome);
    //     attackingProvince.setFaction(gaul);

    //     // adding troups to both armies.
    //     for (int i = 0; i < 10; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("melee infantry"));
    //     }
    //     for (int i = 0; i < 5; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("elephant"));
    //     }
    //     for (int i = 0; i < 1; i ++) {
    //         attackingProvince.getArmy().add(unitFactory.getUnit("catapult"));
    //         defendingProvince.getArmy().add(unitFactory.getUnit("catapult"));
    //     }
    //     //seeded random battle
    //     var bR = new BattleResolver(attackingProvince, defendingProvince, new Random(0));

    //     bR.attachObserver(logger);
    //     // should exceed 200 engagements (defending wins)
    //     bR.initiateBattle();
    //     assertEquals(defendingProvince.getFaction(), gaul);
    // }

}
