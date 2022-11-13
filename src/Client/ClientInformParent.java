package Client;


import Common.Constants;
import TransmitData.SendData;

import java.io.*;
import java.net.*;

import static Common.Constants.portBootSendNeighbours;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class ClientInformParent implements Runnable {
    public int parentPort = 0;
    public int thisPort = 0;
    public String parentIP = "localhost";

    public ClientInformParent(int parentPort, int thisPort) {
        this.parentPort = parentPort;
        this.thisPort = thisPort;
    }
    public ClientInformParent(int parentPort) {
        this.parentPort = parentPort;
        this.thisPort = -1;

    }
    @Override
    public void run() {

        String msg = "Hello";
        DatagramSocket socket = null;
        try {
            if (this.thisPort > 0)
                socket = new DatagramSocket(this.thisPort);
            else
                socket = new DatagramSocket();
            socket.setSoTimeout(Constants.timeoutSockets);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Client] Error creating socket");
        }

        System.out.println("otherServer.otherServer.Servidor ativo");
        byte[] buf = new byte[100];
        DatagramPacket receive = new DatagramPacket(buf, buf.length);
        // De X em X tempo, envia para o parentport um hello com timestamp
        // Falta controlar se recebeu mensagem para atualizar pai.
        while(true) {
            try {
                // Envia para o pai
                SendData.sendStillAliveMSG(socket, InetAddress.getByName(this.parentIP), this.parentPort );
                System.out.println("[Client] Send still alive msg");
                Thread.sleep(Constants.timeoutSockets-1);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[Client] Error sending message");
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.out.println("[Client] Error no sleep");

            }

        }


    }
}
