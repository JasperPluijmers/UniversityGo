package Client;

import go.model.Board;

import javax.xml.ws.Response;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;


public class Client extends Thread {
    public static void main(String[] args) {

        InetAddress host = null;

        try {
            host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            print("ERROR: no valid hostname!");
            System.exit(0);
        }

        try {
            Client client = new Client("Jasper", host, 3001);
            client.start();

            do {
            } while (true);

        } catch (IOException e) {
            print("ERROR: couldn't construct a client object!");
            System.exit(0);
        }

    }

    private String userName;
    private Socket sock;
    private BufferedReader inStream;
    private BufferedWriter outStream;
    private int gameId;
    private int colour;
    private Board board;
    private boolean turn;

    public Client(String name, InetAddress host, int port)
            throws IOException {

        this.userName = name;

        sock = new Socket(host, port);
        this.inStream = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        this.outStream = new BufferedWriter(new OutputStreamWriter(this.sock.getOutputStream()));

        this.talk(ResponseBuilder.handshake(name));
    }


    public void run() {
        String nextLine;
        try {
            while ((nextLine = this.inStream.readLine()) != null) {
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
            this.outStream.write(message+'\n');
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
                this.colour = Integer.parseInt(command[2]);
                updateStatus(command[4]);
                break;
            case "UPDATE_STATUS":
                updateStatus(command[1]);
                break;
            case "INVALID_MOVE":
                System.out.println(command[1]);
                break;
            case "REQUEST_CONFIG":
                makeConfig();
                break;
            case "GAME_FINISHED":
                System.out.println(message);
                break;
            default:
                System.out.println("Not in protocol" + message);
        }
    }

    public void makeConfig() {
        int prefColor = readInt("What is your preferred colour? black (1) or white (2)");
        int boardSize = readInt("What boardsize would you like?");
        board = new Board(boardSize);
        talk(ResponseBuilder.setConfig(gameId,prefColor,boardSize));
    }

    public void updateStatus(String status) {
        String[] stati = status.split(";");
        if (board == null) {
            this.board = new Board((int)Math.sqrt(stati[2].length()));
        }

        if (this.colour == Integer.parseInt(stati[1])) {
            turn = true;
        } else {
            turn = false;
        }
        this.board.fromString(stati[2]);
        System.out.println(board.toString());

        if (turn == true && stati[0].equals("PLAYING")) {
            String move = readMove("Which place would you like to play?");
            if (move.equals("PASS")) {
                talk(ResponseBuilder.move(this.gameId,this.userName, "-1"));
            } else {
                talk(ResponseBuilder.move(this.gameId,this.userName, move));
            }
        }

        if (turn == false && stati[0].equals("PLAYING")) {

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

    private static void print(String message){
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
