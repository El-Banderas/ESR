package otherServer.Bootstrapper;

import Common.Constants;
import Common.InfoNodo;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Objects;

public class Connection implements Comparable<Connection>, Serializable {
    public InfoNodo from;
    public InfoNodo to;
    public double delay;
    public int numHops;

    public Connection(InfoNodo from, InfoNodo to, double delay, int numHops) {
        this.from = from;
        this.to = to;
        this.delay = delay;
        this.numHops = numHops;
    }

    public InfoNodo getFrom() {
        return from;
    }

    public void setFrom(InfoNodo from) {
        this.from = from;
    }

    public InfoNodo getTo() {
        return to;
    }

    public void setTo(InfoNodo to) {
        this.to = to;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    public int getNumHops() {
        return numHops;
    }

    public void setNumHops(int numHops) {
        this.numHops = numHops;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Double.compare(that.delay, delay) == 0 && numHops == that.numHops && from.equals(that.from) && to.equals(that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, delay, numHops);
    }

    @Override
    public String toString() {
        return "Connection{" +
                from.toStringCon() +
                " ----> " + to.toStringCon() +
                "|| delay=" + delay +
                " || numHops=" + numHops +
                '}';
    }


    public int compareTo(Connection that) {
        return (int) ((this.delay*0.6 + this.numHops*0.4 )- (that.delay*0.6 + that.numHops*0.4));
    }

    public static byte[] toByte(Connection con){
        byte[] fromBytes = con.from.NodeToBytes();
        byte[] toBytes = con.to.NodeToBytes();
        int sizeInt = 4;
        int sizeDouble = 8;
        byte[] bytes = ByteBuffer.allocate(sizeInt*3+sizeDouble+fromBytes.length+toBytes.length+1).
                putInt(con.numHops).
                putDouble(con.delay).
                putInt(fromBytes.length).
                putInt(toBytes.length).
                put(fromBytes).
                put(toBytes).
                array();
        return bytes;

    }

    public static Connection fromByte(byte[] bytes){
        int sizeInt = 4;
        int sizeDouble = 8;

        ByteBuffer msg = ByteBuffer.wrap(bytes);
        int numHops = msg.getInt();
        double delay = msg.getDouble();
        int fromBytesLen = msg.getInt();
        int toBytesLen = msg.getInt();


        byte[] fromBytes = new byte[fromBytesLen];
        byte[] toBytes = new byte[toBytesLen];
        byte[] twoClasses = new byte[fromBytesLen+toBytesLen];
        System.arraycopy(msg.array(),sizeInt*3+sizeDouble*1 , twoClasses,0,fromBytesLen+toBytesLen);

        System.arraycopy(twoClasses, 0, fromBytes, 0, fromBytesLen);
        System.arraycopy(twoClasses, fromBytesLen, toBytes, 0, toBytesLen);
        try {
            InfoNodo from = InfoNodo.BytestoNode(fromBytes);
            InfoNodo to = InfoNodo.BytestoNode(toBytes);
            return new Connection(from, to, delay, numHops);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}

