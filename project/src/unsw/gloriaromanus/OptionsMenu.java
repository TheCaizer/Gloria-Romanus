package unsw.gloriaromanus;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import unsw.gloriaromanus.backend.GameData;

public class OptionsMenu extends MenuController {

    @FXML
    private VBox optionsMenu;

    @FXML
    private TextField saveTextField;

    @FXML
    private ListView<String> saveFileListView;

    @FXML
    private Slider volumeSlider;

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        saveFileListView.getItems().addAll(GameData.getSaves());
        saveFileListView.getSelectionModel().selectedItemProperty().addListener((property, oldVar, newVar) -> {
            saveTextField.setText(newVar);
        });
    }

    @FXML
    void clickedSwitchMenu(ActionEvent event) {
        setCurrentMenu("navigationMenu");
    }

    @FXML
    void onVolumeChange(){
        double value = volumeSlider.getValue();
        getParent().getMediaPlayer().setVolume(value);
    }

    @FXML
    public void handleSaveGame() {
        String saveFileName = saveTextField.getText();
        if (saveFileName == null || saveFileName.equals("")) {
            Alert errorAlert = new Alert(AlertType.WARNING);
            errorAlert.setHeaderText("Invalid Save name");
            errorAlert.setContentText("Please enter a valid save name");
            errorAlert.showAndWait();
            return;
        }
        boolean result = getParent().getGameData().saveGame(saveFileName);
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setHeaderText("Save " + ((result) ? "Successful" : "Failed"));
        alert.showAndWait();
        refresh();
    }

    @Override
    public void refresh() {
        saveTextField.clear();
        saveFileListView.getItems().clear();
        saveFileListView.getItems().addAll(GameData.getSaves());
        volumeSlider.setValue(getParent().getMediaPlayer().getVolume());
    }
}
