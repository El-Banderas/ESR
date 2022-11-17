package otherNode;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class NodeInformParent implements Runnable {
    public int thisPort = 0;

    public InfoNodo parent;

    public ArrayList<InfoNodo> sons;

    public NodeInformParent(InfoNodo parent, int thisPort, ArrayList sons) {
        this.parent = parent;
        this.thisPort = thisPort;
        this.sons = new ArrayList<>(sons);
    }

    @Override
    public void run() {

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
                SendData.sendStillAliveMSG(socket, this.parent.ip, this.parent.port );
                System.out.println("[Client] Send still alive msg");
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);
            } catch (IOException e) {
                System.out.println("[Client] Timeout");
            }

        }


    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType){
            case Constants.sitllAliveID:
                receivedStillAliveMSG(received.packet);
            default:
                System.out.println("[NodeInfomParen] Received message type: " + received.msgType);
        }
    }


    private void receivedStillAliveMSG(DatagramPacket packet) throws IOException {
        float time = ReceiveData.receiveStillAliveMSG(packet);
        System.out.println("Received still alive msg:");
        System.out.println("From: " + packet.getAddress()+ " " + packet.getPort()+ " port.");
        System.out.println("Message time = " + time);

    }
}
