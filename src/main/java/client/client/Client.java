package client.client;

import client.gui.go.gui.GoGuiIntegrator;
import go.model.Board;

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
    private int colour;
    private Board board;
    private boolean turn;
    private GoGuiIntegrator gui;
    private boolean hasGui;

    public Client(String name, InetAddress host, int port, boolean hasGui)
            throws IOException {

        this.userName = name;

        sock = new Socket(host, port);
        this.inStream = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        this.outStream = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));

        this.talk(ResponseBuilder.handshake(name));

        this.hasGui = hasGui;

    }


    public void run() {
        String nextLine;
        try {
            while ((nextLine = this.inStream.readLine()) != null) {
                //System.out.println("Recieved:" + nextLine);
                handleProtocol(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
            System.exit(0);
        }
    }

    public void talk(String message) {
        try {
            //System.out.println("send:" + message);
            this.outStream.write(message + '\n');
            this.outStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
            shutdown();
        }

    }

    public void handleProtocol(String message) {
        String[] command = message.split("\\+");
        switch (command[0]) {
            case "ACKNOWLEDGE_HANDSHAKE":
                this.gameId = Integer.parseInt(command[1]);
                System.out.println("GameId:" + command[1]);
                break;
            case "ACKNOWLEDGE_CONFIG":
                processConfig(command);
                updateStatus(command[4]);
                break;
            case "ACKNOWLEDGE_MOVE":
                updateStatus(command[3]);
                break;
            case "UPDATE_STATUS":
                updateStatus(command[1]);
                break;
            case "INVALID_MOVE":
                System.out.println(command[1]);
                askMove();
                break;
            case "REQUEST_CONFIG":
                makeConfig();
                break;
            case "GAME_FINISHED":
                gameFinished(command);
                break;
            default:
                System.out.println("Not in protocol" + message);
        }
    }

    public void processConfig(String[] config) {
        this.colour = Integer.parseInt(config[2]);
        this.board = new Board(Integer.parseInt(config[3]));
        if (hasGui) {
            gui = new GoGuiIntegrator(Integer.parseInt(config[3]), this);
            gui.startGUI();
        }
    }

    public void gameFinished(String[] message) {
        String[] score = message[3].split(":");
        System.out.println(message[2] + " won the game with id: " + message[1] + ".");
        System.out.println("Black (1) got " + score[1] + " points.");
        System.out.println("White (2) got " + score[3] + " points.");
    }

    public void makeConfig() {
        int prefColor = readInt("What is your preferred colour? black (1) or white (2)");
        int boardSize = readInt("What boardsize would you like?");
        board = new Board(boardSize);
        talk(ResponseBuilder.setConfig(gameId, prefColor, boardSize));
    }

    public void updateStatus(String status) {
        String[] stati = status.split(";");

        if (this.colour == Integer.parseInt(stati[1])) {
            turn = true;
            askMove();

        } else {
            turn = false;
            gui.setTurn(turn);
        }

        this.board.fromString(stati[2]);

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
                    gui.addStone(i,1);
                    break;
                case '2':
                    gui.addStone(i,2);
                    break;
            }
        }
    }

    public void shutdown() {
        print("Closing socket connection...");
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    private static void print(String message) {
        System.out.println(message);
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

}
