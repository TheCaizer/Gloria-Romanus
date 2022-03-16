package unsw.gloriaromanus;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartMenuScreen implements Screen {
    private Stage stage;
    private String title;
    private Scene scene;
    private StartMenuController controller; 


    public StartMenuScreen(Stage stage, StringProperty currentScreen) throws IOException {
        this.stage = stage;
        title = "Gloria Romanus Main Menu";

        FXMLLoader loader = new FXMLLoader(getClass().getResource("start_menu.fxml"));
        scene = new Scene(loader.load(), 600, 400);
        controller = loader.getController();
        controller.setCurrentScreen(currentScreen);
    }

    @Override
    public void start() {
        controller.refresh();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public String getTitle() {
        return title;
    }


}
