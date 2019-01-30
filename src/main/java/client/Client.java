package client;

import client.gui.go.gui.GoGuiIntegrator;
import client.roboresources.BoardStateValue;
import client.utilities.ProtocolHandler;
import client.utilities.ResponseBuilder;
import go.model.Board;
import go.utility.Colour;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

/**
 * A Client to communicate with a Server playing a game of Go coomplying with the following protocol: https://github.com/JasperPluijmers/GoProtocol
 */
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
    private int lastMove;

    /**
     * Constructs a Client object, tries to create a socket connetion with a specified address and port
     *
     * @param name   Username of the client, used in communication with the server
     * @param host   Hostaddress of the server
     * @param port   Port the server is listening on
     * @param hasGui If the client hsould launch a gui
     */
    public Client(String name, InetAddress host, int port, boolean hasGui) throws IOException {

        this.userName = name;


        sock = new Socket(host, port);
        this.inStream = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        this.outStream = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));

        this.talk(ResponseBuilder.handshake(name));

        this.hasGui = hasGui;
        this.protocolHandler = new ProtocolHandler(this);
    }

    /**
     * Runnable method that checks for incoming messages over the socket and routes them to the a ProtocolHandler
     */
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

    /**
     * Method sends a String over the socket to the server
     *
     * @param message String that has to be sent
     */
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

    /**
     * Sets gameId sent from the server in the handshake
     *
     * @param gameId
     */
    public void handleHandshake(int gameId) {
        this.gameId = gameId;
        System.out.println("GameId:" + this.gameId);
    }

    /**
     * Set colour and username sent from the server, creates a new board and starts the gui.
     *
     * @param config
     */
    public void handleAcknowledgeConfig(String[] config) {
        this.colour = Colour.getByInt(Integer.parseInt(config[2]));
        this.board = new Board(Integer.parseInt(config[3]));
        this.userName = config[1];
        if (hasGui && gui == null) {
            gui = new GoGuiIntegrator(Integer.parseInt(config[3]), this);
            gui.startGUI();
        }
    }

    /**
     * Parses the winmessage from the server and sends it to the gui
     *
     * @param message
     */
    public void gameFinished(String[] message) {
        String[] score = message[3].split(";");
        String winString = message[2] + " won the game with id: " + message[1] + "." + "\nBlack (1) got " + score[0] + " points." + "\nWhite (2) got " + score[1] + " points.";
        if (hasGui) {
            gui.winScreen(winString);
        }
        System.out.println(winString);
    }

    /**
     * Asks for configuration input via the terminal, then sends it to the server
     */
    public void makeConfig() {
        int prefColor = readInt("What is your preferred colour? black (1) or white (2)");
        int boardSize = readInt("What boardsize would you like?");
        board = new Board(boardSize);
        talk(ResponseBuilder.setConfig(gameId, prefColor, boardSize));
    }

    /**
     * Updates current status to the status update from the server, if turn of hte player notifies the gui and updates the board.
     *
     * @param status
     */
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

    /**
     * Method that gets called when clicked on the gui, sends the corresponding move to the server
     *
     * @param index
     */
    public void clickMove(int index) {
        if (turn) {
            talk(ResponseBuilder.move(this.gameId, this.userName, String.valueOf(index)));
        }
    }

    /**
     * called if it is the turn of the player, if gui is active it is notified, otherwise the terminal input is initiated.
     */
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

    /**
     * Updates the current with the latest board representation
     *
     * @param boardString
     */
    public void updateGui(String boardString) {
        gui.clearBoard();
        for (int i = 0; i < boardString.length(); i++) {
            switch (boardString.charAt(i)) {
                case '1':
                    gui.addStone(i, Colour.BLACK);
                    break;
                case '2':
                    gui.addStone(i, Colour.WHITE);
                    break;
            }
        }
    }

    /**
     * Highlights the last move on the gui
     *
     * @param move
     */
    public void highlightMove(String move) {
        lastMove = Integer.parseInt(move.split(";")[0]);
        if (!move.split(";")[0].equals("-1")) {
            gui.highlightStone(Integer.parseInt(move.split(";")[0]));
        }
    }

    /**
     * Disconnects from the server if opponent does not want a rematch or starts a new board and refreshes gui if a
     * rematch is initated.
     *
     * @param value
     */
    public void handleAcknowledgeRematch(int value) {
        if (value == 0) {
            talk(ResponseBuilder.exit(gameId, userName));
            shutdown();
        }
        if (value == 1) {
            board = new Board(board.getDimension());
            if (hasGui) {
                gui.newMatch();
            }
        }
    }

    /**
     * Notifies server of choice of rematch.
     *
     * @param value
     */
    public void handleRematch(int value) {
        talk(ResponseBuilder.setRematch(value));
        if (value == 0) {
            shutdown();
        }
    }

    /**
     * notifies gui so a rematch popup can be shown
     */
    public void handleRequestRematch() {
        if (hasGui) {
            gui.requestRematch();
        }
    }

    /**
     * Closes the socket connection, then terminates the program
     */
    private void shutdown() {
        System.out.println("Closing socket connection...");
        try {
            sock.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    /**
     * Reads an int from user input, keeps trying untill an int is put in
     *
     * @param prompt Message to be shown in the terminal
     * @return
     */
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

    /**
     * Reads a String from user input
     *
     * @param prompt Message to be shown in the terminal
     * @return
     */
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

    protected int getLastMove() {
        return lastMove;
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

    protected String getUserName() {
        return userName;
    }

    public void askHint() {
        BoardStateValue boardStateValue = new BoardStateValue(board, colour);
        int bestMove = boardStateValue.bestMove();
        gui.addStone(bestMove, Colour.GREEN);
    }
}
