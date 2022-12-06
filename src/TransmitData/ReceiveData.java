package TransmitData;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import otherServer.Bootstrapper.InfoConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

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

        int msgTime = msg.getInt();
        int now = Constants.getCurrentTime();
        int delay = now-msgTime;

        return new InfoConnection(other, delay, now, interested);
    }

    public static InfoNodo  receiveHelloMsg(DatagramPacket packet) {

        ByteBuffer msg = ByteBuffer.wrap(packet.getData());

        InfoNodo other = new InfoNodo(packet.getAddress(), packet.getPort());

        int type = msg.getInt();
        if(type==Constants.hellomesage) return other;


        return null;
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
