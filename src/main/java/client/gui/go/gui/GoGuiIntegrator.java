package client.gui.go.gui;

import client.client.Client;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class GoGuiIntegrator implements GoGui {

	private GoGuiImpl wrappee;
	private Client client;

	/**
	 * Creates a GoGUIIntegrator that is capable of configuring and controlling the
	 * GO GUI.
	 *
	 * @param boardSize            the desired initial board size.
	 */
	public GoGuiIntegrator(int boardSize, Client client) {
		createWrappedObject();
		wrappee.setInitialBoardSize(boardSize);
		this.client = client;

		wrappee.setPassButtonListener(() -> client.clickMove(-1));
	}

	@Override
	public synchronized void setBoardSize(int size) {
		Platform.runLater(() -> wrappee.setBoardSize(size));
	}

	public synchronized int getBoardSize() {
		return wrappee.getBoardSize();
	}

	@Override
	public synchronized void addStone(int x, int y, int colour) {
		Platform.runLater(() -> {
			try {
				wrappee.addStone(x, y, colour);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	public synchronized void addPlaceholderStone(int x, int y) {
		Platform.runLater(() -> {
			try {
				wrappee.addPlaceHolderStone(x, y, client);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	public synchronized void addPlaceholderStone(int index) {
		int x = index % getBoardSize();
		int y = index / getBoardSize();
		addPlaceholderStone(x, y);
	}

	public synchronized void addStone(int index, int colour) {
		int x = index % getBoardSize();
		int y = index / getBoardSize();
		addStone(x, y, colour );
	}

	public synchronized void setTurn(boolean turn) {

			Platform.runLater(() -> {
				wrappee.setTurn(turn);
			});
	}

	@Override
	public synchronized void removeStone(int x, int y) {
		Platform.runLater(() -> {
			try {
				wrappee.removeStone(x, y);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void addAreaIndicator(int x, int y, int colour) {
		Platform.runLater(() -> {
			try {
				wrappee.addAreaIndicator(x, y, colour);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void addHintIndicator(int x, int y) {
		Platform.runLater(() -> {
			try {
				wrappee.addHintIndicator(x, y);
			} catch (InvalidCoordinateException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public synchronized void removeHintIdicator() {
		Platform.runLater(() -> wrappee.removeHintIdicator());
	}

	@Override
	public synchronized void clearBoard() {
		Platform.runLater(() -> wrappee.clearBoard(client));
	}

	@Override
	public synchronized void startGUI() {
		startJavaFX();
		wrappee.waitForInitializationLatch();
		System.out.println("GO GUI was successfully started!");
	}

	@Override
	public synchronized void stopGUI() {
		// Not implemented yet
	}

	private void createWrappedObject() {
		if (wrappee == null) {
			GoGuiImpl.startGUI();

			while (!GoGuiImpl.isInstanceAvailable()) {
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			wrappee = GoGuiImpl.getInstance();
		}
	}

	private void startJavaFX() {
		createWrappedObject();
		wrappee.countDownConfigurationLatch();
	}
}
