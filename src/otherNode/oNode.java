package otherNode;

import Client.ClientInformParent;
import Common.Constants;
import Common.InfoNodo;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.InfoConnection;
import otherServer.Bootstrapper.SendNeighbours;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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


        // send hello msg
        DatagramSocket s ;
        int portNode = Integer.parseInt(args[0]);
        int portBoot = Integer.parseInt(args[1]);
        try {
            if (portNode > 0) {
                s = new DatagramSocket(portNode);
                InfoNodo boot = new InfoNodo(InetAddress.getByName("localhost"),portBoot);
                InitializeNode i = new InitializeNode(s,boot);
                i.start();


            }
            else
                s = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Client] Error creating socket");
        } catch (IOException e) {
            e.printStackTrace();
        }




        /*
        boolean stillAliveParte = true;
        if (stillAliveParte) {
            InetAddress parentIP = InetAddress.getByName("localhost");
            int parentPort = Integer.parseInt(args[0]);
            InfoNodo parent = new InfoNodo( parentIP, parentPort);
            System.out.println("[Nodo] Endereço nodo pai: " + args[0]);
            System.out.println("[Nodo] Endereço nodo atual: " + args[1]);

            ArrayList sons = new ArrayList<>();
            // Neste momento a porta do filho está harcoded, depois vai ser dada dinâmicamente.
            for (int i = 2; i < args.length; i++){
                System.out.println("[Nodo] Endereço nodos filhos: " + args[i]);
                InfoNodo son = new InfoNodo(InetAddress.getByName("localhost"), Integer.parseInt(args[i]));
                sons.add(son);
            }

            // Neste momento, não precisamos de saber os filhos
            // Quando for para mandar a árvore dos caminhos, tem de ir preenchendo o array de filhos.
            NodeInformParent comunication_TH = new NodeInformParent(parent, Integer.parseInt(args[1]), sons);
            new Thread(comunication_TH).start();
        }


         */

        }

    }








