import client.Client;
import client.RoboClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//NVC3823, 8000 <gerjan
//NVC3827, 5698 <sarah
//localhost, 3001

public class startRoboClient {
    public static void main(String[] args) {

        InetAddress host = null;

        try {
            host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: no valid hostname!");
            System.exit(0);
        }

        RoboClient client = new RoboClient("Jasper", host, 3001, true);
        client.start();


    }
}
