package unsw.gloriaromanus;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;

import unsw.gloriaromanus.backend.GameData;
import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;
import unsw.gloriaromanus.backend.victoryConditions.VictoryFactory;

public class StartMenuController {

    @FXML
    private Pane MainMenuPane;

    @FXML
    private Button newGameButton;

    @FXML
    private Button loadGameButton;

    @FXML
    private Button mainQuitButton;

    @FXML
    private StackPane mainMenuOptionsPane;

    @FXML
    private Pane startOptionsPane;

    @FXML
    private Pane loadOptionsPane;

    @FXML
    private Button startButton;

    @FXML
    private Button loadButton;
    
    @FXML
    private ListView<String> factionsListView;

    @FXML
    private ListView<String> saveFileListView;

    @FXML
    private TextArea conditionsText;

    @FXML
    private Text TitleCard;

    @FXML
    private TextField initialWealthText;

    private StringProperty currentScreen;

    private MediaPlayer mediaPlayer;

    @FXML
    private void initialize() throws IOException {
        currentScreen = new SimpleStringProperty();
        factionsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        factionsListView.getItems().addAll(GameData.getAllPossibleFactions());
        DropShadow is = new DropShadow();
        TitleCard.setFill(Color.GOLDENROD);
        is.setRadius(10.0f);
        is.setOffsetX(5.0f);
        is.setOffsetY(10.0f);
        TitleCard.setFont(Font.font("Alegreya", FontWeight.BOLD, 50));
        TitleCard.setEffect(is);
        MainMenuPane.setStyle("-fx-background-color: radial-gradient(radius 100%, red, moccasin  , moccasin  );");
        /*
        MainMenuPane.setStyle("-fx-background-color: radial-gradient(closest-side, #3f87a6, #ebf8e1, #f69d3c);");
        */
        refresh();
        
    }

    /*
    * code to play the background music
    */
    public void playMusic() {
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/StartMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        //this line of code loops the music
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();
    }

    public void setCurrentScreen(StringProperty currentScreen) {
        this.currentScreen.bindBidirectional(currentScreen);
    }

    public void refresh() {
        playMusic();
        initialWealthText.setText("1000");
        saveFileListView.getItems().clear();
        saveFileListView.getItems().addAll(GameData.getSaves());
        showOptions(null);
    }

    @FXML
    public void handleGenerateConditions(ActionEvent event) throws IOException {
        JSONObject conds = VictoryFactory.generateRandomConditions(new Random(System.currentTimeMillis()));
        conditionsText.setText(conds.toString(2));
    }

    @FXML
    public void handleLoadGamePress(ActionEvent event) {

        saveFileListView.getSelectionModel().clearSelection();
        showOptions(loadOptionsPane);
    }

    @FXML
    public void handleNewGamePress(ActionEvent event) throws IOException {

        factionsListView.getSelectionModel().clearSelection();
        conditionsText.clear();
        showOptions(startOptionsPane);
    }

    @FXML
    public void handleQuitPress(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void handleLoadPress(ActionEvent event) throws IOException {
        String save = saveFileListView.getSelectionModel().getSelectedItem();
        if (save == null) {
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setHeaderText("Invalid Save Selection");
            errorAlert.setContentText("You did not select a save to load");
            errorAlert.showAndWait();
            return;
        }
        
        createConfigFile(save);
        currentScreen.set("Gloria Romanus");
        mediaPlayer.stop();
    }


    @FXML
    public void handleStartPress(ActionEvent event) throws IOException {
        JSONObject factions = checkFactions();
        JSONObject conditions = checkConditions();
        int initialGold = 1000;
        try {
            initialGold = Integer.parseInt(initialWealthText.getText().trim());
        } catch (NumberFormatException e) {
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setHeaderText("Invalid initial Gold");
            errorAlert.setContentText("Initial gold must be an Integer");
            errorAlert.showAndWait();
            return;
        }
        if (initialGold <= 0) {
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setHeaderText("Invalid initial Gold");
            errorAlert.setContentText("Initial Gold must be greater than 0");
            errorAlert.showAndWait();
            return;
        }
        if (factions == null || conditions == null) return;
        
        // Generate JSON Config File
        createConfigFile(factions, conditions, initialGold);
        // Switch screens to main game
        currentScreen.set("Gloria Romanus");
        mediaPlayer.stop();
    }

    /**
     * Creates a JSON file containing the configuration information for 
     * loading a previous save.
     * 
     * @param saveFileName the selected save file name.
     * @throws IOException - If an I/O error occurs
     */
    private void createConfigFile(String saveFileName) throws IOException {
        JSONObject configJSON = new JSONObject();
        configJSON.put("stateType", "loadGame");
        configJSON.put("saveFile", saveFileName);


        File config = new File("src/configs/gameConfig.json");
        config.createNewFile();
        var fileWriter = new FileWriter(config, false);
        fileWriter.write(configJSON.toString(4));
        fileWriter.close();
    }


    /**
     * Creates a JSON file containing the configuration information for a 
     * new game. 
     * 
     * File created at path src/configs/gameConfig.json
     * @param factions the initial ownership of provinces
     * @param conditions the victory conditions for the game.
     * @throws IOException - If an I/O error occurs
     */
    private void createConfigFile(JSONObject factions, JSONObject conditions, int initialGold) throws IOException {
        JSONObject configJSON = new JSONObject();
        configJSON.put("stateType", "newGame");
        var details = new JSONObject();
        details.put("initialGold", initialGold);
        details.put("conditions", conditions);
        details.put("initialProvinceOwnership", factions);
        configJSON.put("newGameConfiguration", details);


        File config = new File("src/configs/gameConfig.json");
        //if (config.exists()) config.delete();
        config.createNewFile();
        var fileWriter = new FileWriter(config, false);
        fileWriter.write(configJSON.toString(4));
        fileWriter.close();
    }



    private JSONObject checkFactions() throws IOException {
        List<String> factions = factionsListView.getSelectionModel().getSelectedItems();
        if (factions.size() < 2) {
            Alert errorAlert = new Alert(AlertType.INFORMATION);
            errorAlert.setHeaderText("Invalid Selection of factions");
            errorAlert.setContentText("You need to pick at least 2 Factions");
            errorAlert.showAndWait();
            return null;
        }
        return GameData.generateRandomOwnership(factions);
    }

    private JSONObject checkConditions() {
        //System.out.println(conditionsText.getText());
        try {
            // checking that the JSON string can be correctly parsed into a composite condition.
            VictoryFactory.getVictoryCondition(new JSONObject(conditionsText.getText()));

        } catch (JSONException e) {
            Alert errorAlert = new Alert(AlertType.INFORMATION);
            errorAlert.setHeaderText("Invalid Victory Conditions");
            errorAlert.setContentText("Victory conditions must be in a valid JSON format");
            errorAlert.showAndWait();
            return null;
        } catch (ConditionsParseException e) {
            Alert errorAlert = new Alert(AlertType.INFORMATION);
            errorAlert.setHeaderText("Invalid Victory Conditions");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
            return null;
        }

        return new JSONObject(conditionsText.getText());
    }



    private void showOptions(Pane optionsPane) {
        startOptionsPane.setVisible(false);
        loadOptionsPane.setVisible(false);
        if (optionsPane != null) {
            optionsPane.setVisible(true);
        }

    }
}
