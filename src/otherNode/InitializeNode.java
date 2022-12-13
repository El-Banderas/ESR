package otherNode;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;
import org.xml.sax.SAXException;
import otherServer.Bootstrapper.Connection;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class InitializeNode {

    private final InfoNodo thisNodeNet;


    private final InfoNodo thisNodeStream;
    private DatagramSocket socket;
    private InfoNodo boot;

    // Este depois não vai ser preciso
    public InitializeNode(DatagramSocket s, InfoNodo b) {
        this.socket = s;
        this.boot = b;
        this.thisNodeStream = null;
        this.thisNodeNet = null;
    }


    public InitializeNode(DatagramSocket socket, InfoNodo boot, InfoNodo thisNodeNet, InfoNodo thisNodeStream) {
        this.thisNodeNet = thisNodeNet;
        this.thisNodeStream = thisNodeStream;
        this.socket = socket;
        this.boot = boot;
    }

    public void start() {
        try {
            SendData.sendHelloMsg(this.socket, this.boot.ip, boot.portNet);
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

        String word = new String(neigbours.packet.getData());
        System.out.println("Server said: \n Your neighbours: \n");
        String[] neighboursList = word.split("END");
        return neighboursList[0];
    }


    public InfoNodo[] parseVizinhos(String vizinhos) throws UnknownHostException {


        String[] neighboursList = vizinhos.split("/");
        InfoNodo[] nodos = new InfoNodo[neighboursList.length];
        System.out.println("Vizinhos string completa");
        System.out.println(vizinhos);

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


    private void receiveXML() {
        try {
            MessageAndType received = ReceiveData.receiveData(socket);
            if (received.msgType == Constants.XMLmsg) {
                ShareNodes shared = new ShareNodes();

                InfoNodo parent = new InfoNodo(received.packet.getAddress(), received.packet.getPort());
                NodeInformParent comunication_TH = new NodeInformParent(parent, boot, thisNodeNet, new ArrayList<>(), shared);
                // NodeInformParent comunication_TH = new NodeInformParent(parent, boot, thisNode, sons, shared);
                new Thread(comunication_TH).start();
                //TODO: Falta thread Stream

            } else {
                System.out.println("InitializeNodes - Not expecting message type: " + received.msgType);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
