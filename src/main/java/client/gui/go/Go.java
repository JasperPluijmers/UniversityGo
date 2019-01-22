package client.gui.go;


import client.gui.go.gui.GoGuiIntegrator;

/**
 * Example on how to use the GoGui
 *
 */
public class Go {

	private GoGuiIntegrator gogui;

	public Go(int boardSize) {
		gogui = new GoGuiIntegrator(boardSize);
		gogui.startGUI();
	}

	public void testBoard() {
		gogui.addStone(0,1);
		gogui.addStone(9,2);
		gogui.addStone(80,1);
	}

	public static void main(String[] args) {
		new Go(9).testBoard();
	}
}
