package TransmitData;

import Common.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class SendData {

    public static void sendStillAliveMSG(DatagramSocket socket, InetAddress destIP, int destPort, int messageType) throws IOException {
        int dateInSec = Constants.getCurrentTime();
        byte[] bytes = ByteBuffer.allocate(100).putInt(messageType).putInt(dateInSec).array();
        sendData(socket, bytes, destIP, destPort);
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