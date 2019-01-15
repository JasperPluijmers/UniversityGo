package Server;

import java.util.Scanner;

public class Game {

    private GameState state;
    private Board board;

    public Game() {
        this.board = new Board(4);
        this.state  = new GameState(board);
    }

    public void play() {
        while (true) {
            System.out.println(board.toString());
            this.board.playMove(move(), state.currentPlayer + 1);
            state.updateCurrent();
            state.updateBoard();
        }
    }

    private int move() {
        return readInt(String.format("Next move, player %d?",state.currentPlayer + 1));
    }

    private int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine());) {
                if (scannerLine.hasNextInt()) {
                    intRead = true;
                    value = scannerLine.nextInt();
                }
            }
        } while (!intRead);
        return value;
    }


}
