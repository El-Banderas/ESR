
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Nodo {
    public String idNodo;
    public InetAddress ip;

  
    public Nodo(String id,InetAddress ip) {
        this.idNodo = id;
        this.ip = ip;
 
    }

    public String getidNodo() { return idNodo; }

	public InetAddress getIp() { return ip; }

	public void setX(String idNodo) { this.idNodo = idNodo; }

	public void setY(InetAddress ip) { this.ip = ip; }


    @Override
    public String toString() {
        return "{" + idNodo  +" :" + " ip - " + ip.toString() + "}";
    }

}