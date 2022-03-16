package unsw.gloriaromanus.backend.units;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class UnitFactory implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private transient JSONObject units;


    /**
     * Default Constructer for UnitFactory from default file path
     * @param path path of unit config file
     */
    public UnitFactory() throws IOException {
        this("src/configs/units.json");
    }

    /**
     * Construct UnitFactory from config file path
     * @param path path of unit config file
     */
    public UnitFactory(String path) throws IOException{
        //"src/unsw/gloriaromanus/unit_stats.json"
        String content = Files.readString(Paths.get(path));
        units = new JSONObject(content);
    }

    /**
     * Construct UnitFactory from JSONObject.
     * @param json JSONObject containing units config stats.
     */
    public UnitFactory(JSONObject json) {
        this.units = json;
    }
    
    
    public Unit getUnit(String unitName) {
        if(unitName == null){
            return null;
        }
        JSONObject unitConfig  = units.getJSONObject(unitName);
        switch(unitName){
            case "legionaire":
                return new Infantry(unitName, unitConfig);
            case "chariot":
                return new Cavlary(unitName, unitConfig);
            case "peasant":
                return new Infantry(unitName, unitConfig);
            case "berserker":
                return new Infantry(unitName, unitConfig);
            case "horse archer":
                return new Cavlary(unitName, unitConfig);
            case "horse cavlary":
                return new Cavlary(unitName, unitConfig);
            case "melee infantry":
                return new Infantry(unitName, unitConfig);
            case "ranged infantry":
                return new Infantry(unitName, unitConfig);
            case "elephant":
                return new Cavlary(unitName, unitConfig);
            case "druid":
                return new Infantry(unitName, unitConfig);
            case "pikemen":
                return new Spearmen(unitName, unitConfig);
            case "javelin-skirmisher":
                return new Spearmen(unitName, unitConfig);
            case "ballista":
                return new Artillery(unitName, unitConfig);
            case "catapult":
                return new Artillery(unitName, unitConfig);
        }
        return null;
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
        out.writeUTF(units.toString());
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
        units = new JSONObject(in.readUTF());
    }
}
