package TransmitData;

import Common.Constants;
import Common.InfoNodo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class SendData {

    public static void sendStillAliveMSG(DatagramSocket socket, InetAddress destIP, int destPort, int messageType) throws IOException {
        double dateInSec = Constants.getCurrentTime();
        byte[] bytes = ByteBuffer.allocate(100).putInt(messageType).putDouble(dateInSec).array();
        System.out.println("Envia still alive para: " + destIP + " e porta: " + destPort);
        sendData(socket, bytes, destIP, destPort);
    }

    /**
     * This message sends:
     * MessageType | IP Lost Node | Port Lost Node
     */
    public static void sendLostSonMSG(DatagramSocket socket, InfoNodo dest, InfoNodo lostNode) throws IOException {
        byte[] bytes = ByteBuffer.allocate(30).
                putInt(Constants.lostNode).
                putInt(lostNode.port).
        put(lostNode.ip.getAddress()).array();
        System.out.println("Envia msg filho perdido: " + dest.ip + " e porta: " + dest.port);
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

    public static void sendData(DatagramSocket socket, byte[] buf, InetAddress destIP, int destPort) throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, destIP, destPort);
        socket.send(packet);

    }
}