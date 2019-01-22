package client.gui.go.gui;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GoGuiImpl extends Application {

	private final static int INITIAL_BOARD_SIZE = 19;
	private final static int INITIAL_SQUARE_SIZE = 50;

	private int currentBoardSize = INITIAL_BOARD_SIZE;
	private int currentSquareSize = INITIAL_SQUARE_SIZE;

	private Node[][] board = null;
	private List<Line> boardLines = new ArrayList<>();
	private Group root = null;
	private Stage primaryStage = null;
	private Node hint = null;

	private final PhongMaterial blackMaterial = new PhongMaterial();
	private final PhongMaterial whiteMaterial = new PhongMaterial();
	private final PhongMaterial yellowMaterial = new PhongMaterial();

	private static final CountDownLatch waitForConfigurationLatch = new CountDownLatch(1);
	private static final CountDownLatch initializationLatch = new CountDownLatch(1);

	private static GoGuiImpl instance;

	protected static boolean isInstanceAvailable() {
		return instance != null;
	}

	public static GoGuiImpl getInstance() {
		return instance;
	}

	protected void countDownConfigurationLatch() {
		waitForConfigurationLatch.countDown();
	}


	@Override
	public void start(Stage primaryStage) {
		instance = this;
		initDrawMaterials();

		try {
			waitForConfigurationLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.primaryStage = primaryStage;

		primaryStage.setTitle("GO");

		initNewBoard();
		initializationLatch.countDown();

	}

	private void initDrawMaterials() {
		blackMaterial.setDiffuseColor(Color.BLACK);
		blackMaterial.setSpecularColor(Color.LIGHTBLUE);
		whiteMaterial.setDiffuseColor(Color.WHITE);
		whiteMaterial.setSpecularColor(Color.LIGHTBLUE);
		yellowMaterial.setDiffuseColor(Color.YELLOW);
		yellowMaterial.setSpecularColor(Color.LIGHTBLUE);
	}


	private void initNewBoard() {
		root = new Group();
		board = new Node[currentBoardSize][currentBoardSize];

		Scene scene = new Scene(root, (currentBoardSize + 1) * currentSquareSize,
				(currentBoardSize + 1) * currentSquareSize);
		primaryStage.setScene(scene);
		primaryStage.show();

		//ImagePattern pattern = new ImagePattern(new Image("background_1920.jpg"));
		scene.setFill(new Color(0.625,0.473,0.238,1));

		initBoardLines();
	}

	private void initBoardLines() {
		root.getChildren().removeAll(boardLines);
		boardLines.clear();

		int squareSize = currentSquareSize;

		// Draw horizontal lines
		for (int i = 1; i <= currentBoardSize; i++) {
			boardLines.add(new Line(squareSize, i * squareSize, currentBoardSize * squareSize, i * squareSize));
		}

		// Draw vertical lines
		for (int i = 1; i <= currentBoardSize; i++) {
			boardLines.add(new Line(i * squareSize, squareSize, i * squareSize, currentBoardSize * squareSize));
		}

		root.getChildren().addAll(boardLines);


		hint = new Circle(currentSquareSize / 2);
		((Circle) hint).setFill(Color.YELLOW);

		hint.setVisible(false);
		root.getChildren().add(hint);
	}

	private void drawDiagonalStoneLine(int diagonal, int colour, boolean flip) {
		try {
			for (int x = 0; x < currentBoardSize; x++) {
				for (int y = 0; y < currentBoardSize; y++) {
					if (x + y == diagonal * 2) {
						if (!flip) {
							addStone(x, y, colour);
						} else {
							addStone(currentBoardSize - 1 - x, y, colour);
						}
					}
				}
			}
		} catch (InvalidCoordinateException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void addStone(int x, int y, int colour) throws InvalidCoordinateException {
		checkCoordinates(x, y);
		removeStone(x, y);


			Circle newStone = new Circle(((x + 1) * currentSquareSize), ((y + 1) * currentSquareSize),
					currentSquareSize / 2);

			switch (colour) {
				case 2:
				newStone.setFill(Color.WHITE);
				break;
				case 1:
				newStone.setFill(Color.BLACK);
				break;
			}

			board[x][y] = newStone;
			root.getChildren().add(newStone);
	}

	protected void removeStone(int x, int y) throws InvalidCoordinateException {
		checkCoordinates(x, y);

		if (board[x][y] != null) {
			root.getChildren().remove(board[x][y]);
		}
		board[x][y] = null;
	}

	protected void addAreaIndicator(int x, int y, int colour) throws InvalidCoordinateException {
		checkCoordinates(x, y);
		removeStone(x, y);


			Rectangle areaStone = new Rectangle(((x + 1) * currentSquareSize) - currentSquareSize / 6,
					((y + 1) * currentSquareSize) - currentSquareSize / 6, currentSquareSize / 3,
					currentSquareSize / 3);
			switch (colour) {
				case 1:
					areaStone.setFill(Color.BLACK);
					break;
				case 2:
					areaStone.setFill(Color.WHITE);
					break;
			}
			board[x][y] = areaStone;
			root.getChildren().add(areaStone);

	}

	protected void addHintIndicator(int x, int y) throws InvalidCoordinateException {
		hint.setTranslateX(((x + 1) * currentSquareSize));
		hint.setTranslateY(((y + 1) * currentSquareSize));
		hint.setVisible(true);
	}

	protected void removeHintIdicator() {
		hint.setVisible(false);
	}

	private void checkCoordinates(int x, int y) throws InvalidCoordinateException {
		if (x < 0 || x >= currentBoardSize) {
			throw new InvalidCoordinateException("x coordinate is outside of board range. x coordinate: " + x
					+ " board range: 0-" + (currentBoardSize - 1));
		}

		if (y < 0 || y >= currentBoardSize) {
			throw new InvalidCoordinateException("y coordinate is outside of board range. y coordinate: " + y
					+ " board range: 0-" + (currentBoardSize - 1));
		}
	}

	protected void clearBoard() {
		try {
			for (int x = 0; x < currentBoardSize; x++) {
				for (int y = 0; y < currentBoardSize; y++) {
					removeStone(x, y);
				}
			}
		} catch (InvalidCoordinateException e) {
			throw new IllegalStateException(e);
		}
	}

	protected void setBoardSize(int size) {
		currentBoardSize = size;
		currentBoardSize = size;

		initNewBoard();
	}

	protected int getBoardSize() {
		return currentBoardSize;
	}

	protected void setInitialBoardSize(int size) {
		currentBoardSize = size;
	}

	protected static void startGUI() {
		new Thread() {

			@Override
			public void run() {
				Application.launch(GoGuiImpl.class);
			}

		}.start();
	}

	protected void waitForInitializationLatch() {
		try {
			System.out.println("Attempting init of the GoGui!");
			if (!initializationLatch.await(30, TimeUnit.SECONDS)) {
				System.out.println("Initialization of the GOGUI failed!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
