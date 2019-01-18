package go.utility;

import go.controller.Game;
import go.model.Board;

import java.util.Map;
import java.util.Scanner;

import static java.util.jar.Pack200.Packer.PASS;

public class TerminalPlayer implements Player {

    private final int playerNumber;
    private Game game;
    private int colour;

    public TerminalPlayer(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public void playMove(Board board) {
        System.out.println(board);
        game.playMove(readMove(String.format("What is your move, player %d? (HELP for options)", playerNumber)), colour);
    }

    @Override
    public void wrongMove() {
        System.out.println("Move invalid, please try again");
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public void requestMove(Board board) {
        playMove(board);
    }

    @Override
    public void setColour(int colour) {
        this.colour = colour;
    }

    @Override
    public void updateState() {

    }

    @Override
    public void finishGame(String winner, Map<Integer, Integer> score, String reason) {

    }

    @Override
    public String getUsername() {
        return null;
    }

    private String readMove(String prompt) {
        String value = "";
        boolean intRead = false;
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNextInt()) {
                    return String.format("PLAY %d", scannerLine.nextInt());
                }
                if (scannerLine.hasNextLine()) {
                    switch (scannerLine.nextLine()) {
                        case ("PASS"):
                            return "PASS";
                        case ("HELP"):
                            System.out.println("To play, give the index you want to play on, to pass, type PASS");
                    }
                }
            }
        } while (!intRead);
        return value;
    }
}
