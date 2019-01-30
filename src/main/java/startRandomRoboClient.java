import client.RandomRoboClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//NVC3823, 8000 <gerjan
//NVC3827, 5698 <sarah
//localhost, 3001

/**
 * Class for testing purposes, please ignore.
 */
public class startRandomRoboClient {
    public static void main(String[] args) throws IOException {

        InetAddress host = null;

        try {
            host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: no valid hostname!");
            System.exit(0);
        }

        RandomRoboClient client = new RandomRoboClient("Jasper", host, 2001, true);
        client.start();


    }
}
