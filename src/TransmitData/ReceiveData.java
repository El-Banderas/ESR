package TransmitData;

import Common.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ReceiveData {
        public static DatagramPacket receiveData(DatagramSocket socket) throws IOException {
            byte[] buf = new byte[Constants.arraySize];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            return packet;

        }
}
