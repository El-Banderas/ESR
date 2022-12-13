import Common.Constants;
import Common.InfoNodo;
import otherServer.Servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Executable {
    public static void main(String[] args) {
        if (Constants.Windows) {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?
                try {
                    System.out.println("Boot");

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
                System.out.println("Node ");
                System.out.println("Boot address: " + args[1]);
                return;
            }
            if (args[0].equals("client")) {
                System.out.println("Client");
                System.out.println("Boot address: " + args[1]);

                return;
            }
        }
        else {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?
                    System.out.println("Boot");

                    Servidor.runServer();


                return;
            }
            if (args[0].equals("node")) {
                System.out.println("Node ");
                System.out.println("Boot address: " + args[1]);
                return;
            }
            if (args[0].equals("client")) {
                System.out.println("Client");
                System.out.println("Boot address: " + args[1]);

                return;
            }
        }
        System.out.println("Invalid arguments");

    }
}
