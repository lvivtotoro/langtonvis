package org.midnightas.langton;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Langton extends Application {

	public static final int BLOCK_SIZE = 8;

	public SplitPane pane = new SplitPane();

	// left
	public LangtonTimer canvasTimer = new LangtonTimer(this::step);
	public Canvas canvas = new Canvas(480, 480);
	public HashMap<IntVec2, CellType> map = new HashMap<>();
	public Image antImage = new Image(Langton.class.getResourceAsStream("/ant.png"));
	public IntVec2 ant = new IntVec2(0, 0);
	public IntVec2 antDirection = new IntVec2(0, -1);
	public CellType type0; // to avoid unnecessary calculation

	// right
	public VBox vbox = new VBox();
	public ListView<CellType> types = new ListView<>();
	public Button addButton = new Button("+"), remButton = new Button("-"); // in one HBox
	public Button startBtn = new Button("Start"), stopBtn = new Button("Stop"), stepBtn = new Button("Step"),
			resetBtn = new Button("Reset"); // in one HBox
	public Slider speedSlider = new Slider(20, 1000, 30);

	@Override
	public void start(Stage stage) throws Exception {
		pane.getItems().addAll(canvas, vbox);
		pane.setDividerPositions(0.75f);

		type0 = new CellType(0, 1, Direction.RIGHT);
		types.getItems().add(type0);
		types.getItems().add(new CellType(1, 0, Direction.LEFT));
		types.setCellFactory(CellType.ListCellFactory::new);
		vbox.getChildren().add(types);

		addButton.setOnMouseClicked(e -> new NewCellTypeDialog(types));
		remButton.setOnMouseClicked(e -> {
			CellType cellType = types.getSelectionModel().getSelectedItem();
			if (cellType.id != 0)
				types.getItems().remove(cellType);
		});
		vbox.getChildren().add(new HBox(addButton, remButton));

		startBtn.setOnMouseClicked(e -> {
			canvasTimer.start();
			types.setDisable(true);
		});
		stopBtn.setOnMouseClicked(e -> canvasTimer.stop());
		stepBtn.setOnMouseClicked(e -> this.step());
		resetBtn.setOnMouseClicked(e -> {
			canvasTimer.stop();
			types.setDisable(false);
			map.clear();
			ant.x = 0;
			ant.y = 0;
		});
		vbox.getChildren().add(new HBox(startBtn, stopBtn, stepBtn, resetBtn));
		
		speedSlider.setBlockIncrement(1);
		speedSlider.setShowTickMarks(true);
		speedSlider.setMajorTickUnit(100);
		speedSlider.setTooltip(new Tooltip("Speed slider"));
		speedSlider.valueProperty().addListener((ob, ol, ne) -> {
			canvasTimer.setPeriod(ne.intValue());
		});
		vbox.getChildren().add(speedSlider);

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		stage.show();

		canvasTimer.setPeriod(30);
		canvasTimer.stop();
	}

	public void step() {
		// DRAW THE GAME
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		ctx.setFill(Color.WHITE);
		ctx.fillRect(0, 0, 480, 480);

		ctx.save();
		ctx.translate(240, 240);

		for (Map.Entry<IntVec2, CellType> entry : map.entrySet()) {
			IntVec2 pos = entry.getKey();
			CellType type = entry.getValue();

			ctx.setFill(CellType.COLORS[type.id]);
			ctx.fillRect(pos.x * BLOCK_SIZE, pos.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
		}

		ctx.drawImage(antImage, ant.x * BLOCK_SIZE, ant.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

		ctx.restore();

		// UPDATE THE GAME
		CellType typeBelowAnt = map.get(ant);
		if (typeBelowAnt == null)
			typeBelowAnt = this.type0;

		map.put(ant.copy(), getCellTypeFromId(types.getItems(), typeBelowAnt.becomes));

		switch (typeBelowAnt.direction) {
		case RIGHT: {
			IntVec2 newDir = new IntVec2(0, 0);
			newDir.x = -antDirection.y;
			newDir.y = antDirection.x;
			antDirection = newDir;
			break;
		}
		case BACK: {
			antDirection.x *= -1;
			antDirection.y *= -1;
			break;
		}
		case LEFT: {
			IntVec2 newDir = new IntVec2(0, 0);
			newDir.x = antDirection.y;
			newDir.y = -antDirection.x;
			antDirection = newDir;
			break;
		}
		default:
			break;
		}

		ant.x += antDirection.x;
		ant.y += antDirection.y;
	}

	public static void main(String[] args) {
		launch(args);
		System.exit(0); // close the LangtonTimer
	}

	public static CellType getCellTypeFromId(ObservableList<CellType> list, int id) {
		if (id == 0)
			return list.get(0);

		for (int i = 0; i < list.size(); i++) {
			CellType type = list.get(i);
			if (type.id == id)
				return type;
		}
		return null;
	}

}
