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
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.List;


/**
 * Main class of bootstrapper functions.
 * It start by reading the config file. Then, starts receiveng request of nodes.
 * <p>
 * The best_paths_tree stores the best ways to get to each node. TODO: Falta fazer
 * <p>
 * Neste momento, a TypologyGraph e o Layout são coisas separadas, mas deviam ser a mesma.
 * Porque ambas guardam tudo o que a topologia tem.
 * <p>
 * Acho que também é necessário guardar o filho atual do boot/servidor.
 * Isso tem de ser comunicado entre boot e servidor
 * (talvez?: https://stackoverflow.com/questions/13582395/sharing-a-variable-between-multiple-different-threads)
 */

public class Bootstrapper implements Runnable {
    // All the topology
    //private TypologyGraph topology;
    private Typology topologyTypology;

    // Tree of connections
    private Tree best_paths_tree;

    private InfoNodo thisBoot;
    private InfoNodo sonInfo;
    private CommuncationBetweenThreads shared;

    private DatagramSocket socket;

    boolean interested;
    private double lastTimeSomeoneInterested;
    private boolean isPrinciple;
    /**
     * In case this is alternative server, we have the altBoot
     */
    private InfoNodo otherBoot;


    public Bootstrapper(Typology t) {
        this.topologyTypology = t;
    }

    public Bootstrapper(CommuncationBetweenThreads shared, boolean isPrinciple) {
        this.shared = shared;
        try {
            InfoNodo serverInfo = new InfoNodo(InetAddress.getLocalHost(), Constants.portNet);
            System.out.println("IP do server");
            System.out.println(serverInfo.getIp());
            this.thisBoot = serverInfo;
            initBootGeneral(Constants.portNet, isPrinciple);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    // Talvez não seja necessária a informação do próprio server
    // Agora é necessário para podermos testar. Mas depois, não precisaoms de dizer qual é esta porta.

    /**
     * Boot needs this info because:
     *
     * @param thisBoot - Só precisa da porta agora para criar o socket.
     * @param sonInfo    - TO send StillAlives, será calculado pela parte do Miguel qual é o filho
     * @param shared     - Common classe with stream thread.
     */
    public Bootstrapper(InfoNodo thisBoot, InfoNodo sonInfo, CommuncationBetweenThreads shared, boolean isPrinciple) {
        this.thisBoot = thisBoot;
        this.sonInfo = sonInfo;
        this.shared = shared;
        this.interested = false;
        this.lastTimeSomeoneInterested = 0;
        this.topologyTypology = new Typology();
        this.isPrinciple = isPrinciple;
        // Creation of server
        try {
            if (this.thisBoot.portNet > 0) {
                socket = new DatagramSocket(this.thisBoot.portNet);
                System.out.println("Criado na porta " + this.thisBoot.portNet);

            } else {
                socket = new DatagramSocket();
            }
            socket.setSoTimeout(Constants.timeoutSockets);

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Server] Error creating socket");
        }
    }

    // Quando testamos no windows, precisamos de dar a porta
    public Bootstrapper(InfoNodo thisBoot, CommuncationBetweenThreads shared, boolean isPrinciple) {
        this.thisBoot = thisBoot;
        this.shared = shared;
        // Nós não conhecemos o alt boot
        this.otherBoot = null;
        initBootGeneral(thisBoot.portNet, isPrinciple);
    }

    // Construtor alternative boot
    public Bootstrapper(InfoNodo principleBoot, InfoNodo altBoot, CommuncationBetweenThreads shared) {
        this.thisBoot = altBoot;
        this.shared = shared;
        this.otherBoot = principleBoot;
        // When we are alternative server, we use the altBoot to the info of the socket
        initBootGeneral(altBoot.portNet, false);
    }


    public void initBootGeneral(int portBoot, boolean isPrinciple) {
        this.sonInfo = null;
        this.isPrinciple = isPrinciple;
        this.interested = false;
        this.lastTimeSomeoneInterested = 0;
        this.topologyTypology = new Typology();
        // Creation of server
        try {
            socket = new DatagramSocket(portBoot);
            System.out.println("Criado na porta " + thisBoot.portNet);
            socket.setSoTimeout(Constants.timeoutSockets);

        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Server] Error creating socket");
        }
    }

    /**
     * Banderas: In this method, we sent the tree to the network, in one message.
     * It takes the tree, converts to an array of bytes, and sent to the first Node.
     * <p>
     * Nota: Meter um TIMESTAMP no início da mensagem, para que os filhos possam calcular o delay da transmissão nessa mensagem
     * E criar um infoConnection.
     * Igual nos nodos que reencaminham o xml, também tem meter o TIMESTAMP
     */
    public void sendTree() {

    }

    @Override
    public void run() {
        System.out.println("[Server] Bootstrapper on");
        // Topology
        try {
            //l.parse("otherServer/config.txt");
            // É preciso corrigir a parte de baixo :)
            if(Constants.Windows){
                this.topologyTypology.parse("src/otherServer/Config/testWindows.txt");
            }
            else {
                topologyTypology.parse("/home/core/Desktop/ESR/src/otherServer/Config/topCenario2.txt");
            }
            this.topologyTypology.setCompleteNetwork();
        } catch (IOException | InterruptedException e) {
            System.out.println("[SERVERDATA] Error in parte of config file.");
            e.printStackTrace();
        }


        byte[] buf = new byte[100];
        DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);
        if (isPrinciple) {
            bodyMainBoot();
        } else {
            startAlterServer();
        }
    }

    private void bodyMainBoot() {
        while (true) {
            try {
                checkStreamInterested();
                sendStillAlive();
                MessageAndType received = ReceiveData.receiveData(socket);

                System.out.println("Escuta no " + socket.getLocalPort());
                handleReceivedMessagePrinciple(received);

            } catch (IOException | InterruptedException | ParserConfigurationException | SAXException e) {
                System.out.println("[Boot] Timeout, listening in: " + socket.getLocalPort());

            }
        }
    }

    private void startAlterServer() {
        try {

        SendData.sendHelloFromAlt(socket, this.otherBoot);

        MessageAndType received = ReceiveData.receiveData(socket);
        while (received.msgType != Constants.StillAliveBootAlt){
            System.out.println("Receu mensagem estranha no server alternativo: " + received.msgType);
            received = ReceiveData.receiveData(socket);
        }
        while (true){
            try {
                received = ReceiveData.receiveData(socket);
                handleReceivedMessageAlternative(received);
            }
            catch (IOException e) {
              //  throw new RuntimeException(e);
                System.out.println("Não recebe still ALive do boot");
            }

        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleReceivedMessageAlternative(MessageAndType received) {
        System.out.println("Alter receive: " + received.msgType);
        switch (received.msgType) {
            // Como stillAlives é pai->filho, boo não tem pais, boot não tem stillAlives
            //case Constants.sitllAliveNoInterest:
            case Constants.StillAliveBootAlt:
                receivedStillAlivePrimeBoot(received.packet);
                break;
            case Constants.changeTree:
                receivedActiveTree(received.packet);
                break;

            default:
                System.out.println("\n[NodeInfomParen] Received message type: " + Constants.convertMessageType(received.msgType) + "\n");

        }
    }

    private void receivedActiveTree(DatagramPacket packet) {
        topologyTypology.activeNetwork = ReceiveData.getActiveNodes(packet);
    }

    private void receivedStillAlivePrimeBoot(DatagramPacket packet) {
        shared.timestampStream = ReceiveData.receiveStillAliveFromPrimeBoot(packet);
        System.out.println("Recebeu Timestamp " + shared.timestampStream);
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
            if (differenceLastTimeStreamInterested > Constants.timeToConsiderNodeLost) {
                interested = false;
                shared.setSendStream(false);
                System.out.println("Check stream interested");
            }
        }
    }

    private void handleReceivedMessagePrinciple(MessageAndType received) throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        switch (received.msgType) {
            // Como stillAlives é pai->filho, boo não tem pais, boot não tem stillAlives
            //case Constants.sitllAliveNoInterest:
            case Constants.streamWanted:
                receivedStreamWanted(received.packet);
                break;
            case Constants.ConnectionMsg:
                Connection c = ReceiveData.receiveConnection(received.packet);
                // atualiza arvore com esta connection
                addConnection(c.from, c.to, c.delay, c.numHops, socket, sonInfo);
                break;
            // When we receive a timestamp, it's from the connected node from boot.
            case Constants.timeStamp:
                InfoNodo receivedSon = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                InfoNodo receivedSonStream = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                System.out.println("Novo filho: " + receivedSonStream);
                this.sonInfo = receivedSon;
                this.shared.son = receivedSonStream;
                Connection co = ReceiveData.BootreceivedTimeStamp(received.packet, thisBoot.ip, thisBoot.portNet);
                addConnection(co.from, co.to, co.delay, co.numHops, socket, sonInfo);
                break;
            case Constants.hellomesage:
                System.out.println("Node " + received.packet.getAddress().toString() + " connecting ... \n");
                InfoNodo nodo = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                this.topologyTypology.activateConnection(nodo);
                ReceiveData.receivedHelloMsg(received.packet, this.socket, this.topologyTypology, this.otherBoot);
                break;
            case Constants.helloClient:
                handleHelloClient(received.packet.getAddress(), received.packet.getPort());
                break;

            case Constants.lostNode:
                receiveLostNodeMSG(received.packet);
                break;
            case Constants.helloAltBoot:
                handleHelloFromAlt(received.packet);
                System.out.println("Falta ativar o boot na tipologia");
                break;
            default:
                System.out.println("\n[NodeInfomParen] Received message type: " + received.msgType + "\n");
        }
    }

    private void handleHelloClient(InetAddress address, int port) {
        System.out.println("Recebeu hello do client " + port);
        try {

            InfoNodo newClient = new InfoNodo(address, port);
            this.topologyTypology.activateConnection(newClient);
            InfoNodo possibleParent = this.topologyTypology.getFather(newClient);
            if (possibleParent == null) {
                System.out.println("Caminho impossível, pai não está ativo");
                this.topologyTypology.removeNode(newClient);
                SendData.sendImpossibleConnection(socket, newClient);

                return;
            }
            this.topologyTypology.addConection(newClient, possibleParent, 0, 0, null, null);
            List<InfoNodo> parents = topologyTypology.getPath(newClient);

            if (parents.size() > 0){
                //this.topologyTypology.addConection(newClient, possibleParent, 0, 0, null, null);

                System.out.println("Sucesso: " + newClient);
                //List<InfoNodo> withoutServer = parents.subList(1, parents.size());
                System.out.println("Lista de caminhos: " + parents);
                parents.add(newClient);
                SendData.sendWakeUpClient(socket, parents);

            }
            else {
                this.topologyTypology.removeNode(newClient);
                System.out.println("Caminho impossível");
                SendData.sendImpossibleConnection(socket, newClient);

            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private void addConnection(InfoNodo from, InfoNodo to, double delay, int numHops, DatagramSocket socket, InfoNodo sonInfo) {
        this.topologyTypology.addConection(from, to, delay, numHops, socket, sonInfo);
        // Se o boot alter estiver ativo, devemos atualizá-lo
        if (this.otherBoot != null){
            System.out.println("Enviar ao alt nova árvore");
            SendData.sendActiveNetwork(socket, otherBoot,topologyTypology.activeNetwork);
        }
    }

    private void handleHelloFromAlt(DatagramPacket packet) {
        InfoNodo alterBoot = new InfoNodo(packet.getAddress(), packet.getPort());
        this.otherBoot = alterBoot;
    }


    private void receivedStreamWanted(DatagramPacket packet) {
        ReceiveData.receivedWantStream(packet);
        lastTimeSomeoneInterested = Constants.getCurrentTime();

        // Necessary to warn stream thread that stream must start/stop.
        if (!this.interested) {
            System.out.println("Change interess");
            this.interested = true;
            shared.setSendStream(true);
        }
    }


    /**
     * When a node in the network is lost, the parent of that node sends a message notifying the other nodes.
     * Será que quando um "pai" descobre que o filho morre deve mandar mensagem para o boot, em vez de seguir a àrvore?
     * Miguel
     *
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
        } else {
            System.out.println("Boot - No son connected");
        }
        if (this.otherBoot != null){
            try {
                SendData.sendStillAliveBootAlt(socket, this.otherBoot, shared.timestampStream);
                System.out.println("Send still alive to alter boot, timestamp: " + shared.timestampStream);
            } catch (IOException e) {
                System.out.println("Error sendind still alive to alt server");
                throw new RuntimeException(e);
            }
        }
    }


}

