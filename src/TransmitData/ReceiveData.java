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


        double msgTime = msg.getDouble();
        double now = Constants.getCurrentTime();
        double delay = now-msgTime;
// Estou a meter o interested como falso, o que não é necessariamente verdade.
        // O still alive agora é sobre o pai, e ele não tem "interesse"
        return new InfoConnection(other, delay, now, false);
    }

    public static InfoNodo receiveLostNodeMSG(DatagramPacket packet) throws UnknownHostException {
        // To calculate sizes, this could be put in constants, but I don't know the sizes.
        // Change later.
        int sizeInetAdressByteArray = 4;
        int sizeInt = 4;
        /*
        try {
            sizeInetAdressByteArray = InetAddress.getByName("127.0.0.1").getAddress().length;
        } catch (UnknownHostException e) {
            System.out.println("Ignore");
        }
        System.out.println("Size IP: " + sizeInetAdressByteArray);
        */
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int portLostSon = msg.getInt();

        byte[] lostNodeIpPart = new byte[sizeInetAdressByteArray];
        // Cuidado com este 8, é o tamanho de 2 ints
        System.arraycopy(msg.array(), sizeInt*2, lostNodeIpPart, 0, sizeInetAdressByteArray);

        InetAddress ipLostNode = InetAddress.getByAddress(lostNodeIpPart);
        return new InfoNodo(ipLostNode, portLostSon);
    }


    public static byte[] receiveStreamContentMSG(DatagramPacket packet) throws UnknownHostException {
        // To calculate sizes, this could be put in constants, but I don't know the sizes.
        // Change later.
        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        // We already know the type, so we can ignore it
        int type = msg.getInt();
        int sizeContent = msg.getInt();

        byte[] content = new byte[sizeContent];
        // Cuidado com este 8, é o tamanho de 2 ints
        System.arraycopy(msg.array(), 8, content, 0, sizeContent);


        return content;
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
