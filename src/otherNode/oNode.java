package otherNode;

import Client.ClientInformParent;
import Common.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Currently, there are two developing functions in nodes:
 *
 * Send the neibourghs to someone.
 * Receiving and redirecting the still alive messages. This node can reveice still alive messages from clients and nodes.
 *
 * Para testar a parte de still alive messages, recebe: parent port / porta atual. (8009 / 8010)
 * Depois, estas informações vão ser calculadas e passadas no construtor.
 */

public class oNode {

    public static void main(String[] args) {
      System.out.println("[oNode] Started ");

        boolean stillAliveParte = true;
        if (stillAliveParte) {
            NodeInformParent comunication_TH = new NodeInformParent(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
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




