package test;

//import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.GameData;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.units.Unit;
import unsw.gloriaromanus.backend.units.UnitFactory;


public class MovementTest {
    // private static String testPath = "src/test/testGameState.json";
    // private static JSONObject testGameConfig;


    // @BeforeAll
    // public static void initialise() throws IOException {
    //     testGameConfig = new JSONObject(Files.readString(Paths.get(testPath)));
    // }


    // @Test
    // public void adjacentPaths() throws IOException, Exception{


    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province xi = gameData.getProvince("XI");
    //     Province x = gameData.getProvince("X");
    //     Province ix = gameData.getProvince("IX");
    //     //x.getShortestDistTo(x);

    //     // Adjacent roman provinces -> no roads
    //     assertDoesNotThrow(() -> x.getShortestDistTo(xi));
    //     assertEquals(x.getShortestDistTo(xi), 4);
    //     assertDoesNotThrow(() -> xi.getShortestDistTo(x));
    //     assertEquals(xi.getShortestDistTo(x), 4);

    //     // Adjacent provinces of different factions, no path
    //     assertThrows(Exception.class, () -> x.getShortestDistTo(ix));
    //     assertThrows(Exception.class, () -> ix.getShortestDistTo(x));

    //     x.upgradeRoad();
    //     // Adjacent roman provinces -> level 1 road
    //     assertDoesNotThrow(() -> x.getShortestDistTo(xi));
    //     assertEquals(x.getShortestDistTo(xi), 3);
    //     assertDoesNotThrow(() -> xi.getShortestDistTo(x));
    //     assertEquals(xi.getShortestDistTo(x), 4);


    //     x.upgradeRoad();
    //     xi.upgradeRoad();
    //     // Adjacent roman provinces -> level 2 road
    //     assertDoesNotThrow(() -> x.getShortestDistTo(xi));
    //     assertEquals(x.getShortestDistTo(xi), 2);
    //     assertDoesNotThrow(() -> xi.getShortestDistTo(x));
    //     assertEquals(xi.getShortestDistTo(x), 3);
    // }

    // @Test
    // public void invalidPaths() throws IOException, Exception{
    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province x = gameData.getProvince("X");
    //     Province ix = gameData.getProvince("IX");
    //     Province narbonensis = gameData.getProvince("Narbonensis");
    //     Province syria = gameData.getProvince("Syria");
        
    //     //x.getShortestDistTo(x);

    //     // Adjacent provinces of different factions, no path
    //     assertThrows(Exception.class, () -> x.getShortestDistTo(ix));
    //     assertThrows(Exception.class, () -> ix.getShortestDistTo(x));

    //     // Same faction, not connected
    //     assertThrows(Exception.class, () -> narbonensis.getShortestDistTo(x));
    //     assertThrows(Exception.class, () -> x.getShortestDistTo(narbonensis));
    //     assertThrows(Exception.class, () -> syria.getShortestDistTo(x));
    //     assertThrows(Exception.class, () -> x.getShortestDistTo(syria));
    //     assertThrows(Exception.class, () -> syria.getShortestDistTo(narbonensis));
    //     assertThrows(Exception.class, () -> narbonensis.getShortestDistTo(syria));
    // }

    
    // @Test
    // public void noRoadsPaths() throws IOException, Exception {
    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province raetia = gameData.getProvince("Raetia");
    //     Province macedonia = gameData.getProvince("Macedonia");
    //     Province mI = gameData.getProvince("Moesia Inferior");
    //     //x.getShortestDistTo(x);

    //     // non-Adjacent roman provinces -> no roads
    //     assertDoesNotThrow(() -> raetia.getShortestDistTo(macedonia));
    //     assertEquals(raetia.getShortestDistTo(macedonia), 4*5);
    //     assertDoesNotThrow(() -> macedonia.getShortestDistTo(raetia));
    //     assertEquals(macedonia.getShortestDistTo(raetia), 4*5);

    //     assertDoesNotThrow(() -> raetia.getShortestDistTo(mI));
    //     assertEquals(raetia.getShortestDistTo(mI), 4*5);
    //     assertDoesNotThrow(() -> mI.getShortestDistTo(raetia));
    //     assertEquals(mI.getShortestDistTo(raetia), 4*5);

    //     assertDoesNotThrow(() -> mI.getShortestDistTo(macedonia));
    //     assertEquals(mI.getShortestDistTo(macedonia), 4*2);
    //     assertDoesNotThrow(() -> macedonia.getShortestDistTo(mI));
    //     assertEquals(macedonia.getShortestDistTo(mI), 4*2);
    // }

    // @Test
    // public void singleUnitSimpleTest() throws IOException, Exception {
    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province raetia = gameData.getProvince("Raetia");
    //     Province noricum = gameData.getProvince("Noricum");
    //     Province macedonia = gameData.getProvince("Macedonia");
    //     // Province mI = gameData.getProvince("Moesia Inferior");
    //     UnitFactory factory = new UnitFactory("src/configs/units.json");

    //     Unit leg = factory.getUnit("legionaire");
    //     int legMovement = leg.getMovement();
    //     raetia.getArmy().add(leg);

    //     // Not enough movement points
    //     assertFalse(raetia.moveUnits(raetia.getArmy(), macedonia));
    //     assertEquals(leg.getMovement(), legMovement);
    //     assertTrue(raetia.getArmy().contains(leg));
    //     assertFalse(macedonia.getArmy().contains(leg));

    //     // Move to same destination - no points lost
    //     assertTrue(raetia.moveUnits(raetia.getArmy(), raetia));
    //     assertEquals(leg.getMovement(), legMovement);
    //     assertTrue(raetia.getArmy().contains(leg));
    //     assertTrue(raetia.getArmy().size() == 1);


    //     // should remove 4 movement points and move unit
    //     assertTrue(raetia.moveUnits(raetia.getArmy(), noricum));
    //     assertEquals(leg.getMovement(), legMovement - 4);
    //     assertTrue(noricum.getArmy().contains(leg));
    //     assertFalse(raetia.getArmy().contains(leg));

    //     // Move back
    //     assertTrue(noricum.moveUnits(noricum.getArmy(), raetia));
    //     assertEquals(leg.getMovement(), legMovement - 8);
    //     assertTrue(raetia.getArmy().contains(leg));
    //     assertFalse(noricum.getArmy().contains(leg));

    //     //out of movement points
    //     assertFalse(raetia.moveUnits(raetia.getArmy(), noricum));
    //     assertEquals(leg.getMovement(), legMovement - 8);
    //     assertFalse(noricum.getArmy().contains(leg));
    //     assertTrue(raetia.getArmy().contains(leg));
        
    //     // reset unit state
    //     raetia.onTurnEnd();
    //     assertTrue(raetia.moveUnits(raetia.getArmy(), noricum));
    //     assertEquals(leg.getMovement(), legMovement - 4);
    //     assertTrue(noricum.getArmy().contains(leg));
    //     assertFalse(raetia.getArmy().contains(leg));
    // }

    // @Test
    // public void UnitsAdjacentTest()  throws IOException, Exception {
    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province xi = gameData.getProvince("XI");
    //     Province x = gameData.getProvince("X");
    //     Province ix = gameData.getProvince("IX");
    //     //x.getShortestDistTo(x);
    //     UnitFactory factory = new UnitFactory("src/configs/units.json");
    //     Unit leg = factory.getUnit("legionaire");

    //     for(int i = 0; i < 10; i++) {
    //         x.getArmy().add(factory.getUnit("legionaire"));
    //     }
    //     int initialMovement = x.getArmy().get(0).getMovement();



    //     // moving to same province -> no movement points used
    //     assertDoesNotThrow(() -> assertTrue(x.moveUnits(x.getArmy(), x)));
    //     assertEquals(x.getArmy().size(), 10);
    //     for(Unit unit : x.getArmy()) {
    //         assertEquals(unit.getMovement(), initialMovement);
    //     }

    //     // moving to adjacent
    //     assertDoesNotThrow(() -> assertTrue(x.moveUnits(x.getArmy(), xi)));
    //     assertTrue(x.getArmy().isEmpty());
    //     assertEquals(xi.getArmy().size(), 10);
    //     for(Unit unit : xi.getArmy()) {
    //         assertEquals(unit.getMovement(), initialMovement -4 );
    //     }

    //     // Can't move to adjacent enemy province
    //     assertThrows(Exception.class, () -> xi.moveUnits(xi.getArmy(), ix));
    //     assertTrue(ix.getArmy().isEmpty());
    //     assertEquals(xi.getArmy().size(), 10);
    //     for(Unit unit : xi.getArmy()) {
    //         assertEquals(unit.getMovement(), initialMovement -4 );
    //     }

    //     //Splitting army into smaller groups
    //     List<Unit> subArmy = xi.getArmy().subList(0, 5);

    //     // Moving half army 
    //     assertDoesNotThrow(() -> assertTrue(xi.moveUnits(subArmy, x)));
    //     assertEquals(xi.getArmy().size(), 5);
    //     assertEquals(x.getArmy().size(), 5);
    //     for(Unit unit : xi.getArmy()) {
    //         assertEquals(unit.getMovement(), initialMovement - 4 );
    //     }
    //     for(Unit unit : x.getArmy()) {
    //         assertEquals(unit.getMovement(), initialMovement - 4 - 4);
    //     }
    
    //     // Checking that group cant be moved if one has attacked
    //     xi.getArmy().get(0).setHasAttacked(true);
    //     assertDoesNotThrow(() -> assertFalse(xi.moveUnits(xi.getArmy(), x)));
    //     assertEquals(xi.getArmy().size(), 5);
    // }
    
    // @Test
    // public void roadTest() throws IOException, Exception {
    //     // Testing multiple units with roads
    //     GameData gameData = GameData.getGameData(testGameConfig);
    //     Province ps = gameData.getProvince("Pannonia Superior");
        
    //     Province raetia = gameData.getProvince("Raetia");
    //     Province noricum = gameData.getProvince("Noricum");
    //     Province xi = gameData.getProvince("XI");
    //     Province x = gameData.getProvince("X");

    //     UnitFactory factory = new UnitFactory();
    //     Unit chariot = factory.getUnit("chariot");
    //     int initialChariot = chariot.getMovement();
    //     Unit leg = factory.getUnit("berserker");
    //     int initialLeg = leg.getMovement();
    //     ps.getArmy().add(chariot);
    //     ps.getArmy().add(leg);

    //     // Should make no difference (still not on shortest path)
    //     raetia.upgradeRoad();
    //     raetia.upgradeRoad();

    //     // now shortest path
    //     noricum.upgradeRoad();
    //     xi.upgradeRoad();
        
    //     assertDoesNotThrow(() -> assertTrue(ps.moveUnits(ps.getArmy(), xi)));
    //     assertEquals(initialLeg - 4 -3,  leg.getMovement());
    //     assertEquals(initialChariot - 4 -3,  chariot.getMovement());
    //     assertTrue(xi.getArmy().contains(leg));
    //     assertTrue(xi.getArmy().contains(chariot));

    //     Unit leg2 = factory.getUnit("berserker");
    //     leg2.deductMovement(10);
    //     xi.getArmy().add(leg2);

    //     // one unit does not have enough movement points.
    //     assertDoesNotThrow(() -> assertFalse(xi.moveUnits(xi.getArmy(), noricum)));
    //     xi.getArmy().remove(leg2);
    //     assertDoesNotThrow(() -> assertTrue(xi.moveUnits(xi.getArmy(), noricum)));
    // }
}