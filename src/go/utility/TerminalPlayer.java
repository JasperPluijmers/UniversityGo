package go.utility;

import go.model.Board;

import java.util.Scanner;

import static java.util.jar.Pack200.Packer.PASS;

public class TerminalPlayer implements Player {

    private final int playerNumber;

    public TerminalPlayer(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public String playMove(Board board) {
        System.out.println(board);
        return readMove(String.format("What is your move, player %d? (HELP for options)", playerNumber));
    }

    @Override
    public void wrongMove() {
        System.out.println("Move invalid, please try again");
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
