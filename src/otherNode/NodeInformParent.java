package otherNode;


import Common.Constants;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class NodeInformParent implements Runnable {
    public int parentPort = 0;
    public int thisPort = 0;
    public String parentIP = "localhost";

    public NodeInformParent(int parentPort, int thisPort) {
        this.parentPort = parentPort;
        this.thisPort = thisPort;
    }
    public NodeInformParent(int parentPort) {
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
        DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);
        // De X em X tempo, envia para o parentport um hello com timestamp
        // Falta controlar se recebeu mensagem para atualizar pai.
        while(true) {
            try {
                // Envia para o pai
                SendData.sendStillAliveMSG(socket, InetAddress.getByName(this.parentIP), this.parentPort );
                System.out.println("[Client] Send still alive msg");
                DatagramPacket lixo = ReceiveData.receiveData(socket);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("[Client] Error sending message");
            }

        }


    }
}
