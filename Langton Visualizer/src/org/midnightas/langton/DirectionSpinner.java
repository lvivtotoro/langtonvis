package org.midnightas.langton;

import static org.midnightas.langton.Direction.*;

import javafx.scene.control.ComboBox;

public class DirectionSpinner extends ComboBox<Direction> {

	public DirectionSpinner() {
		getItems().addAll(FORWARD, RIGHT, BACK, LEFT);
		getSelectionModel().selectFirst();
	}

}
