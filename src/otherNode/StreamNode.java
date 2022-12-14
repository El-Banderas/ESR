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

    public StreamNode(DatagramSocket socket, ShareNodes shared) {
        this.shared = shared;
        this.socket = socket;
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
