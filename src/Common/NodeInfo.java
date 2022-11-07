package Common;

import java.net.InetAddress;

public class NodeInfo {
    public InetAddress myIP;
    public int myPort;

    public NodeInfo(InetAddress myIP, int myPort) {
        this.myIP = myIP;
        this.myPort = myPort;
    }
}
