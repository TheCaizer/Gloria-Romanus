package unsw.gloriaromanus;

import java.io.IOException;
import java.util.List;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.Duration;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.exceptions.DefeatException;
import unsw.gloriaromanus.backend.exceptions.InvasionException;
import unsw.gloriaromanus.backend.exceptions.MovementException;
import unsw.gloriaromanus.backend.units.Unit;

public class InvasionMenuController extends MenuController{
    @FXML
    private TextField invading_province; 
    @FXML
    private TextField opponent_province;
    @FXML
    private TextArea output_terminal;
    @FXML
    private ListView<Unit> unitsListView;

    @FXML
    private Button unitActionButton;

    @FXML
    private CheckBox raidModeCheckBox;

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        unitsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        getSelectedSourceProvince().addListener((property, oldVar, newVar) -> {
            if (newVar == null){
                invading_province.setText("");
                unitsListView.setItems(null);
            } else {
                invading_province.setText((String) newVar.getName());
                unitsListView.setItems(newVar.getArmy());
                unitsListView.setCellFactory((new Callback<ListView<Unit>, ListCell<Unit>>() {
                    @Override 
                    public ListCell<Unit> call(ListView<Unit> list) {
                        return new CustomListCell2();
                    }
                }));
            }
        });

        getSelectedTargetProvince().addListener((property, oldVar, newVar) -> {
            if (newVar == null) opponent_province.setText("");
            else {
                opponent_province.setText((String) newVar.getName());
                if (getSelectedSourceProvince().get() != null 
                 && getSelectedSourceProvince().get().getFaction() == newVar.getFaction()) {
                    unitActionButton.setText("Move Units");
                    raidModeCheckBox.setVisible(false);
                 } else {
                    raidModeCheckBox.setVisible(true);
                    unitActionButton.setText("Invade");
                }
            }
        });
    }

    public void appendToTerminal(String message) {
        output_terminal.appendText(message + "\n");
    }

    @FXML
    public void handleUnitAction(ActionEvent e) throws IOException {
        output_terminal.clear();
        Province src = getSelectedSourceProvince().get();
        Province dst = getSelectedTargetProvince().get();
        //getParent().clickedInvadeButton(e);
        if (src == null || dst == null) {
            appendToTerminal("You need to select a Source and a target province");
            return;
        }
        List<Unit> selected = unitsListView.getSelectionModel().getSelectedItems();
        if (selected.size() < 1) {
            appendToTerminal("You haven't selected any units!");
            return;
        }

        try {
            if (src.getFaction() == dst.getFaction()) {
                // Unit Movement
                src.moveUnits(selected, dst);
            } else {
                //Invasion
                src.invadeProvince(selected, dst, raidModeCheckBox.isSelected(),  (x) -> appendToTerminal(x));
            }
        } catch (InvasionException | MovementException ex ) {
            //ex.printStackTrace();
            appendToTerminal(ex.getMessage());
        } catch (DefeatException ex) {
            Alert errorAlert = new Alert(AlertType.INFORMATION);
            errorAlert.setHeaderText("Defeat!");
            errorAlert.setContentText(ex.getMessage());
            errorAlert.showAndWait();
            return;
        }
        unitsListView.refresh();
    }

    @Override
    public void refresh() {
        unitsListView.refresh();
        output_terminal.clear();
    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        setCurrentMenu("navigationMenu");
    }

}

class CustomListCell2 extends ListCell<Unit> {
    public CustomListCell2() {
        super();
    }

    @Override
    protected void updateItem(Unit item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            String choice = item.getName();
            String stat1 = "\t\t\t\t" + choice.substring(0, 1).toUpperCase() + choice.substring(1) + "\t\t\t\t" + "\n";
            String stat2 = "Number of Troops: " + Integer.toString(item.getNumTroops()) + "\t\t\t"  + "Type: " + item.getType() + "\n";
            String stat3 = "Range: " + item.getRange() + "\t\t\t"  + "Number of Engagement: " + item.getNumEngagement() + "\n";
            String stat5 = "Armour: " + Integer.toString(item.getArmour()) + "\t\t\t\t" + "Morale: " + Integer.toString(item.getMorale())+ "\n";
            String stat4 = "Attack: " + Integer.toString(item.getAttack()) + "\t\t\t\t" + "Speed:  " + Integer.toString(item.getSpeed())+ "\n";
            String stat6 = "Movement Points: " + Integer.toString(item.getMovement()) + "\t" + "Charge: " + Integer.toString(item.getCharge())+ "\n";
            String stat7 = "Defense Skill: " + Integer.toString(item.getArmour()) + "\t\t\t" + "Shield Defense: " + Integer.toString(item.getShieldDefense())+ "\n";
            String stat8 = "Broken: " + item.isBroken() + "\t\t\t" + "Attacked: " + item.hasAttacked() +"\n";

            String result = stat1 + stat2 + stat3 + stat4 + stat5 + stat6 + stat7 + stat8;
            setText(item.toString());
            Tooltip tooltip = new Tooltip(result);
            tooltip.setFont(new Font(20.0));
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setShowDelay(Duration.seconds(0.1));
            setTooltip(tooltip);
        }
        else{
            setText(null);
            setTooltip(null);
        }
    }
}