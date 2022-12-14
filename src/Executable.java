import Client.InitClient;
import Common.Constants;
import Common.InfoNodo;
import otherNode.oNode;
import otherServer.SendStream.SendStream;
import otherServer.Servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Executable {
    public static void main(String[] args) throws UnknownHostException {
        if (Constants.Windows) {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?
                try {
                    if (args.length < 3) {
                        System.out.println("Boot Principal Windows ");
                        InetAddress ipServer = InetAddress.getByName("127.0.0.1");
                        int portBoot = Integer.parseInt(args[1]);
                        InfoNodo infoServer = new InfoNodo(ipServer, portBoot);
                        Servidor.runServer(infoServer, true);


                    } else {
                        System.out.println("Boot Alter Windows ");
                        // Info main boot
                        InetAddress ipBoot = InetAddress.getByName("127.0.0.1");
                        int portBoot = Integer.parseInt(args[1]);
                        InfoNodo otherInfo = new InfoNodo(ipBoot, portBoot);

                        // Info this boot (alter)
                        InetAddress ipAltBoot = InetAddress.getByName("127.0.0.1");
                        int altPortBoot = Integer.parseInt(args[2]);
                        InfoNodo thisInfo = new InfoNodo(ipAltBoot, altPortBoot);

                        Servidor.runServer(otherInfo, thisInfo);
                    }

                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (args[0].equals("node")) {
                try {
                    System.out.println("Node Windows");

                    InetAddress ipBoot = InetAddress.getByName("127.0.0.1");
                    int portBoot = Integer.parseInt(args[1]);
                    InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);


                    InetAddress ipNodeNet = InetAddress.getByName("127.0.0.1");
                    int portNodeNet = Integer.parseInt(args[2]);
                    InfoNodo infoNodeNet = new InfoNodo(ipNodeNet, portNodeNet);

                    InetAddress ipNodeStream = InetAddress.getByName("127.0.0.1");
                    int portNodeStream = portNodeNet+1;//Integer.parseInt(args[3]);
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

                System.out.println("Node Windows");

                InetAddress ipBoot = InetAddress.getByName("127.0.0.1");
                int portBoot = Integer.parseInt(args[1]);
                InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);


                InetAddress ipClientNet = InetAddress.getByName("127.0.0.1");
                int portClientNet = Integer.parseInt(args[2]);
                InfoNodo infoClientNet = new InfoNodo(ipClientNet, portClientNet);

                System.out.println("Print informations");
                System.out.println(infoBoot);
                System.out.println(infoClientNet);

                InitClient initClient = new InitClient(infoBoot, infoClientNet);
                initClient.runClient();



                return;
            }
        } else {
            if (args[0].equals("boot")) {
                // Podem haver erros por ser static?

                if (args.length < 3) {

                    InetAddress ipServer = InetAddress.getByName(args[1]);
                    System.out.println("Boot Principle Core");


                    InfoNodo infoServer = new InfoNodo(ipServer, Constants.portNet);
                    Servidor.runServer(infoServer, true);
                }
                else {
                    System.out.println("Boot Alter Core");
                    //InfoNodo infoServer = new InfoNodo(ipServer, Constants.portNet);
                    System.out.println("Info this node");


                    InetAddress ipBoot = InetAddress.getByName(args[1]);
                    InfoNodo infoServer = new InfoNodo(ipBoot, Constants.portNet);

                    InetAddress ipAltBoot = InetAddress.getByName(args[2]);
                    InfoNodo thisInfo = new InfoNodo(ipAltBoot, Constants.portNet);


                    System.out.println(infoServer);
                    Servidor.runServer(infoServer, thisInfo);

                }


                return;
            }
            if (args[0].equals("node")) {
                System.out.println("Nodo Core");
                try {

                    InetAddress ipBoot = InetAddress.getByName(args[1]);
                    int portBoot = Constants.portNet;
                    InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);


                    InetAddress ipNodeNet = InetAddress.getByName(args[2]);
                    int portNodeNet = Constants.portNet;
                    InfoNodo infoNodeNet = new InfoNodo(ipNodeNet, portNodeNet);

                    InetAddress ipNodeStream = InetAddress.getByName(args[2]);
                    int portNodeStream = Constants.portNet+1;
                    InfoNodo infoNodeStream = new InfoNodo(ipNodeStream, portNodeStream);

                    System.out.println("Info this node");
                    System.out.println(infoBoot);
                    System.out.println(infoNodeNet);
                    System.out.println(infoNodeStream);

                    oNode.runNode(infoBoot, infoNodeNet, infoNodeStream);

                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (args[0].equals("client")) {
                System.out.println("Client Core");

                InetAddress ipBoot = InetAddress.getByName(args[1]);//InetAddress.getByName("127.0.0.1");
                int portBoot = Constants.portNet;
                InfoNodo infoBoot = new InfoNodo(ipBoot, portBoot);

                InetAddress ipClientNet = InetAddress.getByName(args[2]);//InetAddress.getByName("127.0.0.1");
                int portClientNet = Constants.portNet;
                InfoNodo infoClientNet = new InfoNodo(ipClientNet, portClientNet);

                System.out.println("Print informations");
                System.out.println(infoBoot);
                System.out.println(infoClientNet);

                InitClient initClient = new InitClient(infoBoot, infoClientNet);
                initClient.runClient();


                return;
            }
        }
        System.out.println("Invalid arguments");

    }
}
