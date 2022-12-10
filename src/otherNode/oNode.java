package otherNode;

import Common.InfoNodo;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 * Currently, there are two developing functions in nodes:
 * <p>
 * Send the neibourghs to someone.
 * Receiving and redirecting the still alive messages. This node can reveice still alive messages from clients and nodes.
 * <p>
 * Node 1
 * Para testar a parte de still alive messages, recebe: parent port / porta atual. (8009 / 8010)
 * Depois, estas informações vão ser calculadas e passadas no construtor.
 * <p>
 * Node 2
 * *  Para testar a parte de still alive messages, recebe: parent port / porta atual. (8008 / 8009)
 * * Depois, estas informações vão ser calculadas e passadas no construtor.
 */

public class oNode {

    public static void main(String[] args) throws UnknownHostException, SocketException {
        System.out.println("[oNode] Started ");

        boolean initNode = false;
        if (initNode) {
            // send hello msg
            DatagramSocket s;
            int portNode = Integer.parseInt(args[0]);
            int portBoot = Integer.parseInt(args[1]);
            try {
                if (portNode > 0) {
                    s = new DatagramSocket(portNode);
                    InfoNodo boot = new InfoNodo(InetAddress.getByName("localhost"), portBoot);
                    InitializeNode i = new InitializeNode(s, boot);
                    i.start();


                } else
                    s = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
                System.out.println("[Client] Error creating socket");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        boolean stillAliveParte = true;
        if (stillAliveParte) {
            InetAddress parentIP = InetAddress.getByName("localhost");
            InetAddress bootIP = InetAddress.getByName("localhost");
            InetAddress nodoIP = InetAddress.getByName("localhost");
            int parentPort = Integer.parseInt(args[0]);
            InfoNodo parent = new InfoNodo(parentIP, parentPort);
            System.out.println("[Nodo] Endereço nodo pai: " + args[0]);
            System.out.println("[Nodo] Endereço boot: " + args[1]);
            int bootPort = Integer.parseInt(args[1]);
            InfoNodo boot = new InfoNodo(bootIP, bootPort);

            System.out.println("[Nodo] Endereço nodo atual: " + args[2]);
            int thisPortNet = Integer.parseInt(args[2]);
            int thisPortStream = Integer.parseInt(args[3]);
            InfoNodo thisNode = new InfoNodo(nodoIP, thisPortNet, thisPortStream);
            ArrayList<InfoNodo> sons = new ArrayList<>();
            for (int i = 4; i < args.length; i+=2) {
                System.out.println("[Nodo] Endereço nodos filhos: " + args[i] + " " + args[i+1]);
                int sonNetPort = Integer.parseInt(args[i]);
                int sonStreamPort = Integer.parseInt(args[i+1]);
                InetAddress sonStreamIP = InetAddress.getByName("localhost");
                InfoNodo son = new InfoNodo(sonStreamIP, sonNetPort, sonStreamPort);
                sons.add(son);
            }

            ShareNodes shared = new ShareNodes();

            // Neste momento, não precisamos de saber os filhos
            // Quando for para mandar a árvore dos caminhos, tem de ir preenchendo o array de filhos.
            //                                                 pai | boot | porta atual | filhos
            NodeInformParent comunication_TH = new NodeInformParent(parent, boot, thisNode, sons, shared);
            new Thread(comunication_TH).start();

            StreamNode stream_TH = new StreamNode(thisNode, shared);
            new Thread(stream_TH).start();

        }

    }

}








