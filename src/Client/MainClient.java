package Client;

import Common.Constants;
import Common.InfoNodo;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Depois, estas informações vão ser calculadas e passadas no construtor.
 *
 * O que recebe inicialmente devem ser outras coisas
 */
public class MainClient {
    // Recebe parent port / boot /  porta atual
    public static void main(String[] args) throws UnknownHostException {
        /*
        if(Constants.Windows){
            //InetAddress connectedNode = InetAddress.getByName(args[1]);
            InetAddress parentIP = InetAddress.getByName("localhost");
            InfoNodo parent = new InfoNodo(parentIP,Integer.parseInt(args[0]) );
            InetAddress bootIP = InetAddress.getByName("localhost");
            InfoNodo boot = new InfoNodo(parentIP,Integer.parseInt(args[1]) );

            //                                            parent | boot | porta atual

            ClientInformParent comunication_TH = new ClientInformParent(parent, boot, Integer.parseInt(args[2]));
            new Thread(comunication_TH).start();
        } else   {
            //InetAddress connectedNode = InetAddress.getByName(args[1]);
            InetAddress parentIP = InetAddress.getByName("localhost");
            InfoNodo parent = new InfoNodo(parentIP,8001 );
            InetAddress bootIP = InetAddress.getByName("localhost");
            InfoNodo boot = new InfoNodo(parentIP,Integer.parseInt(args[0]) );

            //                                            parent | boot | porta atual

            ClientInformParent comunication_TH = new ClientInformParent(parent, boot, 8002);
            new Thread(comunication_TH).start();

        }
*/
    }
}
