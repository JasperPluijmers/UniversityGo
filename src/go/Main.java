package go;

import go.controller.Game;
import go.utility.Player;
import go.utility.TerminalPlayer;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Player playerOne = new TerminalPlayer(1);
        Player playerTwo = new TerminalPlayer(2);
        ArrayList<Player> players = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
        new Game(7, players).play();
    }
}
