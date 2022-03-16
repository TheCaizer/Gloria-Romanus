package unsw.gloriaromanus.backend.infrastructure;

import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Paths;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

/**
 * Constructs BuildingState objects based on a json configuration file.
 * Used to construct the next building state in a building chain, i.e. getting
 * a buildings upgraded state.
 */
public class BuildingFactory implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private transient JSONObject buildings; // contains building stats


    /**
     * Default constructor to read building config from default path
     * @param path path to buildings config file.
     */
    public BuildingFactory() {
        this("src/configs/buildings.json");
    }

    /**
     * Constructor to read building config from file.
     * @param path path to buildings config file.
     */
    public BuildingFactory(String path) {
        buildings = getJSON(path);
    }

    /**
     * Construct factory directly from json file.
     * @param json json object containing building stats
     */
    public BuildingFactory(JSONObject json) {
        buildings = json;
    }

    /**
     * Parses json data into buildings state.
     * @param state the current MarketState.
     * @return Returns the next MarketState in the Market building chain, if it 
     *         exists, otherwise null.
     */
    public MarketState getUpgradedState(MarketState state) {
        JSONObject json = getUpgradedJSON(state, "Market");
        if (json == null) return null;
    
        return new MarketState
            (
                json.getInt("level"), 
                json.getInt("baseWealthCost"), 
                json.getInt("baseTurnCost"), 
                json.getInt("wealthBonus"), 
                json.getInt("growthBonus"), 
                json.getInt("factionConstrWealthCostReduction")
            );
    }

    /**
     * Parses json data into buildings state.
     * @param state the current RoadState
     * @return Returns the next RoadState in the Road building chain, if it 
     *         exists, otherwise null.
     */
    public RoadState getUpgradedState(RoadState state) {
        JSONObject json = getUpgradedJSON(state, "Road");
        if (json == null) return null;

        return new RoadState
            (
                json.getInt("level"),
                json.getString("type"),
                json.getInt("baseWealthCost"),
                json.getInt("baseTurnCost"),
                json.getInt("provinceMovementCost")
            );
    }

    /**
     * Parses json data into buildings state.
     * @param state the current MineState
     * @return Returns the next MineState in the Mine building chain, if it 
     *         exists, otherwise null.
     */
    public MineState getUpgradedState(MineState state) {
        JSONObject json = getUpgradedJSON(state, "Mine");
        if (json == null) return null;
        
        return new MineState
            (
                json.getInt("level"),
                json.getInt("baseWealthCost"),
                json.getInt("baseTurnCost"),
                json.getInt("wealthBonus"),
                json.getInt("growthBonus"),
                json.getInt("factionSoldierWealthCostReduction"),
                json.getInt("factionConstrTurnCostReduction")
            );
    }

    /**
     * Parses json data into buildings state.
     * @param state the current PortState
     * @return Returns the next PortState in the Port building chain, if it 
     *         exists, otherwise null.
     */
    public PortState getUpgradedState(PortState state) {
        JSONObject json = getUpgradedJSON(state, "Port");
        if (json == null) return null;
        
        return new PortState
            (
                json.getInt("level"),
                json.getInt("baseWealthCost"),
                json.getInt("baseTurnCost"),
                json.getInt("wealthBonus"),
                json.getInt("growthBonus"),
                json.getInt("coastWealthBonus"),
                json.getInt("coastGrowthBonus")
            );
    }

    // Helper Functions

    /**
     * @param state The current building state.
     * @param key the String key corresponding to the building type.
     * @return the JSON object corresponding to the next state in a 
     *         building chain if it exists, otherwise null. 
     *         If state is null, the base state is returned.

     */
    private JSONObject getUpgradedJSON(BuildingState state, String key) {
        JSONArray jsonArray = buildings.getJSONArray(key);
        int index = (state == null) ? 0 : state.getLevel() + 1;
        if (index >= jsonArray.length()) {
            return null; // Max upgraded building state
        }

        return jsonArray.getJSONObject(index);
    }

    /**
     * 
     * @param path The path of the buildings config file.
     * @return The json object containing the building config stats.
     */
    private JSONObject getJSON(String path) {
        JSONObject json = null;
        try {
            json = new JSONObject(Files.readString(Paths.get(path)));
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return json;
    }

    /**
     * Custom serialization function for this class. Used by 
     * ObjectOutputStream.writeObject().
     * @param out ObjectStream to write to.
     * @throws IOException if I/O errors occur while writing to the underlying OutputStream
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // Saving json in text format - as its not serializable
        out.writeUTF(buildings.toString());
    }

    /**
     * Custom deserialization function for this class. Used by 
     * ObjectInputStream.readObject().
     * @param in ObjectStream to read from
     * @throws IOException  if the class of a serialized object could not be found.
     * @throws ClassNotFoundException if an I/O error occurs.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // reading json from stream
        buildings = new JSONObject(in.readUTF());
    }
}