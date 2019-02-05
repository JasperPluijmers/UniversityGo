import client.RoboClient;
import client.RoboClient2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//NVC3823, 8000 <gerjan
//NVC3827, 5698 <sarah
//localhost, 3001

/**
 * Class for testing purposes, please ignore.
 */
public class startRoboClient2 {
    public static void main(String[] args) throws IOException {

        String hostname = "localhost";
        int port = 2001;


        InetAddress host = null;

        try {
            host = InetAddress.getByName(hostname  );
        } catch (UnknownHostException e) {
            System.out.println("ERROR: no valid hostname!");
            System.exit(0);
        }

        RoboClient2 client = new RoboClient2("RoboJasper", host, port, true);
        client.start();
    }
}
