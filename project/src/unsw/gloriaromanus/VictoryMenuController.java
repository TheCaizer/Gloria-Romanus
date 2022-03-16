package unsw.gloriaromanus;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class VictoryMenuController extends MenuController{


    @FXML
    private VBox victoryMenu;
    
    @FXML
    private TextArea victoryProgressTextArea;

    @Override
    public void refresh() {
        victoryProgressTextArea.clear();
        victoryProgressTextArea.setText(getParent().getCurrPlayerFaction().getVictoryString());
    }

    @FXML
    public void clickedSwitchMenu() {
        setCurrentMenu("navigationMenu");
    }
}
