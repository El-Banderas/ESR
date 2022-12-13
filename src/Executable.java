import Common.Constants;
import Common.InfoNodo;
import otherNode.oNode;
import otherServer.Servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Executable {
    public static void main(String[] args) {
        if (Constants.Windows) {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?
                try {
                    System.out.println("Boot Windows");

                    InetAddress ipServer = InetAddress.getByName("127.0.0.1");
                    int portBoot = Integer.parseInt(args[1]);
                    InfoNodo infoServer = new InfoNodo(ipServer, portBoot);
                    Servidor.runServer(infoServer);


                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (args[0].equals("node")) {
                try {
                    System.out.println("Node Windows");

                    InetAddress ipBoot = InetAddress.getByName("127.0.0.1");
                    int portBoot = Integer.parseInt(args[0]);
                    InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);


                    InetAddress ipNodeNet = InetAddress.getByName("127.0.0.1");
                    int portNodeNet = Integer.parseInt(args[1]);
                    InfoNodo infoNodeNet = new InfoNodo(ipNodeNet, portNodeNet);

                    InetAddress ipNodeStream = InetAddress.getByName("127.0.0.1");
                    int portNodeStream = Integer.parseInt(args[2]);
                    InfoNodo infoNodeStream = new InfoNodo(ipNodeStream, portNodeStream);



                    oNode.runNode(infoBoot, infoNodeNet, infoNodeStream);


                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (args[0].equals("client")) {
                System.out.println("Client Windows");
                System.out.println("Boot address: " + args[1]);

                return;
            }
        }
        else {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?
                    System.out.println("Boot Core");

                    Servidor.runServer();


                return;
            }
            if (args[0].equals("node")) {
                System.out.println("Nodo Core");
                try {

                InetAddress ipBoot = InetAddress.getByName(args[0]);
                int portBoot = Constants.portNet;
                InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);


                InetAddress ipNodeNet = InetAddress.getLocalHost();
                int portNodeNet = Constants.portNet;
                InfoNodo infoNodeNet = new InfoNodo(ipNodeNet, portNodeNet);

                InetAddress ipNodeStream = InetAddress.getLocalHost();
                int portNodeStream = Constants.portNet;
                InfoNodo infoNodeStream = new InfoNodo(ipNodeStream, portNodeStream);



                oNode.runNode(infoBoot, infoNodeNet, infoNodeStream);

                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }


                return;
            }
            if (args[0].equals("client")) {
                System.out.println("Client Core");
                System.out.println("Boot address: " + args[1]);

                return;
            }
        }
        System.out.println("Invalid arguments");

    }
}
