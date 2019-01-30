package client.gui.go.gui;

import client.Client;
import client.gui.go.gui.utilities.ClickListener;
import client.gui.go.gui.utilities.ClickMoveListener;
import go.utility.Colour;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
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
    private boolean turn;
    private Button passButton;
    private Button hintButton;


    private Stage finishWindow;
    private Stage rematchWindow;

    private ClickListener passButtonListener;
    private ClickListener hintButtonListener;
    private ClickMoveListener placeHolderButtonListener;
    private ClickListener noListener;
    private ClickListener yesListener;

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

        try {
            waitForConfigurationLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.primaryStage = primaryStage;

        primaryStage.setTitle("GO");

        initNewBoard();
        initializationLatch.countDown();
        addPassButton();
        addHintButton();

    }

    public void setPassButtonListener(ClickListener clickListener) {
        this.passButtonListener = clickListener;
    }

    public void setHintButtonListener(ClickListener clickListener) {
        this.hintButtonListener = clickListener;
    }

    public void setPlaceHolderButtonListener(ClickMoveListener clickListener) {
        this.placeHolderButtonListener = clickListener;
    }

    private void initNewBoard() {
        root = new Group();
        board = new Node[currentBoardSize][currentBoardSize];

        Scene scene = new Scene(root, (currentBoardSize + 1) * currentSquareSize,
                (currentBoardSize + 1) * currentSquareSize);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.setFill(new Color(0.625, 0.473, 0.238, 1));
        initBoardLines();
    }

    private void addPassButton() {
        passButton = new Button("Pass");
        passButton.setLayoutX(currentSquareSize);
        passButton.setLayoutY(0);
        passButton.setOnMouseClicked(event -> passButtonListener.onclick());
        root.getChildren().add(passButton);
    }

    private void addHintButton() {
        hintButton = new Button("Hint");
        hintButton.setLayoutX(currentSquareSize * currentBoardSize);
        hintButton.setLayoutY(0);
        hintButton.setOnMouseClicked(event -> hintButtonListener.onclick());
        root.getChildren().add(hintButton);
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

    protected void addStone(int x, int y, Colour colour) throws InvalidCoordinateException {
        checkCoordinates(x, y);
        removeStone(x, y);
        Circle newStone = new Circle(((x + 1) * currentSquareSize), ((y + 1) * currentSquareSize),
                currentSquareSize / 2);

        switch (colour) {
            case WHITE:
                newStone.setFill(Color.WHITE);
                break;
            case BLACK:
                newStone.setFill(Color.BLACK);
                break;
            case GREEN:
                newStone.setFill(Color.GREEN);
                newStone.setOnMouseEntered(event -> newStone.setStroke(Color.BLACK));

                newStone.setOnMouseExited(event -> newStone.setStroke(Color.TRANSPARENT));

                newStone.setOnMouseClicked(event -> placeHolderButtonListener.onclick(y * currentBoardSize + x));

        }

        board[x][y] = newStone;
        root.getChildren().add(newStone);
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
        passButton.setDisable(!turn);
        hintButton.setDisable(!turn);
    }

    public void addPlaceHolderStone(int x, int y) throws InvalidCoordinateException {
        checkCoordinates(x, y);
        removeStone(x, y);

        Circle newStone = new Circle(((x + 1) * currentSquareSize), ((y + 1) * currentSquareSize),
                currentSquareSize / 2);

        newStone.setFill(Color.TRANSPARENT);
        if (turn) {
            newStone.setOnMouseEntered(event -> newStone.setStroke(Color.BLACK));

            newStone.setOnMouseExited(event -> newStone.setStroke(Color.TRANSPARENT));

            newStone.setOnMouseClicked(event -> placeHolderButtonListener.onclick(y * currentBoardSize + x));
        }
        board[x][y] = newStone;
        root.getChildren().add(newStone);
    }

    public void highlightStone(int x, int y) throws InvalidCoordinateException {
        checkCoordinates(x, y);
        Circle currentCircle = (Circle) board[x][y];
        currentCircle.setStroke(Color.GREEN);
        currentCircle.setStrokeWidth(5);
    }


    protected void removeStone(int x, int y) throws InvalidCoordinateException {
        checkCoordinates(x, y);

        if (board[x][y] != null) {
            root.getChildren().remove(board[x][y]);
        }
        board[x][y] = null;
    }

    protected void addAreaIndicator(int x, int y, Colour colour) throws InvalidCoordinateException {
        checkCoordinates(x, y);
        removeStone(x, y);

        Rectangle areaStone = new Rectangle(((x + 1) * currentSquareSize) - currentSquareSize / 6,
                ((y + 1) * currentSquareSize) - currentSquareSize / 6, currentSquareSize / 3,
                currentSquareSize / 3);
        switch (colour) {
            case BLACK:
                areaStone.setFill(Color.BLACK);
                break;
            case WHITE:
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

    protected void clearBoard(Client client) {
        try {
            for (int x = 0; x < currentBoardSize; x++) {
                for (int y = 0; y < currentBoardSize; y++) {
                    removeStone(x, y);
                    addPlaceHolderStone(x, y);
                }
            }
        } catch (InvalidCoordinateException e) {
            throw new IllegalStateException(e);
        }
    }

    protected void setBoardSize(int size) {
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
        new Thread(() -> Application.launch(GoGuiImpl.class)).start();
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

    public void winScreen(String winString) {
        StackPane secondaryLayout = new StackPane();

        secondaryLayout.getChildren().add(new Text(winString));

        Scene secondScene = new Scene(secondaryLayout, 230, 100);
        // New window (Stage)
        finishWindow = new Stage();
        finishWindow.setTitle("Game finished");
        finishWindow.setScene(secondScene);

        // Set position of second window, related to primary window.
        finishWindow.setX(primaryStage.getX() + 200);
        finishWindow.setY(primaryStage.getY() + 200);

        finishWindow.show();
    }

    public void requestRematch() {
        FlowPane rematchPane = new FlowPane();

        Button noButton = new Button("No");
        Button yesButton = new Button("Yes");

        Scene rematchScene = new Scene(rematchPane);
        // New window (Stage)
        rematchWindow = new Stage();

        noButton.setOnMouseClicked(event -> noListener.onclick());
        yesButton.setOnMouseClicked(event -> yesListener.onclick());

        rematchPane.getChildren().add(noButton);

        rematchPane.getChildren().add(yesButton);


        rematchWindow.setTitle("Rematch?");
        rematchWindow.setScene(rematchScene);

        // Set position of second window, related to primary window.
        rematchWindow.setX(primaryStage.getX() + 200);
        rematchWindow.setY(primaryStage.getY() + 100);

        rematchWindow.show();
    }

    public void setRematchButtonListener(ClickListener noListener, ClickListener yesListener) {
        this.noListener = noListener;
        this.yesListener = yesListener;
    }

    public void newMatch() {
        rematchWindow.close();
        finishWindow.close();
    }
}
