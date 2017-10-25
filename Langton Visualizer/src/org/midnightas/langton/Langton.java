package org.midnightas.langton;

import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.StatusBar;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Langton extends Application {

	public float BLOCK_SIZE = 8;

	public BorderPane root = new BorderPane();
	public SplitPane split = new SplitPane();

	// left
	public Canvas canvas = new Canvas(480, 480);
	public HashMap<IntVec2, CellType> map = new HashMap<>();
	public Image antImage = new Image(Langton.class.getResourceAsStream("/ant.png"));
	public IntVec2 ant = new IntVec2(0, 0);
	public IntVec2 antDirection = new IntVec2(0, -1);
	public CellType type0; // to avoid unnecessary calculation
	public boolean paused = true;

	public float camX;
	public float camY;

	// right
	public VBox vbox = new VBox();
	public ListView<CellType> types = new ListView<>();
	public Button addButton = new Button("+"), remButton = new Button("-"); // in one HBox
	public Button startBtn = new Button("Start"), stopBtn = new Button("Stop"), stepBtn = new Button("Step"),
			resetBtn = new Button("Reset"); // in one HBox
	public Slider speedSlider = new Slider(1, 500, 1);

	// bottom
	public StatusBar statusBar = new StatusBar();
	public int stepsTaken = 0;
	public Label stepsLabel = new Label();

	@Override
	public void start(Stage stage) throws Exception {
		split.getItems().addAll(canvas, vbox);
		split.setDividerPositions(0.75f);

		type0 = new CellType(0, 1, Direction.RIGHT);
		types.getItems().add(type0);
		types.getItems().add(new CellType(1, 0, Direction.LEFT));
		types.setCellFactory(CellType.ListCellFactory::new);
		vbox.getChildren().add(types);

		addButton.setOnMouseClicked(e -> {
			CellType cellType = new CellType(findFreeCellTypeId(), 0, Direction.RIGHT);
			types.getItems().add(cellType);
		});
		remButton.setOnMouseClicked(e -> {
			CellType cellType = types.getSelectionModel().getSelectedItem();
			if (cellType.id != 0)
				types.getItems().remove(cellType);
		});
		vbox.getChildren().add(new HBox(addButton, remButton));

		startBtn.setOnMouseClicked(e -> {
			paused = false;
			types.setDisable(true);
		});
		stopBtn.setOnMouseClicked(e -> paused = true);
		stepBtn.setOnMouseClicked(e -> {
			this.step();
		});
		resetBtn.setOnMouseClicked(e -> {
			paused = true;
			stepsTaken = 0;

			types.setDisable(false);
			map.clear();
			ant.x = 0;
			ant.y = 0;
			antDirection.x = 0;
			antDirection.y = -1;
		});
		vbox.getChildren().add(new HBox(startBtn, stopBtn, stepBtn, resetBtn));

		speedSlider.setBlockIncrement(1);
		speedSlider.setShowTickMarks(true);
		speedSlider.setMajorTickUnit(100);
		speedSlider.setTooltip(new Tooltip("60 steps/second"));
		vbox.getChildren().add(speedSlider);

		statusBar.setText("");
		statusBar.getLeftItems().add(stepsLabel);

		Scene scene = new Scene(root);
		scene.setOnKeyTyped(this::keyTyped);
		scene.setOnScroll(this::onScroll);
		root.setCenter(split);
		root.setBottom(statusBar);
		stage.setScene(scene);
		stage.show();

		new AnimationTimer() {
			@Override
			public void handle(long now) {
				if (!paused) {
					for (int i = 0; i < speedSlider.getValue(); i++)
						step();
				}
				draw();
			}
		}.start();
	}

	public void keyTyped(KeyEvent e) {
		if (e.getCharacter().equals("w"))
			camY -= BLOCK_SIZE;
		if (e.getCharacter().equals("a"))
			camX -= BLOCK_SIZE;
		if (e.getCharacter().equals("s"))
			camY += BLOCK_SIZE;
		if (e.getCharacter().equals("d"))
			camX += BLOCK_SIZE;
	}

	public void onScroll(ScrollEvent e) {
		if(e.getDeltaY() > 0) {
			BLOCK_SIZE *= 2;
		} else {
			BLOCK_SIZE /= 2;
		}
	}

	public void draw() {
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		ctx.setFill(Color.WHITE);
		ctx.fillRect(0, 0, 480, 480);

		ctx.save();
		ctx.translate(240 - camX, 240 - camY);

		for (Map.Entry<IntVec2, CellType> entry : map.entrySet()) {
			IntVec2 pos = entry.getKey();
			CellType type = entry.getValue();

			ctx.setFill(CellType.COLORS[type.id]);
			ctx.fillRect(pos.x * BLOCK_SIZE, pos.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
		}

		ctx.drawImage(antImage, ant.x * BLOCK_SIZE, ant.y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

		ctx.restore();
		stepsLabel.setText(Integer.toString(stepsTaken));
	}

	public void step() {
		stepsTaken++;

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

	public int findFreeCellTypeId() {
		outer: for (int i = 0; i < CellType.COLORS.length; i++) {
			for (int j = 0; j < types.getItems().size(); j++) {
				if (types.getItems().get(j).id == i)
					continue outer;
			}
			return i;
		}
		return -1;
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
