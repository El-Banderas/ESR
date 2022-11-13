package Common;

import java.net.DatagramPacket;

/**
 * This class is used in nodes, to store the type and message received.
 *
 */
public class MessageAndType {
    public int msgType;
    public DatagramPacket packet;

    public MessageAndType(int msgType, DatagramPacket packet) {
        this.msgType = msgType;
        this.packet = packet;
    }
}
