package client;

import client.client.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class main {
    public static void main(String[] args) {

        InetAddress host = null;

        try {
            host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: no valid hostname!");
            System.exit(0);
        }

        try {
            Client client = new Client("Jasper", host, 3001, true);
            client.start();

        } catch (IOException e) {
            System.out.println("ERROR: couldn't construct a client object!");
            System.exit(0);
        }

    }
}
