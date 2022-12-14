package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import otherServer.Bootstrapper.Connection;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class SendData {

    public static void sendStillAliveMSG(DatagramSocket socket, InetAddress destIP, int destPort) throws IOException {
        double dateInSec = Constants.getCurrentTime();
        byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.sitllAlive).putDouble(dateInSec).array();
        sendData(socket, bytes, destIP, destPort);
    }

    public static void sendHelloMsg(DatagramSocket socket, InetAddress destIP, int destPort) throws IOException {
        byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.hellomesage).array();
        System.out.println("Connecting to Server ... \n");
        sendData(socket, bytes, destIP, destPort);
    }

    public static void sendTimeStamp(DatagramSocket socket, InetAddress destIP, int destPort) throws IOException {
        double dateInSec = Constants.getCurrentTime();
        // int msg timestamp 22
        byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.timeStamp).putDouble(dateInSec).putInt(1).array();
        sendData(socket, bytes, destIP, destPort);
    }


    public static void sendConnection(DatagramSocket socket, Connection c, InetAddress destIP, int destPort) throws IOException {

        double delay = c.delay;
        byte[] info1 = c.from.NodeToBytes();

        int size1 = info1.length;
        byte[] info2 = c.to.NodeToBytes();
        int size2 = info2.length;
        int hops = c.numHops + 1;
        byte[] bytes = ByteBuffer.allocate(size1 + size2 + 40).putInt(Constants.ConnectionMsg).putDouble(delay).putInt(hops).putInt(size1).putInt(size2).put(info1).put(info2).array();

        sendData(socket, bytes, destIP, destPort);
    }


    /**
     * This message sends:
     * MessageType |  Port Lost Node | IP Lost Node
     */
    public static void sendParentLostMSG(DatagramSocket socket, InfoNodo dest, InfoNodo lostNode) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(50).
                putInt(Constants.lostNode).
                putInt(lostNode.portNet);
        byte[] bytesIP = lostNode.ip.getAddress();

        byte[] bytes = bb.put(bytesIP).array();
        System.out.println("Envia msg filho perdido: " + dest.ip + " e porta: " + dest.portNet);
        //   sendData(socket, bytes, dest.ip, dest.portNet);
    }

    /**
     * This message sends:
     * MessageType | Size content | Content
     */

    public static void sendStreamContentMSG(DatagramSocket socket, InfoNodo dest, byte[] content) throws IOException {
        sendData(socket, content, dest.ip, dest.portStream);
    }

    /**
     * TODO: Servidor alternativo
     *
     * @param socket
     * @param destIP
     * @param destPort
     */
    public static void sendTooMuchDelayMSG(DatagramSocket socket, InetAddress destIP, int destPort) {
    }

    public static void wantsStream(DatagramSocket socket, InfoNodo parent, int portReceiveStream) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(50).
                putInt(Constants.streamWanted).putInt(portReceiveStream);

        System.out.println("Envia quero stream");
        sendData(socket, bb.array(), parent.ip, parent.portNet);

    }

    public static void sendData(DatagramSocket socket, byte[] buf, InetAddress destIP, int destPort) throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, destIP, destPort);
        socket.send(packet);

    }


    public static void sendXML(DatagramSocket socket, InfoNodo son, String xml) throws IOException {
        System.out.println("Vai enviar o XML: " + son.portNet);
        System.out.println("Vai enviar o XML ---------IP-------- : " + son.ip);
        System.out.println(xml);

        byte[] xmlBytes = xml.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(8 + xmlBytes.length).
                putInt(Constants.XMLmsg).
                putInt(xmlBytes.length).
                put(xmlBytes);

        byte[] bytes = bb.array();
        sendData(socket, bytes, son.ip, son.portNet);
    }

    public static void sendHelloFromAlt(DatagramSocket socket, InfoNodo serverInfo) throws IOException {
        byte[] bytes = ByteBuffer.allocate(50).
                putInt(Constants.helloAltBoot).array();
        sendData(socket, bytes, serverInfo.ip, serverInfo.portNet);
    }

    public static void sendStillAliveBootAlt(DatagramSocket socket, InfoNodo otherBoot, int timestampStream) throws IOException {
        byte[] bytes = ByteBuffer.allocate(50).
                putInt(Constants.StillAliveBootAlt).putInt(timestampStream).array();
        sendData(socket, bytes, otherBoot.ip, otherBoot.portNet);

    }

    public static void sendActiveNetwork(DatagramSocket socket, InfoNodo otherBoot, Map<InfoNodo, List<Connection>> activeNetwork) {
        try {

            ByteArrayOutputStream ba = new ByteArrayOutputStream(activeNetwork.size() * 200);
            ObjectOutputStream oba = new ObjectOutputStream(ba);
            oba.writeObject(activeNetwork);
            byte[] toSend = ba.toByteArray();
            byte[] bytes = ByteBuffer.allocate(toSend.length + 8).
                    putInt(Constants.changeTree).putInt(toSend.length).put(toSend).array();

            ByteArrayInputStream ba1 = new ByteArrayInputStream(toSend);
            ObjectInputStream oba1 = new ObjectInputStream(ba1);
            System.out.println("Como a árvore deve aparecer do outro lado");
            System.out.println(oba1.readObject());
            sendData(socket, bytes, otherBoot.ip, otherBoot.portNet);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public static void sendHelloMsgClient(DatagramSocket socket, InfoNodo serverInfo) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).
                putInt(Constants.helloClient).array();
        sendData(socket, bytes, serverInfo.ip, serverInfo.portNet);
    }

    public static void sendImpossibleConnection(DatagramSocket socket, InfoNodo newClient) throws IOException {
        byte[] bytes = ByteBuffer.allocate(4).
                putInt(Constants.impossibleConnection).array();
        sendData(socket, bytes, newClient.ip, newClient.portNet);
    }

    public static void sendWakeUpClient(DatagramSocket socket, List<InfoNodo> parents) throws IOException {
        InfoNodo sendTo = parents.remove(0);
        sendTo = parents.remove(0);
        if (Constants.Windows) {
            System.out.println("Lista de parents");
            System.out.println(parents);
            // 4 para o tipo, 4 para o  número de elems
            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + parents.size() * (Constants.sizeInetAdressByteArray + 4));
            buffer.putInt(Constants.wakeUpClient).putInt(parents.size());
            for (InfoNodo parent : parents) {
                buffer.putInt(parent.portNet);
                buffer.put(parent.ip.getAddress());

            }
            System.out.println("Sending wake up to: " + sendTo);
            sendData(socket, buffer.array(), sendTo.ip, sendTo.portNet);
        } else {
            // 4 para o tipo, 4 para o  número de elems
            ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + parents.size() * Constants.sizeInetAdressByteArray);
            buffer.putInt(Constants.wakeUpClient).putInt(parents.size());
            for (InfoNodo parent : parents) {
                buffer.put(parent.ip.getAddress());
            }
            System.out.println("Sending wake up to: " + sendTo);
            System.out.println("Parents"+ parents);
            sendData(socket, buffer.array(), sendTo.ip, sendTo.portNet);

        }

    }

    // When it is received by a node
    public static void sendWakeUpClient(DatagramSocket socket, byte[] data) throws IOException {
        ByteBuffer msg = ByteBuffer.wrap(data);

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int howMany = msg.getInt();
        System.out.println("Tem " + howMany + " elementos");
        if (Constants.Windows) {
            int portNext = msg.getInt();
            data = msg.array();
            // 4 para o tipo, 4 para o  número de elems
            byte[] ipNext = new byte[Constants.sizeInetAdressByteArray];
            // Cuidado com este 8, é o tamanho de 2 ints
            System.arraycopy(data, 12, ipNext, 0, Constants.sizeInetAdressByteArray);

            InetAddress ipNextNode = InetAddress.getByAddress(ipNext);
            if (howMany <= 1) {
                byte[] bytes = ByteBuffer.allocate(4 * 2).
                        putInt(Constants.wakeUpClient).
                        putInt(0).array();
                System.out.println("Sending wake up to: " + ipNextNode + " - " + portNext);
                System.out.println("A partir do socket: " + socket);
                System.out.println("Bytes: " + bytes.length);
                sendData(socket, bytes, ipNextNode, portNext);

            } else {

                byte[] restParents = new byte[data.length - 12];

                System.arraycopy(data, 12, restParents, 0, restParents.length);

                byte[] bytes = ByteBuffer.allocate(restParents.length + 4 * 2 + 100).
                        putInt(Constants.wakeUpClient).
                        putInt(howMany - 1).
                        put(restParents).array();
                System.out.println("Sending wake up to: " + ipNextNode + " - " + portNext);
                sendData(socket, bytes, ipNextNode, portNext);
            }

        } else {
            byte[] ipNext = new byte[Constants.sizeInetAdressByteArray];
            // Cuidado com este 8, é o tamanho de 2 ints
            System.arraycopy(data, 8, ipNext, 0, Constants.sizeInetAdressByteArray);

            InetAddress ipNextNode = InetAddress.getByAddress(ipNext);
            if (howMany <= 1) {
                byte[] bytes = ByteBuffer.allocate(4 * 2).
                        putInt(Constants.wakeUpClient).
                        putInt(0).array();
                System.out.println("Sending wake up to: " + ipNextNode + " - " + Constants.portNet);
                System.out.println("A partir do socket: " + socket);
                System.out.println("Bytes: " + bytes.length);
                sendData(socket, bytes, ipNextNode, Constants.portNet);

            }
            else {
                byte[] restParents = new byte[data.length - 12];
                System.arraycopy(data, 12, restParents, 0, restParents.length);

                byte[] bytes = ByteBuffer.allocate(restParents.length + 4 * 2).
                        putInt(Constants.wakeUpClient).
                        putInt(howMany - 1).
                        put(restParents).array();
                System.out.println("Sending wake up to: " + ipNextNode + " - " + Constants.portNet);
                System.out.println("Tem x elems" + ((int) (howMany - 1)));
                System.out.println("TAMANHO ARRRAY ------------" + bytes.length);
               /* for (byte b : bytes) {
                    System.out.println(b);
                }*/
                sendData(socket, bytes, ipNextNode, Constants.portNet);
            }
        }

    }

}