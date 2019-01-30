import client.Client;
import client.RoboClient;
import server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    private static InetAddress address;

    public static void main(String[] args) {
        int input = readInt("Do you want to start a client (0) or a server (1)?");
        switch (input) {
            case 0:
                startClient();
                break;
            case 1:
                startServer();
                break;
            default:
                System.out.println("Input unknown");
                main(new String[0]);
        }
    }

    private static void startServer() {
        int port = readInt("Please provide the port you want to listen on:");
        try {
            Server server = new Server(port);
            server.start();
        } catch (IOException e) {
            System.out.println("Serversocket could not be made, please try again ");
            startServer();
        }

    }

    private static void startClient() {
        try {
            address = InetAddress.getByName(readInput("Please provide host"));
        } catch (UnknownHostException e) {
            System.out.println("ERROR: host " + address + " unknown, please try again");
            startClient();
        }

        int port = readInt("Please provide the port you want to connect to");

        String userName = readInput("What is your username?");

        int computerPlayer = readInt("Do you want to use an AI?(yes (1) or no (0)");

        switch (computerPlayer) {
            case 0:
                try {
                    Client client = new Client(userName, address, port, true);
                    client.start();
                } catch (IOException e) {
                    System.out.println("Could not connect to server, please try again");
                    startClient();
                } catch (IllegalArgumentException e) {
                    System.out.println("Illegal argument, check if the port you provided is a valid port");
                    startClient();
                }
                break;
            case 1:
                try {
                    Client client = new RoboClient(userName, address, port, true);
                    client.start();
                } catch (IOException e) {
                    System.out.println("Could not connect to server, please try again");
                    startClient();
                } catch (IllegalArgumentException e) {
                    System.out.println("Illegal argument, check if the port you provided is a valid port");
                    startClient();
                }
                break;
            default:
                System.out.println("Input unknown, please try again");
                startClient();
        }


    }

    private static String readInput(String prompt) {
        String value = "";
        Scanner line = new Scanner(System.in);
        System.out.print(prompt);
        try (Scanner scannerLine = new Scanner(line.nextLine())) {
            if (scannerLine.hasNextLine()) {
                value = scannerLine.nextLine();
            }
        }
        return value;
    }

    private static int readInt(String prompt) {
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
}
