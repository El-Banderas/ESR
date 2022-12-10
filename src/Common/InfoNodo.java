package Common;

import java.net.InetAddress;

public class InfoNodo {
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
        this.portStream = -1;
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
}