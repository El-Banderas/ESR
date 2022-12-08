package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import otherServer.CommuncationBetweenThreads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.net.UnknownHostException;


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

    public Bootstrapper() {
        //this.typology = typology;
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
        // Creation of server
        try {
            if (this.serverInfo.port > 0){
                System.out.println("Criado na porta " + this.serverInfo.port);
                socket = new DatagramSocket(this.serverInfo.port);
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
        Typology l = new Typology();
        try {
            //l.parse("otherServer/config.txt");
            // É preciso corrigir a parte de baixo :)
            l.parse("src/otherServer/config.txt");
        } catch (IOException e) {
            System.out.println("[SERVERDATA] Error in parte of config file.");
            e.printStackTrace();
        }

        // Fica à espera de enviar informações sobre vizinhos

        // mudar aqui


        byte[] buf = new byte[100];
        DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                checkStreamInterested();
                sendStillAlive();
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

            } catch (IOException e) {
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
            }
        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
            switch (received.msgType){
                // Como stillAlives é pai->filho, boo não tem pais, boot não tem stillAlives
                //case Constants.sitllAliveNoInterest:
                case Constants.streamWanted:
                    receivedStreamWanted(received.packet);
                    break;
                case Constants.hellomesage:
                    System.out.println("Node " + received.packet.getAddress().toString() + " connecting ... \n");
                    receivedHelloMsg(received.packet);
                case Constants.lostNode:
                    receiveLostNodeMSG(received.packet);
                    break;
                default:
                    System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
            }
        }



    private void receivedStreamWanted(DatagramPacket packet) {
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);
        // Necessary to warn stream thread that stream must start/stop.
        if (!this.interested){
            System.out.println("Change interess");
            this.interested = true;
            shared.setSendStream(true);
        }
    }


    private void receivedHelloMsg(DatagramPacket packet) throws IOException {
        // get Vizinhos na TypologyGraph
        // vizinhos imaginarios para teste
        InfoNodo[] vizinhos = new InfoNodo[5];
        InfoNodo v1 = new InfoNodo(InetAddress.getByName("localhost"),2000);
        InfoNodo v2 = new InfoNodo(InetAddress.getByName("localhost"),2001);
        InfoNodo v3 = new InfoNodo(InetAddress.getByName("localhost"),2002);
        InfoNodo v4 = new InfoNodo(InetAddress.getByName("localhost"),2003);
        InfoNodo v5 = new InfoNodo(InetAddress.getByName("localhost"),2004);
        vizinhos[0]=v1;
        vizinhos[1]=v2;
        vizinhos[2]=v3;
        vizinhos[3]=v4;
        vizinhos[4]=v5;
        // converter a lista de vizinhos num pacote
        String v = String.valueOf(v1) + v2 + v3 + v4 + v5 + "END";

        byte[] bytes = ByteBuffer.allocate(18+(2*v.length())).put(v.getBytes()).array();

        SendData.sendData(this.socket,bytes,packet.getAddress(), packet.getPort());
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
        try {
            SendData.sendStillAliveMSG(socket, sonInfo.ip, sonInfo.port);
        }
        catch (Exception e){
            System.out.println("[Boot] Error sending still alive msg");
            e.printStackTrace();
        }
    }


}

