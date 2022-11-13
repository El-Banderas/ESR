package Client;

import java.net.UnknownHostException;

/**
 * Atualmente recebe parent port / porta atual. (8010 / 8020)
 * Depois, estas informações vão ser calculadas e passadas no construtor.
 *
 * O que recebe inicialmente devem ser outras coisas
 */
public class MainClient {
    // Recebe parent port / porta atual
    public static void main(String[] args) throws UnknownHostException {
        //InetAddress connectedNode = InetAddress.getByName(args[1]);
        ClientInformParent comunication_TH = new ClientInformParent(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        new Thread(comunication_TH).start();

    }
}
