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

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class NodeInformParent implements Runnable {
    public int thisPort = 0;

    // Necessary to reconect after lost parent.
private InfoNodo bootstrapper;
    public InfoConnection parent;

    // This array is constant, the neibourghs are always the same.
    public ArrayList<InfoNodo> neibourghs;

    // This array is determined by the formulated tree from the bootstrapper.
    // Based on the state of the connections.
    // We don't need to know the delays
    public ArrayList<InfoNodo> interestedSons;
    // The sons come from XML file, we believe that they are alive, but we can't confirm that.
    // The only ones we know are alive are the interested sons, because we receive message from them.
    public ArrayList<InfoNodo> sons;

    // Used to send messages, always the same.
    private DatagramSocket socket;

    /**
     * Depois tem de poder receber a mensagem do XML
     * Node needs this info because:
     * @param parent - To send wantsStream, but will get this node with stillAlives. Ao receber o XML, pode fazer getAdress do pai
     * @param boot - When the current node lost connection with parent node.
     * @param thisPort - To create socket (temporária).
     * @param sons - This info comes from XML file.
     */
    public NodeInformParent(InfoNodo parent, InfoNodo boot, int thisPort, ArrayList<InfoNodo> sons) {
        // O delay tem de vir do xml, alterar depois
        this.parent = new InfoConnection(parent, 100, Constants.getCurrentTime(), false);
        this.thisPort = thisPort;
        this.sons = sons;
        this.neibourghs = new ArrayList<>();
        this.interestedSons = new ArrayList<>();
        this.bootstrapper = boot;
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
        // De X em X tempo, envia para o parentport um hello com timestamp
        // Falta controlar se recebeu mensagem para atualizar pai.
        while(true) {
            try {
//                checkSons();
                checkParent();

                //System.out.println("\n[Node Inform Parent] While cycle:");
                //sons.forEach(InfoConnection::toString);
                // Envia para o pai
                //int messageType = calculateTypeOfStillAliveMessage();
                sendStillAliveMSG();
                //SendData.sendStillAliveMSG(socket, this.parent.ip, this.parent.port, messageType);
              //  System.out.println(" Send still alive msg, type: " + Constants.convertMessageType(messageType));
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);
            } catch (IOException e) {
                System.out.println("[Node] Timeout");
            }

        }


    }

    /**
     * Here, the node checks if the parent is considered up or not.
     * Se o pai for considerado perdido, continuamos? Sim, temos de mandar stillAlives senão consideram-nos desconectados
     * Ou esperamos?
     */
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

    /**
     * Send a still alive message to every son.
     * Maybe we should send a wantStream here, so the server knows we are still interested.
     * Porque se o nodo for invadido por conteúdo de streams,
     */
    private void sendStillAliveMSG() {
        try {

            for (InfoNodo son : sons) {
                SendData.sendStillAliveMSG(socket, son.ip, son.port);
            }
        } catch (IOException e) {
            System.out.println("[Node] ERROR MESSAGE SENDING LOST SON MSG ");

            throw new RuntimeException(e);
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
        switch (received.msgType){
            case Constants.sitllAlive:
                receivedStillAliveMSG(received.packet);
                break;

            case Constants.streamWanted:
                receivedWantStreamMSG(received.packet);
                break;
            // Será que um Nodo recebe a mensagem de LostNode?
            case Constants.lostNode:
                System.out.println("\n\n\nAlguma vez isto vai aparecer?\n\n\n");
                System.out.println("Apareceu isto");
                System.out.println(Constants.lostNode);
                System.out.println("Still alive code");
                System.out.println(Constants.streamContent);
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
     * Whe this message is received, these happens:
     * Add the son to the interested sons, if he is already there, stays;
     * Send message to parent saying we want stream;
     *
     * Problem: If we got 3 interested sons, we send 3 different messages, instead of one, and that's bad.
     * We should join them, with a ?timer? that wakes up, see if there are interested, and send message.
     * In this way, we accumulate messages, but that's harder to implement.
     *
     * Também há uma cena:
     * Nós mandamos stillAlives quando recebemos uma mensagem, e por exemplo, quando recebe stream, está sempre a receber coisas.
     * Logo, manda **imensas** mensagens. Nós sabemos como resolver, é meter uma thread separada, que de X em X tempo acorda e manda mensagem,
     * Mas começa a complicar, e para o trabalho o pc pode não aguentear, porque cada nodo teria 2 thread, apesar de uma não fazer muita coisa.
     * @param packet
     */
    private void receivedWantStreamMSG(DatagramPacket packet) {
        InfoNodo interestedSon = new InfoNodo(packet.getAddress(), packet.getPort());
        boolean alreadyInterested = interestedSons.stream().anyMatch(oneSon -> InfoNodo.compareInfoNodes(oneSon, interestedSon));
        if (!alreadyInterested) interestedSons.add(interestedSon);
        try {
            SendData.wantsStream(socket, parent.otherNode);
        } catch (IOException e) {
            System.out.println("[Node] Error sending want Stream Message");
            throw new RuntimeException(e);
        }

    }



    /**
     * This function calculates if the new message has too much delay.
     * @param oldDelay Delay of last message from parent.
     * @param currentDelay To compare the new
     * @return If the delay has increased too much, so a message is necessary to be sent.
     */
    private boolean tooMuchDelay(double oldDelay, double currentDelay){
            double maxDelay = Math.max(oldDelay, currentDelay);
            double minDelay = Math.min(oldDelay, currentDelay);
            double percentageDelay = ((maxDelay - minDelay) / maxDelay) * 100;
            return percentageDelay > Constants.minDelayToTrigger;
        }

    private void receivedStillAliveMSG(DatagramPacket packet) throws IOException {
//        StillAliveMsgContent time = ReceiveData.receiveStillAliveMSG(packet);
        InfoConnection parentNow = ReceiveData.receiveStillAliveMSG(packet);

        if (tooMuchDelay(parent.delay, parentNow.delay)){
            sendTooMuchDelay(parentNow.otherNode);
        }
        System.out.println("[Node] Receive still alive from parent");
        parent = parentNow;
/*
        int portOther = parentNow.otherNode.port;
        InetAddress ipOther = parentNow.otherNode.ip;

        System.out.println("\nReceived still alive msg (interested?): " + parentNow.interested);
        System.out.println("From: " + ipOther + " " + portOther+ " port.");

        List otherSons = sons.stream().filter(son -> (son.otherNode.ip != ipOther && son.otherNode.port != portOther)).collect(Collectors.toList());
        System.out.println("Delay = " + parentNow.delay+ "\n");
        // Remove the current node from the sons list.
        sons = new ArrayList<InfoConnection>(otherSons);
        // And add the new information to sons list, with updated info.
        sons.add(parentNow);


 */
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
/*
     * Update sons array to see wich one's are alive.
     * @return
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

     * Decides what type of message is going to be sent to parent node.
     * If there are no interested sons, will send a still alive, no interest, message.
     * @return
    private int calculateTypeOfStillAliveMessage(){
        boolean numberOfInterestedChildren = sons.stream().anyMatch(x -> x.interested);
        if (numberOfInterestedChildren)
            return Constants.streamWanted;
        else return Constants.sitllAliveNoInterest;
    }

     * Agora manda a mensagem para o nodo pai, mas se for para mandar para o bootstrapper,
     * deve-se mudar o segundo argumento da função do SendData
     * @param lostSon

    private void sendLostSonMessage(InfoNodo lostSon){
        try {
            System.out.println("[NodeInformParent] Send lost son message");
            SendData.sendParentSonMSG(socket, parent, lostSon);
       } catch (IOException e) {
            e.printStackTrace();
            System.out.println("[NodeInformParent] ERROR MESSAGE SENDING LOST SON MSG ");
        }

    }
*/
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
            System.out.println("Veio da porta: ");
            System.out.println(packet.getPort());
            //sendLostSonMessage(lostSon);
            System.out.println("TIrar de comentário o que está em cima");
            System.out.println("Mas isto NÃO DEVIA APARECER");
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("[NodeInformParent] ERROR MESSAGE RECEIVING LOST SON MSG ");

        }

    }

    /**
     *
     * @param packet
     * @throws IOException
     */
    private void receiveStreamContentMSG(DatagramPacket packet) throws IOException {
            byte[] content = ReceiveData.receiveStreamContentMSG(packet);

            System.out.println("Receive stream content, send to sons");
            for (InfoNodo son : interestedSons){
                SendData.sendStreamContentMSG(socket, son, content);
            }
    }


    private void receiveMaybeRTPStream(DatagramPacket packet) {
        for (InfoNodo son : interestedSons){
            try {
                System.out.println("Envia para o filho");
                System.out.println(son);
                SendData.sendStreamContentMSG(socket, son, packet.getData());
            } catch (IOException e) {
                System.out.println("What son not receive: ");
                System.out.println(son);
                throw new RuntimeException(e);
            }
        }
    }
}
