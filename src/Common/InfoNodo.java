package Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;

public class InfoNodo {
    public String idNodo;
    public InetAddress ip;
    public int port;


  
    public InfoNodo(InetAddress ip, int port ) {
        this.idNodo = InfoNodo.generateId(ip, port);
        this.ip = ip;
        this.port=port;
    }
    public InfoNodo(String id, InetAddress ip, int port ) {
        this.idNodo = id;
        this.ip = ip;
        this.port=port;
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

        int porta = Integer.parseInt(aux4[1]);

        return new InfoNodo(ip,porta);

    }

    @Override
    public String toString() {
        return "{ ip - " + ip.toString() +" " + " porta - " + port + "}";
    }

    public String toStringCon() {return "{  id - " + idNodo + " || ip -"  + ip.toString() + " || " + " porta - " + port + "}"; }

    public static String generateId(InetAddress ip, int port){
        return ip.toString()+"-"+Integer.toString(port);
    }

    public static boolean compareInfoNodes (InfoNodo in1, InfoNodo in2){
        if (in1.ip == in2.ip && in1.port == in2.port) return true;
        else return false;
    }
}