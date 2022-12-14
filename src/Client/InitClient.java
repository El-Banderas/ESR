package Client;

import Common.InfoNodo;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;

public class InitClient {
    private DatagramSocket socketNet;
    private DatagramSocket socketStream;
    private InfoNodo infoBoot;
    private InfoNodo infoClientNet;
    private    InfoNodo infoClientStream;

    public InitClient(InfoNodo infoBoot, InfoNodo infoClientNet, InfoNodo infoClientStream) {
        this.infoBoot = infoBoot;
        this.infoClientNet = infoClientNet;
        this.infoClientStream = infoClientStream;
        try {
            socketNet = new DatagramSocket(infoClientNet.portNet);
            socketStream = new DatagramSocket(infoClientNet.portStream);

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    public void runClient() {

        try {
            SendData.sendHelloMsgClient(this.socketNet, infoBoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
