package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import otherServer.Bootstrapper.Connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

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

        byte[] bytes = ByteBuffer.allocate(1000).putInt(Constants.ConnectionMsg).putInt(c.numHops+1).putInt(size1).putInt(size2).put(info1).put(info2).putDouble(delay).array();
        sendData(socket, bytes, destIP, destPort);
    }








    /**
     * This message sends:
     * MessageType |  Port Lost Node | IP Lost Node
     * TODO: Verificar se dá para juntar o getAdress com a linha de cima
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
     * TODO: Falta decidir como fazemos isto
     * @param socket
     * @param destIP
     * @param destPort
     */
    public static void sendTooMuchDelayMSG(DatagramSocket socket, InetAddress destIP, int destPort){
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


}