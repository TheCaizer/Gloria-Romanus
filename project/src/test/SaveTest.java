package test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import unsw.gloriaromanus.backend.GameData;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.Turn;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;


public class SaveTest {
    private static String testPath = "src/test/testGameState.json";
    private static JSONObject testGameConfig;


    @BeforeAll
    public static void initialise() throws IOException {
        //Getting the configuration used for testing.
        testGameConfig = new JSONObject(Files.readString(Paths.get(testPath)));
    }
    
    @Test
    public void basicSaveTest() throws FileNotFoundException, IOException, ClassNotFoundException, ConditionsParseException {
        GameData orig = GameData.getGameData(testGameConfig);
        String saveName = "save01";
        assertTrue(orig.saveGame(saveName));

        boolean contains = false;
        for (String name : GameData.getSaves()) {
            if (name.equals(saveName)) contains = true;
        }
        assertTrue(contains);

        GameData loadedSave = GameData.loadGame(saveName);
        assertNotEquals(null, loadedSave);
    }

    @Test
    public void testProvince() throws IOException {
        Province p = new Province("p", new Turn(), new BuildingFactory());
        File saveFile = new File("saveGames/provinceTest");
        saveFile.createNewFile();
        var fileStream = new FileOutputStream(saveFile, false);
        var objStream = new ObjectOutputStream(fileStream);
        objStream.writeObject(p);
        objStream.close();
        fileStream.close();
    }
}
