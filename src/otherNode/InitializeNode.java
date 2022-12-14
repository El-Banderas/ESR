package otherNode;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class InitializeNode {

    private final InfoNodo thisNodeNet;


    private final DatagramSocket socketStream;
    private DatagramSocket socket;
    private InfoNodo boot;

    private InfoNodo altBoot;

    // Este depois não vai ser preciso
    public InitializeNode(DatagramSocket s, InfoNodo b, int thisPort) {
        this.socket = s;
        this.boot = b;
        try {
        InfoNodo thisNodo  = new InfoNodo(InetAddress.getByName("127.0.0.1"), thisPort);
        this.socketStream = null;
        this.thisNodeNet = thisNodo;
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    public InitializeNode(DatagramSocket socket, InfoNodo boot, InfoNodo thisNodeNet, DatagramSocket thisNodeStream) {
        this.thisNodeNet = thisNodeNet;
        this.socketStream = thisNodeStream;
        this.socket = socket;
        this.boot = boot;
    }

    public void start() {
        try {
            SendData.sendHelloMsg(this.socket, this.boot.ip, boot.portNet);
            System.out.println("Send hello to: " + this.boot.portNet);
            System.out.println("Aqui");
            MessageAndType neigbours = ReceiveData.receiveData(this.socket);
            String neighboursList = findNeighbours(neigbours);
            InfoNodo[] Nlist = parseVizinhos(neighboursList);

            for (int i = 0; i < Nlist.length - 1; i++) {
                System.out.println(Nlist[i].toString());
            }

            // envia msg aos vizinhos
            for (int i = 0; i < Nlist.length - 1; i++) {
                SendData.sendTimeStamp(this.socket, Nlist[i].ip, Nlist[i].portNet);
            }
            receiveXML();

        } catch (
                IOException e) {
            System.out.println("[Node] Timeout");
        }

    }


    public String findNeighbours(MessageAndType neigbours) {
        ByteBuffer msg = ByteBuffer.wrap(neigbours.packet.getData());
        int type = msg.getInt();
        int isThereAlterBot = msg.getInt();
        if (isThereAlterBot == 1){
            try {
                if (Constants.Windows) {
                    int portAlterBoot = msg.getInt();
                    InetAddress ipBootAlter = InetAddress.getByName("127.0.0.1");
                    this.altBoot = new InfoNodo(ipBootAlter, portAlterBoot);
                }
                else {
                    byte[] ipArray = new byte[Constants.sizeInetAdressByteArray];
                    System.arraycopy(msg.array(), 4*2, ipArray, 0, Constants.sizeInetAdressByteArray);
                    InetAddress ipLostNode = InetAddress.getByAddress(ipArray);
                    this.altBoot = new InfoNodo(ipLostNode, Constants.portNet);

                }
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            this.altBoot = null;
        }
        System.out.println("O boot alternativo é: "+ this.altBoot);
        String word = new String(neigbours.packet.getData());

        String[] neighboursList = word.split("END");
        return neighboursList[0];
    }


    public InfoNodo[] parseVizinhos(String vizinhos) throws UnknownHostException {


        String[] neighboursList = vizinhos.split("/");
        InfoNodo[] nodos = new InfoNodo[neighboursList.length];

        String[] ips = new String[neighboursList.length - 1];
        int[] portas = new int[neighboursList.length - 1];

        for (int i = 1; i < neighboursList.length; i++) {
            String aux1[] = neighboursList[i].split("\\s+");
            ips[i - 1] = aux1[0];
            String aux2[] = neighboursList[i].split("-", 2);
            String aux3[] = aux2[1].split("\\s+");
            String aux4[] = aux3[1].split("}");
            portas[i - 1] = Integer.parseInt(aux4[0]);
        }

        for (int i = 0; i < ips.length; i++) {
            nodos[i] = new InfoNodo(InetAddress.getByName(ips[i]), portas[i]);
        }

        return nodos;
    }

    /**
     * Here we ignore the xml because, when a node is created, we don't have sons.
     */
    private void receiveXML() {
        try {
            MessageAndType received = ReceiveData.receiveData(socket);

            while (received.msgType == Constants.sitllAlive) {
                System.out.println("Recebi still alive, inútil");
                received = ReceiveData.receiveData(socket);

            }
            if (received.msgType == Constants.XMLmsg) {
                System.out.println("Recebi XML ");
                ShareNodes shared = new ShareNodes();

                InfoNodo parent = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                NodeInformParent comunication_TH = new NodeInformParent(parent, boot, thisNodeNet, socket, shared, this.altBoot);
                // NodeInformParent comunication_TH = new NodeInformParent(parent, boot, thisNode, sons, shared);
                new Thread(comunication_TH).start();
                StreamNode stream_TH = new StreamNode(this.socketStream, shared);
                new Thread(stream_TH).start();


            } else {
                System.out.println("InitializeNodes - Not expecting message type: " + received.msgType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
