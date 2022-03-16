package unsw.gloriaromanus.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import unsw.gloriaromanus.ArrayUtil;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.infrastructure.BuildingFactory;
import unsw.gloriaromanus.backend.units.UnitFactory;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;

public class GameData implements Serializable{


    private static final long serialVersionUID = 1L;
    private Turn turn;
    private HashMap <String, Province> gameMap;
    private HashMap <String, Faction> factions;
    //Stores turn order of factions
    private LinkedList<Faction> factionList;
    //The faction who's turn it is currently 
    private Faction currFaction;
    // The number of factions who have acted this turn.
    private int numHadTurn = 0;

    /**
     * @param configuration JSON object containing the configuration info
     * for a new game.
     */
    private GameData (JSONObject newGameConfig) throws ConditionsParseException {
        JSONObject adjacencyMatrix = null;
        JSONArray landLockedJson = null;
        JSONObject buildings = null;
        JSONObject units = null;
        try {
            adjacencyMatrix = new JSONObject(Files.readString(Paths.get("src/configs/province_adjacency_matrix_fully_connected.json")));
            landLockedJson = new JSONArray(Files.readString(Paths.get("src/configs/landlocked_provinces.json")));
            buildings = new JSONObject(Files.readString(Paths.get("src/configs/buildings.json")));
            units = new JSONObject(Files.readString(Paths.get("src/configs/units.json")));
        } catch (IOException e) {
            System.err.println(e.toString());
            System.exit(1);
        }
        turn = new Turn();
        gameMap = generateGameMap(adjacencyMatrix, landLockedJson, buildings);
        factions = generateFactions(newGameConfig, units);
        generateFactionList();
    }

    /**
     * Methods to generate GameData instance from configuration file.
     * @param path Path to gameConfig.json file
     * @return an instance of GameData.
     * @throws IOException 
     */
    public static GameData getGameData(String path) throws IOException, ConditionsParseException {
        return getGameData(new JSONObject(Files.readString(Paths.get(path))));
    }


    /**
     * Method to generate GameData instance from configuration JSONObject
     * @param config JSON object containing configuration info
     * @return an instance of GameData.
     */
    public static GameData getGameData(JSONObject config) throws ConditionsParseException {
        switch (config.getString("stateType")) {
            case "newGame":
                return new GameData(config.getJSONObject("newGameConfiguration"));
        
            case "loadGame":
                return loadGame(config.getString("saveFile"));
        }
        return null;
    }


    private HashMap <String, Faction> generateFactions(JSONObject newGameConfig, JSONObject units) throws ConditionsParseException {
        var initialOwnership = newGameConfig.getJSONObject("initialProvinceOwnership");
        int initialGold = newGameConfig.getInt("initialGold");

        HashMap <String, Faction> map = new HashMap <String, Faction>();
        var winCondition = VictoryFactory.getVictoryCondition(newGameConfig.getJSONObject("conditions"));
        UnitFactory unitFactory = new UnitFactory(units);

        for (String key : initialOwnership.keySet()) {
            Faction faction = new Faction(key, initialGold, unitFactory, winCondition);
            ArrayList<String> provinces = ArrayUtil.convert(initialOwnership.getJSONArray(key));
            for (String provinceName : provinces) {
                //faction.addProvince(gameMap.get(provinceName));
                Province curr = gameMap.get(provinceName);
                curr.setFaction(faction);
                // Add default 10 troops to all provinces.
                for (int i =0; i < 10; i++) curr.getArmy().add(unitFactory.getUnit("peasant"));
            }
            map.put(key, faction);
        }

        return map;
    }

    private void generateFactionList() {
        //Maybe here we make the make itseld but for now im just getting all the factions
        //That are in the hash map
        factionList = new LinkedList<Faction>(factions.values());
        //we shuffle the list to for randomize turn order (can remove)
        Collections.shuffle(factionList);
        currFaction = getNextFaction();
    }

    /**
     * @return the faction whos turn it is currently.
     */
    public Faction getCurrFaction() {
        return currFaction;
    }

    // Gets the next faction
    public Faction getNextFaction() {
        currFaction = factionList.poll();
        if (currFaction.checkDefeat()) {
            factions.remove(currFaction.getName());
            currFaction = getNextFaction();
        } else {
            factionList.offer(currFaction);
            numHadTurn++;
            if (numHadTurn > factionList.size()) {
                numHadTurn = 1;
                turn.increment();
            }
        }
        return currFaction;
    }

    private HashMap <String, Province> generateGameMap(JSONObject adjacencyMatrix, JSONArray landLockedJson, JSONObject buildings) {
        var buildingFactory = new BuildingFactory(buildings);
        ArrayList<String> landLocked = ArrayUtil.convert(landLockedJson);
        HashMap<String, Province> map = new HashMap<String, Province>();
        for (String key : adjacencyMatrix.keySet()) {
            map.put
                (key, (landLocked.contains(key)) ? new Province(key, turn, buildingFactory) 
                                                 : new CoastProvince(key, turn, buildingFactory)
                );
        }
        for (String key1 : adjacencyMatrix.keySet()) {
            JSONObject adjProvinces = adjacencyMatrix.getJSONObject(key1);
            for (String key2 : adjProvinces.keySet()) {
                if (adjProvinces.getBoolean(key2)) {
                    map.get(key1).addAdjacent(map.get(key2));
                }
            }
        }
        return map;
    }

    /**
     * Retrieves a faction so that a player can interact with the game.
     * DOES NOT removes the faction from the game and returns null if the faction has
     * lost the game.
     * @param name the name of a faction
     * @return null if the faction has been defeated, otherwise the faction 
     *         object.
     */
    public Faction getFaction(String name) {
        Faction faction = factions.get(name);
        if (faction == null) return null;
        // if (faction.getFactionSize() == 0) {
        //     factions.remove(faction.getName());
        //     factionList.remove(faction);
        //     return null;
        // }
        return faction;
    }

    public Province getProvince(String name) {
        return gameMap.get(name);
    }

    public Turn getTurn() {
        return turn;
    }


    /**
     * Serializes all game data into a file by the name saveName.ser
     * in the directory saveGames.
     * @param saveName name of save file
     * @return true if save was successful, otherwise false;
     */
    public boolean saveGame(String saveName) {

        new File("saveGames").mkdir();
        File saveFile = new File("saveGames/" + saveName + ".ser");
        try {
            saveFile.createNewFile();
            var fileStream = new FileOutputStream(saveFile, false);
            var objStream = new ObjectOutputStream(fileStream);
            objStream.writeObject(this);
            objStream.close();
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false; // basic error handling for now
        }
        return true;
    }

    public static List<String> getSaves() {
        File saveFolder = new File("saveGames");
        saveFolder.mkdir();
        List<String> saves = Arrays.asList(saveFolder.list());
        List<String> result = new ArrayList<String>();
        // remove file extension
        saves.forEach((x) 
            -> result.add((x.endsWith(".ser")) ? x.substring(0, x.indexOf(".ser")) : x));

        return result;
    }
    
    public static GameData loadGame(String saveName) {
        new File("saveGames").mkdir();
        File saveFile = new File("saveGames/" + saveName + ".ser");
        GameData data = null;
        try {
            var fileIn = new FileInputStream(saveFile);
            var objIn = new ObjectInputStream(fileIn);
            data = (GameData) objIn.readObject();
            objIn.close();
            fileIn.close();
        } catch (IOException| ClassNotFoundException e) {
            return null; // basic error handling for now
        }
        return data;
    }

    public static List<String> getAllPossibleFactions() throws IOException {
        var factionsJson  = new JSONObject(Files.readString(Paths.get("src/configs/factions.json")));
        return new ArrayList<String>(factionsJson.keySet());
    }

    /**
     * basic Province assignment
     * @param factions
     * @return
     * @throws IOException
     */
    public static JSONObject generateRandomOwnership(List<String> factions) throws IOException {
        if (factions.size() == 0) return null;

        var ownership = new JSONObject();
        var provinceJson = new JSONObject(Files.readString(Paths.get("src/configs/province_adjacency_matrix_fully_connected.json")));
        var provinceList = new ArrayList<String>(provinceJson.keySet());

        for (String faction: factions) {
            ownership.put(faction, new JSONArray());
        }

        var provinceI = provinceList.iterator();
        while (provinceI.hasNext()) {
            for (String faction: factions) {
                if (!provinceI.hasNext()) break;
                ownership.getJSONArray(faction).put(provinceI.next());
            }
        }

        return ownership;
    }
}