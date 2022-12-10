package otherNode;

import Common.Constants;
import Common.InfoNodo;
import Common.MessageAndType;
import TransmitData.ReceiveData;
import TransmitData.SendData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class StreamNode implements Runnable{
    private DatagramSocket socket;
    private ShareNodes shared;

    public StreamNode(InfoNodo thisNode, ShareNodes shared) {
        this.shared = shared;
        try {
            if (thisNode.portStream > 0)
                socket = new DatagramSocket(thisNode.portStream);
            else
                socket = new DatagramSocket();
            socket.setSoTimeout(Constants.timeoutSockets);
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println("[Client] Error creating socket");
        }
    }

    @Override
    public void run() {
        System.out.println("Redirect stream started");
        while(true) {
            try {
                ReceiveData.nodeReceiveStream(socket, shared.interestedSons);
            } catch (IOException e) {
                System.out.println("[STREAM] Timout");
                //throw new RuntimeException(e);
            }
        }

    }
}
