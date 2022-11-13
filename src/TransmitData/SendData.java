package TransmitData;

import Common.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class SendData {

    public static void sendStillAliveMSG(DatagramSocket socket, InetAddress destIP, int destPort) throws IOException {
        int dateInSec = (int) (System.currentTimeMillis() / 1000);
        byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.sitllAliveID).putInt(dateInSec).array();
        sendData(socket, bytes, destIP, destPort);
    }

    public static void sendData(DatagramSocket socket, byte[] buf, InetAddress destIP, int destPort) throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, destIP, destPort);
        socket.send(packet);

    }
}