package org.midnightas.langton;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewCellTypeDialog extends Stage {

	public HBox hbox = new HBox();
	public Spinner<Integer> turnTo = new Spinner<>(0, 255, 0);
	public DirectionSpinner direction = new DirectionSpinner();

	public Button setButton = new Button("Set");

	public ListView<CellType> types;

	public NewCellTypeDialog(ListView<CellType> types) {
		this.types = types;

		initStyle(StageStyle.UTILITY);

		turnTo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		turnTo.setEditable(true);

		setButton.setOnMouseClicked(this::onSet);

		hbox.getChildren().add(turnTo);
		hbox.getChildren().add(direction);
		hbox.getChildren().add(setButton);
		// padding
		hbox.setStyle("-fx-padding: 10px 10px 10px 10px;");

		Scene scene = new Scene(hbox);
		setScene(scene);
		show();
	}

	public void onSet(MouseEvent e) {
		// find a free ID, this is a bit of a wtf-y code
		int freeId = 0;
		outer: for (;; freeId++) {
			for (int i = 0; i < types.getItems().size(); i++) {
				if (types.getItems().get(i).id == freeId) {
					continue outer;
				}
			}
			break;
		}

		CellType newType = new CellType(freeId, turnTo.getValue(), direction.getValue());
		types.getItems().add(newType);

		close(); // the dialog has done it's duty
	}

}
