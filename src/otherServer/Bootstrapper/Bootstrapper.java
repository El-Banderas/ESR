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
    private TypologyGraph topology;
    private Layout topologyLayout;

    // Tree of connections
    private Tree best_paths_tree;

    private InfoNodo serverInfo;
    private InfoNodo sonInfo;
    private CommuncationBetweenThreads shared;

    private DatagramSocket socket;

    boolean interested;

    public Bootstrapper() {
        this.topology = topology;
    }

    // Talvez não seja necessária a informação do próprio server
    // Agora é necessário para podermos testar. Mas depois, não precisaoms de dizer qual é esta porta.
    public Bootstrapper(InfoNodo serverInfo, InfoNodo sonInfo, CommuncationBetweenThreads shared) {
        this.serverInfo = serverInfo;
        this.sonInfo = sonInfo;
        this.shared = shared;
        this.interested = false;
        // Creation of server
        try {
            if (this.serverInfo.port > 0)
                socket = new DatagramSocket(this.serverInfo.port);
            else {
                socket = new DatagramSocket();
                socket.setSoTimeout(Constants.timeoutSockets);
            }
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Server] Error creating socket");
        }
    }

    /**
     * Banderas: In this method, we sent the tree to the network, in one message.
     * It takes the tree, converts to an array of bytes, and sent to the first Node.
     */
    public void sendTree(){

    }

    @Override
    public void run() {
        System.out.println("[Server] Bootstrapper on");
        // Topology
        Layout l = new Layout();
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
                // Isto ta a chegar vazio talvez
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

            } catch (IOException e) {
                System.out.println("[Node] Timeout, listening in: " + socket.getPort());

            }

        }
    }

    private void handleReceivedMessage(MessageAndType received) throws IOException {
            switch (received.msgType){
                case Constants.hellomesage:
                    System.out.println("Node " + received.packet.getAddress().toString() + " connecting ... \n");
                    receivedHelloMsg(received.packet);
                case Constants.sitllAliveNoInterest:
                case Constants.sitllAliveWithInterest:
                    receivedStillAliveMSG(received.packet);
                default:
                    System.out.println("\n[NodeInfomParen] Received message type: " +Constants.convertMessageType(received.msgType) + "\n");
            }
        }

    private void receivedStillAliveMSG(DatagramPacket packet) {
        InfoConnection info = ReceiveData.receiveStillAliveMSG(packet);
        // Necessary to warn stream thread that stream must start/stop.
        if (info.interested != this.interested){
            System.out.println("Change interess");
            this.interested = info.interested;
            shared.setSendStream(info.interested);
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
}

