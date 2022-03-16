package unsw.gloriaromanus;
import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GloriaRomanusScreen implements Screen {
    private Stage stage;
    private String title;
    private GloriaRomanusController controller = null;
    private StringProperty currentScreen; // Storing to pass to controller


    public GloriaRomanusScreen(Stage stage, StringProperty currentScreen) throws IOException {
        this.stage = stage;
        title = "Gloria Romanus";
        this.currentScreen = currentScreen;
    }


    @Override
    public void start() {
        Scene scene = null;
        FXMLLoader sceneLoader = new FXMLLoader(getClass().getResource("main.fxml"));

        try {
            scene =  new Scene(sceneLoader.load(), 800, 700);
            controller = sceneLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        controller.setCurrentScreen(currentScreen);
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void terminate() {
        if (controller != null) {
            controller.terminate();
            controller = null;
        }
    }
}
