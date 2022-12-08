package Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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


    @Override
    public String toString() {
        return "{ ip - " + ip.toString() +" " + " porta - " + port + "}";
    }

    public static String generateId(InetAddress ip, int port){
        return ip.toString()+"-"+Integer.toString(port);
    }
}