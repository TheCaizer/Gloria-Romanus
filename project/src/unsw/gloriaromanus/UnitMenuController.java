package unsw.gloriaromanus;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import unsw.gloriaromanus.backend.Province;
import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.units.Unit;
import unsw.gloriaromanus.backend.units.UnitHut;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import javafx.util.Duration;


public class UnitMenuController extends MenuController{
    private ObservableList<String> infantryList = FXCollections.observableArrayList("legionaire", "berserker", "melee infantry", "ranged infantry", "druid");
    private ObservableList<String> cavlaryList = FXCollections.observableArrayList("chariot", "peasant", "horse archer", "horse cavlary", "elephant");
    private ObservableList<String> artilleryList = FXCollections.observableArrayList("ballista", "catapult");
    private ObservableList<String> spearmenList = FXCollections.observableArrayList("pikemen", "javelin-skirmisher");

    @FXML
    private TextArea unitTextArea;
    @FXML
    private Button CavlaryHire;
    @FXML
    private Button InfantryHire;
    @FXML
    private Button ArtilleryHire;
    @FXML
    private Button SpearmenHire;
    @FXML
    private Button buttonCancelTroop;
    @FXML
    private ChoiceBox<String> CavlaryChoice;
    @FXML
    private ChoiceBox<String> InfantryChoice;
    @FXML
    private ChoiceBox<String> ArtilleryChoice;
    @FXML
    private ChoiceBox<String> SpearmenChoice;
    @FXML
    private ListView<UnitHut> viewUnitHut;
    @FXML
    private TextField provinceUnitField;

    @Override
    @FXML
    public void initialize() {
        //initialize the menu
        super.initialize();

        viewUnitHut.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        getSelectedSourceProvince().addListener((property, oldVar, newVar) -> {
            if (newVar == null){
                provinceUnitField.setText("");;
                viewUnitHut.setItems(null);
            } else {
                provinceUnitField.setText((String) newVar.getName());
                refreshNewList();
            }
        });

        CavlaryChoice.setItems(cavlaryList);
        InfantryChoice.setItems(infantryList);
        ArtilleryChoice.setItems(artilleryList);
        SpearmenChoice.setItems(spearmenList);
    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        setCurrentMenu("navigationMenu");
    }
    @FXML
    public void selectedChoiceMenuI(){
        String choice = InfantryChoice.getValue();
        Faction curr = getParent().getCurrPlayerFaction();
        Unit unit = curr.getUnitFactory().getUnit(choice);
        String stat = getUnitStat(unit, curr, choice);
        Tooltip tooltip = getNewTooltip(stat);
        InfantryChoice.setTooltip(tooltip);
    }
    @FXML
    public void selectedChoiceMenuA(){
        String choice = ArtilleryChoice.getValue();
        Faction curr = getParent().getCurrPlayerFaction();
        Unit unit = curr.getUnitFactory().getUnit(choice);
        String stat = getUnitStat(unit, curr, choice);
        Tooltip tooltip = getNewTooltip(stat);
        ArtilleryChoice.setTooltip(tooltip);
    }
    @FXML 
    public void selectedChoiceMenuC(){
        String choice = CavlaryChoice.getValue();
        Faction curr = getParent().getCurrPlayerFaction();
        Unit unit = curr.getUnitFactory().getUnit(choice);
        String stat = getUnitStat(unit, curr, choice);
        Tooltip tooltip = getNewTooltip(stat);
        CavlaryChoice.setTooltip(tooltip);
    }
    @FXML 
    public void selectedChoiceMenuS(){
        String choice = SpearmenChoice.getValue();
        Faction curr = getParent().getCurrPlayerFaction();
        Unit unit = curr.getUnitFactory().getUnit(choice);
        String stat = getUnitStat(unit, curr, choice);
        Tooltip tooltip = getNewTooltip(stat);
        SpearmenChoice.setTooltip(tooltip);
    }
    @FXML
    public void clickInfantry(){
        String choice = InfantryChoice.getValue();
        if(choice == null){
            appendToTerminal("Please choose a valid unit");
            return;
        }
        Province src  = getSelectedSourceProvince().get();
        Faction curr = getParent().getCurrPlayerFaction();
        AttemptHireUnit(choice, curr, src);
    }

    @FXML
    public void clickCavlary(){
        String choice = CavlaryChoice.getValue();
        if(choice == null){
            appendToTerminal("Please choose a valid unit");
            return;
        }
        Province src  = getSelectedSourceProvince().get();
        Faction curr = getParent().getCurrPlayerFaction();
        AttemptHireUnit(choice, curr, src);
    }
    @FXML
    public void clickArtillery(){
        String choice = ArtilleryChoice.getValue();
        if(choice == null){
            appendToTerminal("Please choose a valid unit");
            return;
        }
        Province src  = getSelectedSourceProvince().get();
        Faction curr = getParent().getCurrPlayerFaction();
        AttemptHireUnit(choice, curr, src);
    }
    @FXML
    public void clickSpearmen(){
        String choice = SpearmenChoice.getValue();
        if(choice == null){
            appendToTerminal("Please choose a valid unit");
            return;
        }
        Province src  = getSelectedSourceProvince().get();
        Faction curr = getParent().getCurrPlayerFaction();
        AttemptHireUnit(choice, curr, src);
    }

    @FXML
    public void clearTextArea(){
        unitTextArea.clear();
    }

    @FXML
    public void clickCancelTroop(){
        Province src = getSelectedSourceProvince().get();
        if(src == null){
            appendToTerminal("You haven't selected a province");
            return;
        }
        UnitHut hut = viewUnitHut.getSelectionModel().getSelectedItem();
        if(hut == null){
            appendToTerminal("You haven't selected a troop to cancel");
            return;
        }
        Unit u = viewUnitHut.getSelectionModel().getSelectedItem().getUnit();
        if(u == null){
            appendToTerminal("You haven't selected a troop to cancel");
            return;
        }
        src.cancelUnitHire(u, src.getFaction().getTreasury());
        appendToTerminal("You have cancelled "+ u.getName() + " and refunded " + Integer.toString((int)((double)src.getFaction().adjustUnitCost(u.getCost()) * 0.9)));
        viewUnitHut.refresh();
        refreshNewList();
    }

    public void appendToTerminal(String message) {
        unitTextArea.appendText(message + "\n");
    }

    public void AttemptHireUnit(String unit, Faction faction, Province province){
        if(province == null){
            appendToTerminal("Please select an ally province");
            return;
        }
        boolean success = province.hireUnits(faction.getTreasury(), faction.getUnitFactory().getUnit(unit));
        if(success){
            appendToTerminal(province.getName() + " has hired " + unit);
            viewUnitHut.refresh();
            refreshNewList();
        }
        else{
            appendToTerminal(province.getName() + " couldn't hired " + unit);
            if (faction.getTreasury().getBalance() < faction.adjustConstrCost(faction.getUnitFactory().getUnit(unit).getCost())){
                appendToTerminal("Not enough treasury gold");
            }
            else{
                appendToTerminal("Not enough space in Unit Hut");
            }
        }
    }

    public String getUnitStat(Unit u, Faction f, String choice){
        int cost = f.adjustUnitCost(u.getCost());
        String stat1 = "\t\t\t\t" + choice.substring(0, 1).toUpperCase() + choice.substring(1) + "\t\t\t\t" + "\n";
        String stat2 = "Number of Troops: " + Integer.toString(u.getNumTroops()) + "\t" + "Cost: " + cost + "\n";
        String stat3 = "Range: " + u.getRange() + "\t\t\t" + "Turn to Produce: " + Integer.toString(u.getTurnToProduce())+ "\n";
        String stat5 = "Armour: " + Integer.toString(u.getArmour()) + "\t\t\t" + "Morale: " + Integer.toString(u.getMorale())+ "\n";
        String stat4 = "Attack: " + Integer.toString(u.getAttack()) + "\t\t\t" + "Speed:  " + Integer.toString(u.getSpeed())+ "\n";
        String stat6 = "Movement Points: " + Integer.toString(u.getMovement()) + "\t\t" + "Charge: " + Integer.toString(u.getCharge())+ "\n";
        String stat7 = "Defense Skill: " + Integer.toString(u.getArmour()) + "\t\t" + "Shield Defense: " + Integer.toString(u.getShieldDefense())+ "\n";
        String result = stat1 + stat2 + stat3 + stat4 + stat5 + stat6 + stat7;
        return result;
    }

    public void refreshNewList(){

        ObservableList<UnitHut> data = FXCollections.observableArrayList();

        for(UnitHut u: getSelectedSourceProvince().get().getUnitHut()){
            if(u.getUnit() != null){
                data.add(u);
            }
        }

        viewUnitHut.setItems(data);

        viewUnitHut.setCellFactory(new Callback<ListView<UnitHut>, ListCell<UnitHut>>() {
            @Override 
            public ListCell<UnitHut> call(ListView<UnitHut> list) {
                return new CustomListCell();
            }
        });
    }

    public Tooltip getNewTooltip(String stat){
        Tooltip tooltip = new Tooltip(stat);
        tooltip.setFont(new Font(20.0));
        tooltip.setShowDuration(Duration.INDEFINITE);
        tooltip.setShowDelay(Duration.seconds(0.1));
        return tooltip;
    }
}

class CustomListCell extends ListCell<UnitHut> {
    public CustomListCell() {
        super();
    }

    @Override
    protected void updateItem(UnitHut item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            setText(item.getUnit().getName() + " Turn Finish Training: " + Integer.toString(item.getTurnFinish()));
        }
        else{
            setText(null);
        }
    }
}