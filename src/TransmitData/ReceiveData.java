package TransmitData;

import Common.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ReceiveData {
    public static String receiveStillAliveMSG(DatagramSocket socket, DatagramPacket packet) throws IOException {
        int dateInSec = (int) (System.currentTimeMillis() / 1000);
        byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.sitllAliveID).putInt(dateInSec).array();
        //sendData(socket, bytes, destIP, destPort);
        return "Hello";
    }

    public static DatagramPacket receiveData(DatagramSocket socket) throws IOException {
            byte[] buf = new byte[Constants.arraySize];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            int numbers = ByteBuffer.wrap(buf).getInt();
        System.out.println("Receubeu o número: "+ numbers);
        return packet;

        }
}
