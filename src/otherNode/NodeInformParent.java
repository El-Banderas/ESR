package otherNode;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.InfoConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class NodeInformParent implements Runnable {
    public int thisPort = 0;

    public InfoNodo parent;

    // This array is constant, the sons are always the same.
    public ArrayList<InfoNodo> neibourghs;

    // This array is determined by the formulated tree from the bootstrapper.
    // Based on the state of the connections.
    public ArrayList<InfoConnection> sons;

    private DatagramSocket socket;


    public NodeInformParent(InfoNodo parent, int thisPort, ArrayList neibourghs) {
        this.parent = parent;
        this.thisPort = thisPort;
        this.neibourghs = new ArrayList<>(neibourghs);
        this.sons = new ArrayList<>();
    }

        @Override
    public void run() {

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
                checkSons();

                System.out.println("\n[Node Inform Parent] While cycle:");
                sons.forEach(InfoConnection::toString);
                // Envia para o pai
                int messageType = calculateTypeOfStillAliveMessage();
                SendData.sendStillAliveMSG(socket, this.parent.ip, this.parent.port, messageType);
                System.out.println(" Send still alive msg, type: " + Constants.convertMessageType(messageType));
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);
            } catch (IOException e) {
                System.out.println("[Node] Timeout");
            }

        }


    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType){
            case Constants.sitllAliveNoInterest:
            case Constants.sitllAliveWithInterest:
                receivedStillAliveMSG(received.packet);
            default:
                System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
        }
    }


    private void receivedStillAliveMSG(DatagramPacket packet) throws IOException {
//        StillAliveMsgContent time = ReceiveData.receiveStillAliveMSG(packet);
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);

        if (info.delay > Constants.minDelayToTrigger){
            sendTooMuchDelay(info.otherNode);
        }

        int portOther = info.otherNode.port;
        InetAddress ipOther = info.otherNode.ip;

        System.out.println("\nReceived still alive msg (interested?): " + info.interested);
        System.out.println("From: " + ipOther + " " + portOther+ " port.");

        List otherSons = sons.stream().filter(son -> (son.otherNode.ip != ipOther && son.otherNode.port != portOther)).collect(Collectors.toList());
        System.out.println("Delay = " + info.delay+ "\n");
        // Remove the current node from the sons list.
        sons = new ArrayList<InfoConnection>(otherSons);
        // And add the new information to sons list, with updated info.
        sons.add(info);
    }

    /**
     * TODO: Manda para o bootstrapper?
     * When there is too much delay.
     * @param otherNode
     */
    private void sendTooMuchDelay(InfoNodo otherNode) {
        // O destIP e port devem ser do bootstrapper
        //SendData.sendTooMuchDelayMSG(this.socket, );
    }

    /**
     * Update sons array to see wich one's are alive.
     * @return
     */
    public void checkSons(){
        List sonsList = sons.stream().filter(InfoConnection::isAliveTimeout).collect(Collectors.toList());
        sons = new ArrayList<>(sonsList);
        System.out.println("Existem "+ sons.size() + " filhos");
    }

     /**
     * Decides what type of message is going to be sent to parent node.
     * If there are no interested sons, will send a still alive, no interest, message.
     * @return
     */
    private int calculateTypeOfStillAliveMessage(){
        boolean numberOfInterestedChildren = sons.stream().anyMatch(x -> x.interested);
        if (numberOfInterestedChildren)
            return Constants.sitllAliveWithInterest;
        else return Constants.sitllAliveNoInterest;

    }

}
