import client.Client;
import client.RoboClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//NVC3823, 8000 <gerjan
//NVC3827, 5698 <sarah
//NVC3449, 8000 <Luuk
//localhost, 3001

public class startClient {
    public static void main(String[] args) {

        InetAddress host = null;

        try {
            host = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            System.out.println("ERROR: no valid hostname!");
            System.exit(0);
        }

        Client client = new Client("Jasper", host, 2001, true);
        client.start();
    }
}
