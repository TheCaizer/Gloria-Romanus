package unsw.gloriaromanus;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import unsw.gloriaromanus.backend.Province;

public abstract class MenuController {
    private GloriaRomanusController parent;
    private StringProperty currentMenuId;
    private ObjectProperty<Province> selectedSourceProvince;
    private ObjectProperty<Province> selectedTargetProvince;
    
    
    /**
     * performs any needed action before a menu is switched to
     */
    public void refresh(){};

    @FXML
    public void initialize() {
        currentMenuId = new SimpleStringProperty();
        currentMenuId.addListener((property, oldVar, newVar) -> {
            refresh();
        });
        selectedSourceProvince = new SimpleObjectProperty<Province>(null);
        selectedTargetProvince = new SimpleObjectProperty<Province>(null);
    }


    public void setParent(GloriaRomanusController parent) {
        if (parent == null){
            System.out.println("GOT NULL");
        }
        this.parent = parent;
    }

    public GloriaRomanusController getParent() {
        return parent;
    }

    public void bindMenuId(StringProperty currentMenuId) {
        this.currentMenuId.bindBidirectional(currentMenuId);
    }

    public ObjectProperty<Province> getSelectedSourceProvince() {
        return selectedSourceProvince;
    }

    public ObjectProperty<Province> getSelectedTargetProvince() {
        return selectedTargetProvince;
    }

    public void setCurrentMenu(String menuId) {
        currentMenuId.set(menuId);
    }
}
