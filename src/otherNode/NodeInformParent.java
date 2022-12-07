package otherNode;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.Bootstrapper.InfoConnection;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    // Used to send messages, always the same.
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

        System.out.println("Node on");
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
                break;

            case Constants.lostNode:
                receiveLostNodeMSG(received.packet);
                break;

            case Constants.streamContent:
                receiveStreamContentMSG(received.packet);
                break;


            default:
                System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
                receiveMaybeRTPStream(received.packet);
        }
    }




    /**
     * This function calculates if the new message has too much delay.
     * @param node To find the old delay.
     * @param currentDelay To compare the new
     * @return If the delay has increased too much, so a message is necessary to be sent.
     */
    private boolean tooMuchDelay(InfoNodo node, double currentDelay){
        Optional<InfoConnection> old = sons.stream().filter(son -> (son.otherNode.ip == node.ip && son.otherNode.port == node.port)).findAny();
        if (old.isPresent()){
            double maxDelay = Math.max(old.get().delay, currentDelay);
            double minDelay = Math.min(old.get().delay, currentDelay);
            double percentageDelay = ((maxDelay - minDelay) / maxDelay) * 100;
            if (percentageDelay > Constants.minDelayToTrigger)
                return true;
            else return false;
        }
        return false;
    }

    private void receivedStillAliveMSG(DatagramPacket packet) throws IOException {
//        StillAliveMsgContent time = ReceiveData.receiveStillAliveMSG(packet);
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);

        if (tooMuchDelay(info.otherNode, info.delay)){
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
        System.out.println("[NodeInformParent] Too much  delay");
    }

    /**
     * Update sons array to see wich one's are alive.
     * @return
     */
    public void checkSons(){
        List sonsList = sons.stream().filter(InfoConnection::isAliveTimeout).collect(Collectors.toList());
        // In case there are lost sons.
        if (sonsList.size() < sons.size()){
            List<InfoConnection> sonsCopy = new ArrayList<>(sons);
            sonsCopy.removeAll(sonsList);
            for (InfoConnection lostSon : sonsCopy){
                System.out.println("Filho perdido:");
                System.out.println(lostSon.toString());
                sendLostSonMessage(lostSon.otherNode);
            }

        }
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

    /**
     * Agora manda a mensagem para o nodo pai, mas se for para mandar para o bootstrapper,
     * deve-se mudar o segundo argumento da função do SendData
     * @param lostSon
     */
    private void sendLostSonMessage(InfoNodo lostSon){
        try {
            System.out.println("[NodeInformParent] Send lost son message");
            SendData.sendLostSonMSG(socket, parent, lostSon);
       } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[NodeInformParent] ERROR MESSAGE SENDING LOST SON MSG ");
        }

    }

    /**
     * Send to parent that a node is lost
     * FIXME: Será que devia ir para o bootstrapper?
     * @param packet
     */
    private void receiveLostNodeMSG(DatagramPacket packet) {
        try {
            InfoNodo lostSon = ReceiveData.receiveLostNodeMSG(packet);
            System.out.println("Receive lost node msg");
            System.out.println(lostSon);
            sendLostSonMessage(lostSon);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("[NodeInformParent] ERROR MESSAGE RECEIVING LOST SON MSG ");

        }

    }

    private void receiveStreamContentMSG(DatagramPacket packet) throws IOException {
            byte[] content = ReceiveData.receiveStreamContentMSG(packet);

            System.out.println("Receive stream content, send to sons");
            for (InfoConnection son : sons){
                SendData.sendStreamContentMSG(socket, son.otherNode, content);
            }
    }

    private void receiveMaybeRTPStream(DatagramPacket packet) {
        for (InfoConnection son : sons){
            try {
                System.out.println("Envia para o filho");
                System.out.println(son.otherNode);
                SendData.sendStreamContentMSG(socket, son.otherNode, packet.getData());
            } catch (IOException e) {
                System.out.println("What son not receive: ");
                System.out.println(son.otherNode);
                throw new RuntimeException(e);
            }
        }
    }
}
