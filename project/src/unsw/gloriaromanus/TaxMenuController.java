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
import javafx.util.Duration;
import unsw.gloriaromanus.backend.Province;

public class TaxMenuController extends MenuController {

    private ObservableList<String> taxList = FXCollections.observableArrayList("Low Tax", "Normal Tax", "High Tax", "Very High Tax");

    @FXML
    private ChoiceBox<String> taxChoiceBox;

    @FXML
    private TextField textFactionWealth;

    @FXML
    private TextField textProvinceWealth;

    @FXML
    private TextField provinceTaxField;

    @FXML
    private Button buttonTax;

    @FXML
    private Button taxClear;

    @FXML
    private TextArea taxOutput;

    @FXML 
    private TextField textGrowth;

    Tooltip LowTooltip = new Tooltip("Low Tax Rate: 10%");
    Tooltip NormalTooltip = new Tooltip("Normal Tax Rate: 15%");
    Tooltip HighTooltip = new Tooltip("High Tax Rate: 20%");
    Tooltip VeryHighTooltip = new Tooltip("Very High Rate 25%");

    @Override
    @FXML
    public void initialize() {
        //initialize the menu
        super.initialize();
        //set the items just to see the tax rate
        taxChoiceBox.setItems(taxList);
        //set the province wealth and faction wealth to nothing
        textProvinceWealth.setText("");
        textFactionWealth.setText("");
        textGrowth.setText("");
        initializeTooltip();
        //listener once you select a province show the current tax rate in the choice box
        //button to select tax rate and province wealth
        getSelectedSourceProvince().addListener((property, oldVar, newVar) -> {
            if (newVar != null){
                String currTax = newVar.getTaxBuff();
                taxChoiceBox.setValue(currTax);
                taxChoiceBox.setItems(taxList);
                textFactionWealth.setText(Integer.toString(getSelectedSourceProvince().get().getFaction().getFactionWealth()) + " g");
                textProvinceWealth.setText(Integer.toString(getSelectedSourceProvince().get().getProvinceWealth())+ " g");
                textGrowth.setText(Integer.toString(getSelectedSourceProvince().get().getProvinceGrowth())+ " g");
                provinceTaxField.setText((String) newVar.getName());
                if(getSelectedSourceProvince().get().getTaxBuff().equals("Low Tax")){
                    taxChoiceBox.setTooltip(LowTooltip);
                }
                else if(getSelectedSourceProvince().get().getTaxBuff().equals("Normal Tax")){
                    taxChoiceBox.setTooltip(NormalTooltip);
                }
                else if(getSelectedSourceProvince().get().getTaxBuff().equals("High Tax")){
                    taxChoiceBox.setTooltip(HighTooltip);
                }
                else if(getSelectedSourceProvince().get().getTaxBuff().equals("Very High Tax")){
                    taxChoiceBox.setTooltip(VeryHighTooltip);
                }
            }
            else{
                provinceTaxField.setText("");
                taxChoiceBox.setValue(null);
                taxChoiceBox.setItems(taxList);
                textFactionWealth.setText("");
                textProvinceWealth.setText("");
                textGrowth.setText("");
            }
        });
    }

    @FXML
    public void clearTextArea(){
        taxOutput.clear();
    }

    @FXML
    public void handleTaxChoice(){
        String tax = taxChoiceBox.getValue();
        Province src  = getSelectedSourceProvince().get();
        if (src == null) {
            appendToTerminal("Select an ally province");
            return;
        }
        String name = src.getName();
        src.setTaxRate(tax);

        if(getSelectedSourceProvince().get().getTaxBuff().equals("Low Tax")){
            taxChoiceBox.setTooltip(LowTooltip);
            appendToTerminal(name + " has changed tax rate to Low Tax and now taxes 10%");
        }
        else if(getSelectedSourceProvince().get().getTaxBuff().equals("Normal Tax")){
            taxChoiceBox.setTooltip(NormalTooltip);
            appendToTerminal(name + " has changed tax rate to Normal Tax and now taxes 15%");
        }
        else if(getSelectedSourceProvince().get().getTaxBuff().equals("High Tax")){
            taxChoiceBox.setTooltip(HighTooltip);
            appendToTerminal(name + " has changed tax rate to High Tax and now taxes 20%");
        }
        else if(getSelectedSourceProvince().get().getTaxBuff().equals("Very High Tax")){
            taxChoiceBox.setTooltip(VeryHighTooltip);
            appendToTerminal(name + " has changed tax rate to Very High Tax and now taxes 25% but units lose 1 morale");
        }
        textGrowth.setText(Integer.toString(getSelectedSourceProvince().get().getProvinceGrowth())+ " g");
    }

    @Override
    public void refresh() {
        textFactionWealth.setText(getParent().getCurrPlayerFaction().getFactionWealth() + " g");
    }

    @FXML
    public void clickedSwitchMenu(ActionEvent e) throws Exception {
        setCurrentMenu("navigationMenu");
    }

    public void initializeTooltip(){
        LowTooltip.setFont(new Font(15.0));
        LowTooltip.setShowDuration(Duration.INDEFINITE);
        LowTooltip.setShowDelay(Duration.seconds(0.1));
        NormalTooltip.setFont(new Font(15.0));
        NormalTooltip.setShowDuration(Duration.INDEFINITE);
        NormalTooltip.setShowDelay(Duration.seconds(0.1));
        HighTooltip.setFont(new Font(15.0));
        HighTooltip.setShowDuration(Duration.INDEFINITE);
        HighTooltip.setShowDelay(Duration.seconds(0.1));
        VeryHighTooltip.setFont(new Font(15.0));
        VeryHighTooltip.setShowDuration(Duration.INDEFINITE);
        VeryHighTooltip.setShowDelay(Duration.seconds(0.1));
    }

    public void appendToTerminal(String message) {
        taxOutput.appendText(message + "\n");
    }
}
