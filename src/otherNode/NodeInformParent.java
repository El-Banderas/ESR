package otherNode;


import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import org.xml.sax.SAXException;
import otherServer.Bootstrapper.Connection;
import otherServer.Bootstrapper.InfoConnection;
import otherServer.Bootstrapper.XMLParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static TransmitData.SendData.sendWakeUpClient;

/**
 * This class is responsible for sending "alive" messages to the parent node, from time to time.
 * Ao mesmo tempo, esta classe é responsável por saber se o nodo pai é alterado.
 */
public class NodeInformParent implements Runnable {
    public InfoNodo thisNode;

    // Necessary to reconect after lost parent.
    private InfoNodo bootstrapper;
    public InfoConnection parent;

    // This array is constant, the neibourghs are always the same.
    //public ArrayList<InfoNodo> neibourghs;

    // This array is determined by the formulated tree from the bootstrapper.
    // Based on the state of the connections.
    // We don't need to know the delays
   // public ArrayList<InfoNodo> interestedSons;
    // The sons come from XML file, we believe that they are alive, but we can't confirm that.
    // The only ones we know are alive are the interested sons, because we receive message from them.
    public ArrayList<InfoNodo> sons;

    // Used to send messages, always the same.
    private DatagramSocket socket;

    private ShareNodes shared;

    // Necessary when connection becomes bad
    private InfoNodo altBoot;

    public NodeInformParent(InfoNodo parent, InfoNodo boot, InfoNodo thisNode, DatagramSocket socket, ShareNodes shared, InfoNodo altBoot) {
        // O delay tem de vir do xml, alterar depois
        // Here, the last time the parent answer is now, because this class is created after we receive the xml file.
        this.parent = new InfoConnection(parent, 100, Constants.getCurrentTime(), false);
        this.thisNode = thisNode;
        this.sons = new ArrayList<>();
        //this.neibourghs = new ArrayList<>();
        //this.interestedSons = new ArrayList<>();
        this.bootstrapper = boot;
            this.socket = socket;
        this.shared = shared;
        this.socket = socket;
        this.altBoot = altBoot;
    }


        @Override
    public void run() {
            System.out.println("------------------Node Inform Parent começou--------------");
        try {
            if (this.thisNode.portNet > 0)
              //  socket = new DatagramSocket(this.thisNode.portNet);
                socket = this.socket;
            else
                socket = new DatagramSocket();
                socket.setSoTimeout(Constants.timeoutSockets);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Client] Error creating socket");
        }

        System.out.println("Node on");
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
                System.out.println("Escuto msg : " + socket.getLocalPort());
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);
            } catch (IOException | ParserConfigurationException | SAXException e) {
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
                SendData.sendStillAliveMSG(socket, son.ip, son.portNet);
            }
        } catch (IOException e) {
            System.out.println("[Node] ERROR MESSAGE SENDING LOST SON MSG ");

            throw new RuntimeException(e);
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Recebe do tipo "+received.msgType);
        switch (received.msgType){
            case Constants.sitllAlive:
                receivedStillAliveMSG(received.packet);
                break;

            case Constants.timeStamp:
                // receive packet do nodo c timestamp e calcula delay
                System.out.println("Timestamp");
                ReceiveData.receivedTimeStamp(received.packet,this.thisNode.ip,this.thisNode.portNet,this.socket,this.parent);
    break;
            case Constants.ConnectionMsg:
                Connection n = ReceiveData.receiveConnection(received.packet);
                // falta enviar ao pai
                SendData.sendConnection(this.socket,n,this.parent.otherNode.ip,this.parent.otherNode.portNet);
break;

            case Constants.XMLmsg:
                String xml = ReceiveData.receivedXML(received.packet);
                System.out.println("Recebi XML");
                System.out.println(xml);
                InfoNodo newParent = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                // Aqui não metemos uma conexão, porque a mensagem de XML já é bastante complexa.
                // A conexão é atualizada com o StillAlive, que é uma mensagem mais simples.
                this.parent.otherNode = newParent;
                handleXML(xml);
                // falta enviar ao pai
                //SendData.sendConnection(this.socket,n,this.parent.otherNode.ip,this.parent.otherNode.portNet);
                break;
            case Constants.wakeUpClient:
                System.out.println("Envia wake up");
                receivedWakeUpClient(received.packet);
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

            //case Constants.streamContent:
            //receiveStreamContentMSG(received.packet);
            //break;


            default:
                System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
                System.out.println("Undefined message, rtp packets are handled in other thread/port.");
                //receiveMaybeRTPStream(received.packet);
        }
    }

    private void receivedWakeUpClient(DatagramPacket packet) {
        try {
            SendData.sendWakeUpClient(socket, packet.getData());
        } catch (IOException e) {
            System.out.println("Error sending wake up client");
            throw new RuntimeException(e);
        }
    }

    private void handleXML(String xml ) {

        // clear the ArrayList of sons
        this.sons.clear();

        XMLParser xmlParser = new XMLParser();
        Map<InfoNodo, String> xmlSeparated = null;
        System.out.println("Teste XML");
        System.out.println(xml);
        try {
            xmlSeparated = xmlParser.partitionXML(xml);
        } catch (Exception e) {
            System.out.println("ERROR  Parse XML ");
            throw new RuntimeException(e);
        }
        for (Map.Entry<InfoNodo, String> eachSon : xmlSeparated.entrySet()){
            //Add the son to the ArrayList
            //sons.add(eachSon.getKey());
            // Maybe Problem, children may not exist?
            try {
                if (!eachSon.getValue().equals("")) {
                    //System.out.println("Envia para o filho " + eachSon.getKey());
                    //System.out.println(eachSon.getValue());
                    XMLParser p = new XMLParser();
                    Map<InfoNodo,String> sonsInside = p.partitionXML(eachSon.getValue());
                    /*if(sonsInside.size() > 0){
                        for(Map.Entry<InfoNodo, String> son : sonsInside.entrySet()){
                            sons.add(son.getKey());
                            System.out.println("Envia para o filho " + son.getKey());
                            System.out.println(son.getValue());
                            SendData.sendXML(socket, son.getKey(), son.getValue());
                        }
                    }else{*/
                        String destiny = p.destiny(eachSon.getValue());
                        System.out.println("Envia para o filho " + p.destinyInfoNodo(eachSon.getValue()));
sons.add(p.destinyInfoNodo(eachSon.getValue()));
                        System.out.println(eachSon.getValue());
                        SendData.sendXML(socket, p.destinyInfoNodo(eachSon.getValue()), eachSon.getValue());
                    //}
                }
                else{
                    System.out.println("O que é?");
                    System.out.println(eachSon.getValue());
                }
            } catch (IOException e) {
                System.out.println("Error sending node: "+ eachSon.getKey());
                throw new RuntimeException(e);
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }

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
     * @param packet
     */
    private void receivedWantStreamMSG(DatagramPacket packet) {
//        InfoNodo interestedSon = new InfoNodo(packet.getAddress(), packet.getPort());
        InfoNodo wantStream = ReceiveData.receivedWantStream(packet);
        // Check if the sender of message is already registed.
        System.out.println("Received Want stream from: " + wantStream);
        shared.maybeAddInterestedSon(wantStream);
        try {
            SendData.wantsStream(socket, parent.otherNode, thisNode.portStream);
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
        //System.out.println("[NodeInformParent] Too much  delay");
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
     * NÂO DEVE APARECER
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





}
