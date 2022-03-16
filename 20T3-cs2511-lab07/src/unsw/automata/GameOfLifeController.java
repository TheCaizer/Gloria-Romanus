package unsw.automata;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.event.ActionEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GameOfLifeController {

	private GameOfLife game;

	@FXML
	private GridPane grid;

	@FXML
	private Button Play;

	@FXML
	private Button Tick;

	private boolean ticking = false;
	private Timeline timeline;

	@FXML
	void clickPlayButton(ActionEvent event) {
		if(ticking){
			timeline.stop();
			Play.setText("Play");
        } 
        else{
			timeline.play();
			Play.setText("Stop");
		}
		ticking = !ticking;
	}

	@FXML
	void clickTickButton(ActionEvent event) {
		game.tick();
	}

	public GameOfLifeController() {
		timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
			game.tick();
		}));
        timeline.setCycleCount(Animation.INDEFINITE);
	}
    
	@FXML
	void initialize() {
		game = new GameOfLife();
		int size = game.size;
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				CheckBox checkbox = new CheckBox();
				grid.add(checkbox, i, j);

				BooleanProperty position = game.cellProperty(i, j);
				position.addListener((observable, oldValue, newValue) -> {
					checkbox.setSelected(newValue);
				});

				checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
					position.set(newValue);
				});
			}
		}
	}

}