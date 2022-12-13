package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import org.xml.sax.SAXException;
import otherServer.CommuncationBetweenThreads;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.*;


/**
 * Main class of bootstrapper functions.
 * It start by reading the config file. Then, starts receiveng request of nodes.
 *
 * The best_paths_tree stores the best ways to get to each node. TODO: Falta fazer
 *
 *  Neste momento, a TypologyGraph e o Layout são coisas separadas, mas deviam ser a mesma.
 *  Porque ambas guardam tudo o que a topologia tem.
 *
 *  Acho que também é necessário guardar o filho atual do boot/servidor.
 *  Isso tem de ser comunicado entre boot e servidor
 *      (talvez?: https://stackoverflow.com/questions/13582395/sharing-a-variable-between-multiple-different-threads)
 */

public class Bootstrapper implements Runnable{
    // All the topology
    //private TypologyGraph topology;
    private Typology topologyTypology;

    // Tree of connections
    private Tree best_paths_tree;

    private InfoNodo serverInfo;
    private InfoNodo sonInfo;
    private CommuncationBetweenThreads shared;

    private DatagramSocket socket;

    boolean interested;
    double lastTimeSomeoneInterested;

    public Bootstrapper(Typology t) {
        this.topologyTypology = t;
    }

    public Bootstrapper(CommuncationBetweenThreads shared) {
        this.shared = shared;
        try {
            InfoNodo serverInfo = new InfoNodo(InetAddress.getLocalHost(), Constants.portNet);
            System.out.println("IP do server");
            System.out.println(serverInfo.getIp());
            this.serverInfo = serverInfo;
            initBootGeneral(Constants.portNet);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    // Talvez não seja necessária a informação do próprio server
    // Agora é necessário para podermos testar. Mas depois, não precisaoms de dizer qual é esta porta.

    /**
     * Boot needs this info because:
     * @param serverInfo - Só precisa da porta agora para criar o socket.
     * @param sonInfo - TO send StillAlives, será calculado pela parte do Miguel qual é o filho
     * @param shared - Common classe with stream thread.
     */
    public Bootstrapper(InfoNodo serverInfo, InfoNodo sonInfo, CommuncationBetweenThreads shared) {
        this.serverInfo = serverInfo;
        this.sonInfo = sonInfo;
        this.shared = shared;
        this.interested = false;
        this.lastTimeSomeoneInterested = 0;
        this.topologyTypology = new Typology();
        // Creation of server
        try {
            if (this.serverInfo.portNet > 0){
                socket = new DatagramSocket(this.serverInfo.portNet);
                System.out.println("Criado na porta " + this.serverInfo.portNet);

            }
            else {
                socket = new DatagramSocket();
            }
            socket.setSoTimeout(Constants.timeoutSockets);

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Server] Error creating socket");
        }
    }
    // Quando testamos no windows, precisamos de dar a porta
    public Bootstrapper(InfoNodo serverInfo, CommuncationBetweenThreads shared) {
        this.serverInfo = serverInfo;
        this.shared = shared;
        initBootGeneral(serverInfo.portNet);

    }

    public void initBootGeneral(int portBoot){
        this.sonInfo = null;
        this.interested = false;
        this.lastTimeSomeoneInterested = 0;
        this.topologyTypology = new Typology();
        // Creation of server
        try {
                socket = new DatagramSocket(portBoot);
                System.out.println("Criado na porta " + serverInfo.portNet);
            socket.setSoTimeout(Constants.timeoutSockets);

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Server] Error creating socket");
        }
    }

    /**
     * Banderas: In this method, we sent the tree to the network, in one message.
     * It takes the tree, converts to an array of bytes, and sent to the first Node.
     *
     * Nota: Meter um TIMESTAMP no início da mensagem, para que os filhos possam calcular o delay da transmissão nessa mensagem
     * E criar um infoConnection.
     * Igual nos nodos que reencaminham o xml, também tem meter o TIMESTAMP
     */
    public void sendTree(){

    }

    @Override
    public void run() {
        System.out.println("[Server] Bootstrapper on");
        // Topology
        try {
            //l.parse("otherServer/config.txt");
            // É preciso corrigir a parte de baixo :)
            this.topologyTypology.parse("src/otherServer/Config/test.txt");
            this.topologyTypology.setCompleteNetwork();
        } catch (IOException | InterruptedException e) {
            System.out.println("[SERVERDATA] Error in parte of config file.");
            e.printStackTrace();
        }


        byte[] buf = new byte[100];
        DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                checkStreamInterested();
                sendStillAlive();
                MessageAndType received = ReceiveData.receiveData(socket);
                System.out.println("Escuta no " + socket.getLocalPort());
                handleReceivedMessage(received);

            } catch (IOException | InterruptedException | ParserConfigurationException | SAXException e) {
                System.out.println("[Boot] Timeout, listening in: " + socket.getLocalPort());

            }

        }
    }
/* 
    private void handleReceivedMessage(MessageAndType received) throws IOException {
            switch (received.msgType){
                case Constants.hellomesage:
                    System.out.println("Node " + received.packet.getAddress().toString() + " connecting ... \n");
                    receivedHelloMsg(received.packet);
                case Constants.sitllAliveNoInterest:
                case Constants.sitllAliveWithInterest:
                    receivedStillAliveMSG(received.packet);
                    */
    /**
     * Check if someone is interested recently.
     */
    private void checkStreamInterested() {
        if (interested) {
            // Se passou muito tempo, deve considerar que ninguém quer.
            double differenceLastTimeStreamInterested = Constants.getCurrentTime() - lastTimeSomeoneInterested;
            if (differenceLastTimeStreamInterested > Constants.timeToConsiderNodeLost){
                interested = false;
                shared.setSendStream(false);
                System.out.println("Check stream interested");
            }
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
            switch (received.msgType){
                // Como stillAlives é pai->filho, boo não tem pais, boot não tem stillAlives
                //case Constants.sitllAliveNoInterest:
                case Constants.streamWanted:
                    receivedStreamWanted(received.packet);
                    break;
                case Constants.ConnectionMsg:
                  Connection c = ReceiveData.receiveConnection(received.packet);
                  // atualiza arvore com esta connection

                    this.topologyTypology.addConection(c.from,c.to,c.delay,c.numHops, socket, sonInfo);

                    break;
                    // When we receive a timestamp, it's from the connected node from boot.
                case Constants.timeStamp:
                    InfoNodo receivedSon = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                    this.sonInfo = receivedSon;
                    Connection co = ReceiveData.BootreceivedTimeStamp(received.packet, serverInfo.ip, serverInfo.portNet);

                    this.topologyTypology.addConection(co.from,co.to,co.delay,co.numHops, socket, sonInfo );
break;
                case Constants.hellomesage:
                    System.out.println("Node " + received.packet.getAddress().toString() + " connecting ... \n");
                    InfoNodo nodo = new InfoNodo(received.packet.getAddress(),received.packet.getPort());
                    this.topologyTypology.activateConnection(nodo,false);
                    ReceiveData.receivedHelloMsg(received.packet, this.socket, this.topologyTypology);
                    break;
                case Constants.lostNode:
                    receiveLostNodeMSG(received.packet);
                    break;
                default:
                    System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
            }
        }



    private void receivedStreamWanted(DatagramPacket packet) {
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);
        lastTimeSomeoneInterested = info.timeLastMessage;

        // Necessary to warn stream thread that stream must start/stop.
        if (!this.interested){
            System.out.println("Change interess");
            this.interested = true;
            shared.setSendStream(true);
        }
    }


    /**
     * When a node in the network is lost, the parent of that node sends a message notifying the other nodes.
     * Será que quando um "pai" descobre que o filho morre deve mandar mensagem para o boot, em vez de seguir a àrvore?
     * Miguel
     * @param packet
     */
    private void receiveLostNodeMSG(DatagramPacket packet) {
        try {
           InfoNodo lostSon = ReceiveData.receiveLostNodeMSG(packet);

        System.out.println("Receive lost node msg");
        System.out.println(lostSon);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("[Bootstrapper] ERROR MESSAGE RECEIVING LOST SON MSG ");

        }
    }


    /**
     * This method sends still alives messages to son.
     */
    private void sendStillAlive() {
        if (sonInfo != null) {
            try {

                SendData.sendStillAliveMSG(socket, sonInfo.ip, sonInfo.portNet);
            } catch (Exception e) {
                System.out.println("[Boot] Error sending still alive msg");
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Boot - No son connected");
        }
    }


}

