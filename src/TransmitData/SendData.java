package TransmitData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendData {
    public static void sendData(DatagramSocket socket, byte[] buf, InetAddress destIP, int destPort) throws IOException {
        DatagramPacket packet
                = new DatagramPacket(buf, buf.length, destIP, destPort);
        socket.send(packet);

    }
}