package Client;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.InfoConnection;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class ClientInformParent implements Runnable {
   private InfoConnection parent;
   private InfoNodo bootstrapper;
    public int thisPort;
    private DatagramSocket socket;

    /**
     * Client needs this info because:
     * @param parent - Target to wantsStream Message
     * @param boot - When client losts Parent Node
     * @param thisPort - To create socket
     */
    public ClientInformParent(InfoNodo parent,InfoNodo boot, int thisPort) {
        this.parent = new InfoConnection(parent, 100, Constants.getCurrentTime(), true);
        this.thisPort = thisPort;
        this.bootstrapper = boot;

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

    }
    @Override
    public void run() {


        System.out.println("otherServer.otherServer.Client ativo");
        byte[] buf = new byte[100];
        DatagramPacket receive = new DatagramPacket(buf, buf.length);
        // De X em X tempo, envia para o parentport um hello com timestamp
        // Falta controlar se recebeu mensagem para atualizar pai.
        while(true) {
            try {
                // Envia para o pai
                // O cliente tem sempre interesse
                checkParent();
                SendData.wantsStream(socket, parent.otherNode);
                System.out.println("[Client] Send still alive msg");
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("[Client] Message not received, timeout socket");
            }
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType){
            case Constants.sitllAlive:
                receiveStillAliveMSG(received.packet);
break;
            case Constants.streamContent:
                receiveStreamContentMSG(received.packet);
                break;

            default:
                System.out.println("\n[Client] What I received? " +Constants.convertMessageType(received.msgType) + "\n");
        }
    }

    /**
     * TODO: Se calhar fazer aqui o mesmo que no nodo, too much delay manda mensagem para o BootStrappper.
     * @param packet
     */
    private void receiveStillAliveMSG(DatagramPacket packet) {
        this.parent = ReceiveData.receiveStillAliveMSG(packet);
    }

    private void checkParent() {
        double timeSinceLastMessage = Constants.getCurrentTime() - parent.timeLastMessage;
        if (timeSinceLastMessage > Constants.timeToConsiderNodeLost){
            try {
                SendData.sendParentLostMSG(socket, bootstrapper, parent.otherNode );
            } catch (IOException e) {
                System.out.println("[Node] Error sending lost parent");
                throw new RuntimeException(e);
            }
            System.out.println("[NODE] Parent lost, sending message to Boot");
        }

    }
    private void receiveStreamContentMSG(DatagramPacket packet) throws IOException {
        byte[] content = ReceiveData.receiveStreamContentMSG(packet);

        System.out.println("\nReceive stream content:");
        System.out.println(new String(content, StandardCharsets.UTF_8));
        System.out.println("");
    }


}
