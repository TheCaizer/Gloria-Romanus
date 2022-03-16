package unsw.gloriaromanus;

import java.io.IOException;
import java.util.HashMap;

import javafx.application.Application;

import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
  

public class GloriaRomanusApplication extends Application implements ChangeListener<String> {

    private Stage stage;
    private StringProperty currentScreen;
    private HashMap<String, Screen> screens;


    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        currentScreen = new SimpleStringProperty("Gloria Romanus Main Menu");

        screens = new HashMap<String, Screen>();

        // adding the screens to this application


        var gameScreen = new GloriaRomanusScreen(stage, currentScreen);
        screens.put(gameScreen.getTitle(), gameScreen);

        var startScreen = new StartMenuScreen(stage, currentScreen);
        screens.put(startScreen.getTitle(), startScreen);

        
        // set up the stage
        currentScreen.addListener(this);
        startScreen.start();
    }
    

    /**
     * Stops and releases all resources used in application.
     */
    @Override
    public void stop() {
        stage.close();
        screens.forEach((key, screen) -> screen.terminate());
    }

    @Override
    public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
        if (screens.containsKey(arg2)) {
            screens.get(arg2).start();
            screens.get(arg1).terminate();
        }
    }

    /**
     * Opens and runs application.
     *
     * @param args arguments passed to this application
     */
    public static void main(String[] args) {
        Application.launch(args);
    }
}