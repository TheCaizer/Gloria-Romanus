package unsw.gloriaromanus.backend.victoryConditions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;

/**
 * Factory pattern to produce Composite Victory conditions.
 */
public class VictoryFactory {


    /**
     * Generates composite VictoryCondition object based on default path
     * @return composite condition object
     */
    public static VictoryCondition getVictoryCondition() throws ConditionsParseException {
        return getVictoryCondition("src/configs/conditions.json");
    }

    /**
     * Generates composite VictoryCondition object based on path
     * @return composite condition object
     */
    public static VictoryCondition getVictoryCondition(String path) throws ConditionsParseException {
        JSONObject json = null;
        try {
            json = new JSONObject(Files.readString(Paths.get(path)));
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return getVictoryCondition(json);
    }

    /**
     * Generates composite VictoryCondition object based on json object
     * @return composite condition object
     */
    public static VictoryCondition getVictoryCondition(JSONObject json) throws ConditionsParseException {
        return json.has("subgoals") ? deserializeComposite(json) : deserializeLeaf(json);
    }
    
    /**
     * Deserializes a json object into a composite Victory condition object, 
     * using reccursive calls to getVictoryCondition(JSONObject json);
     * @return composite condition object
     */
    private static VictoryCondition deserializeComposite(JSONObject json) throws ConditionsParseException {
        JSONArray subGoals =  json.getJSONArray("subgoals");
        var conditions = new ArrayList<VictoryCondition>();
        for (int i = 0; i < subGoals.length(); i++) {
            conditions.add(getVictoryCondition(subGoals.getJSONObject(i)));
        }

        switch (json.getString("goal")) {
            case "OR"   : return new OrCondition(conditions);
            case  "AND" : return new AndCondition(conditions);
        }
        throw new ConditionsParseException(json.getString("goal") + " is not a valid composite goal.");
    }

    /**
     * Deserializes a json object into a leaf VictoryCondition object.
     * @return leaf condition object
     */
    private static VictoryCondition deserializeLeaf(JSONObject json) throws ConditionsParseException {
        if (json.has("option")) {
            switch (json.getString("goal")) {
            case "TREASURY": return new TreasuryCondition(json.getInt("option"));
            case "CONQUEST": return new ConquestCondition(json.getInt("option"));
            case "WEALTH"  : return new WealthCondition(json.getInt("option"));
            }
        } else {
            switch (json.getString("goal")) {
            case "TREASURY": return new TreasuryCondition();
            case "CONQUEST": return new ConquestCondition();
            case "WEALTH"  : return new WealthCondition();
            }
        }
        throw new ConditionsParseException(json.getString("goal") + " is not a valid leaf goal.");
    }

    /**
     * Generates a json object representing a random conjunction/disjunction of
     * victory conditions. 
     * @param rng random seed
     * @return JSON object containing victory condition information
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static JSONObject generateRandomConditions(Random rng) throws IOException {
        String content = Files.readString(Paths.get("src/configs/VictoryConditions.json"));
        JSONObject json = new JSONObject(content);
        
        JSONArray compositeGoals = json.getJSONArray("compositeGoals");
        JSONArray basicGoals = json.getJSONArray("basicGoals");

        //Naieve approach to randomisation.
        JSONObject prev = new JSONObject();
        prev.put("goal", basicGoals.remove(rng.nextInt(basicGoals.length())));
        while (!basicGoals.isEmpty()) {
            JSONArray subGoals = new JSONArray();
            subGoals.put(prev);
            prev = new JSONObject();
            prev.put("goal", basicGoals.remove(rng.nextInt(basicGoals.length())));
            subGoals.put(prev);
            prev = new JSONObject();
            prev.put("goal", compositeGoals.getString(rng.nextInt(compositeGoals.length())));
            prev.put("subgoals", subGoals);
        }
        return prev;
    }

}
