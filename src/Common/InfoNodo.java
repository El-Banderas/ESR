package Common;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Objects;

public class  InfoNodo implements Serializable {
    public String idNodo;
    public InetAddress ip;
    public int portNet;
    public int portStream;


    /**
     * Server and client only have one port to send and receive data.
     * @param ip
     * @param portNet
     */
    public InfoNodo(InetAddress ip, int portNet) {
        this.idNodo = InfoNodo.generateId(ip, portNet);
        this.ip = ip;
        this.portNet = portNet;
        this.portStream = portNet+1;
    }

    /**
     * Nodes differenciate the port of stream with the port of network.
     * @param ip
     * @param portNet
     * @param portStream
     */
    public InfoNodo(InetAddress ip, int portNet, int portStream) {
        this.idNodo = InfoNodo.generateId(ip, portNet);
        this.ip = ip;
        this.portNet = portNet;
        this.portStream = portStream;
    }

    public InfoNodo(String id, InetAddress ip, int portNet) {
        this.idNodo = id;
        this.ip = ip;
        this.portNet = portNet;
    }

    public String getidNodo() { return idNodo; }

	public InetAddress getIp() { return ip; }


	public void setY(InetAddress ip) { this.ip = ip; }


    public byte[] NodeToBytes(){
        String v = this.toString();

        return  ByteBuffer.allocate(18+(2*v.length())).put(v.getBytes()).array();
    }

    public static InfoNodo BytestoNode(byte[] bytes) throws UnknownHostException {

        String word = new String(bytes);
        String[] aux = word.split( "/");
        String[] aux1 = aux[1].split("\\s+");

        InetAddress ip = InetAddress.getByName(aux1[0]);

        String[] aux2 = word.split( "-");
        String[] aux3 = aux2[2].split( "}");
        String[] aux4 = aux3[0].split( "\\s+");

        System.out.println(aux4[1]);
        int porta = Integer.parseInt(aux4[1]);

        return new InfoNodo(ip,porta);

    }

    @Override
    public String toString() {
        return "{ ip - " + ip.toString() +" " + " porta - " + portNet + "}";
    }

    public String toStringCon() {return "{  id - " + idNodo + " || ip -"  + ip.toString() + " || " + " porta - " + portNet + "}"; }

    public static String generateId(InetAddress ip, int port){
        return ip.toString()+"-"+ port;
    }

    public static boolean compareInfoNodes (InfoNodo in1, InfoNodo in2){
        if (in1.ip == in2.ip && in1.portNet == in2.portNet) return true;
        else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoNodo infoNodo = (InfoNodo) o;
        return portNet == infoNodo.portNet && Objects.equals(ip, infoNodo.ip);
    }


}