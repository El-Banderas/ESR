package otherServer.Bootstrapper;

import Common.InfoNodo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static Common.Constants.portBootSendNeighbours;

public class SendNeighbours implements Runnable {
    Layout l;

    public SendNeighbours(Layout l) {
        this.l = l;
    }

    public void run() {

        Socket socket = null;
        InputStreamReader input = null;
        OutputStreamWriter output = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        ServerSocket sv = null;




        Map<String, List<String>> rede;
        List<Common.InfoNodo> nodos;


        rede = l.getRede();
        nodos = l.getNodos();


        try {
            sv = new ServerSocket(portBootSendNeighbours);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[SERVERDATA] Error in creating server socket");
        }


        while (true) {

            try {

                socket = sv.accept();

                input = new InputStreamReader(socket.getInputStream());
                output = new OutputStreamWriter(socket.getOutputStream());

                br = new BufferedReader(input);
                bw = new BufferedWriter(output);

                while (true) {
                    String msgfromBoot = br.readLine();
                    String[] msg = msgfromBoot.split("/");

                    System.out.println("Bootsttrap: " + msg[1]);

                    String idNodo = msg[0];
                    List<String> vizinhos = rede.get(idNodo);

                    for (InfoNodo i : nodos) {
                        if (i.getidNodo().equals(idNodo)) {
                            i.setBootStrap();
                        }
                    }

                    bw.write("Os teus vizinhos são: " + vizinhos);
                    bw.newLine();
                    bw.flush();

                    break;
                }

                socket.close();
                input.close();
                output.close();
                bw.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (InfoNodo a : nodos) {


                System.out.println(a.toString());
            }
        }
    }

    }

