package unsw.gloriaromanus;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class NavigationMenuController extends MenuController {

    boolean hidden = false;

    @FXML
    private Label selectMenu;

    @FXML
    private VBox navigationMenu;

    @FXML
    private Button taxMenu;

    @FXML
    private Button optionMenu;

    @FXML
    private Button invasionMenu;

    @FXML
    private Button unitMenu;

    @FXML
    private Button buildingMenu;

    @FXML
    private Button victoryMenu;

    @FXML
    private Button exitMenu;

    @FXML
    private ToggleButton hideMenu;

    MediaPlayer mediaPlayer;

    @FXML
    public void handleHideMenu(){
        if(!hidden){
            hideEverything();
            hidden = true;
        }
        else{
            showEverything();
            hidden = false;
        }
    }

    @FXML
    public void handleBuildingsMenuSwitch(ActionEvent event) {
        setCurrentMenu("buildingsMenu");
        if (mediaPlayer != null) mediaPlayer.stop();
        playBuildingMusic();
    }

    @FXML
    public void handleInvasionMenuSwitch(ActionEvent event) {
        setCurrentMenu("invasionMenu");
        if (mediaPlayer != null) mediaPlayer.stop();
        playInvasionMusic();
    }

    @FXML
    public void handleTaxMenuSwitch(ActionEvent event) {
        setCurrentMenu("taxMenu");
        if (mediaPlayer != null) mediaPlayer.stop();
        playTaxMusic();
    }

    @FXML
    public void handleUnitMenuSwitch(ActionEvent event){
        setCurrentMenu("unitMenu");
        if (mediaPlayer != null) mediaPlayer.stop();
        playUnitMusic();
    }
    public void handleOptionsMenuSwitch(ActionEvent event){
        setCurrentMenu("optionsMenu");
    }

    @FXML
    public void handleVictoryMenuSwitch() {
        setCurrentMenu("victoryMenu");
        if (mediaPlayer != null) mediaPlayer.stop();
        playVictoryMusic();
    }


    @FXML
    public void handleExit() {
        if (mediaPlayer != null) mediaPlayer.stop();
        getParent().exitToMenu();
    }

    public void hideEverything(){
        navigationMenu.setStyle("-fx-background-color: transparent;");
        selectMenu.setVisible(false);
        exitMenu.setVisible(false);
        victoryMenu.setVisible(false);
        buildingMenu.setVisible(false);
        unitMenu.setVisible(false);
        invasionMenu.setVisible(false);
        optionMenu.setVisible(false);
        taxMenu.setVisible(false);
    }

    public void showEverything(){
        navigationMenu.setStyle(null);
        selectMenu.setVisible(true);
        exitMenu.setVisible(true);
        victoryMenu.setVisible(true);
        buildingMenu.setVisible(true);
        unitMenu.setVisible(true);
        invasionMenu.setVisible(true);
        optionMenu.setVisible(true);
        taxMenu.setVisible(true);
    }

    public void playUnitMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/UnitMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        mediaPlayer.setVolume(0.05);
        mediaPlayer.play();
    }

    public void playVictoryMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/VictoryMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        mediaPlayer.setVolume(0.3);
        mediaPlayer.play();
    }

    public void playInvasionMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/InvasionMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        mediaPlayer.setVolume(0.03);
        mediaPlayer.play();
    }

    public void playBuildingMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/BuildingMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0.1); 
        mediaPlayer.play();
    }

    public void playTaxMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/TaxMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        mediaPlayer.setVolume(0.1); 
        mediaPlayer.play();
    }
}
