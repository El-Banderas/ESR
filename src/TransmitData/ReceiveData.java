package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import otherServer.Bootstrapper.InfoConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class ReceiveData {
    public static InfoConnection receiveStillAliveMSG(DatagramPacket packet) {

        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        InfoNodo other = new InfoNodo(packet.getAddress(), packet.getPort());

        boolean interested;
        int type = msg.getInt();
        if (type == Constants.sitllAliveNoInterest) {
            interested = false;
        }
        else interested = true;

        double msgTime = msg.getDouble();
        double now = Constants.getCurrentTime();
        double delay = now-msgTime;

        return new InfoConnection(other, delay, now, interested);
    }


    public static InfoNodo receiveLostNodeMSG(DatagramPacket packet) throws UnknownHostException {
        // To calculate sizes, this could be put in constants, but I don't know the sizes.
        // Change later.
        int sizeInetAdressByteArray = 0;
        try {
            sizeInetAdressByteArray = InetAddress.getByName("127.0.0.1").getAddress().length;
        } catch (UnknownHostException e) {
            System.out.println("Ignore");
        }
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int portLostSon = msg.getInt();

        byte[] lostNodeIpPart = new byte[sizeInetAdressByteArray];
        // Cuidado com este 8, é o tamanho de 2 ints
        System.arraycopy(msg.array(), 8, lostNodeIpPart, 0, sizeInetAdressByteArray);

        InetAddress ipLostNode = InetAddress.getByAddress(lostNodeIpPart);
        return new InfoNodo(ipLostNode, portLostSon);
    }

    public static MessageAndType receiveData(DatagramSocket socket) throws IOException {
            byte[] buf = new byte[Constants.arraySize];
            DatagramPacket packet
                    = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            int type = ByteBuffer.wrap(buf).getInt();
            MessageAndType received = new MessageAndType(type, packet);
            return received;

        }
}
