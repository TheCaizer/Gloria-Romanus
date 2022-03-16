package unsw.gloriaromanus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.JSONObject;

import javafx.scene.image.Image;

/**
 * class to load and return JavaFX images
 */
public class SpriteFactory {
    private HashMap<String, Image> factionSprites;
    
    public SpriteFactory() throws IOException {
        factionSprites = new HashMap<String, Image>();
        JSONObject factionsConfig = new JSONObject(Files.readString(Paths.get("src/configs/factions.json")));
    
        for (String faction:  factionsConfig.keySet()) {
            String spritePath = factionsConfig.getJSONObject(faction).getString("factionSpritePath");
            Image sprite = new Image((new File(spritePath)).toURI().toString());
            factionSprites.put(faction, sprite);
        }
    }
    

    public Image getFactionSprite(String factionName) {
        return factionSprites.get(factionName);
    }
}
