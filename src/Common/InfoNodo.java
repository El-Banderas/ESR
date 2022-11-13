package Common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class InfoNodo {
    public String idNodo;
    public InetAddress ip;
    public int port;
    public int bootsttrap; //1 se for

  
    public InfoNodo(String id, InetAddress ip, int port ) {
        this.idNodo = id;
        this.ip = ip;
        this.bootsttrap=0;
        this.port=port;

    }

    public String getidNodo() { return idNodo; }

	public InetAddress getIp() { return ip; }
    
    public void setBootStrap() {this.bootsttrap = 1;}

	public void setY(InetAddress ip) { this.ip = ip; }


    


    @Override
    public String toString() {
        return "{" + idNodo  +" :" + " ip - " + ip.toString() + "  " + " boot -  " +  bootsttrap +  "}";
    }

}