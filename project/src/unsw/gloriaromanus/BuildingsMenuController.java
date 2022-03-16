package unsw.gloriaromanus;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.exceptions.InfrastructureException;
import unsw.gloriaromanus.backend.infrastructure.Building;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class BuildingsMenuController extends MenuController {

    @FXML
    private VBox buildingsMenu;

    @FXML
    private TextField buildingsSourceText;

    @FXML
    private TextField currentConstructionText;

    @FXML
    private ComboBox<String> buildingsChoiceBox;

    @FXML
    private Label levelLabel;

    @FXML
    private Label costLabel;

    @FXML
    private Label timeLabel;

    @FXML
    private Label refundLabel;

    @FXML
    private TextArea buildingsOutput;
    

    @Override
    @FXML
    public void initialize() {
        super.initialize();

        getSelectedSourceProvince().addListener((property, oldVar, newVar) -> {
            if (newVar == null){
                buildingsSourceText.setText("");
                buildingsChoiceBox.getItems().clear();
                currentConstructionText.setText("");
            } else {
                buildingsSourceText.setText((String) newVar.getName());
                currentConstructionText.setText(newVar.getCurrentConstructionInfo());
                buildingsChoiceBox.getItems().addAll(newVar.getBuildings().keySet());
                buildingsChoiceBox.setCellFactory(x -> {
                    return new ListCell<String>() {
                        @Override
                        public void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(item);
                                setTooltip(null);
                            } else {
                                setText(item);
                                setTooltip(getBuildingToolTip(item));
                            }
                        }
                    };
                });
                
            }
            buildingsOutput.clear();
        });
        buildingsChoiceBox.valueProperty().addListener((property, oldVar, newVar) -> {
            buildingsChoiceBox.setTooltip(getBuildingToolTip(newVar));
        }
        );
        buildingsChoiceBox.valueProperty().addListener((property, oldVar, newVar) -> {
            setBuildingLabels(newVar);
        });
    }


    public void appendToTerminal(String message) {
        buildingsOutput.appendText(message + "\n");
    }

    @FXML
    public void handleConstructionClick (MouseEvent event) {
        if (getSelectedSourceProvince().get() == null || currentConstructionText.getText().equals("")) return;
        buildingsOutput.clear();

        if (event.getClickCount() == 1) {
            appendToTerminal("Double click to cancel construction");
        } else {
            appendToTerminal("Construction cancelled!");
            appendToTerminal("Refunded for " + getSelectedSourceProvince().get().refundConstruction() + "g");
            currentConstructionText.setText("");
        }
    }

    @FXML
    public void handleBuildingConstruct(ActionEvent event) {
        String selected = buildingsChoiceBox.getValue();
        Province currP = getSelectedSourceProvince().get();
        if (selected == null || currP == null) return;

        try {
            boolean isSuccess = currP.upgradeBuilding(selected);
            if (isSuccess) {
                appendToTerminal(selected + " Construction Commenced!");
                currentConstructionText.setText(currP.getCurrentConstructionInfo());
            } else appendToTerminal(selected + " Construction failed");
        } catch (InfrastructureException e) {
            appendToTerminal(e.getMessage());
        }
    }

    @FXML
    public void handleBuildingDestroy(ActionEvent event) {
        String selected = buildingsChoiceBox.getValue();
        Province currP = getSelectedSourceProvince().get();
        if (selected == null || currP == null) return;
        if (currP.getBuildings(selected).getLevel() == 0) return;
        buildingsOutput.clear();
        appendToTerminal(selected + " Destroyed: " + currP.destroyBuilding(selected) + "g refunded.");
        buildingsChoiceBox.getSelectionModel().clearSelection();
    }

    @Override
    public void refresh() {
        buildingsChoiceBox.getSelectionModel().clearSelection();
        buildingsOutput.clear();
        //current construction

    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        setCurrentMenu("navigationMenu");
    }


    private void setBuildingLabels(String building) {
        if (building == null || getSelectedSourceProvince().get() == null) {
            levelLabel.setText("");
            costLabel.setText("");
            timeLabel.setText("");
            refundLabel.setText("");
            return;
        } 
        Building b = getSelectedSourceProvince().get().getBuildings(building);
        levelLabel.setText(b.getLevel()+ "");
        refundLabel.setText(getSelectedSourceProvince().get().getBuildingRefundAmound(building) + "");
        if (b.isMaxLevel()) {
            costLabel.setText("N/A");
            timeLabel.setText("N/A");
            //Hide upgrade button
        } else {
            costLabel.setText(b.getUpgradeCost() + "");
            timeLabel.setText(b.getUpgradeTime() + "");
        }
    }

    private Tooltip getBuildingToolTip(String buildingName) {
        if (buildingName == null) return null;
        String message = getSelectedSourceProvince().get().getBuildings(buildingName).toString();
        var tip = new Tooltip(message);
        tip.setFont(new Font(15.0));
        tip.setShowDuration(Duration.INDEFINITE);
        tip.setShowDelay(Duration.seconds(0.8));
        tip.setTextAlignment(TextAlignment.CENTER);
        return tip;
    }


}