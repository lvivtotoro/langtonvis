package org.midnightas.langton;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class CellType {

	public static final Color[] COLORS = new Color[256];
	static {
		// 8-bit colors, rrrgggbb
		COLORS[0] = new Color(1, 1, 1, 1);
		COLORS[1] = new Color(0, 1, 1, 1);
		COLORS[2] = new Color(0, 0, 1, 1);
		COLORS[3] = new Color(0, 1, 0, 1);
	}

	public int id, becomes;
	public Direction direction;

	public CellType(int id, int becomes, Direction direction) {
		this.id = id % COLORS.length;
		this.becomes = becomes % COLORS.length;
		this.direction = direction;
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	public static class ListCellFactory extends ListCell<CellType> {

		private CellType cellType;

		private HBox graphic = new HBox();
		private Label idLabel = new Label();
		private ComboBox<CellType> turnTo = new ComboBox<CellType>();
		private DirectionSpinner direction = new DirectionSpinner();

		private ListView<CellType> listView;

		public ListCellFactory(ListView<CellType> listView) {
			this.listView = listView;

			idLabel.setTextFill(Color.WHITE);
			idLabel.setBlendMode(BlendMode.DIFFERENCE);
			turnTo.setItems(listView.getItems());
			turnTo.setCellFactory(CellType.ComboBoxFactory::new);

			turnTo.getSelectionModel().selectedItemProperty().addListener((ob, ol, ne) -> {
				cellType.becomes = ne.id;
			});
			direction.getSelectionModel().selectedItemProperty().addListener((ob, ol, ne) -> {
				cellType.direction = ne;
			});
		}

		public void updateItem(CellType type, boolean empty) {
			super.updateItem(type, empty);

			if (!empty) {
				cellType = type;

				turnTo.getSelectionModel().select(Langton.getCellTypeFromId(listView.getItems(), type.becomes));
				idLabel.setText(Integer.toString(type.id));
				direction.getSelectionModel().select(type.direction);

				graphic.getChildren().clear();
				graphic.getChildren().add(idLabel);
				graphic.getChildren().add(turnTo);
				graphic.getChildren().add(direction);
				setGraphic(graphic);

				// set cell bg
				Color color = COLORS[type.id];
				setStyle("-fx-background-color: " + String.format("rgb(%s,%s,%s)", (int) (color.getRed() * 255),
						(int) (color.getGreen() * 255), (int) (color.getBlue() * 255)));
			} else {
				setText(null);
				setGraphic(null);
				setStyle("");
			}
		}

	}

	public static class ComboBoxFactory extends ListCell<CellType> {

		private HBox graphic = new HBox();

		public ComboBoxFactory(ListView<CellType> comboBox) {

		}

		public void updateItem(CellType type, boolean empty) {
			super.updateItem(type, empty);
			if (!empty) {
				graphic.getChildren().clear();

				Rectangle rekt = new Rectangle(32, 32);
				rekt.setFill(COLORS[type.id]);

				graphic.getChildren().add(new Label(Integer.toString(type.id)));
				graphic.getChildren().add(rekt);
				setGraphic(graphic);
			} else {
				setText(null);
				setGraphic(null);
				setStyle("");
			}
		}

	}

}
