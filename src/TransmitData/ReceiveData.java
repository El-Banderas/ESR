package TransmitData;

import Common.Constants;
import Common.MessageAndType;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class ReceiveData {
    public static float receiveStillAliveMSG(DatagramPacket packet) throws IOException {
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());
        int type = msg.getInt();
        float msgTime = msg.getInt() * 1000;
        //int dateInSec = (int) (System.currentTimeMillis() / 1000);
        //byte[] bytes = ByteBuffer.allocate(100).putInt(Constants.sitllAliveID).putInt(dateInSec).array();
        //sendData(socket, bytes, destIP, destPort);
        return msgTime;
    }

    public static MessageAndType receiveData(DatagramSocket socket) throws IOException {
            byte[] buf = new byte[Constants.arraySize];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            int type = ByteBuffer.wrap(buf).getInt();
        MessageAndType received = new MessageAndType(type, packet);
        System.out.println("Receubeu o número: "+ type);
        return received;

        }
}
