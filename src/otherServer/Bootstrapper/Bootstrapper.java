package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import otherServer.CommuncationBetweenThreads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
        boolean sendNeighbours = false;
        if (sendNeighbours) {
            SendNeighbours th_SendNeighbours = new SendNeighbours();
            new Thread(th_SendNeighbours).start();
        }

        byte[] buf = new byte[100];
        DatagramPacket receivePKT = new DatagramPacket(buf, buf.length);

        while (true) {
            try {
                MessageAndType received = ReceiveData.receiveData(socket);
                handleReceivedMessage(received);

            } catch (IOException e) {
                System.out.println("[Node] Timeout, listening in: " + socket.getPort());

            }

        }
    }

    private void handleReceivedMessage(MessageAndType received) {
            switch (received.msgType){
                case Constants.sitllAliveNoInterest:
                case Constants.sitllAliveWithInterest:
                    receivedStillAliveMSG(received.packet);
                    break;
                case Constants.lostNode:
                    receiveLostNodeMSG(received.packet);
                    break;
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


}
