package client;

import client.utilities.ProtocolHandler;
import client.utilities.ResponseBuilder;
import client.gui.go.gui.GoGuiIntegrator;
import go.model.Board;
import go.utility.Colour;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Thread {

    private String userName;
    private Socket sock;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private int gameId;
    private Colour colour;
    private Board board;
    private boolean turn;
    private GoGuiIntegrator gui;
    private boolean hasGui;
    private ProtocolHandler protocolHandler;

    public Client(String name, InetAddress host, int port, boolean hasGui) {

        this.userName = name;

        try {
            sock = new Socket(host, port);
            this.inStream = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
            this.outStream = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.talk(ResponseBuilder.handshake(name));

        this.hasGui = hasGui;
        this.protocolHandler = new ProtocolHandler(this);
    }


    public void run() {
        String nextLine;
        try {
            while ((nextLine = this.inStream.readLine()) != null) {
                //System.out.println("received: " + nextLine);
                protocolHandler.handleProtocol(nextLine);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void talk(String message) {
        try {
            //System.out.println("sent: " + message);
            this.outStream.write(message + '\n');
            this.outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        }

    }

    public void requestRematch() {
        if (hasGui) {
            gui.requestRematch();
        }
    }

    public void handleHandshake(int gameId) {
        this.gameId = gameId;
        System.out.println("GameId:" + this.gameId);
    }

    public void processConfig(String[] config) {
        this.colour = Colour.getByInt(Integer.parseInt(config[2]));
        this.board = new Board(Integer.parseInt(config[3]));
        this.userName = config[1];
        if (hasGui) {
            gui = new GoGuiIntegrator(Integer.parseInt(config[3]), this);
            gui.startGUI();
        }
    }

    public void gameFinished(String[] message) {
        String[] score = message[3].split(";");
        String winString = message[2] + " won the game with id: " + message[1] + "." + "\nBlack (1) got " + score[0] + " points." + "\nWhite (2) got " + score[1] + " points.";
        if (hasGui) {
            gui.winScreen(winString);
        }
        System.out.println(winString);
    }

    public void makeConfig() {
        int prefColor = readInt("What is your preferred colour? black (1) or white (2)");
        int boardSize = readInt("What boardsize would you like?");
        board = new Board(boardSize);
        talk(ResponseBuilder.setConfig(gameId, prefColor, boardSize));
    }

    public void updateStatus(String status) {
        String[] stati = status.split(";");

        this.board.fromString(stati[2]);

        if (this.colour == Colour.getByInt(Integer.parseInt(stati[1]))) {
            turn = true;
            askMove();

        } else {
            turn = false;
            gui.setTurn(turn);
        }


        if (hasGui) {
            updateGui(stati[2]);
        }
    }

    public void clickMove(int index) {
        if (turn == true) {
            talk(ResponseBuilder.move(this.gameId, this.userName, String.valueOf(index)));
        }
    }

    public void askMove() {
        if (!hasGui) {
            System.out.println(board.toString());

            if (turn) {
                String move = readMove("Which place would you like to play? HELP for options");
                if (move.equals("PASS")) {
                    talk(ResponseBuilder.move(this.gameId, this.userName, "-1"));
                } else {
                    talk(ResponseBuilder.move(this.gameId, this.userName, move));
                }
            }
        } else {
            gui.setTurn(true);
        }
    }

    public void updateGui(String boardString) {
        gui.clearBoard();
        for (int i = 0; i < boardString.length(); i++) {
            switch (boardString.charAt(i)) {
                case '1':
                    gui.addStone(i,Colour.BLACK);
                    break;
                case '2':
                    gui.addStone(i,Colour.WHITE);
                    break;
            }
        }
    }

    public void highlightMove(String move) {
        if (!move.split(";")[0].equals("-1")) {
            gui.highlightStone(Integer.parseInt(move.split(";")[0]));
        }
    }

    public void shutdown() {
        System.out.println("Closing socket connection...");
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    private int readInt(String prompt) {
        int value = 0;
        boolean intRead = false;
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNextInt()) {
                    intRead = true;
                    value = scannerLine.nextInt();
                }
            }
        } while (!intRead);
        return value;
    }

    private String readMove(String prompt) {
        String value = "";
        boolean intRead = false;
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine())) {
                if (scannerLine.hasNextInt()) {
                    return String.format("%d", scannerLine.nextInt());
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

    public void acknowledgeRematchHandler(int value) {
        if (value == 0) {
            talk(ResponseBuilder.exit(gameId,userName));
            shutdown();
        }
        if (value == 1) {
            board = new Board(board.dimension);
            if (hasGui) {
                gui.newMatch();
            }
        }
    }

    public void handleRematch(int value) {
        talk(ResponseBuilder.setRematch(value));
        if (value == 0) {
            shutdown();
        }
    }

    protected Board getBoard() {
        return this.board;
    }

    protected Colour getColour() {
        return this.colour;
    }

    protected int getGameId() {
        return this.gameId;
    }
}
