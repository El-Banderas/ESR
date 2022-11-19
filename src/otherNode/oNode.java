package otherNode;

import Client.ClientInformParent;
import Common.Constants;
import Common.InfoNodo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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

    public static void main(String[] args) throws UnknownHostException {
        System.out.println("[oNode] Started ");

        boolean stillAliveParte = true;
        if (stillAliveParte) {
            InetAddress parentIP = InetAddress.getByName("localhost");
            int parentPort = Integer.parseInt(args[0]);
            InfoNodo parent = new InfoNodo( parentIP, parentPort);
            // Neste momento a porta do filho está harcoded, depois vai ser dada dinâmicamente.
            InfoNodo son = new InfoNodo(InetAddress.getByName("localhost"), 8020);
            ArrayList sons = new ArrayList<>(Collections.singleton(son));
            // Neste momento, não precisamos de saber os filhos
            // Quando for para mandar a árvore dos caminhos, tem de ir preenchendo o array de filhos.
            NodeInformParent comunication_TH = new NodeInformParent(parent, Integer.parseInt(args[1]), sons);
            new Thread(comunication_TH).start();
        }

        boolean sendNeighbours = false;
        if (sendNeighbours) {
            Socket socket = null;
            InputStreamReader input = null;
            OutputStreamWriter output = null;
            BufferedReader br = null;
            BufferedWriter bw = null;

            try {

                socket = new Socket("localhost", Constants.portBootSendNeighbours);

                input = new InputStreamReader(socket.getInputStream());
                output = new OutputStreamWriter(socket.getOutputStream());

                br = new BufferedReader(input);
                bw = new BufferedWriter(output);

                Scanner scanner = new Scanner(System.in);

                while (true) {
                    String msgToSend;
                    if (args.length > 0) msgToSend = args[0] + "/" + scanner.nextLine();
                    else msgToSend = "n1" + "/" + scanner.nextLine();


                    bw.write(msgToSend);
                    bw.newLine();
                    bw.flush();

                    System.out.println("Server: " + br.readLine());
                    break;


                }


            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }


    }

}




