package TransmitData;

import Common.Constants;
import Common.InfoNodo;

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

    /**
     * This message sends:
     * MessageType |  Port Lost Node | IP Lost Node
     * TODO: Verificar se dá para juntar o getAdress com a linha de cima
     */
    public static void sendParentLostMSG(DatagramSocket socket, InfoNodo dest, InfoNodo lostNode) throws IOException {
         ByteBuffer bb = ByteBuffer.allocate(50).
                putInt(Constants.lostNode).
                putInt(lostNode.port);
        byte[] bytesIP = lostNode.ip.getAddress();

        byte[] bytes = bb.put(bytesIP).array();
        System.out.println("Envia msg filho perdido: " + dest.ip + " e porta: " + dest.port);
        sendData(socket, bytes, dest.ip, dest.port);
    }

    /**
     * This message sends:
     * MessageType | Size content | Content
     */

    public static void sendStreamContentMSG(DatagramSocket socket, InfoNodo dest, byte[] content) throws IOException {
        System.out.println("Envia stream");
        // Size int = 4
        ByteBuffer bb = ByteBuffer.allocate(content.length+4*2).
                putInt(Constants.streamContent).
                putInt(content.length).put(content);

        byte[] bytes = bb.array();
        System.out.println("Envia Stream");
        sendData(socket, bytes, dest.ip, dest.port);
    }

    /**
     * TODO: Falta decidir como fazemos isto
     * @param socket
     * @param destIP
     * @param destPort
     */
    public static void sendTooMuchDelayMSG(DatagramSocket socket, InetAddress destIP, int destPort){
    }

    public static void wantsStream(DatagramSocket socket, InfoNodo parent) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(50).
                putInt(Constants.streamWanted);

        System.out.println("Envia quero stream");
        sendData(socket, bb.array(), parent.ip, parent.port);

    }

    public static void sendData(DatagramSocket socket, byte[] buf, InetAddress destIP, int destPort) throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, destIP, destPort);
        socket.send(packet);

    }


}