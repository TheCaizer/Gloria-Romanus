package unsw.gloriaromanus;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;

import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.GeoPackage;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol;
import com.esri.arcgisruntime.symbology.TextSymbol.HorizontalAlignment;
import com.esri.arcgisruntime.symbology.TextSymbol.VerticalAlignment;
import com.esri.arcgisruntime.data.Feature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.geojson.FeatureCollection;
import org.geojson.LngLatAlt;

import unsw.gloriaromanus.backend.Faction;
import unsw.gloriaromanus.backend.GameData;
import unsw.gloriaromanus.backend.Province;

import unsw.gloriaromanus.backend.exceptions.ConditionsParseException;

public class GloriaRomanusController {

    @FXML
    private MapView mapView;

    @FXML
    private StackPane stackPaneMain;

    @FXML
    private Label turnNumLabel;
    
    @FXML
    private Label currentFactionLabel;


    @FXML
    private Label factionTreasuryLabel;

    @FXML
    private ImageView factionIcon;


    private ArcGISMap map;

    private Faction currPlayerFaction;
    private IntegerProperty currFactionBalance;

    private ObjectProperty<Feature> selectedSourceProperty;
    private ObjectProperty<Feature> selectedTargetProperty;

    private ObjectProperty<Province> selectedSourceProvince;
    private ObjectProperty<Province> selectedTargetProvince;

    private FeatureLayer featureLayer_provinces;

    //Current Game State
    private GameData gameData;

    //Image Storage
    private SpriteFactory spriteFactory;

    private StringProperty currentMenuId;
    
    private StringProperty currentScreen;

    private MediaPlayer mediaPlayer;

    @FXML
    private void initialize() throws JsonParseException, JsonMappingException, IOException, InterruptedException, ConditionsParseException {
        currentScreen = new SimpleStringProperty();
        this.gameData = GameData.getGameData("src/configs/gameConfig.json");

        turnNumLabel.setText(gameData.getTurn().getTurnNum() + "");
        gameData.getTurn().weaklyAttach( (x) -> {
            turnNumLabel.setText(x.getTurnNum() + ""); return false;
        });



        this.spriteFactory = new SpriteFactory();
        this.currentMenuId = new SimpleStringProperty();
        currentMenuId.addListener((property, oldVar, newVar) -> {
            stackPaneMain.getChildren().forEach((x) -> {
                if (x.getId().equals(newVar)) {
                    x.setVisible(true);
                } else if (x.getId().contains("Menu")) x.setVisible(false);
            });
        });

        this.currFactionBalance = new SimpleIntegerProperty();
        currFactionBalance.addListener( (property, oldVar, newVar) ->
        {
            if (newVar == null) factionTreasuryLabel.setText("Treasury: 0g");
            else factionTreasuryLabel.setText("Treasury: "+ newVar +"g");
        });
        

        selectedSourceProvince = new SimpleObjectProperty<Province> (null);
        selectedTargetProvince = new SimpleObjectProperty<Province> (null);

        selectedSourceProperty = new SimpleObjectProperty<Feature>(null);
        selectedTargetProperty = new SimpleObjectProperty<Feature>(null);

        selectedSourceProperty.addListener((property, oldVar, newVar) -> {
            selectedSourceProvince.set(featureToProvince(newVar));
        });

        selectedTargetProperty.addListener((property, oldVar, newVar) -> {
            selectedTargetProvince.set(featureToProvince(newVar));
        });

        currPlayerFaction = gameData.getCurrFaction();
        currentFactionLabel.setText(currPlayerFaction.getName());
        factionIcon.setImage(spriteFactory.getFactionSprite(currPlayerFaction.getName()));

        currFactionBalance.bind(currPlayerFaction.getBalanceProperty());
        String[] menus = { "invasion_menu.fxml", "tax_menu.fxml", "navigation_menu.fxml", 
        "buildings_menu.fxml", "Unit_menu.fxml", "options_menu.fxml", "victory_menu.fxml" };
        for (String fxmlName : menus) {
            //System.out.println(fxmlName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
            VBox root = (VBox) loader.load();
            MenuController menuController = (MenuController) loader.getController();
            menuController.bindMenuId(currentMenuId);
            //uniDirectional bindings
            menuController.getSelectedSourceProvince().bind(selectedSourceProvince);
            menuController.getSelectedTargetProvince().bind(selectedTargetProvince);
            root.setVisible(false);
            stackPaneMain.getChildren().add(root);
            menuController.setParent(this);
        }


        initializeProvinceLayers();
        playBackgroundMusic();
        currentMenuId.set("navigationMenu");


    }

    /*
    * code to play the background music
    */
    public void playBackgroundMusic(){
        //I think unbuntu needs to use wav files and cant use mp3
        //Also need to add the media module to launch.json to play music
        String musicPath = "images/BackgroundMusic.wav";
        Media media = new Media(new File(musicPath).toURI().toString());  
        mediaPlayer = new MediaPlayer(media); 
        //this line of code loops the music
        mediaPlayer.setOnEndOfMedia(new Runnable() {
            public void run() {
                mediaPlayer.seek(Duration.ZERO);
            }
        });
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();
    }

    /**
     * run this initially to update province owner, change feature in each
     * FeatureLayer to be visible/invisible depending on owner. Can also update
     * graphics initially
     */
    private void initializeProvinceLayers() throws JsonParseException, JsonMappingException, IOException {

        Basemap myBasemap = Basemap.createImagery();
        // myBasemap.getReferenceLayers().remove(0);
        map = new ArcGISMap(myBasemap);
        mapView.setMap(map);

        // note - tried having different FeatureLayers for AI and human provinces to
        // allow different selection colors, but deprecated setSelectionColor method
        // does nothing
        // so forced to only have 1 selection color (unless construct graphics overlays
        // to give color highlighting)
        GeoPackage gpkg_provinces = new GeoPackage("src/configs/provinces_right_hand_fixed.gpkg");
        gpkg_provinces.loadAsync();
        gpkg_provinces.addDoneLoadingListener(() -> {
            if (gpkg_provinces.getLoadStatus() == LoadStatus.LOADED) {
                // create province border feature
                featureLayer_provinces = createFeatureLayer(gpkg_provinces);
                map.getOperationalLayers().add(featureLayer_provinces);

            } else {
                System.out.println("load failure");
            }
        });

        addAllPointGraphics();
    }

    private void addAllPointGraphics() throws JsonParseException, JsonMappingException, IOException {
        mapView.getGraphicsOverlays().clear();

        InputStream inputStream = new FileInputStream(new File("src/configs/provinces_label.geojson"));
        FeatureCollection fc = new ObjectMapper().readValue(inputStream, FeatureCollection.class);

        GraphicsOverlay graphicsOverlay = new GraphicsOverlay();

        for (org.geojson.Feature f : fc.getFeatures()) {
            if (f.getGeometry() instanceof org.geojson.Point) {
                org.geojson.Point p = (org.geojson.Point) f.getGeometry();
                LngLatAlt coor = p.getCoordinates();
                Point curPoint = new Point(coor.getLongitude(), coor.getLatitude(), SpatialReferences.getWgs84());

                String provinceName = (String) f.getProperty("name");
                Province province = gameData.getProvince(provinceName);
                String faction = province.getFaction().getName();

                Graphic gPic = new Graphic(curPoint);
                Graphic gText = new Graphic(curPoint);
                Graphic gArmy = new Graphic(curPoint);

                setProvinceFactionPic(gPic, faction);
                setProvinceFactionText(gText, faction, provinceName);
                setProvinceArmySizeText(gArmy, province.getArmy().size());

                province.getArmy().addListener((property, oldV, newV) -> {
                    setProvinceArmySizeText(gArmy, newV.size());
                });

                province.addFactionListener((obs,oldV,newV) -> 
                    {
                        setProvinceFactionPic(gPic, newV.getName());
                        setProvinceFactionText(gText, newV.getName(), provinceName);
                    }
                );
                graphicsOverlay.getGraphics().add(gPic);
                graphicsOverlay.getGraphics().add(gText);
                graphicsOverlay.getGraphics().add(gArmy);
            } else {
                System.out.println("Non-point geo json object in file");
            }
        }
        inputStream.close();
        mapView.getGraphicsOverlays().add(graphicsOverlay);
    }


    private void setProvinceFactionText(Graphic gText, String faction, String province) {
        TextSymbol text = new TextSymbol(10, faction + "\n" + province + "\n\n", 0xFFFF0000,
                HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        text.setHaloColor(0xFFFFFFFF);
        text.setHaloWidth(2);
        gText.setSymbol(text);
    }

    private void setProvinceFactionPic(Graphic gPic, String faction) {
        Image image = spriteFactory.getFactionSprite(faction);
        PictureMarkerSymbol s = new PictureMarkerSymbol(image);

        //Resizing symbol
        double ratio =  image.getHeight()/20;

        s.setWidth((float) (image.getWidth() / ratio));
        s.setHeight((float) (image.getHeight() / ratio));
        gPic.setSymbol(s);
    }

    private void setProvinceArmySizeText(Graphic gArmy, int size) {
        TextSymbol army = new TextSymbol(10, size + "\n", 0xFFFF0000,
        HorizontalAlignment.CENTER, VerticalAlignment.BOTTOM);
        army.setHaloColor(0xFFFFFFFF);
        army.setHaloWidth(2);
        gArmy.setSymbol(army);
    }

    @FXML
    public void handleEndTurn() {
        currentMenuId.set("navigationMenu");
        currFactionBalance.unbind();
        currPlayerFaction.endTurn();
        Faction oldFaction = currPlayerFaction;
        currPlayerFaction = gameData.getNextFaction();
        currentFactionLabel.setText(currPlayerFaction.getName());
        factionIcon.setImage(spriteFactory.getFactionSprite(currPlayerFaction.getName()));
        currFactionBalance.bind(currPlayerFaction.getBalanceProperty());
        resetSelections();

        //System.out.println(oldFaction.getVictoryString());
        if (oldFaction.checkVictory()) {
            initiateVictory(oldFaction);
        }
    }

    public void initiateVictory(Faction faction) {
        var currentSaves = GameData.getSaves();
        //Finding unique save name
        int i = 0;
        while (currentSaves.contains(faction.getName() + "_VICTORY" + i)) i++;
        //disabling victory
        faction.disableVictory();
        gameData.saveGame(faction.getName() + "_VICTORY" + i);
        Alert errorAlert = new Alert(AlertType.INFORMATION);
        
        errorAlert.setHeaderText(faction.getName() + " Victory!");
        errorAlert.setContentText(faction.getName() + " Have Achieved Victory!\n"
                + "To continue this campaign, reload the following save:\n" 
                + faction.getName() + "_VICTORY" + i);
        errorAlert.showAndWait();
        exitToMenu();
        return;
    }

    public GameData getGameData() {
        return gameData;
    }

    private FeatureLayer createFeatureLayer(GeoPackage gpkg_provinces) {
        FeatureTable geoPackageTable_provinces = gpkg_provinces.getGeoPackageFeatureTables().get(0);

        // Make sure a feature table was found in the package
        if (geoPackageTable_provinces == null) {
            System.out.println("no geoPackageTable found");
            return null;
        }

        // Create a layer to show the feature table
        FeatureLayer flp = new FeatureLayer(geoPackageTable_provinces);

        // https://developers.arcgis.com/java/latest/guide/identify-features.htm
        // listen to the mouse clicked event on the map view
        mapView.setOnMouseClicked(e -> {
            // get the screen point where the user clicked or tapped
            Point2D screenPoint = new Point2D(e.getX(), e.getY());

            // specifying the layer to identify, where to identify, tolerance around point,
            // to return pop-ups only, and
            // maximum results
            // note - if select right on border, even with 0 tolerance, can select multiple
            // features - so have to check length of result when handling it
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(flp,
                    screenPoint, 0, false, 25);

            // add a listener to the future
            identifyFuture.addDoneListener(() -> {
                try {
                    // get the identify results from the future - returns when the operation is
                    // complete
                    IdentifyLayerResult identifyLayerResult = identifyFuture.get();
                    // a reference to the feature layer can be used, for example, to select
                    // identified features
                    if (!(identifyLayerResult.getLayerContent() instanceof FeatureLayer)) return;
                    FeatureLayer featureLayer = (FeatureLayer) identifyLayerResult.getLayerContent();
                    // select all features that were identified
                    List<Feature> features = identifyLayerResult.getElements().stream().map(f -> (Feature) f)
                            .collect(Collectors.toList());

                    if (features.size() != 1) {
                        //printMessageToTerminal("Have more than 1 element - you might have clicked on boundary!");
                        return;
                    }
                    // note maybe best to track whether selected...
                    Feature f = features.get(0);

                    String province = (String) f.getAttributes().get("name"); 
    
                    // Primary Click + friendly province to set source province.
                    if (gameData.getProvince(province).getFaction() == currPlayerFaction && e.getButton() == MouseButton.PRIMARY) {
                        if (getSelectedSourceFeature() != null) {
                            featureLayer.unselectFeature(getSelectedSourceFeature());
                            setSelectedSourceFeature(null);
                        }
                        setSelectedSourceFeature(f);
                        featureLayer.selectFeature(getSelectedSourceFeature());

                    // Secondary Click + friendly province || Enemy Province -> set destination province
                    } else {
                        if (getSelectedTargetFeature() != null) {
                            featureLayer.unselectFeature(getSelectedTargetFeature());
                            setSelectedTargetFeature(null);
                        }
                        setSelectedTargetFeature(f);
                        featureLayer.selectFeature(getSelectedTargetFeature());
                    }
                    // if both source and target are the same, dont select target.
                    if (selectedSourceProvince.get() == selectedTargetProvince.get()) {
                        setSelectedTargetFeature(null);
                     }
                } catch (InterruptedException | ExecutionException ex) {
                    // ... must deal with checked exceptions thrown from the async identify
                    // operation
                    System.out.println("InterruptedException occurred");
                }
            });
        });
        return flp;
    }


    private void resetSelections() {
        if (getSelectedSourceFeature() != null) featureLayer_provinces.unselectFeature(getSelectedSourceFeature());
        if (getSelectedTargetFeature() != null) featureLayer_provinces.unselectFeature(getSelectedTargetFeature());
        setSelectedSourceFeature(null);
        setSelectedTargetFeature(null);
    }

    /**
     * Stops and releases all resources used in application.
     */
    public void terminate() {
        if (mapView != null) {
            mapView.dispose();
        }
    }


    public Faction getCurrPlayerFaction() {
        return currPlayerFaction;
    }

    private Feature getSelectedSourceFeature() {
        return selectedSourceProperty.get();
    }

    private void setSelectedSourceFeature(Feature selectedSourceFeature) {
        selectedSourceProperty.set(selectedSourceFeature);
    }

    private Feature getSelectedTargetFeature() {
        return selectedTargetProperty.get();
    }

    private void setSelectedTargetFeature(Feature selectedTargetFeature) {
        selectedTargetProperty.set(selectedTargetFeature);
    }

    private Province featureToProvince(Feature feature) {
        if (feature == null) return null;
        return gameData.getProvince((String)feature.getAttributes().get("name"));
    }

    public void setCurrentScreen(StringProperty currentScreen) {
        this.currentScreen.bindBidirectional(currentScreen);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void exitToMenu() {
        currentScreen.set("Gloria Romanus Main Menu");
        mediaPlayer.stop();
    }
}
